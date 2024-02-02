package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.sql.SQLException;

public class ManageStaffPage extends JFrame {

    public ManageStaffPage() throws PermissionErrorException {
        // init
        super("Manager Panel");
        this.setSize(1600, 1200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        JButton homePageButton = new JButton("Home Page");
        JButton staffViewButton = new JButton("Staff View");
        JButton logoutButton = new JButton("Logout");
        JLabel emailLabel = new JLabel("Email: ");
        JTextField emailField = new JTextField(20);
        JButton addStaffButton = new JButton("Promote to Staff");

        // check authority
        Session session = Session.getInstance();
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You are not logged in.");
            Utils.redirectLoginPage(this);
            throw new PermissionErrorException("User is not logged in.");
        } else if (!session.getIsManager()) {
            JOptionPane.showMessageDialog(this, "You are not authorised to view this page.");
            Utils.redirectHomePage(this);
            throw new PermissionErrorException("User is not manager.");
        }

        // button panel
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        buttonPanelLeft.add(homePageButton);
        JPanel buttonPanelRight = new JPanel(new FlowLayout());
        buttonPanelRight.add(staffViewButton);
        buttonPanelRight.add(logoutButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        buttonPanel.add(buttonPanelRight, BorderLayout.EAST);

        // promote panel
        JPanel promotePanel = new JPanel(new FlowLayout());
        promotePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
        emailField.setPreferredSize(new Dimension(WIDTH, 25));
        addStaffButton.setPreferredSize(new Dimension(200, 25));
        promotePanel.add(emailLabel);
        promotePanel.add(emailField);
        promotePanel.add(addStaffButton);

        // staff list
        JPanel staffListPanel = new JPanel();
        staffListPanel.setLayout(new BoxLayout(staffListPanel, BoxLayout.Y_AXIS));
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            ArrayList<User> staffList = db.getUsersByRole(1, handler.openAndGetConnection());
            handler.closeConnection();
            for (User staff : staffList) {
                // row init
                JLabel staffID = new JLabel(Integer.toString(staff.getUserID()));
                JLabel staffName = new JLabel(staff.getForename() + " " + staff.getSurname());
                JLabel staffEmailLabel = new JLabel(staff.getEmail());
                JButton removeButton = new JButton("Remove Staff");

                // staffInfo panel
                JPanel staffInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
                staffInfoPanel.setSize(WIDTH, 50);
                staffID.setPreferredSize(new Dimension(50, 50));
                staffID.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                staffName.setPreferredSize(new Dimension(200, 50));
                staffName.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                staffEmailLabel.setPreferredSize(new Dimension(200, 50));
                staffEmailLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.BLACK));
                staffInfoPanel.add(staffID);
                staffInfoPanel.add(staffName);
                staffInfoPanel.add(staffEmailLabel);

                // row panel
                JPanel staffRowPanel = new JPanel(new BorderLayout());
                staffRowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                staffRowPanel.setMinimumSize(new Dimension(Integer.MIN_VALUE, 50));
                staffRowPanel.setPreferredSize(new Dimension(staffRowPanel.getPreferredSize().width, 50));
                staffRowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
                staffRowPanel.add(staffInfoPanel, BorderLayout.WEST);
                removeButton.setPreferredSize(new Dimension(150, 50));
                staffRowPanel.add(removeButton, BorderLayout.EAST);

                // add row to staffList
                staffListPanel.add(staffRowPanel);

                // remove staff button
                removeButton.addActionListener(e -> {
                    try {
                        boolean success = db.deleteUserRole(staff.getUserID(), 1, handler.openAndGetConnection());
                        if (!success) {
                            JOptionPane.showMessageDialog(this, "Failed to remove staff.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        JOptionPane.showMessageDialog(this, "Staff removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        Utils.redirectManageStaffPage(this);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        handler.closeConnection();
                    }
                });
            }
        } catch (InvalidInputException e) {
            e.printStackTrace();
            e.displayErrorMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            handler.closeConnection();
        }

        // content panel (scroll)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(promotePanel, BorderLayout.NORTH);
        contentPanel.add(staffListPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // add panels to frame
        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);

        // add staff
        addStaffButton.addActionListener(e -> {
            String email = emailField.getText().strip();
            // email regex
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Invalid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                User user = db.getUserByEmail(email, handler.openAndGetConnection());
                handler.closeConnection();
                if (user == null) {
                    JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (db.hasRole(user.getUserID(), 1, handler.openAndGetConnection())) {
                        JOptionPane.showMessageDialog(this, "User is already staff.", "Error", JOptionPane.ERROR_MESSAGE);
                        handler.closeConnection();
                        return;
                    }
                    boolean success = db.insertUserRole(new UserRole(user.getUserID(), 1), handler.openAndGetConnection());
                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Failed to promote user to staff.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "User promoted to staff.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    Utils.redirectManageStaffPage(this);
                }
            } catch (InvalidInputException ex) {
                ex.printStackTrace();
                ex.displayErrorMessage();
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                handler.closeConnection();
            }
        });

        // logout
        logoutButton.addActionListener(e -> {
            // clear the current user session
            Session.getInstance().logout();
            Utils.redirectLoginPage(this);
        });

        // Redirect to the Staff Page
        staffViewButton.addActionListener(e -> {
            Utils.redirectStaffHomePage(this);
        });

        // Redirect to the Home Page
        homePageButton.addActionListener(e -> {
            Utils.redirectHomePage(this);
        });
    }

    public static void main(String[] args) throws PermissionErrorException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    ManageStaffPage manageStaffPage = new ManageStaffPage();
                    manageStaffPage.setVisible(true);
                } catch (PermissionErrorException e) {
                    return;
                }
            }
        });
    }
}
