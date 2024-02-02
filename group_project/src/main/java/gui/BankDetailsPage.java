package gui;

import group069.*;
import exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Calendar;

public class BankDetailsPage extends JFrame {
    private JLabel cardNameLabel = new JLabel("Card Name");
    private JTextField cardNameField = new JTextField();
    private JLabel cardNumberLabel = new JLabel("Card Number");
    private JTextField cardNumberField = new JTextField();
    private JLabel expiryDateLabel = new JLabel("Expiry Date");
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JLabel securityCodeLabel = new JLabel("Security Code");
    private JPasswordField securityCodeField = new JPasswordField();
    private JButton saveButton = new JButton("Save");
    private JButton backButton = new JButton("Back");

    public BankDetailsPage() {
        super("Bank Details");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridLayout(5, 2));

        // get current user's bank details
        int userID = Session.getInstance().getCurrentUser().getUserID();
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        String[] date = null;
        try {
            BankDetail bankDetails = db.getBankDetailsByUser(userID, handler.openAndGetConnection());
            if (bankDetails != null) {
                cardNameField.setText(bankDetails.getCardName());
                cardNumberField.setText(bankDetails.getCardNumber());
                String expiryDate = bankDetails.getExpiryDate();
                date = expiryDate.split("/");
                securityCodeField.setText(bankDetails.getSecurityCode());
            }
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }

        // Expiry date panel
        JPanel expiryDatePanel = new JPanel(new FlowLayout());
        String[] month = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        monthComboBox = new JComboBox<String>(month);
        String[] year = new String[20];
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < year.length; i++) {
            year[i] = Integer.toString(currentYear + i);
        }
        yearComboBox = new JComboBox<String>(year);
        if (date != null) {
            monthComboBox.setSelectedItem(date[0]);
            yearComboBox.setSelectedItem(date[1]);
        }
        expiryDatePanel.add(monthComboBox);
        expiryDatePanel.add(yearComboBox);

        this.add(cardNameLabel);
        this.add(cardNameField);
        this.add(cardNumberLabel);
        this.add(cardNumberField);
        this.add(expiryDateLabel);
        this.add(expiryDatePanel);
        this.add(securityCodeLabel);
        this.add(securityCodeField);
        this.add(backButton);
        this.add(saveButton);

        backButton.addActionListener(e -> {
            Utils.redirectMyOrderPage(this);
        });

        saveButton.addActionListener(e -> {
            try {
                handleSave();
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
            }
        });
    }

    private void handleSave() throws InvalidInputException {
        String cardName = cardNameField.getText();
        String cardNumber = cardNumberField.getText();
        String securityCode = new String(securityCodeField.getPassword());
        int month = Integer.parseInt((String) monthComboBox.getSelectedItem());
        int year = Integer.parseInt((String) yearComboBox.getSelectedItem()) - 2000;

        // basic checking
        if (cardName.isEmpty() || cardNumber.isEmpty() || securityCode.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else {
            // save to database
            int userID = Session.getInstance().getCurrentUser().getUserID();
            BankDetail bankDetails = new BankDetail(cardName, cardNumber, month, year, securityCode);
            DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
            DatabaseOperations db = new DatabaseOperations();
            try {
                if (db.isCardNumberUsed(cardNumber, handler.openAndGetConnection())) {
                    JOptionPane.showMessageDialog(this, "Cannot change details of existing valid card", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!db.insertBankDetails(bankDetails, handler.openAndGetConnection())) {
                    JOptionPane.showMessageDialog(this, "Failed to save bank details", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                UserBankDetail userBankDetail = new UserBankDetail(userID, cardNumber);
                if (!db.insertUserBankDetails(userBankDetail, handler.openAndGetConnection())) {
                    JOptionPane.showMessageDialog(this, "Failed to link bank details to user", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                db.deleteOtherUserBankDetails(userID, cardNumber, handler.openAndGetConnection());
                JOptionPane.showMessageDialog(this, "Details saved", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                handler.closeConnection();
            }
        }
        Utils.redirectMyOrderPage(this);
    }
}