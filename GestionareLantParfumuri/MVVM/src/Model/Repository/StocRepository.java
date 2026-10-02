package Model.Repository;

import Connection.SqlConnection;
import Model.Stoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StocRepository {


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
        return -1;
    }



}
