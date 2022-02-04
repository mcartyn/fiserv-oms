package com.bypass.oms.component;

import org.apache.log4j.Logger;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.springframework.stereotype.Component;

@Component
public class SomeComponent {
    public static final Logger logger = LogManager.getLogger(SomeComponent.class);
    

    public SomeComponent(DataSource dataSource) throws SQLException {
        String res = "Database connection valid = " + dataSource.getConnection().isValid(1000);
        System.out.println(res);
    }
}