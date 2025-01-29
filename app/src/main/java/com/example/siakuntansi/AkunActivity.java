package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AkunActivity extends AppCompatActivity {

    ListView listView;
    Button btnTambah;
    ArrayList<String> akunList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private String URL; // Dideklarasikan untuk mendapatkan nilai dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "akun.php";

        listView = findViewById(R.id.listViewAkun);
        btnTambah = findViewById(R.id.inputAkunButton);

        // Gunakan AkunAdapter atau ArrayAdapter tergantung kebutuhan
        adapter = new AkunAdapter(this, akunList); // Sesuaikan jika menggunakan AkunAdapter atau ArrayAdapter
        listView.setAdapter(adapter);

        btnTambah.setOnClickListener(v -> {
            Log.d("BTN_TAMBAH", "Navigasi ke InputAkunActivity");
            startActivity(new Intent(this, InputAkunActivity.class));
        });

        getData();
    }

    private void getData() {
        Log.d("FETCH_DATA", "URL: " + URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        akunList.clear(); // Kosongkan list sebelum menambahkan data baru
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String data = obj.getString("kode_akun") + " - "
                                    + obj.getString("nama_akun") + " - "
                                    + obj.getString("saldo_akun");
                            akunList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing data", e);
                        Toast.makeText(AkunActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error fetching data", error);
                    Toast.makeText(AkunActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void deleteData(String kodeAkun) {
        String deleteUrl = URL + "?kode_akun=" + kodeAkun;
        Log.d("DELETE_REQUEST", "URL: " + deleteUrl);

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equals(status)) {
                            Toast.makeText(AkunActivity.this, message, Toast.LENGTH_SHORT).show();
                            akunList.clear(); // Kosongkan list untuk menyegarkan data
                            getData(); // Muat ulang data
                        } else {
                            Toast.makeText(AkunActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(AkunActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error deleting data", error);
                    Toast.makeText(AkunActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(deleteRequest);
    }
}
