package group069;

import java.sql.SQLException;
import java.util.Date;

public class Order {

    public enum Status {
        PENDING, CONFIRMED, FULFILLED, ALL,
    }

    private int orderNo;
    private int userID;
    private long date;
    private int OrderQueueNo;
    private Status status;

    // Constructor for new order
    public Order(int userID, Status status) {
        this.setUserID(userID);
        this.setDate(new Date().getTime());
        this.setStatus(status);
        this.setOrderQueueNo(-1);
    }

    // Constructor when fetching from database
    public Order(int orderNo, int userID, long date, String OrderQueueNo, Status status) {
        this.setOrderNo(orderNo);
        this.setUserID(userID);
        this.setDate(date);
        if (OrderQueueNo != null) {
            this.setOrderQueueNo(Integer.parseInt(OrderQueueNo));
        }
        this.setStatus(status);
    }

    // Getters and Setters
    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        if (!isValidUserID(userID)) {
            throw new IllegalArgumentException("Invalid userID " + userID);
        }
        this.userID = userID;
    }

    public Date getDate() {
        return new Date(date);
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getOrderQueueNo() {
        return OrderQueueNo;
    }

    public void setOrderQueueNo(int OrderQueueNo) {
        if (OrderQueueNo < -1) {
            throw new IllegalArgumentException("Illegal OrderQueueNo " + OrderQueueNo);
        }
        this.OrderQueueNo = OrderQueueNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    // Check user exists
    private boolean isValidUserID(int userID) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            return db.isValidUserID(userID, handler.openAndGetConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            handler.closeConnection();
        }
    }

    @Override
    public String toString() {
        return "Order{" + "orderNo=" + orderNo + ", userID=" + userID + ", date=" + date + ", OrderQueueNo=" + OrderQueueNo + ", status=" + status
                + '}';
    }
}
