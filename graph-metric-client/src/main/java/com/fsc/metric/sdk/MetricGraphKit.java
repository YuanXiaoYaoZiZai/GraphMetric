package com.fsc.metric.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fsc.metric.model.GraphAll;
import com.fsc.metric.model.GraphBaseReq;
import com.fsc.metric.model.GraphReq;
import com.fsc.common.utils.StreamResponse;

import java.util.Map;

public class MetricGraphKit {

    private static Logger logger = LoggerFactory.getLogger(MetricGraphKit.class);

    private MetricClientRetrofit metricClientRetrofit;

    public void initMetricCfg(String metricUrl) {
        this.metricClientRetrofit = new MetricClientRetrofit(metricUrl);
    }

    /**
     * Method: 获取实时上报属性参数
     */
    public Map<String, String> getRealTimeProps() {
        try {
            StreamResponse<Map<String, String>> response = metricClientRetrofit.execute(
                    metricClientRetrofit.getMetricClientRequest().getRealTimeProps()
            );
            return response.getData();
        } catch (Exception e) {
            logger.error("[GraphMetric] Get real-time props failed. ", e);
        }
        return null;
    }

    public void updateRealTimeProps(Map<String, String> req) {
        try {
            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().updateRealTimeProps(req)
            );

        } catch (Exception e) {
            logger.error("[GraphMetric] Update real-time props failed. ", e);
        }
    }


    /**
     * Method: 直接执行语句
     *
     * @param cql 查询语句
     */
    public void executeCQL(String cql) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setExecuteCQL(cql);
            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().executeCQL(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] CQL execute failed. [{}]", cql, e);
        }
    }

    /**
     * Method: 查询节点
     *
     * @param labelName 节点类型
     * @param props     节点属性，可以理解为联合主键
     * @return 返回当前节点关联的图表结构
     */
    public GraphAll getGraphNode(String labelName, Map<String, Object> props) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(labelName, props));

            StreamResponse<GraphAll> response = metricClientRetrofit.execute(
                    metricClientRetrofit.getMetricClientRequest().getNode(graphReq)
            );
            return response.getData();
        } catch (Exception e) {
            logger.error("[GraphMetric] Node get failed. [{}] [{}]",
                    labelName, props, e);
        }
        return null;
    }

    /**
     * Method: 删除节点
     *
     * @param labelName 节点类型
     * @param props     节点属性，可以理解为联合主键
     */
    public void deleteGraphNode(String labelName, Map<String, Object> props) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(labelName, props));

            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().deleteNode(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Node delete failed. [{}] [{}]",
                    labelName, props, e);
        }
    }

    /**
     * Method: 创建或修改节点
     *
     * @param labelName 节点类型
     * @param props     节点属性，可以理解为联合主键
     * @param saveProps 当前更新的节点属性
     * @param fields    节点其他属性，附加信息，存贮在缓存中，主要用于减轻图形结构存贮，加快查询
     */
    public void saveGraphNode(String labelName, Map<String, Object> props, Map<String, Object> saveProps, Map<String, Object> fields) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(labelName, props, saveProps, fields));

            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().saveNode(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Node save failed. [{}] [{}] [{}] [{}]",
                    labelName, props, saveProps, fields, e);
        }
    }

    /**
     * Method: 修改节点
     *
     * @param labelName 节点类型
     * @param props     节点属性，可以理解为联合主键
     * @param saveProps 当前更新的节点属性
     * @param fields    节点其他属性，附加信息，存贮在缓存中，主要用于减轻图形结构存贮，加快查询
     */
    public void updateGraphNode(String labelName, Map<String, Object> props, Map<String, Object> saveProps, Map<String, Object> fields) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(labelName, props, saveProps, fields));

            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().updateNode(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Node update failed. [{}] [{}] [{}] [{}]",
                    labelName, props, saveProps, fields, e);
        }
    }

    /**
     * Method: 创建或修改连线
     *
     * @param labelName  连线类型
     * @param props      连线属性，可以理解为联合主键
     * @param saveProps  当前更新的连线属性
     * @param fields     连线其他属性，附加信息，存贮在缓存中，主要用于减轻图形结构存贮，加快查询
     * @param sLabelName 起始节点类型
     * @param sNodeProps 起始节点属性，可以理解为联合主键
     * @param tLabelName 结束节点类型
     * @param tNodeProps 结束节点属性，可以理解为联合主键
     */
    public void saveGraphRelationship(String labelName, Map<String, Object> props,
                                      String sLabelName, Map<String, Object> sNodeProps,
                                      String tLabelName, Map<String, Object> tNodeProps,
                                      Map<String, Object> saveProps, Map<String, Object> fields) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(sLabelName, sNodeProps));
            graphReq.setEndNode(new GraphBaseReq(tLabelName, tNodeProps));
            graphReq.setRelationship(new GraphBaseReq(labelName, props, saveProps, fields));

            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().saveRelationship(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Relationship save failed. [{}] [{}] [{}] [{}],[{}] [{}],[{}] [{}]",
                    labelName, props, saveProps, fields, sLabelName, sNodeProps, tLabelName, tNodeProps, e);
        }
    }

    /**
     * Method: 修改连线
     *
     * @param labelName  连线类型
     * @param props      连线属性，可以理解为联合主键
     * @param saveProps  当前更新的连线属性
     * @param fields     连线其他属性，附加信息，存贮在缓存中，主要用于减轻图形结构存贮，加快查询
     * @param sLabelName 起始节点类型
     * @param sNodeProps 起始节点属性，可以理解为联合主键
     * @param tLabelName 结束节点类型
     * @param tNodeProps 结束节点属性，可以理解为联合主键
     */
    public void updateGraphRelationship(String labelName, Map<String, Object> props,
                                        String sLabelName, Map<String, Object> sNodeProps,
                                        String tLabelName, Map<String, Object> tNodeProps,
                                        Map<String, Object> saveProps, Map<String, Object> fields) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(sLabelName, sNodeProps));
            graphReq.setEndNode(new GraphBaseReq(tLabelName, tNodeProps));
            graphReq.setRelationship(new GraphBaseReq(labelName, props, saveProps, fields));

            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().updateRelationship(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Relationship update failed. [{}] [{}] [{}] [{}],[{}] [{}],[{}] [{}]",
                    labelName, props, saveProps, fields, sLabelName, sNodeProps, tLabelName, tNodeProps, e);
        }
    }

    /**
     * Method: 查询连线
     *
     * @param labelName  连线类型
     * @param props      连线属性，可以理解为联合主键
     * @param sLabelName 起始节点类型
     * @param sNodeProps 起始节点属性，可以理解为联合主键
     * @param tLabelName 结束节点类型
     * @param tNodeProps 结束节点属性，可以理解为联合主键
     * @return 返回当前连线关联的图表结构
     */
    public GraphAll getGraphRelationship(String labelName, Map<String, Object> props,
                                         String sLabelName, Map<String, Object> sNodeProps,
                                         String tLabelName, Map<String, Object> tNodeProps) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(sLabelName, sNodeProps));
            graphReq.setEndNode(new GraphBaseReq(tLabelName, tNodeProps));
            graphReq.setRelationship(new GraphBaseReq(labelName, props));


            StreamResponse<GraphAll> response = metricClientRetrofit.execute(
                    metricClientRetrofit.getMetricClientRequest().getRelationship(graphReq)
            );
            return response.getData();
        } catch (Exception e) {
            logger.error("[GraphMetric] Relationship get failed. [{}] [{}],[{}] [{}],[{}] [{}]",
                    labelName, props, sLabelName, sNodeProps, tLabelName, tNodeProps, e);
        }
        return null;
    }

    /**
     * Method: 删除连线
     *
     * @param labelName  连线类型
     * @param props      连线属性，可以理解为联合主键
     * @param sLabelName 起始节点类型
     * @param sNodeProps 起始节点属性，可以理解为联合主键
     * @param tLabelName 结束节点类型
     * @param tNodeProps 结束节点属性，可以理解为联合主键
     */
    public void deleteGraphRelationship(String labelName, Map<String, Object> props,
                                        String sLabelName, Map<String, Object> sNodeProps,
                                        String tLabelName, Map<String, Object> tNodeProps) {
        try {
            GraphReq graphReq = new GraphReq();
            graphReq.setStartNode(new GraphBaseReq(sLabelName, sNodeProps));
            graphReq.setEndNode(new GraphBaseReq(tLabelName, tNodeProps));
            graphReq.setRelationship(new GraphBaseReq(labelName, props));
            metricClientRetrofit.asyncExecute(
                    metricClientRetrofit.getMetricClientRequest().deleteRelationship(graphReq)
            );
        } catch (Exception e) {
            logger.error("[GraphMetric] Relationship delete failed. [{}] [{}],[{}] [{}],[{}] [{}]",
                    labelName, props, sLabelName, sNodeProps, tLabelName, tNodeProps, e);
        }
    }


}
