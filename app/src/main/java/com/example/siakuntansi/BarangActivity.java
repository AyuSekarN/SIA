package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class BarangActivity extends AppCompatActivity {

    ListView listView;
    Button btnTambah;
    ArrayList<String> barangList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private String URL; // Dideklarasikan untuk mendapatkan nilai dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "barang.php";

        listView = findViewById(R.id.listViewBarang);
        btnTambah = findViewById(R.id.inputBarangButton);

        // Gunakan BarangAdapter atau ArrayAdapter tergantung kebutuhan
        adapter = new BarangAdapter(this, barangList);
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(v -> {
            Log.d("BTN_TAMBAH", "Navigasi ke InputBarangActivity");
            startActivity(new Intent(this, InputBarangActivity.class));
        });

        getData();
    }

    private void getData() {
        Log.d("FETCH_DATA", "URL: " + URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        barangList.clear(); // Kosongkan list sebelum menambahkan data baru
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String data = obj.getString("id_barang") + " - "
                                    + obj.getString("nama_barang") + " - Rp"
                                    + obj.getString("harga_barang");
                            barangList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing data", e);
                        Toast.makeText(BarangActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error fetching data", error);
                    Toast.makeText(BarangActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deleteData(String idBarang) {
        String deleteUrl = URL + "?id_barang=" + idBarang;
        Log.d("DELETE_REQUEST", "URL: " + deleteUrl);

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                            barangList.clear(); // Kosongkan list untuk menyegarkan data
                            getData(); // Muat ulang data
                        } else {
                            Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(BarangActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error deleting data", error);
                    Toast.makeText(BarangActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(deleteRequest);
    }
}

