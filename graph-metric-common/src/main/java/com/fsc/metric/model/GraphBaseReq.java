package com.fsc.metric.model;


import com.fsc.common.utils.AbstractPrintable;

import java.util.HashMap;
import java.util.Map;

public class GraphBaseReq extends AbstractPrintable {

    private String label;
    private Map<String, Object> props = new HashMap<>();
    private Map<String, Object> saveProps = new HashMap<>();
    private Map<String, Object> fields = new HashMap<>();

    public GraphBaseReq() {
    }

    public GraphBaseReq(String label, Map<String, Object> props) {
        this.label = label;
        this.props = props;
    }

    public GraphBaseReq(String label, Map<String, Object> props, Map<String, Object> saveProps, Map<String, Object> fields) {
        this.label = label;
        this.props = props;
        this.saveProps = saveProps;
        this.fields = fields;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public Map<String, Object> getSaveProps() {
        return saveProps;
    }

    public void setSaveProps(Map<String, Object> saveProps) {
        this.saveProps = saveProps;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

}
