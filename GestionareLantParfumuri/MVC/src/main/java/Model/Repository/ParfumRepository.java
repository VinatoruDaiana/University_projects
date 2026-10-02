package Model.Repository;

import Model.Parfum;
import Model.Parfumerie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ParfumRepository extends AbstractRepository<Parfum> {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(ParfumRepository.class.getName());


    public ParfumRepository() {
        super();
    }




    public int addParfum(Parfum parfum) {
        String sql = "INSERT INTO parfum (nume, producator, descriere, imagine_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, parfum.getNume());
            stmt.setString(2, parfum.getProducator());
            stmt.setString(3, parfum.getDescriere());
            //stmt.setString(4, parfum.getImaginePath());

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la adăugare parfum: " + e.getMessage());
        }
        return -1;
    }

    public void updateParfum(Parfum parfum) {
        String sql = "UPDATE parfum SET nume = ?, producator = ?, descriere = ?, imagine_path = ? WHERE id_parfum = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, parfum.getNume());
            stmt.setString(2, parfum.getProducator());
            stmt.setString(3, parfum.getDescriere());
           // stmt.setString(4, parfum.getImaginePath());
            stmt.setInt(5, parfum.getId_parfum());

            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la actualizare parfum: " + e.getMessage());
        }
    }

    public void deleteParfum(int id) {
        String sql = "DELETE FROM parfum WHERE id_parfum = ?";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la ștergere parfum: " + e.getMessage());
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
            LOGGER.log(Level.WARNING, "Eroare la extragere parfumuri: " + e.getMessage());
        }
        return parfumuri;
    }

    public List<String> getParfumuriFiltrate(int idParfumerie, String producator) {
        List<String> rezultat = new ArrayList<>();
        String sql =
                "SELECT p.id_parfum, p.nume, p.producator, p.descriere " +
                        "FROM parfum p " +
                        "JOIN stoc s ON p.id_parfum = s.id_parfum " +
                        "WHERE s.id_parfumerie = ? " +
                        "AND LOWER(p.producator) LIKE LOWER(?) " +
                        "AND s.cantitate > 0";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfumerie);
            stmt.setString(2, "%" + producator + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rezultat.add("ID: " + rs.getInt("id_parfum") +
                        " | Nume: " + rs.getString("nume") +
                        " | Producator: " + rs.getString("producator") +
                        " | Descriere: " + rs.getString("descriere"));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la filtrare parfumuri: " + e.getMessage());
        }
        return rezultat;
    }

    public List<String> getParfumuriEpuizate(int idParfumerie) {
        List<String> rezultat = new ArrayList<>();
        String sql =
                "SELECT p.id_parfum, p.nume, p.producator, p.descriere " +
                        "FROM parfum p " +
                        "JOIN stoc s ON p.id_parfum = s.id_parfum " +
                        "WHERE s.id_parfumerie = ? AND s.cantitate = 0";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfumerie);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rezultat.add(rs.getInt("id_parfum") + "," +
                        rs.getString("nume") + "," +
                        rs.getString("producator") + "," +
                        rs.getString("descriere"));
            }

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la parfumuri epuizate: " + e.getMessage());
        }
        return rezultat;
    }

    public boolean esteDisponibil(int idParfum, int idParfumerie) {
        String sql = "SELECT 1 FROM stoc WHERE id_parfum = ? AND id_parfumerie = ? AND cantitate > 0";
        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idParfum);
            stmt.setInt(2, idParfumerie);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }




    public List<Parfumerie> getParfumeriiCuParfumDisponibil(int idParfum) {
        List<Parfumerie> rezultat = new ArrayList<>();
        String sql = "SELECT parfumerie.* FROM stoc " +
                "JOIN parfumerie ON stoc.id_parfumerie = parfumerie.id_parfumerie " +
                "WHERE stoc.id_parfum = ? AND stoc.cantitate > 0";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idParfum);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Parfumerie p = new Parfumerie(
                        String.valueOf(rs.getInt("id_parfumerie")),
                        rs.getString("nume"),
                        rs.getString("adresa")
                );

                rezultat.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rezultat;
    }



}