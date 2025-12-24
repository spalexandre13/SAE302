import java.util.ArrayList;
import java.util.List;

/**
 * DummyTool implémente ScanTool.
 * Il sert d'outil de simulation/test pour valider que l'architecture 
 * de l'application (ScannerReseau) fonctionne correctement.
 */
public class DummyTool implements ScanTool {

    @Override
    public String name() {
        return "Dummy Tool (Simulation)";
    }

    /**
     * Implémentation du scan pour la simulation. 
     * Il retourne une liste vide, confirmant que le composant est actif.
     * * @param target L'adresse IP (non utilisée dans la simulation).
     * @param scanType Le type de scan (non utilisé dans la simulation).
     * @return Une liste vide de Faille.
     */
    @Override
    public List<Faille> scan(String target, String scanType) {
        // Retourne une liste vide pour la simulation, garantissant que la chaîne d'appel fonctionne.
        return new ArrayList<>(); 
    }
}