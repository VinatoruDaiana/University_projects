package Main;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

public class CreeazaRaport {

    public int id_utilizator;
    public int functie;
    private JTextField pacientTextField;
    private JTextField medicTextField;
    private JTextField asistentTextField;
    private JButton addButton;
    private JTextArea istoricTextArea;
    private JTextArea simptomeTextArea;
    private JTextArea investigatiiTextArea;
    private JTextArea diagnosticTextArea;
    private JTextArea recomandariTextArea;
    private JPanel creeazaRaportJPanel;
    private JTable raportJTable;
    private JLabel back;
    private JLabel login;

    public CreeazaRaport(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(creeazaRaportJPanel);
        parent.setTitle("Creeaza Programare");
        parent.setDefaultCloseOperation(EXIT_ON_CLOSE);
        parent.setSize(900, 700);
        parent.setLocationRelativeTo(null);

        String query;
        ResultSet rs;
        ResultSetMetaData rsmd;
        DefaultTableModel model;
        try {
            Connection con =DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
            Statement st = con.createStatement();


            parent.setVisible(true);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String pacient = pacientTextField.getText();
                String medic = medicTextField.getText();
                String asistent = asistentTextField.getText();
                String istoric = istoricTextArea.getText();
                String simptome = simptomeTextArea.getText();
                String investigatii = investigatiiTextArea.getText();
                String diagnostic = diagnosticTextArea.getText();
                String recomandari = recomandariTextArea.getText();

                if (pacientTextField.getText().isEmpty() || pacientTextField.getText().isEmpty()
                        || medicTextField.getText().isEmpty() || asistentTextField.getText().isEmpty())
                    JOptionPane.showMessageDialog(null, "Introduceti toate datele!");
                else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();
                        CallableStatement cs = con.prepareCall("{ CALL completaza_raport(?,?,?,?,?,?,?,?) }");
                        cs.setString(1, pacientTextField.getText());
                        cs.setString(2, medicTextField.getText());
                        cs.setString(3, asistentTextField.getText());
                        cs.setString(4, istoricTextArea.getText());
                        cs.setString(5, simptomeTextArea.getText());
                        cs.setString(6, investigatiiTextArea.getText());
                        cs.setString(7, diagnosticTextArea.getText());
                        cs.setString(8, recomandariTextArea.getText());
                        cs.execute();

                        ResultSet rs = st.executeQuery("SELECT * FROM raport ORDER BY id_raport DESC;");
                        if (rs != null) {
                            DefaultTableModel model = (DefaultTableModel) raportJTable.getModel();
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
                 if(functie==5){
                    MedicGestActivitati op =new MedicGestActivitati(id_utilizator, functie);
                     op.setVisible(true);}
                 if(functie==4){
                    AsistentGestActivitati op =new AsistentGestActivitati(id_utilizator, functie);
                     op.setVisible(true);}
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
