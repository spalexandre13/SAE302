import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class NmapTool implements ScanTool {

    @Override
    public String name() {
        return "Nmap Scanner";
    }

    @Override
    public List<Faille> scan(String target, String typeScan) {
        List<Faille> failles = new ArrayList<>();
        System.out.println("   [Nmap] Configuration du scan : " + typeScan);

        // 1. Construction de la commande selon le type
        List<String> command = new ArrayList<>();
        command.add("nmap");
        
        // Options selon le type de scan choisi dans le menu
        if (typeScan.equals("rapide")) {
            command.add("-F"); // Fast scan (100 ports)
            command.add("-T4"); // Plus rapide
        } else if (typeScan.equals("complet")) {
            command.add("-p-"); // Tous les ports (très long)
        } else {
            // "discret" ou par défaut
            command.add("-sS"); // Stealth scan (nécessite sudo souvent, sinon TCP connect)
        }
        
        command.add("-sV"); // Version detection (important pour la description)
        command.add(target);

        System.out.println("   [Nmap] Exécution...");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            // 2. Parsing (Lecture) de la sortie Nmap
            while ((line = reader.readLine()) != null) {
                // On cherche les lignes qui contiennent "open" (ex: "80/tcp open http Apache...")
                if (line.contains("/tcp") && line.contains("open")) {
                    System.out.println("    -> Port trouvé : " + line);
                    
                    // On découpe la ligne pour récupérer les infos
                    String[] parts = line.split("\\s+", 4); // Découpe par espaces
                    String port = parts[0]; // ex: 80/tcp
                    String service = (parts.length > 2) ? parts[2] : "inconnu";
                    String version = (parts.length > 3) ? parts[3] : "";

                    Faille f = new Faille(
                        0,
                        "Port ouvert : " + port + " (" + service + ")", // Nom clair
                        "Version détectée : " + version, // Description détaillée
                        target,
                        "MEDIUM", // Sévérité par défaut pour un port ouvert
                        "Nmap",
                        java.time.LocalDate.now().toString(),
                        "Scan de ports"
                    );
                    failles.add(f);
                }
            }
            proc.waitFor();
        } catch (Exception e) {
            System.out.println("   [Erreur Nmap] " + e.getMessage());
        }

        return failles;
    }
}
