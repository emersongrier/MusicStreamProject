package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteQueueTrackUpdatePosTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long trkPos = Long.parseLong(String.valueOf(oldRow[2]));
        // obtain all tracks in album
        long queId = Long.parseLong(String.valueOf(oldRow[0]));

        System.out.println("queId is " + queId);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM QUEUE_TRACK WHERE que_id=?");
        ps.setLong(1, queId);
        ResultSet rs = ps.executeQuery();
        // decrement all track positions greater than trkPos

        while (rs.next()) {
            if (rs.getLong("que_trk_pos") > trkPos) {
                ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE que_id=?");
                ps.setLong(1, rs.getLong("que_trk_pos") - 1);
                ps.setLong(2, rs.getLong("que_id"));
                ps.executeUpdate();
            }
        }
    }
}