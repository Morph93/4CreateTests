package setupAndUtilitys.util;

public enum Resources {


    /**
     * This class contains resources that you will use through your whole project.
     * Instead of using property files, we switched to ENUM class as it's faster.
     *
     * @loginPage - Staging URL
     * @browser - set in which browser you wish to execute tests.
     * @fakeClientLaravelLogPath - the path to the fake client log from which we are extracting paymentID
     * @stagingAPIConnection - URL used for Billing API connection
     */

    homePage("URL to your webpage"),
    browser("chrome"),
    fakeClientLaravelLogPath("path to the logger file from which you wish to extract value."),
    stagingAPIConnection("url to connect staging"),
    stagingAPIConnectionAdminUsername("username"),
    stagingAPIConnectionPassword("password");


    private final String resource;

    Resources(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }

}
