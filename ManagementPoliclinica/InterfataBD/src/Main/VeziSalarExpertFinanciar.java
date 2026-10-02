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

public class VeziSalarExpertFinanciar {

    public int id_utilizator;
    public int functie;
    private JPanel veziSalarInspectorJPanel;
    private JTextField numeTextField;
    private JTextField prenumeTextField;
    private JTextField anTextField;
    private JTextField lunaTextField;
    private JButton cautaButton;
    private JLabel numeJLabel;
    private JLabel prenumeJLabel;
    private JLabel anJLabel;
    private JLabel lunaJLabel;
    private JTable salariiJTable;
    private JLabel back;
    private JLabel login;


    public VeziSalarExpertFinanciar(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        parent.setContentPane(veziSalarInspectorJPanel);
        parent.setTitle("Inspector / Vezi salarii");
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
                if (numeTextField.getText().isEmpty() || prenumeTextField.getText().isEmpty() || anTextField.getText().isEmpty() || lunaTextField.getText().isEmpty())
                    JOptionPane.showMessageDialog(null, "Introduceti toate datele!");
                else {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");
                        Statement st = con.createStatement();
                        CallableStatement cs = con.prepareCall("{ CALL vezi_salariu(?, ?, ?, ?) }");
                        cs.setString(1, numeTextField.getText());
                        cs.setString(2, prenumeTextField.getText());
                        cs.setString(3, anTextField.getText());
                        cs.setString(4, lunaTextField.getText());
                        //cs.registerOutParameter(4, Types.INTEGER);
                        cs.execute();

                        ResultSet rs = cs.getResultSet();
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
                ExFinOpFinanciare op=new ExFinOpFinanciare(id_utilizator, functie);
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
