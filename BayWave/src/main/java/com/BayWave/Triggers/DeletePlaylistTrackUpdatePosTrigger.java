/**
 * When a track is removed from a playlist, this updates their track positions.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeletePlaylistTrackUpdatePosTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long trkPos = Long.parseLong(String.valueOf(oldRow[2]));
        // obtain all tracks in album
        long plyId = Long.parseLong(String.valueOf(oldRow[0]));

        System.out.println("plyId is " + plyId);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM PLAYLIST_TRACK WHERE ply_id=?");
        ps.setLong(1, plyId);
        ResultSet rs = ps.executeQuery();
        // decrement all track positions greater than trkPos

        while (rs.next()) {
            if (rs.getLong("ply_trk_pos") > trkPos) {
                ps = connection.prepareStatement("UPDATE PLAYLIST_TRACK SET ply_trk_pos=? WHERE ply_id=? AND trk_id=?");
                ps.setLong(1, rs.getLong("ply_trk_pos") - 1);
                ps.setLong(2, rs.getLong("ply_id"));
                ps.setLong(3, rs.getLong("trk_id"));
                ps.executeUpdate();
            }
        }
    }
}