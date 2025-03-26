/**
 * When a reply is deleted, this updates the reply count.
 *
 * Author: Bailey Inman
 */

package com.BayWave.Triggers;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeletePostUpdatePostTrigger implements Trigger {
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        // check old row to see if repl_pst_id is null
        Long replyId = (Long) oldRow[5];
        if (replyId != null) {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE POST SET pst_repls=(SELECT COUNT(*) FROM POST WHERE repl_pst_id=?)" +
                            " WHERE pst_id=?"
            );
            ps.setLong(1, replyId);
            ps.setLong(2, replyId);
            ps.executeUpdate();
        }
    }
}
