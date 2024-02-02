package exceptions;

import javax.swing.JOptionPane;

public class InvalidInputException extends Exception {
    private String errorMessage;

    public InvalidInputException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void displayErrorMessage() {
        JOptionPane.showMessageDialog(null, errorMessage, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }
}
