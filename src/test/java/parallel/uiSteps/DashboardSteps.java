package parallel.uiSteps;

import setupAndUtilitys.driverFactory.DriverFactory;
import io.cucumber.java.en.When;
import pages.dashboard.DashboardPage;

import java.sql.Driver;

public class DashboardSteps {
    DashboardPage dashboardPage;

    @When("User logs in with {string} account")
    public void userLogsInWithAccount() {
        dashboardPage = new DashboardPage(DriverFactory.getDriver(), "firstCase");
    }

}
