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

public class VeziSalarRestul {

    public int id_utilizator;
    public int functie;
    private JPanel veziSalariuJPanel;
    private JTextField anTextField;
    private JTextField lunaTextField;
    private JButton cautaButton;
    private JTable salariiJTable;
    private JLabel anJLabel;
    private JLabel lunaJLabel;
    private JLabel back;
    private JLabel login;

    public VeziSalarRestul(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(veziSalariuJPanel);
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
                String query;
                String nume = null;
                String prenume = null;
                ResultSet rs;
                int a = id_utilizator;
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                    Statement st = con.createStatement();

                    query = "SELECT nume, prenume FROM utilizator WHERE id_utilizator=" + a + ";";
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        nume = rs.getString(1);
                        prenume = rs.getString(2);
                    }

                    CallableStatement cs = con.prepareCall("{ CALL vezi_salariu(?, ?, ?, ?) }");
                    cs.setString(1, nume);
                    cs.setString(2, prenume);
                    cs.setString(3, anTextField.getText());
                    cs.setString(4, lunaTextField.getText());
                    cs.execute();

                    rs = cs.getResultSet();
                    if (rs != null) {
                        DefaultTableModel model = (DefaultTableModel) salariiJTable.getModel();
                        model.setRowCount(0); //nou

                        String[] numeColoaneDorite = {"Salariat", "Ore lucrate", "Salar(lei)", "Bonus(lei)", "Total(lei)", "Luna"};
                        model.setColumnIdentifiers(numeColoaneDorite);

                        while (rs.next()) {
                            String[] row = {rs.getString(1), rs.getString(2), rs.getString(3),
                                    rs.getString(4), rs.getString(5), rs.getString(6)};
                            model.addRow(row);
                        }
                    }

                    st.close();
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 parent.dispose();
                 if(functie==1) {
                     InspectorOpFinanciare op = new InspectorOpFinanciare(id_utilizator, functie);
                     op.setVisible(true);
                 }
                 else if(functie==3){
                    ReceptionerOpFinanciare op=new ReceptionerOpFinanciare(id_utilizator, functie);
                     op.setVisible(true);}
                 else if(functie==4){
                    AsistentOpFinanciare op=new AsistentOpFinanciare(id_utilizator, functie);
                     op.setVisible(true);}
                 else if(functie==5){
                    MedicOpFinanciare op=new MedicOpFinanciare(id_utilizator, functie);
                     op.setVisible(true);}
            }
        });
        login.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                 parent.dispose();
                Clinica op=new Clinica();
            }
        });
    }

}
