<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];
if ($method == 'GET') {
    $query = "SELECT * FROM piutang";
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
    $id_piutang = isset($_PUT['id_piutang']) ? $_PUT['id_piutang'] : '';
    $jumlah_piutang = isset($_PUT['jumlah_piutang']) ? $_PUT['jumlah_piutang'] : '';
    $sisa_piutang = isset($_PUT['sisa_piutang']) ? $_PUT['sisa_piutang'] : '';
    $tanggal_jatuhtempo = isset($_PUT['tanggal_jatuhtempo']) ? $_PUT['tanggal_jatuhtempo'] : '';
    $status_piutang = isset($_PUT['status_piutang']) ? $_PUT['status_piutang'] : '';

    if (empty($id_piutang) || empty($jumlah_piutang) || empty($tanggal_jatuhtempo) || empty($status_piutang)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    // Melakukan sanitasi input
    $id_piutang = mysqli_real_escape_string($conn, $id_piutang);
    $jumlah_piutang = mysqli_real_escape_string($conn, $jumlah_piutang);
    $sisa_piutang = mysqli_real_escape_string($conn, $sisa_piutang);
    $tanggal_jatuhtempo = mysqli_real_escape_string($conn, $tanggal_jatuhtempo);
    $status_piutang = mysqli_real_escape_string($conn, $status_piutang);

    // Query Update
    $query = "UPDATE piutang SET jumlah_piutang='$jumlah_piutang', sisa_piutang='$sisa_piutang', tanggal_jatuhtempo='$tanggal_jatuhtempo', status_piutang='$status_piutang' WHERE id_piutang='$id_piutang'";

    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data piutang berhasil diperbarui"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}
?>


