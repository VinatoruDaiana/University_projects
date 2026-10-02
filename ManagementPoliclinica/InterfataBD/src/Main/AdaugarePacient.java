package Main;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Locale;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class AdaugarePacient {

    public int id_utilizator;
    public int functie;
    private JPanel adaugarePacientJPanel;
    private JTextField numePacientTextField;
    private JTextField prenumePacientTextField;
    private JTextField cnpTextField;
    private JLabel numePacientJLabel;
    private JButton adaugareButton;
    private JLabel prenumePacientJLabel;
    private JLabel cnpJLabel;
    private JLabel back;
    private JLabel login;
    private JTable pacientiJTable;

    public AdaugarePacient(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(adaugarePacientJPanel);
        parent.setTitle("Adaugare Pacienti");
        parent.setDefaultCloseOperation(EXIT_ON_CLOSE);
        parent.setSize(900, 500);
        parent.setLocationRelativeTo(null);


        String query;
        ResultSet rs;
        ResultSetMetaData rsmd;
        DefaultTableModel model;
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
            Statement st = con.createStatement();


            rs = st.executeQuery("SELECT * FROM pacient ORDER BY id_pacient DESC;");
            if (rs != null) {
                model = (DefaultTableModel) pacientiJTable.getModel();
                rsmd = rs.getMetaData();
                model.setRowCount(0);

                int cols = rsmd.getColumnCount();
                String[] colName = new String[cols];
                for (int i = 0; i < cols; i++) {
                    colName[i] = rsmd.getColumnName(i + 1);
                }
                model.setColumnIdentifiers(colName);

                while (rs.next()) {
                    String[] row = {rs.getString(1), rs.getString(2), rs.getString(3),
                            rs.getString(4)};
                    model.addRow(row);

                }
            }

            parent.setVisible(true);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adaugareButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numePacient = numePacientTextField.getText();
                String prenumePacient = prenumePacientTextField.getText();
                String cnp = cnpTextField.getText();

                if (numePacientTextField.getText().isEmpty() || prenumePacientTextField.getText().isEmpty() || cnpTextField.getText().isEmpty())
                    JOptionPane.showMessageDialog(null, "Introduceti toate datele!");
                else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();
                        CallableStatement cs = con.prepareCall("{ CALL add_pacient(?, ?, ?, ?) }");
                        cs.setString(1, numePacientTextField.getText());
                        cs.setString(2, prenumePacientTextField.getText());
                        cs.setString(3, cnpTextField.getText());
                        cs.registerOutParameter(4, Types.INTEGER);
                        cs.execute();

                        int result = cs.getInt(4);

                        if (result == 0) {
                            JOptionPane.showMessageDialog(null, "Exista deja un pacient cu acest CNP!");
                        } else if (result == 1) {
                            JOptionPane.showMessageDialog(null, "Pacientul a fost inregistrat cu succes!");

                            ResultSet rs = st.executeQuery("SELECT * FROM pacient ORDER BY id_pacient DESC;");
                            if (rs != null) {
                                DefaultTableModel model = (DefaultTableModel) pacientiJTable.getModel();
                                ResultSetMetaData rsmd = rs.getMetaData();
                                model.setRowCount(0);

                                int cols = rsmd.getColumnCount();
                                String[] colName = new String[cols];
                                for (int i = 0; i < cols; i++) {
                                    colName[i] = rsmd.getColumnName(i + 1);
                                }
                                model.setColumnIdentifiers(colName);

                                while (rs.next()) {
                                    String[] row = {rs.getString(1), rs.getString(2), rs.getString(3),
                                            rs.getString(4)};
                                    model.addRow(row);

                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "CNP-ul trebuie sa aiba 13 cifre!");

                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 parent.dispose();
                ReceptionerGestActivitati op=new ReceptionerGestActivitati(id_utilizator, functie);
                op.setVisible(true);
            }
        });
        login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 parent.dispose();
                Clinica op=new Clinica();
                op.setVisible(true);
            }
        });
    }

}
