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

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class VeziPacientiProgramatiMedic {

    public int id_utilizator;
    public int functie;
    private JPanel veziPacientiProgramatiMedicJPanel;
    private JTextField dataTextField;
    private JTextField lunaTextField;
    private JButton cautaButton;
    private JTable pacientiProgramatiMedicJTable;
    private JLabel dataJLabel;
    private JLabel back;
    private JLabel login;

    public VeziPacientiProgramatiMedic(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(veziPacientiProgramatiMedicJPanel);
        parent.setTitle("Emite bon");
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


            parent.setVisible(true);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cautaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs;
                int id = 1;
                if (dataTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lipsesc date!");
                } else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();

                        CallableStatement cs = con.prepareCall("{ CALL vezi_programari_doctor(?, ?) }");
                        cs.setInt(1, id);
                        cs.setString(2, dataTextField.getText());
                        cs.execute();

                        rs = cs.getResultSet();
                        if (rs != null) {
                            DefaultTableModel model = (DefaultTableModel) pacientiProgramatiMedicJTable.getModel();
                            model.setRowCount(0); //nou

                            String[] numeColoaneDorite = {"Pacient", "Serviciu", "Ora inceput", "Ora final", "Adresa"};
                            model.setColumnIdentifiers(numeColoaneDorite);

                            while (rs.next()) {
                                String[] row = {rs.getString(1), rs.getString(2), rs.getString(3),
                                        rs.getString(4), rs.getString(5)};
                                model.addRow(row);
                            }
                        }

                        st.close();
                        con.close();
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
                MedicGestActivitati op=new MedicGestActivitati(id_utilizator, functie);
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


