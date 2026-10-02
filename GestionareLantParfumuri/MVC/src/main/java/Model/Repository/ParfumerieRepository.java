package Model.Repository;

import Model.Parfumerie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ParfumerieRepository extends AbstractRepository<Parfumerie> {

    private static final Logger LOGGER = Logger.getLogger(ParfumerieRepository.class.getName());

    public int addParfumerie(Parfumerie parfumerie) {
        String sql = "INSERT INTO parfumerie (nume, adresa, telefon) VALUES (?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, parfumerie.getNume());
            stmt.setString(2, parfumerie.getAdresa());
            stmt.setString(3, parfumerie.getTelefon());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la adăugare parfumerie: " + e.getMessage());
        }
        return -1;
    }

    public void updateParfumerie(Parfumerie parfumerie) {
        String sql = "UPDATE parfumerie SET nume = ?, adresa = ?, telefon = ? WHERE id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parfumerie.getNume());
            stmt.setString(2, parfumerie.getAdresa());
            stmt.setString(3, parfumerie.getTelefon());
            stmt.setInt(4, parfumerie.getId_parfumerie());

            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la actualizare parfumerie: " + e.getMessage());
        }
    }

    public void deleteParfumerie(int id) {
        String sql = "DELETE FROM parfumerie WHERE id_parfumerie = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la ștergere parfumerie: " + e.getMessage());
        }
    }

    public List<Parfumerie> getAll() {
        List<Parfumerie> lista = new ArrayList<>();
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
                lista.add(p);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la extragere parfumerii: " + e.getMessage());
        }
        return lista;
    }
}