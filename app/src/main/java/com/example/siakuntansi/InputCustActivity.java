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

public class InputCustActivity extends AppCompatActivity {

    EditText etNamaCust, etAlamat, etNoTelp, etEmail, etBatasKredit;
    Button btnSimpanCust;
    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_cust);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "customer.php";
        Log.d("INIT_URL", "URL: " + URL);

        etNamaCust = findViewById(R.id.namaCustInput);
        etAlamat = findViewById(R.id.alamatInput);
        etNoTelp = findViewById(R.id.noteleponInput);
        etEmail = findViewById(R.id.emailInput);
        etBatasKredit = findViewById(R.id.bataskreditInput);
        btnSimpanCust = findViewById(R.id.submitButton);

        btnSimpanCust.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        // Validasi input
        if (!validateInputs()) {
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(InputCustActivity.this, "Data Cust Tersimpan!", Toast.LENGTH_SHORT).show();

                            // Kirim sinyal ke aktivitas sebelumnya
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isDataAdded", true); // Menandakan data baru ditambahkan
                            setResult(RESULT_OK, resultIntent);

                            finish(); // Kembali ke aktivitas sebelumnya
                        } else {
                            Toast.makeText(InputCustActivity.this, "Gagal menyimpan data: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(InputCustActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error saving data", error);
                    Toast.makeText(InputCustActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama_customer", etNamaCust.getText().toString().trim());
                params.put("alamat_customer", etAlamat.getText().toString().trim());
                params.put("no_telp", etNoTelp.getText().toString().trim());
                params.put("email_customer", etEmail.getText().toString().trim());
                params.put("batas_kredit", etBatasKredit.getText().toString().trim());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private boolean validateInputs() {
        // Validasi untuk nama
        if (etNamaCust.getText().toString().trim().isEmpty()) {
            etNamaCust.setError("Nama customer tidak boleh kosong");
            return false;
        }

        // Validasi untuk alamat
        if (etAlamat.getText().toString().trim().isEmpty()) {
            etAlamat.setError("Alamat tidak boleh kosong");
            return false;
        }

        // Validasi untuk nomor telepon
        String noTelp = etNoTelp.getText().toString().trim();
        if (noTelp.isEmpty() || !noTelp.matches("\\d+")) {
            etNoTelp.setError("Nomor telepon harus berupa angka");
            return false;
        }

        // Validasi untuk email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email tidak valid");
            return false;
        }

        // Validasi untuk batas kredit
        String batasKredit = etBatasKredit.getText().toString().trim();
        if (batasKredit.isEmpty() || !batasKredit.matches("\\d+")) {
            etBatasKredit.setError("Batas kredit harus berupa angka");
            return false;
        }

        return true;
    }
}
