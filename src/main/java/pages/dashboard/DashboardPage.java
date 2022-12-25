package pages.dashboard;

import setupAndUtilitys.util.FunctionsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;


import java.util.HashMap;

public class DashboardPage extends FunctionsPage {

    public DashboardPage(WebDriver driver, String state) {
        super(driver);
        initMap(state);
    }

    SoftAssert softAssert = new SoftAssert();
    HashMap<String, By> mapOfPageElements = new HashMap<>();

    private void initMap(String state) {
        switch (state) {
            case "firstCase":
                mapOfPageElements.put("firstElement", By.xpath("locator"));
                mapOfPageElements.put("secondElement", By.xpath("locator"));
                mapOfPageElements.put("thirdElement", By.xpath("locator"));
                break;
            case "secondCase":
                mapOfPageElements.put("firstElement", By.xpath("locator"));
                mapOfPageElements.put("secondElement", By.xpath("locator"));
                break;
            default:
                break;
        }
    }

    public void checkAllElements() {
        checkIfPageContainsAllElements(mapOfPageElements, softAssert);
    }

}
