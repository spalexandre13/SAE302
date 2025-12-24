<?php
// =====================================================================================
// FICHIER: api_failles.php
// RÔLE: Fournir les données des failles de sécurité au format JSON pour l'application Android.
// =====================================================================================

// --- 1. EN-TÊTES DE RÉPONSE CRITIQUES ---

// 1.1 Définit le type de contenu de la réponse comme JSON. ESSENTIEL pour Android.
header('Content-Type: application/json'); 

// 1.2 Permet aux applications externes (Android) de faire des requêtes (CORS).
header('Access-Control-Allow-Origin: *'); 

$db_path = 'failles.db'; // Le chemin vers votre base de données SQLite
$response = []; // Tableau qui contiendra la réponse finale (données ou erreur)

// --- 2. LOGIQUE DE LECTURE DE LA BASE DE DONNÉES ---

try {
    // Connexion à la base de données SQLite en utilisant PDO
    $db = new PDO("sqlite:$db_path");
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Requête pour récupérer tous les champs de la table 'failles'
    $query = "SELECT id, nom, description, ip, severite, source, dateDetection FROM failles ORDER BY severite DESC, id DESC";
    
    $result = $db->query($query);
    
    // Récupère toutes les lignes de résultat sous forme de tableau associatif (le format idéal pour l'encodage JSON)
    $failles = $result->fetchAll(PDO::FETCH_ASSOC);

    // Si la lecture réussit, stocke les données des failles dans la réponse
    $response['success'] = true;
    $response['data'] = $failles;

} catch (PDOException $e) {
    // 3. GESTION DES ERREURS (si l'application ne peut pas lire le fichier DB)
    $response['success'] = false;
    $response['message'] = 'Erreur BDD: Problème de connexion ou de lecture.';
    $response['details'] = $e->getMessage();
}

// --- 4. ENVOI DE LA RÉPONSE FINALE ---

// Encode le tableau $response en une chaîne JSON et l'affiche comme sortie du script.
echo json_encode($response, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

// Le script se termine ici. L'application Android lira cette sortie JSON.
?>
