package main.java.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyReader {

    private InputStream inputStream;
    private static PropertyReader propertyReader=new PropertyReader();

    private PropertyReader(){

    }

    public static PropertyReader getInstance(){
        return propertyReader;
    }

    private Properties properties=new Properties();

    {
        try {
            String propFileName = "app.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }


        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPropValue(String property) throws IOException {

        return properties.getProperty(property);
    }
}
