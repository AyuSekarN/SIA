<?php

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($_SERVER['REQUEST_METHOD'] == 'GET' && isset($_GET['get_kode_akun'])) {
    // Mendapatkan data dengan kode_akun = 401 dari transaksi_penjualan
    $query = "SELECT * FROM akun WHERE kode_akun = '401'";
    
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        // Ambil data dari query
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row; // Menambahkan setiap row hasil query ke array
        }
        // Kirimkan data sebagai respons JSON
        echo json_encode([
            "status" => "success",
            "data" => $data
        ]);
    } else {
        // Jika tidak ada data yang ditemukan
        echo json_encode([
            "status" => "error",
            "message" => "No records found for kode_akun 401"
        ]);
    }
}

if ($_SERVER['REQUEST_METHOD'] == 'GET' && isset($_GET['get_kode_akun2'])) {
    $query = "SELECT * FROM akun WHERE kode_akun = '402'";
    
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        // Ambil data dari query
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row; // Menambahkan setiap row hasil query ke array
        }
        // Kirimkan data sebagai respons JSON
        echo json_encode([
            "status" => "success",
            "data" => $data
        ]);
    } else {
        // Jika tidak ada data yang ditemukan
        echo json_encode([
            "status" => "error",
            "message" => "No records found for kode_akun 402"
        ]);
    }
}

if ($_SERVER['REQUEST_METHOD'] == 'GET' && isset($_GET['get_kode_akun3'])) {
    $query = "SELECT * FROM akun WHERE kode_akun = '102'";
    
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        // Ambil data dari query
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row; // Menambahkan setiap row hasil query ke array
        }
        // Kirimkan data sebagai respons JSON
        echo json_encode([
            "status" => "success",
            "data" => $data
        ]);
    } else {
        // Jika tidak ada data yang ditemukan
        echo json_encode([
            "status" => "error",
            "message" => "No records found for kode_akun 102"
        ]);
    }
}
?>