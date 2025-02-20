package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class InsertUserAddQueueTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // add row to queue table, associated with new user
        System.out.println("newRow[1]: " + newRow[0]);
        int userId = Integer.parseInt(String.valueOf(newRow[0]));
        System.out.println("userId: " + userId);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO QUEUE (usr_id) VALUES (?)");
        ps.setInt(1, userId);
        ps.execute();
    }
}
