package presentation;

import bll.OrderBLL;
import model.Order;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.NoSuchElementException;

public class OrderForm extends JFrame {

    private JTextField orderIdField, clientIdField, productIdField, quantityField;
    private JTextArea orderListArea;

    public OrderForm() {
        setTitle("Order Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Order ID:"));
        orderIdField = new JTextField();
        inputPanel.add(orderIdField);

        inputPanel.add(new JLabel("Client ID:"));
        clientIdField = new JTextField();
        inputPanel.add(clientIdField);

        inputPanel.add(new JLabel("Product ID:"));
        productIdField = new JTextField();
        inputPanel.add(productIdField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        Dimension buttonSize = new Dimension(120, 25);

        JButton addButton = new JButton("Add Order");
        addButton.setPreferredSize(buttonSize);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Order");
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });
        buttonPanel.add(deleteButton);

        JButton listButton = new JButton("List All Orders");
        listButton.setPreferredSize(buttonSize);
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listAllOrders();
            }
        });
        buttonPanel.add(listButton);

        add(buttonPanel, BorderLayout.CENTER);

        orderListArea = new JTextArea();
        orderListArea.setEditable(false);
        add(new JScrollPane(orderListArea), BorderLayout.SOUTH);
    }

    private void addOrder() {
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            int clientId = Integer.parseInt(clientIdField.getText());
            int productId = Integer.parseInt(productIdField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            Order order = new Order(orderId, clientId, productId, quantity);
            OrderBLL.insertOrder(order);
            JOptionPane.showMessageDialog(this, "Order added successfully!");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Order ID, Client ID, Product ID, and Quantity.");
        }
    }

    private void deleteOrder() {
        try {
            int orderId = Integer.parseInt(orderIdField.getText());
            OrderBLL.deleteOrder(orderId);
            JOptionPane.showMessageDialog(this, "Order deleted successfully!");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for Order ID.");
        }
    }

    private void listAllOrders() {
        List<Order> orders = OrderBLL.findAllOrders();
        orderListArea.setText("");
        for (Order order : orders) {
            orderListArea.append(order.toString() + "\n");
        }
    }

    private void clearFields() {
        orderIdField.setText("");
        clientIdField.setText("");
        productIdField.setText("");
        quantityField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrderForm().setVisible(true));
    }
}