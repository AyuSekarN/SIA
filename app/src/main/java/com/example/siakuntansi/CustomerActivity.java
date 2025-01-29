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

public class CustomerActivity extends AppCompatActivity {

    ListView listView;
    Button btnCust;
    ArrayList<String> custList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private String URL; // URL dinamis dari string resource

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        // Inisialisasi URL setelah context valid
        URL = getString(R.string.base_url) + "customer.php";
        Log.d("INIT_URL", "URL: " + URL);

        listView = findViewById(R.id.listViewCust);
        btnCust = findViewById(R.id.inputCustomerButton);

        adapter = new CustomerAdapter(this, custList);
        listView.setAdapter(adapter);

        btnCust.setOnClickListener(v -> {
            Intent intent = new Intent(this, InputCustActivity.class);
            startActivityForResult(intent, 1);
        });

        getData();
    }

    private void getData() {
        custList.clear(); // Kosongkan daftar sebelum memuat data baru
        Log.d("GET_DATA", "Fetching data from URL: " + URL);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            String data = obj.getString("id_customer") + " - " + obj.getString("nama_customer") +
                                    " - " + obj.getString("alamat_customer") + " - " + obj.getString("no_telp") +
                                    " - " + obj.getString("email_customer") + " - Rp" + obj.getString("batas_kredit");
                            custList.add(data);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing data", e);
                        Toast.makeText(CustomerActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error fetching data", error);
                    Toast.makeText(CustomerActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            boolean isDataAdded = data.getBooleanExtra("isDataAdded", false);
            if (isDataAdded) {
                Log.d("ACTIVITY_RESULT", "Data baru ditambahkan, memuat ulang data...");
                getData(); // Muat ulang data jika data baru telah ditambahkan
            }
        }
    }

    public void deleteData(String idCustomer) {
        String deleteUrl = URL + "?id_customer=" + idCustomer;
        Log.d("DELETE_DATA", "Deleting customer with URL: " + deleteUrl);

        StringRequest deleteRequest = new StringRequest(Request.Method.DELETE, deleteUrl,
                response -> {
                    Log.d("SERVER_RESPONSE", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(CustomerActivity.this, message, Toast.LENGTH_SHORT).show();
                            custList.clear(); // Kosongkan list untuk menyegarkan data
                            getData(); // Muat ulang data
                        } else {
                            Toast.makeText(CustomerActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("PARSE_ERROR", "Error parsing response", e);
                        Toast.makeText(CustomerActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NETWORK_ERROR", "Error deleting data", error);
                    Toast.makeText(CustomerActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(deleteRequest);
    }
}
