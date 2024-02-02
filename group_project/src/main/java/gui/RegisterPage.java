package gui;

import group069.*;
import exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterPage extends JFrame {
    private JLabel emailLabel = new JLabel("Email");
    private JLabel passwordLabel = new JLabel("Password");
    private JLabel confirmPasswordLabel = new JLabel("Confirm Password");
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JPasswordField confirmPasswordField = new JPasswordField();
    private JButton registerButton = new JButton("Register");
    private JButton backButton = new JButton("Back");

    // constructor
    public RegisterPage() {
        super("Register");
        this.setSize(800, 600);
        this.setLayout(new GridLayout(4, 2));
        this.add(emailLabel);
        this.add(emailField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(confirmPasswordLabel);
        this.add(confirmPasswordField);
        this.add(backButton);
        this.add(registerButton);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // register button
        registerButton.addActionListener(e -> {
            try {
                handleRegister();
                Utils.redirectPersonalDetailsPage(this);
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
                return;
            } catch (SQLException ex) {
                return;
            }
        });

        // back button
        backButton.addActionListener(e -> {
            Utils.redirectLoginPage(this);
        });
    }

    // register
    private void handleRegister() throws InvalidInputException, SQLException {
        String email = emailField.getText().strip();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // basic input checking
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new InvalidInputException("Please fill in all fields.");
        } else if (password.length() < 8 || password.length() > 16) {
            throw new InvalidInputException("Password must be between 8 and 16 characters long.");
        } else if (!password.equals(confirmPassword)) {
            throw new InvalidInputException("Password and confirm password do not match.");
        } else {
            // try to add to database if no errors
            User newUser = new User(email, password);
            DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
            DatabaseOperations db = new DatabaseOperations();
            try {
                db.insertUser(newUser, handler.openAndGetConnection());
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw e;
            } finally {
                handler.closeConnection();
            }
            // success message
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // set current user
            Session.getInstance().setCurrentUser(newUser);
        }
    }
}
