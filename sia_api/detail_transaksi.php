<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);

    $id_penjualan = $input['id_penjualan'] ?? null;
    $detail_items = $input['detail_items'] ?? [];

    if (!$id_penjualan || empty($detail_items)) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap."]);
        exit;
    }

    $conn->begin_transaction();
    try {
        foreach ($detail_items as $item) {
            $id_barang = $item['id_barang'] ?? null;
            $jumlah_barang = $item['jumlah_barang'] ?? null;
            $sub_total = $item['sub_total'] ?? null;

            if (!$id_barang || !$jumlah_barang || !$sub_total) {
                throw new Exception("Detail transaksi tidak lengkap.");
            }

            $stmt = $conn->prepare("INSERT INTO detail_transaksi (id_penjualan, id_barang, jumlah_barang, sub_total) VALUES (?, ?, ?, ?)");
            $stmt->bind_param("iiid", $id_penjualan, $id_barang, $jumlah_barang, $sub_total);

            if (!$stmt->execute()) {
                throw new Exception($stmt->error);
            }

            $stmt->close();
        }

        $conn->commit();
        echo json_encode(["status" => "success", "message" => "Detail transaksi berhasil disimpan."]);
    } catch (Exception $e) {
        $conn->rollback();
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
} else {
    http_response_code(405);
    echo json_encode(["status" => "error", "message" => "Metode tidak diizinkan."]);
}
?>
