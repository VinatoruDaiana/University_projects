import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCalendarPage {
    private int userId;  // ID-ul utilizatorului conectat
    private JFrame frame;
    private JList<String> reservationsList;  // Lista pentru afișarea programărilor
    private DefaultListModel<String> listModel;  // Modelul listei
    private JButton modifyButton;  // Butonul pentru modificare

    public UserCalendarPage(int userId) {
        this.userId = userId;  // Salvăm ID-ul utilizatorului pentru utilizare
        frame = new JFrame("Calendarul Rezervărilor");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titlu
        JLabel titleLabel = new JLabel("Calendar rezervări", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.PINK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        // ComboBox pentru selectarea lunii
        JLabel monthLabel = new JLabel("Selectează luna:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(monthLabel, gbc);

        JComboBox<String> monthComboBox = new JComboBox<>(new String[]{
                "Ianuarie", "Februarie", "Martie", "Aprilie", "Mai", "Iunie",
                "Iulie", "August", "Septembrie", "Octombrie", "Noiembrie", "Decembrie"
        });
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(monthComboBox, gbc);

        // Listă pentru afișarea programărilor
        JLabel reservationsLabel = new JLabel("Programările lunii:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(reservationsLabel, gbc);

        listModel = new DefaultListModel<>();
        reservationsList = new JList<>(listModel);
        reservationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Permite selecție unică
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(new JScrollPane(reservationsList), gbc);

        // Buton pentru modificare
        modifyButton = new JButton("Modificare");
        modifyButton.setBackground(Color.PINK);
        modifyButton.setForeground(Color.WHITE);
        modifyButton.setEnabled(false);  // Buton dezactivat până se selectează o programare
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(modifyButton, gbc);

        // Eveniment pentru activarea butonului "Modificare"
        reservationsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !reservationsList.isSelectionEmpty()) {
                modifyButton.setEnabled(true);  // Activează butonul dacă se selectează o programare
            }
        });

        // Eveniment la apăsarea butonului "Modificare"
        modifyButton.addActionListener(e -> modifyReservation());

        // Eveniment la selectarea unei luni
        monthComboBox.addActionListener(e -> {
            String selectedMonth = (String) monthComboBox.getSelectedItem();
            showUserReservationsForMonth(selectedMonth);
        });

        frame.setVisible(true);
    }

    private void showUserReservationsForMonth(String selectedMonth) {
        listModel.clear();  // Curățăm lista înainte de afișare

        // Mapare lună română -> engleză
        Map<String, String> monthTranslation = new HashMap<>();
        monthTranslation.put("Ianuarie", "January");
        monthTranslation.put("Februarie", "February");
        monthTranslation.put("Martie", "March");
        monthTranslation.put("Aprilie", "April");
        monthTranslation.put("Mai", "May");
        monthTranslation.put("Iunie", "June");
        monthTranslation.put("Iulie", "July");
        monthTranslation.put("August", "August");
        monthTranslation.put("Septembrie", "September");
        monthTranslation.put("Octombrie", "October");
        monthTranslation.put("Noiembrie", "November");
        monthTranslation.put("Decembrie", "December");

        // Conversie lună
        String monthInEnglish = monthTranslation.get(selectedMonth);
        System.out.println("Luna selectată (în engleză): " + monthInEnglish);  // Debug

        String query = "SELECT DAY(data_ora_programare) AS zi, TIME(data_ora_programare) AS ora, " +
                "s.nume_salon, p.status FROM programari p " +
                "JOIN saloane s ON p.id_salon = s.id " +
                "WHERE MONTHNAME(data_ora_programare) = ? AND p.id_utilizator = ?";

        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, monthInEnglish);  // Denumirea în engleză pentru SQL
            preparedStatement.setInt(2, userId);  // ID-ul utilizatorului conectat
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int zi = resultSet.getInt("zi");
                String ora = resultSet.getString("ora");
                String salon = resultSet.getString("nume_salon");
                String status = resultSet.getString("status");

                // Debug - afișăm fiecare programare
                System.out.println("Programare: " + zi + " " + selectedMonth + " " + salon + " la " + ora + " (" + status + ")");

                listModel.addElement(zi + " " + selectedMonth + " - " + salon + " la " + ora + " (" + status + ")");
            }

            if (listModel.isEmpty()) {
                listModel.addElement("Nu există programări pentru luna " + selectedMonth + ".");
            }

        } catch (Exception e) {
            e.printStackTrace();
            listModel.addElement("A apărut o eroare la încărcarea programărilor.");
        }
    }


    // Modificarea unei programări
    private void modifyReservation() {
        int selectedIndex = reservationsList.getSelectedIndex();
        if (selectedIndex == -1 || listModel.get(selectedIndex).contains("Nu există programări")) {
            return;  // Dacă nu e selectată o programare validă, nu face nimic
        }

        String newDate = JOptionPane.showInputDialog(frame, "Introduceți noua dată (YYYY-MM-DD):");
        String newTime = JOptionPane.showInputDialog(frame, "Introduceți noua oră (HH:MM):");

        if (newDate != null && newTime != null) {
            String selectedReservation = listModel.get(selectedIndex);
            String[] details = selectedReservation.split(" - | la | \\(");
            String salonName = details[1];

            String updateQuery = "UPDATE programari SET data_ora_programare = ? " +
                    "WHERE id_utilizator = ? AND id_salon = (SELECT id FROM saloane WHERE nume_salon = ?)";

            try (Connection connection = Conexiune.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newDate + " " + newTime);
                preparedStatement.setInt(2, userId);
                preparedStatement.setString(3, salonName);
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Programarea a fost modificată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                    showUserReservationsForMonth((String) reservationsList.getSelectedValue());
                } else {
                    JOptionPane.showMessageDialog(frame, "Eroare la modificare. Reîncercați!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "A apărut o eroare la modificarea programării!", "Eroare", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
