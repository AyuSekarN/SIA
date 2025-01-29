<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'GET') {
    if (isset($_GET['id_penjualan'])) {
        $id_penjualan = $_GET['id_penjualan'];

        // Validasi input
        if (!is_numeric($id_penjualan)) {
            echo json_encode([
                "status" => "error",
                "message" => "ID penjualan harus berupa angka."
            ]);
            exit;
        }

        // Query dengan JOIN untuk mengambil nama_customer
        $query = "
            SELECT 
                p.id_penjualan, 
                p.jumlah_piutang, 
                p.id_customer, 
                c.nama_customer 
            FROM 
                piutang p 
            JOIN 
                customer c 
            ON 
                p.id_customer = c.id_customer 
            WHERE 
                p.id_penjualan = ?";
        $stmt = $conn->prepare($query);

        if (!$stmt) {
            echo json_encode([
                "status" => "error",
                "message" => "Gagal menyiapkan statement."
            ]);
            exit;
        }

        $stmt->bind_param("i", $id_penjualan);
        $stmt->execute();
        $result = $stmt->get_result();

        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }

        if (!empty($data)) {
            echo json_encode([
                "status" => "success",
                "data" => $data
            ]);
        } else {
            echo json_encode([
                "status" => "error",
                "message" => "Data tidak ditemukan untuk ID penjualan $id_penjualan."
            ]);
        }
        $stmt->close();
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Parameter id_penjualan diperlukan."
        ]);
    }
    exit;
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Metode HTTP tidak valid."
    ]);
    exit;
}

?>
