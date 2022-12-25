package setupAndUtilitys.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    /**
     * This method is used to load the properties from config.properties file
     *
     * @return it returns Properties prop object
     */
    public static Properties init_prop(String place) {

        Properties prop = new Properties();
        switch (place) {
            case "config":
                try {
                    FileInputStream ip = new FileInputStream("./src/test/resources/config/config.properties");
                    prop.load(ip);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "api":
                try {
                    FileInputStream ip = new FileInputStream("./src/test/resources/data/apiData.properties");
                    prop.load(ip);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }


        return prop;

    }


}
