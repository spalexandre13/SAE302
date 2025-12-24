<?php
// =====================================================================================
// FICHIER: failles.php
// RÔLE: Lit la base de données SQLite (failles.db) et affiche les résultats dans un tableau HTML.
// =====================================================================================

// NOTE IMPORTANTE: Ajustez ce chemin pour qu'il pointe vers votre fichier failles.db.
$db_path = 'failles.db'; 

// --- LOGIQUE PHP : CONNEXION ET RÉCUPÉRATION DES DONNÉES ---

try {
    // Connexion à la base de données SQLite en utilisant PDO
    $db = new PDO("sqlite:$db_path");
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Requête SQL pour récupérer les champs nécessaires
    $query = "SELECT id, nom, ip, severite, dateDetection, description FROM failles ORDER BY id DESC";
    $result = $db->query($query);
    $failles = $result->fetchAll(PDO::FETCH_ASSOC);

} catch (PDOException $e) {
    // Gestion des erreurs de connexion/permission BDD
    echo "<h1 style='color: red;'>Erreur de Base de Données</h1>";
    echo "<p>Impossible de se connecter ou de lire la BDD: " . htmlspecialchars($e->getMessage()) . "</p>";
    exit();
}
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Rapport des Failles de Sécurité - SAE 302</title>
    <style>
        body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            margin: 0; 
            padding: 20px; 
            background-color: #f4f6f9; /* Fond légèrement gris */
            color: #333;
        }
        h1 { 
            color: #1e3a8a; /* Bleu foncé professionnel */
            border-bottom: 2px solid #ccc;
            padding-bottom: 10px;
        }
        table { 
            width: 100%; 
            border-collapse: collapse; 
            margin-top: 25px; 
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Légère ombre */
        }
        th, td { 
            border: 1px solid #ddd; 
            padding: 12px; 
            text-align: left; 
        }
        th { 
            background-color: #e9ecef; /* Tête de colonne claire */
            color: #333;
            font-weight: 600;
        }
        /* Mise en évidence des sévérités avec couleurs spécifiques */
        .high { background-color: #fce8e8; color: #cc0000; font-weight: bold; } /* Rouge très clair */
        .medium { background-color: #fff4e5; color: #ff9900; } /* Orange très clair */
        
        /* Style pour la zone de saisie (Exigence SAE) */
        .input-area {
            background-color: #fff;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .disabled-button {
            background-color: #ccc;
            color: #666;
            cursor: not-allowed;
            border: none;
            padding: 8px 15px;
            border-radius: 3px;
        }
    </style>
</head>
<body>

    <h1>Rapport des Failles de Sécurité - SAE 302</h1>
    <p>Source : Lecture directe du fichier failles.db.</p>

    <div class="input-area">
        <h2>Action de l'utilisateur (Exigence SAE)</h2>
        <form>
            <label for="ip_scan">IP Cible pour l'App Java :</label>
            <input type="text" id="ip_scan" name="ip_scan" placeholder="Ex: 10.136.96.83" required>
            <button class="disabled-button" disabled>Lancer Scan (via API future)</button>
            <p style="font-size: 0.8em; color: #999; margin-top: 5px;">Le lancement du scan est effectué par l'application console Java.</p>
        </form>
    </div>

    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Sévérité</th>
                <th>Nom de la Faille</th>
                <th>Adresse IP</th>
                <th>Description</th>
                <th>Date Détection</th>
            </tr>
        </thead>
        <tbody>
            <?php if (empty($failles)): ?>
                <tr><td colspan="6" style="text-align: center;">🛡️ Aucune faille trouvée dans la base de données.</td></tr>
            <?php else: ?>
                <?php foreach ($failles as $faille): ?>
                    <?php 
                        // Détermine la classe CSS en fonction de la sévérité
                        $severite = strtoupper($faille['severite']);
                        $row_class = strtolower($severite);
                        if ($row_class != 'high' && $row_class != 'medium') {
                            $row_class = ''; // Pas de style pour LOW ou autre
                        }
                    ?>
                    <tr class="<?php echo $row_class; ?>">
                        <td><?php echo htmlspecialchars($faille['id']); ?></td>
                        <td><?php echo htmlspecialchars($severite); ?></td>
                        <td><?php echo htmlspecialchars($faille['nom']); ?></td>
                        <td><?php echo htmlspecialchars($faille['ip']); ?></td>
                        <td><?php echo htmlspecialchars($faille['description']); ?></td>
                        <td><?php echo htmlspecialchars($faille['dateDetection']); ?></td>
                    </tr>
                <?php endforeach; ?>
            <?php endif; ?>
        </tbody>
    </table>
</body>
</html>
