import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale (main) de l'application Java SAE302.
 * Version finale : Intégration Nmap + Gobuster + BDD Web
 */
public class App {

    private static void displayMenu() {
        System.out.println("\n--- MENU SAE302 TD2/TD3 ---");
        System.out.println("1 - Créer la table (si besoin)");
        System.out.println("2 - Lancer détection simulée (TD2)");
        System.out.println("3 - Lister toutes les failles");
        System.out.println("4 - Chercher une faille par ID");
        System.out.println("5 - Filtrer par sévérité");
        System.out.println("6 - Enregistrer DummyTool (Test)");
        System.out.println("7 - Lister outils enregistrés");
        System.out.println("8 - Lancer un FULL SCAN (Nmap + Gobuster)");
        System.out.println("9 - Ajouter une cible (IP)");
        System.out.println("10 - Supprimer une faille par ID");
        System.out.println("11 - Mettre à jour une faille (ID)");
        System.out.println("0 - Quitter");
    }

    public static void main(String[] args) {
        // 1. Initialisation avec le chemin vers le dossier Web
        // (Assure-toi d'avoir fait le chmod 777 sur /var/www/html avant !)
        DatabaseManager db = new DatabaseManager("/var/www/html/failles.db");
        
        // On utilise 'open()' car c'est le nom dans ton DatabaseManager corrigé
        db.open(); 
        
        ScannerReseau scanner = new ScannerReseau(db);
        Scanner sc = new Scanner(System.in);
        int choice = -1;

        // 2. ENREGISTREMENT DES OUTILS (Nmap ET Gobuster)
        // Ils sont ajoutés dès le démarrage pour être prêts pour l'option 8
        scanner.registerTool(new NmapTool()); 
        scanner.registerTool(new GobusterTool());
        scanner.registerTool(new NiktoTool());

        System.out.println("✅ Application démarrée.");
        System.out.println("✅ Outils chargés : Nmap, Gobuster.");
        System.out.println("📂 Base de données : /var/www/html/failles.db");

        // Boucle principale du menu
        while (choice != 0) {
            displayMenu();
            System.out.print("Choix: ");

            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consomme le retour à la ligne

                switch (choice) {
                    case 1:
                        // Correspond à la méthode dans DatabaseManager
                        db.createTableIfNotExists(); 
                        break;
                    case 2:
                        scanner.detecterFaillesSimule();
                        break;
                    case 3:
                        // Correspond à la méthode renommée getAllFailles()
                        List<Faille> toutesFailles = db.getAllFailles();
                        if (toutesFailles.isEmpty()) {
                            System.out.println("Aucune faille dans la base de données.");
                        } else {
                            for (Faille f : toutesFailles) {
                                System.out.println(f);
                            }
                        }
                        break;
                    case 4:
                        System.out.print("Entrez l'ID de la faille à chercher : ");
                        int searchId = sc.nextInt();
                        sc.nextLine();
                        Faille f = db.getFailleById(searchId);
                        if (f != null) {
                            System.out.println(f);
                        } else {
                            System.out.println("Faille avec ID " + searchId + " non trouvée.");
                        }
                        break;
                    case 5:
                        System.out.print("Entrez la sévérité (LOW, MEDIUM, HIGH) : ");
                        String severiteFiltre = sc.nextLine().toUpperCase();
                        // Correspond à getFaillesBySeverity()
                        List<Faille> faillesFiltrees = db.getFaillesBySeverity(severiteFiltre);
                        if (faillesFiltrees.isEmpty()) {
                            System.out.println("Aucune faille trouvée avec la sévérité " + severiteFiltre);
                        } else {
                            for (Faille faille : faillesFiltrees) {
                                System.out.println(faille);
                            }
                        }
                        break;
                    case 6:
                        // Option manuelle pour ajouter le DummyTool
                        scanner.registerTool(new DummyTool());
                        break;
                    case 7:
                        System.out.println("Outils enregistrés: " + scanner.listTools());
                        break;
                    case 8:
                        System.out.print("Choisissez le type de scan (discret, rapide, complet) : ");
                        String typeScan = sc.nextLine().toLowerCase();
                        // Lance le scan avec TOUS les outils enregistrés (Nmap + Gobuster)
                        scanner.runFullScan(typeScan);
                        break;
                    case 9:
                        System.out.print("Nouvelle cible (IP) ou réseau (CIDR) : ");
                        String cible = sc.nextLine();
                        if (cible.contains("/")) {
                            scanner.ajouterReseauCible(cible);
                        } else {
                            scanner.ajouterCible(cible);
                        }
                        break;
                    case 10:
                        System.out.print("Entrez l'ID de la faille à supprimer : ");
                        int deleteId = sc.nextInt();
                        sc.nextLine();
                        db.deleteFaille(deleteId);
                        break;
                    case 11:
                        System.out.println("Fonction mise à jour (voir code précédent si besoin).");
                        break;
                    case 0:
                        System.out.println("Fermeture de l'application.");
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur: Veuillez entrer un nombre pour le choix.");
                sc.nextLine(); 
            } catch (Exception e) {
                System.out.println("Une erreur est survenue: " + e.getMessage());
            }
        }

        db.close(); // Fermeture propre
        sc.close();
    }
}
