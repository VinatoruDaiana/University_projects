package Model.Repository;

import Connection.SqlConnection;
import Model.Parfumerie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParfumerieRepository {

    // Adaugă o parfumerie în baza de date (fără să trimiți id_parfumerie)
    public int addParfumerie(Parfumerie parfumerie) {
        String sql = "INSERT INTO parfumerie (nume, adresa, telefon) VALUES (?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, parfumerie.getNume());
            stmt.setString(2, parfumerie.getAdresa());
            stmt.setString(3, parfumerie.getTelefon());
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

    // Șterge parfumerie după ID
    public void deleteParfumerie(int id) {
        String sql = "DELETE FROM parfumerie WHERE id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update parfumerie
    public void updateParfumerie(Parfumerie parfumerie) {
        String sql = "UPDATE parfumerie SET nume = ?, adresa = ?, telefon = ? WHERE id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, parfumerie.getNume());
            stmt.setString(2, parfumerie.getAdresa());
            stmt.setString(3, parfumerie.getTelefon());
            stmt.setInt(4, parfumerie.getIdParfumerie());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Returnează toate parfumeriile
    public List<Parfumerie> getAll() {
        List<Parfumerie> parfumerii = new ArrayList<>();
        String sql = "SELECT * FROM parfumerie";
        try (Connection conn = SqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Parfumerie p = new Parfumerie(
                        rs.getInt("id_parfumerie"),
                        rs.getString("nume"),
                        rs.getString("adresa"),
                        rs.getString("telefon")
                );
                parfumerii.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parfumerii;
    }
}
