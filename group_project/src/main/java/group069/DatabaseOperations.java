package group069;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import exceptions.InvalidInputException;

public class DatabaseOperations {
    // Insert a new user into the database
    public void insertUser(User newUser, Connection connection) throws SQLException {
        // check if user exists
        if (isEmailUsed(newUser.getEmail(), connection)) {
            throw new SQLException("Email already in use.");
        }

        // Insert user with only email and password
        String insertSQL = "INSERT INTO Users (email, password) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newUser.getEmail());
        preparedStatement.setString(2, newUser.getPassword());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertUser.");
    }

    // Update an existing user in the database
    public boolean updateUser(User user, Connection connection) throws SQLException {
        String updateSQL = "UPDATE Users SET email=?, password=?, forename=?, surname=?, addressID=? WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setString(1, user.getEmail());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setString(3, user.getForename());
        preparedStatement.setString(4, user.getSurname());
        preparedStatement.setString(5, user.getAddress().getAddressID());
        preparedStatement.setInt(6, user.getUserID());

        // Insert new address if not found, and delete old address if not used
        insertAddress(user.getAddress(), connection);
        int rowsAffected = preparedStatement.executeUpdate();
        deleteAddress(user.getAddress().getAddressID(), connection);

        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) updated successfully in updateUser.");
        } else {
            System.out.println("No rows were updated for userID: " + user.getUserID());
        }
        return rowsAffected > 0;
    }

    // Delete a User from the database by userID
    public void deleteUser(int userID, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM Users WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, userID);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteUser.");
            // Delete address if not used
            deleteAddress(deleteSQL, connection);
        } else {
            System.out.println("No rows were deleted for userID: " + userID);
        }
    }

    // Get all Users from the database
    public ArrayList<User> getAllUsers(Connection connection) throws SQLException, InvalidInputException {
        // Create an list of users
        ArrayList<User> users = new ArrayList<User>();
        String selectSQL = "SELECT * FROM Users";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into User object
            users.add(new User(resultSet.getInt("userID"), resultSet.getString("email"), resultSet.getString("password"),
                    resultSet.getString("forename"), resultSet.getString("surname"), resultSet.getString("addressID")));
        }
        return users;
    }

    // Return a User by userID
    public User getUserByID(int userID, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Users WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into User object
            return new User(resultSet.getInt("userID"), resultSet.getString("email"), resultSet.getString("password"),
                    resultSet.getString("forename"), resultSet.getString("surname"), resultSet.getString("addressID"));
        } else {
            return null;
        }
    }

    // Get a User by email
    public User getUserByEmail(String email, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Users WHERE email=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into User object
            return new User(resultSet.getInt("userID"), resultSet.getString("email"), resultSet.getString("password"),
                    resultSet.getString("forename"), resultSet.getString("surname"), resultSet.getString("addressID"));
        } else {
            return null;
        }
    }

    // Check if email is used
    public boolean isEmailUsed(String email, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Users WHERE email=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, email);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Check if userID is valid
    public boolean isValidUserID(int userID, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Users WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Return roleName by roleID
    public String getRoleNameByID(int roleID, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Roles WHERE roleID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, roleID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("roleName");
        } else {
            return null;
        }
    }

    // Add a new role to a user into the UserRole table
    public boolean insertUserRole(UserRole newUserRole, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO UserRoles (userID, roleID) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, newUserRole.getUserID());
        preparedStatement.setInt(2, newUserRole.getRoleID());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertUserRole.");
        return rowsAffected > 0;
    }

    // Delete a role from a user from the UserRole table
    public boolean deleteUserRole(int userID, int roleID, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM UserRoles WHERE userID=? AND roleID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, userID);
        preparedStatement.setInt(2, roleID);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteUserRole.");
            return true;
        } else {
            System.out.println("No rows were deleted for userID: " + userID + " and roleID: " + roleID);
            return false;
        }
    }

    // Return all Users from a Role from the UserRole table
    public ArrayList<User> getUsersByRole(int roleID, Connection connection) throws SQLException, InvalidInputException {
        // Create an list of users
        ArrayList<User> users = new ArrayList<User>();
        String selectSQL = "SELECT * FROM UserRoles WHERE roleID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, roleID);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into User object
            users.add(getUserByID(resultSet.getInt("userID"), connection));
        }
        return users;
    }

    // Get all Roles from a User from the UserRole table
    public ArrayList<Role> getRolesByUser(int userID, Connection connection) throws SQLException {
        // Create a list of roles
        ArrayList<Role> roles = new ArrayList<Role>();
        String selectSQL = "SELECT * FROM UserRoles WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Role object
            roles.add(new Role(resultSet.getInt("roleID"), getRoleNameByID(resultSet.getInt("roleID"), connection)));
        }
        return roles;
    }

    // Check if User has Role
    public boolean hasRole(int userID, int roleID, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM UserRoles WHERE userID=? AND roleID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);
        preparedStatement.setInt(2, roleID);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Insert a new address into the database
    public void insertAddress(Address newAddress, Connection connection) throws SQLException {
        // Stop if address is present
        if (isValidAddressID(newAddress.getAddressID(), connection)) {
            return;
        }

        String insertSQL = "INSERT INTO Addresses (addressID, roadName, cityName, houseNumber, postCode) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newAddress.getAddressID());
        preparedStatement.setString(2, newAddress.getRoadName());
        preparedStatement.setString(3, newAddress.getCityName());
        preparedStatement.setInt(4, newAddress.getHouseNumber());
        preparedStatement.setString(5, newAddress.getPostCode());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertAddress.");
    }

    // Delete an address from the database by addressID
    public void deleteAddress(String addressID, Connection connection) throws SQLException {
        // Stop if address is used
        if (isAddressUsed(addressID, connection)) {
            return;
        }

        String deleteSQL = "DELETE FROM Addresses WHERE addressID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setString(1, addressID);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteAddress.");
        } else {
            System.out.println("No rows were deleted for addressID: " + addressID);
        }
    }

    // Get all Addresses from the database
    public ArrayList<Address> getAllAddresses(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of addresses
        ArrayList<Address> addresses = new ArrayList<Address>();
        String selectSQL = "SELECT * FROM Addresses";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Address object
            addresses.add(new Address(resultSet.getString("roadName"), resultSet.getString("cityName"), resultSet.getInt("houseNumber"),
                    resultSet.getString("postCode")));
        }
        return addresses;
    }

    // Get an Address by addressID
    public Address getAddressByID(String addressID, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Addresses WHERE addressID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, addressID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into Address object
            return new Address(resultSet.getString("roadName"), resultSet.getString("cityName"), resultSet.getInt("houseNumber"),
                    resultSet.getString("postCode"));
        } else {
            System.out.println("Address with addressID " + addressID + " not found.");
            return null;
        }
    }

    // Check if addressID is valid
    public boolean isValidAddressID(String addressID, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Addresses WHERE addressID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, addressID);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Check if any user has addressID
    public boolean isAddressUsed(String addressID, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Users WHERE addressID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, addressID);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Insert a new bank details into the database
    public boolean insertBankDetails(BankDetail newBankDetails, Connection connection) throws SQLException {
        // Stop if card number is present
        if (isValidCardNumber(newBankDetails.getCardNumber(), connection)) {
            return false;
        }

        String insertSQL = "INSERT INTO BankDetails (cardNumber, cardName, expiryDate, securityCode) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newBankDetails.getCardNumber());
        preparedStatement.setString(2, newBankDetails.getCardName());
        preparedStatement.setString(3, newBankDetails.getExpiryDate());
        preparedStatement.setString(4, newBankDetails.getSecurityCode());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertBankDetails.");
        if (rowsAffected > 0) {
            return true;
        } else {
            throw new SQLException("insert BankDetails failed.");
        }
    }

    // Delete a bank details from the database by cardNumber
    public boolean deleteBankDetails(String cardNumber, Connection connection) throws SQLException {
        // Stop if card number is used
        if (isCardNumberUsed(cardNumber, connection)) {
            return false;
        }

        String deleteSQL = "DELETE FROM BankDetails WHERE cardNumber=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setString(1, cardNumber);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteBankDetails.");
        } else {
            System.out.println("No rows were deleted for cardNumber: " + cardNumber);
        }
        return rowsAffected > 0;
    }

    // Get all BankDetails from the database
    public ArrayList<BankDetail> getAllBankDetails(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of bank details
        ArrayList<BankDetail> bankDetails = new ArrayList<BankDetail>();
        String selectSQL = "SELECT * FROM BankDetails";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into BankDetails object
            bankDetails.add(new BankDetail(resultSet.getString("cardName"), resultSet.getString("cardNumber"), resultSet.getString("expiryDate"),
                    resultSet.getString("securityCode")));
        }
        return bankDetails;
    }

    // Get a BankDetails by cardNumber
    public BankDetail getBankDetailsByCardNumber(String cardNumber, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM BankDetails WHERE cardNumber=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, cardNumber);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into BankDetails object
            return new BankDetail(resultSet.getString("cardName"), resultSet.getString("cardNumber"), resultSet.getString("expiryDate"),
                    resultSet.getString("securityCode"));
        } else {
            System.out.println("BankDetails with cardNumber " + cardNumber + " not found.");
            return null;
        }
    }

    // Check if cardNumber is valid
    public boolean isValidCardNumber(String cardNumber, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM BankDetails WHERE cardNumber=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, cardNumber);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Add a new bank details to a user into the UserBankDetails table
    public boolean insertUserBankDetails(UserBankDetail newUserBankDetails, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO UserBankDetails (userID, cardNumber) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, newUserBankDetails.getUserID());
        preparedStatement.setString(2, newUserBankDetails.getCardNumber());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertUserBankDetails.");
        return rowsAffected > 0;
    }

    // Delete a bank details of a user from the UserBankDetails table
    public boolean deleteUserBankDetails(int userID, String cardNumber, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM UserBankDetails WHERE userID=? AND cardNumber=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, cardNumber);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteUserBankDetails.");
            return true;
        } else {
            System.out.println("No rows were deleted for userID: " + userID + " and cardNumber: " + cardNumber);
            return false;
        }
    }

    // Return the BankDetails of a User from the UserBankDetails table
    public BankDetail getBankDetailsByUser(int userID, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT b.* FROM UserBankDetails u INNER JOIN BankDetails b ON u.cardNumber=b.cardNumber WHERE u.userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into BankDetails object
            return new BankDetail(resultSet.getString("cardName"), resultSet.getString("cardNumber"), resultSet.getString("expiryDate"),
                    resultSet.getString("securityCode"));
        } else {
            System.out.println("BankDetails for userID " + userID + " not found.");
            return null;
        }
    }

    // Delete BankDetails of User other than the one specified
    public void deleteOtherUserBankDetails(int userID, String cardNumber, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM UserBankDetails WHERE userID=? AND cardNumber!=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, cardNumber);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteOtherUserBankDetails.");
        } else {
            System.out.println("No rows were deleted for userID: " + userID + " and cardNumber: " + cardNumber);
        }
    }

    // Check if BankDetails is used
    public boolean isCardNumberUsed(String cardNumber, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM UserBankDetails WHERE cardNumber=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, cardNumber);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Insert a new product into the database
    public boolean insertProduct(Product newProduct, Connection connection) throws SQLException, InvalidInputException {
        // Stop if product exists
        if (isProductCodeUsed(newProduct.getProductCode(), connection)) {
            throw new InvalidInputException("Product code already in use.");
        }

        String insertSQL = "INSERT INTO Products (productCode, productBrand, productName, cost, gauge, stock, categoryID, eraCode, dccCode, digital, packageContent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setString(1, newProduct.getProductCode());
        preparedStatement.setString(2, newProduct.getProductBrand());
        preparedStatement.setString(3, newProduct.getProductName());
        preparedStatement.setFloat(4, newProduct.getCost());
        preparedStatement.setString(5, newProduct.getGauge());
        preparedStatement.setInt(6, newProduct.getStock());
        preparedStatement.setInt(7, newProduct.getCategoryID());
        preparedStatement.setString(8, newProduct.getEraCode());
        preparedStatement.setString(9, newProduct.getDccCode());
        if (newProduct.isDigital() == null) {
            preparedStatement.setNull(10, java.sql.Types.NULL);
        } else {
            preparedStatement.setBoolean(10, newProduct.isDigital());
        }
        preparedStatement.setString(11, newProduct.getPackageContent());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertProduct.");
        return rowsAffected > 0;
    }

    // Update an existing product in the database
    public boolean updateProduct(Product product, String oldProductCode, Connection connection) throws SQLException {
        String updateSQL = "UPDATE Products SET productBrand=?, productName=?, cost=?, gauge=?, stock=?, categoryID=?, eraCode=?, dccCode=?, digital=?, packageContent=?, productCode=? WHERE productCode=?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setString(1, product.getProductBrand());
        preparedStatement.setString(2, product.getProductName());
        preparedStatement.setFloat(3, product.getCost());
        preparedStatement.setString(4, product.getGauge());
        preparedStatement.setInt(5, product.getStock());
        preparedStatement.setInt(6, product.getCategoryID());
        preparedStatement.setString(7, product.getEraCode());
        preparedStatement.setString(8, product.getDccCode());
        if (product.isDigital() == null) {
            preparedStatement.setNull(9, java.sql.Types.NULL);
        } else {
            preparedStatement.setBoolean(9, product.isDigital());
        }
        preparedStatement.setString(10, product.getPackageContent());
        preparedStatement.setString(11, product.getProductCode());
        preparedStatement.setString(12, oldProductCode);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) updated successfully in updateProduct.");
        } else {
            System.out.println("No rows were updated for productCode: " + product.getProductCode());
        }
        return rowsAffected > 0;
    }

    // Delete a product from the database by productCode
    public boolean deleteProduct(String productCode, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM Products WHERE productCode=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setString(1, productCode);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteProduct.");
        } else {
            System.out.println("No rows were deleted for productCode: " + productCode);
        }
        return rowsAffected > 0;
    }

    // Get all Products from the database
    public ArrayList<Product> getAllProducts(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of products
        ArrayList<Product> products = new ArrayList<Product>();
        String selectSQL = "SELECT * FROM Products";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Product object
            products.add(new Product(resultSet.getString("productCode"), resultSet.getString("productBrand"), resultSet.getString("productName"),
                    resultSet.getFloat("cost"), resultSet.getString("gauge"), resultSet.getInt("stock"), resultSet.getInt("categoryID"),
                    resultSet.getString("eraCode"), resultSet.getString("dccCode"), resultSet.getString("digital"),
                    resultSet.getString("packageContent")));
        }
        return products;
    }

    // Get all Products from the database by categoryID
    public ArrayList<Product> getProductsByCategory(int categoryID, Connection connection) throws SQLException, InvalidInputException {
        // Create a list of products
        ArrayList<Product> products = new ArrayList<Product>();
        String selectSQL = "SELECT * FROM Products WHERE categoryID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, categoryID);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Product object
            products.add(new Product(resultSet.getString("productCode"), resultSet.getString("productBrand"), resultSet.getString("productName"),
                    resultSet.getFloat("cost"), resultSet.getString("gauge"), resultSet.getInt("stock"), resultSet.getInt("categoryID"),
                    resultSet.getString("eraCode"), resultSet.getString("dccCode"), resultSet.getString("digital"),
                    resultSet.getString("packageContent")));
        }
        return products;
    }

    // Get a Product by productCode
    public Product getProductByProductCode(String productCode, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Products WHERE productCode=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, productCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into Product object
            return new Product(resultSet.getString("productCode"), resultSet.getString("productBrand"), resultSet.getString("productName"),
                    resultSet.getFloat("cost"), resultSet.getString("gauge"), resultSet.getInt("stock"), resultSet.getInt("categoryID"),
                    resultSet.getString("eraCode"), resultSet.getString("dccCode"), resultSet.getString("digital"),
                    resultSet.getString("packageContent"));
        } else {
            System.out.println("Product with productCode " + productCode + " not found.");
            return null;
        }
    }

    // Check if Product already exists
    public boolean isProductCodeUsed(String productCode, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Products WHERE productCode=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, productCode);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Insert a new order into the database
    public boolean insertOrder(Order newOrder, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO Orders (userID, date, status, orderQueueNo) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, newOrder.getUserID());
        preparedStatement.setLong(2, newOrder.getDate().getTime());
        preparedStatement.setString(3, newOrder.getStatus().toString());
        int orderQueueNo = newOrder.getOrderQueueNo();
        if (orderQueueNo == -1) {
            preparedStatement.setNull(4, java.sql.Types.INTEGER);
        } else {
            preparedStatement.setInt(4, orderQueueNo);
        }

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertOrder.");
        return rowsAffected > 0;
    }

    // Update an existing order in the database
    public boolean updateOrder(Order order, Connection connection) throws SQLException {
        String updateSQL = "UPDATE Orders SET userID=?, date=?, status=?, orderQueueNo=? WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setInt(1, order.getUserID());
        preparedStatement.setLong(2, order.getDate().getTime());
        preparedStatement.setString(3, order.getStatus().toString());
        int orderQueueNo = order.getOrderQueueNo();
        if (orderQueueNo == -1) {
            preparedStatement.setNull(4, java.sql.Types.INTEGER);
        } else {
            preparedStatement.setInt(4, orderQueueNo);
        }
        preparedStatement.setInt(5, order.getOrderNo());

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) updated successfully in updateOrder.");
        } else {
            System.out.println("No rows were updated for orderNo: " + order.getOrderNo());
        }
        return rowsAffected > 0;
    }

    // Delete an order from the database by orderNo
    public boolean deleteOrder(int orderNo, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM Orders WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, orderNo);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteOrder.");
        } else {
            System.out.println("No rows were deleted for orderNo: " + orderNo);
        }
        return rowsAffected > 0;
    }

    // Get all Orders from the database
    public ArrayList<Order> getAllOrders(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of orders
        ArrayList<Order> orders = new ArrayList<Order>();
        String selectSQL = "SELECT * FROM Orders";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Order object
            orders.add(new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"),
                    resultSet.getString("orderQueueNo"), Order.Status.valueOf(resultSet.getString("status"))));
        }
        return orders;
    }

    // Get all Orders from the database by userID
    public ArrayList<Order> getOrdersByUserID(int userID, Connection connection) throws SQLException, InvalidInputException {
        // Create a list of orders
        ArrayList<Order> orders = new ArrayList<Order>();
        String selectSQL = "SELECT * FROM Orders WHERE userID=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Order object
            orders.add(new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"),
                    resultSet.getString("orderQueueNo"), Order.Status.valueOf(resultSet.getString("status"))));
        }
        return orders;
    }

    // Get all Orders from the database by status
    public ArrayList<Order> getOrdersByStatus(Order.Status status, Connection connection) throws SQLException, InvalidInputException {
        // Create a list of orders
        ArrayList<Order> orders = new ArrayList<Order>();
        String selectSQL = "SELECT * FROM Orders WHERE status=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, status.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Order object
            orders.add(new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"),
                    resultSet.getString("orderQueueNo"), Order.Status.valueOf(resultSet.getString("status"))));
        }
        return orders;
    }

    // Get confirmed Orders sorted according to orderQueueNo
    public ArrayList<Order> getConfirmedOrders(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of orders
        ArrayList<Order> orders = new ArrayList<Order>();
        String selectSQL = "SELECT * FROM Orders WHERE status=? ORDER BY orderQueueNo ASC";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setString(1, Order.Status.CONFIRMED.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into Order object
            orders.add(new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"),
                    resultSet.getString("orderQueueNo"), Order.Status.valueOf(resultSet.getString("status"))));
        }
        return orders;
    }

    // Get an Order by orderNo
    public Order getOrderByOrderNo(int orderNo, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Orders WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into Order object
            return new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"), resultSet.getString("orderQueueNo"),
                    Order.Status.valueOf(resultSet.getString("status")));
        } else {
            System.out.println("Order with orderNo " + orderNo + " not found.");
            return null;
        }
    }

    // Get the pending Order of a User
    public Order getPendingOrderByUserID(int userID, Connection connection) throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM Orders WHERE userID=? AND status=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, userID);
        preparedStatement.setString(2, Order.Status.PENDING.toString());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into Order object
            return new Order(resultSet.getInt("orderNo"), resultSet.getInt("userID"), resultSet.getLong("date"), resultSet.getString("orderQueueNo"),
                    Order.Status.valueOf(resultSet.getString("status")));
        } else {
            System.out.println("Pending order for userID " + userID + " not found.");
            return null;
        }
    }

    // Check if orderNo is valid
    public boolean isValidOrderNo(int orderNo, Connection connection) throws SQLException {
        String selectSQL = "SELECT * FROM Orders WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet.next();
    }

    // Get total cost of an Order
    public float getTotalCostOfOrder(int orderNo, Connection connection) throws SQLException {
        String selectSQL = "SELECT SUM(totalCost) FROM OrderLines WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getFloat(1);
        } else {
            return -1;
        }
    }

    // Insert a new order line into the database
    public boolean insertOrderLine(OrderLine newOrderLine, Connection connection) throws SQLException {
        String insertSQL = "INSERT INTO OrderLines (orderNo, orderLineNo, productCode, quantity, totalCost) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.setInt(1, newOrderLine.getOrderNo());
        preparedStatement.setInt(2, newOrderLine.getOrderLineNo());
        preparedStatement.setString(3, newOrderLine.getProductCode());
        preparedStatement.setInt(4, newOrderLine.getQuantity());
        preparedStatement.setFloat(5, newOrderLine.getTotalCost());

        int rowsAffected = preparedStatement.executeUpdate();
        System.out.println(rowsAffected + " row(s) inserted successfully in insertOrderLine.");
        return rowsAffected > 0;
    }

    // Update an existing order line in the database
    public boolean updateOrderLine(OrderLine orderLine, Connection connection) throws SQLException {
        String updateSQL = "UPDATE OrderLines SET quantity=?, totalCost=? WHERE orderNo=? AND orderLineNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
        preparedStatement.setInt(1, orderLine.getQuantity());
        preparedStatement.setFloat(2, orderLine.getTotalCost());
        preparedStatement.setInt(3, orderLine.getOrderNo());
        preparedStatement.setInt(4, orderLine.getOrderLineNo());

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) updated successfully in updateOrderLine.");
        } else {
            System.out.println("No rows were updated for orderLineNo: " + orderLine.getOrderLineNo());
        }
        return rowsAffected > 0;
    }

    // Delete an order line from the database by orderNo and orderLineNo
    public boolean deleteOrderLine(int orderNo, int orderLineNo, Connection connection) throws SQLException {
        String deleteSQL = "DELETE FROM OrderLines WHERE orderNo=? AND orderLineNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL);
        preparedStatement.setInt(1, orderNo);
        preparedStatement.setInt(2, orderLineNo);

        int rowsAffected = preparedStatement.executeUpdate();

        // if order is empty then delete order
        boolean orderLinesDoExist = orderLinesExist(orderNo, connection);
        if (!orderLinesDoExist) {
            deleteOrder(orderNo, connection);
        }

        if (rowsAffected > 0) {
            System.out.println(rowsAffected + " row(s) deleted successfully in deleteOrderLine.");
        } else {
            System.out.println("No rows were deleted for orderNo: " + orderNo + " and orderLineNo: " + orderLineNo);
        }
        return rowsAffected > 0;
    }

    // checks if an order exists without any orderLines
    public boolean orderLinesExist(int orderNo, Connection connection) throws SQLException {
        String checkOrderLineExist = "SELECT 1 FROM OrderLines WHERE orderNo = ? LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(checkOrderLineExist)) {
            preparedStatement.setInt(1, orderNo);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a record exists, false otherwise
            }
        }
    }

    // Get all OrderLines from the database
    public ArrayList<OrderLine> getAllOrderLines(Connection connection) throws SQLException, InvalidInputException {
        // Create a list of order lines
        ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
        String selectSQL = "SELECT * FROM OrderLines";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into OrderLine object
            orderLines.add(new OrderLine(resultSet.getInt("orderNo"), resultSet.getInt("orderLineNo"), resultSet.getString("productCode"),
                    resultSet.getInt("quantity"), resultSet.getFloat("totalCost")));
        }
        return orderLines;
    }

    // Get all OrderLines from the database by orderNo
    public ArrayList<OrderLine> getOrderLinesByOrderNo(int orderNo, Connection connection) throws SQLException, InvalidInputException {
        // Create a list of order lines
        ArrayList<OrderLine> orderLines = new ArrayList<OrderLine>();
        String selectSQL = "SELECT * FROM OrderLines WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            // Put into OrderLine object
            orderLines.add(new OrderLine(resultSet.getInt("orderNo"), resultSet.getInt("orderLineNo"), resultSet.getString("productCode"),
                    resultSet.getInt("quantity"), resultSet.getFloat("totalCost")));
        }
        return orderLines;
    }

    // Get an OrderLine by orderNo and orderLineNo
    public OrderLine getOrderLineByOrderNoAndOrderLineNo(int orderNo, int orderLineNo, Connection connection)
            throws SQLException, InvalidInputException {
        String selectSQL = "SELECT * FROM OrderLines WHERE orderNo=? AND orderLineNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);
        preparedStatement.setInt(2, orderLineNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            // Put into OrderLine object
            return new OrderLine(resultSet.getInt("orderNo"), resultSet.getInt("orderLineNo"), resultSet.getString("productCode"),
                    resultSet.getInt("quantity"), resultSet.getFloat("totalCost"));
        } else {
            return null;
        }
    }

    // Get the next orderLineNo
    public int getNextOrderLineNo(int orderNo, Connection connection) throws SQLException {
        String selectSQL = "SELECT MAX(orderLineNo) FROM OrderLines WHERE orderNo=?";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
        preparedStatement.setInt(1, orderNo);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) + 1;
        } else {
            return -1;
        }
    }

    // Get the next orderQueueNo
    public int getNextOrderQueueNo(Connection connection) throws SQLException {
        String selectSQL = "SELECT MAX(orderQueueNo) FROM Orders";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) + 1;
        } else {
            return 0;
        }
    }

    // Get the current orderQueueNo
    public int getCurrentOrderQueueNo(Connection connection) throws SQLException {
        String selectSQL = "SELECT MIN(orderQueueNo) FROM Orders";
        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            return -1;
        }
    }
}
