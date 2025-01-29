<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

function writeLog($message) {
    $logFile = "logs/debug.log";
    file_put_contents($logFile, "[" . date("Y-m-d H:i:s") . "] " . $message . PHP_EOL, FILE_APPEND);
}

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'GET') {
    $nama_barang = isset($_GET['nama_barang']) ? trim($_GET['nama_barang']) : '';

    if (empty($nama_barang)) {
        writeLog("Nama barang tidak diberikan: " . json_encode($_GET));
        echo json_encode(["status" => "error", "message" => "Nama barang tidak diberikan"]);
        exit();
    }

    writeLog("Mencari barang dengan nama: " . $nama_barang);

    $query = "SELECT id_barang FROM barang WHERE nama_barang = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $nama_barang);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result && $result->num_rows > 0) {
        if ($result->num_rows == 1) {
            $row = $result->fetch_assoc();
            writeLog("Barang ditemukan: " . json_encode($row));
            echo json_encode(["status" => "success", "id_barang" => $row['id_barang']]);
        } else {
            writeLog("Ditemukan lebih dari satu barang untuk nama: " . $nama_barang);
            echo json_encode(["status" => "error", "message" => "Ditemukan lebih dari satu barang dengan nama tersebut. Harap gunakan nama unik."]);
        }
    } else {
        writeLog("Barang tidak ditemukan untuk nama: " . $nama_barang);
        echo json_encode(["status" => "error", "message" => "Barang tidak ditemukan"]);
    }

    $stmt->close();
}
?>
