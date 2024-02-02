package gui;

import group069.*;
import exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PersonalDetailsPage extends JFrame {
    private JLabel emaiLabel = new JLabel("Email");
    private JLabel passwordLabel = new JLabel("Password");
    private JLabel forenameLabel = new JLabel("Forename");
    private JLabel surnameLabel = new JLabel("Surname");
    private JLabel postcodeLabel = new JLabel("Postcode");
    private JLabel houseNumberLabel = new JLabel("House Number");
    private JLabel roadNameLabel = new JLabel("Road Name");
    private JLabel cityNameLabel = new JLabel("City Name");
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField forenameField = new JTextField();
    private JTextField surnameField = new JTextField();
    private JTextField postcodeField = new JTextField();
    private JTextField houseNumberField = new JTextField();
    private JTextField roadNameField = new JTextField();
    private JTextField cityNameField = new JTextField();
    private JButton backButton = new JButton("Back");
    private JButton saveButton = new JButton("Save");

    // constructor
    public PersonalDetailsPage() {
        super("Personal Details");
        this.setSize(800, 600);
        this.setLayout(new GridLayout(9, 2));
        this.add(emaiLabel);
        this.add(emailField);
        this.add(passwordLabel);
        this.add(passwordField);
        this.add(forenameLabel);
        this.add(forenameField);
        this.add(surnameLabel);
        this.add(surnameField);
        this.add(postcodeLabel);
        this.add(postcodeField);
        this.add(houseNumberLabel);
        this.add(houseNumberField);
        this.add(roadNameLabel);
        this.add(roadNameField);
        this.add(cityNameLabel);
        this.add(cityNameField);
        this.add(backButton);
        this.add(saveButton);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        // set fields to current user's details
        User currentUser = Session.getInstance().getCurrentUser();
        emailField.setText(currentUser.getEmail());
        forenameField.setText(currentUser.getForename());
        surnameField.setText(currentUser.getSurname());
        if (currentUser.getAddress() != null) {
            postcodeField.setText(currentUser.getAddress().getPostCode());
            houseNumberField.setText(Integer.toString(currentUser.getAddress().getHouseNumber()));
            roadNameField.setText(currentUser.getAddress().getRoadName());
            cityNameField.setText(currentUser.getAddress().getCityName());
        }

        // check if user is new
        if (forenameField.getText().isEmpty()) {
            backButton.setVisible(false);
        }

        // back button
        backButton.addActionListener(e -> {
            Utils.redirectHomePage(this);
        });

        // save button
        saveButton.addActionListener(e -> {
            // save personal details
            try {
                handleSave();
                Utils.redirectHomePage(this);
            } catch (InvalidInputException ex) {
                ex.displayErrorMessage();
            } catch (SQLException ex) {
                return;
            }
        });
    }

    // save personal details
    private void handleSave() throws InvalidInputException, SQLException {
        // get details from fields
        String email = emailField.getText().strip();
        String password = new String(passwordField.getPassword());
        String forename = forenameField.getText().strip();
        String surname = surnameField.getText().strip();
        String postcode = postcodeField.getText().strip();
        int houseNumber;
        try {
            houseNumber = Integer.parseInt(houseNumberField.getText().strip());
        } catch (NumberFormatException e) {
            houseNumber = 0;
        }
        String roadName = roadNameField.getText().strip();
        String cityName = cityNameField.getText().strip();

        // basic input checking
        if (email.isEmpty() || password.isEmpty() || forename.isEmpty() || surname.isEmpty() || postcode.isEmpty() || roadName.isEmpty() || cityName.isEmpty()) {
            throw new InvalidInputException("All fields must be filled.");
        } else {
            // try to add to database if no errors
            User currentUser = Session.getInstance().getCurrentUser();
            DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
            DatabaseOperations db = new DatabaseOperations();
            try {
                // check password for verification
                if (!Utils.sha256(currentUser.getEmail()+password).equals(currentUser.getPassword())) {
                    throw new InvalidInputException("Incorrect password.");
                }
                // update email and password if changed
                if (!currentUser.getEmail().equals(email)) {
                    currentUser.setEmail(email);
                    currentUser.setPassword(Utils.sha256(email+password));
                }
                currentUser.setForename(forename);
                currentUser.setSurname(surname);
                currentUser.setAddress(roadName, cityName, houseNumber, postcode);
                boolean success = db.updateUser(currentUser, handler.openAndGetConnection());
                if (!success) {
                    JOptionPane.showMessageDialog(this, "Failed to save details", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "Details saved", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } finally {
                handler.closeConnection();
            }
        }
    }
}
