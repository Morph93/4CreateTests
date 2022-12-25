package apiPOJO.jiraIntegration;

public enum ResourcesJIRA {

    createIssue("/rest/api/2/issue/"),
    addAttachmentToIssue("/rest/api/2/issue/{issueId}/attachments");

    private String resource;

    ResourcesJIRA(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
