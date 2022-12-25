package setupAndUtilitys.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")

public abstract class FunctionsPage {

    protected WebDriverWait wait;
    protected static WebDriver driver;
    private final Clock clock;
    private final Duration timeout = Duration.ofSeconds(10);
    private final Duration refreshPeriod = Duration.ofMillis(500);

    Logger logger = LoggerFactory.getLogger(FunctionsPage.class);

    public FunctionsPage(WebDriver driver) {
        FunctionsPage.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        this.clock = Clock.systemDefaultZone();
    }

    protected void setTimeoutTo1() {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(1));
    }

    protected void setTimeoutTo5() {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    protected void setTimeoutTo10() {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement waitIsPresent(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected List<WebElement> waitListIsPresent(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    protected void waitIsDisplayed(By by) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void waitIsClickable(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    /**
     * This function is used when you want to interact with any element via Selenium
     * It will check if the element is present on the page
     * ,then it will scroll element into view, check if it's visible, then if it's clickable.
     *
     * @param locator     of the desired element.
     * @param elementName for easier debugging and report.
     * @return type is WebElement.
     */
    private WebElement elementInteractionPrecondition(By locator, String elementName) {
        WebElement element = null;
        try {
            element = waitIsPresent(locator);
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoViewIfNeeded()", element);
                Thread.sleep(500);
                try {
                    waitIsDisplayed(locator);
                    try {
                        waitIsClickable(locator);
                    } catch (Exception e) {
                        Assert.fail("Element " + elementName + " with locator: " + locator + "is not clickable");
                    }
                } catch (Exception e) {
                    Assert.fail("Element " + elementName + " with locator: " + locator + "is not visible on the page.");
                }
            } catch (Exception e) {
                Assert.fail("Unable to scroll element: " + elementName + " into view due to: " + e);
            }
        } catch (Exception e) {
            Assert.fail("Element " + elementName + " with locator: " + locator + " is not present on the page.");
        }
        return element;
    }

    /**
     * This function is used to click desired element
     *
     * @param by          is locator of the element.
     * @param elementName is used for easier debugging.
     */
    protected void clickElementXpath(By by, String elementName) {

        WebElement element;
        try {
            element = elementInteractionPrecondition(by, elementName);
            try {
                element.click();
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                element = elementInteractionPrecondition(by, elementName);
                element.click();
            }
        } catch (NoSuchElementException | TimeoutException e) {
            Assert.fail("Couldn't find element: " + elementName + " with locator: " + by);
        } catch (ElementClickInterceptedException | StaleElementReferenceException a) {
            Assert.fail("Couldn't click element: " + elementName + " with locator: " + by);
        }
    }


    /**
     * This function is used when checking multiple elements on the page. We don't want it to fail on the first one,
     * instead it will go through all elements that are given, check them all, and give us results at the end.
     *
     * @param locator     locator of element that you wish to check.
     * @param elementName name of the element that you wish to check for easier debugging.
     * @param softAssert  this is used in case we have multiple elements. After checking all assert will occur.
     */
    public void checkElementsPresenceAndVisibility(By locator, String elementName, SoftAssert softAssert) {

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoViewIfNeeded()", waitIsPresent(locator));
            Thread.sleep(500);
            try {
                waitIsDisplayed(locator);
            } catch (Exception e) {
                softAssert.fail("Element " + elementName + " with locator: " + locator + "is not visible on the page.");
            }
        } catch (Exception e) {
            softAssert.fail("Element " + elementName + " with locator: " + locator + "is not present on the page.");
        }
    }

    /**
     * This function is used when checking element on the page. If the given element is not present or visible,
     * the test will fail here.
     *
     * @param locator     locator of element that you wish to check.
     * @param elementName name of the element that you wish to check for easier debugging.
     */
    public void checkElementPresenceAndVisibility(By locator, String elementName) {

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoViewIfNeeded()", waitIsPresent(locator));
            Thread.sleep(500);
            try {
                waitIsDisplayed(locator);
            } catch (Exception e) {
                Assert.fail("Element " + elementName + " with locator: " + locator + "is not visible on the page.");
            }
        } catch (Exception e) {
            Assert.fail("Element " + elementName + " with locator: " + locator + "is not present on the page.");
        }
    }

    /**
     * This function is used for checking if all predefined elements are PRESENT and DISPLAYED on the given page.
     *
     * @param mapOfPageElements -> Providing HashMap of all elements that should be present on the given page in String,By format.
     * @param softAssert        -> Providing SoftAssert object so that all elements can be checked before throwing any exception.
     */
    public void checkIfPageContainsAllElements(HashMap<String, By> mapOfPageElements, SoftAssert softAssert) {
        if (mapOfPageElements.size() != 0) {
            checkIfEachElementIsPresentAndDisplayed(mapOfPageElements, softAssert);
            softAssert.assertAll();
        }
    }

    private void checkIfEachElementIsPresentAndDisplayed(HashMap<String, By> mapOfPageElements, SoftAssert softAssert) {
        for (Map.Entry<String, By> item : mapOfPageElements.entrySet()) {
            checkElementsPresenceAndVisibility(item.getValue(), item.getKey(), softAssert);
        }
    }

    /**
     * This function is used to check all elements inside an iFrame.
     * If the element is expected inside iFrame, the name of element must contain name of that iFrame.
     *
     * @param mapOfPageElements should contain all elements, elements that are inside iFrame must contain name of iFrame inside the hashMap.
     * @param mapOfPageIframes  should contain all iFrames on the page.
     * @param softAssert        will assert all failed checks at the end.
     */
    public void checkIfPageContainsAllElementsUsingIframe(HashMap<String, By> mapOfPageElements, HashMap<String, String> mapOfPageIframes, SoftAssert softAssert) {
        if (mapOfPageElements.size() != 0) {

            String elementName;
            for (Map.Entry<String, By> element : mapOfPageElements.entrySet()) {
                elementName = element.getKey();
                driver.switchTo().defaultContent();

                for (Map.Entry<String, String> iframe : mapOfPageIframes.entrySet()) {
                    if (elementName.contains(iframe.getKey())) {
                        driver.switchTo().frame(iframe.getValue());
                    }
                }
                checkElementsPresenceAndVisibility(element.getValue(), element.getKey(), softAssert);
            }
            softAssert.assertAll();
        }
    }


    /**
     * This function will check if the elements is gone (not present) on the page anymore.
     * The check is done every 0.5 seconds, and if the element is still present it will fail.
     *
     * @param by          is locator of the element.
     * @param elementName is used for easier debugging.
     */
    protected void waitIsGone(By by, String elementName) {
        Instant end = clock.instant().plus(timeout);
        setTimeoutTo1();
        for (Instant i = clock.instant(); i.isBefore(end); ) {
            i = i.plus(refreshPeriod);
            try {
                Thread.sleep(500);
                waitIsPresent(by);
            } catch (Exception e) {
                setTimeoutTo10();
                break;
            }
            if (i.getEpochSecond() >= end.getEpochSecond()) {
                Assert.fail("Element " + elementName + " with locator: " + by + " is still visible.");
            }
        }
        setTimeoutTo10();
    }

    /**
     * This function is used to switch focus from default view to iFrame view in order
     * to interact with elements inside it.
     *
     * @param iFrame is the name of the iFrame you wish to switch to.z
     */
    protected void switchToIframe(String iFrame) {
        try {
            driver.switchTo().frame(iFrame);
        } catch (NoSuchFrameException e) {
            Assert.fail("The iFrame " + iFrame + "doesn't exist. Check structure.");
        }
    }

    /**
     * This function is used to switch to desired iFrame and then click desired element inside it.
     *
     * @param iFrame      provide the iframe inside which is the element you wish to click.
     * @param by          locator of the element you wish to click.
     * @param elementName of the element you wish to click.
     */
    protected void switchToIframeAndClickElement(String iFrame, By by, String elementName) {
        switchToIframe(iFrame);
        clickElementXpath(by, elementName);
        driver.switchTo().defaultContent();
    }

    /**
     * This function is used in order to input text into webElement that are inside an iFrame.
     * It switches to desired iFrame then inputs desired text into the given element.
     *
     * @param iFrame      iFrame name you want to switch to
     * @param by          element that you want to input text into
     * @param text        text that you want to input
     * @param elementName element name, so you can easily debug
     */
    protected void switchToIframeAndInputText(String iFrame, By by, String text, String elementName) {
        switchToIframe(iFrame);

        try {
            WebElement element = waitIsPresent(by);
            waitIsDisplayed(by);
            element.sendKeys(text);
        } catch (NoSuchElementException | TimeoutException | ElementNotInteractableException e) {
            Assert.fail("Couldn't find " + elementName + " with locator: " + by + e);
        } catch (StaleElementReferenceException e) {
            WebElement element = waitIsPresent(by);
            element.clear();
            element.sendKeys(text);
        }

        driver.switchTo().defaultContent();
    }


    /**
     * This function will click on the element using JS injection instead of selenium click function.
     *
     * @param by          -> element locator.
     * @param elementName -> name of the element you want to click.
     */
    protected void clickButtonElementXpathJS(By by, String elementName) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement element;

        try {
            element = waitIsPresent(by);
            try {
                waitIsClickable(by);
                js.executeScript("arguments[0].click();", element);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                element = elementInteractionPrecondition(by, elementName);
                try {
                    js.executeScript("arguments[0].click();", element);
                } catch (Exception ex) {
                    Assert.fail("Unable to click element: " + elementName + ", due to: " + ex);
                }
            }
        } catch (NoSuchElementException | TimeoutException e) {
            logger.atError().log("Couldn't find " + elementName + " with locator: " + by);
            Assert.fail("Couldn't find " + elementName + " with locator: " + by);
        }
    }

    /**
     * This function is used to click on radioButton specifically.
     * Due to opacity=0 in radio buttons they won't be clickable by default
     * ,so we are changing the opacity to 1 before checking if the element is clickable.
     *
     * @param by          - locator of the element.
     * @param elementName - name of the element you wish to interact to.
     */
    protected void clickRadioButtonElementXpath(By by, String elementName) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        WebElement element;
        try {
            element = waitIsPresent(by);
            js.executeScript("arguments[0].style.opacity=1", element);

            try {
                waitIsClickable(by);
                js.executeScript("arguments[0].click();", element);
            } catch (StaleElementReferenceException | ElementNotInteractableException e) {
                element = elementInteractionPrecondition(by, elementName);
                try {
                    js.executeScript("arguments[0].click();", element);
                } catch (Exception ex) {
                    Assert.fail("Couldn't click " + elementName + " with locator: " + by + " due to: " + ex);
                }
            }
        } catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
            Assert.fail("Couldn't find " + elementName + " with locator: " + by);
        }

    }

    /**
     * This function will send text to a dropDown element of your choosing and trigger search (simulate Enter key).
     * If the staleElementException is caught it will clean the input field and retry to input given text.
     *
     * @param by          - locator of the dropDown menu
     * @param text        - text you wish to send
     * @param elementName - name of the element you are interacting with
     */
    protected void inputTextIntoDropdown(By by, String text, String elementName) {
        try {
            WebElement element = waitIsPresent(by);
            element.sendKeys(text + "\n");
        } catch (NoSuchElementException | TimeoutException | ElementNotInteractableException e) {
            Assert.fail("Couldn't find " + elementName + " with locator: " + by + " due to: " + e);
        } catch (StaleElementReferenceException e) {
            WebElement element = elementInteractionPrecondition(by, elementName);
            try {
                element.sendKeys(text + "\n");
            } catch (Exception ex) {
                Assert.fail("Couldn't send text to " + elementName + " with locator: " + by + " due to: " + ex);
            }
        }
    }

    /**
     * This function fills input field with given text.
     * If the staleElementException is caught it will clean the input field and retry to input given text.
     *
     * @param by          - locator of the dropDown menu
     * @param text        - text you wish to send
     * @param elementName - name of the element you are interacting with
     */
    protected void inputText(By by, String text, String elementName) {
        try {
            WebElement element = elementInteractionPrecondition(by, elementName);
            element.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            WebElement element = elementInteractionPrecondition(by, elementName);
            try {
                element.clear();
                element.sendKeys(text);
            } catch (Exception ex) {
                Assert.fail("Unable to input text into: " + elementName + " element due to: " + ex);
            }
        }
    }

    /**
     * This function fills input field with current date.
     * If the staleElementException is caught it will clean the input field and retry to input given date.
     *
     * @param by          - locator of the dropDown menu
     * @param elementName - name of the element you are interacting with
     */
    protected void inputCurrentDate(By by, String elementName) {
        Format f = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = f.format(new Date());

        try {
            WebElement element = waitIsPresent(by);
            element.sendKeys(currentDate);
        } catch (NoSuchElementException | TimeoutException e) {
            Assert.fail("Couldn't find " + elementName + " with locator: " + by);
        } catch (StaleElementReferenceException e) {
            WebElement element = elementInteractionPrecondition(by, elementName);
            try {
                element.clear();
                element.sendKeys(currentDate);
            } catch (Exception ex) {
                Assert.fail("Unable to input current date into: " + elementName + " element due to: " + ex);
            }

        }
    }

    /**
     * This function fills input field with date that's current + number of months in advance.
     * If the staleElementException is caught it will clean the input field and retry to input given date.
     *
     * @param by                        - locator of the dropDown menu
     * @param elementName               - name of the element you are interacting with
     * @param numberOfMonthsInTheFuture - number of months you want to add to the current date
     */
    protected void inputFutureDate(By by, String elementName, int numberOfMonthsInTheFuture) {
        String DATE_FORMAT = "MM/dd/yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        // Get current date
        Date currentDate = new Date();

        // convert date to localdatetime
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // plus one
        localDateTime = localDateTime.plusMonths(numberOfMonthsInTheFuture);
        Date futureDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());


        try {
            WebElement element = waitIsPresent(by);
            element.sendKeys(dateFormat.format(futureDate));
        } catch (NoSuchElementException | TimeoutException e) {
            Assert.fail("Couldn't find " + elementName + " with locator: " + by);
        } catch (StaleElementReferenceException e) {
            WebElement element = elementInteractionPrecondition(by, elementName);
            try {
                element.clear();
                element.sendKeys(dateFormat.format(futureDate));
            } catch (Exception ex) {
                Assert.fail("Couldn't input date into " + elementName + " element with locator: " + by + " due to: " + ex);
            }
        }
    }

    protected void isTextEqual(By by, String expectedText, String elementName) {
        WebElement element;
        element = waitIsPresent(by);
        String actualText = element.getText();
        if (!actualText.equals(expectedText)) {
            Assert.fail("Element:  " + elementName + " actually have text [ " + actualText + " ] but it should have text [ " + expectedText + " ]");
        }
    }

    protected static class LocatorAndText {
        public LocatorAndText(By locator, String text) {
            this.locator = locator;
            this.text = text;
        }

        By locator;
        String text;

        public By getLocator() {
            return locator;
        }

        public void setLocator(By locator) {
            this.locator = locator;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    protected void checkMultipleTexts(Map<String, LocatorAndText> mapOfElements, SoftAssert softAssert) {
        for (Map.Entry<String, LocatorAndText> item : mapOfElements.entrySet()) {
            LocatorAndText locatorAndText = item.getValue();
            WebElement element;

            try {
                element = waitIsPresent(locatorAndText.getLocator());
                waitIsDisplayed(locatorAndText.getLocator());
                waitIsClickable(locatorAndText.getLocator());

                if (item.getKey().equals("urlInputField")) { //Za URL polje tekst mora da se izvuce preko atributa.
                    String actualText = waitIsPresent(locatorAndText.getLocator()).getAttribute("value");
                    if (!actualText.equals(locatorAndText.getText())) {
                        softAssert.fail("Element: urlInputField actually have text [ " + actualText + " ] but it should have text [ " + locatorAndText.getText() + " ]");
                    }
                } else if (!element.getText().equals(locatorAndText.getText())) {
                    softAssert.fail(item.getKey() + " element text doesn't match the expected text. Text is: " + element.getText() + ", but it should be: " + locatorAndText.getText());
                }
            } catch (NoSuchElementException | TimeoutException e) {
                softAssert.fail(item.getKey() + " element is not present.");
            } catch (StaleElementReferenceException s) {
                element = waitIsPresent(locatorAndText.getLocator());
                waitIsDisplayed(locatorAndText.getLocator());
                waitIsClickable(locatorAndText.getLocator());
                if (!element.getText().equals(locatorAndText.getText())) {
                    softAssert.fail(item.getKey() + " element text doesn't match the expected text. Text is: " + element.getText() + ", but it should be: " + locatorAndText.getText());
                }
            }

        }
        softAssert.assertAll();
    }

    protected void checkMultipleTextsForInputFields(Map<String, LocatorAndText> mapOfElements, SoftAssert softAssert) {
        for (Map.Entry<String, LocatorAndText> item : mapOfElements.entrySet()) {
            LocatorAndText locatorAndText = item.getValue();
            WebElement element;

            try {
                waitIsPresent(locatorAndText.getLocator());
                waitIsDisplayed(locatorAndText.getLocator());
                String actualText = waitIsPresent(locatorAndText.getLocator()).getAttribute("value");
                if (!actualText.equals(locatorAndText.getText())) {
                    softAssert.fail("Element: urlInputField actually have text [ " + actualText + " ] but it should have text [ " + locatorAndText.getText() + " ]");
                }
            } catch (NoSuchElementException | TimeoutException | IllegalArgumentException e) {
                softAssert.fail(item.getKey() + " element is not present.");
            }

        }
        softAssert.assertAll();
    }

    protected void enterDataIntoMultipleInputFields(Map<String, LocatorAndText> mapOfElements, SoftAssert softAssert) {
        for (Map.Entry<String, LocatorAndText> item : mapOfElements.entrySet()) {
            LocatorAndText locatorAndText = item.getValue();
            WebElement element;
            try {
                element = elementInteractionPrecondition(locatorAndText.getLocator(), item.getKey());
                element.sendKeys(locatorAndText.getText());
            } catch (NoSuchElementException | TimeoutException | ElementNotInteractableException e) {
                softAssert.fail(item.getKey() + " element is not present.");
            } catch (StaleElementReferenceException e) {
                element = elementInteractionPrecondition(locatorAndText.getLocator(), item.getKey());
                element.clear();
                element.sendKeys(locatorAndText.getText());
            }
        }
        softAssert.assertAll();
    }


    protected void enterDataInMultipleDropdowns(Map<String, LocatorAndText> mapOfElements, SoftAssert softAssert) {
        for (Map.Entry<String, LocatorAndText> item : mapOfElements.entrySet()) {
            LocatorAndText locatorAndText = item.getValue();
            WebElement element;
            try {
                element = waitIsPresent(locatorAndText.getLocator());
                element.sendKeys(locatorAndText.getText() + "\n");
            } catch (NoSuchElementException | TimeoutException | ElementNotInteractableException e) {
                softAssert.fail("Couldn't find " + item.getKey() + " with locator: " + locatorAndText.getLocator());
            } catch (StaleElementReferenceException e) {
                element = waitIsPresent(locatorAndText.getLocator());
                element.clear();
                element.sendKeys(locatorAndText.getText() + "\n");
            }
        }
        softAssert.assertAll();
    }

    public static String generateRandomString(int charNumber) {
        return RandomStringUtils.randomAlphabetic(charNumber);
    }

    public static String generateRandomAlphanumeric(int charNumber) {
        return RandomStringUtils.randomAlphanumeric(charNumber);
    }

    public static String generateRandomNumeric(int numNumber) {
        return RandomStringUtils.randomNumeric(numNumber);
    }

    public static String parseJsonFromLocal(String filePath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(
                new FileReader(filePath));
        return data.toJSONString();
    }

    /**
     * This will open given HTML file from given path in your current browser session.
     *
     * @param pathToLocalHTML path to the HTML -> give relative path always
     */
    protected void loadLocalHTMLIntoBrowserThroughURL(String pathToLocalHTML) throws MalformedURLException {
        driver.get(String.valueOf(new File(pathToLocalHTML).toURI().toURL()));
    }

    /**
     * This can be used to read HTML file and convert everything to a string so you can manipulate or validate it.
     *
     * @param pathToFile path to the HTML -> give relative path always
     * @return the String object of the given file.
     */
    public static String readFileAndConvertItIntoAString(String pathToFile) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader defaultStructure = new BufferedReader(new FileReader(pathToFile));
            String str;
            while ((str = defaultStructure.readLine()) != null) {
                stringBuilder.append(str);
            }
            defaultStructure.close();
        } catch (IOException ignored) {
        }
        return stringBuilder.toString();
    }

    /**
     * This will allow you to use DevTools that was introduced by Selenium 4
     *
     * @return object of DevTools
     */
    public static DevTools getDevTools() {
        return ((ChromeDriver) driver).getDevTools();
    }
}
