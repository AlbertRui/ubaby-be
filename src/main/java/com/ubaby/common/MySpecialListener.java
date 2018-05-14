package com.ubaby.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * @author AlbertRui
 * @date 2018-05-14 21:05
 */
public class MySpecialListener implements ServletContextListener {

    private Logger logger = LoggerFactory.getLogger(MySpecialListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // On Application Startup, please…

        // Usually I'll make a singleton in here, set up my pool, etc.
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                logger.info("deregistering jdbc driver:{}", driver);
            } catch (SQLException e) {
                logger.info("Error deregistering driver:{}", driver, e);
            }

        }
    }

}
