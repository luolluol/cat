package com.dianping.cat.consumer.forward.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoaderUtil {

    public static Properties loadPropertis(String path) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(path);
        properties.load(inputStream);
        return properties;
    }

}
