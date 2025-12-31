<?php
// CONFIGURATION
$db_path = 'failles.db';
$target_ip = isset($_GET['ip']) ? $_GET['ip'] : null; // Récupère l'IP si on a cliqué dessus

try {
    $db = new PDO("sqlite:$db_path");
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if ($target_ip) {
        // --- CAS 2 : ON A CLIQUÉ SUR UNE IP -> On affiche ses failles ---
        $query = "SELECT * FROM failles WHERE ip = :ip ORDER BY id DESC";
        $stmt = $db->prepare($query);
        $stmt->execute(['ip' => $target_ip]);
        $failles = $stmt->fetchAll(PDO::FETCH_ASSOC);
    } else {
        // --- CAS 1 : ACCUEIL -> On affiche la liste des machines ---
        // On groupe par IP et on compte le nombre de failles pour chaque machine
        $query = "SELECT ip, COUNT(*) as count, MAX(dateDetection) as last_seen FROM failles GROUP BY ip ORDER BY last_seen DESC";
        $result = $db->query($query);
        $machines = $result->fetchAll(PDO::FETCH_ASSOC);
    }

} catch (PDOException $e) {
    die("<div style='color:red;'>Erreur BDD : " . htmlspecialchars($e->getMessage()) . "</div>");
}
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Sécurité - SAE 302</title>
    <style>
        /* --- DESIGN DARK MODE / CYBER --- */
        body { 
            margin: 0; padding: 20px; 
            background-color: #121212; 
            color: #e0e0e0; 
            font-family: 'Courier New', Courier, monospace; 
        }
        header { border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 30px; }
        h1 { color: #fff; text-transform: uppercase; letter-spacing: 2px; text-shadow: 0 0 10px rgba(255, 255, 255, 0.3); }
        .subtitle { color: #00e676; font-size: 0.9em; margin-top: 5px; }

        /* TABLEAUX */
        table { width: 100%; border-collapse: separate; border-spacing: 0; background-color: #1e1e1e; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.5); }
        th { background-color: #252525; color: #aaa; text-transform: uppercase; font-size: 0.85em; padding: 15px; text-align: left; border-bottom: 1px solid #333; }
        td { padding: 15px; border-bottom: 1px solid #2a2a2a; font-size: 0.95em; vertical-align: top; }
        tr:hover td { background-color: #2a2a2a; }

        /* UTILITAIRES COULEURS */
        .text-high { color: #ff3d00; font-weight: bold; text-shadow: 0 0 5px rgba(255, 61, 0, 0.4); }
        .text-medium { color: #ffc107; font-weight: bold; }
        .text-low { color: #00e676; font-weight: bold; }
        .ip-badge { background-color: #333; padding: 4px 8px; border-radius: 4px; color: #fff; }

        /* BOUTONS & LIENS */
        a.machine-link { color: #00e676; text-decoration: none; border: 1px solid #00e676; padding: 5px 10px; border-radius: 4px; transition: 0.3s; }
        a.machine-link:hover { background-color: #00e676; color: #000; }
        .btn-back { display: inline-block; margin-bottom: 20px; color: #fff; text-decoration: none; font-size: 1.2em; }
        .btn-back:hover { color: #00e676; }

        .footer { margin-top: 40px; text-align: center; font-size: 0.8em; color: #555; border-top: 1px solid #333; padding-top: 20px; }
    </style>
</head>
<body>

    <header>
        <h1>Dashboard Sécurité</h1>
        <?php if ($target_ip): ?>
            <div class="subtitle">> Analyse détaillée de la cible : <?php echo htmlspecialchars($target_ip); ?></div>
        <?php else: ?>
            <div class="subtitle">> Vue d'ensemble du parc informatique</div>
        <?php endif; ?>
    </header>

    <?php if ($target_ip): ?>
        
        <a href="failles.php" class="btn-back">← Retour à la liste des machines</a>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Sévérité</th>
                    <th>Type de Faille</th>
                    <th>Description</th>
                    <th>Date</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($failles as $faille): ?>
                    <?php 
                        $sev = strtoupper($faille['severite']);
                        $class = 'text-low';
                        if (strpos($sev, 'HIGH') !== false || strpos($sev, 'CRIT') !== false) $class = 'text-high';
                        elseif (strpos($sev, 'MED') !== false) $class = 'text-medium';
                    ?>
                    <tr>
                        <td style="color: #666;">#<?php echo $faille['id']; ?></td>
                        <td class="<?php echo $class; ?>"><?php echo $sev; ?></td>
                        <td style="font-weight: bold;"><?php echo htmlspecialchars($faille['nom']); ?></td>
                        <td style="color: #bbb;"><?php echo htmlspecialchars($faille['description']); ?></td>
                        <td style="color: #666;"><?php echo $faille['dateDetection']; ?></td>
                    </tr>
                <?php endforeach; ?>
            </tbody>
        </table>

    <?php else: ?>

        <table>
            <thead>
                <tr>
                    <th>Machine (IP)</th>
                    <th>Vulnérabilités détectées</th>
                    <th>Dernier Scan</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <?php if (empty($machines)): ?>
                    <tr><td colspan="4" style="text-align: center; padding: 30px;">Aucune machine scannée pour le moment.</td></tr>
                <?php else: ?>
                    <?php foreach ($machines as $m): ?>
                        <tr>
                            <td><span class="ip-badge"><?php echo htmlspecialchars($m['ip']); ?></span></td>
                            <td style="color: #fff; font-weight: bold;"><?php echo $m['count']; ?> failles trouvées</td>
                            <td style="color: #666;"><?php echo $m['last_seen']; ?></td>
                            <td>
                                <a href="?ip=<?php echo $m['ip']; ?>" class="machine-link">Voir les détails ></a>
                            </td>
                        </tr>
                    <?php endforeach; ?>
                <?php endif; ?>
            </tbody>
        </table>

    <?php endif; ?>

    <div class="footer">
        Développement d'applications communicantes | Système de monitoring temps réel v4.2
    </div>

</body>
</html>
