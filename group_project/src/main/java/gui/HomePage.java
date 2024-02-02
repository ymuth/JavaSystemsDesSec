package gui;

import group069.*;

import javax.swing.*;
import java.awt.*;

public class HomePage extends JFrame {

    public HomePage() {
        super("Home Page");
        this.setSize(1600, 1200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        JLabel welcomeLabel = new JLabel("Trains of Sheffield Store", SwingConstants.CENTER);
        JButton userProfileButton = new JButton("Profile");
        JButton myOrderButton = new JButton("My Order");
        JButton orderHistoryButton = new JButton("Order History");
        JButton staffButton = new JButton("Staff View");
        JButton logoutButton = new JButton("Logout");

        Session session = Session.getInstance();

        // button panel
        JPanel buttonPanelLeft = new JPanel(new FlowLayout());
        buttonPanelLeft.add(userProfileButton);
        buttonPanelLeft.add(myOrderButton);
        buttonPanelLeft.add(orderHistoryButton);

        JPanel buttonPanelRight = new JPanel(new FlowLayout());
        // only show staff panel button if user is staff or manager
        if (session.getIsStaff() || session.getIsManager()) {
            buttonPanelRight.add(staffButton);
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
                Utils.redirectCustomerCategoryPage(this, categoryID);
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
    
        this.add(buttonPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);

        // logout
        logoutButton.addActionListener(e -> {
            // clear the current user session
            Session.getInstance().logout();
            Utils.redirectLoginPage(this);
        });

        // Redirect to the Personal details page
        userProfileButton.addActionListener(e -> {
            Utils.redirectPersonalDetailsPage(this);
        });

        // Redirect to the My Order page
        myOrderButton.addActionListener(e -> {
            Utils.redirectMyOrderPage(this);
        });

        // Redirect to the My Order History page
        orderHistoryButton.addActionListener(e -> {
            Utils.redirectOrderHistoryPage(this);
        });

        // Redirect to the Staff Home Page
        staffButton.addActionListener(e -> {
            Utils.redirectStaffHomePage(this);
        });
    }
}
