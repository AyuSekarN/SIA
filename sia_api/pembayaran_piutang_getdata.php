<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';


$method = $_SERVER['REQUEST_METHOD'];

if ($_SERVER['REQUEST_METHOD'] == 'GET' && isset($_GET['get_id_pembayaranpiutang'])) {
    $query = "SELECT AUTO_INCREMENT 
              FROM INFORMATION_SCHEMA.TABLES 
              WHERE TABLE_SCHEMA = DATABASE() 
              AND TABLE_NAME = 'pembayaran_piutang'";
    $result = $conn->query($query);

    if ($result && $result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "next_id_pembayaranpiutang" => $row['AUTO_INCREMENT']
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Failed to fetch next ID",
            "debug_query" => $query, // Tambahkan log query
            "debug_result" => $result // Tambahkan log hasil query
        ]);
    }
}



if ($method == 'GET') {
    $query = "SELECT * FROM pembayaran_piutang";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }
    echo json_encode($data);
}

?>
