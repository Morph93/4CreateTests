package apiHandlers.testDataBuilder.login;

import apiPOJO.exampleOnePojo.auth.Login;
import setupAndUtilitys.util.Resources;

public class LoginAuth {

    /**
     * This is example, you will probably need to adapt it for your tests.
     *
     * @param accountType based on account type we are setting values
     * @return return type is object of Login class which is returning the structure that's expected by endpoint.
     */

    public static Login loginAuthorization(String accountType) {
        Login login = new Login();

        switch (accountType) {
            case "admin":
                login.setEmail(Resources.stagingAPIConnectionAdminUsername.getResource());
                login.setPassword(Resources.stagingAPIConnectionPassword.getResource());
                break;
            case "client":
                login.setEmail("username/email for client");
                login.setPassword("password for client");
                break;
            default:
                break;
        }
        return login;
    }
}
