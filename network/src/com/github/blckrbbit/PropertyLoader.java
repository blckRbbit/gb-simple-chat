package com.github.blckrbbit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyLoader {
    private final Properties properties;

    private PropertyLoader(String pathToProperty) throws IOException {
        FileInputStream in = new FileInputStream(pathToProperty);
        this.properties = new Properties();
        this.properties.load(in);
    }

    public static PropertyLoader load(String pathToProperty) {
        PropertyLoader loader = null;
        try {
            loader = new PropertyLoader(pathToProperty);
        } catch (IOException e) {
            System.err.printf("Error. File < %s > was not found...", pathToProperty);
        }
        return loader;
    }

    public Properties properties() {
        return properties;
    }
}
