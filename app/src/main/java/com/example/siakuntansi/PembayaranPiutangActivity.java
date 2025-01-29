package com.example.siakuntansi;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
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

public class PembayaranPiutangActivity extends AppCompatActivity {

    private TextView etKodeAkun, etIdBayarPiutang;
    private EditText etIdPenjualan, etTanggalPembayaran, etJumlahPiutang, etJumlahPembayaran, etCustomer;
    private Button btnSimpan, btnSearchIdPenjualan;
    private RequestQueue requestQueue;
    private String urlPiutangById;
    private static final String TAG = PembayaranPiutangActivity.class.getSimpleName();

    private String urlKodeAkun, urlIDPembayaranPiutang;
    private String urlJurnalUmum;

    private String buildUrl(String endpoint) {
        return getString(R.string.base_url) + endpoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_piutang);

        // Inisialisasi Views
        etKodeAkun = findViewById(R.id.etKodeAkun);
        etIdBayarPiutang = findViewById(R.id.etIdBayarPiutang);
        etIdPenjualan = findViewById(R.id.etIdPenjualan);
        etCustomer = findViewById(R.id.etCustomer);
        etTanggalPembayaran = findViewById(R.id.etTanggalPembayaran);
        etJumlahPiutang = findViewById(R.id.etJumlahPiutang);
        etJumlahPembayaran = findViewById(R.id.etJumlahPembayaran);
        btnSimpan = findViewById(R.id.btnSubmitPembayaran);
        btnSearchIdPenjualan = findViewById(R.id.btnSearchIdPenjualan);

        // URL API
        urlKodeAkun = getString(R.string.base_url) + "akun_getdata.php?get_kode_akun3=102";
        urlIDPembayaranPiutang = getString(R.string.base_url) + "pembayaran_piutang_getdata.php?get_id_pembayaranpiutang=true";
        urlPiutangById = buildUrl(getString(R.string.piutang_getid_endpoint));
        urlJurnalUmum = getString(R.string.base_url) + "jurnal_umum_bayarpiutang.php";

