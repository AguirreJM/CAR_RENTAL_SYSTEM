import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Login extends JFrame {
    private JTextField userName;
    private JPasswordField passwordField;
    private JButton logInButton;
    private JButton resetButton;
    private Map<String, String> userCredentials;

    public Login() {
        setTitle("Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        initializeUserDatabase();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        initializeComponents();
        JLabel userLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        formPanel.add(userLabel);
        formPanel.add(userName);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(logInButton);
        buttonPanel.add(resetButton);
        formPanel.add(new JLabel(""));
        formPanel.add(buttonPanel);
        JLabel titleLabel = new JLabel("Please Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        addActionListeners();
    }

    private void initializeUserDatabase() {
        userCredentials = new HashMap<>();
        userCredentials.put("admin", "admin123");
        userCredentials.put("user", "password");
        userCredentials.put("nikki", "louise123");
        userCredentials.put("jm123", "aguirre12");
        userCredentials.put("mierda", "kaka123");
        userCredentials.put("yuriz", "Ronnel");
    }

    private void initializeComponents() {
        userName = new JTextField(15);
        passwordField = new JPasswordField(15);
        logInButton = new JButton("Login");
        resetButton = new JButton("Reset");
    }

    private void addActionListeners() {
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }

    private void authenticateUser() {
        String username = userName.getText();
        String password = new String(passwordField.getPassword());

        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username or password cannot be empty",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (userCredentials.containsKey(username) &&
                userCredentials.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this,
                    "Welcome " + username + "! You have successfully logged in.",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            openMainApplication(username);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void resetForm() {
        userName.setText("");
        passwordField.setText("");
        userName.requestFocus();
    }

    private void openMainApplication(String username) {
        this.dispose();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                customer customerManagementForm = new customer();
                customerManagementForm.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });
            SwingUtilities.invokeLater(() -> new Login().setVisible(true));
        }

    }