package group069;

import exceptions.InvalidInputException;
import javax.swing.*;

public class Product {
    private String productCode;
    private String productBrand;
    private String productName;
    private Float cost;
    private String gauge;
    private int stock;
    private int categoryID;
    private String eraCode;
    private String dccCode;
    private Boolean digital;
    private String packageContent;

    // Base constructor
    private Product(String productCode, String productBrand, String productName, Float cost, String gauge, int stock, int categoryID)
            throws InvalidInputException {
        this.setProductCode(productCode);
        this.setProductBrand(productBrand);
        this.setProductName(productName);
        this.setCost(cost);
        this.setGauge(gauge);
        this.setStock(stock);
        this.setCategoryID(categoryID);
    }

    // Constructor for fetching from database or editing existing product
    public Product(String productCode, String productBrand, String productName, Float cost, String gauge, int stock, int categoryID, String eraCode,
            String dccCode, String digital, String packageContent) throws InvalidInputException {
        this(productCode, productBrand, productName, cost, gauge, stock, categoryID);
        this.setEraCode(eraCode);
        this.setDccCode(dccCode);
        this.setDigital(digital);
        this.setPackageContent(packageContent);
    }

    // Getters and Setters
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) throws InvalidInputException {
        if (cost < 0) {
            throw new InvalidInputException("Cost cannot be negative.");
        }
        this.cost = cost;
    }

    public String getGauge() {
        return gauge;
    }

    public void setGauge(String gauge) throws InvalidInputException {
        if (gauge == null || gauge.isEmpty()) {
            this.gauge = null;
            return;
        } else if (!gauge.equals("OO") && !gauge.equals("TT") && !gauge.equals("N")) {
            throw new InvalidInputException("Gauge must be one of the following: OO, TT, N");
        }
        this.gauge = gauge;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) throws InvalidInputException {
        if (stock < 0) {
            throw new InvalidInputException("Stock cannot be negative.");
        }
        this.stock = stock;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) throws InvalidInputException {
        if (!Utils.categoryNames.containsKey(categoryID)) {
            throw new InvalidInputException("Invalid categoryID.");
        }
        this.categoryID = categoryID;
    }

    public String getEraCode() {
        return eraCode;
    }

    public void setEraCode(String eraCode) {
        if (eraCode == null || eraCode.isEmpty()) {
            this.eraCode = null;
            return;
        }
        this.eraCode = eraCode;
    }

    public String getDccCode() {
        return dccCode;
    }

    public void setDccCode(String dccCode) throws InvalidInputException {
        if (dccCode == null || dccCode.isEmpty()) {
            this.dccCode = null;
            return;
        } else if (!dccCode.equals("Analogue") && !dccCode.equals("DCC-Ready") && !dccCode.equals("DCC-Fitted") && !dccCode.equals("DCC-Sound")) {
            JOptionPane.showMessageDialog(null, "DCC Code must be one of the following: Analogue, DCC-Ready, DCC-Fitted, DCC-Sound", "Error",
                    JOptionPane.ERROR_MESSAGE);
            throw new InvalidInputException("DCC Code must be one of the following: Analogue, DCC-Ready, DCC-Fitted, DCC-Sound");
        } else if (eraCode == null) {
            JOptionPane.showMessageDialog(null, "DCC code cannot be set if era code is null", "Error", JOptionPane.ERROR_MESSAGE);
            throw new InvalidInputException("DCC code cannot be set if era code is null.");
        }
        this.dccCode = dccCode;
    }

    public Boolean isDigital() {
        return digital;
    }

    public void setDigital(Boolean digital) throws InvalidInputException {
        if (digital == null) {
            this.digital = null;
            return;
        } else if (digital && (eraCode != null || dccCode != null)) {
            JOptionPane.showMessageDialog(null, "CANNOT be true if eraCode or dccCode are NOT NULL", "Error", JOptionPane.ERROR_MESSAGE);
            throw new InvalidInputException("Digital cannot be set if era code or dcc code is not null.");
        }
        this.digital = digital;
    }

    public void setDigital(String digital) throws InvalidInputException {
        if (digital == null || digital.isEmpty()) {
            this.digital = null;
            return;
        } else if (digital.equals("0") || digital.equals("false")) {
            this.digital = false;
            return;
        } else if (digital.equals("1") || digital.equals("true")) {
            this.digital = true;
            return;
        } else {
            throw new InvalidInputException("Digital must be 0/false or 1/true.");
        }
    }

    public String getPackageContent() {
        return packageContent;
    }

    public void setPackageContent(String packageContent) throws InvalidInputException {
        this.packageContent = packageContent;
    }

    @Override
    public String toString() {
        return "Product{" + "productCode='" + productCode + '\'' + ", productBrand='" + productBrand + '\'' + ", productName='" + productName + '\''
                + ", cost=" + cost + ", gauge='" + gauge + '\'' + ", stock=" + stock + ", categoryID=" + categoryID + ", eraCode='" + eraCode + '\''
                + ", dccCode='" + dccCode + '\'' + ", digital=" + digital + ", packageContent='" + packageContent + '\'' + "}";
    }

    // main for debugging
    public static void main(String[] args) {
        try {
            // add locomotive
            Product product = new Product("code", "brand", "locomotive", 100.00f, "gauge", 0, 0, "era", "DCC-Ready", null, null);
            System.out.println(product);
            // add rolling stock
            product = new Product("code", "brand", "rollingstock", 100.00f, "gauge", 10, 0, "era", null, null, null);
            System.out.println(product);
            // add controller
            product = new Product("code", "brand", "controller", 100.00f, "gauge", 0, 0, null, null, "1", null);
            System.out.println(product);
            // add trackpiece
            product = new Product("code", "brand", "trackpiece", 100.00f, "gauge", 0, 0, null, null, null, null);
            System.out.println(product);
            // add trainset
            product = new Product("code", "brand", "trainset", 100.00f, "gauge", 0, 0, "era", null, null, "content");
            System.out.println(product);
            // add trackpack
            product = new Product("code", "brand", "trackpack", 100.00f, "gauge", 0, 0, null, null, null, "content");
            System.out.println(product);
            // edit locomotive
            product = new Product("code", "brand", "locomotive", 100.00f, "gauge", 100, 0, "era", "DCC-Sound", null, null);
            System.out.println(product);
        } catch (InvalidInputException e) {
            e.displayErrorMessage();
        }
    }
}
