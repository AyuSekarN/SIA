<?php
// Mengatur header untuk CORS dan tipe konten JSON
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

// Menyertakan file koneksi ke database
include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    // Membaca input JSON dari client
    $raw_input = file_get_contents("php://input");
    $input = json_decode($raw_input, true);

    // Logging input untuk debugging
    file_put_contents('log_input.txt', $raw_input . PHP_EOL, FILE_APPEND);

    // Mendapatkan data utama
    $id_penjualan = $input['id_penjualan'] ?? null;
    $detail_items = $input['detail_items'] ?? [];

    // Validasi data
    if (!$id_penjualan || empty($detail_items)) {
        http_response_code(400);
        echo json_encode([
            "status" => "error",
            "message" => "Data tidak lengkap. id_penjualan: " . json_encode($id_penjualan) . ", detail_items: " . json_encode($detail_items)
        ]);
        exit;
    }

    // Memulai transaksi database
    $conn->begin_transaction();
    try {
        foreach ($detail_items as $item) {
            // Mengambil detail setiap item
            $id_barang = $item['id_barang'] ?? null;
            $jumlah_barang = $item['jumlah_barang'] ?? null;
            $sub_total = $item['sub_total'] ?? null;

            // Validasi data item
            if (!$id_barang || !$jumlah_barang || !$sub_total) {
                throw new Exception("Detail transaksi tidak lengkap: " . json_encode($item));
            }

            // Menyiapkan dan mengeksekusi query
            $stmt = $conn->prepare("INSERT INTO detail_transaksi (id_penjualan, id_barang, jumlah_barang, sub_total) VALUES (?, ?, ?, ?)");
            $stmt->bind_param("iiid", $id_penjualan, $id_barang, $jumlah_barang, $sub_total);

            if (!$stmt->execute()) {
                throw new Exception("Query Error: " . $stmt->error);
            }

            // Logging query sukses untuk debugging
            file_put_contents('log_success.txt', "Query berhasil: " . json_encode($item) . PHP_EOL, FILE_APPEND);

            $stmt->close();
        }

        // Commit transaksi jika semua berhasil
        $conn->commit();
        echo json_encode([
            "status" => "success",
            "message" => "Detail transaksi berhasil disimpan."
        ]);
    } catch (Exception $e) {
        // Rollback transaksi jika terjadi error
        $conn->rollback();

        // Logging error untuk debugging
        file_put_contents('log_error.txt', "Error: " . $e->getMessage() . PHP_EOL, FILE_APPEND);

        // Mengirimkan respons error ke client
        http_response_code(500);
        echo json_encode([
            "status" => "error",
            "message" => $e->getMessage()
        ]);
    }
} else {
    // Respons jika metode HTTP tidak diizinkan
    http_response_code(405);
    echo json_encode([
        "status" => "error",
        "message" => "Metode tidak diizinkan. Gunakan metode POST."
    ]);
}
?>
