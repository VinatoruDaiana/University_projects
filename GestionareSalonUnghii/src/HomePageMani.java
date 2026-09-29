
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class HomePageMani {
    public static void showManichiuristaLoginPage() {
        // Fereastra pentru autentificarea manichiuristelor
        JFrame frame = new JFrame("Autentificare Manichiuristă");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1));

        // Titlu
        JLabel titleLabel = new JLabel("Autentificare Manichiuristă", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.PINK);

        // Câmpuri pentru email și parolă
        JTextField emailField = new JTextField();
        emailField.setToolTipText("Introdu adresa de email");

        JPasswordField passwordField = new JPasswordField();
        passwordField.setToolTipText("Introdu parola");

        // Buton de autentificare
        JButton loginButton = new JButton("Conectare");
        loginButton.setBackground(Color.PINK);
        loginButton.setForeground(Color.WHITE);

        // Label pentru status
        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);

        // Eveniment la apăsarea butonului "Conectare"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                // Verificăm autentificarea
                if (authenticateManichiurista(email, password)) {
                    JOptionPane.showMessageDialog(frame, "Te-ai conectat cu succes ca Manichiuristă!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    // Aici poți deschide o interfață dedicată pentru manichiuristă, dacă este necesar.
                } else {
                    JOptionPane.showMessageDialog(frame, "Email sau parolă incorectă!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adăugare componente pe fereastră
        frame.add(titleLabel);
        frame.add(emailField);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(statusLabel);

        frame.setVisible(true);
    }

    // Metodă pentru autentificarea utilizatorului de tip Manichiuristă
    private static boolean authenticateManichiurista(String email, String password) {
        String query = "SELECT id FROM utilizatori WHERE email = ? AND parola = ? AND rol = 'Manichiurista'";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();  // Returnează true dacă există un rezultat

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

