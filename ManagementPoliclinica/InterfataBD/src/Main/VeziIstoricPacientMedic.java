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


public class VeziIstoricPacientMedic {

    public int id_utilizator;
    public int functie;
    private JPanel veziIstoricPacientJPanel;
    private JTextField numePacientTextField;
    private JTextField prenumePacientTextField;
    private JTable istoricPacientJTable;
    private JLabel numePacientJLabel;
    private JLabel prenumePacientJLabel;
    private JButton cautaButton;
    private JLabel back;
    private JLabel login;

    public VeziIstoricPacientMedic(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(veziIstoricPacientJPanel);
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
                int idPacient;
                if (numePacientTextField.getText().isEmpty() || prenumePacientTextField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lipsesc date!");
                } else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();

                        rs = st.executeQuery("SELECT id_pacient FROM pacient WHERE nume='" + numePacientTextField.getText() + "' " +
                                "AND prenume='" + prenumePacientTextField.getText() + "';");

                        if (rs.next()) {
                            idPacient = rs.getInt(1);

                            CallableStatement cs = con.prepareCall("{ CALL istoric_pacient(?) }");
                            cs.setInt(1, idPacient);
                            cs.execute();

                            rs = cs.getResultSet();
                            if (rs != null) {
                                DefaultTableModel model = (DefaultTableModel) istoricPacientJTable.getModel();
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
                                            rs.getString(4), rs.getString(5), rs.getString(6),
                                            rs.getString(7), rs.getString(8), rs.getString(9)};
                                    model.addRow(row);

                                }
                            }

                            JOptionPane.showMessageDialog(null, "Pacientul a fost gasit!");
                        } else {
                            JOptionPane.showMessageDialog(null, "Pacientul nu a fost gasit!");
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