        requestQueue = Volley.newRequestQueue(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        etTanggalPembayaran.setText(currentDate);

        // Fetch data awal
        fetchKodeAkun(urlKodeAkun);
        fetchIDPembayaranPiutang(urlIDPembayaranPiutang);

        // Button Search ID Penjualan
        btnSearchIdPenjualan.setOnClickListener(v -> {
            String idPenjualan = etIdPenjualan.getText().toString();
            if (idPenjualan.isEmpty()) {
                Toast.makeText(this, "Masukkan ID Penjualan!", Toast.LENGTH_SHORT).show();
            } else {
                fetchPiutangById(idPenjualan);
            }
        });

        // Button Simpan
        // ... existing code ...

// Button Simpan
        btnSimpan.setOnClickListener(v -> {
            String kodeAkun = etKodeAkun.getText().toString();
            String idBayarPiutang = etIdBayarPiutang.getText().toString();
            String tanggalPembayaran = etTanggalPembayaran.getText().toString();
            String jumlahPiutang = etJumlahPiutang.getText().toString();
            String jumlahPembayaran = etJumlahPembayaran.getText().toString();

            // Validasi input
            if (kodeAkun.isEmpty() || idBayarPiutang.isEmpty() || tanggalPembayaran.isEmpty() ||
                    jumlahPiutang.isEmpty() || jumlahPembayaran.isEmpty()) {
                Toast.makeText(this, "Semua parameter wajib diisi.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Double.parseDouble(jumlahPembayaran) > Double.parseDouble(jumlahPiutang)) {
                Toast.makeText(this, "Jumlah pembayaran tidak boleh lebih besar dari jumlah piutang.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parameter untuk API
            HashMap<String, String> params = new HashMap<>();
            params.put("kode_akun", kodeAkun);
            params.put("id_pembayaranpiutang", idBayarPiutang);
            params.put("tanggal_pembayaran", tanggalPembayaran);
            params.put("jumlah_piutang", jumlahPiutang);
            params.put("jumlah_pembayaran", jumlahPembayaran);

            Log.d(TAG, "Parameters: " + params.toString());

            sendDataToApi(params);
        });

    }

    private void fetchKodeAkun(String urlKodeAkun) {
        StringRequest kodeAkunRequest = new StringRequest(Request.Method.GET, urlKodeAkun,
                response -> {
                    Log.d(TAG, "Kode Akun Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray dataArray = jsonResponse.getJSONArray("data");
                            if (dataArray.length() > 0) {
                                JSONObject dataObject = dataArray.getJSONObject(0);
                                String kodeAkun = dataObject.optString("kode_akun", "102");
                                etKodeAkun.setText(kodeAkun);
                            } else {
                                etKodeAkun.setText("102");
                            }
                        } else {
                            Toast.makeText(this, "Error fetching Kode Akun", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Parsing Error Kode Akun", e);
                        Toast.makeText(this, "Parsing Error Kode Akun", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Request Error Kode Akun", error);
                    Toast.makeText(this, "Request Error Kode Akun", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(kodeAkunRequest);
    }

    private void fetchIDPembayaranPiutang(String urlIDPembayaranPiutang) {
        StringRequest idPembayaranRequest = new StringRequest(Request.Method.GET, urlIDPembayaranPiutang,
                response -> {
                    Log.d(TAG, "ID Pembayaran Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            String nextId = jsonResponse.optString("next_id_pembayaranpiutang", "1");
                            etIdBayarPiutang.setText(nextId);
                        } else {
                            Toast.makeText(this, "Error fetching ID Pembayaran", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Parsing Error ID Pembayaran", e);
                        Toast.makeText(this, "Parsing Error ID Pembayaran", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Request Error ID Pembayaran", error);
                    Toast.makeText(this, "Request Error ID Pembayaran", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(idPembayaranRequest);
    }

    private void fetchPiutangById(String idPenjualan) {
        String fullUrl = urlPiutangById + "?id_penjualan=" + idPenjualan;
        Log.d(TAG, "Request URL: " + fullUrl);

        StringRequest piutangRequest = new StringRequest(Request.Method.GET, fullUrl,
                response -> {
                    Log.d(TAG, "Raw Response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray dataArray = jsonResponse.optJSONArray("data");
                            if (dataArray != null && dataArray.length() > 0) {
                                JSONObject dataObject = dataArray.getJSONObject(0);
                                String jumlahPiutang = dataObject.optString("jumlah_piutang", "0");
                                String namaCustomer = dataObject.optString("nama_customer", "Tidak Diketahui");

                                // Set nilai ke EditText atau TextView
                                etJumlahPiutang.setText(jumlahPiutang);
                                etCustomer.setText(namaCustomer);

                                // Simpan idPiutang untuk digunakan saat menyimpan data
                                etIdBayarPiutang.setTag(idPenjualan);

                                Toast.makeText(PembayaranPiutangActivity.this, "Data berhasil ditemukan!", Toast.LENGTH_SHORT).show();
                            } else {
                                etJumlahPiutang.setText("0");
                                etCustomer.setText("");
                                etIdBayarPiutang.setTag(null);
                                Toast.makeText(PembayaranPiutangActivity.this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String errorMessage = jsonResponse.optString("message", "Unknown error");
                            Toast.makeText(PembayaranPiutangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error", e);
                        Toast.makeText(PembayaranPiutangActivity.this, "Parsing Error Piutang", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Request Error Piutang", error);
                    Toast.makeText(PembayaranPiutangActivity.this, "Request Error Piutang", Toast.LENGTH_SHORT).show();
                });
        requestQueue.add(piutangRequest);
    }

    private void sendDataToApi(HashMap<String, String> params) {
        String urlPostApi = getString(R.string.base_url) + "pembayaran_piutang_post.php";

        String idPenjualan = (String) etIdBayarPiutang.getTag();
        if (idPenjualan != null && !idPenjualan.isEmpty()) {
            params.put("id_penjualan", idPenjualan);
        } else {
            Toast.makeText(this, "ID Piutang tidak ditemukan. Periksa kembali ID Penjualan.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "API Endpoint: " + urlPostApi);
        Log.d(TAG, "Request Parameters: " + params.toString());

        // Ambil sisa piutang sebelum mengirim data
        String fullUrl = urlPiutangById + "?id_penjualan=" + idPenjualan;
        StringRequest piutangRequest = new StringRequest(Request.Method.GET, fullUrl,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            JSONArray dataArray = jsonResponse.optJSONArray("data");
                            if (dataArray != null && dataArray.length() > 0) {
                                JSONObject dataObject = dataArray.getJSONObject(0);
                                double sisaPiutang = dataObject.optDouble("sisa_piutang", 0);
                                double jumlahPembayaran = Double.parseDouble(params.get("jumlah_pembayaran"));

                                // Hitung sisa piutang setelah pembayaran
                                double newSisaPiutang = sisaPiutang - jumlahPembayaran;
                                String newStatus = newSisaPiutang <= 0 ? "Lunas" : "Belum Lunas";

                                // Kirim data pembayaran
                                JSONObject jsonObject = new JSONObject(params);
                                jsonObject.put("new_sisa_piutang", newSisaPiutang);
                                jsonObject.put("new_status", newStatus);

                                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, urlPostApi, jsonObject,
                                        responsePost -> {
                                            try {
                                                Log.d(TAG, "API Response: " + responsePost.toString());
                                                if (responsePost.getString("status").equals("success")) {
                                                    String message = responsePost.getString("message");
                                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                                    sendDataToJurnalUmum(params);
                                                } else {
                                                    String errorMessage = responsePost.optString("message", "Terjadi kesalahan tanpa pesan spesifik");
                                                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                Log.e(TAG, "Error parsing API response", e);
                                                Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        },
                                        error -> {
                                            // Tangani berbagai kemungkinan error
                                            handleError(error);
                                        });

                                // Tambahkan request ke RequestQueue
                                requestQueue.add(postRequest);
                            }
                        } else {
                            Toast.makeText(this, "Error fetching Piutang", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error", e);
                        Toast.makeText(this, "Parsing Error Piutang", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e(TAG, "Request Error Piutang", error);
                    Toast.makeText(this, "Request Error Piutang", Toast.LENGTH_SHORT).show();
                });

        // Tambahkan request ke RequestQueue
        requestQueue.add(piutangRequest);
    }

    private void handleError(VolleyError error) {
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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(requestJurnal);
    }
}


