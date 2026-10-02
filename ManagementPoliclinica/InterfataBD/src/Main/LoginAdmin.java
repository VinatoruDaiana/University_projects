package Main;

//import Admins.AdminActivity;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.*;

public class LoginAdmin extends JFrame {

    public int id_utilizator;
    public int lvl;
    PreparedStatement prp = null;
    ResultSet result = null;
    Connection connection = null;
    String username;

    public LoginAdmin(int lvl) {
        this.lvl=lvl;
        initComponents();
        ImageIcon ic = new ImageIcon(getClass().getResource("/imagini/hospital.png"));
        this.setIconImage(ic.getImage());
    }

    private void initComponents() {

        aUserLabel = new JLabel();
        jLabel2 = new JLabel();
        aUserField = new JTextField();
        aPassField = new JPasswordField();
        dExitBtn = new JButton();
        aLoginBtn = new JButton();
        mLabel = new Label();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Welcome!");
        setMaximizedBounds(new Rectangle(0, 0, 1000, 550));
        setResizable(false);
        setSize(new Dimension(1000, 550));

        aUserLabel.setFont(new Font("Tahoma", 0, 24)); // NOI18N
        aUserLabel.setText("Username:");

        jLabel2.setFont(new Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Parola:");

        aUserField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aUserFieldActionPerformed(evt);
            }
        });

        dExitBtn.setBackground(new Color(0, 204, 204));
        dExitBtn.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        dExitBtn.setForeground(new Color(255, 255, 255));
        dExitBtn.setText("Home");
        dExitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dExitBtnActionPerformed(evt);
            }
        });

        aLoginBtn.setBackground(new Color(0, 153, 153));
        aLoginBtn.setFont(new Font("Tahoma", 0, 18)); // NOI18N
        aLoginBtn.setForeground(new Color(255, 255, 255));
        aLoginBtn.setText("Login");
        aLoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aLoginBtnActionPerformed(evt);
            }
        });

        mLabel.setAlignment(Label.CENTER);
        mLabel.setBackground(new Color(0, 153, 153));
        mLabel.setFont(new Font("Arial", 0, 24)); // NOI18N
        mLabel.setForeground(new Color(255, 255, 255));
        mLabel.setText("AUTENTIFICARE");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(mLabel, GroupLayout.PREFERRED_SIZE, 1000, GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(262, 262, 262)
                                .addComponent(aUserLabel)
                                .addGap(10, 10, 10)
                                .addComponent(aUserField, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(261, 261, 261)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(aPassField, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(388, 388, 388)
                                .addComponent(dExitBtn, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                .addGap(97, 97, 97)
                                .addComponent(aLoginBtn, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(mLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                .addGap(64, 64, 64)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(aUserLabel)
                                        .addComponent(aUserField, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                                .addGap(105, 105, 105)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel2)
                                        .addComponent(aPassField, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                                .addGap(73, 73, 73)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(dExitBtn, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(aLoginBtn, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(110, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void dExitBtnActionPerformed(ActionEvent evt) {
        Clinica clinica = new Clinica();
        clinica.setVisible(true);
        dispose();
    }

    private boolean loginSuccessful(String user, String pass) {
        int val=0;
        try (Connection connection = ConexiuneSQL.Conexiune()) {
            if (connection != null) {
                String sql = "SELECT COUNT(*), id_utilizator FROM autentificare WHERE nume=? AND parola=?;";
                try (PreparedStatement pst = connection.prepareStatement(sql)) {
                    pst.setString(1, user);
                    pst.setString(2, pass);

                    pst.execute();
                    ResultSet rs=pst.getResultSet();
                    if(rs.next())
                    {
                        val=rs.getInt(1);
                        id_utilizator=rs.getInt(2);
                    }

                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        if(val==0)
            return false;
        else
            return true;
    }

    private void aLoginBtnActionPerformed(ActionEvent evt) {
        int aux = 0;
        String user = aUserField.getText();
        String pass = String.valueOf(aPassField.getPassword());
        if (loginSuccessful(user, pass)) {

            if(lvl==1) //daca e admin facem select de admin_lvl
            {
                try (Connection connection = ConexiuneSQL.Conexiune()) {
                    if (connection != null) {
                        String sql = "SELECT admin_lvl FROM utilizator WHERE id_utilizator=?;";
                        try (PreparedStatement pst = connection.prepareStatement(sql)) {
                            pst.setInt(1, id_utilizator);

                            pst.execute();
                            ResultSet rs=pst.getResultSet();
                            if(rs.next())
                                aux=rs.getInt(1);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                if(aux==0){
                    JOptionPane.showMessageDialog(null, "User is not ADMIN", "Error", JOptionPane.WARNING_MESSAGE);
                }
                else if(aux==1) {
                    ActivitateAdmin adminActivity = new ActivitateAdmin(aux);
                    adminActivity.setUser(user);
                    adminActivity.setVisible(true);
                    JOptionPane.showMessageDialog(null, "Login Successful", "Welcome " + user, JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
                else {
                    ActivitateSuperAdmin superAdminActivity = new ActivitateSuperAdmin(aux);
                    superAdminActivity.setUser(user);
                    superAdminActivity.setVisible(true);
                    JOptionPane.showMessageDialog(null, "Login Successful", "Welcome " + user, JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
            else //daca e utilizator facem search de functie
            {
                try (Connection connection = ConexiuneSQL.Conexiune()) {
                    if (connection != null) {
                        String sql = "SELECT functie FROM utilizator WHERE id_utilizator=?;";
                        try (PreparedStatement pst = connection.prepareStatement(sql)) {
                            pst.setInt(1, id_utilizator);

                            pst.execute();
                            ResultSet rs=pst.getResultSet();
                            if(rs.next())
                                aux=rs.getInt(1);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();

                }

                OptiuniUtilizator utilizator = new OptiuniUtilizator(id_utilizator, aux);
                utilizator.setUser(user);
                utilizator.setVisible(true);
                JOptionPane.showMessageDialog(null, "Login Successful", "Welcome " + user, JOptionPane.INFORMATION_MESSAGE);
                dispose();

            }

        } else {
            JOptionPane.showMessageDialog(null, "Invalid Username or Password Format", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void aUserFieldActionPerformed(ActionEvent evt) {

    }

    /*public static void main(String args[]) {
        EventQueue.invokeLater(() -> {
            new LoginAdmin().setVisible(true);
        });
    }*/
    private JButton aLoginBtn;
    private JPasswordField aPassField;
    private JTextField aUserField;
    private JLabel aUserLabel;
    private JButton dExitBtn;
    private JLabel jLabel2;
    private Label mLabel;

}

