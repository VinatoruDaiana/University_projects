package presentation;

import model.Client;
import bll.ClientBLL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.NoSuchElementException;

public class ClientForm extends JFrame {

    private JTextField idField, nameField, addressField, emailField, ageField;
    private JTextArea clientListArea;

    public ClientForm() {
        setTitle("Client Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        inputPanel.add(addressField);

        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("Age:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        Dimension buttonSize = new Dimension(120, 25);


        JButton addButton = new JButton("Add Client");
        addButton.setPreferredSize(buttonSize);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Client");
        deleteButton.setPreferredSize(buttonSize);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });
        buttonPanel.add(deleteButton);

        JButton updateButton = new JButton("Update Client");
        updateButton.setPreferredSize(buttonSize);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClient();
            }
        });
        buttonPanel.add(updateButton);

        JButton findButton = new JButton("Find Client");
        findButton.setPreferredSize(buttonSize);
        findButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findClient();
            }
        });
        buttonPanel.add(findButton);

        JButton listButton = new JButton("List All Clients");
        listButton.setPreferredSize(buttonSize);
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listAllClients();
            }
        });
        buttonPanel.add(listButton);

        add(buttonPanel, BorderLayout.CENTER);

        clientListArea = new JTextArea();
        clientListArea.setEditable(false);
        add(new JScrollPane(clientListArea), BorderLayout.SOUTH);
    }

    private void addClient() {
        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        int age = Integer.parseInt(ageField.getText());
        Client client = new Client(name, address, email, age);
        ClientBLL.insertClient(client);
        JOptionPane.showMessageDialog(this, "Client added successfully!");
        clearFields();
    }

    private void deleteClient() {
        int id = Integer.parseInt(idField.getText());
        ClientBLL.deleteClient(id);
        JOptionPane.showMessageDialog(this, "Client deleted successfully!");
        clearFields();
    }

    private void updateClient() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        String address = addressField.getText();
        String email = emailField.getText();
        int age = Integer.parseInt(ageField.getText());
        Client client = new Client(id, name, address, email, age);
        ClientBLL.updateClient(client);
        JOptionPane.showMessageDialog(this, "Client updated successfully!");
        clearFields();
    }

    private void findClient() {
        int id = Integer.parseInt(idField.getText());
        try {
            Client client = ClientBLL.findClientById(id);
            nameField.setText(client.getName());
            addressField.setText(client.getAddress());
            emailField.setText(client.getEmail());
            ageField.setText(String.valueOf(client.getAge()));
        } catch (NoSuchElementException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void listAllClients() {
        List<Client> clients = ClientBLL.findAllClients();
        clientListArea.setText("");
        for (Client client : clients) {
            clientListArea.append(client.toString() + "\n");
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        addressField.setText("");
        emailField.setText("");
        ageField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientForm().setVisible(true);
            }
        });
    }

}