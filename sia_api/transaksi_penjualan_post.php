<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    $kode_akun = $input['kode_akun'] ?? null;
    $tanggal_transaksi = $input['tanggal_transaksi'] ?? null;
    $id_customer = $input['id_customer'] ?? null;
    $sub_total = $input['sub_total'] ?? null;
    $diskon_barang = $input['diskon_barang'] ?? 0;
    $pajak = $input['pajak'] ?? 0;
    $total_bayar = $input['total_bayar'] ?? null;
    $jenis_bayar = $input['jenis_bayar'] ?? null;

    if (!$kode_akun || !$tanggal_transaksi || !$id_customer || !$sub_total || !$total_bayar || !$jenis_bayar) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap."]);
        exit;
    }

    // Mulai transaksi database
    mysqli_begin_transaction($conn);

    try {
        // Simpan data ke transaksi_penjualan
        $stmt = $conn->prepare("INSERT INTO transaksi_penjualan 
            (kode_akun, tanggal_transaksi, id_customer, sub_total, diskon_barang, pajak, total_bayar, jenis_bayar) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        $stmt->bind_param("ssidddds", $kode_akun, $tanggal_transaksi, $id_customer, $sub_total, $diskon_barang, $pajak, $total_bayar, $jenis_bayar);
        $stmt->execute();

        $id_penjualan = $conn->insert_id;

        // Commit transaksi
        mysqli_commit($conn);

        echo json_encode([
            "status" => "success",
            "message" => "Data transaksi berhasil ditambahkan",
            "id_penjualan" => $id_penjualan // Sertakan ID transaksi
        ]);
    } catch (Exception $e) {
        mysqli_rollback($conn);
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    } finally {
        $stmt->close();
    }
} elseif ($method == 'GET') {
    $query = "SELECT * FROM transaksi_penjualan";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }

    if (empty($data)) {
        echo json_encode(["status" => "error", "message" => "Data tidak ditemukan"]);
    } else {
        echo json_encode(["status" => "success", "data" => $data]);
    }
}
?>
