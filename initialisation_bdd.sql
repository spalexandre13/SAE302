-- Fichier d'initialisation de la Base de Données SAE 3.02
-- Groupe : Samperez, Badaoui, Cruz-Mermy
-- Description : Structure de la base SQLite et données de démonstration.

BEGIN TRANSACTION;

-- 1. Nettoyage préalable (pour éviter les erreurs si on rejoue le script)
DROP TABLE IF EXISTS "failles";

-- 2. Création de la table principale
-- Note : Structure simple (monotable) adaptée au prototype Java/Android
CREATE TABLE IF NOT EXISTS "failles" (
    "id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "nom" TEXT,
    "description" TEXT,
    "ip" TEXT,
    "severite" TEXT,     -- Ex: LOW, MEDIUM, HIGH, CRITICAL
    "source" TEXT,       -- Ex: Nmap Scanner, Nikto, Simulateur
    "dateDetection" TEXT,
    "reference" TEXT
);

-- 3. Insertion de données de démonstration (Jeu de données varié)

-- A. Données du Simulateur (Pour tester les couleurs dans l'appli)
INSERT INTO failles VALUES(1,'Port 21 ouvert','Serveur FTP non sécurisé','192.168.1.10','HIGH','Simulateur','2025-11-28','ref-FTP');
INSERT INTO failles VALUES(2,'Port 80 HTTP','Port HTTP par défaut ouvert','192.168.1.15','MEDIUM','Simulateur','2025-11-28','ref-HTTP');
INSERT INTO failles VALUES(3,'Port 22 SSH','Port SSH par défaut','192.168.1.20','LOW','Simulateur','2025-11-28','ref-SSH');

-- B. Données Réelles - Scan Nmap (Infrastructure)
INSERT INTO failles VALUES(4,'Port ouvert: SSH (22/tcp)','Service: ssh | Version: OpenSSH 9.2p1 Debian 2+deb12u5','10.11.10.30','LOW','Nmap Scanner','2025-11-28','Réf: 22-tcp');
INSERT INTO failles VALUES(5,'Port ouvert: HTTP (80/tcp)','Service: http | Version: Apache httpd 2.4.65 ((Debian))','192.168.204.128','MEDIUM','Nmap Scanner','2025-12-24','Réf: 80-tcp');

-- C. Données Réelles - Scan Gobuster (Dossiers cachés)
INSERT INTO failles VALUES(6,'Dossier caché trouvé','/.htaccess (Status: 403) [Size: 280]','192.168.204.128','LOW','Gobuster','2025-12-24','Dir Enum');
INSERT INTO failles VALUES(7,'Dossier caché trouvé','/server-status (Status: 200) [Size: 4375]','192.168.204.128','LOW','Gobuster','2025-12-24','Dir Enum');

-- D. Données Réelles - Scan Nikto (Vulnérabilités Web)
INSERT INTO failles VALUES(38,'Vulnérabilité Web (Config)','X-Frame-Options header is not present (Clickjacking risk)','192.168.204.128','MEDIUM','Nikto','2025-12-24','Web Config Scan');
INSERT INTO failles VALUES(39,'Vulnérabilité Web (Config)','X-Content-Type-Options header is not set','192.168.204.128','MEDIUM','Nikto','2025-12-24','Web Config Scan');

COMMIT;
