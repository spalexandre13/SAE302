import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private String dbPath;
    private Connection connection;

    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
    }

    // CORRECTION IMPORTANTE : public (et non private)
    public void open() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                String url = "jdbc:sqlite:" + dbPath;
                this.connection = DriverManager.getConnection(url);
                System.out.println("   [BDD] Connexion ouverte : " + dbPath);
            }
        } catch (SQLException e) {
            System.out.println("   [BDD] Erreur ouverture : " + e.getMessage());
        }
    }

    // CORRECTION IMPORTANTE : public
    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                this.connection = null;
                System.out.println("   [BDD] Connexion fermée.");
            }
        } catch (SQLException e) {
            System.out.println("   [BDD] Erreur fermeture : " + e.getMessage());
        }
    }

    // CORRECTION : Renommé pour correspondre à App.java
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS failles (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "nom TEXT, " +
                     "description TEXT, " +
                     "ip TEXT, " +
                     "severite TEXT, " +
                     "source TEXT, " +
                     "dateDetection TEXT, " +
                     "reference TEXT" +
                     ");";
        open(); 
        try (Statement stmt = this.connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Table 'failles' vérifiée.");
        } catch (SQLException e) {
            System.out.println("❌ Erreur création table : " + e.getMessage());
        }
    }

    public boolean insertFaille(Faille faille) {
        String sql = "INSERT INTO failles(nom, description, ip, severite, source, dateDetection, reference) VALUES(?,?,?,?,?,?,?)";
        open();
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, faille.getNom());
            pstmt.setString(2, faille.getDescription());
            pstmt.setString(3, faille.getIp());
            pstmt.setString(4, faille.getSeverite());
            pstmt.setString(5, faille.getSource());
            pstmt.setString(6, faille.getDateDetection());
            pstmt.setString(7, faille.getReference());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ Erreur insert : " + e.getMessage());
            return false;
        }
    }

    // CORRECTION : Renommé (avant c'était listFailles)
    public List<Faille> getAllFailles() {
        List<Faille> list = new ArrayList<>();
        String sql = "SELECT * FROM failles";
        open();
        try (Statement stmt = this.connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ Erreur getAll : " + e.getMessage());
        }
        return list;
    }

    public Faille getFailleById(int id) {
        String sql = "SELECT * FROM failles WHERE id = ?";
        open();
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.out.println("❌ Erreur getById : " + e.getMessage());
        }
        return null;
    }

    // CORRECTION : Renommé (avant c'était filterBySeverite)
    public List<Faille> getFaillesBySeverity(String severity) {
        List<Faille> list = new ArrayList<>();
        String sql = "SELECT * FROM failles WHERE severite = ?";
        open();
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, severity);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ Erreur getBySeverity : " + e.getMessage());
        }
        return list;
    }

    public boolean deleteFaille(int id) {
        String sql = "DELETE FROM failles WHERE id = ?";
        open();
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur delete : " + e.getMessage());
            return false;
        }
    }
    
    // Fonction utilitaire pour la mise à jour
    public boolean updateFaille(int id, String nom, String severite) {
        String sql = "UPDATE failles SET nom = COALESCE(NULLIF(?, ''), nom), severite = COALESCE(NULLIF(?, ''), severite) WHERE id = ?";
        open();
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.setString(2, severite);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Erreur update : " + e.getMessage());
            return false;
        }
    }

    private Faille mapRow(ResultSet rs) throws SQLException {
        return new Faille(
            rs.getInt("id"), rs.getString("nom"), rs.getString("description"),
            rs.getString("ip"), rs.getString("severite"), rs.getString("source"),
            rs.getString("dateDetection"), rs.getString("reference")
        );
    }
}
