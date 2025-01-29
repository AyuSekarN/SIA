<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'GET') {
    $query = "SELECT * FROM transaksi_pendapatanlain_lain";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }
    echo json_encode($data);
    
    if (mysqli_num_rows($result) > 0) {
        while ($row = mysqli_fetch_assoc($result)) {
            $row['jumlah_pendapatan'] = "Rp" . number_format($row['jumlah_pendapatan'], 0, ',', '.');
            $data[] = $row;
        }
        echo json_encode($data);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "No data found"
        ]);
    }
    
}
?>