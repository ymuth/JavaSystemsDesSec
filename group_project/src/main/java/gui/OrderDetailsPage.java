package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;

public class OrderDetailsPage extends JFrame {
    private Order.Status status;

    public OrderDetailsPage(Window parent, int orderNo) {
        this(parent, orderNo, false);
    }

    public OrderDetailsPage(Window parent, int orderNo, boolean permission) {
        super("Order Details Page");
        this.setSize(1600, 1200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        float totalCost = 0.0f;

        // button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        buttonPanelLeft.add(backButton);
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        this.add(buttonPanel, BorderLayout.NORTH);

        // content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            // order details panel
            Order order = db.getOrderByOrderNo(orderNo, handler.openAndGetConnection());
            handler.closeConnection();
            User user = db.getUserByID(order.getUserID(), handler.openAndGetConnection());
            handler.closeConnection();
            JPanel orderDetailsPanel = new JPanel();
            orderDetailsPanel.setLayout(new BoxLayout(orderDetailsPanel, BoxLayout.Y_AXIS));
            JLabel orderNumberLabel = new JLabel("Order No: " + order.getOrderNo());
            JLabel nameLabel = new JLabel("Name: " + user.getForename() + " " + user.getSurname());
            JLabel emailLabel = new JLabel("Email: " + user.getEmail());
            JLabel addressLabel = new JLabel("Address: " + user.getAddress().toString());
            JLabel dateLabel = new JLabel("Date: " + order.getDate().toString());
            JLabel validCardLabel = new JLabel(
                    "Valid Bank Details: " + (db.getBankDetailsByUser(user.getUserID(), handler.openAndGetConnection()) == null ? "No" : "Yes"));
            this.status = order.getStatus();
            orderDetailsPanel.add(orderNumberLabel);
            orderDetailsPanel.add(nameLabel);
            orderDetailsPanel.add(emailLabel);
            orderDetailsPanel.add(addressLabel);
            orderDetailsPanel.add(dateLabel);
            orderDetailsPanel.add(validCardLabel);
            contentPanel.add(orderDetailsPanel, BorderLayout.NORTH);

            // order line list panel
            JPanel orderLineListPanel = new JPanel();
            orderLineListPanel.setLayout(new BoxLayout(orderLineListPanel, BoxLayout.Y_AXIS));
            ArrayList<OrderLine> orderLineList = db.getOrderLinesByOrderNo(orderNo, handler.openAndGetConnection());
            handler.closeConnection();

            // if no order lines found
            if (orderLineList.isEmpty()) {
                JLabel noOrderLabel = new JLabel("No items found");
                JPanel noOrderPanel = new JPanel(new FlowLayout());
                noOrderPanel.add(noOrderLabel);
                orderLineListPanel.add(noOrderPanel);
                contentPanel.add(orderLineListPanel, BorderLayout.CENTER);
                this.add(contentPanel, BorderLayout.CENTER);
            } else {
                // headers
                JPanel headerRowPanel = new JPanel(new BorderLayout());
                headerRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                headerRowPanel.setPreferredSize(new Dimension(headerRowPanel.getPreferredSize().width, 50));
                headerRowPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                headerPanel.setSize(new Dimension(WIDTH, 50));
                JLabel orderLineNoColumnLabel = new JLabel("Line No.");
                JLabel productCodeColumnLabel = new JLabel("Product Code");
                JLabel productBrandColumnLabel = new JLabel("Brand");
                JLabel productNameColumnLabel = new JLabel("Name");
                JLabel quantityColumnLabel = new JLabel("Quantity");
                JLabel costColumnLabel = new JLabel("Cost");
                orderLineNoColumnLabel.setPreferredSize(new Dimension(100, 50));
                productCodeColumnLabel.setPreferredSize(new Dimension(100, 50));
                productBrandColumnLabel.setPreferredSize(new Dimension(150, 50));
                productNameColumnLabel.setPreferredSize(new Dimension(300, 50));
                quantityColumnLabel.setPreferredSize(new Dimension(100, 50));
                costColumnLabel.setPreferredSize(new Dimension(100, 50));
                orderLineNoColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productCodeColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productBrandColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productNameColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                quantityColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                costColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                headerPanel.add(orderLineNoColumnLabel);
                headerPanel.add(productCodeColumnLabel);
                headerPanel.add(productBrandColumnLabel);
                headerPanel.add(productNameColumnLabel);
                headerPanel.add(costColumnLabel);
                headerPanel.add(quantityColumnLabel);
                headerRowPanel.add(headerPanel, BorderLayout.WEST);
                orderLineListPanel.add(headerRowPanel);

                // order lines
                int orderLineNo = 0;
                for (OrderLine orderLines : orderLineList) {
                    // update total cost
                    totalCost += orderLines.getTotalCost();
                    // get product
                    Product product = db.getProductByProductCode(orderLines.getProductCode(), handler.openAndGetConnection());
                    handler.closeConnection();

                    // order line row
                    JPanel orderLineRowPanel = new JPanel(new BorderLayout());
                    orderLineRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                    orderLineRowPanel.setPreferredSize(new Dimension(orderLineRowPanel.getPreferredSize().width, 50));
                    orderLineRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                    // order line info
                    JPanel orderLineInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                    orderLineInfoPanel.setSize(WIDTH, 50);
                    JLabel orderLineNoLabel = new JLabel(Integer.toString(++orderLineNo));
                    JLabel productCodeLabel = new JLabel(orderLines.getProductCode());
                    JLabel productBrandLabel = new JLabel(product.getProductBrand());
                    JLabel productNameLabel = new JLabel(product.getProductName());
                    JLabel quantityLabel = new JLabel(Integer.toString(orderLines.getQuantity()));
                    JLabel costLabel = new JLabel("£" + orderLines.getTotalCost());
                    orderLineNoLabel.setPreferredSize(new Dimension(100, 50));
                    productCodeLabel.setPreferredSize(new Dimension(100, 50));
                    productBrandLabel.setPreferredSize(new Dimension(150, 50));
                    productNameLabel.setPreferredSize(new Dimension(300, 50));
                    quantityLabel.setPreferredSize(new Dimension(100, 50));
                    costLabel.setPreferredSize(new Dimension(100, 50));
                    orderLineNoLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productCodeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productBrandLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productNameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    quantityLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    costLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    orderLineInfoPanel.add(orderLineNoLabel);
                    orderLineInfoPanel.add(productCodeLabel);
                    orderLineInfoPanel.add(productBrandLabel);
                    orderLineInfoPanel.add(productNameLabel);
                    orderLineInfoPanel.add(costLabel);
                    orderLineInfoPanel.add(quantityLabel);
                    orderLineRowPanel.add(orderLineInfoPanel, BorderLayout.WEST);
                    orderLineListPanel.add(orderLineRowPanel);
                }
                JScrollPane scrollPane = new JScrollPane(orderLineListPanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                contentPanel.add(scrollPane, BorderLayout.CENTER);
            }
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
        // footer panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        JPanel footerPanelRight = new JPanel(new FlowLayout());
        JLabel totalCostLabel = new JLabel("Total Cost: £" + totalCost);
        totalCostLabel.setPreferredSize(new Dimension(150, 50));
        JLabel statusLabel = new JLabel("Status: " + status);
        statusLabel.setPreferredSize(new Dimension(200, 50));
        footerPanelRight.add(totalCostLabel);
        footerPanelRight.add(statusLabel);
        // add fulfill/refuse if granted permission
        if (permission && status == Order.Status.CONFIRMED) {
            try {
                // check if this order is at top of queue
                int currentQueueNo = db.getCurrentOrderQueueNo(handler.openAndGetConnection());
                int order = db.getOrderByOrderNo(orderNo, handler.openAndGetConnection()).getOrderQueueNo();

                if (currentQueueNo == order) {
                    JButton fulfillButton = new JButton("Fufill Order");
                    fulfillButton.setPreferredSize(new Dimension(150, 50));
                    JButton refuseButton = new JButton("Refuse Order");
                    refuseButton.setPreferredSize(new Dimension(150, 50));
                    footerPanelRight.add(fulfillButton);
                    footerPanelRight.add(refuseButton);

                    // fulfill button
                    fulfillButton.addActionListener(e -> {
                        fulfill(orderNo);
                    });

                    // refuse button
                    refuseButton.addActionListener(e -> {
                        refuse(orderNo);
                    });
                }

            } catch (InvalidInputException e) {
                e.displayErrorMessage();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                handler.closeConnection();
            }
        }

        footerPanel.add(footerPanelRight, BorderLayout.EAST);
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        this.add(contentPanel, BorderLayout.CENTER);

        // back button
        backButton.addActionListener(e -> {
            back(parent);
        });
    }

    // back
    public void back(Window parent) {
        this.dispose();
        parent.setVisible(true);
        System.out.println("Back to previous page");
    }

    // fulfill
    public void fulfill(int orderNo) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            // try to update stock, guard against negative stock
            ArrayList<OrderLine> orderLineList = db.getOrderLinesByOrderNo(orderNo, handler.openAndGetConnection());
            HashMap<String, Product> productMap = new HashMap<>();
            for (OrderLine orderLine : orderLineList) {
                try {
                    Product product = productMap.get(orderLine.getProductCode());
                    if (product == null)
                        product = db.getProductByProductCode(orderLine.getProductCode(), handler.openAndGetConnection());
                    product.setStock(product.getStock() - orderLine.getQuantity());
                    productMap.put(product.getProductCode(), product);
                } catch (InvalidInputException ex) {
                    Product product = db.getProductByProductCode(orderLine.getProductCode(), handler.openAndGetConnection());
                    String message = "Quantity of " + product.getProductCode() + " cannot be greater than stock, which is " + product.getStock();
                    throw new InvalidInputException(message);
                }
            }
            // update stock
            for (Product product : productMap.values()) {
                boolean success = db.updateProduct(product, product.getProductCode(), handler.openAndGetConnection());
                if (!success) {
                    JOptionPane.showMessageDialog(this, "Failed to update stock for product " + product.getProductCode(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            // update order status and delete from queue
            Order order = db.getOrderByOrderNo(orderNo, handler.openAndGetConnection());
            order.setStatus(Order.Status.FULFILLED);
            order.setOrderQueueNo(-1);
            db.updateOrder(order, handler.openAndGetConnection());
            JOptionPane.showMessageDialog(this, "Order fulfilled", "Success", JOptionPane.INFORMATION_MESSAGE);
            Utils.redirectOrderHistoryPage(this, Order.Status.CONFIRMED, false);
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }

    // refuse
    public void refuse(int orderNo) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            // delete order
            db.deleteOrder(orderNo, handler.openAndGetConnection());
            JOptionPane.showMessageDialog(this, "Order refused", "Success", JOptionPane.INFORMATION_MESSAGE);
            Utils.redirectOrderHistoryPage(this, Order.Status.CONFIRMED, false);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }
    }
}
