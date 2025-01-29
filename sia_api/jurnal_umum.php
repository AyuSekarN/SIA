<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php'; // File koneksi database

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $input = file_get_contents("php://input");
    $data = json_decode($input, true);

    $tanggal = isset($data['tanggal']) ? $data['tanggal'] : '';
    $jumlah = isset($data['jumlah']) ? $data['jumlah'] : 0.00;

    if (empty($tanggal) || empty($jumlah)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    $tanggal = mysqli_real_escape_string($conn, $tanggal);
    $jumlah = mysqli_real_escape_string($conn, $jumlah);

    // Mulai transaksi
    mysqli_begin_transaction($conn);

    try {
        // Entri Debit (Kas)
        $queryKas = "INSERT INTO jurnal_umum (tanggal, kode_akun, debet, kredit, deskripsi) 
                     VALUES ('$tanggal', '101','Kas', $jumlah, 0.00, 'Kas Dari pendapatan')";
        if (!mysqli_query($conn, $queryKas)) {
            throw new Exception("Gagal menyimpan entri Debit: " . mysqli_error($conn));
        }

        // Entri Kredit (Pendapatan Lain-Lain)
        $queryPendapatan = "INSERT INTO jurnal_umum (tanggal, kode_akun, akun, debet, kredit, deskripsi) 
                            VALUES ('$tanggal', '402', 'Pendapatan Lain', 0.00, $jumlah, 'Pendapatan Dari Pendapatan Lain')";
        if (!mysqli_query($conn, $queryPendapatan)) {
            throw new Exception("Gagal menyimpan entri Kredit: " . mysqli_error($conn));
        }

        // Commit transaksi
        mysqli_commit($conn);

        echo json_encode([
            "status" => "success",
            "message" => "Data jurnal berhasil ditambahkan",
        ]);
    } catch (Exception $e) {
        // Rollback jika ada kegagalan
        mysqli_rollback($conn);
        echo json_encode([
            "status" => "error",
            "message" => $e->getMessage(),
        ]);
    }
} elseif ($method == 'GET') {
    $query = "SELECT * FROM jurnal_umum";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }

    echo json_encode(empty($data)
        ? ["status" => "error", "message" => "Data tidak ditemukan"]
        : ["status" => "success", "data" => $data]);
}
?>
