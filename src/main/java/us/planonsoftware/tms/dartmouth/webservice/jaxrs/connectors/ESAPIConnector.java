package us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import nl.planon.enterprise.service.api.IPnESAction;
import nl.planon.enterprise.service.api.IPnESActionListManager;
import nl.planon.enterprise.service.api.IPnESBusinessObject;
import nl.planon.enterprise.service.api.IPnESContext;
import nl.planon.enterprise.service.api.IPnESContextCreator;
import nl.planon.enterprise.service.api.IPnESDatabaseQuery;
import nl.planon.enterprise.service.api.IPnESMessageHandler;
import nl.planon.enterprise.service.api.IPnESResultSet;
import nl.planon.enterprise.service.api.IPnESSession;
import nl.planon.enterprise.service.api.IPnESStateChange;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import nl.planon.enterprise.service.api.PnESOperator;
import nl.planon.enterprise.service.api.factory.PnESContextCreator;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors.ESAPIField.FieldType;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils.MiscFunctions;


public final class ESAPIConnector {

    private static final ESAPIConnector INSTANCE = new ESAPIConnector();

    private static final String FIELD_SYSCODE = "Syscode";

    // No session needed for a Client Extension since one already exists in the Java Client
    // private IPnESSession session = null;

    private ESAPIConnector() {
    }

    public static ESAPIConnector getInstance() {
        return INSTANCE;
    }

    // public IPnESContext getContext() {
    // if (context == null) {
    // context = PnESContextFactory.getInstance().createContext();
    // }
    // return context;
    // }

    /**
     * 
     * @return IPnESContext context
     */
    public static IPnESContext getContext() {
        final IPnESContextCreator contextCreator = PnESContextCreator.getInstance();
        return getContextFromInstance(contextCreator, null);
    }

    private static IPnESContext getContextFromInstance(final IPnESContextCreator contextCreator, final IPnESSession session) {
        IPnESContext context = null;
        if (session == null) {
            context = contextCreator.createContext();
        } else {
            context = contextCreator.createContext(session);
        }
        return context;
    }

    public String getDataSection() {
        return getContext().getDataSection();
    }

    public ImageIcon getIcon(String iconName) {
        return getContext().loadIcon(iconName);
    }

    /*
     * Create BO
     */
    public IPnESBusinessObject createBO(String boTypeName) throws PnESBusinessException, PnESActionNotFoundException {
        IPnESBusinessObject bo = null;
        IPnESActionListManager actionListManager = ESAPIConnector.getContext()
                                                                 .getActionListManager(boTypeName);
        IPnESAction bomAddAction = actionListManager.getAction("BomAdd");
        if (bomAddAction == null) {
            throw new RuntimeException("Unable to add Element: " + boTypeName);
        }
        bo = bomAddAction.execute();
        return bo;
    }

    public IPnESBusinessObject createBO(String boTypeName, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        return createBO(boTypeName, "BomAdd", fieldAndValuePairArr);
    }

