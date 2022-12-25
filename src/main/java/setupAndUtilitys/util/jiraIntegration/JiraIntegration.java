package setupAndUtilitys.util.jiraIntegration;

import apiPOJO.jiraIntegration.SerializeCreatingIssue;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;

import java.io.File;

import static io.restassured.RestAssured.given;

public class JiraIntegration {

    /**
     * Method below will do authorization with Bearer and token from personal JIRA account
     * You can use later that "session" variable in .filter(parameter = session) as a way to access JIRA
     * and do desire actions once logged in.
     */

    SessionFilter session = new SessionFilter();

    private final String filipToken = "yourToken"; //ToDo -> get your auth token in order to do anything in JIRA via API.

    public synchronized void jiraAuthentication() {
        RestAssured.baseURI = "URL to JIRA"; //ToDo -> address to JIRA you use.

        //Get session ID an store cookie in SessionFilter

        String authorization = RestAssured.given()
                .headers("Authorization", "Bearer " + filipToken,
                        "Content-Type", ContentType.JSON,
                        "Accept", ContentType.JSON)
                .when()
                .post("/auth/1/session")
                .asString();

    }

    String idValueOfCreatedIssue; //ToDo -> id of created issue is used to interact with that specific issue, for ex. add attachment.

    public synchronized void createJiraIssue(String summary, String description, String steps) {

        //Creating Issue/Defect
        SerializeCreatingIssue sci = new SerializeCreatingIssue();
        String createdIssueData = given().log().all().headers("Authorization", "Bearer " + filipToken,
                        "Content-Type", ContentType.JSON,
                        "Accept", ContentType.JSON).
                body(sci.createJson(summary, description, steps))
                .when().
                post("/rest/api/2/issue/")
                .then()
                .statusCode(201)
                .extract()
                .body()
                .asString();


        JsonPath jp = new JsonPath(createdIssueData);
        idValueOfCreatedIssue = jp.getString("id");
    }


    public synchronized void addAttachmentToTheIssue(String screenShotPath, String idValueOfCreatedIssue) {
        //Add attachment

        given().header("X-Atlassian-Token", "no-check")
                .filter(session)
                .pathParam("issueId", idValueOfCreatedIssue)
                .multiPart("file", new File(screenShotPath))
                .when()
                .post("/rest/api/2/issue/{issueId}/attachments")
                .then()
                .log()
                .all()
                .assertThat()
                .statusCode(200);

    }


}

