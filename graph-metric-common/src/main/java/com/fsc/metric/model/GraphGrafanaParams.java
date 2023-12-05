package com.fsc.metric.model;


import com.fsc.common.utils.AbstractPrintable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphGrafanaParams extends AbstractPrintable {

    private String categoryProps = "label";
    private List<List<String>> categoryGroups = new ArrayList<>();
    private List<List<String>> categoryGroupsOther = new ArrayList<>();

    private Map<String, String> extraParams = new HashMap<>();
    private Map<String, String> nodeExpressions = new HashMap<>();
    private Map<String, String> linkExpressions = new HashMap<>();

    public String getCategoryProps() {
        return categoryProps;
    }

    public void setCategoryProps(String categoryProps) {
        this.categoryProps = categoryProps;
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

    public Map<String, String> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, String> extraParams) {
        this.extraParams = extraParams;
    }

    public Map<String, String> getNodeExpressions() {
        return nodeExpressions;
    }

    public void setNodeExpressions(Map<String, String> nodeExpressions) {
        this.nodeExpressions = nodeExpressions;
    }

    public Map<String, String> getLinkExpressions() {
        return linkExpressions;
    }

    public void setLinkExpressions(Map<String, String> linkExpressions) {
        this.linkExpressions = linkExpressions;
    }

}
