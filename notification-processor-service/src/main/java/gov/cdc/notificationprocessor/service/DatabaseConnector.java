package gov.cdc.notificationprocessor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class DatabaseConnector {
    @Value("${spring.datasource.url}")
    private String db_url;

    @Value("${spring.datasource.username}")
    private String db_user;

    @Value("${spring.datasource.password}")
    private String db_password;

    public void persistNotification(String base64EncodedMessage, String tablename) {

        try{


            Connection connection = DriverManager.getConnection(db_url, db_user,db_password);
            String buildInsertStatement = buildInsertQuery(tablename, base64EncodedMessage);
            PreparedStatement preparedStatement = connection.prepareStatement(buildInsertStatement);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildInsertQuery(String tablename, String base64EncodedMessage) {
        String insertStatement = "";

        return insertStatement;
    }

}
