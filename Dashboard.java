package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private int userId;

    public Dashboard(int userId) {
        this.userId = userId;

        setTitle("🌈 Password Manager Dashboard");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Site", "Username", "Password"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFillsViewportHeight(true);
        table.setBackground(new Color(240, 248, 255));
        table.setGridColor(new Color(200, 200, 200));
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        bottomPanel.setBackground(new Color(224, 224, 224));

        JTextField siteField = new JTextField();
        JTextField siteUserField = new JTextField();
        JTextField sitePassField = new JTextField();

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(0, 153, 76));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);

        JButton editBtn = new JButton("Edit Selected");
        editBtn.setBackground(new Color(255, 153, 0));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setBackground(new Color(255, 51, 51));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(102, 102, 255));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);

        bottomPanel.add(new JLabel("Site:"));
        bottomPanel.add(siteField);
        bottomPanel.add(new JLabel("Username:"));
        bottomPanel.add(siteUserField);
        bottomPanel.add(new JLabel("Password:"));
        bottomPanel.add(sitePassField);
        bottomPanel.add(addBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(logoutBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        loadPasswords();

        // Add new password
        addBtn.addActionListener(e -> {
            String site = siteField.getText();
            String username = siteUserField.getText();
            String password = sitePassField.getText();
            if (site.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Fill all fields!");
                return;
            }
            addPassword(site, username, password);
            loadPasswords();
            siteField.setText("");
            siteUserField.setText("");
            sitePassField.setText("");
        });

        // Edit selected password
        editBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a row to edit!");
                return;
            }
            int id = (int) model.getValueAt(selectedRow, 0);
            String site = JOptionPane.showInputDialog(this, "Enter new Site:", model.getValueAt(selectedRow, 1));
            String username = JOptionPane.showInputDialog(this, "Enter new Username:", model.getValueAt(selectedRow, 2));
            String password = JOptionPane.showInputDialog(this, "Enter new Password:", model.getValueAt(selectedRow, 3));
            if (site != null && username != null && password != null) {
                editPassword(id, site, username, password);
                loadPasswords();
            }
        });

        // Delete selected password
        deleteBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "❌ Select a row to delete!");
                return;
            }
            int id = (int) model.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this password?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                deletePassword(id);
                loadPasswords();
            }
        });

        // Logout
        logoutBtn.addActionListener(e -> {
            new LoginRegister();
            this.dispose();
        });

        setVisible(true);
    }

    private void loadPasswords() {
        model.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM passwords WHERE user_id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("site_name"),
                        rs.getString("site_username"),
                        rs.getString("site_password")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addPassword(String site, String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO passwords(user_id, site_name, site_username, site_password) VALUES(?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);
            pst.setString(2, site);
            pst.setString(3, username);
            pst.setString(4, password);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Password added!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error adding password: " + ex.getMessage());
        }
    }

    private void editPassword(int id, String site, String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE passwords SET site_name=?, site_username=?, site_password=? WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, site);
            pst.setString(2, username);
            pst.setString(3, password);
            pst.setInt(4, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Password updated!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error updating password: " + ex.getMessage());
        }
    }

    private void deletePassword(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM passwords WHERE id=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Password deleted!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error deleting password: " + ex.getMessage());
        }
    }
}