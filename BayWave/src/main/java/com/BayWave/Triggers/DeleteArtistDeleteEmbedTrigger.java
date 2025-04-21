/**
 * When an artist is deleted, this deletes any associated EMBED entities.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteArtistDeleteEmbedTrigger implements Trigger {
    @Override

    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        long artId = Long.parseLong(String.valueOf(oldRow[0]));
        String embType = "Artist";

        PreparedStatement ps = connection.prepareStatement("DELETE FROM EMBED WHERE emb_id=? AND emb_type=?");
        ps.setLong(1, artId);
        ps.setString(2, embType);
        int result = ps.executeUpdate();
        if (result == 0) {
            System.out.println("No embedded content associated with artist");
        }
        else {
            System.out.println("Deleted embedded content associated with artist");
        }
    }
}