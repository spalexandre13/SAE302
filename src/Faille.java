/**
 * Faille
 * Modèle de données représentant une vulnérabilité ou une information de sécurité détectée.
 */
public class Faille {
    private int id; // 0 = pas encore en base / inconnu
    private String nom;
    private String description;
    private String ip;
    private String severite;
    private String source;
    private String dateDetection;
    private String reference;

    // Constructeur complet (avec id)
    public Faille(int id, String nom, String description, String ip, String severite, String source, String dateDetection, String reference) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.ip = ip;
        this.severite = severite;
        this.source = source;
        this.dateDetection = dateDetection;
        this.reference = reference;
    }

    // Constructeur pratique pour l'insertion (id inconnu, initialisé à 0)
    public Faille(String nom, String description, String ip, String severite, String source, String dateDetection, String reference) {
        this(0, nom, description, ip, severite, source, dateDetection, reference);
    }

    // ================== Getters et Setters ==================
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom(){ return nom; }
    public void setNom(String nom){ this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIp(){ return ip; }
    public void setIp(String ip){ this.ip = ip; }

    public String getSeverite(){ return severite; }
    public void setSeverite(String severite){ this.severite = severite; }

    public String getSource(){ return source; }
    public void setSource(String source){ this.source = source; }

    public String getDateDetection(){ return dateDetection; }
    public void setDateDetection(String dateDetection){ this.dateDetection = dateDetection; }

    public String getReference(){ return reference; }
    public void setReference(String reference){ this.reference = reference; }

    // ================== toString ==================

    @Override
    public String toString() {
        return "Faille{id=" + id + ", nom='" + nom + '\'' + ", ip='" + ip + '\'' + ", severite='" + severite + '\'' + ", dateDetection='" + dateDetection + '\'' + '}';
    }
}