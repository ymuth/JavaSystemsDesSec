package exceptions;

import javax.swing.JOptionPane;

public class PermissionErrorException extends Exception {
    private String errorMessage;

    public PermissionErrorException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void displayErrorMessage() {
        JOptionPane.showMessageDialog(null, errorMessage, "Permission Error", JOptionPane.ERROR_MESSAGE);
    }
}