    public IPnESBusinessObject createBO(String boTypeName, String action, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {

        IPnESBusinessObject newBO = null;
        IPnESActionListManager actionListManager = this.getContext()
                                                       .getActionListManager(boTypeName);
        IPnESAction bomAction = actionListManager.getAction(action);
        // System.out.println("bomAction.getNumberOfArguments(): " +
        // bomAction.getNumberOfArguments());
        // for (int i = 0; i < bomAction.getNumberOfArguments(); i++) {
        // System.out.println("Argument for action " + boTypeName + "." + action + ": PnName: " +
        // bomAction.getArgumentPnName(i) + ", label: "
        // + bomAction.getArgumentLabel(i) + ", type: " + bomAction.getArgumentType(i).toString());
        // }

        extracted(bomAction, fieldAndValuePairArr);

        if (bomAction == null) {
            throw new RuntimeException("Unable to " + action + " Element: " + boTypeName);
        }
        newBO = bomAction.execute();
        return newBO;
    }

    private void extracted(IPnESAction bomAction, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        for (FieldAndValuePair fieldAndValuePair : fieldAndValuePairArr) {
            if (fieldAndValuePair != null && fieldAndValuePair.getField() != null && fieldAndValuePair.getValue() != null) {
                if (fieldAndValuePair.getValue() instanceof IPnESBusinessObject) {
                    bomAction.getReferenceArgument(fieldAndValuePair.getField())
                             .setValue(((IPnESBusinessObject) fieldAndValuePair.getValue()).getPrimaryKey());
                } else if (fieldAndValuePair.getValue() instanceof Integer) {
                    bomAction.getIntegerArgument(fieldAndValuePair.getField())
                             .setValue((Integer) fieldAndValuePair.getValue());
                } else if (fieldAndValuePair.getValue() instanceof String) {
                    bomAction.getStringArgument(fieldAndValuePair.getField())
                             .setValue((String) fieldAndValuePair.getValue());
                } else if (fieldAndValuePair.getValue() instanceof Date) {
                    bomAction.getDateArgument(fieldAndValuePair.getField())
                             .setValue((Date) fieldAndValuePair.getValue());
                } else if (fieldAndValuePair.getValue() instanceof BigDecimal) {
                    bomAction.getBigDecimalArgument(fieldAndValuePair.getField())
                             .setValue((BigDecimal) fieldAndValuePair.getValue());
                } else if (fieldAndValuePair.getValue() instanceof Boolean) {
                    bomAction.getBooleanArgument(fieldAndValuePair.getField())
                             .setValue((Boolean) fieldAndValuePair.getValue());
                }
            }
        }
    }

    public IPnESBusinessObject createBO(IPnESBusinessObject bo, String action, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        IPnESActionListManager actionListManager = bo.getActionListManager();
        IPnESAction bomAction = actionListManager.getAction(action);

        extracted(bomAction, fieldAndValuePairArr);

        if (bomAction == null) {
            throw new RuntimeException("Unable to " + action + " Element: " + bo);
        }
        return bomAction.execute();
    }

    public IPnESBusinessObject createCommunicationLogBO(IPnESBusinessObject selectedBO, String rootBOPnName, String comlogPnname)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        // get root bo type of the selected BO & its syscode
        Integer pkBO = selectedBO.getPrimaryKey();
        return createBO(comlogPnname, new FieldAndValuePair(FIELD_SYSCODE, pkBO), new FieldAndValuePair("BOType", rootBOPnName));
    }

