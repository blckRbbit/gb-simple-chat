package com.github.blckrbbit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {

    static {
        try(FileInputStream in = new FileInputStream("network/log.config")) {
            LogManager.getLogManager().readConfiguration(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Logger getLogger(String className) {
        return Logger.getLogger(className);
    }
}
