<?php
$host = "localhost";
$username = "root";
$password = "";
$database = "siakuntansi";

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Koneksi database gagal: " . $conn->connect_error]));
}
?>
