import java.util.List;

/**
 * Interface définissant le contrat de base pour tout outil de scan réseau.
 * Tout outil doit pouvoir donner son nom et effectuer un scan sur une cible.
 */
public interface ScanTool {
    
    /**
     * Retourne le nom de l'outil pour le logging.
     * @return Le nom de l'outil.
     */
    String name(); 

    /**
     * Exécute le scan sur une cible spécifiée en fonction du type de scan demandé.
     * @param target L'adresse IP ou le nom d'hôte à scanner.
     * @param scanType Le type de scan demandé ("discret", "rapide", "complet").
     * @return Une liste des objets Faille trouvés.
     */
    List<Faille> scan(String target, String scanType); 
}