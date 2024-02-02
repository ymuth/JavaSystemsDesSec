package group069;

import java.util.Date;

import exceptions.InvalidInputException;

public class BankDetail {

    private String cardName;
    private String cardNumber;
    private String expiryDate;
    private String securityCode;

    // Constructor for new card
    public BankDetail(String cardName, String cardNumber, int month, int year, String securityCode) throws InvalidInputException {
        this.setCardName(cardName);
        this.setCardNumber(cardNumber);
        this.setExpiryDate(month, year);
        this.setSecurityCode(securityCode);
    }

    // Constructor for fetching from database
    public BankDetail(String cardName, String cardNumber, String expiryDate, String securityCode) throws InvalidInputException {
        this.setCardName(cardName);
        this.setCardNumber(cardNumber);
        this.setExpiryDate(expiryDate);
        this.setSecurityCode(securityCode);
    }

    // getters and setters
    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) throws InvalidInputException {
        if (!isValidCardNumber(cardNumber)) {
            throw new InvalidInputException("Card number must be 16 digits.");
        }
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(int month, int year) throws InvalidInputException {
        Date date = Utils.convertMonthYearToDate(month, year);
        if (!isValidExpiryDate(date)) {
            throw new InvalidInputException("Invalid expiry date.");
        }
        this.expiryDate = Utils.convertDateToMonthYear(date);
    }

    public void setExpiryDate(String expiryDate) throws InvalidInputException {
        Date date = Utils.convertMonthYearToDate(expiryDate);
        if (!isValidExpiryDate(date)) {
            throw new InvalidInputException("Invalid expiry date.");
        }
        this.expiryDate = expiryDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) throws InvalidInputException {
        if (!isValidSecurityCode(securityCode)) {
            throw new InvalidInputException("Security code must be 3 digits.");
        }
        this.securityCode = securityCode;
    }

    // Check if card number is valid
    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.length() == 16;
    }

    // Check if expiry date is valid
    private boolean isValidExpiryDate(Date expiryDate) {
        return expiryDate != null && expiryDate.after(new Date());
    }

    // Check if security code is valid
    private boolean isValidSecurityCode(String securityCode) {
        return securityCode.length() == 3;
    }

    @Override
    public String toString() {
        return "BankDetails{cardName=" + cardName + ", cardNumber=" + cardNumber + ", expiryDate=" + expiryDate
                + ", securityCode=" + securityCode + "}";
    }

    // main for debugging
    public static void main(String[] args) {
    }
}