/**
 * When a friendship is created, this increments the users' friend counts.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertLikeTrackUpdateTrackTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE TRACK SET trk_likes = (SELECT COUNT(*) FROM LIKE_TRACK WHERE trk_id=?) WHERE trk_id=?");
        ps.setLong(1, (Long) newRow[0]);
        ps.setLong(2, (Long) newRow[0]);
        ps.executeUpdate();
    }
}
