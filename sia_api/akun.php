<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];
if ($method == 'GET') {
    $query = "SELECT * FROM akun";
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

if ($method == 'PUT') {
    parse_str(file_get_contents("php://input"), $_PUT);
    $kode_akun = isset($_PUT['kode_akun']) ? $_PUT['kode_akun'] : '';
    $nama_akun = isset($_PUT['nama_akun']) ? $_PUT['nama_akun'] : '';
    $saldo_akun = isset($_PUT['saldo_akun']) ? $_PUT['saldo_akun'] : ''; // Bisa diabaikan karena tidak perlu diupdate

    if (empty($kode_akun) || empty($nama_akun)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    // Melakukan sanitasi input
    $kode_akun = mysqli_real_escape_string($conn, $kode_akun);
    $nama_akun = mysqli_real_escape_string($conn, $nama_akun);

    // Query Update
    $query = "UPDATE akun SET nama_akun='$nama_akun' WHERE kode_akun='$kode_akun'";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data akun berhasil diperbarui"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}

?>