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

public class EmiteBon {

    public int id_utilizator;
    public int functie;
    private JPanel emiteBonJPanel;
    private JTextField idBonTextField;
    private JButton emiteBonButton;
    private JLabel dataBonLabel;
    private JLabel oraBonLabel;
    private JLabel serviciuLabel;
    private JLabel pretServiciuLabel;
    private JLabel totalLabel;
    private JLabel totalPretLabel;
    private JPanel bonJPanel;
    private JLabel back;
    private JLabel login;

    public EmiteBon(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        JFrame parent = new JFrame();
        createUIComponents();
        parent.setContentPane(emiteBonJPanel);
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

           /* query = "CALL emite_bon(" + idBonTextField.getText() + ");";
            rs = st.executeQuery(query);
            // Iterarea rezultatelor și adăugarea acestora în Vector
            while (rs.next()) {
                //specializareJComboBox.addItem(rs.getString(1));
            }*/

            totalLabel.setText("Total: ");
            parent.setVisible(true);
            st.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        emiteBonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!idBonTextField.getText().isEmpty()) {
                    try {
                        Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/proiect_v1", "root", "ame_Amelia14");

                        Statement st = con.createStatement();
                        //String string = "CALL emite_bon(" + idBonTextField.getText() + ");";
                        //ResultSet rs = st.executeQuery(string);
                        //ResultSetMetaData rsmd=rs.getMetaData();

                        CallableStatement cs = con.prepareCall("{ CALL emite_bon(?, ?) }");
                        cs.setInt(1, Integer.parseInt(idBonTextField.getText()));
                        cs.registerOutParameter(2, Types.INTEGER);
                        cs.execute();
                        int result = cs.getInt(2);

                        if (result == 0) {
                            JOptionPane.showMessageDialog(null, "Bonul nu poate fi emis!");
                        } else {
                            if (result == 1) {
                                ResultSet rs = cs.getResultSet();
                                if (rs.next()) {
                                    dataBonLabel.setText("" + rs.getString("data_emitere_bon"));
                                    //oraBonLabel.setText("" + rs.getString("ora_emitere_bon"));
                                    serviciuLabel.setText("" + rs.getString("serviciu"));
                                    pretServiciuLabel.setText("" + rs.getInt("total"));
                                    totalLabel.setText("Total: ");
                                    totalPretLabel.setText("" + rs.getInt("total"));
                                }

                                JOptionPane.showMessageDialog(null, "Bonul a fost emis!");
                            } else
                                JOptionPane.showMessageDialog(null, "Bonul a fost emis deja!");

                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Lipsesc date!");
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        dataBonLabel = new JLabel();
        oraBonLabel = new JLabel();
        serviciuLabel = new JLabel();
        pretServiciuLabel = new JLabel();
        totalLabel = new JLabel();
        totalPretLabel = new JLabel();

    }

}