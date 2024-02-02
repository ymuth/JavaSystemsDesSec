package gui;

import group069.*;
import exceptions.*;

import javax.swing.*;
import java.awt.*;

public class StaffHomePage extends JFrame {

    public StaffHomePage() throws PermissionErrorException {
        super("Staff Browse Products Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1600, 1200);
        this.setLocationRelativeTo(null);
        JLabel welcomeLabel = new JLabel("Trains of Sheffield Store", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font(welcomeLabel.getFont().getName(), Font.PLAIN, 30));
        JButton managerButton = new JButton("Manage Staff");
        JButton homePageButton = new JButton("Home Page");
        JButton confirmedOrderButton = new JButton("Confirmed Orders");
        JButton fulfilledOrderButton = new JButton("Fulfilled Orders");
        JButton allOrderButton = new JButton("All Orders");
        JButton logoutButton = new JButton("Logout");

        // check authority
        Session session = Session.getInstance();
        User currentUser = Session.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "You are not logged in.");
            Utils.redirectLoginPage(this);
            throw new PermissionErrorException("User is not logged in.");
        } else if (!session.getIsStaff() && !session.getIsManager()) {
            JOptionPane.showMessageDialog(this, "You are not authorised to view this page.");
            Utils.redirectHomePage(this);
            throw new PermissionErrorException("User is not staff or manager.");
        }

        // button panel
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        buttonPanelLeft.add(homePageButton);
        buttonPanelLeft.add(confirmedOrderButton);
        buttonPanelLeft.add(fulfilledOrderButton);
        buttonPanelLeft.add(allOrderButton);
        JPanel buttonPanelRight = new JPanel(new FlowLayout());
        if (session.getIsManager()) {
            buttonPanelRight.add(managerButton);
        }
        buttonPanelRight.add(logoutButton);
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(buttonPanelLeft, BorderLayout.WEST);
        buttonPanel.add(buttonPanelRight, BorderLayout.EAST);

        // category panel
        JPanel CategoryPanel = new JPanel(new GridLayout(2, 3, 50, 20));
        for (int i = 0; i < Utils.categoryNames.size(); i++) {
            final int categoryID = i;
            JButton categoryButton = new JButton(Utils.categoryNames.get(i));
            categoryButton.setPreferredSize(new Dimension(400, 200));
            categoryButton.addActionListener(e -> {
                Utils.redirectStaffCategoryPage(this, categoryID);
            });
            JPanel ButtonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            ButtonWrapper.add(categoryButton);
            CategoryPanel.add(ButtonWrapper);
        }
        JPanel categoryPanelWrapper = new JPanel(new BorderLayout());
        categoryPanelWrapper.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        categoryPanelWrapper.add(CategoryPanel);

        // content
        JPanel contentPanel = new JPanel(new BorderLayout());
        welcomeLabel.setFont(new Font("Serif", Font.PLAIN, 50));
        contentPanel.add(welcomeLabel, BorderLayout.NORTH);
        contentPanel.add(categoryPanelWrapper, BorderLayout.CENTER);

        // add to frame
        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);

        // logout
        logoutButton.addActionListener(e -> {
            // clear the current user session
            Session.getInstance().logout();
            Utils.redirectLoginPage(this);
        });

        // Redirect to the ManageStaff page
        managerButton.addActionListener(e -> {
            Utils.redirectManageStaffPage(this);
        });

        // Redirect to the Home Page
        homePageButton.addActionListener(e -> {
            Utils.redirectHomePage(this);
        });

        // Redirect to the Confirmed Order Page
        confirmedOrderButton.addActionListener(e -> {
            Utils.redirectOrderHistoryPage(this, Order.Status.CONFIRMED);
        });

        // Redirect to the Fulfilled Order Page
        fulfilledOrderButton.addActionListener(e -> {
            Utils.redirectOrderHistoryPage(this, Order.Status.FULFILLED);
        });

        // Redirect to the All Orders Page
        allOrderButton.addActionListener(e -> {
            Utils.redirectOrderHistoryPage(this, Order.Status.ALL);
        });
    }

    public static void main(String[] args) throws PermissionErrorException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    StaffHomePage staffHomePage = new StaffHomePage();
                    staffHomePage.setVisible(true);
                } catch (PermissionErrorException e) {
                    return;
                }
            }
        });
    }
}
