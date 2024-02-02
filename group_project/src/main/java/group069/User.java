package group069;

import java.util.regex.Pattern;

import exceptions.InvalidInputException;

public class User {
    private int userID;
    private String email;
    private String password;
    private String forename;
    private String surname;
    private Address address;

    // Constructor when registering
    public User(String email, String password) throws InvalidInputException {
        this.setEmail(email);
        this.setPassword(Utils.sha256(this.email + password));
    }

    // Constructor when entering personal details
    public User(String email, String password, String forename, String surname, String postCode, String roadName, String cityName, int houseNumber)
            throws InvalidInputException {
        this.setEmail(email);
        this.setPassword(password);
        this.setForename(forename);
        this.setSurname(surname);
        this.setAddress(roadName, cityName, houseNumber, postCode);
    }

    // Constructor when fetching from database
    public User(int userID, String email, String password, String forename, String surname, String addressID)
            throws InvalidInputException {
        this.setUserID(userID);
        this.setEmail(email);
        this.setPassword(password);
        // if names and addresses are not null, edge case where user hasn't entered them
        if (forename != null && surname != null && addressID != null) {
            this.setForename(forename);
            this.setSurname(surname);
            Address address = getAddressFromDatabase(addressID);
            if (address == null) {
                throw new IllegalArgumentException("Invalid addressID " + addressID + " for user " + userID + ".");
            }
            this.address = address;
        }
    }

    // Getters and Setters
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InvalidInputException {
        if (email == null) {
            throw new IllegalArgumentException("Email is null");
        } else if (!isValidEmail(email)) {
            throw new InvalidInputException(email + " is not a valid email address.");
        }
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password is null");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid hashed password.");
        }
        this.password = (password);
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) throws InvalidInputException {
        if (forename == null || forename.isEmpty()) {
            throw new InvalidInputException("Forename cannot be empty.");
        }
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) throws InvalidInputException {
        if (surname == null || surname.isEmpty()) {
            throw new InvalidInputException("Surname cannot be empty.");
        }
        this.surname = surname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(String roadName, String cityName, int houseNumber, String postCode) throws InvalidInputException {
        this.address = new Address(roadName, cityName, houseNumber, postCode);
    }

    // check email
    private boolean isValidEmail(String email) {
        // regex pattern for email validation
        Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        return pattern.matcher(email).matches();
    }

    // check hashed password length == 64
    private boolean isValidPassword(String password) {
        return password.length() == 64;
    }

    // Get address from database
    private Address getAddressFromDatabase(String addressID) {
        DatabaseConnectionHandler handler = new DatabaseConnectionHandler();
        DatabaseOperations db = new DatabaseOperations();
        try {
            return db.getAddressByID(addressID, handler.openAndGetConnection());
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            handler.closeConnection();
        }
    }

    // toString method
    @Override
    public String toString() {
        return "User{" + "userID=" + userID + ", email='" + email + "'" + ", password='" + password + "'" + ", forename='" + forename + "'"
                + ", surname='" + surname + "'" + ", address=" + address + '}';
    }
}

