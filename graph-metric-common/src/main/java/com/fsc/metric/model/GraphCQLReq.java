package com.fsc.metric.model;

import com.fsc.common.utils.AbstractPrintable;

public class GraphCQLReq extends AbstractPrintable {

    private String nodeCQL;
    private String relationshipCQL;
    private String executeCQL;

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
}
