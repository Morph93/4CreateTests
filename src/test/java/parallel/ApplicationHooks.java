package parallel;

import setupAndUtilitys.driverFactory.DriverFactory;
import setupAndUtilitys.util.Resources;
import setupAndUtilitys.util.jiraIntegration.JiraIntegration;
import setupAndUtilitys.util.testRailIntegration.APIClient;
import setupAndUtilitys.util.testRailIntegration.APIException;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static setupAndUtilitys.util.testRailIntegration.TestRailAccount.testRailApiClient;

public class ApplicationHooks implements ConcurrentEventListener {


    private WebDriver driver;

    public ApplicationHooks() {
    }


    /**
     * @runId -> This is the ID of the specific run inside the TestRail in which you want to add results of testing.
     * It should be changed every time you create new run in TestRail.
     * @FAIL_STATE -> It's from the documentation how to set the state of the test case to FAILED.
     * @SUCCESS_STATE -> It's from the documentation how to set the state of the test case to PASS.
     * @SUCCESS_COMMENT -> Which comment you wish to add to your test case when it's in PASS state.
     * @FAILED_COMMENT -> Which comment you wish to add to your test case when it's in FAILED state.
     * This is just first part of the comment, we are adding additional string based on the fail reason.
     * @caseId -> This is the test case ID from the TestRail, and it has to be unique. It's connected via Cucumber feature file
     * from where we read it.
     * @errMsgList -> This will catch multiple errors in case we are doing softAssert.
     */

    private static APIClient client = null;
    private static String runId = "1";
    private static final int FAIL_STATE = 5;
    private static final int SUCCESS_STATE = 1;
    private static final String SUCCESS_COMMENT = "This test passed with Selenium";
    private static final String FAILED_COMMENT = "This automated test failed due to following reasons: ";
    public static String caseId;
    public static String scenarioName;

    @Rule
    public TestName testName = new TestName();

    /**
     * @initialScenarioName -> get the name of scenario and replace all spaces
     * @recordingName replace all special characters from initialScenarioName
     * //ToDo -> Fix recording name so it's clearer what recording is for which scenario
     * //ToDo -> Uncomment this to enable recorder
     */
//    @Before(order = 0, value = "@UI")
//    public void getProperty(Scenario scenario) throws Exception {
//        String initialScenarioName = scenario.getName().replaceAll(" ", "");
//        String recordingName = initialScenarioName.replaceAll("[^a-zA-Z0-9]", "");
//        //MyScreenRecorder.startRecording(recordingName);
//
//    }

    /**
     * @browserName -> We're getting it from Resources Enum class.
     * @url -> We're reading from configuration property file and setting desired URL address.
     */
    @Before(order = 0, value = "@UI")
    public void launchBrowser() {
        DriverFactory driverFactory = new DriverFactory();
        String browserName = Resources.browser.getResource();
        // String url = prop.getProperty("url");
        driver = driverFactory.init_driver(browserName);
        // driver.get(url);
    }


    /**
     * The order 0 will call quit on driver and close the browser.
     */
    @After(order = 0, value = "@UI")
    public void quitBrowser() {
        driver.quit();
    }

    /**
     * @param scenario is used to extract scenarioName.
     *                 scenarioName is used to set the name of the screenshot.
     *                 <p>
     *                 We are also checking if scenario is passed and if the condition is true we remove recording.
     */
    @After(order = 1, value = "@UI")
    public void tearDown(Scenario scenario) throws Exception {

        //if (scenario.isFailed() && scenario.) { ToDo -> Uncomment if you want scs only on failed
        // take screenshot:
        scenarioName = scenario.getName().replaceAll(" ", "_");
        byte[] sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(sourcePath, "image/png", scenarioName);
        //}

        //ToDo -> Uncomment if you enable screen recorder
//        if (!scenario.isFailed()) {
//            MyScreenRecorder.removeRecording();
//        }
        // MyScreenRecorder.stopRecording();
    }


    /**
     * @param testCaseFinished this is a event listener which will capture when cucumber test case is finished.
     * @finalResult -> This is extracted message that is written in assertion so that issue in JIRA has correct description.
     * @data -> This is HashMap object created for TestRail integration
     */
    public void onTestFail(TestCaseFinished testCaseFinished) {

        /*
          This is how you extract steps from cucumber feature file.
         */
        TestCase testCase = testCaseFinished.getTestCase();
        List<TestStep> testStep = testCase.getTestSteps();
        List<String> listOfTestSteps = new ArrayList<>();
        for (TestStep tc : testStep) {
            if (tc instanceof PickleStepTestStep) {
                PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) tc;
                String text = pickleStepTestStep.getStep().getText();
                listOfTestSteps.add(text);
            }
        }

        List<String> listOfStepsPreparedForAPI = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (String listOfTestStep : listOfTestSteps) {
            listOfStepsPreparedForAPI = Collections.singletonList(sb.append("# Step ").append(": ").append(listOfTestStep).append("\n").toString());
        }

        String listOfFinalSteps = "Steps to reproduce: \n" + listOfStepsPreparedForAPI.get(0);

        /*
          End of step extraction.
         */

        String result = testCaseFinished.getResult().toString();
        String frontCut = result.substring(result.indexOf("failed") + 8);
        String finalResult = "* " + frontCut.substring(0, frontCut.length() - 3);
        Map<String, Serializable> data = new HashMap<>();


        if (testCaseFinished.getResult().toString().contains("status=FAILED")) {
            JiraIntegration jiraIntegration = new JiraIntegration();
            try {
                jiraIntegration.jiraAuthentication();
                jiraIntegration.createJiraIssue(scenarioName, finalResult, listOfFinalSteps);
            } catch (Exception e) {
                System.err.println("JIRA IS NOT CONNECTED PROPERLY.");
            }

            data.put("status_id", FAIL_STATE);
            data.put("comment", FAILED_COMMENT + finalResult);

        } else {
            data.put("status_id", SUCCESS_STATE);
            data.put("comment", SUCCESS_COMMENT);
        }

        if (caseId != null) {
            if (!caseId.equals("")) {
                try {
                    if (System.getenv("runIdTestRail") != null && System.getenv("runTestRailId").equals("")) {
                        runId = System.getenv("runIdTestRail");
                    }

                    client = testRailApiClient();
                    client.sendPost("add_result_for_case/" + runId + "/" + caseId, data);

                } catch (IOException | APIException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("ID does not exist");
        }

    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        // eventPublisher.registerHandlerFor(TestCaseFinished.class, this::onTestFail);
    }
}
