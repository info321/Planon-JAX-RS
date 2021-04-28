package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.planon.enterprise.service.api.IPnESBusinessObject;
import nl.planon.hades.userextension.uxinterface.IUXContext;
import nl.planon.json.server.container.services.common.IJsonJaxRsResource;
import nl.planon.util.pnlogging.PnLogger;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.dto.CommonResponseDTO;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.ICustomServiceTeamMember;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.TeamMemberService;


@SuppressWarnings({"unchecked", "rawtypes"})
@Path("")
public class WebServiceTeamMemberJAXRS implements ICustomServiceTeamMember, IJsonJaxRsResource {

    private static final PnLogger LOG = PnLogger.getLogger(WebServiceTeamMemberJAXRS.class);

    private final List<String> messageDetailsError = new ArrayList<String>();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{teamId}/addteammemeber")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public CommonResponseDTO addTeamMember(@PathParam("teamId")
    String teamId, ITeamMemberBody iTeamMemberBody) {
        CommonResponseDTO commonResponseDTO = new CommonResponseDTO();
        try {
            iTeamMemberBody.setTeamCode(teamId);
            IPnESBusinessObject newRolePlayerParticipantTeam = TeamMemberService.doAddTeamMember(iTeamMemberBody, messageDetailsError);
            if (newRolePlayerParticipantTeam != null) {
                LOG.info("save Role Player Participant Team BO");
                newRolePlayerParticipantTeam.executeSave();
                commonResponseDTO.setData(iTeamMemberBody);
                commonResponseDTO.setIsSuccess(true);

            } else {
                commonResponseDTO.setData("");
                commonResponseDTO.setIsSuccess(false);
            }
            commonResponseDTO.setErrorMessage(TeamMemberService.getSummary(messageDetailsError));

        } catch (Exception e) {
            CommonResponseDTO errorResp = new CommonResponseDTO();
            LOG.info(e.getMessage());
            errorResp.setErrorMessage(e.getMessage());
            return errorResp;
        }

        return commonResponseDTO;
    }

    public IUXContext getContext() {
        // IPnESContext context;
        // context = PnESContextCreator.getInstance().createContext();
        return getContext();
    }

}
