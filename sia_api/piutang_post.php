<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $input = json_decode(file_get_contents("php://input"), true);

    // Validasi input
    $id_penjualan = $input['id_penjualan'] ?? null;
    $id_customer = $input['id_customer'] ?? null;
    $jumlah_piutang = $input['jumlah_piutang'] ?? null;
    $sisa_piutang = $input['sisa_piutang'] ?? null;
    $tanggal_jatuhtempo = $input['tanggal_jatuhtempo'] ?? null;
    $status_piutang = $input['status_piutang'] ?? null;

    if (!$id_penjualan || !$id_customer || !$jumlah_piutang || !$sisa_piutang || !$tanggal_jatuhtempo || !$status_piutang) {
        http_response_code(400);
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap."]);
        exit;
    }

    // Validasi apakah id_penjualan dan id_customer ada di database
    $queryCheckPenjualan = "SELECT * FROM transaksi_penjualan WHERE id_penjualan = ?";
    $stmtPenjualan = $conn->prepare($queryCheckPenjualan);
    $stmtPenjualan->bind_param("i", $id_penjualan);
    $stmtPenjualan->execute();
    $resultPenjualan = $stmtPenjualan->get_result();

    if ($resultPenjualan->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "ID penjualan tidak ditemukan"]);
        exit();
    }

    $queryCheckCustomer = "SELECT * FROM customer WHERE id_customer = ?";
    $stmtCustomer = $conn->prepare($queryCheckCustomer);
    $stmtCustomer->bind_param("i", $id_customer);
    $stmtCustomer->execute();
    $resultCustomer = $stmtCustomer->get_result();

    if ($resultCustomer->num_rows === 0) {
        echo json_encode(["status" => "error", "message" => "ID customer tidak ditemukan"]);
        exit();
    }

    // Simpan data ke tabel piutang
    $stmt = $conn->prepare("INSERT INTO piutang 
        (id_penjualan, id_customer, jumlah_piutang, sisa_piutang, tanggal_jatuhtempo, status_piutang) 
        VALUES (?, ?, ?, ?, ?, ?)");

    $stmt->bind_param("iiddss", $id_penjualan, $id_customer, $jumlah_piutang, $sisa_piutang, $tanggal_jatuhtempo, $status_piutang);

    if ($stmt->execute()) {
        $id_piutang = $conn->insert_id; // Ambil ID piutang yang baru saja dibuat
        echo json_encode([
            "status" => "success",
            "message" => "Data piutang berhasil ditambahkan",
            "id_piutang" => $id_piutang
        ]);
    } else {
        http_response_code(500);
        echo json_encode(["status" => "error", "message" => $stmt->error]);
    }

    $stmt->close();
} elseif ($method == 'GET') {
    $query = "SELECT * FROM piutang";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }

    echo json_encode(empty($data)
        ? ["status" => "error", "message" => "Data tidak ditemukan"]
        : ["status" => "success", "data" => $data]);
}
?>
