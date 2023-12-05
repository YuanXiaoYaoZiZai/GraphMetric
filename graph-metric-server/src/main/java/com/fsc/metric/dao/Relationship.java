package com.fsc.metric.dao;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.Map;

@RelationshipEntity(type = "RELATIONSHIP")
public class Relationship extends BaseRelationship {

    @StartNode
    private Node startNode;
    @EndNode
    private Node endNode;

    public Relationship() {
    }

    public Relationship(Map<String, Object> tags) {
        super(tags);
    }


    @Override
    public BaseNode getStartNode() {
        return startNode;
    }

    @Override
    public BaseNode getEndNode() {
        return endNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

}
