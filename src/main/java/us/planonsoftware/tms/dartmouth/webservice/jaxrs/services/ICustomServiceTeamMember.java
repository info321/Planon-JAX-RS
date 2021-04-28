package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services;

import nl.planon.json.server.container.services.interfaces.IPnWebService;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.dto.CommonResponseDTO;
import us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.impl.ITeamMemberBody;


/**
 * 
 * @author
 *
 */
@SuppressWarnings("rawtypes")
public interface ICustomServiceTeamMember extends IPnWebService {

    CommonResponseDTO addTeamMember(String teamId, ITeamMemberBody iTeamMemberBody);

}
