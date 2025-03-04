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

public class InsertFriendUpdateUserTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE USER_ SET usr_friends = (SELECT COUNT(*) FROM FRIEND WHERE ?=usr_id1 OR ?=usr_id2)"
        + " WHERE usr_id=?");
        ps.setLong(1, (Long) newRow[0]);
        ps.setLong(2, (Long) newRow[0]);
        ps.setLong(3, (Long) newRow[0]);
        ps.executeUpdate();
        ps = connection.prepareStatement(
                "UPDATE USER_ SET usr_friends = (SELECT COUNT(*) FROM FRIEND WHERE ?=usr_id1 OR ?=usr_id2)"
                        + " WHERE usr_id=?");
        ps.setLong(1, (Long) newRow[1]);
        ps.setLong(2, (Long) newRow[1]);
        ps.setLong(3, (Long) newRow[1]);
        ps.executeUpdate();
    }
}
