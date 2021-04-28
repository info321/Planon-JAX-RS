package us.planonsoftware.tms.dartmouth.webservice.jaxrs.services.impl;

public class ITeamMemberBody {
    private String code;
    private String description;
    private String teamCode;
    private String roleCode;
    private String personCode;
    private String startDate;
    private String endDate;
    private String comment;

    @Override
    public String toString() {
        return "ITeamMemberBody [code=" + code + ", description=" + description + ", roleCode=" + roleCode + ", personCode=" + personCode
                        + ", teamCode=" + teamCode + ", startDate=" + startDate + ", endDate=" + endDate + ", comment=" + comment + "]";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getPersonCode() {
        return personCode;
    }

    public void setPersonCode(String personCode) {
        this.personCode = personCode;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
