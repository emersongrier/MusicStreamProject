package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertFollowArtistUpdateArtistTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE ARTIST SET art_flwrs = (SELECT COUNT(*) FROM FOLLOW_ARTIST WHERE ?=art_id)"
        + " WHERE art_id=?");
        ps.setLong(1, (Long) newRow[1]);
        ps.setLong(2, (Long) newRow[1]);
        ps.executeUpdate();
    }
}
