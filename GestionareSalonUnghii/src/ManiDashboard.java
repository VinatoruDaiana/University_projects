import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ManiDashboard {
    private JFrame frame;
    private JTable programariTable;
    private JButton modificaButton;
    private JButton anuleazaButton;
    private JTextField dateField;

    public ManiDashboard() {
        frame = new JFrame("Dashboard Manichiuristă - Gestionare programări");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Dashboard Manichiuristă", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.PINK);
        frame.add(titleLabel, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BorderLayout());

        // Introducerea datei
        JPanel dataPanel = new JPanel();
        JLabel dateLabel = new JLabel("Introduceți data (YYYY-MM-DD): ");
        dateField = new JTextField(10);
        JButton searchButton = new JButton("Caută Programări");
        searchButton.addActionListener(e -> loadProgramariForDate(dateField.getText().trim()));
        dataPanel.add(dateLabel);
        dataPanel.add(dateField);
        dataPanel.add(searchButton);
        panelCentral.add(dataPanel, BorderLayout.NORTH);

        // Tabel pentru programări
        programariTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(programariTable);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelCentral, BorderLayout.CENTER);

        // Butoane Modificare și Anulare
        JPanel buttonPanel = new JPanel();
        modificaButton = new JButton("Modificare");
        anuleazaButton = new JButton("Anulare");

        modificaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = programariTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Vă rugăm să selectați o programare pentru modificare!", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                modificaProgramare(selectedRow);
            }
        });

        anuleazaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = programariTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Vă rugăm să selectați o programare pentru anulare!", "Eroare", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                anuleazaProgramare(selectedRow);
            }
        });

        buttonPanel.add(modificaButton);
        buttonPanel.add(anuleazaButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // Încărcare programări pentru o anumită dată
    private void loadProgramariForDate(String date) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID Programare");
        model.addColumn("Nume Utilizator");
        model.addColumn("Serviciu");
        model.addColumn("Locație");
        model.addColumn("Salon");
        model.addColumn("Data și Ora");
        model.addColumn("Status");

        String query = "SELECT p.id, u.nume, s.denumire, l.adresa, sa.nume_salon, p.data_ora_programare, p.status " +
                "FROM programari p " +
                "JOIN utilizatori u ON p.id_utilizator = u.id " +
                "JOIN servicii s ON p.id_serviciu = s.id " +
                "JOIN locatii l ON p.id_locatie = l.id " +
                "JOIN saloane sa ON p.id_salon = sa.id " +
                "WHERE DATE(p.data_ora_programare) = ?";

        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(resultSet.getInt("id")));
                row.add(resultSet.getString("nume"));
                row.add(resultSet.getString("denumire"));
                row.add(resultSet.getString("adresa"));
                row.add(resultSet.getString("nume_salon"));
                row.add(resultSet.getString("data_ora_programare"));
                row.add(resultSet.getString("status"));
                model.addRow(row);
            }
            programariTable.setModel(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "A apărut o eroare la încărcarea programărilor!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modificarea unei programări
    private void modificaProgramare(int selectedRow) {
        int programareId = Integer.parseInt(programariTable.getValueAt(selectedRow, 0).toString());
        String newDate = JOptionPane.showInputDialog(frame, "Introduceți noua dată și oră (YYYY-MM-DD HH:MM):", "Modificare Programare", JOptionPane.PLAIN_MESSAGE);

        if (newDate == null || newDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Data și ora nu pot fi goale!", "Eroare", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String updateQuery = "UPDATE programari SET data_ora_programare = ? WHERE id = ?";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newDate);
            preparedStatement.setInt(2, programareId);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Programarea a fost modificată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            loadProgramariForDate(dateField.getText().trim());  // Reîncărcăm lista
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "A apărut o eroare la modificarea programării!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Anularea unei programări
    private void anuleazaProgramare(int selectedRow) {
        int programareId = Integer.parseInt(programariTable.getValueAt(selectedRow, 0).toString());
        String updateQuery = "UPDATE programari SET status = 'anulata' WHERE id = ?";
        try (Connection connection = Conexiune.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, programareId);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Programarea a fost anulată cu succes!", "Succes", JOptionPane.INFORMATION_MESSAGE);
            loadProgramariForDate(dateField.getText().trim());  // Reîncărcăm lista
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "A apărut o eroare la anularea programării!", "Eroare", JOptionPane.ERROR_MESSAGE);
        }
    }
}
