/**
 * When a track is added to a chain, this updates their track positions.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertChainTrackUpdatePosTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // obtain all tracks in queue
        Long chainId = (Long) newRow[0];
        PreparedStatement ps = connection.prepareStatement("SELECT chn_trk_pos FROM CHAIN_TRACK WHERE chn_id=?");
        ps.setLong(1, chainId);
        ResultSet rs = ps.executeQuery();
        // determine the highest existing track position
        int highest = 0;
        while (rs.next()) {
            if (rs.getInt(1) > highest) {
                highest = rs.getInt(1);
            }
        }
        ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE trk_id=? AND chn_id=?");
        ps.setInt(1, highest + 1);
        ps.setLong(2, (Long) newRow[1]);
        ps.setLong(3, (Long) newRow[0]);
        ps.executeUpdate();
    }
}
