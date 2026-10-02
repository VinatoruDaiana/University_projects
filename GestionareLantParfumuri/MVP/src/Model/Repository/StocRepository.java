package Model.Repository;

import Connection.SqlConnection;
import Model.Stoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StocRepository {

    // Adaugă un stoc (fără id_stoc manual)
    public int addStoc(Stoc stoc) {
        String sql = "INSERT INTO stoc (id_parfumerie, id_parfum, cantitate, disponibilitate) VALUES (?, ?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, stoc.getIdParfumerie());
            stmt.setInt(2, stoc.getIdParfum());
            stmt.setInt(3, stoc.getCantitate());
            stmt.setBoolean(4, stoc.isDisponibilitate());
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

    // Update stoc după ID
    public void updateStoc(int id, int cantitate, boolean disponibilitate) {
        String sql = "UPDATE stoc SET cantitate = ?, disponibilitate = ? WHERE id_stoc = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantitate);
            stmt.setBoolean(2, disponibilitate);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Returnează toate stocurile
    public List<Stoc> getAll() {
        List<Stoc> stocuri = new ArrayList<>();
        String sql = "SELECT * FROM stoc";
        try (Connection conn = SqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Stoc s = new Stoc(
                        rs.getInt("id_stoc"),
                        rs.getInt("id_parfumerie"),
                        rs.getInt("id_parfum"),
                        rs.getInt("cantitate"),
                        rs.getBoolean("disponibilitate")
                );
                stocuri.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocuri;
    }

    // Returnează stocurile pentru o anumită parfumerie
    public List<Stoc> getByParfumerie(int idParfumerie) {
        List<Stoc> stocuri = new ArrayList<>();
        String sql = "SELECT * FROM stoc WHERE id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Stoc s = new Stoc(
                        rs.getInt("id_stoc"),
                        rs.getInt("id_parfumerie"),
                        rs.getInt("id_parfum"),
                        rs.getInt("cantitate"),
                        rs.getBoolean("disponibilitate")
                );
                stocuri.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stocuri;
    }

    public List<String> getParfumuriFiltrate(int idParfumerie, String producatorFiltru, Boolean disponibilitateFiltru) {
        List<String> parfumuri = new ArrayList<>();

        String sql = "SELECT p.id_parfum, p.nume, p.producator, p.descriere, s.disponibilitate " +
                "FROM parfum p JOIN stoc s ON p.id_parfum = s.id_parfum " +
                "WHERE s.id_parfumerie = ?";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String producator = rs.getString("producator");
                boolean disponibilitate = rs.getBoolean("disponibilitate");

                boolean matchProducator = producatorFiltru.equalsIgnoreCase("Toți") || producator.equalsIgnoreCase(producatorFiltru);
                boolean matchDisponibilitate = disponibilitateFiltru == null || disponibilitate == disponibilitateFiltru;

                if (matchProducator && matchDisponibilitate) {
                    String parfumStr = "ID: " + rs.getInt("id_parfum") +
                            " | Nume: " + rs.getString("nume") +
                            " | Producator: " + producator +
                            " | Descriere: " + rs.getString("descriere") +
                            " | Disponibilitate: " + (disponibilitate ? "Disponibil" : "Indisponibil");
                    parfumuri.add(parfumStr);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parfumuri;
    }

    public int getIdStoc(int idParfum, int idParfumerie) {
        String sql = "SELECT id_stoc FROM stoc WHERE id_parfum = ? AND id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfum);
            stmt.setInt(2, idParfumerie);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_stoc");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // dacă nu a fost găsit
    }

    public List<String> getParfumeriiForParfum(String numeParfum) {
        List<String> parfumerii = new ArrayList<>();

        String sql = "SELECT parfumerie.nume, parfumerie.adresa, parfumerie.telefon " +
                "FROM stoc " +
                "JOIN parfum ON stoc.id_parfum = parfum.id_parfum " +
                "JOIN parfumerie ON stoc.id_parfumerie = parfumerie.id_parfumerie " +
                "WHERE LOWER(parfum.nume) = LOWER(?) AND stoc.disponibilitate = true";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeParfum);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String parfumerieInfo = "Nume: " + rs.getString("nume") +
                        " | Adresa: " + rs.getString("adresa") +
                        " | Telefon: " + rs.getString("telefon");
                parfumerii.add(parfumerieInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parfumerii;
    }

    public List<String> getParfumuriEpuizate(int idParfumerie) {
        List<String> parfumuri = new ArrayList<>();

        String sql = "SELECT p.id_parfum, p.nume, p.producator " +
                "FROM parfum p JOIN stoc s ON p.id_parfum = s.id_parfum " +
                "WHERE s.id_parfumerie = ? AND s.cantitate = 0";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String parfumStr = "ID: " + rs.getInt("id_parfum") +
                        " | Nume: " + rs.getString("nume") +
                        " | Producator: " + rs.getString("producator") +
                        " | Disponibilitate: Epuizat";
                parfumuri.add(parfumStr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parfumuri;
    }



}
