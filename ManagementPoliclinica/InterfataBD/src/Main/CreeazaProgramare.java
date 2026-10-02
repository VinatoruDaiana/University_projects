package Main;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Locale;

public class CreeazaProgramare extends JFrame {

    public int id_utilizator;
    public int functie;
    private JComboBox<String> specializareComboBox;
    private JLabel specializare;
    private JPanel ProgramareJPanel;
    private JTextField prenumePacientTextField;
    private JTextField numePacientTextField;
    private JComboBox unitateMedicalaJComboBox;
    private JComboBox specializareJComboBox;
    private JComboBox serviciuJComboBox;
    private JComboBox medicJComboBox;
    private JTable programariJTable;
    private JLabel numePacient;
    private JLabel prenumePacient;
    private JLabel unitateMedicala;
    private JLabel medic;
    private JLabel serviciu;
    private JButton inregistreazaProgramareaButton;
    private JTextField dataTextField;
    private JTextField oraTextField;
    private JLabel data;
    private JLabel ora;
    private JLabel back;
    private JLabel login;

    public CreeazaProgramare(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(ProgramareJPanel);
        parent.setTitle("Creaza programare");
        parent.setDefaultCloseOperation(EXIT_ON_CLOSE);
        parent.setSize(900, 500);
        parent.setLocationRelativeTo(null);

        String query;
        ResultSet rs;
        ResultSetMetaData rsmd;
        DefaultTableModel model;
        int id_pacient;
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
            Statement st = con.createStatement();

            query = "SELECT nume_specializare FROM specializare";
            rs = st.executeQuery(query);
            specializareJComboBox.addItem("");
            specializareJComboBox.setSelectedItem("");
            while (rs.next()) {
                specializareJComboBox.addItem(rs.getString(1));
            }

            unitateMedicalaJComboBox.addItem("");
            unitateMedicalaJComboBox.setSelectedItem("");
            rs = st.executeQuery("SELECT nume FROM unitate_medicala");
            while (rs.next()) {
                unitateMedicalaJComboBox.addItem(rs.getString(1));
            }

            parent.setVisible(true);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        specializareJComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("SELECT nume FROM servicii_medicale a INNER JOIN specializare b ON a.id_specializare = b.id_specializare WHERE b.nume_specializare='" + specializareJComboBox.getSelectedItem() + "';");

                    serviciuJComboBox.removeAllItems();
                    medicJComboBox.removeAllItems();

                    serviciuJComboBox.addItem("");
                    serviciuJComboBox.setSelectedItem("");
                    while (rs.next()) {
                        serviciuJComboBox.addItem(rs.getString(1));
                    }

                    //pb aici?
                    rs = st.executeQuery("SELECT CONCAT(c.nume, ' ', c.prenume) as nume_medic " +
                            " FROM medic a INNER JOIN utilizator c ON a.id_medic=c.id_utilizator " +
                            " WHERE a.id_specializare = (SELECT id_specializare FROM specializare WHERE nume_specializare='" + specializareJComboBox.getSelectedItem() + "');");
                    medicJComboBox.addItem("");
                    medicJComboBox.setSelectedItem("");
                    while (rs.next()) {
                        medicJComboBox.addItem(rs.getString(1));
                    }


                    st.close();
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        inregistreazaProgramareaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numePacient = numePacientTextField.getText();
                String prenumePacient = prenumePacientTextField.getText();
                String specializare = (String) specializareJComboBox.getSelectedItem();
                String medic = (String) medicJComboBox.getSelectedItem();
                String unitateMedicala = (String) unitateMedicalaJComboBox.getSelectedItem();
                String data = dataTextField.getText();
                String ora = oraTextField.getText();

                if (numePacient != null && prenumePacient != null && specializare != null && medic != null && unitateMedicala != null) {
                    try {
                        Connection con =DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();
                        ResultSet rs;
                        rs = st.executeQuery("SELECT id_pacient FROM pacient WHERE nume='" + numePacient + "' AND prenume='" + prenumePacient + "';");


                        if (rs.next()) {

                            CallableStatement cs = con.prepareCall("CALL creaza_programare(?, ?, ?, ?, ?, ?, ?, ?)");
                            cs.setString(1, numePacient);
                            cs.setString(2, prenumePacient);
                            cs.setString(3, medic);
                            cs.setString(4, specializare);
                            cs.setString(5, unitateMedicala);
                            cs.setString(6, data);
                            cs.setString(7, ora);
                            cs.registerOutParameter(8, Types.INTEGER);

                            cs.execute();
                            int result = cs.getInt(8);

                            if (result == 0)
                                JOptionPane.showMessageDialog(null, "Ora nu este disponibila!");
                            else {
                                JOptionPane.showMessageDialog(null, "Programarea a fost efectuata!");
                                try {
                                    rs = st.executeQuery("SELECT * FROM programari ORDER BY id_programari DESC;"); //etc
                                    ResultSetMetaData rsmd = rs.getMetaData();
                                    DefaultTableModel model = (DefaultTableModel) programariJTable.getModel();
                                    model.setRowCount(0); //nou

                                    int cols = rsmd.getColumnCount();
                                    String[] colName = new String[cols];
                                    for (int i = 0; i < cols; i++) {
                                        colName[i] = rsmd.getColumnName(i + 1);
                                    }
                                    model.setColumnIdentifiers(colName);

                                    while (rs.next()) {
                                        String[] row = {rs.getString(1), rs.getString(2), rs.getString(3),
                                                rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)};
                                        model.addRow(row);
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }

                            st.close();
                            con.close();
                        } else {
                            JOptionPane.showMessageDialog(null, "Pacientul nu este inregistrat!");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else
                    JOptionPane.showMessageDialog(null, "Lipsesc date!");
            }
        });

        medicJComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((String) serviciuJComboBox.getSelectedItem() != "" && (String) medicJComboBox.getSelectedItem() != "") {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();
                        ResultSet rs;
                        rs = st.executeQuery("CALL vezi_programari_doctor('" + medicJComboBox.getSelectedItem() + "', CURDATE());"); //etc
                        ResultSetMetaData rsmd = rs.getMetaData();
                        DefaultTableModel model = (DefaultTableModel) programariJTable.getModel();
                        model.setRowCount(0); //nou

                        int cols = rsmd.getColumnCount();
                        String[] colName = new String[cols];
                        for (int i = 0; i < cols; i++) {
                            colName[i] = rsmd.getColumnName(i + 1);
                        }
                        model.setColumnIdentifiers(colName);

                        while (rs.next()) {
                            String[] row = {rs.getString(1), rs.getString(2), rs.getString(3)};
                            model.addRow(row);
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