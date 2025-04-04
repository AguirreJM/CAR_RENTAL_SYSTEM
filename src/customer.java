import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class customer extends JFrame {
    private JTextField customerIDTextField;
    private JTextField customerNameTextField;
    private JTextField customerAddressTextField;
    private JTextField customerNumTextField;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JTable customerTable;
    private JButton selectCarButton;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private List<Customer> customers = new ArrayList<>();
    private int selectedRow = -1;
    private static customer instance;
    private static Customer selectedCustomer;

    public customer() {
        setTitle("Car Rental System - Customer Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setupLayout();
        attachEventListeners();
        addSampleData();
        instance = this;
        setVisible(true);
    }

    // Common Customer class to be used by both manage and customer classes
    public static class Customer {
        private String id;
        private String name;
        private String address;
        private String phone;

        public Customer(String id, String name, String address, String phone) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.phone = phone;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    private void initComponents() {
        customerIDTextField = new JTextField(20);
        customerNameTextField = new JTextField(20);
        customerAddressTextField = new JTextField(20);
        customerNumTextField = new JTextField(20);
        saveButton = new JButton("Save");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        resetButton = new JButton("Reset");
        selectCarButton = new JButton("Select Car for Rental");
        String[] columnNames = {"ID", "Name", "Address", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        scrollPane = new JScrollPane(customerTable);
        customerTable.setFillsViewportHeight(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        selectCarButton.setEnabled(false);
    }

    private void setupLayout() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        formPanel = new JPanel(new GridBagLayout());
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(customerIDTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(customerNameTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Customer Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(customerAddressTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Customer Phone:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(customerNumTextField, gbc);
        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(selectCarButton);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);
    }

    private void attachEventListeners() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    saveCustomer();
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInputs()) {
                    updateCustomer();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        selectCarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedRow >= 0) {
                    selectedCustomer = customers.get(selectedRow);
                    setVisible(false);
                    try {
                        // Check if manage class exists
                        Class.forName("manage");
                        manage carManageWindow = new manage();
                        carManageWindow.setVisible(true);
                        carManageWindow.enableRentButton();
                    } catch (ClassNotFoundException ex) {
                        JOptionPane.showMessageDialog(customer.this,
                                "Car management system not found. Please ensure manage.java is in the same package.",
                                "Error Loading System",
                                JOptionPane.ERROR_MESSAGE);
                        setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(customer.this,
                            "Please select a customer first!",
                            "No Customer Selected",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = customerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    populateForm(selectedRow);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    saveButton.setEnabled(false);
                    selectCarButton.setEnabled(true);
                    customerIDTextField.setEditable(false);
                }
            }
        });
    }

    private boolean validateInputs() {
        if (customerIDTextField.getText().trim().isEmpty() ||
                customerNameTextField.getText().trim().isEmpty() ||
                customerAddressTextField.getText().trim().isEmpty() ||
                customerNumTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Changed validation to allow alphanumeric IDs
        String id = customerIDTextField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer ID cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String phone = customerNumTextField.getText().trim();
        if (!phone.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "Phone number should be 11 digits!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void saveCustomer() {
        String id = customerIDTextField.getText().trim();
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                JOptionPane.showMessageDialog(this, "Customer ID already exists!", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Customer customer = new Customer(
                id,
                customerNameTextField.getText().trim(),
                customerAddressTextField.getText().trim(),
                customerNumTextField.getText().trim()
        );

        customers.add(customer);
        addCustomerToTable(customer);
        JOptionPane.showMessageDialog(this, "Customer saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearForm();
    }

    private void updateCustomer() {
        if (selectedRow >= 0) {
            Customer customer = customers.get(selectedRow);
            customer.setName(customerNameTextField.getText().trim());
            customer.setAddress(customerAddressTextField.getText().trim());
            customer.setPhone(customerNumTextField.getText().trim());
            tableModel.setValueAt(customer.getName(), selectedRow, 1);
            tableModel.setValueAt(customer.getAddress(), selectedRow, 2);
            tableModel.setValueAt(customer.getPhone(), selectedRow, 3);
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        }
    }

    private void deleteCustomer() {
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this customer?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                customers.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            }
        }
    }

    private void clearForm() {
        customerIDTextField.setText("");
        customerNameTextField.setText("");
        customerAddressTextField.setText("");
        customerNumTextField.setText("");
        selectedRow = -1;
        customerTable.clearSelection();
        customerIDTextField.setEditable(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(true);
        selectCarButton.setEnabled(false);
        customerIDTextField.requestFocus();
    }

    private void populateForm(int row) {
        Customer customer = customers.get(row);
        customerIDTextField.setText(customer.getId());
        customerNameTextField.setText(customer.getName());
        customerAddressTextField.setText(customer.getAddress());
        customerNumTextField.setText(customer.getPhone());
    }

    private void addCustomerToTable(Customer customer) {
        tableModel.addRow(new Object[]{
                customer.getId(),
                customer.getName(),
                customer.getAddress(),
                customer.getPhone()
        });
    }

    private void addSampleData() {
        Customer c1 = new Customer("1001", "John Michael F. Aguirre", "Tabugon, Dingle, Iloilo", "12345678901");
        Customer c2 = new Customer("1002", "Nikki Louise Ang Lee", "Poblacion, Dingle, Iloilo", "23456789012");
        Customer c3 = new Customer("1003", "Yuriz Ronnel Domingo", "Jaro, Iloilo", "34567890123");
        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
        addCustomerToTable(c1);
        addCustomerToTable(c2);
        addCustomerToTable(c3);
    }

    public static void returnToCustomerScreen() {
        if (instance != null) {
            instance.setVisible(true);
            instance.clearForm();
        }
    }

    public static Customer getSelectedCustomer() {
        return selectedCustomer;
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
                new customer();
            }
        });
    }
}