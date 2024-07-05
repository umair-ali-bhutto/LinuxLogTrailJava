package com.ag.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggerProperties {
  static Properties logProperties;
  
  static {
    if (logProperties == null)
      try {
        InputStream in = LoggerProperties.class.getResourceAsStream("LogProperties.properties");
        logProperties = new Properties();
        logProperties.load(in);
      } catch (IOException e) {
        e.printStackTrace();
      }  
  }
  
  public static String getProperty(String key) {
    return logProperties.getProperty(key);
  }
  
  public static Properties getlogProperties() {
    return logProperties;
  }
  
  public static void setlogProperties(Properties logProperties) {
    LoggerProperties.logProperties = logProperties;
  }
}
