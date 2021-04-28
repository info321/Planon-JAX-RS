package us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors;

import nl.planon.enterprise.service.api.PnESOperator;


public class FieldAndValuePair {
    private String field;
    private Object value;
    private PnESOperator operator;

    public FieldAndValuePair(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    public FieldAndValuePair(String field, Object value, PnESOperator operator) {
        this.field = field;
        this.value = value;
        this.operator = operator;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public PnESOperator getOperator() {
        return operator;
    }

    public void setOperator(PnESOperator operator) {
        this.operator = operator;
    }

}
