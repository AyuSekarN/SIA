package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class PendapatanLainActivity extends AppCompatActivity {
    private TableLayout tablePendapatan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendapatan_lain);

        // Initialize TableLayout
        tablePendapatan = findViewById(R.id.tablePendapatan);

        // Fetch data from API
        fetchPendapatanData();

        Button btnInputPendapatanLain = findViewById(R.id.btnInputPendapatanLain);

        btnInputPendapatanLain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PendapatanLainActivity.this, InputPendapatanLainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchPendapatanData() {
        String url = getString(R.string.base_url) + "tr_pendapatanlain.php";

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject obj = response.getJSONObject(i);

                                    // Extract data
                                    String no = String.valueOf(i + 1);
                                    String kodeAkun = obj.getString("kode_akun");
                                    String namaPendapatan = obj.getString("nama_pendapatan");
                                    String jumlahPendapatan = obj.getString("jumlah_pendapatan");

                                    // Add row to table
                                    addTableRow(no, kodeAkun, namaPendapatan, jumlahPendapatan);
                                }
                            } else {
                                Toast.makeText(PendapatanLainActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PendapatanLainActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("API Error", error.toString());
                        Toast.makeText(PendapatanLainActivity.this, "Failed to fetch data. Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    private void addTableRow(String no, String kodeAkun, String namaPendapatan, String jumlahPendapatanRaw) {
        // Format jumlah pendapatan
        double jumlahPendapatanValue = Double.parseDouble(jumlahPendapatanRaw);
        String jumlahPendapatan = "Rp" + NumberFormat.getNumberInstance(new Locale("id", "ID")).format(jumlahPendapatanValue);

        // Create a new table row
        TableRow tableRow = new TableRow(this);

        // Create TextViews for each column
        TextView tvNo = new TextView(this);
        tvNo.setText(no);
        tvNo.setPadding(8, 8, 8, 8);

        TextView tvKodeAkun = new TextView(this);
        tvKodeAkun.setText(kodeAkun);
        tvKodeAkun.setPadding(8, 8, 8, 8);

        TextView tvNamaPendapatan = new TextView(this);
        tvNamaPendapatan.setText(namaPendapatan);
        tvNamaPendapatan.setPadding(8, 8, 8, 8);

        TextView tvJumlahPendapatan = new TextView(this);
        tvJumlahPendapatan.setText(jumlahPendapatan);
        tvJumlahPendapatan.setPadding(8, 8, 8, 8);

        // Add TextViews to the table row
        tableRow.addView(tvNo);
        tableRow.addView(tvKodeAkun);
        tableRow.addView(tvNamaPendapatan);
        tableRow.addView(tvJumlahPendapatan);

        // Add table row to the table layout
        tablePendapatan.addView(tableRow);
    }
}