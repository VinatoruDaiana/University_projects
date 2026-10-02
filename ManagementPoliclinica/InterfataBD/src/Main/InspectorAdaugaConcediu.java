
package Main;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class InspectorAdaugaConcediu extends javax.swing.JFrame {
    Connection connection = null;
    PreparedStatement prp = null;
    public int id_utilizator;
    public int functie;

    public InspectorAdaugaConcediu(int id_utilizator, int functie) {
        this.id_utilizator=id_utilizator;
        this.functie=functie;
        initComponents();
        ImageIcon ic = new ImageIcon(getClass().getResource("/imagini/hospital.png"));
        this.setIconImage(ic.getImage());
    }
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel6= new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();//add medic
        jLabel20 = new javax.swing.JLabel();//login
        jLabel21 = new javax.swing.JLabel();//back
        numeField = new javax.swing.JTextField();
        prenumeField = new javax.swing.JTextField();
        dataInceputField = new javax.swing.JTextField();
        dataFinalField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();//nume
        jLabel2 = new javax.swing.JLabel();//prenume
        jLabel3 = new javax.swing.JLabel();//an
        jLabel4= new javax.swing.JLabel();//luna
        adaugaConcediuButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Adauga Concediu");

        jPanel2.setPreferredSize(new java.awt.Dimension(1260, 610));

        jPanel6.setBackground(new java.awt.Color(0, 153, 153));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Adauga Concediu");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagini/login.png"))); // NOI18N
        jLabel20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel20MouseClicked(evt);
            }
        });

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagini/back.png"))); // NOI18N
        jLabel21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel21MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(800, 800, 800)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addContainerGap())
        );

        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        numeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numeFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 153));
        jLabel1.setText("Nume");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        prenumeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prenumeFieldActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 153, 153));
        jLabel2.setText("Prenume");

        dataInceputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dataInceputFieldActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 153));

        jLabel3.setText("Data Inceput");


        dataFinalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dataFinalFieldActionPerformed(evt);
            }
        });
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 153, 153));
        jLabel4.setText("Data Final");

        adaugaConcediuButton = new javax.swing.JButton();
        adaugaConcediuButton.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        adaugaConcediuButton.setForeground(new java.awt.Color(0, 153, 153));
        adaugaConcediuButton.setText("Adauga concediu");
        adaugaConcediuButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adaugaConcediubtnMouseClicked(evt);
            }
        });

        clearButton = new javax.swing.JButton();
        clearButton.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        clearButton.setForeground(new java.awt.Color(0, 153, 153));
        clearButton.setText("Clear");
        clearButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearPbtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel1)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(numeField, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel2)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(prenumeField, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(dataInceputField, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(jLabel4)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(dataFinalField, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGap(123, 123, 123)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addComponent(adaugaConcediuButton)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(clearButton)
                                                        )
                                                        .addGap(123, 123, 123)

                                                )))));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel1)
                                                                        .addComponent(numeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(23, 23, 23)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel2)
                                                                        .addComponent(prenumeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(23, 23, 23)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel3)
                                                                        .addComponent(dataInceputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(23, 23, 23)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel4)
                                                                        .addComponent(dataFinalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(23, 23, 23)
                                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(adaugaConcediuButton)
                                                                        .addComponent(clearButton))
                                                                .addContainerGap(41, Short.MAX_VALUE)
                                                        )
                                                )))));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1229, Short.MAX_VALUE)
                                .addGap(28, 28, 28))
        );

        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 572, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(null);
    }


    private void adaugaConcediubtnMouseClicked(MouseEvent evt) {
        String nume = numeField.getText();
        String prenume = prenumeField.getText();
        String dataInceput = dataInceputField.getText();
        String dataFinal = dataFinalField.getText();

        try (Connection connection = ConexiuneSQL.Conexiune()) {
            if (connection != null) {
                try {
                    int userId = getUserId(connection, nume, prenume);
                    String sql = "{CALL adauga_concediu(?, ?, ?)}";
                    try (CallableStatement cst = connection.prepareCall(sql)) {
                        cst.setInt(1, userId);
                        cst.setDate(2, java.sql.Date.valueOf(dataInceput));
                        cst.setDate(3, java.sql.Date.valueOf(dataFinal));
                        cst.execute();

                        JOptionPane.showMessageDialog(null, "Concediul a fost adăugat cu succes!");
                        afisareConcediiUtilizator(connection, userId);                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Eroare: " + ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Eroare la stabilirea conexiunii cu baza de date: " + ex.getMessage());
        }
    }

    private void afisareConcediiUtilizator(Connection connection, int userId) {
        try {
            String query = "{CALL afiseaza_concedii_utilizator(?)}";
            try (CallableStatement cst = connection.prepareCall(query)) {
                cst.setInt(1, userId);

                try (ResultSet rs = cst.executeQuery()) {
                    // Crează JTable pentru a afișa datele
                    JFrame userTableFrame = new JFrame("Afișare Concedii Utilizator");
                    userTableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    userTableFrame.setSize(600, 400);

                    JTable userTable = new JTable(buildTableModel(rs));
                    JScrollPane scrollPane = new JScrollPane(userTable);
                    userTableFrame.add(scrollPane);

                    userTableFrame.setVisible(true);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }

    private int getUserId(Connection connection, String nume, String prenume) throws SQLException {
        String query = "SELECT id_utilizator FROM utilizator WHERE Nume = ? AND Prenume = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, nume);
            pst.setString(2, prenume);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_utilizator");
                } else {
                    throw new SQLException("User not found.");
                }
            }
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Create a DefaultTableModel with columns
        DefaultTableModel model = new DefaultTableModel();
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();

        for (int column = 3; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        model.setColumnIdentifiers(columnNames);

        // Add rows to the DefaultTableModel
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int column = 3; column <= columnCount; column++) {
                row.add(rs.getObject(column));
            }
            model.addRow(row);
        }

        return model;
    }


    private void jLabel20MouseClicked(java.awt.event.MouseEvent evt) {
        Clinica clinica= new Clinica();
        clinica.setVisible(true);
        dispose();
    }
    private void jLabel21MouseClicked(MouseEvent evt) {
        InspectorResUmane opp = new InspectorResUmane(id_utilizator, functie);
        opp.setVisible(true);
        dispose();
    }

    private void numeFieldActionPerformed(java.awt.event.ActionEvent evt) {

    }
    private void prenumeFieldActionPerformed(java.awt.event.ActionEvent evt) {

    }
    private void dataFinalFieldActionPerformed(ActionEvent evt) {
    }
    private void dataInceputFieldActionPerformed(ActionEvent evt) {
    }

    private void clearPbtnMouseClicked(java.awt.event.MouseEvent evt) {
        numeField.setText("");
        prenumeField.setText("");
        dataInceputField.setText("");
        dataFinalField.setText("");
    }
   /* public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new InspectorAdaugaConcediu().setVisible(true);
        });
    }*/
    private javax.swing.JButton adaugaConcediuButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField numeField;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField prenumeField;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField dataFinalField;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField dataInceputField;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel6;

}





