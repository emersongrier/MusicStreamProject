/**
 * When a track is removed from an album, this updates their track positions.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteTrackUpdatePosTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long trkPos = Long.parseLong(String.valueOf(oldRow[3]));
        // obtain all tracks in album
        long albId = Long.parseLong(String.valueOf(oldRow[8]));

        System.out.println("albId is " + albId);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM TRACK WHERE alb_id=?");
        ps.setLong(1, albId);
        ResultSet rs = ps.executeQuery();
        // decrement all track positions greater than trkPos

        while (rs.next()) {
            if (rs.getLong("trk_pos") > trkPos) {
                ps = connection.prepareStatement("UPDATE TRACK SET trk_pos=? WHERE trk_id=?");
                ps.setLong(1, rs.getLong("trk_pos") - 1);
                ps.setLong(2, rs.getLong("trk_id"));
                ps.executeUpdate();
            }
        }
        // TODO: FINISH (interrupted by work lol)
    }
}
