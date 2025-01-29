<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

include 'db_connect.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'GET' && isset($_GET['id_penjualan'])) {
    $id_penjualan = $_GET['id_penjualan'];

    // Validasi id_penjualan
    if (!is_numeric($id_penjualan)) {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid id_penjualan"
        ]);
        exit;
    }

    $query = "SELECT * FROM piutang WHERE id_penjualan = ?";
    $stmt = $conn->prepare($query);
    
    if ($stmt === false) {
        echo json_encode([
            "status" => "error",
            "message" => "Failed to prepare query"
        ]);
        exit;
    }

    $stmt->bind_param("i", $id_penjualan);

    if ($stmt->execute()) {
        $result = $stmt->get_result();
        $data = [];
        while ($row = $result->fetch_assoc()) {
            $data[] = $row;
        }

        if (!empty($data)) {
            echo json_encode([
                "status" => "success",
                "data" => $data
            ]);
        } else {
            echo json_encode([
                "status" => "error",
                "message" => "Data tidak ditemukan"
            ]);
        }
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Query execution failed: " . $stmt->error
        ]);
    }
    $stmt->close();
    exit;
}

if ($method == 'GET') {
    $query = "SELECT * FROM piutang";
    $result = mysqli_query($conn, $query);

    if ($result) {
        $data = [];
        while ($row = mysqli_fetch_assoc($result)) {
            $data[] = $row;
        }
        echo json_encode($data);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Failed to retrieve data: " . mysqli_error($conn)
        ]);
    }
}
?>
