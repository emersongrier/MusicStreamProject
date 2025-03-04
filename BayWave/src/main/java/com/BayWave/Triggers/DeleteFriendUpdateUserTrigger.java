/**
 * When a friendship is deleted, this decrements those users' friend counts.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteFriendUpdateUserTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // decrement friend counts
        // usr1
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM USER_ WHERE usr_id=?");
        ps.setLong(1, (Long) oldRow[0]);
        ResultSet rs = ps.executeQuery();
        ResultSet rs2;
        updateFriends(connection, rs);
        // usr2
        ps = connection.prepareStatement(
                "SELECT * FROM USER_ WHERE usr_id=?");
        ps.setLong(1, (Long) oldRow[1]);
        rs = ps.executeQuery();
        updateFriends(connection, rs);
    }

    private void updateFriends(Connection connection, ResultSet rs) throws SQLException {
        PreparedStatement ps;
        ResultSet rs2;
        while (rs.next()) {
            long usr = rs.getLong(1);
            ps = connection.prepareStatement("SELECT usr_friends FROM USER_ WHERE usr_id=?");
            ps.setLong(1, usr);
            rs2 = ps.executeQuery();
            rs2.next();
            int currFriends = rs2.getInt(1);
            ps = connection.prepareStatement(
                    "UPDATE USER_ SET usr_friends=? WHERE usr_id=?");
            ps.setInt(1, currFriends - 1);
            ps.setLong(2, usr);
            ps.executeUpdate();
        }
    }
}
