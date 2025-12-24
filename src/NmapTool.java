import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * NmapTool implémente ScanTool. 
 * Il est responsable de l'exécution de la commande nmap et du parsing de la sortie 
 * pour créer des objets Faille détaillés.
 */
public class NmapTool implements ScanTool {

    private final ScannerReseau scannerReseau;

    /**
     * Constructeur pour injecter la dépendance ScannerReseau, 
     * ce qui permet d'utiliser la méthode runToolCommand.
     */
    public NmapTool(ScannerReseau scannerReseau) {
        this.scannerReseau = scannerReseau;
    }

    public NmapTool() {
        this(null);
    }

    @Override
    public String name() {
        return "Nmap Scanner";
    }

    /**
     * Définit la commande Nmap basée sur le type de scan demandé et exécute le scan.
     * @param target L'adresse IP à scanner.
     * @param scanType Le type de scan ("discret", "rapide", "complet").
     * @return Une liste de Faille détaillée.
     */
    @Override
    public List<Faille> scan(String target, String scanType) {
        if (scannerReseau == null) {
            System.err.println("NmapTool n'est pas initialisé avec ScannerReseau. Impossible d'exécuter la commande.");
            return new ArrayList<>();
        }
        
        List<Faille> results = new ArrayList<>();
        List<String> command = new ArrayList<>();
        command.add("nmap");
        
        // --- 1. DÉFINITION DE LA COMMANDE NMAP ---
        
        switch (scanType.toLowerCase()) {
            case "discret":
                // Furtif et ciblé sur les ports les plus courants pour éviter les IDS
                command.addAll(Arrays.asList("-sS", "-T0", "-p", "1-1024", target));
                break;
            case "rapide":
                // Rapide et suffisant pour les infos de base (-F scanne les 100 ports les plus communs)
                command.addAll(Arrays.asList("-sV", "-F", target));
                break;
            case "complet":
                // Le plus complet: version, scripts par défaut, tous les ports.
                command.addAll(Arrays.asList("-sV", "-sC", "-p", "1-65535", target));
                break;
            default:
                // Par défaut, utilise le scan rapide pour des résultats rapides
                command.addAll(Arrays.asList("-sV", "-F", target));
        }

        System.out.println("   [Nmap] Commande exécutée: " + String.join(" ", command));

        // --- 2. EXÉCUTION ET RÉCUPÉRATION DE LA SORTIE ---
        String resultat = scannerReseau.runToolCommand(command);

        if (resultat == null || resultat.isEmpty()) {
            System.out.println("   [Nmap] Aucune sortie reçue.");
            return results;
        }

        // --- 3. PARSING DÉTAILLÉ ---
        
        String[] lines = resultat.split("\n");
        String dateDetection = LocalDate.now().toString();

        for (String line : lines) {
            // Cherche une ligne qui commence par un numéro de port, contient "/tcp" et contient "open"
            if (line.matches("^\\d+/tcp\\s+open.*")) { 
                
                // Sépare la ligne en 4 parties max pour le port, l'état, le service et la version/description
                String[] parts = line.trim().split("\\s+", 4);
                
                if (parts.length >= 3) {
                    String port_proto = parts[0]; // Ex: 445/tcp
                    String service = parts[2]; // Ex: microsoft-ds
                    String version = (parts.length == 4) ? parts[3] : "Version non détectée"; // Version du logiciel

                    // Détermination simple de la sévérité (Logique de Cybersécurité)
                    String severite = "LOW";
                    if (port_proto.equals("445/tcp") || port_proto.equals("139/tcp")) {
                        severite = "HIGH"; // Services SMB/NetBIOS sont souvent des failles critiques (EternalBlue, etc.)
                    } else if (service.contains("ssl") || service.contains("vmware") || service.equalsIgnoreCase("ftp")) {
                         severite = "MEDIUM"; // Services d'infrastructure critiques ou non chiffrés (FTP)
                    }

                    // Création de l'objet Faille détaillé pour chaque port ouvert
                    Faille f = new Faille(
                        "Port ouvert: " + service.toUpperCase() + " (" + port_proto + ")",
                        "Service: " + service + " | Version: " + version.trim(),
                        target,
                        severite,
                        this.name(),
                        dateDetection,
                        "Réf: " + port_proto.replace("/", "-") 
                    );
                    results.add(f);
                }
            }
        }
        
        return results;
    }
}