package com.example.siakuntansi;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BukuBesarActivity extends AppCompatActivity {

    private TableLayout tlContent;
    private Spinner spinnerAkun;
    private ArrayList<String> kodeAkunList;
    private ArrayList<String> akunList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buku_besar);

        tlContent = findViewById(R.id.tlContent);
        spinnerAkun = findViewById(R.id.spinnerAkun);
        kodeAkunList = new ArrayList<>();
        akunList = new ArrayList<>();

        // Load data akun dari API
        loadDataAkun();
    }

    private void loadDataAkun() {
        String url = getString(R.string.base_url) + "akun.php"; // Ganti dengan URL API Anda

        // Request JSON Object
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Ambil data dari objek JSON
                            if (response.getString("status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject row = dataArray.getJSONObject(i);
                                    String kodeAkun = row.getString("kode_akun");
                                    String namaAkun = row.getString("nama_akun");
                                    kodeAkunList.add(kodeAkun);
                                    akunList.add(namaAkun);
                                }
                                // Isi Spinner dengan data akun
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(BukuBesarActivity.this,
                                        android.R.layout.simple_spinner_item, akunList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerAkun.setAdapter(adapter);

                                // Set listener untuk Spinner
                                spinnerAkun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String selectedKodeAkun = kodeAkunList.get(position);
                                        loadDataBukuBesar(selectedKodeAkun);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                        // Do nothing
                                    }
                                });
                            } else {
                                Toast.makeText(BukuBesarActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BukuBesarActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(BukuBesarActivity.this, "Error fetching data from API", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void loadDataBukuBesar(String kodeAkun) {
        // Clear previous data
        tlContent.removeAllViews();

        // URL untuk mendapatkan data buku besar berdasarkan kode akun
        String url = getString(R.string.base_url) + "buku_besar.php?kode_akun=" + kodeAkun;

        // Request JSON Array untuk buku besar
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parse JSON dan populate the TableLayout
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);

                                String tanggal = row.getString("tanggal");
                                String ref = row.getString("kode_akun");
                                String akun = row.getString("akun");
                                String debet = row.getString("debet");
                                String kredit = row.getString("kredit");
                                String saldo = row.getString("saldo"); // Jika Anda ingin menampilkan saldo

                                addRowToTable(tanggal, ref, akun, debet, kredit, saldo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(BukuBesarActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(BukuBesarActivity.this, "Error fetching data from API", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    private void addRowToTable(String tanggal, String ref, String akun, String debet, String kredit, String saldo) {
        // Create a new TableRow
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        // Create and add TextViews for each column
        tableRow.addView(createTextView(tanggal, 2));
        tableRow.addView(createTextView(ref, 1));
        tableRow.addView(createTextView(akun, 2));
        tableRow.addView(createTextView(debet, 2));
        tableRow.addView(createTextView(kredit, 2));
        tableRow.addView(createTextView(saldo, 2)); // Menambahkan saldo jika diperlukan

        // Add the TableRow to the TableLayout
        tlContent.addView(tableRow);
    }

    private TextView createTextView(String text, int weight) {
        TextView textView = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                weight
        );
        textView.setLayoutParams(params);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setGravity(Gravity.CENTER);
        return textView;
    }
}