    public IPnESBusinessObject createCommunicationLogBO(Integer pkSelectedBO, String rootBOPnName, String comlogPnname)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        // get root bo type of the selected BO & its syscode
        Integer pkBO = pkSelectedBO;
        return createBO(comlogPnname, new FieldAndValuePair(FIELD_SYSCODE, pkBO), new FieldAndValuePair("BOType", rootBOPnName));
    }

    /*
     * Read BO
     */
    public IPnESBusinessObject readBO(String boTypeName, Integer pk) throws PnESBusinessException, PnESActionNotFoundException {
        IPnESActionListManager actionListManager = this.getContext()
                                                       .getActionListManager(boTypeName);
        IPnESAction bomReadAction = actionListManager.getReadAction(pk);
        if (bomReadAction == null) {
            throw new RuntimeException("Unable to read Element: " + boTypeName);
        }
        return bomReadAction.execute();
    }

    // public Integer findPkForBOStateUserDefinedCode(String stateCode) throws
    // PnESBusinessException, PnESFieldNotFoundException {
    // return findPkForBOUsingPVDbQuery(IUserBOState.QNAME, true, new
    // FieldAndValuePair(IUserBOState.CODE, stateCode));
    // }
    //
    // public Integer findPkForBOStateUserDefinedName(String stateName) throws
    // PnESBusinessException, PnESFieldNotFoundException {
    // return findPkForBOUsingPVDbQuery(IUserBOState.QNAME, true, new
    // FieldAndValuePair(IUserBOState.STATE_PNNAME, stateName));
    // }

    /*
     * Count rows in resultSet
     */
    public Integer countRowsForPVdbQuery(String queryName, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return countRowsInResultSet(query, fieldAndValuePairs);
    }

    private Integer countRowsInResultSet(IPnESDatabaseQuery query, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return countRowsInResultSet(query);
    }

    private Integer countRowsInResultSet(IPnESDatabaseQuery query) throws PnESBusinessException {
        Integer rowCount = null;
        if (query != null) {
            rowCount = query.executeCount();
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return rowCount;
    }

    /*
     * Find Primary Key for BO
     */
    public Integer findPkForBOUsingPVDbQuery(String queryName, boolean isUniqueReferenceField, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findPkForBO(query, isUniqueReferenceField, fieldAndValuePairs);
    }

    private Integer findPkForBO(IPnESDatabaseQuery query, boolean isUniqueReferenceField, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return findPkForBO(query, isUniqueReferenceField);
    }

    public List<Integer> findPksForBOUsingPVDbQuery(String queryName) throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findPksForBO(query);
    }

    public List<Integer> findPksForBOUsingPVDbQuery(String queryName, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findPksForBO(query, fieldAndValuePairs);
    }

    private List<Integer> findPksForBO(IPnESDatabaseQuery query, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return findPksForBO(query);
    }

    private Integer findPkForBO(IPnESDatabaseQuery query, boolean isUniqueReferenceField) throws PnESBusinessException {
        Integer pkcode = null;
        if (query != null) {
            IPnESResultSet execute = null;
            int i = 0;
            do {
                query.setPage(i++);
                execute = query.execute();
                if (execute.next()) {
                    pkcode = execute.getPrimaryKey();
                    if (isUniqueReferenceField && execute.next()) {
                        // null, because not unique
                        pkcode = null;
                    }
                }
            } while (execute.hasNextPage());
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return pkcode;
    }

    private List<Integer> findPksForBO(IPnESDatabaseQuery query) throws PnESBusinessException {
        List<Integer> pkcode = new ArrayList<Integer>();
        if (query != null) {
            IPnESResultSet execute = null;
            int i = 0;
            do {
                query.setPage(i++);
                execute = query.execute();
                while (execute.next()) {
                    pkcode.add(execute.getPrimaryKey());
                }
            } while (execute.hasNextPage());
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return pkcode;
    }

    /*
     * Find a single value for BO
     */
    public <T> T findValueForBOUsingPVDbQuery(String queryName, boolean isUniqueReferenceField, ESAPIField field,
                    FieldAndValuePair... fieldAndValuePairs) throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findValueForBO(query, isUniqueReferenceField, field, fieldAndValuePairs);
    }

    private <T> T findValueForBO(IPnESDatabaseQuery query, boolean isUniqueReferenceField, ESAPIField field, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return findValueForBO(query, isUniqueReferenceField, field);
    }

    private <T> T findValueForBO(IPnESDatabaseQuery query, boolean isUniqueReferenceField, ESAPIField field)
                    throws PnESBusinessException, IllegalStateException, PnESFieldNotFoundException {
        T value = null;
        if (query != null) {
            IPnESResultSet execute = null;
            int i = 0;
            do {
                query.setPage(i++);
                execute = query.execute();
                if (execute.next()) {
                    value = getValueFromResultSet(execute, field);
                    if (isUniqueReferenceField && execute.next()) {
                        // System.out.println("findValueForBO: 'null, because not unique'");
                        value = null;
                    }
                }
            } while (execute.hasNextPage());
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return value;
    }

    /*
     * Find a set of a single distinct value for BO
     */
    public <T> Set<T> findDistinctValuesForBOUsingPVDbQuery(String queryName, ESAPIField field, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findDistinctValuesForBO(query, field, fieldAndValuePairs);
    }

    private <T> Set<T> findDistinctValuesForBO(IPnESDatabaseQuery query, ESAPIField field, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return findDistinctValuesForBO(query, field);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<T> findDistinctValuesForBO(IPnESDatabaseQuery query, ESAPIField field)
                    throws PnESBusinessException, IllegalStateException, PnESFieldNotFoundException {
        Set<T> values = new HashSet<T>();
        if (query != null) {
            IPnESResultSet execute = null;
            int i = 0;
            do {
                query.setPage(i++);
                execute = query.execute();
                while (execute.next()) {
                    values.add((T) getValueFromResultSet(execute, field));
                }
            } while (execute.hasNextPage());
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return values;
    }

    /*
     * Find a list of values for BO
     */
    public List<List<ESAPIField>> findValuesForBOUsingPVDbQuery(String queryName, List<ESAPIField> fields)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findValuesForBO(query, fields);
    }

    public List<List<ESAPIField>> findValuesForBOUsingPVDbQuery(String queryName, List<ESAPIField> fields, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return findValuesForBO(query, fields, fieldAndValuePairs);
    }

    private List<List<ESAPIField>> findValuesForBO(IPnESDatabaseQuery query, List<ESAPIField> fields, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            query = getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);
        }
        return findValuesForBO(query, fields);
    }

    private List<List<ESAPIField>> findValuesForBO(IPnESDatabaseQuery query, List<ESAPIField> fields)
                    throws PnESBusinessException, IllegalStateException, PnESFieldNotFoundException {
        List<List<ESAPIField>> values = new ArrayList<List<ESAPIField>>(fields.size());
        if (query != null) {
            IPnESResultSet execute = null;
            int i = 0;
            do {
                query.setPage(i++);
                execute = query.execute();
                while (execute.next()) {
                    values.add(getFieldAndValueFromResultSet(execute, fields));
                }
            } while (execute.hasNextPage());
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return values;
    }

    /*
     * Find BO
     */
    public IPnESBusinessObject findBOUsingPVDbQuery(String queryName, boolean isUniqueReferenceField, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException, PnESActionNotFoundException {
        IPnESDatabaseQuery query = null;
        try {
            query = this.getContext()
                        .getPVDatabaseQuery(queryName);
        } catch (PnESBusinessException e) {
            e.printStackTrace();
        }
        return this.findBO(query, isUniqueReferenceField, fieldAndValuePairs);
    }

    public IPnESBusinessObject findBOUsingPVDbQuery(String queryName, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException, PnESActionNotFoundException {
        return this.findBOUsingPVDbQuery(queryName, false, fieldAndValuePairs);
    }

    private IPnESBusinessObject findBO(IPnESDatabaseQuery query, boolean isUniqueReferenceField, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException, PnESActionNotFoundException {
        IPnESBusinessObject businessObject = null;
        if (query != null) {
            query = this.getQueryWithSearchExpressionsSet(query, fieldAndValuePairs);

            IPnESResultSet execute = query.execute();
            if (execute.next()) {
                String pnName = execute.getBOType()
                                       .getPnName();
                IPnESActionListManager actionListManager = this.getContext()
                                                               .getActionListManager(pnName);
                IPnESAction readAction = actionListManager.getReadAction(execute.getPrimaryKey());
                businessObject = readAction.execute();
                if (isUniqueReferenceField && execute.next()) {
                    // null, because not unique / bo = null;
                    throw new RuntimeException("The found BO is not unique for query: '" + query + "'");
                }
            }
        } else {
            throw new RuntimeException("IPnESDatabaseQuery not found");
        }
        return businessObject;
    }

    /*
     * Helpers
     */
    @SuppressWarnings("unchecked")
    private <T> T getValueFromResultSet(IPnESResultSet execute, ESAPIField field) throws IllegalStateException, PnESFieldNotFoundException {

        switch (field.getType()) {
            case BIGDECIMAL:
                return (T) execute.getBigDecimal(field.getPnName());
            case BOOLEAN:
                return (T) execute.getBoolean(field.getPnName());
            case DATE:
                return (T) execute.getDate(field.getPnName());
            case DATETIME:
                return (T) execute.getDateTime(field.getPnName());
            case INTEGER:
                return (T) execute.getInteger(field.getPnName());
            case PRIMARYKEY:
                return (T) Integer.valueOf(execute.getPrimaryKey());
            case REFERENCE:
                return (T) execute.getReference(field.getPnName());
            case STRING:
                return (T) execute.getString(field.getPnName());
            case STRINGREFERENCE:
                return (T) execute.getStringReference(field.getPnName());
            case TIME:
                return (T) execute.getTime(field.getPnName());
            default:
                throw new RuntimeException("Field type to get from ResultSet is invalid! (" + field.getType() + ")");
        }
    }

    private List<ESAPIField> getFieldAndValueFromResultSet(IPnESResultSet execute, List<ESAPIField> fields)
                    throws IllegalStateException, PnESFieldNotFoundException {

        List<ESAPIField> resultFields = null;

        if (MiscFunctions.noValue(fields)) {
            resultFields = new ArrayList<ESAPIField>(1);
            resultFields.add(new ESAPIField(FIELD_SYSCODE, FieldType.PRIMARYKEY, Integer.valueOf(execute.getPrimaryKey())));
        } else {
            final int fieldsSize = fields.size();
            resultFields = new ArrayList<ESAPIField>(fieldsSize);

            for (int i = 0; i < fieldsSize; i++) {
                final ESAPIField field = fields.get(i);
                ESAPIField resultField = new ESAPIField(field);

                switch (resultField.getType()) {
                    case BIGDECIMAL:
                        resultField.setBigDecimalValue(execute.getBigDecimal(field.getPnName()));
                        break;
                    case BOOLEAN:
                        resultField.setBooleanValue(execute.getBoolean(field.getPnName()));
                        break;
                    case DATE:
                        resultField.setDateValue(execute.getDate(field.getPnName()));
                        break;
                    case DATETIME:
                        resultField.setDateTimeValue(execute.getDateTime(field.getPnName()));
                        break;
                    case INTEGER:
                        resultField.setIntegerValue(execute.getInteger(field.getPnName()));
                        break;
                    case PRIMARYKEY:
                        resultField.setPrimaryKeyValue(Integer.valueOf(execute.getPrimaryKey()));
                        break;
                    case REFERENCE:
                        resultField.setReferenceValue(execute.getReference(field.getPnName()), execute.getBOType()
                                                                                                      .getPnName());
                        break;
                    case STRING:
                        resultField.setStringValue(execute.getString(field.getPnName()));
                        break;
                    case STRINGREFERENCE:
                        resultField.setStringReferenceValue(execute.getStringReference(field.getPnName()));
                    case TIME:
                        resultField.setTimeValue(execute.getTime(field.getPnName()));
                        break;
                }
                resultFields.add(resultField);
            }
        }
        return resultFields;
    }

    private IPnESDatabaseQuery getQueryWithSearchExpressionsSet(IPnESDatabaseQuery query, FieldAndValuePair... fieldAndValuePairs)
                    throws PnESBusinessException, PnESFieldNotFoundException {
        if (query != null) {
            for (FieldAndValuePair fieldAndValuePair : fieldAndValuePairs) {
                if (fieldAndValuePair != null && fieldAndValuePair.getField() != null) {
                    PnESOperator operator = fieldAndValuePair.getOperator() != null ? fieldAndValuePair.getOperator() : PnESOperator.EQUAL;
                    String field = fieldAndValuePair.getField();
                    Object value = fieldAndValuePair.getValue();

                    if (value == null) {
                        query.getSearchExpression(field, operator);
                    } else {
                        if (value instanceof IPnESBusinessObject) {
                            query.getReferenceSearchExpression(field, operator)
                                 .setValue(((IPnESBusinessObject) value).getPrimaryKey());
                        } else {
                            if (value instanceof String) {
                                query.getStringSearchExpression(field, operator)
                                     .setValue((String) value);
                            } else {
                                if (value instanceof Integer) {
                                    query.getIntegerSearchExpression(field, operator)
                                         .setValue((Integer) value);
                                } else {
                                    if (value instanceof Date) {
                                        query.getDateTimeSearchExpression(field, operator)
                                             .setValue((Date) value);
                                    } else {
                                        if (value instanceof BigDecimal) {
                                            query.getBigDecimalSearchExpression(field, operator)
                                                 .setValue((BigDecimal) value);
                                        } else {
                                            if (value instanceof Boolean) {
                                                query.getBooleanSearchExpression(field, operator)
                                                     .setValue((Boolean) value);
                                            } else {
                                                throw new RuntimeException("FieldValue of FieldAndValuePair is invalid! (" + value + ")");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return query;
    }

    public IPnESBusinessObject executeSaveNoWarningsOrConfirmations(IPnESBusinessObject obj, IPnESMessageHandler messHandler)
                    throws PnESActionNotFoundException, PnESBusinessException {
        IPnESBusinessObject returnBo = null;
        try {
            if (messHandler == null) {
                returnBo = obj.executeSave();
            } else {
                returnBo = obj.executeSave(messHandler);
            }
        } catch (PnESBusinessException e) {
            IPnESMessageHandler messageHandler = e.getMessageHandler();
            if (messageHandler.getNumberOfErrors() > 0) {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
            messageHandler.setIgnoreWarnings();
            int i = messageHandler.getNumberOfConfirmations();
            for (int x = 0; x < i; i++) {
                messageHandler.getConfirmation(x)
                              .setAnswer(true);
            }
            boolean canReply = messageHandler.canReply();
            if (canReply) {
                returnBo = executeSaveNoWarningsOrConfirmations(obj, messageHandler);
            } else {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
        }
        return returnBo;
    }

    public IPnESBusinessObject executeStateChangeNoWarningsOrConfirmations(IPnESStateChange stateChange, IPnESBusinessObject obj,
                    IPnESMessageHandler messHandler) throws PnESActionNotFoundException, PnESBusinessException {
        IPnESBusinessObject returnBo = null;
        try {
            if (messHandler == null) {
                returnBo = obj.executeStateChange(stateChange);
            } else {
                returnBo = obj.executeStateChange(stateChange, messHandler);
            }
        } catch (PnESBusinessException e) {
            IPnESMessageHandler messageHandler = e.getMessageHandler();
            if (messageHandler.getNumberOfErrors() > 0) {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
            messageHandler.setIgnoreWarnings();
            int i = messageHandler.getNumberOfConfirmations();
            for (int x = 0; x < i; i++) {
                messageHandler.getConfirmation(x)
                              .setAnswer(true);
            }
            boolean canReply = messageHandler.canReply();
            if (canReply) {
                returnBo = executeStateChangeNoWarningsOrConfirmations(stateChange, obj, messageHandler);
            } else {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
        }
        return returnBo;
    }

    public IPnESBusinessObject executeActionNoWarningsOrConfirmations(String action, IPnESBusinessObject obj, IPnESMessageHandler messHandler)
                    throws PnESActionNotFoundException, PnESBusinessException {
        IPnESBusinessObject returnBo = null;
        try {
            if (messHandler == null) {
                returnBo = obj.executeAction(action);
            } else {
                returnBo = obj.executeAction(action, messHandler);
            }
        } catch (PnESBusinessException e) {
            IPnESMessageHandler messageHandler = e.getMessageHandler();
            if (messageHandler.getNumberOfErrors() > 0) {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
            messageHandler.setIgnoreWarnings();
            int i = messageHandler.getNumberOfConfirmations();
            for (int x = 0; x < i; i++) {
                messageHandler.getConfirmation(x)
                              .setAnswer(true);
            }
            boolean canReply = messageHandler.canReply();
            if (canReply) {
                returnBo = executeActionNoWarningsOrConfirmations(action, obj, messageHandler);
            } else {
                throw new PnESBusinessException(e.getMessage(), messageHandler);
            }
        }
        return returnBo;
    }

    public void printAvailableActionsWithParams(String boTypeName) throws PnESBusinessException {
        IPnESActionListManager actionListManager = this.getContext()
                                                       .getActionListManager(boTypeName);
        for (int i = 0; i < actionListManager.getNumberOfActions(); i++) {
            IPnESAction actionAsString = actionListManager.getAction(i);
            System.out.println(boTypeName + ".action[" + i + "]: " + actionAsString.getBOType() + "." + actionAsString.getPnName());
            for (int j = 0; j < actionAsString.getNumberOfArguments(); j++) {
                System.out.println(
                    "    argument[" + j + "] PnName: " + actionAsString.getArgumentPnName(j) + ", Type: '" + actionAsString.getArgumentType(j) + "'");
            }
        }
    }

    public void printAvailableActionsWithParams(IPnESBusinessObject bo) throws PnESBusinessException {
        IPnESActionListManager actionListManager = bo.getActionListManager();
        for (int i = 0; i < actionListManager.getNumberOfActions(); i++) {
            IPnESAction actionAsString = actionListManager.getAction(i);
            System.out.println(bo.getBOType() + ".action[" + i + "]: " + actionAsString.getBOType() + "." + actionAsString.getPnName());
            for (int j = 0; j < actionAsString.getNumberOfArguments(); j++) {
                System.out.println(
                    "    argument[" + j + "] PnName: " + actionAsString.getArgumentPnName(j) + ", Type: '" + actionAsString.getArgumentType(j) + "'");
            }
        }
    }

}
