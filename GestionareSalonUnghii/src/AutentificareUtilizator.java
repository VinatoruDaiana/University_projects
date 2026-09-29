import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AutentificareUtilizator {

    public static User userAutentificat;  // Utilizator autentificat
    private static String selectedRole = "Utilizator";  // Rol implicit

    public static void showLoginPage() {
        // Fereastra principală
        JFrame frame = new JFrame("Nail Salon Sync - Autentificare");
        frame.setSize(400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 1));

        // Titlu
        JLabel titleLabel = new JLabel("Nail Salon Sync", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.PINK);

        // Selecție rol
        JPanel rolePanel = new JPanel();
        JLabel roleLabel = new JLabel("Selectează rolul:");
        JRadioButton userButton = new JRadioButton("Utilizator");
        JRadioButton adminButton = new JRadioButton("Admin");
        JRadioButton manichiuristaButton = new JRadioButton("Manichiurista");  // Adăugat rolul Manichiuristă
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(userButton);
        roleGroup.add(adminButton);
        roleGroup.add(manichiuristaButton);
        userButton.setSelected(true);  // Utilizator selectat implicit
        rolePanel.add(roleLabel);
        rolePanel.add(userButton);
        rolePanel.add(adminButton);
        rolePanel.add(manichiuristaButton);

        // Câmpuri pentru email și parolă
        JTextField emailField = new JTextField();
        emailField.setToolTipText("Introdu adresa de email");

        JTextField passwordField = new JTextField();  // Parola vizibilă
        passwordField.setToolTipText("Introdu parola vizibilă");

        // Buton de autentificare
        JButton loginButton = new JButton("Conectare");
        loginButton.setBackground(Color.PINK);
        loginButton.setForeground(Color.WHITE);

        // Label pentru status
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        // Eveniment la selectarea rolului
        userButton.addActionListener(e -> selectedRole = "Utilizator");
        adminButton.addActionListener(e -> selectedRole = "Admin");
        manichiuristaButton.addActionListener(e -> selectedRole = "Manichiurista");  // Setăm rolul Manichiuristă

        // Eveniment la apăsarea butonului "Conectare"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = passwordField.getText().trim();

                // Autentificare utilizator
                userAutentificat = authenticateUser(email, password);

                if (userAutentificat != null) {
                    if (userAutentificat.getRol().equalsIgnoreCase("Admin")) {
                        JOptionPane.showMessageDialog(frame, "Te-ai conectat ca admin!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();  // Închidem fereastra de autentificare
                        new AdminDashboard();  // Deschidem dashboard-ul de admin
                    } else if (userAutentificat.getRol().equalsIgnoreCase("User")) {
                        JOptionPane.showMessageDialog(frame, "Te-ai conectat ca utilizator!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();  // Închidem fereastra de autentificare
                        new HomePageUser(userAutentificat);  // Transmitem utilizatorul autentificat
                    } else if (userAutentificat.getRol().equalsIgnoreCase("Manichiurista")) {
                        JOptionPane.showMessageDialog(frame, "Te-ai conectat ca Manichiuristă!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();  // Închidem fereastra de autentificare
                        new ManiDashboard();  // Apelăm clasa pentru Manichiuristă
                    } else {
                        JOptionPane.showMessageDialog(frame, "Acces interzis! Acest rol nu are permisiune pentru această pagină.", "Eroare", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Email sau parolă incorectă pentru " + selectedRole.toLowerCase(), "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adăugare componente pe fereastră
        frame.add(titleLabel);
        frame.add(rolePanel);
        frame.add(emailField);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(statusLabel);

        frame.setVisible(true);
    }

    // Metodă pentru autentificarea utilizatorului
    private static User authenticateUser(String email, String password) {
        String query = "SELECT id, nume, email, telefon, rol, parola FROM utilizatori WHERE email = ? AND parola = ?";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nume = resultSet.getString("nume");
                String telefon = resultSet.getString("telefon");
                String rol = resultSet.getString("rol");
                String parola = resultSet.getString("parola");

                System.out.println("Autentificare reușită! Utilizator: " + nume + " (ID: " + id + ")");
                return new User(id, nume, email, telefon, rol, parola);  // Creare obiect User
            } else {
                System.out.println("Credențiale incorecte.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Dacă autentificarea eșuează
    }
}
