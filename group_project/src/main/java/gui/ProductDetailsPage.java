package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProductDetailsPage extends JFrame {

    private int categoryID;
    private JLabel productCodeLabel = new JLabel("Product Code");
    private JLabel productBrandLabel = new JLabel("Product Brand");
    private JLabel productNameLabel = new JLabel("Product Name");
    private JLabel gaugeLabel = new JLabel("Gauge");
    private JLabel eraCodeLabel = new JLabel("Era Code");
    private JLabel dccCodeLabel = new JLabel("DCC Code");
    private JLabel digitalLabel = new JLabel("Digital");
    private JLabel packageContentLabel = new JLabel("Package Content");
    private JLabel stockLabel = new JLabel("Stock");
    private JLabel costLabel = new JLabel("Cost");
    private JTextField productCodeField = new JTextField();
    private JTextField productBrandField = new JTextField();
    private JTextField productNameField = new JTextField();
    private JTextField gaugeField = new JTextField();
    private JTextField eraCodeField = new JTextField();
    private JTextField dccCodeField = new JTextField();
    private JTextField digitalField = new JTextField();
    private JTextField packageContentField = new JTextField();
    private JTextField stockField = new JTextField();
    private JTextField costField = new JTextField();
    private JButton backButton = new JButton("Back");
    private JButton deleteButton = new JButton("Delete");
    private JButton saveButton = new JButton("Save/Back");

    public ProductDetailsPage(int categoryID, Product currentProduct) {
        if (currentProduct != null) {
            // editing
            this.setTitle("Edit Product " + currentProduct.getProductCode() + " Page");
        } else {
            // adding
            this.setTitle("Add " + Utils.categoryNames.get(categoryID) + " Page");
        }
        this.setSize(800, 600);
        this.setLayout(new GridLayout(11, 2));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.categoryID = categoryID;

        // set fields if editing
        if (currentProduct != null) {
            productCodeField.setText(currentProduct.getProductCode());
            productBrandField.setText(currentProduct.getProductBrand());
            productNameField.setText(currentProduct.getProductName());
            gaugeField.setText(currentProduct.getGauge());
            eraCodeField.setText(currentProduct.getEraCode());
            dccCodeField.setText(currentProduct.getDccCode());
            digitalField.setText(currentProduct.isDigital() == null ? "" : currentProduct.isDigital().toString());
            packageContentField.setText(currentProduct.getPackageContent());
            stockField.setText(Integer.toString(currentProduct.getStock()));
            costField.setText(Float.toString(currentProduct.getCost()));
        }

        // add fields for input
        this.add(productCodeLabel);
        this.add(productCodeField);
        this.add(productBrandLabel);
        this.add(productBrandField);
        this.add(productNameLabel);
        this.add(productNameField);
        this.add(gaugeLabel);
        this.add(gaugeField);
        if (categoryID == 0) {
            this.add(eraCodeLabel);
            this.add(eraCodeField);
            this.add(dccCodeLabel);
            this.add(dccCodeField);
        } else if (categoryID == 1) {
            this.add(eraCodeLabel);
            this.add(eraCodeField);
        } else if (categoryID == 2) {
            this.add(digitalLabel);
            this.add(digitalField);
        } else if (categoryID == 4) {
            this.add(eraCodeLabel);
            this.add(eraCodeField);
            this.add(packageContentLabel);
            this.add(packageContentField);
        } else if (categoryID == 5) {
            this.add(packageContentLabel);
            this.add(packageContentField);
        }
        this.add(stockLabel);
        this.add(stockField);
        this.add(costLabel);
        this.add(costField);
        if (currentProduct != null) {
            this.add(deleteButton);
        } else {
            this.add(backButton);
        }
        this.add(saveButton);

        // back button
        backButton.addActionListener(e -> {
            Utils.redirectStaffCategoryPage(this, this.categoryID);
        });
        
        // delete button
        deleteButton.addActionListener(e -> {
            // delete product
            try {
                handleDelete(currentProduct);
                Utils.redirectStaffCategoryPage(this, this.categoryID);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // save button
        saveButton.addActionListener(e -> {
            // save product details
            try {
                handleSave(categoryID, currentProduct);
                Utils.redirectStaffCategoryPage(this, this.categoryID);
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    // delete product
    private void handleDelete(Product currentProduct) throws SQLException {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            if (!db.deleteProduct(currentProduct.getProductCode(), handler.openAndGetConnection())) {
                JOptionPane.showMessageDialog(this, "Failed to delete product", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Product Deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        } finally {
            handler.closeConnection();
        }
    }
    
    // save product
    private void handleSave(int categoryID, Product currentProduct) throws InvalidInputException, SQLException {
        // get details from fields
        String productCode = productCodeField.getText().strip();
        String productBrand = productBrandField.getText().strip();
        String productName = productNameField.getText().strip();
        String eraCode = eraCodeField.getText().strip();
        String dccCode = dccCodeField.getText().strip();
        String digital = digitalField.getText().strip();
        String packageContent = (packageContentField.getText().strip());
        String stock = stockField.getText().strip();
        String cost = costField.getText().strip();
        String gauge = gaugeField.getText().strip();

        // basic input checking
        if (productCode.isEmpty() || productBrand.isEmpty() || productName.isEmpty() || gauge.isEmpty() || cost.isEmpty() || stock.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else if (categoryID == 0 && (eraCode.isEmpty() || dccCode.isEmpty())) {
            throw new InvalidInputException("All fields must be filled.");
        } else if (categoryID == 1 && eraCode.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else if (categoryID == 2 && digital.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else if (categoryID == 4 && (eraCode.isEmpty() || packageContent.isEmpty())) {
            throw new InvalidInputException("All fields must be filled.");
        } else if (categoryID == 5 && packageContent.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else {
            try{
                Integer.parseInt(stock);
            } catch (NumberFormatException e){
                throw new InvalidInputException("Stock must be an integer.");
            }
            // create product object
            Product newProduct = new Product(productCode, productBrand, productName, Float.parseFloat(cost), gauge, Integer.parseInt(stock), categoryID, eraCode, dccCode,
                    digital.isEmpty() ? null : digital, packageContent.isEmpty() ? null : packageContent);

            // try to add to database if no errors
            DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
            DatabaseOperations db = new DatabaseOperations();
            try {
                if (currentProduct != null) {
                    // update
                    if (newProduct.toString().equals(currentProduct.toString())) {
                        JOptionPane.showMessageDialog(this, "No changes made");
                        return;
                    }
                    // if product code changed but already in use
                    if (!newProduct.getProductCode().equals(currentProduct.getProductCode())) {
                        if (db.isProductCodeUsed(productCode, handler.openAndGetConnection())) {
                            throw new InvalidInputException("Product code already in use");
                        }
                    }
                    if (!db.updateProduct(newProduct, currentProduct.getProductCode(), handler.openAndGetConnection())) {
                        JOptionPane.showMessageDialog(this, "Failed to update product", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "Product Updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // insert
                    if (!db.insertProduct(newProduct, handler.openAndGetConnection())) {
                        JOptionPane.showMessageDialog(this, "Failed to insert product", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "Product Added", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (InvalidInputException e) {
                throw e;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                handler.closeConnection();
            }
        }
    }
}
