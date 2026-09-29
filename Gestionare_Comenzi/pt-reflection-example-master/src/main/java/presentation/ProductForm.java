package presentation;

import bll.ProductBLL;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.NoSuchElementException;

public class ProductForm extends JFrame {

    private JTextField idField, nameField, stockField, priceField;
    private JTextArea productListArea;

    public ProductForm() {
        setTitle("Product Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Stock:"));
        stockField = new JTextField();
        inputPanel.add(stockField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        Dimension buttonSize = new Dimension(120, 25);



        JButton addButton = new JButton("Add Product");
        addButton.setPreferredSize(buttonSize);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Product");
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduct();
            }
        });
        buttonPanel.add(deleteButton);

        JButton updateButton = new JButton("Update Product");
        updateButton.setPreferredSize(buttonSize);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduct();
            }
        });
        buttonPanel.add(updateButton);

        JButton listButton = new JButton("List  Products");
        listButton.setPreferredSize(buttonSize);
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listAllProducts();
            }
        });
        buttonPanel.add(listButton);

        add(buttonPanel, BorderLayout.CENTER);

        productListArea = new JTextArea();
        productListArea.setEditable(false);
        add(new JScrollPane(productListArea), BorderLayout.SOUTH);
    }

    private void addProduct() {
        String name = nameField.getText();
        int stock = Integer.parseInt(stockField.getText());
        int price = Integer.parseInt(priceField.getText());
        Product product = new Product(name, stock, price);
        ProductBLL.insertProduct(product);
        JOptionPane.showMessageDialog(this, "Product added successfully!");
        clearFields();
    }

    private void deleteProduct() {
        int id = Integer.parseInt(idField.getText());
        ProductBLL.deleteProduct(id);
        JOptionPane.showMessageDialog(this, "Product deleted successfully!");
        clearFields();
    }

    private void updateProduct() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        int stock = Integer.parseInt(stockField.getText());
        int price = Integer.parseInt(priceField.getText());
        Product product = new Product(id, name, stock, price);
        ProductBLL.updateProduct(product);
        JOptionPane.showMessageDialog(this, "Product updated successfully!");
        clearFields();
    }

    private void listAllProducts() {
        List<Product> products = ProductBLL.findAllProducts();
        productListArea.setText("");
        for (Product product : products) {
            productListArea.append(product.toString() + "\n");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        stockField.setText("");
        priceField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ProductForm().setVisible(true);
            }
        });
    }
}


