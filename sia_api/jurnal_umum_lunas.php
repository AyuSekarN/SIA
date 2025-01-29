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

    $tanggal = isset($data['tanggal_transaksi']) ? $data['tanggal_transaksi'] : '';
    $jumlah = isset($data['total_bayar']) ? $data['total_bayar'] : 0.00;

    if (empty($tanggal) || empty($jumlah)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    $tanggal = mysqli_real_escape_string($conn, $tanggal);
    $jumlah = mysqli_real_escape_string($conn, $jumlah);

    // Mulai transaksi
    mysqli_begin_transaction($conn);

    try {
        // Entri Debit (Kas/Bank)
        $queryKas = "INSERT INTO jurnal_umum (tanggal, kode_akun, akun, debet, kredit, deskripsi) 
                     VALUES ('$tanggal', 101,'Kas', $jumlah, 0.00, 'Kas Dari Pendapatan')";
        if (!mysqli_query($conn, $queryKas)) {
            throw new Exception("Gagal menyimpan entri Debit: " . mysqli_error($conn));
        }

        // Entri Kredit (Pendapatan Penjualan)
        $queryPendapatan = "INSERT INTO jurnal_umum (tanggal, kode_akun, akun, debet, kredit, deskripsi) 
                            VALUES ('$tanggal', 401,'Pendapatan Penjualan', 0.00, $jumlah, 'Pendapatan Dari Penjualan')";
        if (!mysqli_query($conn, $queryPendapatan)) {
            throw new Exception("Gagal menyimpan entri Kredit: " . mysqli_error($conn));
        }

        mysqli_commit($conn);
        echo json_encode(["status" => "success", "message" => "Data jurnal lunas berhasil ditambahkan"]);
    } catch (Exception $e) {
        mysqli_rollback($conn);
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
}
?>
