package group069;

import exceptions.InvalidInputException;

public class Session {
    private static Session instance;
    private User currentUser;
    private boolean isCustomer;
    private boolean isStaff;
    private boolean isManager;

    private Session() {
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            // fetch user from database to get userID and everything fresh
            this.currentUser = db.getUserByEmail(currentUser.getEmail(), handler.openAndGetConnection());
            this.isCustomer = db.hasRole(currentUser.getUserID(), 0, handler.openAndGetConnection());
            this.isStaff = db.hasRole(currentUser.getUserID(), 1, handler.openAndGetConnection());
            this.isManager = db.hasRole(currentUser.getUserID(), 2, handler.openAndGetConnection());
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }

    public boolean getIsCustomer() {
        return isCustomer;
    }

    public boolean getIsStaff() {
        return isStaff;
    }

    public boolean getIsManager() {
        return isManager;
    }

    public void logout() {
        currentUser = null;
        isCustomer = false;
        isStaff = false;
        isManager = false;
    }
}