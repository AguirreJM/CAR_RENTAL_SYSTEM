import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class manage extends JFrame {
    private JTextField registrationTextField;
    private JTextField brandTextField;
    private JTextField modelTextField;
    private JTextField priceTextField;
    private JComboBox<String> statusComboBox;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JTable carTable;
    private JButton rentButton;
    private JButton backButton;
    private DefaultTableModel tableModel;
    private List<Car> carList;
    private int selectedRow = -1;


    private static class Customer {
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
        public String getId() { return id; }
        public String getName() { return name; }
        public String getAddress() { return address; }
        public String getPhone() { return phone; }
    }

    private Customer mockCustomer = new Customer("CUST-001", "John Doe", "123 Main St", "555-1234");
    public manage() {
        carList = new ArrayList<>();
        setTitle("Car Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Car Information"));
        registrationTextField = new JTextField(20);
        brandTextField = new JTextField(20);
        modelTextField = new JTextField(20);
        priceTextField = new JTextField(20);
        String[] statuses = {"Available", "Rented", "Maintenance", "Reserved"};
        statusComboBox = new JComboBox<>(statuses);
        formPanel.add(new JLabel("Registration Number:"));
        formPanel.add(registrationTextField);
        formPanel.add(new JLabel("Brand:"));
        formPanel.add(brandTextField);
        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelTextField);
        formPanel.add(new JLabel("Price per Day (₱):"));
        formPanel.add(priceTextField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        String[] columnNames = {"Registration", "Brand", "Model", "Price/Day (₱)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        carTable = new JTable(tableModel);
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedRow = carTable.getSelectedRow();
                if (selectedRow >= 0) {
                    displayCarDetails(selectedRow);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    Car selectedCar = carList.get(selectedRow);
                    rentButton.setEnabled("Available".equals(selectedCar.getStatus()));
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Car Inventory"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        saveButton = new JButton("Save");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        resetButton = new JButton("Reset");
        rentButton = new JButton("Rent Selected Car");
        backButton = new JButton("Back to Customers");
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        rentButton.setEnabled(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCar();
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCar();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCar();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rentSelectedCar();
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToCustomerScreen();
            }
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(backButton);
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        addSampleData();
    }

    public void enableRentButton() {
        saveButton.setEnabled(true);
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        resetButton.setEnabled(true);
        JOptionPane.showMessageDialog(this,
                "Please select an available car to rent",
                "Select Car",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void rentSelectedCar() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a car to rent",
                    "No Car Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Car selectedCar = carList.get(selectedRow);
        if (!"Available".equals(selectedCar.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Selected car is not available for rent",
                    "Car Not Available",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer selectedCustomer = null;
        try {
            selectedCustomer = getSelectedCustomerFallback();
        } catch (Exception ex) {
            selectedCustomer = mockCustomer;
        }
        if (selectedCustomer == null) {
            selectedCustomer = promptForCustomerInfo();
            if (selectedCustomer == null) {
                return;
            }
        }
        String daysInput = JOptionPane.showInputDialog(this,
                "Enter number of days for rental:",
                "Rental Duration",
                JOptionPane.QUESTION_MESSAGE);
        if (daysInput == null || daysInput.trim().isEmpty()) {
            return;
        }
        int rentalDays;
        try {
            rentalDays = Integer.parseInt(daysInput);
            if (rentalDays <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a positive number of days",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        selectedCar.setStatus("Rented");
        tableModel.setValueAt("Rented", selectedRow, 4);
        double totalPrice = selectedCar.getPrice() * rentalDays;
        printReceipt(selectedCustomer, selectedCar, rentalDays, totalPrice);
        rentButton.setEnabled(false);
    }
    private Customer promptForCustomerInfo() {
        JTextField idField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(30);
        JTextField phoneField = new JTextField(15);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Customer ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        int result = JOptionPane.showConfirmDialog(this, panel,
                "Enter Customer Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Customer ID and Name are required",
                        "Missing Information",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return new Customer(id, name, address, phone);
        }
        return null;
    }
    private Customer getSelectedCustomerFallback() {
        try {
            Class<?> customerClass = Class.forName("customer");
            java.lang.reflect.Method getSelectedCustomerMethod =
                    customerClass.getMethod("getSelectedCustomer");
            Object result = getSelectedCustomerMethod.invoke(null);
            if (result != null) {
                String id = (String) result.getClass().getMethod("getId").invoke(result);
                String name = (String) result.getClass().getMethod("getName").invoke(result);
                String address = (String) result.getClass().getMethod("getAddress").invoke(result);
                String phone = (String) result.getClass().getMethod("getPhone").invoke(result);
                return new Customer(id, name, address, phone);
            }
        } catch (Exception e) {
        }
        return null;
    }
    private void printReceipt(Customer customer, Car car, int days, double totalPrice) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        StringBuilder receipt = new StringBuilder();
        receipt.append("==================================\n");
        receipt.append("          CAR RENTAL RECEIPT          \n");
        receipt.append("==================================\n\n");
        receipt.append("Date: ").append(currentDate).append("\n\n");
        receipt.append("CUSTOMER INFORMATION\n");
        receipt.append("---------------------\n");
        receipt.append("ID: ").append(customer.getId()).append("\n");
        receipt.append("Name: ").append(customer.getName()).append("\n");
        receipt.append("Address: ").append(customer.getAddress()).append("\n");
        receipt.append("Phone: ").append(customer.getPhone()).append("\n\n");
        receipt.append("VEHICLE INFORMATION\n");
        receipt.append("---------------------\n");
        receipt.append("Registration: ").append(car.getRegistration()).append("\n");
        receipt.append("Brand: ").append(car.getBrand()).append("\n");
        receipt.append("Model: ").append(car.getModel()).append("\n");
        receipt.append("Price per Day: ₱").append(String.format("%.2f", car.getPrice())).append("\n\n");
        receipt.append("RENTAL DETAILS\n");
        receipt.append("---------------------\n");
        receipt.append("Rental Duration: ").append(days).append(" day(s)\n");
        receipt.append("Total Amount: ₱").append(String.format("%.2f", totalPrice)).append("\n\n");
        receipt.append("==================================\n");
        receipt.append("Thank you for choosing our service!\n");
        receipt.append("==================================\n");
        JTextArea textArea = new JTextArea(receipt.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 500));
        JOptionPane.showMessageDialog(this,
                scrollPane,
                "Rental Receipt",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private void returnToCustomerScreen() {
        this.dispose();
        try {
            Class<?> customerClass = Class.forName("customer");
            java.lang.reflect.Method returnMethod =
                    customerClass.getMethod("returnToCustomerScreen");
            returnMethod.invoke(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Unable to return to customer screen. The customer class may not be available.",
                    "Navigation Error",
                    JOptionPane.ERROR_MESSAGE);
            SwingUtilities.invokeLater(() -> new manage().setVisible(true));
        }
    }
    private void addSampleData() {
        addCar("ABC-1234", "Toyota", "Camry", 1450.99, "Available");
        addCar("XYZ-5678", "Honda", "Civic", 1539.99, "Rented");
        addCar("DEF-9012", "Ford", "Focus", 4135.50, "Maintenance");
        addCar("GHI-3456", "Nissan", "Altima", 1642.00, "Reserved");
        addCar("JKL-7890", "Hyundai", "Elantra", 1838.75, "Available");
    }
    private void addCar(String registration, String brand, String model, double price, String status) {
        Car car = new Car(registration, brand, model, price, status);
        carList.add(car);
        Object[] rowData = {registration, brand, model, String.format("%.2f", price), status};
        tableModel.addRow(rowData);
    }
    private void displayCarDetails(int row) {
        Car car = carList.get(row);
        registrationTextField.setText(car.getRegistration());
        brandTextField.setText(car.getBrand());
        modelTextField.setText(car.getModel());
        priceTextField.setText(String.format("%.2f", car.getPrice()));
        statusComboBox.setSelectedItem(car.getStatus());
        registrationTextField.setEditable(false);
    }
    private boolean saveCar() {
        if (!validateInput()) {
            return false;
        }
        String registration = registrationTextField.getText();
        String brand = brandTextField.getText();
        String model = modelTextField.getText();
        double price;
        try {
            price = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String status = (String) statusComboBox.getSelectedItem();
        if (selectedRow == -1) {
            for (Car car : carList) {
                if (car.getRegistration().equals(registration)) {
                    JOptionPane.showMessageDialog(this,
                            "Registration number already exists",
                            "Duplicate Registration",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            Car newCar = new Car(registration, brand, model, price, status);
            carList.add(newCar);
            Object[] rowData = {registration, brand, model, String.format("%.2f", price), status};
            tableModel.addRow(rowData);
            JOptionPane.showMessageDialog(this,
                    "Car saved successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            resetForm();
            return true;
        } else {
            return updateCar();
        }
    }
    private boolean updateCar() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a car to update",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!validateInput()) {
            return false;
        }
        String registration = registrationTextField.getText();
        String brand = brandTextField.getText();
        String model = modelTextField.getText();
        double price;
        try {
            price = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        String status = (String) statusComboBox.getSelectedItem();
        Car car = carList.get(selectedRow);
        car.setBrand(brand);
        car.setModel(model);
        car.setPrice(price);
        car.setStatus(status);
        tableModel.setValueAt(brand, selectedRow, 1);
        tableModel.setValueAt(model, selectedRow, 2);
        tableModel.setValueAt(String.format("%.2f", price), selectedRow, 3);
        tableModel.setValueAt(status, selectedRow, 4);
        JOptionPane.showMessageDialog(this,
                "Car updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        resetForm();
        return true;
    }
    private void deleteCar() {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a car to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this car?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            carList.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this,
                    "Car deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            resetForm();
        }
    }
    private void resetForm() {
        registrationTextField.setText("");
        brandTextField.setText("");
        modelTextField.setText("");
        priceTextField.setText("");
        statusComboBox.setSelectedIndex(0);
        registrationTextField.setEditable(true);
        selectedRow = -1;
        carTable.clearSelection();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        rentButton.setEnabled(false);
    }
    private boolean validateInput() {
        if (registrationTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Registration number cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (brandTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Brand cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (modelTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Model cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (priceTextField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Price cannot be empty",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            double price = Double.parseDouble(priceTextField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Price must be greater than zero",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private class Car {
        private String registration;
        private String brand;
        private String model;
        private double price;
        private String status;
        public Car(String registration, String brand, String model, double price, String status) {
            this.registration = registration;
            this.brand = brand;
            this.model = model;
            this.price = price;
            this.status = status;
        }
        public String getRegistration() {
            return registration;
        }
        public void setRegistration(String registration) {
            this.registration = registration;
        }
        public String getBrand() {
            return brand;
        }
        public void setBrand(String brand) {
            this.brand = brand;
        }
        public String getModel() {
            return model;
        }
        public void setModel(String model) {
            this.model = model;
        }
        public double getPrice() {
            return price;
        }
        public void setPrice(double price) {
            this.price = price;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
    }
    public void setVisible(boolean b) {
        super.setVisible(b);
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
                new manage().setVisible(true);
            }
        });
    }
}