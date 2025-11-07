<h1>SAE302 - Prototype Scanner & Gestion des failles 2025/2026</h1>
<p>Groupe constituée de SAMPEREZ Alexandre(spalexandre13), BADAOUI Walid, CRUZ-MERMY Julien</p>
<p>Ce projet vise à créer un site web qui répertorie toutes les photos du banc avionniques lors de l'utilisation du banc ou toutes les 24 heures sans utilisations. Ce site web contient la création d'un programme python ainsi que d'une base de donnée fonctionnel</p>
<h2>Objectif SAE302</h2> 
Prototype Java démontrant le flux : *Scanner (plugins) → Database (SQLite) → Affichage / API*. Version TD2 : simulation et plugin system (DummyTool). TD3 : remplacement des simulations par des wrappers d'outils réels et API web.


## Équipe & RACI (synthèse)
- **Ilias** — DatabaseManager (R) : CRUD, ouverture/fermeture, mapping ResultSet → Faille.  
- **Walid (Badaoui)** — ScannerReseau & App (R) : orchestration scans, menu console, intégration plugins.  
- **Ezio** — Documentation & Git (R) : README, screenshots, packaging, push.  
> Tous = I sur tout (capables d’expliquer l’ensemble).

---

## Contenu du dépôt (fichiers Java)
- `src/Faille.java` — modèle de vulnérabilité.  
- `src/DatabaseManager.java` — CRUD SQLite (create, insert, select, update, delete).  
- `src/ScannerReseau.java` — orchestration targets + plugin registry + runFullScan().  
- `src/ScanTool.java` — interface plugin (contract).  
- `src/DummyTool.java` — outil factice (safe).  
- `src/App.java` — classe principale (menu console pour la démo).  
- `lib/sqlite-jdbc-3.51.0.0.jar` — driver JDBC SQLite (ou lien dans README si trop gros).  
- `failles.db` ou `init_db.sql` — base livrée (optionnel).  


---

## Prérequis
- Driver JDBC SQLite (jar) : sqlite-jdbc-3.51.0.0.jar dans lib/ ou à la racine.
- JDK installé (Java 11+ recommandé). Vérifier :
  ```bash
  javac -version
  java -version
