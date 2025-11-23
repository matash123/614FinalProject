package src.config;

import java.io.*;
import java.util.*;

public class envLoader {
    private static Properties props;

    static {
        props = new Properties();
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
            System.out.println("environment loader has run");
        } catch (Exception ignored) {}
    };

    public static String get(String key) {
        return props.getProperty(key);
    }
}
