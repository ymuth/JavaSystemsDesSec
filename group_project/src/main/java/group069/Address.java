package group069;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.InvalidInputException;

public class Address {
    private String addressID;
    private String roadName;
    private String cityName;
    private Integer houseNumber;
    private String postCode;

    public Address(String roadName, String cityName, Integer houseNumber, String postCode) throws InvalidInputException {
        this.setRoadName(roadName);
        this.setCityName(cityName);
        this.setHouseNumber(houseNumber);
        this.setPostCode(postCode);
        this.setAddressID(this.postCode, this.houseNumber);
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String postCode, int houseNumber) {
        // pad house number with 0s to 3 digits
        String houseNumberStr = String.format("%03d", houseNumber);
        // remove spaces from postCode and concat with house number to make addressID
        String addressID = postCode.replace(" ", "") + houseNumberStr;
        if (!isValidAddressID(addressID)) {
            System.out.println(addressID);
            throw new IllegalArgumentException("Invalid addressID.");
        }
        this.addressID = addressID;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        if (roadName == null || roadName.isEmpty()) {
            throw new IllegalArgumentException("Road name cannot be empty.");
        }
        this.roadName = roadName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty.");
        }
        this.cityName = cityName;
    }

    public Integer getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(Integer houseNumber) throws InvalidInputException {
        if (houseNumber < 1 || houseNumber > 999) {
            throw new InvalidInputException("House number has to be between 1 and 999.");
        }
        this.houseNumber = houseNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) throws InvalidInputException {
        if (postCode == null) {
            throw new IllegalArgumentException("Postcode is null.");
        }
        if (!isValidPostCode(postCode)) {
            throw new InvalidInputException("Invalid UK postcode or house number.");
        }
        this.postCode = postCode;
    }

    // Check is postcode is valid
    private boolean isValidPostCode(String postCode) {
        // regex pattern for UK postcode validation
        Pattern postcodePattern = Pattern.compile(
                "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|"
                + "(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?)))) s?[0-9][A-Za-z]{2})$");
        Matcher postcodeMatcher = postcodePattern.matcher(postCode);
        return postcodeMatcher.matches();
    }

    // Check if addressID is valid
    private boolean isValidAddressID(String addressID) {
        // regex pattern for addressID validation
        Pattern addressIDPattern = Pattern.compile(
                "^([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|"
                + "(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))s?[0-9][A-Za-z]{2})[0-9]{3}$");
        Matcher addressIDMatcher = addressIDPattern.matcher(addressID);
        return addressIDMatcher.matches();
    }

    @Override
    public String toString() {
        return houseNumber + " " + roadName + ", " + cityName + ", " + postCode;
    }

    // main method for testing
    public static void main(String[] args) throws InvalidInputException {
    }
}
