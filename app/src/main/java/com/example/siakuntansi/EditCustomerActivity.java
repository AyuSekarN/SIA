package com.example.siakuntansi;

import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class EditCustomerActivity extends AppCompatActivity {

    private EditText etNamaCustomer, etAlamatCustomer, etNoTelp, etEmailCustomer, etBatasKredit;
    private Button btnUpdate;
    private String idCustomer;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        URL = getString(R.string.base_url) + "customer.php";
        Log.d("INIT_URL", "URL: " + URL);

        etNamaCustomer = findViewById(R.id.etNamaCustomer);
        etAlamatCustomer = findViewById(R.id.etAlamatCustomer);
        etNoTelp = findViewById(R.id.etNoTelp);
        etEmailCustomer = findViewById(R.id.etEmailCustomer);
        etBatasKredit = findViewById(R.id.etBatasKredit);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Ambil data dari Intent
        idCustomer = getIntent().getStringExtra("id_customer");
        String namaCustomer = getIntent().getStringExtra("nama_customer");
        String alamatCustomer = getIntent().getStringExtra("alamat_customer");
        String noTelp = getIntent().getStringExtra("no_telp");
        String emailCustomer = getIntent().getStringExtra("email_customer");
        String batasKredit = getIntent().getStringExtra("batas_kredit");

        etNamaCustomer.setText(namaCustomer);
        etAlamatCustomer.setText(alamatCustomer);
        etNoTelp.setText(noTelp);
        etEmailCustomer.setText(emailCustomer);
        etBatasKredit.setText(batasKredit);

        btnUpdate.setOnClickListener(this::updateData);
    }

    private void updateData(View view) {
        String namaCustomer = etNamaCustomer.getText().toString().trim();
        String alamatCustomer = etAlamatCustomer.getText().toString().trim();
        String noTelp = etNoTelp.getText().toString().trim();
        String emailCustomer = etEmailCustomer.getText().toString().trim();
        String batasKredit = etBatasKredit.getText().toString().trim();

        if (namaCustomer.isEmpty() || alamatCustomer.isEmpty() || noTelp.isEmpty() || emailCustomer.isEmpty() || batasKredit.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!noTelp.matches("\\d+")) {
            Toast.makeText(this, "Nomor telepon harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailCustomer).matches()) {
            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest updateRequest = new StringRequest(Request.Method.PUT, URL,
                response -> {
                    Toast.makeText(EditCustomerActivity.this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("isDataAdded", true); // Menandakan data baru ditambahkan
                    setResult(RESULT_OK, resultIntent);

                    finish();
                },
                error -> Toast.makeText(EditCustomerActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_customer", idCustomer);
                params.put("nama_customer", namaCustomer);
                params.put("alamat_customer", alamatCustomer);
                params.put("no_telp", noTelp);
                params.put("email_customer", emailCustomer);
                params.put("batas_kredit", batasKredit);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updateRequest);
    }
}