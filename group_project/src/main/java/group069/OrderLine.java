package group069;

import java.sql.SQLException;

import exceptions.InvalidInputException;

public class OrderLine {

    private int orderNo;
    private int orderLineNo;
    private String productCode;
    private int quantity;
    private Float totalCost;

    // Constructor for new orderline
    public OrderLine(int orderNo, String productCode, int quantity) throws InvalidInputException {
        this.setOrderNo(orderNo);
        this.setOrderLineNo();
        this.setProductCode(productCode);
        this.setQuantity(quantity);
        this.setTotalCost(productCode, quantity);
    }

    // Constructor for fetching orderline from database
    public OrderLine(int orderNo, int orderLineNo, String productCode, int quantity, Float totalCost) throws InvalidInputException {
        this.setOrderNo(orderNo);
        this.setOrderLineNo(orderLineNo);
        this.setProductCode(productCode);
        this.setQuantity(quantity);
        this.setTotalCost(totalCost);
    }

    // Getters and Setters
    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        if (!isValidOrderNo(orderNo)) {
            throw new IllegalArgumentException("Invalid orderNo " + orderNo);
        }
        this.orderNo = orderNo;
    }

    public int getOrderLineNo() {
        return orderLineNo;
    }

    public void setOrderLineNo(int orderLineNo) {
        this.orderLineNo = orderLineNo;
    }

    // Overloaded method for new orderline
    public void setOrderLineNo() {
        this.orderLineNo = getNextOrderLineNo(this.orderNo);
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        if (!isValidProductCode(productCode)) {
            throw new IllegalArgumentException("Invalid productCode " + productCode);
        }
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws InvalidInputException {
        if (quantity < 1) {
            throw new InvalidInputException("Invalid quantity " + quantity);
        }
        this.quantity= quantity;
        this.setTotalCost(this.productCode, quantity);
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }

    // Overloaded method for new orderline
    public void setTotalCost(String productCode, int quantity) throws InvalidInputException {
        // get price of product
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            Product product = new DatabaseOperations().getProductByProductCode(productCode, handler.openAndGetConnection());
            Float price = product.getCost();
            this.totalCost = price * quantity;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }

    // Check is orderNo is valid
    private boolean isValidOrderNo(int orderNo) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            return new DatabaseOperations().isValidOrderNo(orderNo, handler.openAndGetConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            handler.closeConnection();
        }
    }

    // Get next orderLineNo
    private int getNextOrderLineNo(int orderNo) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            return new DatabaseOperations().getNextOrderLineNo(orderNo, handler.openAndGetConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            handler.closeConnection();
        }
    }

    // Check if productCode is valid
    private boolean isValidProductCode(String productCode) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            return new DatabaseOperations().isProductCodeUsed(productCode, handler.openAndGetConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            handler.closeConnection();
        }
    }

    // Get quantity of product in stock
    public static int getStock(String productCode) throws InvalidInputException {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        try {
            return new DatabaseOperations().getProductByProductCode(productCode, handler.openAndGetConnection()).getStock();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            handler.closeConnection();
        }
    }

    @Override
    public String toString() {
        return "OrderLines{orderNo=" + orderNo + ", orderLineNo=" + orderLineNo + ", productCode=" + productCode + ", quantity=" + quantity
                + ", totalCost=" + totalCost + "}";
    }
}
