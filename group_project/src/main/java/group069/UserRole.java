package group069;

import java.sql.SQLException;

public class UserRole {
    private int userID;
    private int roleID;

    public UserRole(int userID, int roleID) {
        this.setUserID(userID);
        this.setRoleID(roleID);
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        // check validity by querying database
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            if (!new DatabaseOperations().isValidUserID(userID, handler.openAndGetConnection())) {
                throw new IllegalArgumentException("Invalid userID.");
            }
            this.userID = userID;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }

    public int getRoleID() {
        return roleID;
    }

    public void setRoleID(int roleID) {
        if (roleID < 0 || roleID > 2) {
            throw new IllegalArgumentException("Invalid roleID.");
        }
        this.roleID = roleID;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "userID=" + userID +
                ", roleID=" + roleID +
                '}';
    }
}
