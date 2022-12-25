package parallel;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@CucumberOptions(

/**
 * @pretty -> cucumber default plugin
 * @parallel.ApplicationHooks -> plugin that we created for handling tests.
 * @extentreports -> reporter we are using.
 * @timeline -> report for threads in case you are executing in multithreading.
 *
 * @glue (parallel) -> per cucumber documentation both package that holds feature files and steps for the same must be under package that caries same name.
 * @tags -> target specific test cases in all feature files via tag/annotation. Ex. @smoke / @regression...etc.
 * @features -> path to the parent package of all feature files.
 *
 * Enable QMetry plugin logging -> add "json:target/qmetryUpload/qmetryresults.json", to plugin.
*/


        plugin = {"pretty",
                "parallel.ApplicationHooks",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:test-output-thread/"
        },
        monochrome = true,
        glue = {"parallel"},
        tags = "@DesiredTag",
        features = {"src/test/resources/parallel"}
)


public class RunTests extends AbstractTestNGCucumberTests {

    /**
     * ParallelRun.class.getName()  -> or input desired String
     */
    @AfterSuite
    public synchronized void setReporterName() throws IOException {
        String fileName = RunTests.class.getName() + new Date().toString().replaceAll(" ", "_").replaceAll(":", "-");
        Path sourceHtml = Paths.get("test-output/spark/index.html");
        Path sourcePdf = Paths.get("test-output/pdf/index.pdf");
        Files.move(sourceHtml, sourceHtml.resolveSibling(fileName + ".html"));
        Files.move(sourcePdf, sourcePdf.resolveSibling(fileName + ".pdf"));
    }

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}