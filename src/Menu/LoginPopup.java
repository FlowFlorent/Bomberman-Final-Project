package Menu;

import Database.DatabaseController;
import Database.MongoDBController;
import SystemController.GameStateManager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 *The login popup is called when the user chooses to login from the login menu. The login popup has fields for the
 * username, password and a Login button. The user is logged in after valid credentials are entered and redirected
 * to the main menu. Methods from JFrame are inherited to be used in the popup.
 */
public class LoginPopup extends JFrame {

    private JTextField fieldName;
    private JPasswordField fieldPass;
    private JLabel labelMessage;
    private JButton buttonSubmit;
    private MenuManager menuManager;

    /**
     * Constructor for LoginPopup.
     * @param menuManager Object menuManager is passed to navigate between the different game states and menus.
     */
    public LoginPopup(MenuManager menuManager){
        this.menuManager = menuManager;

        //Sets up window attributes
        createView();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(350, 150);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    /**
     * Creates the window and everything displayed in it in the popup.
     */
    private void createView(){
        JPanel panel = new JPanel();
        getContentPane().add(panel);

        JLabel label = new JLabel("Username: ");
        label.setBounds(10, 10, 80, 25);
        panel.add(label);

        fieldName = new JTextField(20);
        fieldName.setBounds(100, 10, 160, 25);
        panel.add(fieldName);

        JLabel label2 = new JLabel("Password: ");
        label2.setBounds(10, 40, 80, 25);
        panel.add(label2);

        fieldPass = new JPasswordField(20);
        fieldPass.setBounds(100, 40, 160, 25);
        panel.add(fieldPass);


        buttonSubmit = new JButton("Login");
        buttonSubmit.setBounds(10, 80, 80, 25);

        /**
         * Calls the functionality for the login button.
         */
        buttonSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                loginClicked();
            }
        });

        panel.add(buttonSubmit);

        labelMessage = new JLabel("");
        panel.add(labelMessage);
    }

    /**
     * Take the input for username and password when clicked and validates whether credentials are correct by calling
     * the database. If username and password combination is valid, then the user is logged in and redirected to the
     * main menu.
     */
    public void loginClicked() {
        String name = fieldName.getText();
        String password = fieldPass.getText();
        System.out.println(password);

        boolean loginSuccessful = false;

        if (name.isEmpty()){
            labelMessage.setText("                        Username cannot be blank                        ");
        } else if (password.isEmpty()){
            labelMessage.setText("                        Password cannot be blank                        ");
        } else {
            try {
                //loginSuccessful = DatabaseController.authenticateUser(name, password);
                loginSuccessful = MongoDBController.authenticateUser(name, password);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (loginSuccessful) {
                labelMessage.setText("Success");
                menuManager.associatePlayerUserName(name);
                redirectToMainMenu();
                setVisible(false);
                dispose();
            } else {
                labelMessage.setText("                       Username-Password Combination Invalid                 ");
            }
        }
    }

    /**
     * Redirects the user to the main menu.
     */
    public void redirectToMainMenu() {
        menuManager.setMenuState(MenuState.MAIN);
    }

}

