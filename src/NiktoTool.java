import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NiktoTool implements ScanTool {

    @Override
    public String name() {
        return "Nikto Web Scanner";
    }

    @Override
    public List<Faille> scan(String target, String typeScan) {
        List<Faille> failles = new ArrayList<>();
        System.out.println("   [Nikto] Recherche de vulnérabilités Web sur " + target);

        List<String> command = new ArrayList<>();
        command.add("nikto");
        command.add("-h");
        command.add("http://" + target);
        command.add("-maxtime");
        command.add("45s"); // Timeout de sécurité côté Nikto
        command.add("-nointeractive");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);

        Process proc = null;
        try {
            proc = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            // On lit la sortie ligne par ligne
            while ((line = reader.readLine()) != null) {
                // Nikto affiche ses résultats avec un "+"
                if (line.startsWith("+")) {
                    System.out.println("    -> " + line);

                    // --- DETECTION DE LA FIN DU SCAN ---
                    // Si on voit "host(s) tested", c'est que Nikto a fini.
                    // On force l'arrêt pour ne pas rester bloqué.
                    if (line.contains("host(s) tested") || line.contains("End Time")) {
                        proc.destroy(); // On tue le processus proprement
                        break;          // On sort de la boucle
                    }
                    
                    // Analyse pour créer la Faille
                    String description = line.replace("+ ", "").trim();
                    String severite = "LOW";
                    
                    // Petite intelligence pour la sévérité
                    if (description.toLowerCase().contains("xss") || description.toLowerCase().contains("vulnerable")) {
                        severite = "HIGH";
                    } else if (description.toLowerCase().contains("outdated") || description.toLowerCase().contains("header")) {
                        severite = "MEDIUM";
                    }

                    // On n'ajoute pas les lignes de "résumé" comme failles
                    if (!line.contains("items tested") && !line.contains("End Time") && !line.contains("host(s) tested")) {
                         Faille f = new Faille(
                            0,
                            "Vulnérabilité Web (Config)",
                            description,
                            target,
                            severite,
                            "Nikto",
                            java.time.LocalDate.now().toString(),
                            "Web Config Scan"
                        );
                        failles.add(f);
                    }
                }
            }
            
            // Sécurité supplémentaire : si on sort de la boucle, on attend max 2 secondes que le processus meurt
            if (proc.isAlive()) {
                proc.waitFor(2, TimeUnit.SECONDS);
                proc.destroyForcibly();
            }

        } catch (Exception e) {
            System.out.println("   [Erreur Nikto] " + e.getMessage());
        } finally {
            // Dans tous les cas, on s'assure que le processus est mort
            if (proc != null && proc.isAlive()) {
                proc.destroyForcibly();
            }
        }

        return failles;
    }
}
