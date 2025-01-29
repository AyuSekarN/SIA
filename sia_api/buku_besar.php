<?php
// Header untuk mengizinkan akses API
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

// Termasuk file koneksi database
include 'db_connect.php';

// Fungsi untuk mencatat log ke file
function writeLog($message) {
    $logFile = __DIR__ . '/error_log_buku_besar.log';
    $timestamp = date("Y-m-d H:i:s");
    file_put_contents($logFile, "[$timestamp] $message" . PHP_EOL, FILE_APPEND);
}

// Periksa metode HTTP
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $kodeAkun = isset($_GET['kode_akun']) ? $_GET['kode_akun'] : null;

    if (empty($kodeAkun)) {
        http_response_code(400); // Bad Request
        echo json_encode(["status" => "error", "message" => "Parameter 'kode_akun' is required."]);
        exit;
    }

    try {
        // Query untuk mendapatkan data dari jurnal_umum
        $query = "
            SELECT 
                tanggal, kode_akun, akun, debet, kredit 
            FROM 
                jurnal_umum 
            WHERE 
                kode_akun = ? 
            ORDER BY 
                tanggal ASC
        ";
        $stmt = mysqli_prepare($conn, $query);
        if (!$stmt) {
            throw new Exception("Failed to prepare statement: " . mysqli_error($conn));
        }

        mysqli_stmt_bind_param($stmt, "i", $kodeAkun);
        if (!mysqli_stmt_execute($stmt)) {
            throw new Exception("Failed to execute statement: " . mysqli_error($conn));
        }

        $result = mysqli_stmt_get_result($stmt);
        $data = [];
        $saldo = 0;

        while ($row = mysqli_fetch_assoc($result)) {
            $saldo += $row['debet'] - $row['kredit'];

            $data[] = [
                "tanggal" => $row['tanggal'],
                "kode_akun" => $row['kode_akun'],
                "akun" => $row['akun'],
                "debet" => 'Rp' . number_format((float)$row['debet'], 0, ',', '.'),
                "kredit" => 'Rp' . number_format((float)$row['kredit'], 0, ',', '.'),
                "saldo" => 'Rp' . number_format((float)$saldo, 0, ',', '.')
            ];
        }

        if (empty($data)) {
            echo json_encode(["status" => "empty", "message" => "No data found."]);
        } else {
            echo json_encode($data, JSON_PRETTY_PRINT);
        }
    } catch (Exception $e) {
        http_response_code(500); // Internal Server Error
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }
} else {
    http_response_code(405); // Method Not Allowed
    echo json_encode(["status" => "error", "message" => "Method not allowed."]);
}

?>
