import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principale (main) de l'application Java SAE302.
 * Elle gère le menu utilisateur, l'orchestration des scans et la gestion de la base de données.
 */
public class App {

    private static void displayMenu() {
        System.out.println("\n--- MENU SAE302 TD2 ---");
        System.out.println("1 - Créer la table (si besoin)");
        System.out.println("2 - Lancer détection simulée");
        System.out.println("3 - Lister toutes les failles");
        System.out.println("4 - Chercher une faille par ID");
        System.out.println("5 - Filtrer par sévérité");
        System.out.println("6 - Enregistrer DummyTool (plugin)");
        System.out.println("7 - Lister outils enregistrés");
        System.out.println("8 - Lancer un scan");
        System.out.println("9 - Ajouter une cible (IP)");
        System.out.println("10 - Supprimer une faille par ID");
        System.out.println("11 - Mettre à jour une faille (ID)");
        System.out.println("0 - Quitter");
    }

    public static void main(String[] args) {
        // Initialisation des composants
        DatabaseManager db = new DatabaseManager("failles.db");
        db.connect(); // Tente la connexion
        ScannerReseau scanner = new ScannerReseau(db);
        Scanner sc = new Scanner(System.in);
        int choice = -1;

        // Enregistrement des outils au démarrage (Correction de l'initialisation de NmapTool)
        scanner.registerTool(new NmapTool(scanner)); // Initialisation correcte !
        
        System.out.println("Application SAE302 démarrée. Database: failles.db");

        // Boucle principale du menu
        while (choice != 0) {
            displayMenu();
            System.out.print("Choix: ");

            try {
                choice = sc.nextInt();
                sc.nextLine(); // Consomme le retour à la ligne

                switch (choice) {
                    case 1:
                        db.createTable();
                        break;
                    case 2:
                        // Choix 2: Détection simulée (vérification BDD)
                        scanner.detecterFaillesSimule();
                        break;
                    case 3:
                        // Choix 3: Lister toutes les failles
                        List<Faille> toutesFailles = db.listFailles();
                        if (toutesFailles.isEmpty()) {
                            System.out.println("Aucune faille dans la base de données.");
                        } else {
                            for (Faille f : toutesFailles) {
                                System.out.println(f);
                            }
                        }
                        break;
                    case 4:
                        // Choix 4: Chercher une faille par ID
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
                        // Choix 5: Filtrer par sévérité
                        System.out.print("Entrez la sévérité (LOW, MEDIUM, HIGH, CRITICAL) : ");
                        String severiteFiltre = sc.nextLine().toUpperCase();
                        List<Faille> faillesFiltrees = db.filterBySeverite(severiteFiltre);
                        if (faillesFiltrees.isEmpty()) {
                            System.out.println("Aucune faille trouvée avec la sévérité " + severiteFiltre);
                        } else {
                            for (Faille faille : faillesFiltrees) {
                                System.out.println(faille);
                            }
                        }
                        break;
                    case 6:
                        // Choix 6: Enregistrer DummyTool
                        scanner.registerTool(new DummyTool());
                        break;
                    case 7:
                        // Choix 7: Lister outils enregistrés
                        System.out.println("Outils enregistrés: " + scanner.listTools());
                        break;
                    case 8:
                        // Choix 8: Lancer un scan
                        System.out.print("Choisissez le type de scan : (discret, rapide, complet)\n");
                        String typeScan = sc.nextLine().toLowerCase();
                        if (typeScan.matches("discret|rapide|complet")) {
                            scanner.runFullScan(typeScan);
                        } else {
                            System.out.println("Type de scan invalide. Utilisez discret, rapide ou complet.");
                        }
                        break;
                    case 9:
                        // Choix 9: Ajouter une cible
                        System.out.print("Nouvelle cible (IP) ou réseau (CIDR) : ");
                        String cible = sc.nextLine();
                        if (cible.contains("/")) {
                            scanner.ajouterReseauCible(cible);
                        } else {
                            scanner.ajouterCible(cible);
                        }
                        break;
                    case 10:
                        // Choix 10: Supprimer une faille par ID
                        System.out.print("Entrez l'ID de la faille à supprimer : ");
                        int deleteId = sc.nextInt();
                        sc.nextLine();
                        db.deleteFaille(deleteId);
                        System.out.println("Tentative de suppression de l'ID " + deleteId);
                        break;
                    case 11:
                        // Choix 11: Mettre à jour une faille
                        System.out.print("Entrez l'ID de la faille à mettre à jour : ");
                        int updateId = sc.nextInt();
                        sc.nextLine();
                        
                        System.out.print("Nouveau nom (laisser vide pour ne pas changer) : ");
                        String nom = sc.nextLine();
                        
                        System.out.print("Nouvelle sévérité (LOW, MEDIUM, HIGH, CRITICAL ou vide) : ");
                        String severite = sc.nextLine().toUpperCase();
                        
                        if (db.updateFaille(updateId, nom, severite)) {
                            System.out.println("Faille ID " + updateId + " mise à jour.");
                        } else {
                            System.out.println("Échec de la mise à jour ou faille non trouvée.");
                        }
                        break;
                    case 0:
                        System.out.println("Fermeture de l'application.");
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Erreur: Veuillez entrer un nombre pour le choix.");
                sc.nextLine(); // Nettoie le buffer d'entrée
            } catch (Exception e) {
                System.out.println("Une erreur est survenue: " + e.getMessage());
            }
        }

        db.disconnect(); // Déconnexion propre de la BDD
        sc.close();
    }
}