package setupAndUtilitys.util.logReader;

import setupAndUtilitys.util.Resources;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class LogReader {


    /**
     * @param customerID we are going through log file and searching if line contains its value.
     *                   Once we find such line we are deducting number of lines in order to get to paymentID line,
     *                   and then we are doing substring method to extract value that we need.
     *                   You will have to adapt this to your needs, this is just an example.
     * @return type is String, and we are returning in this case paymentID which is extracted from the given log.
     * @fakeClientLaravelLogPath this is a path to the log file -> yours will be different. It can be local,
     * like in this case, or you can access it via API.
     */
    public static String checkIfTheSpecificValueIsPresentInLogs(String customerID) {
        for (int i = 0; i < 30; i++) { //This is due to slow refresh speed of log file because it's not inside the project
            try {

                FileInputStream fstream = new FileInputStream(Resources.fakeClientLaravelLogPath.getResource());
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                String paymentID;
                int customerIDLineNumber = 0;

                while ((strLine = br.readLine()) != null) {
                    Pattern pattern = Pattern.compile(customerID);
                    customerIDLineNumber++;

                    if (strLine.contains(pattern.toString())) {
                        paymentID = Files.readAllLines(Paths.get(Resources.fakeClientLaravelLogPath.getResource())).get(customerIDLineNumber - 8);
                        paymentID = paymentID.substring(12);
                        return paymentID;
                    }

                }
                fstream.close();
                Thread.sleep(1000); //This is due to slow refresh speed of log file because it's not inside the project
            } catch (Exception e) {
                Assert.fail("Given log file doesn't contain desired customerID: " + customerID);
            }
        }
        Assert.fail("Given log file doesn't contain desired customerID: " + customerID);
        return null;
    }


}