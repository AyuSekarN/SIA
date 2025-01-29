package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InputAkunActivity extends AppCompatActivity {

    private EditText etKodeAkun, etNamaAkun;  // Tidak ada EditText untuk saldo_akun karena tidak perlu diinput
    private Button btnSimpanAkun;
    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_akun);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "akun_postdata.php";
        Log.d("INIT_URL", "URL: " + URL);

        // Inisialisasi komponen UI
        etKodeAkun = findViewById(R.id.kodeAkunInput);
        etNamaAkun = findViewById(R.id.namaAkunInput);
        btnSimpanAkun = findViewById(R.id.submitButton);

        // Atur aksi pada tombol Simpan
        btnSimpanAkun.setOnClickListener(v -> {
            Log.d("BTN_SIMPAN", "Tombol simpan ditekan");
            saveData();
        });
    }

    private void saveData() {
        String kodeAkun = etKodeAkun.getText().toString().trim();
        String namaAkun = etNamaAkun.getText().toString().trim();

        // Validasi input
        if (kodeAkun.isEmpty()) {
            etKodeAkun.setError("Kode akun tidak boleh kosong!");
            etKodeAkun.requestFocus();
            Log.d("VALIDATION", "Kode akun kosong");
            return;
        }

        if (namaAkun.isEmpty()) {
            etNamaAkun.setError("Nama akun tidak boleh kosong!");
            etNamaAkun.requestFocus();
            Log.d("VALIDATION", "Nama akun kosong");
            return;
        }

        Log.d("SAVE_DATA", "Kode Akun: " + kodeAkun + ", Nama Akun: " + namaAkun);

        // Kirim request POST tanpa saldo_akun
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(InputAkunActivity.this, "Data Akun Tersimpan!", Toast.LENGTH_SHORT).show();

                            // Kirim sinyal ke aktivitas sebelumnya
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isDataAdded", true); // Menandakan data baru ditambahkan
                            setResult(RESULT_OK, resultIntent);

                            finish(); // Kembali ke aktivitas sebelumnya
                        } else {
                            Toast.makeText(InputAkunActivity.this, "Gagal menyimpan data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(InputAkunActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error posting data", error);
                    Toast.makeText(InputAkunActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("kode_akun", kodeAkun);
                params.put("nama_akun", namaAkun);
                Log.d("POST_PARAMS", "Params: " + params.toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}