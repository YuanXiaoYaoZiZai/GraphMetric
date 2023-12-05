package com.fsc.metric.service;

import com.fsc.common.cache.CacheManager;
import com.fsc.common.config.ConfigTools3;
import com.fsc.metric.dao.BaseNode;
import com.fsc.metric.dao.BaseRelationship;
import com.fsc.metric.dao.GraphDao;
import com.fsc.metric.model.*;
import com.fsc.metric.utils.AviatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class GraphService {

    private final static Logger logger = LoggerFactory.getLogger(GraphService.class);

    private final LinkedBlockingQueue<GraphReq> graphActionQueue = new LinkedBlockingQueue<>(100000);

    @Autowired
    private GraphDao graphDao;

    private CacheManager cacheManager = new CacheManager();

    private ExecutorService executorService;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(ConfigTools3.getConfigAsInt("graph.metric.server.worker.thread.count", 200));

        new Thread(this::doExecuteGraphAction, "EXECUTE_GRAPH_ACTION").start();

        new Thread(this::doMarkOrDeleteNodesAndRelations, "EXECUTE_MARK_DELETE").start();
    }

    private Map<String, Object> getFieldsById(String label, Long id) {
        return (Map<String, Object>) cacheManager.get("GRAPH_CACHE", label + ":" + id);
    }

    private void putFieldsById(String label, Long id, Map<String, Object> fields) {
        Map<String, Object> fieldsCached = getFieldsById(label, id);
        Map<String, Object> params = new HashMap<>();
        if (!CollectionUtils.isEmpty(fieldsCached)) {
            params.putAll(fieldsCached);
        }
        params.putAll(fields);
        cacheManager.put("GRAPH_CACHE", label + ":" + id, params);
    }

    private void deleteFieldsById(String label, Long id) {
        cacheManager.remove("GRAPH_CACHE", label + ":" + id);
    }

    private GraphNode convertToGraphNode(BaseNode node) {
        GraphNode graphNode = new GraphNode();
        graphNode.setId(String.valueOf(node.getId()));
        graphNode.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(node.getCreateTime()))));
        graphNode.setLastUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(node.getLastUpdateTime()))));
        graphNode.setLastUpdateUser(node.getLastUpdateUser());
        graphNode.setVersion(node.getVersion());
        graphNode.setProps(node.getProps());
        graphNode.setFields(getFieldsById(String.valueOf(node.getProps().get("label")), node.getId()));
        return graphNode;
    }

    private GraphRelationship convertToGraphRelationship(BaseNode startNode, BaseNode endNode, BaseNode relationship) {
        GraphRelationship graphRelationship = new GraphRelationship();
        graphRelationship.setId(String.valueOf(relationship.getId()));
        graphRelationship.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(relationship.getCreateTime()))));
        graphRelationship.setLastUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.parseLong(relationship.getLastUpdateTime()))));
        graphRelationship.setLastUpdateUser(relationship.getLastUpdateUser());
        graphRelationship.setVersion(relationship.getVersion());
        graphRelationship.setProps(relationship.getProps());
        graphRelationship.setFields(getFieldsById(String.valueOf(relationship.getProps().get("label")), relationship.getId()));

        graphRelationship.setSource(String.valueOf(startNode.getId()));
        graphRelationship.setTarget(String.valueOf(endNode.getId()));
        return graphRelationship;
    }


    private GraphAll buildGraph(List<BaseNode> nodes, List<BaseRelationship> relationships, GraphGrafanaParams params) {
        GraphAll graph = new GraphAll();
        //构建图表节点分类
        String categoryProps = params.getCategoryProps();
        //实际存在的分类
        List<String> categoriesReal = new ArrayList<>();
        //实际存在的节点
        Map<Long, BaseNode> nodesReal = new HashMap<>();

        nodes.forEach(node -> {
            String nodeCategory = String.valueOf(node.getProps().get(categoryProps));
            if (!categoriesReal.contains(nodeCategory)) {
                categoriesReal.add(nodeCategory);
            }
            nodesReal.put(node.getId(), node);
        });
        Collections.sort(categoriesReal);

        //返回用到的分类集合，即该分类下有节点
        List<String> categories = new ArrayList<>();
        //返回的分类二维集合
        List<List<String>> categoryGroups = new ArrayList<>();
        params.getCategoryGroups().forEach(categoryGroup -> {
            List<String> newCategoryGroup = new ArrayList<>();
            categoryGroup.forEach(category -> {
                if (categoriesReal.contains(category)) {
                    categories.add(category);
                    newCategoryGroup.add(category);
                }
            });

            if (!CollectionUtils.isEmpty(newCategoryGroup)) {
                categoryGroups.add(newCategoryGroup);
            }
        });

        List<List<String>> categoryGroupsOther = new ArrayList<>();
        params.getCategoryGroupsOther().forEach(categoryGroup -> {
            List<String> newCategoryGroup = new ArrayList<>();
            categoryGroup.forEach(category -> {
                if (categoriesReal.contains(category)) {
                    categories.add(category);
                    newCategoryGroup.add(category);
                }
            });

            if (!CollectionUtils.isEmpty(newCategoryGroup)) {
                categoryGroupsOther.add(newCategoryGroup);
            }
        });
        categoryGroups.addAll(categoryGroupsOther);

        //如果参数中未携带分组信息，则自动从节点中提取出来。
        if (CollectionUtils.isEmpty(categories)) {
            categoriesReal.forEach(category -> {
                List<String> newCategoryGroup = new ArrayList<>();
                newCategoryGroup.add(category);
                categories.add(category);
                categoryGroups.add(newCategoryGroup);
            });
        }

        //将不同分类的节点汇总,并且按照分类倒序收集
        Map<String, List<BaseNode>> nodesCategoryMap = new LinkedHashMap<>();
        for (int i = 0; i < categoryGroups.size(); i++) {
            for (String category : categoryGroups.get(categoryGroups.size() - 1 - i)) {
                nodesCategoryMap.putIfAbsent(category, new ArrayList<>());
            }
        }

        nodes.forEach(node -> {
            String nodeCategory = String.valueOf(node.getProps().get(categoryProps));
            if (nodesCategoryMap.containsKey(nodeCategory)) {
                nodesCategoryMap.get(nodeCategory).add(node);
            }
        });

        //起点到终点连线的汇总
        TreeMap<Long, List<BaseNode>> relationsFromSNode = new TreeMap<>();
        //终点到起点连线的汇总
        TreeMap<Long, List<BaseNode>> relationsFromTNode = new TreeMap<>();
        LinkedHashSet<GraphRelationship> graphRelations = new LinkedHashSet<>();
        //构架图表连线
        relationships.forEach(relationship -> {
            if (!nodesReal.containsKey(relationship.getStartNode().getId())
                    || !nodesReal.containsKey(relationship.getEndNode().getId())) {
                return;
            }

            long sGroupCategory = 0;
            long tGroupCategory = 0;
            for (int i = 0; i < categoryGroups.size(); i++) {
                String s = String.valueOf(relationship.getStartNode().getProps().get(categoryProps));
                //获取连线起点的分组下标
                if (categoryGroups.get(i).contains(s)) {
                    sGroupCategory = i;
                }
                //获取连线终点的分组下标
                String t = String.valueOf(relationship.getEndNode().getProps().get(categoryProps));
                if (categoryGroups.get(i).contains(t)) {
                    tGroupCategory = i;
                }
            }

            //只有相邻的分组才需要记录下来
            if (sGroupCategory - 1 == tGroupCategory) {
                relationsFromSNode.putIfAbsent(relationship.getStartNode().getId(), new ArrayList<>());
                relationsFromSNode.get(relationship.getStartNode().getId()).add(relationship.getEndNode());

                relationsFromTNode.putIfAbsent(relationship.getEndNode().getId(), new ArrayList<>());
                relationsFromTNode.get(relationship.getEndNode().getId()).add(relationship.getStartNode());
            }

            GraphRelationship graphRelationship =
                    convertToGraphRelationship(relationship.getStartNode(), relationship.getEndNode(), relationship);
            //计算表达式
            params.getLinkExpressions().forEach((key, value) -> {
                boolean result = AviatorUtils.tryMatchExpression(value, graphRelationship.getProps(),
                        graphRelationship.getFields());
                graphRelationship.getLinkExpressions().put(key, result);
            });
            graphRelations.add(graphRelationship);
        });

        List<BaseNode> nodeUpstreamAll = new ArrayList<>();
        nodesCategoryMap.forEach((category, nodesNotSorted) -> {
            List<BaseNode> nodeSorted = new ArrayList<>();
            //筛选出只有一个入口连线的节点
            List<BaseNode> nodeHasOneRel = nodesNotSorted.stream().filter(item -> relationsFromTNode.containsKey(item.getId())
                    && relationsFromTNode.get(item.getId()).size() == 1).collect(Collectors.toList());
            //删选出不是一个入口连线的节点，没有连线或者大于一个连线
            List<BaseNode> nodeOther = nodesNotSorted.stream().filter(item -> !relationsFromTNode.containsKey(item.getId())
                    || relationsFromTNode.get(item.getId()).size() != 1).collect(Collectors.toList());

            //获取该组的上游节点集合
            Set<BaseNode> nodeUpstream = new LinkedHashSet<>();
            for (BaseNode baseNode : nodeHasOneRel) {
                nodeUpstream.add(relationsFromTNode.get(baseNode.getId()).get(0));
            }

            List<BaseNode> nodeUpstreamSorted = nodeUpstream.stream().sorted(new Comparator<BaseNode>() {
                @Override
                public int compare(BaseNode o1, BaseNode o2) {
                    return nodeUpstreamAll.indexOf(o1) - nodeUpstreamAll.indexOf(o2);
                }
            }).collect(Collectors.toList());

            //依次将同一上游的节点汇集起来，放在一起，画图的时候会分布在一起
            nodeUpstreamSorted.forEach(item -> {
                nodeSorted.addAll(nodeHasOneRel.stream().filter(item1 ->
                        relationsFromTNode.get(item1.getId()).get(0).getId().equals(item.getId())).collect(Collectors.toList()));
            });

            //最后再补上其他节点
            nodeSorted.addAll(nodeOther);

            nodesCategoryMap.put(category, nodeSorted);
            nodeUpstreamAll.addAll(nodeSorted);
        });

        List<BaseNode> nodesSorted = new ArrayList<>();
        categories.forEach(nodeCategory -> {
            nodesSorted.addAll(nodesCategoryMap.get(nodeCategory));
        });

        //构架图表节点
        LinkedHashSet<GraphNode> graphNodes = new LinkedHashSet<>();
        nodesSorted.forEach(node -> {
            String nodeCategory = String.valueOf(node.getProps().get(categoryProps));
            if (!categories.contains(nodeCategory)) {
                return;
            }
            GraphNode graphNode = convertToGraphNode(node);

            for (int i = 0; i < categoryGroups.size(); i++) {
                if (categoryGroups.get(i).contains(nodeCategory)) {
                    graphNode.setGroupCategory((long) i);
                    break;
                }
            }
            graphNode.setCategory((long) categories.indexOf(nodeCategory));

            //计算表达式
            params.getNodeExpressions().forEach((key, value) -> {
                boolean result = AviatorUtils.tryMatchExpression(value, graphNode.getProps(),
                        graphNode.getFields());
                graphNode.getNodeExpressions().put(key, result);
            });

            graphNodes.add(graphNode);
        });

        //返回所有客户端参数
        graph.setParams(params);
        graph.setCategories(categories.stream().map(GraphCategory::new).collect(Collectors.toList()));
        graph.setCategoryGroups(categoryGroups);
        graph.setCategoryGroupsOther(categoryGroupsOther);
        graph.setRelationships(graphRelations);
        graph.setNodes(graphNodes);
        return graph;
    }

    private GraphAll buildGraph(List<BaseNode> nodes, List<BaseRelationship> relationships) {
        return buildGraph(nodes, relationships, new GraphGrafanaParams());
    }

    public GraphAll getGraph(GraphCQLReq req, GraphGrafanaParams params) {

        List<BaseNode> nodes = graphDao.executeCQL(BaseNode.class, req.getNodeCQL());

        List<BaseRelationship> relationships = graphDao.executeCQL(BaseRelationship.class, req.getRelationshipCQL());

        return buildGraph(nodes, relationships, params);
    }

    public void doExecuteCQL(GraphReq req) {
        long start = System.currentTimeMillis();

        graphDao.executeCQL(req.getExecuteCQL());

        logger.info("Execute CQL. Cost[{}ms],[{}]", System.currentTimeMillis() - start, req.getExecuteCQL());
    }

    public void doSaveNode(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel())
                || CollectionUtils.isEmpty(req.getStartNode().getProps())) {
            return;
        }
        //将label写入tags
        req.getStartNode().getProps().put("label", req.getStartNode().getLabel());
        req.getStartNode().getSaveProps().putAll(req.getStartNode().getProps());

        List<BaseNode> nodes = graphDao.saveNode(req);

        nodes.forEach(node -> {
            putFieldsById(String.valueOf(node.getProps().get("label")), node.getId(), req.getStartNode().getFields());
        });

        logger.info("Save Node. Label[{}],Cost[{}ms],Keys[{}],Req[{}]", req.getStartNode().getLabel(), System.currentTimeMillis() - start,
                req.getStartNode().getProps(), req.getStartNode());
    }

    public void doUpdateNode(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel())
                || CollectionUtils.isEmpty(req.getStartNode().getProps())) {
            return;
        }
        //将label写入tags
        req.getStartNode().getProps().put("label", req.getStartNode().getLabel());
        req.getStartNode().getSaveProps().putAll(req.getStartNode().getProps());

        List<BaseNode> nodes = graphDao.updateNode(req);

        nodes.forEach(node -> {
            putFieldsById(String.valueOf(node.getProps().get("label")), node.getId(), req.getStartNode().getFields());
        });

        logger.info("Update Node. Label[{}],Cost[{}ms],Keys[{}],Req[{}]", req.getStartNode().getLabel(), System.currentTimeMillis() - start,
                req.getStartNode().getProps(), req.getStartNode());
    }

    public GraphAll getNode(GraphReq req) {
        if (StringUtils.isEmpty(req.getStartNode().getLabel())) {
            return new GraphAll();
        }
        //将label写入tags
        req.getStartNode().getProps().put("label", req.getStartNode().getLabel());

        List<BaseNode> nodes = graphDao.getNode(req);
        return buildGraph(nodes, Collections.emptyList());
    }

    public void doDeleteNode(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel())
                || CollectionUtils.isEmpty(req.getStartNode().getProps())) {
            return;
        }
        //将label写入tags
        req.getStartNode().getProps().put("label", req.getStartNode().getLabel());

        List<BaseNode> nodes = graphDao.deleteNode(req);

        nodes.forEach(node -> {
            deleteFieldsById(String.valueOf(node.getProps().get("label")), node.getId());
        });

        logger.info("Delete Node. Label[{}],Cost[{}ms],Keys[{}],Req[{}]", req.getStartNode().getLabel(), System.currentTimeMillis() - start,
                req.getStartNode().getProps(), req.getStartNode());
    }


    public void doSaveRelationship(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel()) || CollectionUtils.isEmpty(req.getStartNode().getProps())
                || StringUtils.isEmpty(req.getEndNode().getLabel()) || CollectionUtils.isEmpty(req.getEndNode().getProps())
                || StringUtils.isEmpty(req.getRelationship().getLabel()) || CollectionUtils.isEmpty(req.getRelationship().getProps())) {
            return;
        }

        //将label写入tags
        req.getRelationship().getProps().put("label", req.getRelationship().getLabel());

        req.getRelationship().getSaveProps().putAll(req.getRelationship().getProps());

        List<BaseRelationship> relationships = graphDao.saveRelationship(req);

        relationships.forEach(relationship -> {
            putFieldsById(String.valueOf(relationship.getProps().get("label")), relationship.getId(), req.getRelationship().getFields());
        });

        logger.info("Save Relation. Label[{}-{}->{}],Cost[{}ms],sKeys[{}],rKeys[{}],tKeys[{}],Req[{}]", req.getStartNode().getLabel(), req.getRelationship().getLabel(),
                req.getEndNode().getLabel(), System.currentTimeMillis() - start, req.getStartNode().getProps(),
                req.getRelationship().getProps(), req.getEndNode().getProps(), req.getRelationship());
    }

    public void doUpdateRelationship(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel()) || CollectionUtils.isEmpty(req.getStartNode().getProps())
                || StringUtils.isEmpty(req.getEndNode().getLabel()) || CollectionUtils.isEmpty(req.getEndNode().getProps())
                || StringUtils.isEmpty(req.getRelationship().getLabel()) || CollectionUtils.isEmpty(req.getRelationship().getProps())) {
            return;
        }

        //将label写入tags
        req.getRelationship().getProps().put("label", req.getRelationship().getLabel());

        req.getRelationship().getSaveProps().putAll(req.getRelationship().getProps());

        List<BaseRelationship> relationships = graphDao.updateRelationship(req);

        relationships.forEach(relationship -> {
            putFieldsById(String.valueOf(relationship.getProps().get("label")), relationship.getId(), req.getRelationship().getFields());
        });

        logger.info("Update Relation. Label[{}-{}->{}],Cost[{}ms],sKeys[{}],rKeys[{}],tKeys[{}],Req[{}]", req.getStartNode().getLabel(), req.getRelationship().getLabel(),
                req.getEndNode().getLabel(), System.currentTimeMillis() - start, req.getStartNode().getProps(),
                req.getRelationship().getProps(), req.getEndNode().getProps(), req.getRelationship());
    }

    public GraphAll getRelationship(GraphReq req) {
        if (StringUtils.isEmpty(req.getStartNode().getLabel()) || StringUtils.isEmpty(req.getEndNode().getLabel())
                || StringUtils.isEmpty(req.getRelationship().getLabel())) {
            return new GraphAll();
        }
        //将label写入tags
        req.getRelationship().getProps().put("label", req.getRelationship().getLabel());

        List<BaseRelationship> relationships = graphDao.getRelationship(req);
        List<BaseNode> nodes = new ArrayList<>();
        relationships.forEach(relationship -> {
            nodes.add(relationship.getStartNode());
            nodes.add(relationship.getEndNode());
        });
        return buildGraph(nodes, relationships);
    }

    public void doDeleteRelationship(GraphReq req) {
        long start = System.currentTimeMillis();

        if (StringUtils.isEmpty(req.getStartNode().getLabel()) || CollectionUtils.isEmpty(req.getStartNode().getProps())
                || StringUtils.isEmpty(req.getEndNode().getLabel()) || CollectionUtils.isEmpty(req.getEndNode().getProps())
                || StringUtils.isEmpty(req.getRelationship().getLabel()) || CollectionUtils.isEmpty(req.getRelationship().getProps())) {
            return;
        }
        //将label写入tags
        req.getRelationship().getProps().put("label", req.getRelationship().getLabel());

        List<BaseRelationship> relationships = graphDao.deleteRelationship(req);

        relationships.forEach(relationship -> {
            deleteFieldsById(String.valueOf(relationship.getProps().get("label")), relationship.getId());
        });

        logger.info("Delete Relation. Label[{}-{}->{}],Cost[{}ms],sKeys[{}],rKeys[{}],tKeys[{}],Req[{}]", req.getStartNode().getLabel(), req.getRelationship().getLabel(),
                req.getEndNode().getLabel(), System.currentTimeMillis() - start, req.getStartNode().getProps(),
                req.getRelationship().getProps(), req.getEndNode().getProps(), req.getRelationship());
    }

    public void addGraphActionToQueue(GraphReq req) {
        if (!graphActionQueue.offer(req)) {
            logger.error("Add graph action failed, queue is full. {}", req);
        }
    }

    private void doExecuteGraphAction() {
        while (true) {
            try {
                GraphReq graphReq = graphActionQueue.take();
                executorService.execute(() -> {
                    if (graphReq.getGraphAction() == GraphAction.EXECUTE_CQL) {
                        doExecuteCQL(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.SAVE_NODE) {
                        doSaveNode(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.UPDATE_NODE) {
                        doUpdateNode(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.DELETE_NODE) {
                        doDeleteNode(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.SAVE_RELATIONSHIP) {
                        doSaveRelationship(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.UPDATE_RELATIONSHIP) {
                        doUpdateRelationship(graphReq);
                    } else if (graphReq.getGraphAction() == GraphAction.DELETE_RELATIONSHIP) {
                        doDeleteRelationship(graphReq);
                    }
                });
            } catch (Exception e) {
                logger.error("doExecuteGraphAction error. ", e);
            }
        }
    }

    public void doMarkOrDeleteNodesAndRelations() {
        while (ConfigTools3.getBoolean("graph.metric.server.mark.delete.on", false)) {
            try {
                //1分钟内上报就设置为有效
                long activateTs = System.currentTimeMillis() - ConfigTools3.getConfigAsLong(
                        "graph.metric.server.valid.period", 60_000L);
                String cqlActivateNodes = "MATCH (m:NODE) WHERE m.lastUpdateTime > '"
                        + activateTs + "' AND (m.props_deleted = '1' OR NOT EXISTS(m.props_deleted)) SET m.props_deleted = '0' RETURN m";
                graphDao.executeCQL(cqlActivateNodes);

                //1分钟内上报就设置为有效
                String cqlActivateRelations = "MATCH p=(m:NODE)-[r:RELATIONSHIP]-(n:NODE) WHERE r.lastUpdateTime > '"
                        + activateTs + "' AND (r.props_deleted = '1' OR NOT EXISTS(r.props_deleted)) SET r.props_deleted = '0' RETURN p";
                graphDao.executeCQL(cqlActivateRelations);


                //一分半钟未上报就设置为过期
                long markDeletedTs = System.currentTimeMillis() - ConfigTools3.getConfigAsLong(
                        "graph.metric.server.expired.period", 90_000L);
                String cqlMarkDeletedNodes = "MATCH (m:NODE) WHERE m.lastUpdateTime < '"
                        + markDeletedTs + "' AND m.props_deleted = '0' SET m.props_deleted = '1' RETURN m";
                graphDao.executeCQL(cqlMarkDeletedNodes);

                //一分半钟未上报就设置为过期
                String cqlMarkDeletedRelations = "MATCH p=(m:NODE)-[r:RELATIONSHIP]-(n:NODE) WHERE r.lastUpdateTime < '"
                        + markDeletedTs + "' AND r.props_deleted = '0' SET r.props_deleted = '1' RETURN p";
                graphDao.executeCQL(cqlMarkDeletedRelations);

                long doDeletedTs = System.currentTimeMillis() - ConfigTools3.getConfigAsLong(
                        "graph.metric.server.delete.period", 600_000L);
                //删除节点，默认10分钟未上报数据就删除。
                String cqlDoDeletedNodes = "MATCH (m:NODE) WHERE m.lastUpdateTime < '"
                        + doDeletedTs + "' AND m.props_deleted = '1' DETACH DELETE m RETURN m";
                graphDao.executeCQL(cqlDoDeletedNodes);

                //删除头端的关系，默认1天未上报数据就删除。
                String cqlDoDeletedRelations = "MATCH p=(m:NODE)-[r:RELATIONSHIP]-(n:NODE) WHERE r.lastUpdateTime < '"
                        + doDeletedTs + "' AND r.props_deleted = '1' DELETE r RETURN p";
                graphDao.executeCQL(cqlDoDeletedRelations);

                TimeUnit.MILLISECONDS.sleep(ConfigTools3.getConfigAsLong(
                        "graph.metric.server.delete.scheduler.sleep", 3_000L));
            } catch (Exception e) {
                logger.error("doMarkOrDeleteNodesAndRelations in scheduler. ", e);
            }
        }

    }

}
