<?php
// Header untuk mengizinkan akses API
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

// Termasuk file koneksi database
include 'db_connect.php';

// Periksa metode HTTP
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    // Query untuk mengambil data dari tabel jurnal_umum
    $query = "SELECT tanggal, kode_akun, akun, debet, kredit, deskripsi FROM jurnal_umum ORDER BY tanggal DESC";
    $result = mysqli_query($conn, $query);

    // Jika query berhasil dijalankan
    if ($result) {
        $data = [];
        while ($row = mysqli_fetch_assoc($result)) {
            // Format tanggal ke yy/mm/dd
            $formattedDate = date('y/m/d', strtotime($row['tanggal']));

            // Format debet dan kredit ke format Rp.xxx.xxx
            $formattedDebet = 'Rp' . number_format((float)$row['debet'], 0, ',', '.');
            $formattedKredit = 'Rp' . number_format((float)$row['kredit'], 0, ',', '.');

            // Tambahkan data ke array
            $data[] = [
                "tanggal" => $formattedDate,
                "kode_akun" => $row['kode_akun'],
                "akun" => $row['akun'],
                "debet" => $formattedDebet,
                "kredit" => $formattedKredit,
                "deskripsi" => $row['deskripsi']
            ];
        }

        // Jika data ditemukan
        if (count($data) > 0) {
            echo json_encode($data, JSON_PRETTY_PRINT);
        } else {
            // Tidak ada data ditemukan
            echo json_encode([
                "status" => "empty",
                "message" => "No records found in the jurnal_umum table."
            ], JSON_PRETTY_PRINT);
        }
    } else {
        // Jika query gagal
        http_response_code(500); // Internal Server Error
        echo json_encode([
            "status" => "error",
            "message" => "Database query failed: " . mysqli_error($conn)
        ], JSON_PRETTY_PRINT);
    }
} else {
    // Metode selain GET tidak diizinkan
    http_response_code(405); // Method Not Allowed
    echo json_encode([
        "status" => "error",
        "message" => "Method not allowed. Please use GET."
    ], JSON_PRETTY_PRINT);
}

// Tutup koneksi database
mysqli_close($conn);
?>
