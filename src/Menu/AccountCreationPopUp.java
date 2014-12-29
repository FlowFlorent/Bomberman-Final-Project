package Menu;

import Database.DatabaseController;
import Database.MongoDBController;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Normalizer;
import java.util.regex.Pattern;

import javax.swing.*;

/**
 * The Account Creation popup menu is called when the user chooses to create a new account in the login menu. The popup
 * has fields for username, real name, password and re-enter password. It also contains two buttons: Submit and Exit.
 * All fields are required to create a new account. Methods from JFrame are inherited to be used in the popup.
 */
public class AccountCreationPopUp extends JFrame {


    private JTextField realName;
    private JTextField userName;
    private JPasswordField fieldPass;
    private JPasswordField retypePass;
    private JLabel labelMessage;
    private JLabel labelMessage2;
    private JButton buttonSubmit;
    private JButton buttonExit;
    private MenuManager menuManager;

    /**
     * Constructor for AccountCreationMenuPopUp
     * @param menuManager Object menuManager is passed to navigate between the different game states and menus.
     */
    public AccountCreationPopUp(MenuManager menuManager){
        this.menuManager = menuManager;
        createView();

        //Sets up window attributes
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);


    }

    /**
     * Creates the window and everything displayed in it in the popup.
     */
    private void createView(){
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JLabel label = new JLabel("Real Name:             ");
        label.setBounds(10, 10, 80, 25);
        panel.add(label);

        realName = new JTextField(20);
        realName.setBounds(100, 10, 160, 25);
        panel.add(realName);

        JLabel label3 = new JLabel("User Name:             ");
        label3.setBounds(10, 50, 80, 25);
        panel.add(label3);

        userName = new JTextField(20);
        userName.setBounds(100, 50, 160, 25);
        panel.add(userName);


        JLabel label2 = new JLabel("Password:               ");
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
        buttonExit  = new JButton("Go back to Login Menu");
        buttonExit.setBounds(50, 170, 80, 25);
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

        panel.add(buttonSubmit);
        panel.add(buttonExit);

        labelMessage = new JLabel("");
        labelMessage.setBounds(10, 190, 160, 25);
        panel.add(labelMessage);
        labelMessage2 = new JLabel("");
        labelMessage2.setBounds(10, 210, 160, 25);
        panel.add(labelMessage2);
    }

    /**
     * Exits from the popup when clicked and takes the user back to the login page.
     */
    public void exitClicked(){
        menuManager.setMenuState(MenuState.LOGIN);
        setVisible(false);
        dispose();
    }

    /**
     * Takes the input when clicked, authenticates the user and creates an account if all criteria are met. Makes sure
     * no fields are empty and that password and username meets requirements. Redirects the user to the main menu after
     * successful account creation.
     */
    public void submitClicked() {
        String realNameText = realName.getText();
        String userNameText = userName.getText();
        String password = fieldPass.getText();
        String passwordDuplicate = retypePass.getText();

        boolean creationSuccessful = false;

        if (realNameText.isEmpty() || userNameText.isEmpty() || password.isEmpty() || passwordDuplicate.isEmpty() ){
            labelMessage.setText("                    Please fill out all the information                      ");
            labelMessage2.setText("");
        } else if (!(password.equals(passwordDuplicate))) {
            labelMessage.setText("                    Passwords do not match                    ");
            labelMessage2.setText("");
        } else if (userNameText.length() < 6){
            labelMessage.setText("                    Username must be at least 6 characters                    ");
            labelMessage2.setText("");
        } else if (!isValidPassword(password)) {
            labelMessage.setText("The password is too weak. Enter one of each type from:");
            labelMessage2.setText("Capital letters, Small letters, Digits and Symbols");
        } else if(password.length() < 8) {
            labelMessage.setText("                    Password must be at least 8 characters                    ");
            labelMessage2.setText("");
        } else if(!isValidUsername(userNameText)){ //Needs to consider latin characters
            labelMessage.setText("                    Username has to be alphanumeric                    ");
            labelMessage2.setText("");
        } else {
            try {
                //creationSuccessful = DatabaseController.createNewUser(userNameText, password, realNameText);
                creationSuccessful = MongoDBController.createNewUser(userNameText, password, realNameText);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (creationSuccessful) {
                labelMessage.setText("                    Account Created                    ");
                labelMessage2.setText("");
                menuManager.associatePlayerUserName(userNameText);
                redirectToMainMenu();
                setVisible(false);
                dispose();
            } else {
                labelMessage.setText("Error: Username may already be in use or");
                labelMessage2.setText("invalid entry for username and password.");
            }
        }
    }

    /**
     * Redirects the user to the main menu.
     */
    public void redirectToMainMenu() {
        menuManager.setMenuState(MenuState.MAIN);
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

    /**
     * Checks to see if username characters are valid or not. Username must be alphanumeric and may contain accents.
     * @param sI The string username input is taken to see if it is valid or not
     * @return Boolean is returned to see if the username is valid or not.
     */
    public boolean isValidUsername(String sI){
        String s = removeAccent(sI);
        String pattern= "^[a-zA-Z0-9]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;
    }

    /**
     * Normalizes (removes accents) from the username before the alphanumeric check is done.
     * @param str The username string is taken in to be normalized (accent free)
     * @return The normalized username string is returned.
     */
    public String removeAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pat = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pat.matcher(nfdNormalizedString).replaceAll("");
    }

}
