package Model.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticiRepository {
    private static final Logger LOGGER = Logger.getLogger(StatisticiRepository.class.getName());

    public Map<String, Integer> getNrParfumuriPeDescriere() {
        Map<String, Integer> statistici = new HashMap<>();
        String sql = "SELECT descriere, COUNT(*) AS total FROM parfum GROUP BY descriere";

        try (Connection conn = SqlConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String descriere = rs.getString("descriere");
                int count = rs.getInt("total");
                statistici.put(descriere, count);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Eroare la interogarea descrierilor: " + e.getMessage());
        }
        return statistici;
    }
}