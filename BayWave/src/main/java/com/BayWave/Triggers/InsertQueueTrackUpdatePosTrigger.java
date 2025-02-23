package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertQueueTrackUpdatePosTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // obtain all tracks in queue
        Long userId = (Long) newRow[0];
        PreparedStatement ps = connection.prepareStatement("SELECT que_trk_pos FROM QUEUE_TRACK WHERE usr_id=?");
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        // determine the highest existing track position
        int highest = 0;
        while (rs.next()) {
            if (rs.getInt(1) > highest) {
                highest = rs.getInt(1);
            }
        }
        ps = connection.prepareStatement("UPDATE QUEUE_TRACK SET que_trk_pos=? WHERE trk_id=?");
        ps.setInt(1, highest + 1);
        ps.setLong(2, (Long) newRow[1]);
        ps.executeUpdate();
    }
}