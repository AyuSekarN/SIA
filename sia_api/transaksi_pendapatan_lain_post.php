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
    $tanggal_transaksi = $input['tanggal'] ?? null;
    $nama_pendapatan = $input['deskripsi'] ?? null;
    $jumlah_pendapatan = $input['jumlah'] ?? null;

    // Log input untuk debug
    error_log("Input diterima: " . json_encode($input));

    // Validasi bahwa data yang dibutuhkan ada
    if (!$kode_akun || !$tanggal_transaksi || !$nama_pendapatan || !$jumlah_pendapatan) {
        http_response_code(400);
        $missingFields = [];
        if (!$kode_akun) $missingFields[] = "kode_akun";
        if (!$tanggal_transaksi) $missingFields[] = "tanggal_transaksi";
        if (!$nama_pendapatan) $missingFields[] = "nama_pendapatan";
        if (!$jumlah_pendapatan) $missingFields[] = "jumlah_pendapatan";
        error_log("Missing fields: " . implode(", ", $missingFields));
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap.", "missing_fields" => $missingFields]);
        exit;
    }

    // Query untuk memasukkan data
    $stmt = $conn->prepare("INSERT INTO transaksi_pendapatanlain_lain 
        (kode_akun, tanggal_transaksi, nama_pendapatan, jumlah_pendapatan) 
        VALUES (?, ?, ?, ?)");

    if (!$stmt) {
        http_response_code(500);
        error_log("Prepare failed: " . $conn->error);
        echo json_encode(["status" => "error", "message" => "Database error: prepare statement failed", "error" => $conn->error]);
        exit;
    }

    // Menyusun parameter untuk query
    if (!$stmt->bind_param("issd", $kode_akun, $tanggal_transaksi, $nama_pendapatan, $jumlah_pendapatan)) {
        http_response_code(500);
        error_log("Bind failed: " . $stmt->error);
        echo json_encode(["status" => "error", "message" => "Database error: bind parameter failed", "error" => $stmt->error]);
        exit;
    }

    // Mengeksekusi query dan mengirimkan response sesuai hasilnya
    if ($stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Data transaksi berhasil ditambahkan"]);
    } else {
        http_response_code(500);
        error_log("Execute failed: " . $stmt->error);
        echo json_encode(["status" => "error", "message" => "Database error: execute failed", "error" => $stmt->error]);
    }

    // Menutup prepared statement
    $stmt->close();
} else {
    http_response_code(405);
    error_log("Invalid request method: " . $method);
    echo json_encode(["status" => "error", "message" => "Metode HTTP tidak diizinkan"]);
}
?>