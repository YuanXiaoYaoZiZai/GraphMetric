package com.fsc.metric.model;

import java.util.*;

public class GraphAll {

    private List<GraphCategory> categories = new ArrayList<>();
    private List<List<String>> categoryGroups = new ArrayList<>();
    private List<List<String>> categoryGroupsOther = new ArrayList<>();
    private LinkedHashSet<GraphNode> nodes = new LinkedHashSet<>();
    private LinkedHashSet<GraphRelationship> relationships = new LinkedHashSet<>();

    private GraphGrafanaParams params = new GraphGrafanaParams();

    public List<GraphCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<GraphCategory> categories) {
        this.categories = categories;
    }

    public List<List<String>> getCategoryGroups() {
        return categoryGroups;
    }

    public void setCategoryGroups(List<List<String>> categoryGroups) {
        this.categoryGroups = categoryGroups;
    }

    public List<List<String>> getCategoryGroupsOther() {
        return categoryGroupsOther;
    }

    public void setCategoryGroupsOther(List<List<String>> categoryGroupsOther) {
        this.categoryGroupsOther = categoryGroupsOther;
    }

    public LinkedHashSet<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(LinkedHashSet<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public LinkedHashSet<GraphRelationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(LinkedHashSet<GraphRelationship> relationships) {
        this.relationships = relationships;
    }

    public GraphGrafanaParams getParams() {
        return params;
    }

    public void setParams(GraphGrafanaParams params) {
        this.params = params;
    }
}
