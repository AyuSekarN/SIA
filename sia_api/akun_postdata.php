<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $kode_akun = isset($_POST['kode_akun']) ? $_POST['kode_akun'] : '';
    $nama_akun = isset($_POST['nama_akun']) ? $_POST['nama_akun'] : '';
    $saldo_akun = isset($_POST['saldo_akun']) ? $_POST['saldo_akun'] : '';

    if (empty($kode_akun) || empty($nama_akun)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    // Melakukan sanitasi input
    $kode_akun = mysqli_real_escape_string($conn, $kode_akun);
    $nama_akun = mysqli_real_escape_string($conn, $nama_akun);

    // Query Insert
    $query = "INSERT INTO akun (kode_akun, nama_akun) VALUES ('$kode_akun', '$nama_akun')";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data akun berhasil ditambahkan"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}
?>
