<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $nama = $_POST['nama_barang'];
    $harga = $_POST['harga_barang'];

    // Hitung ID baru
    $result = mysqli_query($conn, "SELECT MAX(id_barang) AS id_terakhir FROM barang");
    $row = mysqli_fetch_assoc($result);
    $id_baru = $row['id_terakhir'] + 1;

    // Insert data baru
    $query = "INSERT INTO barang (id_barang, nama_barang, harga_barang) VALUES ('$id_baru', '$nama', '$harga')";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data barang berhasil ditambahkan"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}

if ($method == 'GET') {
    $query = "SELECT * FROM barang";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }
    echo json_encode($data);
}

if ($method == 'PUT') {
    parse_str(file_get_contents("php://input"), $_PUT);
    $kode = isset($_PUT['id_barang']) ? $_PUT['id_barang'] : '';
    $nama_barang = isset($_PUT['nama_barang']) ? $_PUT['nama_barang'] : '';
    $harga = isset($_PUT['harga_barang']) ? $_PUT['harga_barang'] : '';

    if (empty($kode) || empty($nama_barang) || empty($harga)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    $query = "UPDATE barang SET nama_barang='$nama_barang', harga_barang='$harga' WHERE id_barang='$kode'";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data barang berhasil diperbarui"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}

if ($method == 'DELETE') {
    $kode = isset($_GET['id_barang']) ? $_GET['id_barang'] : '';

    if (empty($kode)) {
        echo json_encode(["status" => "error", "message" => "ID barang tidak diberikan"]);
        exit();
    }

    $query = "DELETE FROM barang WHERE id_barang='$kode'";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data barang berhasil dihapus"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}
?>
