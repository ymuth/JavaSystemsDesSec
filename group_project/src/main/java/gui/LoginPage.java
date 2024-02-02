package gui;

import group069.*;
import exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JLabel emailLabel = new JLabel("Email");
    private JLabel passwordLabel = new JLabel("Password");
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");

    public LoginPage() {
        super("Login");
        this.setSize(800, 600);
        this.setLayout(new GridLayout(3, 2));
        this.add(emailLabel);
        this.add(emailField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(registerButton);
        this.add(loginButton);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // login button
        loginButton.addActionListener(e -> {
            try {
                handleLogin();
                Utils.redirectHomePage(this);
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
                return;
            } catch (SQLException ex) {
                return;
            }
        });

        // register button
        registerButton.addActionListener(e -> {
            Utils.redirectRegisterPage(this);
        });
    }

    private void handleLogin() throws InvalidInputException, SQLException {
        String email = emailField.getText().strip();
        String password = new String(passwordField.getPassword());
        String hashedPassword = Utils.sha256(email + password);

        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            User user = db.getUserByEmail(email, handler.openAndGetConnection());
            if (user == null) {
                throw new InvalidInputException("Email not found");
            } else if (!user.getPassword().equals(hashedPassword)) {
                throw new InvalidInputException("Incorrect password");
            }
            Session.getInstance().setCurrentUser(user);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            throw ex;
        } finally {
            handler.closeConnection();
        }        
    }
}