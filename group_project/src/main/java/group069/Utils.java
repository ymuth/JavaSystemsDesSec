package group069;

import gui.*;
import exceptions.*;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.security.MessageDigest;

import javax.swing.*;
import java.awt.*;

public class Utils {

    // hashmap of categoryID to category name
    public static final HashMap<Integer, String> categoryNames = new HashMap<Integer, String>() {
        {
            put(0, "Locomotives");
            put(1, "Rolling Stocks");
            put(2, "Controllers");
            put(3, "Track Pieces");
            put(4, "Train Sets");
            put(5, "Track Packs");
        }
    };

    // SHA256 hash
    public static String sha256(final String base) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i : hash) {
                final String hex = Integer.toHexString(0xff & i);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Get current date as unix timestamp
    public static long getCurrentUnixTimestamp() {
        return new Date().getTime();
    }

    // Convert month year to date
    public static Date convertMonthYearToDate(int month, int year) throws InvalidInputException {
        if (month < 1 || month > 12) {
            throw new InvalidInputException("Invalid month " + month + ".");
        }
        Calendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year + 2000);
        Date date = calendar.getTime();
        return date;
    }

    // Convert "MM/YY" string to date
    public static Date convertMonthYearToDate(String monthYear) throws InvalidInputException {
        String[] monthYearArray = monthYear.split("/");
        int month = Integer.parseInt(monthYearArray[0]);
        int year = Integer.parseInt(monthYearArray[1]);
        return convertMonthYearToDate(month, year);
    }

    // Convert date to "MM/YY" string
    public static String convertDateToMonthYear(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR) - 2000;
        String monthString = Integer.toString(month);
        String yearString = Integer.toString(year);
        if (monthString.length() == 1) {
            monthString = "0" + monthString;
        }
        if (yearString.length() == 1) {
            yearString = "0" + yearString;
        }
        return monthString + "/" + yearString;
    }

    // Redirect to the Staff page
    public static void redirectStaffHomePage(Window currentPage) {
        System.out.println("Redirecting to StaffHomePage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final StaffHomePage window = new StaffHomePage();
                    window.setVisible(true);
                } catch (PermissionErrorException e) {
                    e.printStackTrace();
                    e.displayErrorMessage();
                }
            }
        });
        currentPage.dispose();
    }

    // Redirect to the ManageStaff page
    public static void redirectManageStaffPage(Window currentPage) {
        System.out.println("Redirecting to ManageStaffPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final ManageStaffPage window = new ManageStaffPage();
                    window.setVisible(true);
                } catch (PermissionErrorException e) {
                    e.printStackTrace();
                    e.displayErrorMessage();
                }
            }
        });
        currentPage.dispose();
    }

    // Redirect to the Home page
    public static void redirectHomePage(Window currentPage) {
        System.out.println("Redirecting to HomePage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final HomePage window = new HomePage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    // Redirect to the Login page
    public static void redirectLoginPage(Window currentPage) {
        System.out.println("Redirecting to LoginPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final LoginPage window = new LoginPage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    // Redirect to the Register page
    public static void redirectRegisterPage(Window currentPage) {
        System.out.println("Redirecting to RegisterPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final RegisterPage window = new RegisterPage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    // Redirect to the PersonalDetails page
    public static void redirectPersonalDetailsPage(Window currentPage) {
        System.out.println("Redirecting to PersonalDetailsPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final PersonalDetailsPage window = new PersonalDetailsPage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    // Redirect to ViewCategoryPage
    public static void redirectCustomerCategoryPage(Window currentPage, int categoryID) {
        System.out.println("Redirecting to ViewCategoryPage, viewing category " + categoryID + ", " + categoryNames.get(categoryID) + ".");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final CustomerCategoryPage window = new CustomerCategoryPage(categoryID);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectMyOrderPage(Window currentPage) {
        System.out.println("Redirecting to MyOrderPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final MyOrderPage window = new MyOrderPage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectOrderHistoryPage(Window currentPage) {
        System.out.println("Redirecting to OrderHistoryPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final OrderHistoryPage window = new OrderHistoryPage(currentPage);
                window.setVisible(true);
            }
        });
        currentPage.setVisible(false);
    }

    public static void redirectOrderHistoryPage(Window currentPage, Order.Status status) {
        System.out.println("Redirecting to OrderHistoryPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final OrderHistoryPage window = new OrderHistoryPage(currentPage, status);
                window.setVisible(true);
            }
        });
        currentPage.setVisible(false);
    }

    public static void redirectOrderHistoryPage(Window currentPage, Order.Status status, boolean back) {
        System.out.println("Redirecting to OrderHistoryPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final OrderHistoryPage window = new OrderHistoryPage(currentPage, status, back);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectOrderDetailsPage(Window currentPage, int orderID) {
        System.out.println("Redirecting to OrderDetailsPage viewing order " + orderID);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final OrderDetailsPage window = new OrderDetailsPage(currentPage, orderID);
                window.setVisible(true);
            }
        });
        currentPage.setVisible(false);
    }

    public static void redirectOrderDetailsPage(Window currentPage, int orderID, boolean isStaff) {
        System.out.println("Redirecting to OrderDetailsPage viewing order " + orderID);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final OrderDetailsPage window = new OrderDetailsPage(currentPage, orderID, isStaff);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectBankDetailsPage(Window currentPage) {
        System.out.println("Redirecting to BankDetailsPage");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final BankDetailsPage window = new BankDetailsPage();
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectStaffCategoryPage(Window currentPage, int categoryID) {
        System.out.println("Redirecting to StaffCategoryPage, viewing category " + categoryID + ", " + categoryNames.get(categoryID));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final StaffCategoryPage window = new StaffCategoryPage(categoryID);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectAddProductPage(Window currentPage, int categoryID) {
        System.out.println("Redirecting to AddProductPage, adding product to category " + categoryID + ", " + categoryNames.get(categoryID) + ".");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final ProductDetailsPage window = new ProductDetailsPage(categoryID, null);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }

    public static void redirectAddProductPage(Window currentPage, int categoryID, Product product) {
        System.out.println("Redirecting to ProductDetailsPage, viewing product " + product.getProductCode());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final ProductDetailsPage window = new ProductDetailsPage(categoryID, product);
                window.setVisible(true);
            }
        });
        currentPage.dispose();
    }
}
