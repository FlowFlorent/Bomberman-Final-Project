package Menu;

import Database.DatabaseController;
import Database.MongoDBController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The AccountOptionsPopUp is called when the user selects Modify Account Information in the main menu. The popup has
 * fields for username, password and re-type password. It also contains 3 buttons: Submit, Delete and Exit. The user
 * can update their username, password or both. The user also has the option of deleting the account entirely or
 * returning to the main menu. Methods from JFrame are inherited to be used in the popup.
 */
public class AccountOptionsPopUp extends JFrame {

    private JTextField realName;
    private JPasswordField fieldPass;
    private JPasswordField retypePass;
    private JLabel labelMessage;
    private JLabel labelMessage2;
    private JLabel labelMessage3;
    private JButton buttonSubmit;
    private JButton buttonExit;
    private JButton buttonDeleteAccount;
    private MenuManager menuManager;
    private boolean updatedPassword = false;
    private boolean updatedRealName = false;

    /**
     * Constructor for the AccountOptionsPopUp
     * @param menuManager Object menuManager is passed to navigate between the different game states and menus.
     */
    public AccountOptionsPopUp(MenuManager menuManager){
        this.menuManager = menuManager;
        createView();

        //Sets up window attributes
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(420, 230);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Creates the window and everything displayed in it in the popup.
     */
    private void createView(){
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JLabel label = new JLabel("New Name:             ");
        label.setBounds(10, 10, 80, 25);
        panel.add(label);

        realName = new JTextField(20);
        realName.setBounds(100, 10, 160, 25);
        panel.add(realName);

        JLabel label2 = new JLabel("New Password:        ");
        label2.setBounds(10, 90, 80, 25);
        panel.add(label2);

        fieldPass = new JPasswordField(20);
        fieldPass.setBounds(100, 90, 160, 25);
        panel.add(fieldPass);

        JLabel label4 = new JLabel("Re-enter Password: ");
        label4.setBounds(10, 130, 80, 25);
        panel.add(label4);

        retypePass = new JPasswordField(20);
        retypePass.setBounds(100, 130, 160, 25);
        panel.add(retypePass);

        buttonSubmit = new JButton("Submit");
        buttonSubmit.setBounds(10, 170, 80, 25);
        buttonExit  = new JButton("Close");
        buttonExit.setBounds(50, 170, 80, 25);
        buttonDeleteAccount = new JButton("Delete Account");
        buttonDeleteAccount.setBounds(10, 170, 80, 25);

        /**
         * Calls the functionality for the submit button
         */
        buttonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                submitClicked();
            }
        });

        /**
         * Calls the functionality for the exit button
         */
        buttonExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                exitClicked();
            }
        });

        /**
         * Calls the functionality for the delete button
         */
        buttonDeleteAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                deleteAccountClicked();
            }
        });

        panel.add(buttonSubmit);
        panel.add(buttonExit);
        panel.add(buttonDeleteAccount);

        labelMessage = new JLabel("Leave field blank if no update is required");
        labelMessage.setBounds(10, 190, 160, 25);
        panel.add(labelMessage);
        labelMessage2 = new JLabel("");
        labelMessage2.setBounds(10, 210, 160, 25);
        panel.add(labelMessage2);
        labelMessage3 = new JLabel("");
        labelMessage3.setBounds(10, 230, 160, 25);
        panel.add(labelMessage3);
    }

    /**
     * Exits from the popup  when clicked and takes the user back to the main menu page.
     */
    public void exitClicked(){
        setVisible(false);
        dispose();
    }

    /**
     * Takes the input from the user when clicked. Checks which information is updated by the user. Calls respective
     * methods to validate and update information.  Shows confirmation text when information is updated and redirects
     * the user to the login menu when account is deleted.
     */
    public void submitClicked() {
        labelMessage.setText("");
        labelMessage2.setText("");
        labelMessage3.setText("");

        String newRealName = realName.getText();
        String newPassWord = fieldPass.getText();
        String newPassWordDuplicate = retypePass.getText();

        if (newRealName.isEmpty()  && newPassWord.isEmpty() && newPassWordDuplicate.isEmpty() ){
            labelMessage.setText("                              Nothing to Update.                            ");
            labelMessage2.setText("");
            labelMessage3.setText("");
            return;
        }
        boolean passWordUpdatedSuccessfully = true;
        if (!newPassWord.isEmpty()){
            passWordUpdatedSuccessfully = submitNewPassWord(newPassWord, newPassWordDuplicate);
            updatedPassword = true;
        }
        if (passWordUpdatedSuccessfully) {
            if (!newRealName.isEmpty()) {
                submitNewRealName(newRealName);
                updatedRealName = true;
            }
        }

        if (updatedPassword && updatedRealName) {
            updatedPassword = false;
            updatedRealName = false;
            labelMessage.setText("                       Information Updated                     ");
            labelMessage2.setText("");
            labelMessage3.setText("");
        }
    }

    /**
     * Deletes the account of the current logged in user and redirects them to the login menu.
     */
    public void deleteAccountClicked() {
        boolean accountDeleted = false;
        String currUser = menuManager.getPlayerUserName();
        try {
            //accountDeleted = DatabaseController.deleteAccount(currUser);
            accountDeleted = MongoDBController.deleteAccount(currUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accountDeleted){
            labelMessage.setText("                               Account Deleted                         ");
            labelMessage2.setText("");
            labelMessage3.setText("");
            menuManager.setMenuState(MenuState.LOGIN);
            setVisible(false);
            dispose();
        } else {
            labelMessage.setText("                       Account could not be Deleted                     ");
            labelMessage2.setText("");
            labelMessage3.setText("");
        }
    }

    /**
     * Validates and updates the password.
     * @param newPassWord String password is passed to be checked for validity.
     * @param newPassWordDuplicate String re-entered password is taken in to see if they match
     * @return Boolean is returned on whether the password is valid or not
     */
    public boolean submitNewPassWord(String newPassWord, String newPassWordDuplicate) {
        boolean updateSuccessful = false;

        if (!isValidPassword(newPassWord)) {
            labelMessage.setText("The password is too weak. Enter one of each type from:");
            labelMessage2.setText("Capital letters, Small letters, Digits and Symbols");
            labelMessage3.setText("");
            updateSuccessful = false;
        } else if(newPassWord.length() < 8) {
            labelMessage.setText("Password must be at least 8 characters");
            labelMessage2.setText("");
            labelMessage3.setText("");
            updateSuccessful = false;
        } else if (!(newPassWord.equals(newPassWordDuplicate))) {
            labelMessage.setText("                             Passwords do not match                        ");
            labelMessage2.setText("");
            labelMessage3.setText("");
            updateSuccessful = false;
        } else {
            try {
                //updateSuccessful = DatabaseController.updatePassword(newPassWord, menuManager.getPlayerUserName());
                updateSuccessful = MongoDBController.updatePassword(newPassWord, menuManager.getPlayerUserName());
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (updateSuccessful) {
                labelMessage.setText("                             Password Updated                        ");
                labelMessage2.setText("");
                labelMessage3.setText("");
            } else {
                labelMessage.setText("                             Error: Invalid Entry                        ");
                labelMessage2.setText("");
                labelMessage3.setText("");
            }
        }
        return updateSuccessful;
    }

    /**
     * Updates username
     * @param newRealName String real name of the user is passed to be updated.
     */
    public void submitNewRealName(String newRealName) {
        boolean updateSuccessful = false;

        try {
            //updateSuccessful = DatabaseController.updateRealName(newRealName, menuManager.getPlayerUserName());
            updateSuccessful = MongoDBController.updateRealName(newRealName, menuManager.getPlayerUserName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (updateSuccessful) {
            labelMessage.setText("");
            labelMessage2.setText("");
            labelMessage3.setText("                             Real Name Updated                        ");
        } else {
            labelMessage.setText("");
            labelMessage2.setText("");
            labelMessage3.setText("                              Error: Invalid Entry                        ");
        }
    }

    /**
     * Checks to see if the password meets the requirement of having capital letters, small letters, digits and
     * symbols.
     * @param password The password input is taken in for strength to checked.
     * @return Boolean is returned on whether the password is strong enough or not.
     */
    private boolean isValidPassword(String password) {
        int passwordStrength = 0;
        String[] partialRegexChecks = {".*[a-z]+.*", // Lower Case
                ".*[A-Z]+.*", // Upper Case
                ".*[\\d]+.*", // Digits
                ".*[-$&+,:;=?@#|'<>.^*()%!_]+.*" // Symbols
        };


        if (password.matches(partialRegexChecks[0])) {
            passwordStrength += 25;
        }
        if (password.matches(partialRegexChecks[1])) {
            passwordStrength += 25;
        }
        if (password.matches(partialRegexChecks[2])) {
            passwordStrength += 25;
        }
        if (password.matches(partialRegexChecks[3])) {
            passwordStrength += 25;
        }

        if (passwordStrength != 100){
            return false;
        } else {
            return true;
        }
    }



}

