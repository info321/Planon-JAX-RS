package us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors;

import java.math.BigDecimal;
import java.util.Date;

import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils.MiscFunctions;


public final class ESAPIField {

    private static final String QUOTE_COMMA_SPACE_QUOTE = "', '";

    enum FieldType {
        BIGDECIMAL, BOOLEAN, DATE, DATETIME, INTEGER, PRIMARYKEY, REFERENCE, STRING, STRINGREFERENCE, TIME;
    }

    private final String pnName;
    private FieldType type;
    private BigDecimal bigDecimalValue;
    private Boolean booleanValue;
    private Date dateValue;
    private Integer integerValue;
    private String stringValue;

    public ESAPIField(String field) {
        this.pnName = field;
        clear();
    }

    public ESAPIField(String field, FieldType type) {
        this.pnName = field;
        setValue(type, null);
    }

    public ESAPIField(String field, FieldType type, Object value) {
        this.pnName = field;
        setValue(type, value);
    }

    public ESAPIField(String field, FieldType type, Object value, String referenceTypeName) {
        this.pnName = field;
        setValue(type, value, referenceTypeName);
    }

    /*
     * Copy constructor http://stackoverflow.com/a/869078/610329
     */
    public ESAPIField(ESAPIField field) {
        this.pnName = field.pnName;
        this.type = field.type;
        this.bigDecimalValue = field.bigDecimalValue;
        this.booleanValue = field.booleanValue;
        this.dateValue = field.dateValue;
        this.integerValue = field.integerValue;
        this.stringValue = field.stringValue;
    }

    private void clear() {
        type = null;// NOPMD
        bigDecimalValue = null;// NOPMD
        booleanValue = null;// NOPMD
        dateValue = null;// NOPMD
        integerValue = null;// NOPMD
        stringValue = null;// NOPMD
    }

    public boolean hasValue() {
        return (MiscFunctions.hasValue(bigDecimalValue) || MiscFunctions.hasValue(booleanValue) || MiscFunctions.hasValue(dateValue)
                        || MiscFunctions.hasValue(integerValue) || MiscFunctions.hasValue(stringValue));
    }

    public String getPnName() {
        return pnName;
    }

    public FieldType getType() {
        return type;
    }

    private void setValue(FieldType aType, Object aValue) {
        setValue(aType, aValue, null);
    }

    private void setValue(FieldType aType, Object value, String referenceTypeName) {
        switch (aType) {
            case BIGDECIMAL:
                this.setBigDecimalValue((BigDecimal) value);
                break;
            case BOOLEAN:
                this.setBooleanValue((Boolean) value);
                break;
            case DATE:
                this.setDateValue((Date) value);
                break;
            case DATETIME:
                this.setDateTimeValue((Date) value);
                break;
            case INTEGER:
                this.setIntegerValue((Integer) value);
                break;
            case PRIMARYKEY:
                this.setPrimaryKeyValue((Integer) value);
                break;
            case REFERENCE:
                this.setReferenceValue((Integer) value, referenceTypeName);
                break;
            case STRING:
                this.setStringValue((String) value);
                break;
            case STRINGREFERENCE:
                this.setStringReferenceValue((String) value);
                break;
            case TIME:
                this.setTimeValue((Date) value);
                break;
        }
    }

    public void setBigDecimalValue(BigDecimal value) {
        clear();
        type = FieldType.BIGDECIMAL;
        bigDecimalValue = value;
    }

    public BigDecimal getBigDecimalValue() {
        if (FieldType.BIGDECIMAL.equals(type)) {
            return bigDecimalValue;
        }
        return null;
    }

    public void setBooleanValue(Boolean value) {
        clear();
        type = FieldType.BOOLEAN;
        booleanValue = value;
    }

    public Boolean getBooleanValue() {
        if (FieldType.BOOLEAN.equals(type)) {
            return booleanValue;
        }
        return null;
    }

    public void setDateValue(Date value) {
        clear();
        type = FieldType.DATE;
        dateValue = value;
    }

    public Date getDateValue() {
        if (FieldType.DATE.equals(type)) {
            return dateValue;
        }
        return null;
    }

    public void setDateTimeValue(Date value) {
        clear();
        type = FieldType.DATETIME;
        dateValue = value;
    }

    public Date getDateTimeValue() {
        if (FieldType.DATETIME.equals(type)) {
            return dateValue;
        }
        return null;
    }

    public void setIntegerValue(Integer value) {
        clear();
        type = FieldType.INTEGER;
        integerValue = value;
    }

    public Integer getIntegerValue() {
        if (FieldType.INTEGER.equals(type)) {
            return integerValue;
        }
        return null;
    }

    public void setPrimaryKeyValue(Integer value) {
        clear();
        type = FieldType.PRIMARYKEY;
        integerValue = value;
    }

    public Integer getPrimaryKeyValue() {
        if (FieldType.PRIMARYKEY.equals(type)) {
            return integerValue;
        }
        return null;
    }

    public void setReferenceValue(Integer syscode, String referenceTypeName) {
        clear();
        type = FieldType.REFERENCE;
        integerValue = syscode;
        stringValue = referenceTypeName;
    }

    public Integer getReferenceValue() {
        if (FieldType.REFERENCE.equals(type)) {
            return integerValue;
        }
        return null;
    }

    public String getReferenceTypeValue() {
        if (FieldType.REFERENCE.equals(type)) {
            return stringValue;
        }
        return null;
    }

    public void setStringValue(String value) {
        clear();
        type = FieldType.STRING;
        stringValue = value;
    }

    public String getStringValue() {
        if (FieldType.STRING.equals(type)) {
            return stringValue;
        }
        return null;
    }

    public void setStringReferenceValue(String value) {
        clear();
        type = FieldType.STRINGREFERENCE;
        stringValue = value;
    }

    public String getStringReferenceValue() {
        if (FieldType.STRINGREFERENCE.equals(type)) {
            return stringValue;
        }
        return null;
    }

    public void setTimeValue(Date value) {
        clear();
        type = FieldType.TIME;
        dateValue = value;
    }

    public Date getTimeValue() {
        if (FieldType.TIME.equals(type)) {
            return dateValue;
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder toStringBuilder = new StringBuilder(50).append("ESAPI Field: PnName: '")
                                                             .append(pnName)
                                                             .append("' Type: '")
                                                             .append(type)
                                                             .append("' Value: '");

        if (this.hasValue()) {
            if (MiscFunctions.hasValue(bigDecimalValue)) {
                toStringBuilder.append(bigDecimalValue)
                               .append(QUOTE_COMMA_SPACE_QUOTE);
            }
            if (booleanValue != null) {
                toStringBuilder.append(booleanValue)
                               .append(QUOTE_COMMA_SPACE_QUOTE);
            }
            if (MiscFunctions.hasValue(dateValue)) {
                toStringBuilder.append(dateValue)
                               .append(QUOTE_COMMA_SPACE_QUOTE);
            }
            if (MiscFunctions.hasValue(integerValue)) {
                toStringBuilder.append(integerValue)
                               .append(QUOTE_COMMA_SPACE_QUOTE);
            }
            if (MiscFunctions.hasValue(stringValue)) {
                toStringBuilder.append(stringValue)
                               .append(QUOTE_COMMA_SPACE_QUOTE);
            }
            return toStringBuilder.subSequence(0, (toStringBuilder.length() - 3))
                                  .toString();
        }

        toStringBuilder.append('\'');
        return toStringBuilder.toString();
    }
}
