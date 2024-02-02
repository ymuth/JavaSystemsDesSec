package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class OrderHistoryPage extends JFrame {

    // current user, all status
    public OrderHistoryPage(Window parent) {
        this(parent, Session.getInstance().getCurrentUser().getUserID(), Order.Status.ALL, true);
    }

    // all users, specific status
    public OrderHistoryPage(Window parent, Order.Status status) {
        this(parent, -1, status, true);
    }

    // special case when fulfilled/refused order
    public OrderHistoryPage(Window parent, Order.Status status, boolean back) {
        this(parent, -1, status, back);
    }

    public OrderHistoryPage(Window parent, int userID, Order.Status status, boolean back) {
        super("Order History Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 1200);
        this.setLocationRelativeTo(null);

        // button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        JButton backButton = new JButton("Back");
        buttonPanelLeft.add(backButton);
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        this.add(buttonPanel, BorderLayout.NORTH);

        // order list panel
        JPanel orderListPanel = new JPanel();
        orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            ArrayList<Order> orderList;
            if (userID == -1) {
                if (status == Order.Status.ALL) {
                    orderList = db.getAllOrders(handler.openAndGetConnection());
                    this.setTitle("All Orders");
                } else if (status == Order.Status.CONFIRMED) {
                    orderList = db.getConfirmedOrders(handler.openAndGetConnection());
                    this.setTitle("Orders with status " + status.toString());
                } else {
                    orderList = db.getOrdersByStatus(status, handler.openAndGetConnection());
                    this.setTitle("Orders with status " + status.toString());
                }
            } else {
                orderList = db.getOrdersByUserID(userID, handler.openAndGetConnection());
                this.setTitle("My Orders");
            }

            // if no order history found
            if (orderList.isEmpty()) {
                JLabel noOrderLabel = new JLabel("No order history found");
                JPanel noOrderPanel = new JPanel(new FlowLayout());
                noOrderPanel.add(noOrderLabel);
                orderListPanel.add(noOrderPanel);
                this.add(orderListPanel, BorderLayout.CENTER);
            } else {
                // headers
                JPanel headersRowPanel = new JPanel(new BorderLayout());
                headersRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                headersRowPanel.setPreferredSize(new Dimension(headersRowPanel.getPreferredSize().width, 50));
                headersRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                headerPanel.setSize(new Dimension(WIDTH, 50));
                JLabel orderNoColumnPanel = new JLabel("Order No");
                JLabel dateColumnPanel = new JLabel("Date");
                JLabel statusColumnPanel = new JLabel("Status");
                orderNoColumnPanel.setPreferredSize(new Dimension(100, 50));
                dateColumnPanel.setPreferredSize(new Dimension(200, 50));
                statusColumnPanel.setPreferredSize(new Dimension(150, 50));
                orderNoColumnPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                dateColumnPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                statusColumnPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                headerPanel.add(orderNoColumnPanel);
                headerPanel.add(dateColumnPanel);
                headerPanel.add(statusColumnPanel);
                if (userID == -1 && status == Order.Status.CONFIRMED) {
                    JLabel orderQueueNo = new JLabel("Order Queue No");
                    orderQueueNo.setPreferredSize(new Dimension(100, 50));
                    orderQueueNo.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    headerPanel.add(orderQueueNo);
                }
                headersRowPanel.add(headerPanel, BorderLayout.WEST);
                orderListPanel.add(headersRowPanel);

                // order rows
                int orderQueueNo = 0;
                for (Order order : orderList) {
                    // order row
                    JPanel orderRowPanel = new JPanel(new BorderLayout());
                    orderRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                    orderRowPanel.setPreferredSize(new Dimension(orderRowPanel.getPreferredSize().width, 50));
                    orderRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                    // order info
                    JPanel orderInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                    orderInfoPanel.setSize(WIDTH, 50);
                    JLabel orderNoLabel = new JLabel(Integer.toString(order.getOrderNo()));
                    JLabel dateLabel = new JLabel(order.getDate().toString());
                    JLabel statusLabel = new JLabel(order.getStatus().toString());
                    orderNoLabel.setPreferredSize(new Dimension(100, 50));
                    dateLabel.setPreferredSize(new Dimension(200, 50));
                    statusLabel.setPreferredSize(new Dimension(150, 50));
                    orderNoLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    dateLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    statusLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    orderInfoPanel.add(orderNoLabel);
                    orderInfoPanel.add(dateLabel);
                    orderInfoPanel.add(statusLabel);
                    if (userID == -1 && status == Order.Status.CONFIRMED) {
                        JLabel orderQueueNoLabel = new JLabel(++orderQueueNo + "");
                        orderQueueNoLabel.setPreferredSize(new Dimension(100, 50));
                        orderQueueNoLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                        orderInfoPanel.add(orderQueueNoLabel);
                    }
                    orderRowPanel.add(orderInfoPanel, BorderLayout.WEST);
                    // details button on the right
                    JButton viewOrderDetailsButton = new JButton("View Order Details");
                    viewOrderDetailsButton.setPreferredSize(new Dimension(200, 50));
                    orderRowPanel.add(viewOrderDetailsButton, BorderLayout.EAST);
                    orderListPanel.add(orderRowPanel);

                    // view order details button
                    viewOrderDetailsButton.addActionListener(e -> {
                        if (status == Order.Status.CONFIRMED)
                            Utils.redirectOrderDetailsPage(this, order.getOrderNo(), true);
                        else
                            Utils.redirectOrderDetailsPage(this, order.getOrderNo());
                    });
                    JScrollPane scrollPane = new JScrollPane(orderListPanel);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    this.add(scrollPane, BorderLayout.CENTER);
                }
            }
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }

        // back button
        backButton.addActionListener(e -> {
            if (back) {
                this.dispose();
                parent.setVisible(true);
                System.out.println("Back to previous page");
            } else {
                Utils.redirectStaffHomePage(this);
            }
        });
    }
}
