import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GobusterTool implements ScanTool {

    private static final String WORDLIST = "/usr/share/wordlists/dirb/common.txt";

    @Override
    public String name() {
        return "Gobuster (Dir Enum)";
    }

    @Override
    public List<Faille> scan(String target, String typeScan) {
        List<Faille> faillesTrouvees = new ArrayList<>();
        System.out.println("   [Gobuster] Scan web sur http://" + target);

        List<String> command = new ArrayList<>();
        command.add("gobuster");
        command.add("dir");
        command.add("-u");
        command.add("http://" + target);
        command.add("-w");
        command.add(WORDLIST);
        command.add("--no-color");
        command.add("-q"); // Quiet mode
        command.add("-t"); 
        command.add("10"); 

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        try {
            Process proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                // Exemple de ligne : "/admin (Status: 301) [Size: 323]"
                if (!line.isEmpty()) {
                    System.out.println("    -> " + line);
                    
                    // --- LOGIQUE D'AMÉLIORATION DU NOM ---
                    String nomFaille = "Dossier caché trouvé";
                    String description = line;
                    
                    // On essaie de séparer le chemin du reste (séparateur espace)
                    String[] parts = line.split(" ", 2);
                    if (parts.length > 0) {
                        nomFaille = "Dossier Web : " + parts[0]; // ex: "Dossier Web : /admin"
                    }
                    if (parts.length > 1) {
                        description = parts[1]; // ex: "(Status: 301) [Size: 323]"
                    }

                    Faille f = new Faille(
                        0, 
                        nomFaille, // NOM DYNAMIQUE ICI
                        description, // DESCRIPTION TECHNIQUE ICI
                        target, 
                        "LOW", 
                        "Gobuster", 
                        java.time.LocalDate.now().toString(), 
                        "Enumération Web"
                    );
                    faillesTrouvees.add(f);
                }
            }
            proc.waitFor();
        } catch (Exception e) {
            System.out.println("   [Erreur Gobuster] " + e.getMessage());
        }
        return faillesTrouvees;
    }
}
