package com.fsc.metric.dao;

import org.neo4j.ogm.annotation.NodeEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NodeEntity("NODE")
public class Node extends BaseNode {

    @org.neo4j.ogm.annotation.Relationship(type = "RELATIONSHIP")
    private Set<Relationship> serverConnected = new HashSet<>();

    public Node() {
    }

    public Node(Map<String, Object> tags) {
        super(tags);
    }

    public Set<Relationship> getServerConnected() {
        return serverConnected;
    }

    public void setServerConnected(Set<Relationship> serverConnected) {
        this.serverConnected = serverConnected;
    }

}

