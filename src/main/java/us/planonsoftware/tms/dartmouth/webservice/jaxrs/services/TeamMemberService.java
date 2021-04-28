package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.planonsoftware.tms.lib.client.BusinessObject;
import com.planonsoftware.tms.lib.client.exception.ServiceApiUtilsException;

import nl.planon.enterprise.service.api.IPnESAction;
import nl.planon.enterprise.service.api.IPnESActionListManager;
import nl.planon.enterprise.service.api.IPnESBusinessObject;
import nl.planon.enterprise.service.api.PnESActionNotFoundException;
import nl.planon.enterprise.service.api.PnESBusinessException;
import nl.planon.enterprise.service.api.PnESFieldNotFoundException;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors.ESAPIConnector;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.connectors.FieldAndValuePair;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.impl.ITeamMemberBody;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils.Constants;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.utils.TMSLogger;


public final class TeamMemberService {

    private static final TMSLogger LOG = new TMSLogger(TeamMemberService.class);

    private TeamMemberService() {
        LOG.setLogLevel(TMSLogger.Level.DEBUG);
    }

    public static String getSummary(List<String> messageDetails) {
        String summary = "";
        boolean first = true;
        for (int i = 0; i < messageDetails.size(); i++) {
            if (first) {
                summary = messageDetails.get(i);
                first = false;
            } else {
                summary = summary + "; " + messageDetails.get(i);
            }
        }
        return summary;
    }

    public static IPnESBusinessObject doAddTeamMember(ITeamMemberBody iTeamMemberBody, List<String> messageDetailsError)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException, ServiceApiUtilsException, ParseException {
        // Get Team BO from Code
        if ("".equals(iTeamMemberBody.getTeamCode())) {
            messageDetailsError.add("The field Team must contain a valid value. (PN_T00397)");
            return null;
        }
        IPnESBusinessObject teamBO = BusinessObject.read(Constants.USR_MAINTENANCE_TEAM, Constants.CODE, iTeamMemberBody.getTeamCode());
        if (teamBO == null) {
            messageDetailsError.add("The field Team must contain a valid value. (PN_T00397)");
            return null;
        }
        // Create new Role Player Person BO and linked it to team BO
        IPnESBusinessObject newRolePlayerPersonBO = createCommunicationLogBO(teamBO, Constants.USR_MAINTENANCE_TEAM, Constants.BO_ROLE_PLAYER_PERSON);

        // add Code to Team Member Body
        iTeamMemberBody.setCode(newRolePlayerPersonBO.getStringField(Constants.CODE)
                                                     .getValue());

        // Get Person BO from Code
        if ("".equals(iTeamMemberBody.getPersonCode())) {
            messageDetailsError.add("The field Person must contain a valid value. (PN_T00397)");
            return null;
        }
        IPnESBusinessObject personBO = BusinessObject.read(Constants.BO_PERSON, Constants.CODE, iTeamMemberBody.getPersonCode());

        if (personBO == null) {
            messageDetailsError.add("The field Person must contain a valid value. (PN_T00397)");
            return null;
        }
        // Link Person BO to Role Player Person BO
        newRolePlayerPersonBO.getReferenceField(Constants.PERSON_REF)
                             .setValue(personBO.getPrimaryKey());

        // Get Role BO from Code
        if ("".equals(iTeamMemberBody.getRoleCode())) {
            messageDetailsError.add("The field Role must contain a valid value. (PN_T00397)");
            return null;
        }
        IPnESBusinessObject rolBO = BusinessObject.read(Constants.BO_ROLE, Constants.CODE, iTeamMemberBody.getRoleCode());
        if (rolBO == null) {
            messageDetailsError.add("The field Role must contain a valid value. (PN_T00397)");
            return null;
        }
        // Link Role BO to Role Player Person BO
        newRolePlayerPersonBO.getReferenceField(Constants.ROLE_REF)
                             .setValue(rolBO.getPrimaryKey());

        if ("".equals(iTeamMemberBody.getStartDate())) {
            messageDetailsError.add("The field Start date must contain a value. (PN_T00397)");
            return null;
        }
        newRolePlayerPersonBO.getDateField(Constants.START_DATE)
                             .setValue(new SimpleDateFormat("yyyy-MM-dd").parse(iTeamMemberBody.getStartDate()));
        if (!"".equals(iTeamMemberBody.getEndDate())) {
            newRolePlayerPersonBO.getDateField(Constants.END_DATE)
                                 .setValue(new SimpleDateFormat("yyyy-MM-dd").parse(iTeamMemberBody.getEndDate()));
        }
        newRolePlayerPersonBO.getStringField(Constants.DESCRIPTION)
                             .setValue(iTeamMemberBody.getDescription());

        newRolePlayerPersonBO.getStringField(Constants.COMMENT)
                             .setValue(iTeamMemberBody.getComment());

        return newRolePlayerPersonBO;

    }

    public static IPnESBusinessObject createCommunicationLogBO(IPnESBusinessObject teamBO, String teamBOPnName, String rolePlayerPersonPnname)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        // get root bo type of the selected BO & its syscode
        Integer pkTeamBO = teamBO.getPrimaryKey();
        return createBO(rolePlayerPersonPnname, new FieldAndValuePair("Syscode", pkTeamBO), new FieldAndValuePair("BOType", teamBOPnName));

    }

    public static IPnESBusinessObject createBO(String boTypeName, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        return createBO(boTypeName, "BomAdd", fieldAndValuePairArr);
    }

    public static IPnESBusinessObject createBO(String boTypeName, String action, FieldAndValuePair... fieldAndValuePairArr)
                    throws PnESBusinessException, PnESActionNotFoundException, PnESFieldNotFoundException {
        IPnESBusinessObject bo = null;
        IPnESActionListManager actionListManager = ESAPIConnector.getContext()
                                                                 .getActionListManager(boTypeName);
        IPnESAction bomAction = actionListManager.getAction(action);
        if (bomAction == null) {
            throw new RuntimeException("Unable to " + action + " Element: " + boTypeName);
        }
        System.out.println("action found: " + bomAction.getBOType() + "." + bomAction.getPnName() + "->" + bomAction.getNumberOfArguments());
        for (FieldAndValuePair fieldAndValuePair : fieldAndValuePairArr) {
            if (fieldAndValuePair != null && fieldAndValuePair.getField() != null && fieldAndValuePair.getValue() != null) {
                if (fieldAndValuePair.getValue() instanceof Integer) {
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

        bo = bomAction.execute();
        return bo;
    }

}
