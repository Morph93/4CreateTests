package setupAndUtilitys.util.testRailIntegration;


public class TestRailAccount {

    public static APIClient testRailApiClient() {

        String baseUrl = ""; //URL to testrail (ex. https://fgtesttr.testrail.io/)
        String usernameTestRail = ""; //Username for account that has write permission.
        String passwordTestRail = "";//Password for the account.

        APIClient client = new APIClient(baseUrl);
        client.setUser(usernameTestRail);
        client.setPassword(passwordTestRail);

        return client;
    }

}
