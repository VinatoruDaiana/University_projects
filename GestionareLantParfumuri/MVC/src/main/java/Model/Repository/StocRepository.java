package Model.Repository;

import Model.Stoc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StocRepository extends AbstractRepository<Stoc> {

    private static final Logger LOGGER = Logger.getLogger(StocRepository.class.getName());

    public int addStoc(Stoc stoc) {
        String sql = "INSERT INTO stoc (id_parfum, id_parfumerie, cantitate, disponibilitate) VALUES (?, ?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, stoc.getId_parfum());
            stmt.setInt(2, stoc.getId_parfumerie());
            stmt.setInt(3, stoc.getCantitate());
            stmt.setBoolean(4, stoc.isDisponibilitate());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la adăugare stoc: " + e.getMessage());
        }
        return -1;
    }

    public void updateStoc(Stoc stoc) {
        String sql = "UPDATE stoc SET cantitate = ?, disponibilitate = ? WHERE id_stoc = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stoc.getCantitate());
            stmt.setBoolean(2, stoc.isDisponibilitate());
            stmt.setInt(3, stoc.getId_stoc());

            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la actualizare stoc: " + e.getMessage());
        }
    }

    public void deleteStoc(int id) {
        String sql = "DELETE FROM stoc WHERE id_stoc = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la ștergere stoc: " + e.getMessage());
        }
    }

    public List<Stoc> getAll() {
        List<Stoc> lista = new ArrayList<>();
        String sql = "SELECT * FROM stoc";
        try (Connection conn = SqlConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Stoc s = new Stoc(
                        rs.getInt("id_stoc"),
                        rs.getInt("id_parfum"),
                        rs.getInt("id_parfumerie"),
                        rs.getInt("cantitate"),
                        rs.getBoolean("disponibilitate")
                );
                lista.add(s);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la extragere stocuri: " + e.getMessage());
        }
        return lista;
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
            LOGGER.log(Level.WARNING, "Eroare la găsirea id_stoc: " + e.getMessage());
        }
        return -1;
    }

    public List<Stoc> getStocuriByParfumId(int parfumId) {
        List<Stoc> stocuri = new ArrayList<>();
        String query = "SELECT * FROM Stoc WHERE idParfum = ?";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, parfumId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Stoc stoc = new Stoc();
                    stoc.setId_stoc(rs.getInt("idStoc"));
                    stoc.setId_parfum(rs.getInt("idParfum"));
                    stoc.setId_parfumerie(rs.getInt("idParfumerie"));
                    stoc.setCantitate(rs.getInt("cantitate"));
                    stoc.setDisponibilitate(rs.getBoolean("disponibilitate"));
                    stocuri.add(stoc);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la interogarea stocurilor după idParfum: " + e.getMessage());
        }

        return stocuri;
    }

}
