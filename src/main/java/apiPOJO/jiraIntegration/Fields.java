package apiPOJO.jiraIntegration;

public class Fields {

    Project project;
    private String summary;
    private String description;
    Issuetype issuetype;

    public Project getProject() {
        return project;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public Issuetype getIssuetype() {
        return issuetype;
    }

    public void setProject(Project projectObject) {
        this.project = projectObject;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIssuetype(Issuetype issuetypeObject) {
        this.issuetype = issuetypeObject;
    }
}
