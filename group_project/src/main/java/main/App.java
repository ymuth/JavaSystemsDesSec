package main;

import gui.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            System.out.println(e);
        }
        // show login page
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final LoginPage window = new LoginPage();
                window.setVisible(true);
            }
        });
    }
}
