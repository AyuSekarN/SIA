<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    // Ambil parameter id_penjualan dari query string
    $id_penjualan = $_GET['id_penjualan'] ?? null;

    if (!$id_penjualan) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "ID Penjualan tidak diberikan."]);
        exit;
    }

    // Query untuk mendapatkan detail transaksi berdasarkan id_penjualan
    $query = "SELECT id_barang, jumlah_barang, sub_total FROM detail_transaksi WHERE id_penjualan = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("i", $id_penjualan);

    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $details = [];

        while ($row = $result->fetch_assoc()) {
            $details[] = $row;
        }

        echo json_encode(["status" => "success", "data" => $details]);
    } else {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => "Gagal mengambil detail transaksi."]);
    }

    $stmt->close();
    exit;
}
?>