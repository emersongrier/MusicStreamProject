/**
 * When a track is deleted, this deletes any associated EMBED entities.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteTrackDeleteEmbedTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long trkId = Long.parseLong(String.valueOf(oldRow[0]));
        String embType = "Track";

        PreparedStatement ps = connection.prepareStatement("DELETE FROM EMBED WHERE emb_id=? AND emb_type=?");
        ps.setLong(1, trkId);
        ps.setString(2, embType);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("No embedded content associated with track");
        }
        else {
            System.out.println("Deleted embedded content associated with track");
        }
    }
}