package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteChainTrackUpdatePosTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long trkPos = Long.parseLong(String.valueOf(oldRow[2]));
        // obtain all tracks in album
        long chainId = Long.parseLong(String.valueOf(oldRow[0]));

        System.out.println("chnId is " + chainId);
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM CHAIN_TRACK WHERE chn_id=?");
        ps.setLong(1, chainId);
        ResultSet rs = ps.executeQuery();
        // decrement all track positions greater than trkPos

        while (rs.next()) {
            if (rs.getLong("chn_trk_pos") > trkPos) {
                ps = connection.prepareStatement("UPDATE CHAIN_TRACK SET chn_trk_pos=? WHERE chn_id=? AND trk_id=?");
                ps.setLong(1, rs.getLong("chn_trk_pos") - 1);
                ps.setLong(2, rs.getLong("chn_id"));
                ps.setLong(3, rs.getLong("trk_id"));
                ps.executeUpdate();
            }
        }
    }
}