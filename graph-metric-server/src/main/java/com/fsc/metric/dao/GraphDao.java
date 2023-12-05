package com.fsc.metric.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.transaction.SessionFactoryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.fsc.metric.model.GraphReq;
import com.fsc.metric.utils.TemplateTools;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional("neo4jTransactionManager")
public class GraphDao {

    private final static Logger logger = LoggerFactory.getLogger(GraphDao.class);

    private static final String SAVE_NODE = "MERGE (m:{{mLabel}} {{mProps}}) " +
            "ON CREATE SET {{mSaveProps}} ON MATCH SET {{mUpdateProps}} RETURN m";
    private static final String UPDATE_NODE = "MATCH (m:{{mLabel}} {{mProps}}) " +
            " SET {{mUpdateProps}} RETURN m";
    private static final String GET_NODE = "MATCH (m:{{mLabel}} {{mProps}}) RETURN m";
    private static final String DELETE_NODE = "MATCH (m:{{mLabel}} {{mProps}}) DETACH DELETE m RETURN m";

    private static final String SAVE_RELATIONSHIP =
            "MATCH (m:{{mLabel}} {{mProps}}) " +
                    "MATCH (n:{{nLabel}} {{nProps}}) " +
                    "MERGE p=(m)-[r:{{rLabel}} {{rProps}}]->(n) " +
                    "ON CREATE SET {{rSaveProps}} ON MATCH SET {{rUpdateProps}} " +
                    "RETURN p";

    private static final String UPDATE_RELATIONSHIP =
            "MATCH p=(m:{{mLabel}} {{mProps}})-[r:{{rLabel}} {{rProps}}]->(n:{{nLabel}} {{nProps}}) SET {{rUpdateProps}} RETURN p";
    private static final String GET_RELATIONSHIP =
            "MATCH p=(m:{{mLabel}} {{mProps}})-[r:{{rLabel}} {{rProps}}]->(n:{{nLabel}} {{nProps}}) RETURN p";
    private static final String DELETE_RELATIONSHIP =
            "MATCH p=(m:{{mLabel}} {{mProps}})-[r:{{rLabel}} {{rProps}}]->(n:{{nLabel}} {{nProps}}) DELETE r RETURN p";

    @Autowired
    private SessionFactory sessionFactory;

    private String buildProps(Map<String, Object> props, Map<String, Object> customProps, String start, String end, String prefix, String separator) {
        StringBuffer sb = new StringBuffer(start);
        props.forEach((key, value) -> {
            sb.append(prefix).append("props_").append(key).append(separator).append("'").append(value).append("'").append(",");
        });

        customProps.forEach((key, value) -> {
            sb.append(prefix).append(key).append(separator).append("'").append(value).append("'").append(",");
        });

        if (sb.lastIndexOf(",") > 0) {
            sb.replace(sb.lastIndexOf(","), sb.length(), "");
        }
        sb.append(end);
        return sb.toString();
    }

    private String buildProps(Map<String, Object> props) {
        return buildProps(props, ImmutableMap.of(), "{", "}", "", ":");
    }

    private Long getVersion() {
        return -1L;
    }

    private String buildSaveProps(String lastUpdateUser, Map<String, Object> props, String prefix) {
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("createTime", System.currentTimeMillis());
        customProps.put("lastUpdateTime", System.currentTimeMillis());
        customProps.put("lastUpdateUser", lastUpdateUser);
        customProps.put("version", getVersion());
        return buildProps(props, customProps, "", "", prefix, "=");
    }

    private String buildUpdateProps(String lastUpdateUser, Map<String, Object> props, String prefix) {
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("lastUpdateTime", System.currentTimeMillis());
        customProps.put("lastUpdateUser", lastUpdateUser);
        customProps.put("version", getVersion());
        return buildProps(props, customProps, "", "", prefix, "=");
    }

    private Class getEntityClass(String label) {
        return label.contains("NODE") ? Node.class : Relationship.class;
    }

    private String getLabelName(String label) {
        return label.contains("NODE") ? "NODE" : "RELATIONSHIP";
    }

