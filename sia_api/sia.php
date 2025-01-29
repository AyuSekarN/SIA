<?php
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    header("Access-Control-Allow-Origin: *");
    header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
    header("Access-Control-Allow-Headers: Content-Type");
    exit(); // Menghentikan eksekusi lebih lanjut pada request OPTIONS
}

$conn = new mysqli("localhost", "root", "", "sia");

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'];
    $password = md5($_POST['password']);

    $query = "SELECT * FROM users WHERE username='$username' AND password='$password'";
    $result = $conn->query($query);

    if ($result->num_rows > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "Login successful!"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Invalid credentials."
        ]);
    }
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Invalid request."
    ]);
}
?>
