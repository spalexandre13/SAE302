<?php
header('Content-Type: application/json'); 
header('Access-Control-Allow-Origin: *'); 

$db_path = 'failles.db'; 

try {
    $db = new PDO("sqlite:$db_path");
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $query = "SELECT id, nom, description, ip, severite, source, dateDetection FROM failles ORDER BY id DESC";
    $result = $db->query($query);
    $failles = $result->fetchAll(PDO::FETCH_ASSOC);

    // ✅ ON ENVOIE DIRECTEMENT LE TABLEAU $failles
    // C'est ce que Retrofit attend pour remplir List<Faille>
    echo json_encode($failles, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    // En cas d'erreur, on renvoie un tableau vide pour ne pas faire crash Android
    echo json_encode([]);
}
?>
