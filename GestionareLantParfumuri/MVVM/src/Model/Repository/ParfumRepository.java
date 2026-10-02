package Model.Repository;

import Connection.SqlConnection;
import Model.Parfum;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParfumRepository {


    public int addParfum(Parfum parfum) {
        String sql = "INSERT INTO parfum (nume, producator, descriere) VALUES (?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, parfum.getNume());
            stmt.setString(2, parfum.getProducator());
            stmt.setString(3, parfum.getDescriere());

            stmt.executeUpdate();


            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    public void deleteParfum(int id) {
        String sql = "DELETE FROM parfum WHERE id_parfum = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateParfum(Parfum parfum) {
        String sql = "UPDATE parfum SET nume = ?, producator = ?, descriere = ? WHERE id_parfum = ?";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parfum.getNume());
            stmt.setString(2, parfum.getProducator());
            stmt.setString(3, parfum.getDescriere());
            stmt.setInt(4, parfum.getIdParfum());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Parfum> getAll() {
        List<Parfum> parfumuri = new ArrayList<>();
        String sql = "SELECT * FROM parfum";
        try (Connection conn = SqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Parfum p = new Parfum(
                        rs.getInt("id_parfum"),
                        rs.getString("nume"),
                        rs.getString("producator"),
                        rs.getString("descriere")
                );

                parfumuri.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parfumuri;
    }



    public List<String> getParfumuriFiltrate(int idParfumerie, String producatorFiltru) {
        List<String> parfumuri = new ArrayList<>();
        String sql = "SELECT p.id_parfum, p.nume, p.producator, p.descriere, s.cantitate " +
                "FROM parfum p JOIN stoc s ON p.id_parfum = s.id_parfum " +
                "WHERE s.id_parfumerie = ? AND LOWER(p.producator) LIKE LOWER(?) AND s.cantitate > 0";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParfumerie);
            stmt.setString(2, "%" + producatorFiltru.trim() + "%"); // potrivește parțial
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String parfumStr = "ID: " + rs.getInt("id_parfum") +
                        " | Nume: " + rs.getString("nume") +
                        " | Producator: " + rs.getString("producator") +
                        " | Descriere: " + rs.getString("descriere");
                parfumuri.add(parfumStr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parfumuri;
    }


    public List<String> getParfumuriEpuizate(int idParfumerie) {
        List<String> lista = new ArrayList<>();
        String sql = """
        SELECT p.id_parfum, p.nume, p.producator, p.descriere 
        FROM parfum p
        JOIN stoc s ON p.id_parfum = s.id_parfum
        WHERE s.id_parfumerie = ? AND s.cantitate = 0
    """;
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String linie = rs.getInt("id_parfum") + "," +
                        rs.getString("nume") + "," +
                        rs.getString("producator") + "," +
                        rs.getString("descriere");
                lista.add(linie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public static class ParfumWithDisponibilitate {
        private int idParfum;
        private String nume;
        private String producator;
        private String descriere;
        private boolean disponibilitate;

        public ParfumWithDisponibilitate(int idParfum, String nume, String producator, String descriere, boolean disponibilitate) {
            this.idParfum = idParfum;
            this.nume = nume;
            this.producator = producator;
            this.descriere = descriere;
            this.disponibilitate = disponibilitate;
        }

        public int getIdParfum() { return idParfum; }
        public String getNume() { return nume; }
        public String getProducator() { return producator; }
        public String getDescriere() { return descriere; }
        public boolean isDisponibilitate() { return disponibilitate; }
    }
}
