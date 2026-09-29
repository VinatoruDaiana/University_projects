import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HomePageUser {

    private JFrame frame;
    private JTextField dateField;
    private JComboBox<String> timeComboBox;
    private JComboBox<String> serviceComboBox;
    private JComboBox<String> locationComboBox;
    private JComboBox<Salon> salonComboBox;  // Noul JComboBox pentru saloane
    private JTextArea resultsArea;
    private User user;

    public HomePageUser(User user) {
        this.user = user;
        frame = new JFrame("Nail Salon Sync - Programările mele");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titlu
        JLabel titleLabel = new JLabel("Programările mele", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.PINK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        frame.add(titleLabel, gbc);

        // Câmp pentru dată
        JLabel dateLabel = new JLabel("Dată:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(dateLabel, gbc);

        dateField = new JTextField();
        dateField.setToolTipText("Introdu data dorită (ex: 2025-01-10)");
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(dateField, gbc);

        // Câmp pentru oră
        JLabel timeLabel = new JLabel("Oră:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(timeLabel, gbc);

        String[] times = {"09:00", "10:00","10:30", "11:00","12:00" ,"13:00","14:00" ,"15:00", "18:12"};
        timeComboBox = new JComboBox<>(times);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(timeComboBox, gbc);

        // Câmp pentru tip serviciu
        JLabel serviceLabel = new JLabel("Tip serviciu:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        frame.add(serviceLabel, gbc);

        String[] services = {"Manichiură clasică", "Manichiură semipermanentă", "Manichiură cu gel", "Întreținere unghii cu gel", "Pedichiură clasică", "Pedichiură semipermanentă"};
        serviceComboBox = new JComboBox<>(services);
        gbc.gridx = 1;
        gbc.gridy = 3;
        frame.add(serviceComboBox, gbc);

        // Câmp pentru locație
        JLabel locationLabel = new JLabel("Locație:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(locationLabel, gbc);

        String[] locations = getLocationsFromDatabase();  // Obținem locațiile din baza de date
        locationComboBox = new JComboBox<>(locations);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(locationComboBox, gbc);

        // Buton pentru căutarea saloanelor
        JButton searchButton = new JButton("Caută Saloane");
        searchButton.setBackground(Color.PINK);
        searchButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        frame.add(searchButton, gbc);

        // Câmp pentru afișarea saloanelor disponibile
        JLabel salonLabel = new JLabel("Selectează salon:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        frame.add(salonLabel, gbc);

        salonComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 6;
        frame.add(salonComboBox, gbc);

        // Eveniment la apăsarea butonului "Caută Saloane"
        searchButton.addActionListener(e -> {
            String selectedLocation = (String) locationComboBox.getSelectedItem();
            List<Salon> salons = getSalonsForLocation(selectedLocation);
            salonComboBox.removeAllItems();  // Curățăm lista de saloane
            if (salons.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Nu există saloane pentru această locație!", "Eroare", JOptionPane.ERROR_MESSAGE);
            } else {
                for (Salon salon : salons) {
                    salonComboBox.addItem(salon);
                }
            }
        });

        // Buton pentru confirmare
        JButton confirmButton = new JButton("Confirmare");
        confirmButton.setBackground(Color.PINK);
        confirmButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        frame.add(confirmButton, gbc);

        // Eveniment la apăsarea butonului "Confirmare"
        confirmButton.addActionListener(e -> {
            String selectedDate = dateField.getText().trim();
            String selectedTime = (String) timeComboBox.getSelectedItem();
            String selectedService = (String) serviceComboBox.getSelectedItem();
            Salon selectedSalon = (Salon) salonComboBox.getSelectedItem();  // Conversie la tipul Salon

            if (selectedDate.isEmpty() || selectedSalon == null) {
                JOptionPane.showMessageDialog(frame, "Te rugăm să introduci o dată validă și să selectezi un salon!", "Eroare", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    if (checkAvailability(selectedDate, selectedTime, selectedSalon)) {
                        JOptionPane.showMessageDialog(frame, "Programarea a fost efectuată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
                        insertAppointment(selectedDate, selectedTime, selectedService, selectedSalon);

                        // După confirmare, deschide UserCalendarPage
                        frame.dispose();  // Închidem fereastra curentă
                        new UserCalendarPage(user.getId());  // Deschidem pagina de calendar și transmitem ID-ul utilizatorului
                    } else {
                        JOptionPane.showMessageDialog(frame, "Salonul selectat este deja ocupat!", "Eroare", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "A apărut o eroare la conectarea la baza de date!", "Eroare", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        frame.setVisible(true);
    }

    // Obține locațiile din baza de date
    private String[] getLocationsFromDatabase() {
        List<String> locations = new ArrayList<>();
        try (Connection connection = Conexiune.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT adresa FROM locatii")) {
            while (resultSet.next()) {
                locations.add(resultSet.getString("adresa"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locations.toArray(new String[0]);
    }

    // Obține saloanele pentru o locație
    // Obține lista de saloane pentru locația selectată
    private List<Salon> getSalonsForLocation(String location) {
        List<Salon> salons = new ArrayList<>();
        String query = "SELECT s.id, s.nume_salon, s.id_locatie FROM saloane s JOIN locatii l ON s.id_locatie = l.id WHERE l.adresa = ?";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, location);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String numeSalon = resultSet.getString("nume_salon");
                int idLocatie = resultSet.getInt("id_locatie");
                salons.add(new Salon(id, numeSalon, idLocatie));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salons;
    }


    // Verifică disponibilitatea pentru un salon
    private boolean checkAvailability(String date, String time, Salon salon) throws Exception {
        String query = "SELECT COUNT(*) FROM programari WHERE data_ora_programare = ? AND id_salon = ?";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, date + " " + time);
            preparedStatement.setInt(2, salon.getId());  // Folosim id-ul salonului
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) == 0;
            }
        }
        return false;
    }


    // Introduce o nouă programare în baza de date
    private void insertAppointment(String date, String time, String service, Salon salon) throws Exception {
        String query = "INSERT INTO programari (id_utilizator, id_serviciu, id_locatie, id_salon, data_ora_programare, status) " +
                "VALUES (?, (SELECT id FROM servicii WHERE denumire = ?), ?, ?, ?, 'confirmata')";

        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, user.getId());  // Utilizăm direct ID-ul utilizatorului (INT)
            preparedStatement.setString(2, service);  // Denumirea serviciului
            preparedStatement.setInt(3, salon.getIdLocatie());  // ID locație
            preparedStatement.setInt(4, salon.getId());  // ID salon
            preparedStatement.setString(5, date + " " + time);  // Data și ora programării
            preparedStatement.executeUpdate();
        }
    }


}
