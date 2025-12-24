# 🛡️ SAE302 - Scanner & Gestion des Failles (Cybersécurité) 2025/2026

**Groupe :** SAMPEREZ Alexandre, BADAOUI Walid, CRUZ-MERMY Julien

---

## 🎯 Objectif du Projet (Exigence SAE)

Ce projet vise à développer une chaîne complète d'applications communicantes dans le domaine de la cybersécurité:

1.  **Application Java** : Scan du réseau local pour identifier les failles (ports ouverts, services).
2.  **Base de Données (SQLite)** : Stockage des résultats du scan.
3.  **Site Web & API JSON** : Lecture des failles depuis la base de données et affichage.
4.  **Application Android** : Communication avec l'API Web pour visualiser les failles.

Le prototype Java ci-dessous démontre le flux : **Scanner (plugins) → Database (SQLite) → Affichage console**.ns par des wrappers d'outils réels (nmap, nikto, ...) et API web.

---

## Équipe & RACI (synthèse)
![RACI image](https://github.com/user-attachments/assets/7a2a5f5c-f53a-4f45-85d0-a2d866475414)

> Tous = **I** sur tout (capables d’expliquer l’ensemble).

**RACI résumé (version courte)**  
- **Walid** : Responsable & Accountable (R/A) pour toute la partie **Java** (Faille, DatabaseManager, ScannerReseau, App).  
- **Alexandre** : Responsable & Accountable (R/A) pour **site web / backend**.  
- **Julien** : Responsable & Accountable (R/A) pour **Android** (UI / intégration) et tests.  
> Note : chaque membre produit un court rapport individuel sur l'usage de l'IA (`IA_report_<nom>.md`).

---

## Diagramme simplifié des classes (Mermaid)

## 🏗️ Diagramme de l'Architecture Java (Mermaid)

Le cœur du projet Java repose sur l'injection de dépendances et les interfaces, permettant l'ajout de nouveaux outils (plugins) sans modifier l'orchestrateur.

```mermaid
classDiagram
    class App {
        +main()
    }
    class ScannerReseau {
        -DatabaseManager db
        -List~ScanTool~ tools
        +runToolCommand(List<String>)
        +runFullScan()
    }
    class ScanTool {
        <<interface>>
        +name()
        +scan(target, type)
    }
    class NmapTool {
        +scan(target, type)
    }
    class DatabaseManager {
        +createTable()
        +insertFaille(Faille) : int
        +listFailles() : List~Faille~
    }
    class Faille {
        -int id
        -String nom
        -String ip
        -String severite
        -String dateDetection
    }

    App --> ScannerReseau
    ScannerReseau --> DatabaseManager
    ScannerReseau --> ScanTool
    NmapTool ..|> ScanTool
    DatabaseManager --> Faille
```
---

## ⚙️ Manuel d’installation et d’utilisation

### 1️⃣ Prérequis Kali Linux / Debian

* **JDK 11+** installé
  Partie Java
  Vérifier :

  ```bash
  sudo apt install -y default-jdk
  javac -version
  java -version
  ```
* **Driver JDBC SQLite (Xerial)** :
  Télécharger [`sqlite-jdbc-3.51.0.0.jar`](https://github.com/xerial/sqlite-jdbc) et le placer dans `lib/` ou `src/`.
* (Optionnel) **DB Browser for SQLite** pour visualiser `failles.db`.
* Fonctionne sur **Windows**, **Linux** et **macOS**.

Partie Web (Serveur Apache/PHP)
```bash
sudo apt install apache2
sudo apt install php php-sqlite3
sudo systemctl restart apache2
  ```
Note : L'installation de php-sqlite3 est indispensable pour éviter l'erreur "could not find driver".

---

### 2️⃣ Installation

1. Cloner le dépôt :

   ```bash
   git clone <URL_DU_REPO>
   cd <repo>/src
   ```
2. Copier le fichier `sqlite-jdbc-3.51.0.0.jar` dans `lib/`
   
    ```bash
   mv sqlite-jdbc-3.51.0.0.jar lib
   ```

4. Mise en place des fichiers Web : Copiez les fichiers vers le répertoire du serveur Apache :
   
   ```bash
   sudo cp src/failles.php /var/www/html/
   sudo cp src/failles.db /var/www/html/
   ```

5. Permissions critiques (SQLite) : Pour que le script PHP puisse lire la base de données, donnez les droits d'écriture au dossier :

   ```bash
   sudo chmod 666 /var/www/html/failles.db
   sudo chmod 777 /var/www/html/
   ```
---

### 3️⃣ Compilation

Dans le dossier `src` :

```bash
cd <repo>/src
# Compilation de tous les fichiers .java en incluant le JAR dans lib/
javac -cp "../lib/*:." *.java
```

---

### 4️⃣ Exécution

#### Sous **Windows**

```powershell
java -cp ".;sqlite-jdbc-3.51.0.0.jar" App
```

#### Sous **Linux / macOS**

```bash
# Exécution du programme
java -cp "../lib/sqlite-jdbc-3.51.0.0.jar:." App
```

---

### 5️⃣ Scénario de vérification (TD2)

1. **1 →** Créer la table `failles`.
2. **2 →** Lancer la détection simulée (ajoute 2 failles factices).
3. **3 →** Lister toutes les failles.
4. **6 →** Enregistrer `DummyTool` (plugin de test).
5. **9 →** Ajouter une cible (ex. `192.168.1.10`).
6. **8 →** Lancer `runFullScan()` (tous les outils sur toutes les cibles).
7. **3 →** Relister pour vérifier les nouvelles entrées.
8. **10 →** Supprimer une faille (test delete).
9. **11 →** Modifier une faille (test update).

---

### 6️⃣ Vérification dans la base

Ouvrir la base `failles.db` :

```bash
sqlite3 failles.db
```

Commandes utiles :

```sql
SELECT COUNT(*) FROM failles;
SELECT id, nom, ip, severite FROM failles LIMIT 10;
```

Nettoyer les doublons :

```sql
DELETE FROM failles
WHERE id NOT IN (
  SELECT MIN(id)
  FROM failles
  GROUP BY nom, description, ip, severite, source, dateDetection, reference
);
```

Exporter la base :

```bash
sqlite3 failles.db ".dump" > init_db.sql
```

---

### 7️⃣ Structure du dépôt

| Dossier/Fichier                | Description                    |
| ------------------------------ | ------------------------------ |
| `src/Faille.java`              | Classe modèle de vulnérabilité |
| `src/DatabaseManager.java`     | CRUD SQLite complet            |
| `src/ScannerReseau.java`       | Gestion des cibles + outils    |
| `src/ScanTool.java`            | Interface pour plugins         |
| `src/DummyTool.java`           | Exemple d’outil factice        |
| `src/App.java`                 | Menu console principal         |
| `lib/sqlite-jdbc-3.51.0.0.jar` | Driver SQLite                  |
| `failles.db`                   | Base de données                |
| `failles.php`                  | Site web                       |
| `api_failles.php`              | API communication Web - Android|
| `screens/`                     | Captures du projet             |
| `README.md`                    | Documentation et manuel        |

---

### 8️⃣ Ajouter un nouvel outil

Créer une classe implémentant `ScanTool` :

```java
public class MyNmapTool implements ScanTool {
    public String name() { return "nmap"; }
    public List<Faille> scan(String target) {
        return List.of(new Faille(0, "Test", "Port 22 ouvert", target, "LOW", "nmap", "2025-11-08", "NMAP-01"));
    }
}
```

Enregistrer le nouvel outil :

```java
scanner.registerTool(new MyNmapTool());
```

Puis exécuter via :

```
Option 8 → Run full scan
```

---

### 📧 Contacts

* **Walid Badaoui** — Java / SQLite / Scanner
* **Alexandre Samperez** — site web
* **Julien Cruz-Mermy** — Android / tests


---

**Fichiers obligatoires :**

* `src/*.java`
* `lib/sqlite-jdbc-3.51.0.0.jar`
* `failles.db` ou `init_db.sql`
* `screens/`
* `README.md`
* `RACI.md`


---
### Maquette de l'application android
<img width="791" height="715" alt="PremierVisuelAndroid" src="https://github.com/user-attachments/assets/6327cb7a-10f8-4715-8b85-e61b5ca05946" />



✅ **Ce README sert de documentation et  de manuel d’installation.**
