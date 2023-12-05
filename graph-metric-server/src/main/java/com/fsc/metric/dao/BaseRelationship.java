package com.fsc.metric.dao;

import java.util.Map;

public abstract class BaseRelationship extends BaseNode {

    public BaseRelationship() {
    }

    public BaseRelationship(Long id) {
        super(id);
    }

    public BaseRelationship(Map<String, Object> props) {
        super(props);
    }

    public abstract BaseNode getStartNode();

    public abstract BaseNode getEndNode();

}
