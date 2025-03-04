/**
 * When a track is added to an album, this updates their track positions.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertTrackUpdatePosTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // obtain all tracks in album
        Long albId = (Long) newRow[8];
        PreparedStatement ps = connection.prepareStatement("SELECT trk_pos FROM TRACK WHERE alb_id=?");
        ps.setLong(1, albId);
        ResultSet rs = ps.executeQuery();
        // determine the highest existing track position
        int highest = 0;
        while (rs.next()) {
            if (rs.getInt(1) > highest) {
                highest = rs.getInt(1);
            }
        }
        ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
        ps.setInt(1, highest + 1);
        ps.setLong(2, (Long)newRow[0]);
        ps.executeUpdate();
        // increment alb_trks
        ps = connection.prepareStatement("SELECT alb_trks FROM ALBUM WHERE alb_id=?");
        ps.setLong(1, albId);
        rs = ps.executeQuery();
        if (!rs.isBeforeFirst()) {
            System.out.println("NO RESULTS");
            return;
        }
        rs.next();
        int newTrks = rs.getInt(1) + 1;
        ps = connection.prepareStatement("UPDATE ALBUM SET alb_trks=? WHERE alb_id=?");
        ps.setInt(1, newTrks);
        ps.setLong(2, albId);
        ps.executeUpdate();
    }
}
