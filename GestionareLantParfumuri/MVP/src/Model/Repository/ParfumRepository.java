package Model.Repository;

import Connection.SqlConnection;
import Model.Parfum;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParfumRepository {

    // Adaugă un parfum în baza de date (fără să trimiți id_parfum)
    public int addParfum(Parfum parfum) {
        String sql = "INSERT INTO parfum (nume, producator, descriere) VALUES (?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, parfum.getNume());
            stmt.setString(2, parfum.getProducator());
            stmt.setString(3, parfum.getDescriere());

            stmt.executeUpdate();

            // Obținem ID-ul generat automat
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // returnăm ID-ul generat
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Șterge parfum după ID
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

    // Update parfum
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

    // Returnează TOATE parfumurile din baza de date
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

    // Caută parfum după nume
    public Optional<Parfum> getByName(String name) {
        String sql = "SELECT * FROM parfum WHERE LOWER(nume) = LOWER(?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Parfum p = new Parfum(
                        rs.getInt("id_parfum"),
                        rs.getString("nume"),
                        rs.getString("producator"),
                        rs.getString("descriere")
                );
                return Optional.of(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Returnează parfumurile + disponibilitate pentru o parfumerie
    public List<ParfumWithDisponibilitate> getParfumuriByParfumerie(int idParfumerie) {
        List<ParfumWithDisponibilitate> parfumuri = new ArrayList<>();
        String sql = "SELECT p.id_parfum, p.nume, p.producator, p.descriere, s.disponibilitate " +
                "FROM parfum p " +
                "JOIN stoc s ON p.id_parfum = s.id_parfum " +
                "WHERE s.id_parfumerie = ?";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ParfumWithDisponibilitate p = new ParfumWithDisponibilitate(
                        rs.getInt("id_parfum"),
                        rs.getString("nume"),
                        rs.getString("producator"),
                        rs.getString("descriere"),
                        rs.getBoolean("disponibilitate")
                );
                parfumuri.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parfumuri;
    }

    // Clasa pentru rezultat (parfum + disponibilitate)
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
