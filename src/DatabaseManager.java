import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final String dbPath;
    private Connection connection;

    public DatabaseManager(String dbPath) {
        this.dbPath = dbPath;
    }

    // ================== GESTION DE LA CONNEXION ==================

    /** Tente d'ouvrir la connexion à la BDD SQLite. */
    private void open() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            String url = "jdbc:sqlite:" + dbPath; 
            this.connection = DriverManager.getConnection(url); 
        }
    }
    
    /** Ferme la connexion à la BDD. */
    private void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                this.connection = null;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }

    // Méthodes publiques pour la connexion/déconnexion (utilisées par App.java)
    public void connect() {
        try {
            open();
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        } finally {
            close();
        }
    }

    public void disconnect() {
        close();
    }

    // ================== MÉTHODE UTILITAIRE ==================

    /** Mappe une ligne ResultSet en objet Faille */
    private Faille mapResultSetToFaille(ResultSet rs) throws SQLException {
        return new Faille(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getString("description"),
            rs.getString("ip"),
            rs.getString("severite"),
            rs.getString("source"),
            rs.getString("dateDetection"),
            rs.getString("reference")
        );
    }

    // ================== CRUD (CREATE / READ) ==================

    /** Crée la table 'failles' si elle n'existe pas. */
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS failles (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "nom TEXT, description TEXT, ip TEXT, severite TEXT, " +
                     "source TEXT, dateDetection TEXT, reference TEXT" +
                     ");";

        try {
            open();
            try (Statement stmt = this.connection.createStatement()) {
                stmt.execute(sql);
                System.out.println("Table 'failles' vérifiée/créée avec succès.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la création de la table : " + e.getMessage());
        } finally {
            close();
        }
    }

    /** Insère une faille et met à jour son ID. */
    public int insertFaille(Faille faille) {
        String sql = "INSERT INTO failles(nom, description, ip, severite, source, dateDetection, reference) VALUES(?,?,?,?,?,?,?)";
        int generatedId = -1;

        try {
            open();
            try (PreparedStatement pstmt = this.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                pstmt.setString(1, faille.getNom());
                pstmt.setString(2, faille.getDescription());
                pstmt.setString(3, faille.getIp());
                pstmt.setString(4, faille.getSeverite());
                pstmt.setString(5, faille.getSource());
                pstmt.setString(6, faille.getDateDetection());
                pstmt.setString(7, faille.getReference());

                pstmt.executeUpdate();
                
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedId = keys.getInt(1);
                        faille.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'insertion de la faille : " + e.getMessage());
        } finally {
            close();
        }

        return generatedId;
    }

    /** Obtient toutes les failles. */
    public List<Faille> listFailles() {
        List<Faille> faillesList = new ArrayList<>();
        String sql = "SELECT * FROM failles";

        try {
            open();
            try (Statement stmt = this.connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    faillesList.add(mapResultSetToFaille(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des failles : " + e.getMessage());
        } finally {
            close();
        }
        return faillesList;
    }

    /** Obtient une faille par son ID. */
    public Faille getFailleById(int id) {
        Faille f = null;
        String sql = "SELECT * FROM failles WHERE id = ?";

        try {
            open();
            try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    f = mapResultSetToFaille(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la faille : " + e.getMessage());
        } finally {
            close();
        }
        return f;
    }

    /** Obtient les failles par sévérité. */
    public List<Faille> filterBySeverite(String severity) {
        List<Faille> faillesList = new ArrayList<>();
        String sql = "SELECT * FROM failles WHERE severite = ?";

        try {
            open();
            try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
                pstmt.setString(1, severity);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    faillesList.add(mapResultSetToFaille(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des failles par sévérité : " + e.getMessage());
        } finally {
            close();
        }
        return faillesList;
    }
    
    // ================== CRUD (UPDATE / DELETE) ==================

    /** Met à jour une faille par ID et sévérité (simplifié pour l'exemple App.java). */
    public boolean updateFaille(int id, String nom, String severite) {
        String sql = "UPDATE failles SET nom = ?, severite = ? WHERE id = ?";
        boolean success = false;

        try {
            open();
            try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
                pstmt.setString(1, nom);
                pstmt.setString(2, severite);
                pstmt.setInt(3, id);

                int affected = pstmt.executeUpdate();
                success = (affected > 0);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour de la faille : " + e.getMessage());
        } finally {
            close();
        }
        return success;
    }

    /** Supprime une faille par son ID. */
    public boolean deleteFaille(int id) {
        String sql = "DELETE FROM failles WHERE id = ?";
        boolean success = false;

        try {
            open();
            try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                int affected = pstmt.executeUpdate();
                success = (affected > 0);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression de la faille : " + e.getMessage());
        } finally {
            close();
        }
        return success;
    }
}