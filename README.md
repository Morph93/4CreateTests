# FRAMEWORK STRUCTURE!

## First separation:

* main
    * main should contain everything regarding the structure of the project and all page object models that reflect
      pages of the project. the
* test
    * test should contain all data regarding test cases such as feature files, step classes, runner class...etc.

## Secondary separation:

### main/java:

* apiPOJO -> this package should contain:
    * sub-packages with API names that should contain: (ex.jiraIntegration, billingService...This can go deeper, check
      billingService.)
        * Enum resources class  (ex. jiraIntegration/ResourcesJIRA).
        * POJO class that reflects response structure (ex. jiraIntegration/CreateIssue class).


* setupAndUtilitys -> this package should contain all custom classes that we are using.
    * ex:
        * FunctionsPage
        * jdbc
        * jiraIntegration
        * etc...


* xmlExecution -> should contain XML files that will control execution such as number of threads and targeting of runner
  classes.


* pages -> should contain structure of packages/classes that reflect structure of the application that you are working
  on. Each page should contain HashMap of all elements present on the given page depending on a state for that page.
  State can vary, and it's triggered by additional actions on that page. Check LoginPage and DashboardPage.

### test/java:

* apiHandlers -> this package should contain:
    * testDataBuilder package which should contain responseSpecification that will match structure of the response of
      the API call that you are using, so you can send data using it (check LoginAuth).
    * ApiUtils -> class that contains functions for BE/API calls -> this class is similar to FunctionsPage -> It would
     


* parallel package -> This package should contain:
    * steps for API test cases -> contains mapped steps from cucumber feature files for API test.
    * steps for UI test cases -> contains mapped steps from cucumber feature files for UI test.
    * Application Hooks class -> which handles everything that we be done before and after test.
    * Runner class -> which is used to target specific tests and set plugins that you wish to use.

### test/resources:

* parallel package should contain:
    * api package -> which will contain subpackages that match structure of api calls which will contain feature files
      for API tests
    * ui package -> which will contain subpackages that match structure of UI from application side which will contain
      feature files for UI tests

* cucumber.properties -> one liner, set if you wish to publish default cucumber report online and have access to it from
  the cloud.
* extent.properties -> configuration for extent report, location, naming...etc. Check extent documentation
  online: https://www.extentreports.com/docs/versions/4/java/spark-reporter.html
* extent-config.xml -> similar like properties, configuration for extent report. Check documentation.
* logback.xml -> for depth level of logs.

# WRITING,RUNNING AND REPORTING!

### STEPS:

**Each feature file test that you create for UI must carry annotation @ UI in order to trigger desired functions from
ApplicationHooks class**

1. Create feature file for desired tests in:
    1. `src/test/resources/parallel/ui`
    2. `src/test/resources/parallel/api`
2. Fill the feature file with:
    1. `Scenario `
    2. `Scenario Outline`
        1. **Separation of tests for Scenario Outline can be done with annotations ex. @ Smoke / @Regression. Check login.feature.**
3. Create steps that will map those scenarios/steps in:
    1. `src/test/java/parallel/uiSteps`
    2. `src/test/java/parallel/apiSteps`
4. Create page class for the page you wish to test in: `src/main/java/pages`
5. Fill the page class with the elements of that page using `HashMap<String, By> mapOfPageElements = new HashMap<>();`
6. Initiate that map in constructor of the page class
7. Use functions created in page class in the steps by creating object of the page class.
   ex. `LoginPage loginPage = new LoginPage(DriverFactory.getDriver(), "");` -> `loginPage.checkAllElements();`

### EXECUTION:

**There are multiple ways to execute test cases**

1. Directly from feature file by **right-clicking** on specific Scenario or Scenario Outline and then choosing **Run**.
   That way you will run desired test in single thread.
2. From Runner class in our case **RunTests**. Right-click on the class and choose **Run**. If you choose to run tests
   like this, the tests will run in multithreading creating 10 instances and will target tests that carry annotation set
   inside the class under **@CucumberOptions** -> **tags**
3. From XLM files `src/main/java/xmlExecution` -> those xml files will target runner class in our case
   RunTests and in them, you can set number of threads for execution.

### REPORTING:

**We are currently generating multiple reports for our tests**

1. `project/test-output`
    1. `pdf` -> this package contains **PDF** report for tests that are executed through either **Runner class** or **
       XML file**.
    2. `spark`-> this package contains **HTML** report for tests that are executed through either **Runner class** or **
       XML file**.


2. `project/test-output-thread` -> this package contains **HTML** report for **threads** and tests that are executed **
   in multithreading** through either **Runner class** or **XML file**. 
