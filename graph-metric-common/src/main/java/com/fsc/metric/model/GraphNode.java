package com.fsc.metric.model;

import java.util.HashMap;
import java.util.Map;

public class GraphNode {

    private String id;
    private String createTime;
    private String lastUpdateTime;
    private String lastUpdateUser;
    private String version;
    private Map<String, Object> props = new HashMap<>();
    private Map<String, Object> fields = new HashMap<>();

    private Long groupCategory;
    private Long category;

    private Map<String, Boolean> nodeExpressions = new HashMap<>();

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof GraphNode) {
            return id.equals(((GraphNode) obj).getId());
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Long getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(Long groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Boolean> getNodeExpressions() {
        return nodeExpressions;
    }

    public void setNodeExpressions(Map<String, Boolean> nodeExpressions) {
        this.nodeExpressions = nodeExpressions;
    }

}
