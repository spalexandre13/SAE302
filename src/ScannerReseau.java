import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ScannerReseau
 * Rôle : Orchestre les opérations de scan, gère les cibles, les outils, et 
 * l'insertion finale des résultats en BDD.
 */
public class ScannerReseau {

    private final DatabaseManager db;
    private final List<String> targets;
    private final List<ScanTool> tools;

    public ScannerReseau(DatabaseManager db) {
        this.db = db;
        this.targets = new ArrayList<>();
        this.tools = new ArrayList<>();
    }

    // ================== LOGIQUE DE SIMULATION (Correction pour Choix 2) ==================

    /**
     * detecterFaillesSimule : 
     * Ajoute des failles de test directement dans la base de données pour la démo.
     */
    public void detecterFaillesSimule() {
        System.out.println("Simulation de détection de failles...");
        
        // Simuler quelques failles
        Faille f1 = new Faille("Port 21 ouvert", "Serveur FTP non sécurisé", "192.168.1.10", "HIGH", "Simulateur", LocalDate.now().toString(), "ref-FTP");
        Faille f2 = new Faille("Port 80 HTTP", "Port HTTP par défaut ouvert", "192.168.1.15", "MEDIUM", "Simulateur", LocalDate.now().toString(), "ref-HTTP");
        Faille f3 = new Faille("Port 22 SSH", "Port SSH par défaut", "192.168.1.20", "LOW", "Simulateur", LocalDate.now().toString(), "ref-SSH");
        
        // Insertion dans la base de données via le DatabaseManager
        db.insertFaille(f1);
        db.insertFaille(f2);
        db.insertFaille(f3);
        
        System.out.println("3 failles simulées insérées dans la base de données.");
    }
    
    // ================== GESTION DES CIBLES ==================

    /** Ajouter une cible (IP) à scanner */
    public void ajouterCible(String target) {
        if (target != null && !target.trim().isEmpty()) {
            targets.add(target.trim());
            System.out.println("Cible ajoutée : " + target);
        }
    }

    /** Ajouter un réseau entier (CIDR /24, ex: 192.168.1.0/24) */
    public void ajouterReseauCible(String reseau) {
        String[] parts = reseau.split("/");
        if (parts.length == 2) {
            try {
                InetAddress networkAddress = InetAddress.getByName(parts[0]);
                int prefixLength = Integer.parseInt(parts[1]);

                if (prefixLength < 8 || prefixLength > 32) {
                    System.out.println("Le suffixe CIDR doit être entre 8 et 32.");
                    return;
                }

                byte[] ipBytes = networkAddress.getAddress();
                int ipInt = byteArrayToInt(ipBytes);
                int mask = 0xFFFFFFFF << (32 - prefixLength);

                int startIp = ipInt & mask;
                int endIp = startIp | (~mask);

                for (int i = startIp + 1; i < endIp; i++) {
                    String ip = intToIp(i);
                    ajouterCible(ip);
                }
                System.out.println("Plage d'IP ajoutée pour le réseau : " + reseau);
            } catch (Exception e) {
                System.out.println("Erreur dans la plage réseau : " + e.getMessage());
            }
        } else {
            System.out.println("Format du réseau invalide, utilisez le format : 192.168.1.0/24");
        }
    }

    private int byteArrayToInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result |= (bytes[i] & 0xFF) << (8 * (3 - i));
        }
        return result;
    }

    private String intToIp(int ipInt) {
        return ((ipInt >> 24) & 0xFF) + "." +
               ((ipInt >> 16) & 0xFF) + "." +
               ((ipInt >> 8) & 0xFF) + "." +
               (ipInt & 0xFF);
    }
    
    // ================== GESTION DES OUTILS ==================

    public void registerTool(ScanTool tool) {
        if (tool != null) {
            tools.add(tool);
            System.out.println("Outil enregistré : " + tool.name());
        }
    }

    public List<String> listTools() {
        List<String> names = new ArrayList<>();
        for (ScanTool t : tools) {
            names.add(t.name());
        }
        return names;
    }

    // ================== EXÉCUTION DES COMMANDES EXTERNES ==================

    public String runToolCommand(List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            proc.waitFor();
            return output.toString();
        } catch (Exception e) {
            System.out.println("Erreur runToolCommand : " + e.getMessage());
            return null;
        }
    }

    // ================== SCAN COMPLET ==================

    public void runFullScan(String scanType) {
        if (targets.isEmpty() || tools.isEmpty()) {
            System.out.println("⚠️ Cibles ou outils manquants.");
            return;
        }
        System.out.println("=== Lancement FULL SCAN: " + scanType + " ===");

        for (String cible : targets) {
            System.out.println("→ Cible : " + cible);

            for (ScanTool tool : tools) {
                System.out.println("   → Outil : " + tool.name());
                
                // CORRECTION CRITIQUE: Passe les deux arguments cible et scanType à l'outil
                List<Faille> resultats = tool.scan(cible, scanType); 

                if (resultats != null && !resultats.isEmpty()) {
                    for (Faille f : resultats) {
                        db.insertFaille(f);
                    }
                    System.out.println("     ✓ " + resultats.size() + " faille(s) ajoutée(s) depuis " + tool.name());
                } else {
                    System.out.println("     (aucune faille détectée)");
                }
            }
        }
        System.out.println("=== Full scan terminé ===\n");
    }
}