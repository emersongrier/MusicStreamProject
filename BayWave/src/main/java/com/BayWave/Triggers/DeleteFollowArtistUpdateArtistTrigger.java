package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DeleteFollowArtistUpdateArtistTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // decrement friend count
        PreparedStatement ps = connection.prepareStatement("SELECT art_flwrs FROM ARTIST WHERE art_id=?");
        ps.setLong(1, (Long) oldRow[1]);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int currentMembers = rs.getInt(1);
        PreparedStatement ps2 = connection.prepareStatement("UPDATE ARTIST SET art_flwrs=? WHERE art_id=?");
        ps2.setInt(1, currentMembers - 1);
        ps2.setLong(2, (Long) oldRow[1]);
        ps2.executeUpdate();
    }
}
