package com.fsc.metric.model;

import com.fsc.common.utils.AbstractPrintable;

public class GraphReq extends AbstractPrintable {

    private GraphAction graphAction;

    private String lastUpdateUser;

    private String nodeCQL;
    private String relationshipCQL;
    private String executeCQL;

    private GraphBaseReq startNode = new GraphBaseReq();
    private GraphBaseReq endNode = new GraphBaseReq();
    private GraphBaseReq relationship = new GraphBaseReq();

    public String getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(String lastUpdateUser) {
        this.lastUpdateUser = lastUpdateUser;
    }

    public GraphAction getGraphAction() {
        return graphAction;
    }

    public void setGraphAction(GraphAction graphAction) {
        this.graphAction = graphAction;
    }

    public String getNodeCQL() {
        return nodeCQL;
    }

    public void setNodeCQL(String nodeCQL) {
        this.nodeCQL = nodeCQL;
    }

    public String getRelationshipCQL() {
        return relationshipCQL;
    }

    public void setRelationshipCQL(String relationshipCQL) {
        this.relationshipCQL = relationshipCQL;
    }

    public String getExecuteCQL() {
        return executeCQL;
    }

    public void setExecuteCQL(String executeCQL) {
        this.executeCQL = executeCQL;
    }

    public GraphBaseReq getStartNode() {
        return startNode;
    }

    public void setStartNode(GraphBaseReq startNode) {
        this.startNode = startNode;
    }

    public GraphBaseReq getEndNode() {
        return endNode;
    }

    public void setEndNode(GraphBaseReq endNode) {
        this.endNode = endNode;
    }

    public GraphBaseReq getRelationship() {
        return relationship;
    }

    public void setRelationship(GraphBaseReq relationship) {
        this.relationship = relationship;
    }

}
