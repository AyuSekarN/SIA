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

public class EditAkunActivity extends AppCompatActivity {

    private EditText etNamaAkun, etSaldoAkun, etKodeAkun;
    private Button btnUpdate;
    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_akun);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "akun_postdata.php";
        Log.d("INIT_URL", "URL: " + URL);

        // Inisialisasi komponen UI
        etKodeAkun = findViewById(R.id.etKodeAkun);
        etNamaAkun = findViewById(R.id.etNamaAkun);
        etSaldoAkun = findViewById(R.id.etSaldoAkun);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Ambil data dari Intent
        String kodeAkun = getIntent().getStringExtra("kode_akun");
        String namaAkun = getIntent().getStringExtra("nama_akun");
        String saldoAkun = getIntent().getStringExtra("saldo_akun");

        Log.d("INTENT_DATA", "KODE Akun: " + kodeAkun + ", Nama: " + namaAkun + ", Saldo: " + saldoAkun);

        // Tampilkan data di EditText
        etKodeAkun.setText(kodeAkun);
        etNamaAkun.setText(namaAkun);
        etSaldoAkun.setText(saldoAkun);

        // Atur aksi pada tombol Update
        btnUpdate.setOnClickListener(this::updateData);
    }

    private void updateData(View view) {
        // Ambil data dari EditText yang benar
        String kodeAkun = etKodeAkun.getText().toString().trim();  // Perbaikan di sini
        String namaAkun = etNamaAkun.getText().toString().trim();
        String saldoAkun = etSaldoAkun.getText().toString().trim();

        // Validasi input
        if (namaAkun.isEmpty()) {
            etNamaAkun.setError("Nama akun tidak boleh kosong!");
            etNamaAkun.requestFocus();
            Log.d("VALIDATION", "Nama akun kosong");
            return;
        }

        Log.d("UPDATE_DATA", "ID Akun: " + kodeAkun + ", Nama: " + namaAkun + ", Saldo: " + saldoAkun);

        // Kirim data ke server menggunakan PUT request
        StringRequest updateRequest = new StringRequest(Request.Method.PUT, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(EditAkunActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            finish(); // Kembali ke Activity sebelumnya
                        } else {
                            Toast.makeText(EditAkunActivity.this, "Gagal memperbarui data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(EditAkunActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error updating data", error);
                    Toast.makeText(EditAkunActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Kirim data melalui parameter PUT (POST body)
                Map<String, String> params = new HashMap<>();
                params.put("kode_akun", kodeAkun);  // Pastikan kode akun yang benar
                params.put("nama_akun", namaAkun);
                params.put("saldo_akun", saldoAkun);  // meskipun saldo tidak perlu diubah, tetap bisa disertakan
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