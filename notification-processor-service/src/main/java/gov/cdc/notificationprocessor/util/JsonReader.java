package gov.cdc.notificationprocessor.util;


import java.io.InputStream;


public class JsonReader {
    public static String readJsonFromResources(String filename){
        ClassLoader classloader = JsonReader.class.getClassLoader();
        try{

            InputStream inputStream = classloader.getResourceAsStream(filename);
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
