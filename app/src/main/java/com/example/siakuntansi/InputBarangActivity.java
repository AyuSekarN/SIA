package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class InputBarangActivity extends AppCompatActivity {

    EditText etNamaBarang, etHargaBarang;
    Button btnSimpanBarang;
    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_barang);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "barang.php";
        Log.d("INIT_URL", "URL: " + URL);

        etNamaBarang = findViewById(R.id.namaSepatuInput);
        etHargaBarang = findViewById(R.id.hargaSepatuInput);
        btnSimpanBarang = findViewById(R.id.submitButton);

        btnSimpanBarang.setOnClickListener(v -> {
            Log.d("BTN_SIMPAN", "Tombol simpan ditekan");
            saveData();
        });
    }

    private void saveData() {
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

        Log.d("SAVE_DATA", "Nama Barang: " + namaBarang + ", Harga Barang: " + hargaBarang);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(InputBarangActivity.this, "Data Barang Tersimpan!", Toast.LENGTH_SHORT).show();

                            // Kirim sinyal ke aktivitas sebelumnya
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isDataAdded", true); // Menandakan data baru ditambahkan
                            setResult(RESULT_OK, resultIntent);

                            finish(); // Kembali ke aktivitas sebelumnya
                        } else {
                            Toast.makeText(InputBarangActivity.this, "Gagal menyimpan data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(InputBarangActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error posting data", error);
                    Toast.makeText(InputBarangActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama_barang", namaBarang);
                params.put("harga_barang", hargaBarang);
                Log.d("POST_PARAMS", "Params: " + params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}


