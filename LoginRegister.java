package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginRegister extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn, registerBtn;

    public LoginRegister() {
        setTitle("🌈 Password Manager - Login/Register");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Password Manager", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(new Color(224, 224, 224));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout());
        btnPanel.setBackground(new Color(224, 224, 224));

        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(0, 153, 76));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);

        registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(255, 102, 102));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        add(btnPanel, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> loginUser());
        registerBtn.addActionListener(e -> registerUser());

        setVisible(true);
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");  // ✅ corrected
                JOptionPane.showMessageDialog(this, "🎉 Login Successful!");
                new Dashboard(userId);             // open dashboard
                this.dispose();                    // close login window
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid credentials!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO users(username,password) VALUES(?,?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Registration Successful!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "❌ Username already exists!");
        }
    }

    public static void main(String[] args) {
        new LoginRegister();
    }
}