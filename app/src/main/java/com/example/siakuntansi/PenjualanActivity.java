package com.example.siakuntansi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class PenjualanActivity extends AppCompatActivity {

    private Spinner spinnerNamaCustomer;
    private ArrayList<String> customerList;
    private TextView tvIDPenjualan;
    private RequestQueue requestQueue;
    private ArrayList<PenjualanItem> transaksiList = new ArrayList<>();
    private PenjualanAdapter penjualanAdapter;
    private RecyclerView recyclerViewBarang;
    private String urlCustomer;
    private String urlIDPenjualan;
    private String urlBarang;
    private String urlKodeAkun;
    private EditText etTotalHarga;
    private EditText etTotalBayar;
    private EditText etDiskon;
    private EditText etPajak;
    private TextView tvKodeAkun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);

        // Inisialisasi komponen UI
        TextView tvTanggalTransaksi = findViewById(R.id.tvTanggalTransaksi);
        tvIDPenjualan = findViewById(R.id.tvIDPenjualan);
        spinnerNamaCustomer = findViewById(R.id.spinnerNamaCustomer);
        Button btnTambahItem = findViewById(R.id.btnTambahItem);
        etTotalHarga = findViewById(R.id.etTotalHarga);
        etTotalBayar = findViewById(R.id.etTotalBayar);
        etPajak = findViewById(R.id.etPajak);
        RadioGroup radioGroupPembayaran = findViewById(R.id.radioGroupPembayaran);
        Button btnCheckout = findViewById(R.id.btnCheckout);
        tvKodeAkun = findViewById(R.id.tvKodeAkun);

        // Inisialisasi RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Inisialisasi RecyclerView
        recyclerViewBarang = findViewById(R.id.recyclerViewBarang);
        recyclerViewBarang.setLayoutManager(new LinearLayoutManager(this));
        transaksiList = new ArrayList<>();
        penjualanAdapter = new PenjualanAdapter(this, transaksiList);
        recyclerViewBarang.setAdapter(penjualanAdapter);
        etDiskon = findViewById(R.id.etDiskon);

        // Mendapatkan tanggal perangkat
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Menampilkan tanggal di TextView
        tvTanggalTransaksi.setText(currentDate);

        // URL API
        urlKodeAkun = getString(R.string.base_url) + "akun_getdata.php?get_kode_akun=401";
        urlIDPenjualan = getString(R.string.base_url) + "transaksi_penjualan_getdata.php?get_id_penjualan=true";
        urlCustomer = getString(R.string.base_url) + "customer.php";

        // Mengambil ID Penjualan dari API
        fetchIDPenjualan(urlIDPenjualan);

        // Mengambil data customer untuk Spinner
        fetchCustomerData(urlCustomer);

        fetchKodeAkun(urlKodeAkun);

        // Menangani klik tombol "Tambah Item"
        btnTambahItem.setOnClickListener(v -> showDialogTambahItem());

        btnCheckout.setOnClickListener(v -> {
            // Validasi metode pembayaran
            int selectedId = radioGroupPembayaran.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(PenjualanActivity.this, "Pilih metode pembayaran terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil data input form
            String kodeAkun = tvKodeAkun.getText().toString();
            String tanggalTransaksi = tvTanggalTransaksi.getText().toString();
            String namaCustomer = spinnerNamaCustomer.getSelectedItem().toString();
            String subTotal = etTotalHarga.getText().toString();
            String diskonBarang = etDiskon.getText().toString();
            String pajak = etPajak.getText().toString();
            String totalBayar = etTotalBayar.getText().toString();

            // Ambil ID customer
            HashMap<String, String> customerMap = (HashMap<String, String>) spinnerNamaCustomer.getTag();
            String idCustomer = customerMap.get(namaCustomer);

            if (tanggalTransaksi.isEmpty() || idCustomer == null || subTotal.isEmpty() || totalBayar.isEmpty()) {
                Toast.makeText(PenjualanActivity.this, "Data tidak lengkap.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Siapkan parameter untuk transaksi utama
            HashMap<String, String> params = new HashMap<>();
            params.put("kode_akun", kodeAkun);
            params.put("tanggal_transaksi", tanggalTransaksi);
            params.put("id_customer", idCustomer);
            params.put("sub_total", subTotal);
            params.put("diskon_barang", diskonBarang.isEmpty() ? "0" : diskonBarang);
            params.put("pajak", pajak.isEmpty() ? "0" : pajak);
            params.put("total_bayar", totalBayar);

            // Jenis pembayaran
            RadioButton selectedRadioButton = findViewById(selectedId);
            String jenisBayar = selectedRadioButton.getText().toString().toLowerCase();
            if (jenisBayar.equals("kredit")) {
                params.put("jenis_bayar", "kredit");
            } else {
                params.put("jenis_bayar", "lunas");
            }

            // Kirim data transaksi ke API
            sendDataToAPI(params, jenisBayar);
        });
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            int idDetail = data.getIntExtra("id_detail", -1);
            String namaBarang = data.getStringExtra("nama_barang");
            int hargaBarang = data.getIntExtra("harga_barang", 0);
            int jumlahBarang = data.getIntExtra("jumlah_barang", 0);

            if (idDetail != -1) {
                // Perbarui item di transaksiList
                PenjualanItem updatedItem = transaksiList.get(idDetail);
                updatedItem.setNamaBarang(namaBarang);
                updatedItem.setHarga(hargaBarang);
                updatedItem.setJumlah(jumlahBarang);

                penjualanAdapter.notifyItemChanged(idDetail); // Notifikasi perubahan
                hitungTotalHarga(); // Perbarui total harga setelah perubahan
            }
        }
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
                                String kodeAkun = akunObject.optString("kode_akun", ""); // Default ke 401 jika kosong

                                tvKodeAkun.setText(kodeAkun); // Set ke TextView
                            } else {
                                tvKodeAkun.setText("401"); // Set default jika tidak ada data
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Terjadi kesalahan");
                            Toast.makeText(PenjualanActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(PenjualanActivity.this, "Error parsing Kode Akun", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(PenjualanActivity.this, "Error fetching Kode Akun", Toast.LENGTH_SHORT).show());

        requestQueue.add(kodeAkunRequest);
    }

    private void fetchIDPenjualan(String urlIDPenjualan) {
        StringRequest request = new StringRequest(Request.Method.GET, urlIDPenjualan,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.getString("status").equals("success")) {
                            String nextID = jsonResponse.optString("next_id_penjualan", "1"); // Default ke 1 jika kosong

                            if (nextID.isEmpty() || nextID.equals("null")) {
                                nextID = "1"; // Set ID Penjualan ke 1 jika respons kosong
                            }

                            tvIDPenjualan.setText(nextID); // Set ID Penjualan ke TextView
                            Toast.makeText(this, "ID Penjualan berhasil diambil: " + nextID, Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonResponse.optString("message", "Terjadi kesalahan");
                            Toast.makeText(this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing ID Penjualan", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error fetching ID Penjualan", Toast.LENGTH_SHORT).show();
                }
        );

        // Tambahkan request ke queue
        requestQueue.add(request);
    }


    private void fetchCustomerData(String urlCustomer) {
        customerList = new ArrayList<>();
        HashMap<String, String> customerMap = new HashMap<>();

        StringRequest customerRequest = new StringRequest(Request.Method.GET, urlCustomer,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject customer = jsonArray.getJSONObject(i);
                            String namaCustomer = customer.getString("nama_customer");
                            String idCustomer = customer.getString("id_customer");

                            customerList.add(namaCustomer);
                            customerMap.put(namaCustomer, idCustomer); // Mapping nama ke ID
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PenjualanActivity.this,
                                android.R.layout.simple_spinner_item, customerList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerNamaCustomer.setAdapter(adapter);

                        // Menyimpan mapping untuk digunakan saat checkout
                        spinnerNamaCustomer.setTag(customerMap);

                    } catch (JSONException e) {
                        Toast.makeText(PenjualanActivity.this, "Error parsing customer data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(PenjualanActivity.this, "Error fetching customer data", Toast.LENGTH_SHORT).show());
        requestQueue.add(customerRequest);
    }

    private void fetchBarangData(Spinner spinnerItem, EditText etHargaItem) {
        urlBarang = getString(R.string.base_url) + "barang.php";
        ArrayList<String> barangList = new ArrayList<>();
        HashMap<String, String> barangHargaMap = new HashMap<>();

        StringRequest barangRequest = new StringRequest(Request.Method.GET, urlBarang,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject barang = jsonArray.getJSONObject(i);
                            String idBarang = barang.getString("id_barang");
                            String namaBarang = barang.getString("nama_barang");
                            String hargaBarang = barang.getString("harga_barang");

                            barangList.add(namaBarang);
                            barangHargaMap.put(namaBarang, hargaBarang);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PenjualanActivity.this,
                                android.R.layout.simple_spinner_item, barangList);
                        spinnerItem.setAdapter(adapter);

                        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                etHargaItem.setText(barangHargaMap.get(barangList.get(position)));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                etHargaItem.setText("");
                            }
                        });

                    } catch (JSONException e) {
                        Toast.makeText(PenjualanActivity.this, "Error parsing barang data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(PenjualanActivity.this, "Error fetching barang data", Toast.LENGTH_SHORT).show());
        requestQueue.add(barangRequest);
    }

    private void showDialogTambahItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_tambah_item, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Spinner spinnerItem = dialogView.findViewById(R.id.spinnerItem);
        EditText etHargaItem = dialogView.findViewById(R.id.etHargaItem);
        EditText etJumlahItem = dialogView.findViewById(R.id.etJumlahItem);
        Button btnSimpanItem = dialogView.findViewById(R.id.btnSimpanItem);

        fetchBarangData(spinnerItem, etHargaItem);

        btnSimpanItem.setOnClickListener(v -> {
            String namaBarang = spinnerItem.getSelectedItem().toString();
            String hargaStr = etHargaItem.getText().toString();
            String jumlahStr = etJumlahItem.getText().toString();

            if (namaBarang.isEmpty() || hargaStr.isEmpty() || jumlahStr.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            int harga = Integer.parseInt(hargaStr);
            int jumlah = Integer.parseInt(jumlahStr);

            PenjualanItem newItem = new PenjualanItem(namaBarang, harga, jumlah);
            transaksiList.add(newItem);
            penjualanAdapter.notifyDataSetChanged();
            hitungTotalHarga(); // Perbarui total harga setelah item ditambahkan

            Toast.makeText(this, "Item ditambahkan: " + namaBarang, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void hitungTotalHarga() {
        int totalHarga = 0;

        // Hitung total harga dari transaksi
        for (PenjualanItem item : transaksiList) {
            totalHarga += item.getHarga() * item.getJumlah();
        }
        etTotalHarga.setText(String.valueOf(totalHarga));

        // Hitung diskon 10% jika total harga lebih dari 1.000.000
        int diskon = 0;
        if (totalHarga > 2_000_000) {
            diskon = (int) Math.round(totalHarga * 0.10); // Diskon 10%
        }
        etDiskon.setText(String.valueOf(diskon)); // Tampilkan diskon

        // Hitung pajak 11%
        int pajak = (int) Math.round(totalHarga * 0.11);
        etPajak.setText(String.valueOf(pajak)); // Simpan nilai pajak

        // Hitung total bayar
        int totalBayar = (totalHarga - diskon) + pajak;
        etTotalBayar.setText(String.valueOf(totalBayar)); // Tampilkan total bayar
    }

    private void sendDataToAPI(HashMap<String, String> params, String jenisBayar) {
        String url = getString(R.string.base_url) + "transaksi_penjualan_post.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    try {
                        if (response.has("status") && response.getString("status").equals("success")) {
                            Toast.makeText(this, "Transaksi berhasil disimpan.", Toast.LENGTH_SHORT).show();

                            // Ambil ID transaksi yang baru saja disimpan
                            String idPenjualan = response.getString("id_penjualan");

                            // Jika kredit, tambahkan data piutang
                            if (jenisBayar.equals("kredit")) {
                                savePiutang(idPenjualan, params);
                                recordToJournalKredit(idPenjualan, params.get("tanggal_transaksi"), params.get("total_bayar"));
                            } else if (jenisBayar.equals("lunas")) {
                                recordToJournalLunas(idPenjualan, params.get("tanggal_transaksi"), params.get("total_bayar"));
                            }
                        } else {
                            String message = response.has("message") ? response.getString("message") : "Respons tidak valid.";
                            Toast.makeText(PenjualanActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PenjualanActivity.this, "Error JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("Volley Error", errorMsg);
                        Toast.makeText(PenjualanActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PenjualanActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void savePiutang(String idPenjualan, HashMap<String, String> params) {
        String url = getString(R.string.base_url) + "piutang_post.php";

        HashMap<String, String> piutangParams = new HashMap<>();
        piutangParams.put("id_penjualan", idPenjualan);
        piutangParams.put("id_customer", params.get("id_customer"));
        piutangParams.put("jumlah_piutang", params.get("total_bayar"));
        piutangParams.put("sisa_piutang", params.get("total_bayar"));
        piutangParams.put("tanggal_jatuhtempo", calculateDueDate(params.get("tanggal_transaksi")));
        piutangParams.put("status_piutang", "Belum Lunas");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(piutangParams),
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            Toast.makeText(this, "Data piutang berhasil disimpan.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PenjualanActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PenjualanActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(PenjualanActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Untuk transaksi lunas
    private void recordToJournalLunas(String idPenjualan, String tanggalTransaksi, String totalBayar) {
        String url = getString(R.string.base_url) + "jurnal_umum_lunas.php";

        HashMap<String, String> journalParams = new HashMap<>();
        journalParams.put("tanggal_transaksi", tanggalTransaksi);
        journalParams.put("total_bayar", totalBayar);

        sendJournalRequest(url, journalParams);
    }

    // Untuk transaksi kredit
    private void recordToJournalKredit(String idPenjualan, String tanggalTransaksi, String totalBayar) {
        String url = getString(R.string.base_url) + "jurnal_umum_kredit.php";

        HashMap<String, String> journalParams = new HashMap<>();
        journalParams.put("tanggal_transaksi", tanggalTransaksi);
        journalParams.put("total_bayar", totalBayar);

        sendJournalRequest(url, journalParams);
    }

    // Metode umum untuk mengirim permintaan jurnal
    private void sendJournalRequest(String url, HashMap<String, String> params) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                response -> {
                    try {
                        if (response.has("status") && response.getString("status").equals("success")) {
                            Toast.makeText(this, "Jurnal berhasil diperbarui.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = response.has("message") ? response.getString("message") : "Respons tidak valid.";
                            Toast.makeText(PenjualanActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PenjualanActivity.this, "Error JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMsg = new String(error.networkResponse.data);
                        Log.e("Volley Error", errorMsg);
                        Toast.makeText(PenjualanActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PenjualanActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    // Fungsi untuk menghitung tanggal jatuh tempo (30 hari setelah transaksi)
    private String calculateDueDate(String tanggalTransaksi) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(tanggalTransaksi));
            calendar.add(Calendar.DAY_OF_MONTH, 30); // Tambahkan 30 hari
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(calendar.getTime());
    }
}

