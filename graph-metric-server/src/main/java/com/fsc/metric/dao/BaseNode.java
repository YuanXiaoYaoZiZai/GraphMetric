package com.fsc.metric.dao;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Properties;

import java.util.HashMap;
import java.util.Map;

public class BaseNode {

    @Id
    @GeneratedValue
    private Long id;

    @Properties(allowCast = true, delimiter = "_")
    private Map<String, Object> props = new HashMap<>();

    private String createTime;
    private String lastUpdateTime;
    private String lastUpdateUser;
    private String version;

    public BaseNode() {
    }

    public BaseNode(Long id) {
        this.id = id;
    }

    public BaseNode(Map<String, Object> props) {
        this.props = props;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
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
}

