package group069;

import java.sql.SQLException;

public class UserBankDetail {
    private int userID;
    private String cardNumber;

    public UserBankDetail(int userID, String cardNumber) {
        this.setUserID(userID);
        this.setCardNumber(cardNumber);
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        // check validity by querying database
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try{
            if (!new DatabaseOperations().isValidCardNumber(cardNumber, handler.openAndGetConnection())) {
                throw new IllegalArgumentException("Invalid cardNumber.");
            }
            this.cardNumber = cardNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }

    @Override
    public String toString() {
        return "UserBankDetails{" +
                "userID=" + userID +
                ", cardNumber=" + cardNumber +
                '}';
    }
}