package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class Welcome {

    private final String rootPath = "src/main/resources/";
    private final String propFilePath_EN = rootPath + "application.properties";
    private final String propFilePath_DE = rootPath + "application_DE.properties";
    private final String propFilePath_FR = rootPath + "application_FR.properties";
    private Properties properties = new Properties();

    private final String lang;
    private String msg;

    public Welcome () {
        lang = getRandomLanguage();

        try {
            loadMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        msg = properties.getProperty("message");
    }

    private String getRandomLanguage() {
        String[] languages = new String[]{"en","de","fr"};
        int random = new Random().nextInt(languages.length);
        return languages[random];
    }

    private void loadMessage() throws IOException {
        // Load relevant message
        switch (lang) {
            case "de": properties.load(new FileInputStream(propFilePath_DE)); break;
            case "fr": properties.load(new FileInputStream(propFilePath_FR)); break;
            default: properties.load(new FileInputStream(propFilePath_EN)); break;
        }
    }

    public String getLang() {
        return lang;
    }

    public String getMsg() {
        return msg;
    }
}
