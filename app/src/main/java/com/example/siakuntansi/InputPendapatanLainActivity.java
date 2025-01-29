package com.example.siakuntansi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class InputPendapatanLainActivity extends AppCompatActivity {

    private TextView tvKodeAkun, tvIDPendapatan;
    private EditText etNamaPendapatan, etJumlahPendapatan;
    private Button btnSimpan;
    private RequestQueue requestQueue;

    private String urlKodeAkun;
    private String urlIDPendapatanLain;
    private String urlPendapatanLain;
    private String urlJurnalUmum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_input_pendapatan_lain);

        tvKodeAkun = findViewById(R.id.tvKodeAkun);
        TextView tvTanggalTransaksi = findViewById(R.id.tvTanggalTransaksi);
        tvIDPendapatan = findViewById(R.id.tvIDPendapatan);
        etNamaPendapatan = findViewById(R.id.etNamaPendapatan);
        etJumlahPendapatan = findViewById(R.id.etJumlahPendapatan);
        btnSimpan = findViewById(R.id.btnSimpan);

        urlKodeAkun = getString(R.string.base_url) + "akun_getdata.php?get_kode_akun2=402";
        urlIDPendapatanLain = getString(R.string.base_url) + "transaksi_pendapatan_lain_getdata.php?get_id_pendapatan_lain=true";
        urlPendapatanLain = getString(R.string.base_url) + "transaksi_pendapatan_lain_post.php";
        urlJurnalUmum = getString(R.string.base_url) + "jurnal_umum.php";

        requestQueue = Volley.newRequestQueue(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        tvTanggalTransaksi.setText(currentDate);

        fetchKodeAkun(urlKodeAkun);
        fetchIDPendapatanLain(urlIDPendapatanLain);

        btnSimpan.setOnClickListener(v -> {
            String kodeAkun = tvKodeAkun.getText().toString();
            String tanggalTransaksi = tvTanggalTransaksi.getText().toString();
            String namaPendapatan = etNamaPendapatan.getText().toString();
            String jumlahPendapatan = etJumlahPendapatan.getText().toString();

            if (namaPendapatan.isEmpty() || jumlahPendapatan.isEmpty()) {
                Toast.makeText(this, "Data tidak lengkap.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parameter untuk API
            HashMap<String, String> params = new HashMap<>();
            params.put("kode_akun", kodeAkun);
            params.put("tanggal", tanggalTransaksi);
            params.put("jumlah", jumlahPendapatan);
            params.put("deskripsi", namaPendapatan);

            sendDataToPendapatanLain(params);
        });
    }

    private void fetchKodeAkun(String urlKodeAkun) {
        StringRequest kodeAkunRequest = new StringRequest(Request.Method.GET, urlKodeAkun,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray akunArray = jsonResponse.getJSONArray("data");
                            if (akunArray.length() > 0) {
                                JSONObject akunObject = akunArray.getJSONObject(0);
                                String kodeAkun = akunObject.optString("kode_akun", "");
                                tvKodeAkun.setText(kodeAkun);
                            } else {
                                tvKodeAkun.setText("402");
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Terjadi kesalahan");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing Kode Akun", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching Kode Akun", Toast.LENGTH_SHORT).show());

        requestQueue.add(kodeAkunRequest);
    }

    private void fetchIDPendapatanLain(String urlIDPendapatanLain) {
        StringRequest idPendapatanRequest = new StringRequest(Request.Method.GET, urlIDPendapatanLain,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            String nextID = jsonResponse.optString("next_id_pendapatan_lain", "1");
                            tvIDPendapatan.setText(nextID);
                        } else {
                            String message = jsonResponse.optString("message", "Terjadi kesalahan");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing ID Pendapatan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching ID Pendapatan", Toast.LENGTH_SHORT).show());
        requestQueue.add(idPendapatanRequest);
    }

    private void sendDataToPendapatanLain(HashMap<String, String> params) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, urlPendapatanLain, new JSONObject(params),
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Data berhasil disimpan di transaksi pendapatan lain-lain!", Toast.LENGTH_SHORT).show();
                            sendDataToJurnalUmum(params);
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(request);
    }

    private void sendDataToJurnalUmum(HashMap<String, String> params) {
        JsonObjectRequest requestJurnal = new JsonObjectRequest(Request.Method.POST, urlJurnalUmum, new JSONObject(params),
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Data berhasil disimpan di jurnal umum!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        requestQueue.add(requestJurnal);
    }
}