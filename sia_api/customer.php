<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, PUT, DELETE");

include 'db_connect.php';

// CREATE: Tambah data customer
$method = $_SERVER['REQUEST_METHOD'];

if ($method == 'POST') {
    $nama_customer = isset($_POST['nama_customer']) ? $_POST['nama_customer'] : '';
    $alamat = isset($_POST['alamat_customer']) ? $_POST['alamat_customer'] : '';
    $no_telepon = isset($_POST['no_telp']) ? $_POST['no_telp'] : '';
    $email = isset($_POST['email_customer']) ? $_POST['email_customer'] : '';
    $batas_kredit = isset($_POST['batas_kredit']) ? $_POST['batas_kredit'] : '';

    $result = mysqli_query($conn, "SELECT MAX(id_customer) AS id_terakhir FROM customer");
    $row = mysqli_fetch_assoc($result);
    $id_baru = $row['id_terakhir'] + 1;

    // Validasi input tidak boleh kosong
    if (empty($nama_customer) || empty($alamat) || empty($no_telepon) || empty($email) || empty($batas_kredit)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    // Validasi nomor telepon hanya angka
    if (!preg_match('/^[0-9]+$/', $no_telepon)) {
        echo json_encode(["status" => "error", "message" => "Nomor telepon hanya boleh berisi angka"]);
        exit();
    }

    // Validasi email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(["status" => "error", "message" => "Format email tidak valid"]);
        exit();
    }

    // Query untuk menambahkan data customer
    $query = "INSERT INTO customer (id_customer, nama_customer, alamat_customer, no_telp, email_customer, batas_kredit) 
          VALUES ('$id_baru', '$nama_customer', '$alamat', '$no_telepon', '$email', '$batas_kredit')";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data customer berhasil ditambahkan"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}

// READ: Ambil semua data customer
if ($method == 'GET') {
    $query = "SELECT * FROM customer";
    $result = mysqli_query($conn, $query);
    $data = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $data[] = $row;
    }
    echo json_encode($data);
}

if ($method == 'PUT') {
    parse_str(file_get_contents("php://input"), $_PUT);
    $id_customer = isset($_PUT['id_customer']) ? $_PUT['id_customer'] : '';
    $nama_customer = isset($_PUT['nama_customer']) ? $_PUT['nama_customer'] : '';
    $alamat = isset($_PUT['alamat_customer']) ? $_PUT['alamat_customer'] : '';
    $no_telepon = isset($_PUT['no_telp']) ? $_PUT['no_telp'] : '';
    $email = isset($_PUT['email_customer']) ? $_PUT['email_customer'] : '';
    $batas_kredit = isset($_PUT['batas_kredit']) ? $_PUT['batas_kredit'] : null;

    // Validasi input
    if (empty($id_customer) || empty($nama_customer) || empty($alamat) || empty($no_telepon) || empty($email) || empty($batas_kredit)) {
        echo json_encode(["status" => "error", "message" => "Data tidak lengkap"]);
        exit();
    }

    if (!preg_match('/^[0-9]+$/', $no_telepon)) {
        echo json_encode(["status" => "error", "message" => "Nomor telepon hanya boleh berisi angka"]);
        exit();
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(["status" => "error", "message" => "Format email tidak valid"]);
        exit();
    }

    if (is_null($batas_kredit)) {
        $result = mysqli_query($conn, "SELECT batas_kredit FROM customer WHERE id_customer='$id_customer'");
        if ($result && mysqli_num_rows($result) > 0) {
            $row = mysqli_fetch_assoc($result);
            $batas_kredit = $row['batas_kredit'];
        } else {
            echo json_encode(["status" => "error", "message" => "Customer tidak ditemukan"]);
            exit();
        }
    }

    // Query untuk update data customer
    $query = "UPDATE customer SET 
              nama_customer='$nama_customer', 
              alamat_customer='$alamat', 
              no_telp='$no_telepon', 
              email_customer='$email', 
              batas_kredit='$batas_kredit' 
              WHERE id_customer='$id_customer'";

    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data customer berhasil diperbarui"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}

if ($method == 'DELETE') {
    $kode = isset($_GET['id_customer']) ? $_GET['id_customer'] : '';

    if (empty($kode)) {
        echo json_encode(["status" => "error", "message" => "ID barang tidak diberikan"]);
        exit();
    }

    $query = "DELETE FROM customer WHERE id_customer='$kode'";
    if (mysqli_query($conn, $query)) {
        echo json_encode(["status" => "success", "message" => "Data barang berhasil dihapus"]);
    } else {
        echo json_encode(["status" => "error", "message" => mysqli_error($conn)]);
    }
}
// // UPDATE: Ubah data customer
// if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['update'])) {
//     $id_customer = $_POST['id_customer'];
//     $nama_customer = $_POST['nama_customer'];
//     $alamat = $_POST['alamat_customer'];
//     $no_telepon = $_POST['no_telp'];
//     $email = $_POST['email_customer'];
//     $batas_kredit = $_POST['batas_kredit'];

//     $query = "UPDATE customer SET nama_customer='$nama_customer', alamat_customer='$alamat', no_telp='$no_telepon', email_customer='$email', batas_kredit='$batas_kredit' WHERE id_customer=$id_customer";
//     if ($conn->query($query)) {
//         echo json_encode(["status" => "success", "message" => "Data customer berhasil diubah"]);
//     } else {
//         echo json_encode(["status" => "error", "message" => $conn->error]);
//     }
// }

?>
