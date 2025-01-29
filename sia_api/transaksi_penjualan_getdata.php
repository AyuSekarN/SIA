<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';


$method = $_SERVER['REQUEST_METHOD'];

// GET: Ambil ID Penjualan terakhir
if ($_SERVER['REQUEST_METHOD'] == 'GET' && isset($_GET['get_id_penjualan'])) {
    $query = "SELECT AUTO_INCREMENT 
              FROM INFORMATION_SCHEMA.TABLES 
              WHERE TABLE_SCHEMA = DATABASE() 
              AND TABLE_NAME = 'transaksi_penjualan'";
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        echo json_encode([
            "status" => "success",
            "next_id_penjualan" => $row['AUTO_INCREMENT']
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Failed to fetch next ID"
        ]);
    }
}

if ($method == 'GET') {
    $query = "SELECT * FROM transaksi_penjualan";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }
    echo json_encode($data);
}

?>