    private <T extends BaseNode> List<T> execute(String template, String label, GraphReq req) {
        Class<T> entityClass = getEntityClass(label);
        if (entityClass == null) {
            return Collections.emptyList();
        }

        Map<String, Object> params = new HashMap<>();
        if (!StringUtils.isEmpty(req.getStartNode().getLabel())) {
            params.put("mLabel", getLabelName(req.getStartNode().getLabel()));
            params.put("mProps", buildProps(req.getStartNode().getProps()));
            params.put("mSaveProps", buildSaveProps(req.getLastUpdateUser(), req.getStartNode().getSaveProps(), "m."));
            params.put("mUpdateProps", buildUpdateProps(req.getLastUpdateUser(), req.getStartNode().getSaveProps(), "m."));
        }

        if (!StringUtils.isEmpty(req.getEndNode().getLabel())) {
            params.put("nLabel", getLabelName(req.getEndNode().getLabel()));
            params.put("nProps", buildProps(req.getEndNode().getProps()));
            params.put("nSaveProps", buildSaveProps(req.getLastUpdateUser(), req.getEndNode().getSaveProps(), "n."));
            params.put("nUpdateProps", buildUpdateProps(req.getLastUpdateUser(), req.getStartNode().getSaveProps(), "n."));
        }

        if (!StringUtils.isEmpty(req.getRelationship().getLabel())) {
            params.put("rLabel", getLabelName(req.getRelationship().getLabel()));
            params.put("rProps", buildProps(req.getRelationship().getProps()));
            params.put("rSaveProps", buildSaveProps(req.getLastUpdateUser(), req.getRelationship().getSaveProps(), "r."));
            params.put("rUpdateProps", buildUpdateProps(req.getLastUpdateUser(), req.getRelationship().getSaveProps(), "r."));
        }

        String cql = TemplateTools.replacePlaceholder(template, params);
        return executeCQL(entityClass, cql);
    }

    public <T extends BaseNode> List<T> executeCQL(Class<T> clazz, String cql) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory);
            Iterable<T> query = session.query(clazz, cql, Maps.newHashMap());
            return Lists.newArrayList(query);
        } catch (Exception e) {
            logger.error("[Graph] execute CQL Error, {},{}", clazz.getName(), cql, e);
        }
        return Collections.emptyList();
    }

    public void executeCQL(String cql) {
        try {
            Session session = SessionFactoryUtils.getSession(sessionFactory);
            session.query(cql, Maps.newHashMap());
        } catch (Exception e) {
            logger.error("[Graph] execute CQL Error, {}", cql, e);
        }
    }

    public <T extends BaseNode> List<T> getNode(GraphReq req) {
        return execute(GET_NODE, req.getStartNode().getLabel(), req);
    }

    public <T extends BaseNode> List<T> saveNode(GraphReq req) {
        return execute(SAVE_NODE, req.getStartNode().getLabel(), req);
    }

    public <T extends BaseNode> List<T> updateNode(GraphReq req) {
        return execute(UPDATE_NODE, req.getStartNode().getLabel(), req);
    }

    public <T extends BaseNode> List<T> deleteNode(GraphReq req) {
        List<T> nodes = getNode(req);
        execute(DELETE_NODE, req.getStartNode().getLabel(), req);
        return nodes;
    }

    public <T extends BaseNode> List<T> getRelationship(GraphReq req) {
        return execute(GET_RELATIONSHIP, req.getRelationship().getLabel(), req);
    }

    public <T extends BaseNode> List<T> saveRelationship(GraphReq req) {
        return execute(SAVE_RELATIONSHIP, req.getRelationship().getLabel(), req);
    }

    public <T extends BaseNode> List<T> updateRelationship(GraphReq req) {
        return execute(UPDATE_RELATIONSHIP, req.getRelationship().getLabel(), req);
    }

    public <T extends BaseNode> List<T> deleteRelationship(GraphReq req) {
        List<T> relationships = getRelationship(req);
        execute(DELETE_RELATIONSHIP, req.getRelationship().getLabel(), req);
        return relationships;
    }

}
