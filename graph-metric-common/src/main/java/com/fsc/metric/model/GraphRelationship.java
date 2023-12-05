package com.fsc.metric.model;

import java.util.HashMap;
import java.util.Map;

public class GraphRelationship {

    private String id;
    private String source;
    private String target;
    private String createTime;
    private String lastUpdateTime;
    private String lastUpdateUser;
    private String version;
    private Map<String, Object> props = new HashMap<>();
    private Map<String, Object> fields = new HashMap<>();

    private Long category;

    private Map<String, Boolean> linkExpressions = new HashMap<>();

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof GraphRelationship) {
            return id.equals(((GraphRelationship) obj).getId());
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Map<String, Boolean> getLinkExpressions() {
        return linkExpressions;
    }

    public void setLinkExpressions(Map<String, Boolean> linkExpressions) {
        this.linkExpressions = linkExpressions;
    }
}
