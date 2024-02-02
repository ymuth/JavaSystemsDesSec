package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class CustomerCategoryPage extends JFrame {

    public CustomerCategoryPage(int categoryID) {
        super(Utils.categoryNames.get(categoryID) + " Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 1200);
        this.setLocationRelativeTo(null);
        JButton backButton = new JButton("Back");

        // button panel
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        buttonPanelLeft.add(backButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        this.add(buttonPanel, BorderLayout.NORTH);

        // product list
        JPanel productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            ArrayList<Product> productList = db.getProductsByCategory(categoryID, handler.openAndGetConnection());
            handler.closeConnection();

            // no products found
            if (productList.isEmpty()) {
                JLabel noProductsLabel = new JLabel("No products found for this category.");
                productListPanel.add(noProductsLabel);
                this.add(productListPanel);
                return;
            }

            // products found
            // add mandatory columns
            JPanel headersPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
            JLabel productCodeColumnLabel = new JLabel("Product Code");
            JLabel productBrandColumnLabel = new JLabel("Brand");
            JLabel productNameColumnLabel = new JLabel("Name");
            JLabel gaugeColumnLabel = new JLabel("Gauge");
            JLabel stockColumnLabel = new JLabel("Stock");
            JLabel costColumnLabel = new JLabel("Cost");
            JLabel quantityColumnLabel = new JLabel("Quantity");
            productCodeColumnLabel.setPreferredSize(new Dimension(100, 50));
            productBrandColumnLabel.setPreferredSize(new Dimension(100, 50));
            productNameColumnLabel.setPreferredSize(new Dimension(300, 50));
            gaugeColumnLabel.setPreferredSize(new Dimension(100, 50));
            stockColumnLabel.setPreferredSize(new Dimension(100, 50));
            costColumnLabel.setPreferredSize(new Dimension(100, 50));
            quantityColumnLabel.setPreferredSize(new Dimension(100, 50));
            productCodeColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            productBrandColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            productNameColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            gaugeColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            stockColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            costColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            quantityColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            headersPanel.setSize(WIDTH, 50);
            headersPanel.add(productCodeColumnLabel);
            headersPanel.add(productBrandColumnLabel);
            headersPanel.add(productNameColumnLabel);
            headersPanel.add(gaugeColumnLabel);

            // check optional columns
            Product sampleProduct = productList.get(0);
            if (sampleProduct.getEraCode() != null) {
            JLabel eraCodeColumnLabel = new JLabel("Era Code");
            eraCodeColumnLabel.setPreferredSize(new Dimension(100, 50));
            eraCodeColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            headersPanel.add(eraCodeColumnLabel);
            }
            if (sampleProduct.getDccCode() != null) {
            JLabel dccCodeColumnLabel = new JLabel("DCC Code");
            dccCodeColumnLabel.setPreferredSize(new Dimension(100, 50));
            dccCodeColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            headersPanel.add(dccCodeColumnLabel);
            }
            if (sampleProduct.isDigital() != null) {
            JLabel digitalColumnLabel = new JLabel("Digital");
            digitalColumnLabel.setPreferredSize(new Dimension(100, 50));
            digitalColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            headersPanel.add(digitalColumnLabel);
            }
            if (sampleProduct.getPackageContent() != null) {
            JLabel packageContentColumnLabel = new JLabel("Package Content");
            packageContentColumnLabel.setPreferredSize(new Dimension(200, 50));
            packageContentColumnLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
            headersPanel.add(packageContentColumnLabel);
            }
            headersPanel.add(stockColumnLabel);
            headersPanel.add(costColumnLabel);
            headersPanel.add(quantityColumnLabel);

            // wrap in row panel
            JPanel headersRowPanel = new JPanel(new BorderLayout());
            headersRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            headersRowPanel.setPreferredSize(new Dimension(headersRowPanel.getPreferredSize().width, 50));
            headersRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
            headersRowPanel.add(headersPanel, BorderLayout.WEST);
            productListPanel.add(headersRowPanel);

            // add products per row
            for (Product product : productList) {
                JPanel productInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));

                // add mandatory columns
                JLabel productCodeLabel = new JLabel(product.getProductCode());
                productCodeLabel.setPreferredSize(new Dimension(100, 50));
                productCodeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(productCodeLabel);
                JLabel productBrandLabel = new JLabel(product.getProductBrand());
                productBrandLabel.setPreferredSize(new Dimension(100, 50));
                productBrandLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(productBrandLabel);
                JLabel productNameLabel = new JLabel(product.getProductName());
                productNameLabel.setPreferredSize(new Dimension(300, 50));
                productNameLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(productNameLabel);
                JLabel gaugeLabel = new JLabel(product.getGauge());
                gaugeLabel.setPreferredSize(new Dimension(100, 50));
                gaugeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(gaugeLabel);

                // check optional columns
                if (product.getEraCode() != null) {
                    JLabel eraCodeLabel = new JLabel(product.getEraCode());
                    eraCodeLabel.setPreferredSize(new Dimension(100, 50));
                    eraCodeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productInfoPanel.add(eraCodeLabel);
                }
                if (product.getDccCode() != null) {
                    JLabel dccCodeLabel = new JLabel(product.getDccCode());
                    dccCodeLabel.setPreferredSize(new Dimension(100, 50));
                    dccCodeLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productInfoPanel.add(dccCodeLabel);
                }
                if (product.isDigital() != null) {
                    JLabel digitalLabel = new JLabel(String.valueOf(product.isDigital()));
                    digitalLabel.setPreferredSize(new Dimension(100, 50));
                    digitalLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productInfoPanel.add(digitalLabel);
                }
                if (product.getPackageContent() != null) {
                    JLabel packageContentLabel = new JLabel(product.getPackageContent());
                    packageContentLabel.setPreferredSize(new Dimension(200, 50));
                    packageContentLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                    productInfoPanel.add(packageContentLabel);
                }

                // add remaining mandatory columns
                JLabel stockLabel = new JLabel(Integer.toString(product.getStock()));
                stockLabel.setPreferredSize(new Dimension(100, 50));
                stockLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(stockLabel);
                JLabel costLabel = new JLabel("£" + product.getCost());
                costLabel.setPreferredSize(new Dimension(100, 50));
                costLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(costLabel);
                JTextField quantityField = new JTextField(9);
                quantityField.setMinimumSize(new Dimension(100, 50));
                quantityField.setPreferredSize(new Dimension(100, 50));
                quantityField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                productInfoPanel.add(quantityField);
                JButton addProductButton = new JButton("Add to Cart");

                // wrap in row panel
                JPanel productRowPanel = new JPanel(new BorderLayout());
                productRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                productRowPanel.setPreferredSize(new Dimension(productRowPanel.getPreferredSize().width, 50));
                productRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                productInfoPanel.setSize(WIDTH, 50);
                productRowPanel.add(productInfoPanel, BorderLayout.WEST);
                addProductButton.setPreferredSize(new Dimension(150, 50));
                if (product.getStock() <= 0) {
                    addProductButton.setEnabled(false);
                    addProductButton.setText("Out of Stock");
                }
                productRowPanel.add(addProductButton, BorderLayout.EAST);

                // add row to productList panel
                productListPanel.add(productRowPanel);

                // add product to cart
                addProductButton.addActionListener(e -> {
                    try {
                        // input checking
                        if (quantityField.getText().isEmpty()) {
                            throw new InvalidInputException("Quantity cannot be empty.");
                        }
                        int quantity = Integer.parseInt(quantityField.getText());
                        if (quantity <= 0) {
                            throw new InvalidInputException("Quantity must be positive.");
                        } else if (quantity > product.getStock()) {
                            throw new InvalidInputException("Quantity cannot be greater than stock.");
                        }

                        // get pending order
                        Session session = Session.getInstance();
                        int userID = session.getCurrentUser().getUserID();
                        Order pendingOrder = db.getPendingOrderByUserID(userID, handler.openAndGetConnection());
                        handler.closeConnection();
                        // create pending order if it doesn't exist
                        if (pendingOrder == null) {
                            boolean success = db.insertOrder(new Order(userID, Order.Status.PENDING), handler.openAndGetConnection());
                            handler.closeConnection();
                            if (!success) {
                                JOptionPane.showMessageDialog(this, "Failed to create pending order.", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                            if (!session.getIsCustomer()) {
                                // add customer role
                                success = db.insertUserRole(new UserRole(userID, 0), handler.openAndGetConnection());
                                if (!success) {
                                    throw new SQLException("Failed to add customer role");
                                }
                            }
                            pendingOrder = db.getPendingOrderByUserID(userID, handler.openAndGetConnection());
                            handler.closeConnection();
                        }

                        // add orderline to order (add to cart)
                        OrderLine orderLine = new OrderLine(pendingOrder.getOrderNo(), product.getProductCode(), quantity);
                        boolean success = db.insertOrderLine(orderLine, handler.openAndGetConnection());
                        if (!success) {
                            JOptionPane.showMessageDialog(this, "Failed to add product to cart.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(this, "Product added to cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (InvalidInputException ex) {
                        ex.displayErrorMessage();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        handler.closeConnection();
                    }
                });
            }
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }

        JScrollPane scrollPane = new JScrollPane(productListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        this.add(scrollPane, BorderLayout.CENTER);

        // back button
        backButton.addActionListener(e -> {
            Utils.redirectHomePage(this);
        });
    }
}
