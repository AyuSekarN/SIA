package com.example.siakuntansi;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditBarangActivity extends AppCompatActivity {

    private EditText etNamaBarang, etHargaBarang;
    private Button btnUpdate;
    private String idBarang;
    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_barang);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "barang.php";
        Log.d("INIT_URL", "URL: " + URL);

        // Inisialisasi komponen UI
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etHargaBarang = findViewById(R.id.etHargaBarang);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Ambil data dari Intent
        idBarang = getIntent().getStringExtra("id_barang");
        String namaBarang = getIntent().getStringExtra("nama_barang");
        String hargaBarang = getIntent().getStringExtra("harga_barang");

        Log.d("INTENT_DATA", "ID Barang: " + idBarang + ", Nama: " + namaBarang + ", Harga: " + hargaBarang);

        // Tampilkan data di EditText
        etNamaBarang.setText(namaBarang);
        etHargaBarang.setText(hargaBarang);

        // Atur aksi pada tombol Update
        btnUpdate.setOnClickListener(this::updateData);
    }

    private void updateData(View view) {
        String namaBarang = etNamaBarang.getText().toString().trim();
        String hargaBarang = etHargaBarang.getText().toString().trim();

        // Validasi input
        if (namaBarang.isEmpty()) {
            etNamaBarang.setError("Nama barang tidak boleh kosong!");
            etNamaBarang.requestFocus();
            Log.d("VALIDATION", "Nama barang kosong");
            return;
        }

        if (hargaBarang.isEmpty()) {
            etHargaBarang.setError("Harga barang tidak boleh kosong!");
            etHargaBarang.requestFocus();
            Log.d("VALIDATION", "Harga barang kosong");
            return;
        }

        if (!hargaBarang.matches("\\d+")) {
            etHargaBarang.setError("Harga barang harus berupa angka!");
            etHargaBarang.requestFocus();
            Log.d("VALIDATION", "Harga barang bukan angka");
            return;
        }

        Log.d("UPDATE_DATA", "ID Barang: " + idBarang + ", Nama: " + namaBarang + ", Harga: " + hargaBarang);

        // Kirim data ke server menggunakan PUT request
        StringRequest updateRequest = new StringRequest(Request.Method.PUT, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(EditBarangActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            finish(); // Kembali ke Activity sebelumnya
                        } else {
                            Toast.makeText(EditBarangActivity.this, "Gagal memperbarui data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(EditBarangActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error updating data", error);
                    Toast.makeText(EditBarangActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Kirim data melalui parameter POST
                Map<String, String> params = new HashMap<>();
                params.put("id_barang", idBarang);
                params.put("nama_barang", namaBarang);
                params.put("harga_barang", hargaBarang);
                Log.d("POST_PARAMS", "Params: " + params.toString());
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        // Tambahkan request ke antrian Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updateRequest);
    }
}

