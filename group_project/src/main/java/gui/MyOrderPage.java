package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;

public class MyOrderPage extends JFrame {
    private ArrayList<OrderLine> orderLineList;

    public MyOrderPage() {
        super("My Order Page");
        this.setSize(1600, 1200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        float totalCost = 0.0f;
        JButton confirmOrderButton = new JButton("Confirm Order");

        // button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        buttonPanelLeft.add(backButton);
        JPanel buttonPanelRight = new JPanel(new FlowLayout());
        JButton myCardButton = new JButton("My Card");
        buttonPanelRight.add(myCardButton);
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        buttonPanel.add(buttonPanelRight, BorderLayout.EAST);
        this.add(buttonPanel, BorderLayout.NORTH);

        // content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            Session session = Session.getInstance();
            int userID = session.getCurrentUser().getUserID();
            // get pending order, make new if null
            Order pendingOrder = db.getPendingOrderByUserID(userID, handler.openAndGetConnection());
            if (pendingOrder == null) {
                boolean success = db.insertOrder(new Order(userID, Order.Status.PENDING), handler.openAndGetConnection());
                if (!success) {
                    throw new SQLException("Failed to create pending order");
                }
                if (!session.getIsCustomer()) {
                    // add customer role
                    success = db.insertUserRole(new UserRole(userID, 0), handler.openAndGetConnection());
                    if (!success) {
                        throw new SQLException("Failed to add customer role");
                    }
                }
                System.out.println("Created pending order for user " + userID);
                pendingOrder = db.getPendingOrderByUserID(userID, handler.openAndGetConnection());
            }

            // order details panel
            Order order = db.getOrderByOrderNo(pendingOrder.getOrderNo(), handler.openAndGetConnection());
            User user = db.getUserByID(order.getUserID(), handler.openAndGetConnection());
            JPanel orderDetailsPanel = new JPanel();
            orderDetailsPanel.setLayout(new BoxLayout(orderDetailsPanel, BoxLayout.Y_AXIS));
            JLabel orderNumberLabel = new JLabel("Order No: " + order.getOrderNo());
            JLabel nameLabel = new JLabel("Name: " + user.getForename() + " " + user.getSurname());
            JLabel emailLabel = new JLabel("Email: " + user.getEmail());
            JLabel addressLabel = new JLabel("Address: " + user.getAddress().toString());
            JLabel dateLabel = new JLabel("Date: " + order.getDate().toString());
            JLabel validCardLabel = new JLabel(
                    "Valid Bank Details: " + (db.getBankDetailsByUser(user.getUserID(), handler.openAndGetConnection()) == null ? "No" : "Yes"));
            orderDetailsPanel.add(orderNumberLabel);
            orderDetailsPanel.add(nameLabel);
            orderDetailsPanel.add(emailLabel);
            orderDetailsPanel.add(addressLabel);
            orderDetailsPanel.add(dateLabel);
            orderDetailsPanel.add(validCardLabel);
            contentPanel.add(orderDetailsPanel, BorderLayout.NORTH);

            // orderline list panel
            JPanel orderLineListPanel = new JPanel();
            orderLineListPanel.setLayout(new BoxLayout(orderLineListPanel, BoxLayout.Y_AXIS));
            this.orderLineList = db.getOrderLinesByOrderNo(pendingOrder.getOrderNo(), handler.openAndGetConnection());
            handler.closeConnection();

            // if no order lines
            if (this.orderLineList.isEmpty()) {
                JPanel noItemsPanel = new JPanel(new FlowLayout());
                JLabel noItemsLabel = new JLabel("Your order is empty");
                noItemsPanel.add(noItemsLabel);
                orderLineListPanel.add(noItemsPanel);
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
                quantityColumnLabel.setPreferredSize(new Dimension(300, 50));
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
                for (OrderLine orderLines : this.orderLineList) {
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
                    orderLineInfoPanel.setSize(new Dimension(WIDTH, 50));
                    orderLineInfoPanel.setSize(WIDTH, 50);
                    JLabel orderLineNoLabel = new JLabel(Integer.toString(++orderLineNo));
                    JLabel productCodeLabel = new JLabel(orderLines.getProductCode());
                    JLabel productBrandLabel = new JLabel(product.getProductBrand());
                    JLabel productNameLabel = new JLabel(product.getProductName());
                    JTextField quantityField = new JTextField(9);
                    quantityField.setText(Integer.toString(orderLines.getQuantity()));
                    JLabel costLabel = new JLabel("£" + orderLines.getTotalCost());
                    JButton updateQuantityButton = new JButton("Update Quantity");
                    orderLineNoLabel.setPreferredSize(new Dimension(100, 50));
                    productCodeLabel.setPreferredSize(new Dimension(100, 50));
                    productBrandLabel.setPreferredSize(new Dimension(150, 50));
                    productNameLabel.setPreferredSize(new Dimension(300, 50));
                    quantityField.setPreferredSize(new Dimension(100, 50));
                    costLabel.setPreferredSize(new Dimension(100, 50));
                    updateQuantityButton.setPreferredSize(new Dimension(200, 50));
                    orderLineNoLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productCodeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productBrandLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productNameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    quantityField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    costLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    orderLineInfoPanel.add(orderLineNoLabel);
                    orderLineInfoPanel.add(productCodeLabel);
                    orderLineInfoPanel.add(productBrandLabel);
                    orderLineInfoPanel.add(productNameLabel);
                    orderLineInfoPanel.add(costLabel);
                    orderLineInfoPanel.add(quantityField);
                    orderLineInfoPanel.add(updateQuantityButton);
                    // remove button
                    JButton removeOrderLineButton = new JButton("Remove");
                    // info, button -> order line row -> order line list -> content panel -> window
                    orderLineRowPanel.add(orderLineInfoPanel, BorderLayout.WEST);
                    orderLineRowPanel.add(removeOrderLineButton, BorderLayout.EAST);
                    orderLineListPanel.add(orderLineRowPanel);

                    // update quantity button
                    updateQuantityButton.addActionListener(e -> {
                        try {
                            // input checking
                            if (quantityField.getText().isEmpty()) {
                                throw new InvalidInputException("Quantity cannot be empty.");
                            }
                            int quantity = Integer.parseInt(quantityField.getText());
                            int stock = db.getProductByProductCode(orderLines.getProductCode(), handler.openAndGetConnection()).getStock();
                            handler.closeConnection();
                            if (quantity <= 0) {
                                throw new InvalidInputException("Quantity must be positive.");
                            } else if (quantity > stock) {
                                throw new InvalidInputException("Quantity cannot be greater than stock, which is " + stock + ".");
                            }

                            // update order line
                            orderLines.setQuantity(quantity);
                            boolean success = db.updateOrderLine(orderLines, handler.openAndGetConnection());
                            handler.closeConnection();
                            if (!success) {
                                JOptionPane.showMessageDialog(this, "Failed to update order line", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            JOptionPane.showMessageDialog(this, "Quantity updated to " + quantity, "Success", JOptionPane.INFORMATION_MESSAGE);
                            Utils.redirectMyOrderPage(this);
                        } catch (InvalidInputException ex) {
                            ex.displayErrorMessage();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } finally {
                            handler.closeConnection();
                        }
                    });

                    // remove order line button
                    removeOrderLineButton.addActionListener(e -> {
                        try {
                            boolean success = db.deleteOrderLine(orderLines.getOrderNo(), orderLines.getOrderLineNo(),
                                    handler.openAndGetConnection());
                            handler.closeConnection();
                            if (!success) {
                                JOptionPane.showMessageDialog(this, "Failed to remove order line", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            JOptionPane.showMessageDialog(this, "Order line removed", "Success", JOptionPane.INFORMATION_MESSAGE);
                            Utils.redirectMyOrderPage(this);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
                JScrollPane scrollPane = new JScrollPane(orderLineListPanel);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                contentPanel.add(scrollPane, BorderLayout.CENTER);

            }
        } catch (InvalidInputException e) {
            e.printStackTrace();
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }

        // confirm order panel
        JPanel confirmOrderPanel = new JPanel(new BorderLayout());
        confirmOrderPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
        JPanel confirmOrderPanelRight = new JPanel(new FlowLayout());
        JLabel totalCostLabel = new JLabel("Total Cost: £" + totalCost);
        totalCostLabel.setPreferredSize(new Dimension(150, 50));
        confirmOrderButton.setPreferredSize(new Dimension(200, 50));
        if (this.orderLineList.size() == 0) {
            confirmOrderButton.setEnabled(false);
        }
        confirmOrderPanelRight.add(totalCostLabel);
        confirmOrderPanelRight.add(confirmOrderButton);
        confirmOrderPanel.add(confirmOrderPanelRight, BorderLayout.EAST);
        contentPanel.add(confirmOrderPanel, BorderLayout.SOUTH);
        this.add(contentPanel, BorderLayout.CENTER);

        // confirm order button
        confirmOrderButton.addActionListener(e -> {
            try {
                int currentUserID = Session.getInstance().getCurrentUser().getUserID();
                boolean hasBankDetails = db.getBankDetailsByUser(currentUserID, handler.openAndGetConnection()) != null;
                handler.closeConnection();
                if (!hasBankDetails) {
                    Utils.redirectBankDetailsPage(this);
                    return;
                } else {
                    // get pending order
                    Order pendingOrder = db.getPendingOrderByUserID(currentUserID, handler.openAndGetConnection());

                    // guard against negative stock
                    ArrayList<OrderLine> orderLineList = db.getOrderLinesByOrderNo(pendingOrder.getOrderNo(), handler.openAndGetConnection());
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

                    // update order status
                    int orderQueueNo = db.getNextOrderQueueNo(handler.openAndGetConnection());
                    pendingOrder.setStatus(Order.Status.CONFIRMED);
                    pendingOrder.setOrderQueueNo(orderQueueNo);
                    pendingOrder.setDate(Utils.getCurrentUnixTimestamp());
                    boolean success = db.updateOrder(pendingOrder, handler.openAndGetConnection());
                    handler.closeConnection();
                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Failed to confirm order", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "Order confirmed", "Success", JOptionPane.INFORMATION_MESSAGE);
                    Utils.redirectHomePage(this);
                }
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                handler.closeConnection();
            }
        });

        // back button
        backButton.addActionListener(e -> {
            Utils.redirectHomePage(this);
        });

        // my card button
        myCardButton.addActionListener(e -> {
            Utils.redirectBankDetailsPage(this);
        });
    }
}
