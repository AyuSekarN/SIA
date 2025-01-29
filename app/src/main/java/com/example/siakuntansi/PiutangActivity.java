package com.example.siakuntansi;

import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class PiutangActivity extends AppCompatActivity {

    private TableLayout tableContent;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piutang);

        // Initialize TableLayout
        tableContent = findViewById(R.id.tableContent);

        // Initialize Volley Request Queue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch data from API
        fetchPiutangData();
    }

    private void fetchPiutangData() {
        String url = getString(R.string.base_url) + "piutang_getdata.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);

                                // Extract data from JSON object
                                String idPiutang = obj.getString("id_piutang");
                                String idPenjualan = obj.getString("id_penjualan");
                                String idCustomer = obj.getString("id_customer");
                                String jumlahPiutang = obj.getString("jumlah_piutang");
                                String sisaPiutang = obj.getString("sisa_piutang");
                                String tanggalJatuhTempo = obj.getString("tanggal_jatuhtempo");
                                String statusPiutang = obj.getString("status_piutang");

                                // Add a new row to the table
                                addTableRow(idPiutang, idPenjualan, idCustomer, jumlahPiutang, sisaPiutang, tanggalJatuhTempo, statusPiutang);
                            }
                        } else {
                            Toast.makeText(PiutangActivity.this, "Tidak ada data piutang", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PiutangActivity.this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("API Error", error.toString());
                    Toast.makeText(PiutangActivity.this, "Gagal mengambil data. Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    private void addTableRow(String idPiutang, String idPenjualan, String idCustomer, String jumlahPiutangRaw, String sisaPiutangRaw, String tanggalJatuhTempo, String statusPiutang) {
        double jumlahPiutangValue = Double.parseDouble(jumlahPiutangRaw);
        double sisaPiutangValue = Double.parseDouble(sisaPiutangRaw);

        // Format without decimals
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        numberFormat.setMaximumFractionDigits(0);
        String jumlahPiutang = "Rp" + numberFormat.format(jumlahPiutangValue);
        String sisaPiutang = "Rp" + numberFormat.format(sisaPiutangValue);

        // Create a new table row
        TableRow tableRow = new TableRow(this);

        // Create TextViews for each column
        TextView tvIdPiutang = new TextView(this);
        tvIdPiutang.setText(idPiutang);
        tvIdPiutang.setPadding(8, 8, 8, 8);

        TextView tvIdPenjualan = new TextView(this);
        tvIdPenjualan.setText(idPenjualan);
        tvIdPenjualan.setPadding(8, 8, 8, 8);

        TextView tvIdCustomer = new TextView(this);
        tvIdCustomer.setText(idCustomer);
        tvIdCustomer.setPadding(8, 8, 8, 8);

        TextView tvJumlahPiutang = new TextView(this);
        tvJumlahPiutang.setText(jumlahPiutang);
        tvJumlahPiutang.setPadding(8, 8, 8, 8);

        TextView tvSisaPiutang = new TextView(this);
        tvSisaPiutang.setText(sisaPiutang);
        tvSisaPiutang.setPadding(8, 8, 8, 8);

        TextView tvTanggalJatuhTempo = new TextView(this);
        tvTanggalJatuhTempo.setText(tanggalJatuhTempo);
        tvTanggalJatuhTempo.setPadding(8, 8, 8, 8);

        TextView tvStatusPiutang = new TextView(this);
        tvStatusPiutang.setText(statusPiutang);
        tvStatusPiutang.setPadding(8, 8, 8, 8);

        // Add TextViews to the table row
        tableRow.addView(tvIdPiutang);
        tableRow.addView(tvIdPenjualan);
        tableRow.addView(tvIdCustomer);
        tableRow.addView(tvJumlahPiutang);
        tableRow.addView(tvSisaPiutang);
        tableRow.addView(tvTanggalJatuhTempo);
        tableRow.addView(tvStatusPiutang);

        // Add the table row to the table layout
        tableContent.addView(tableRow);
    }
}
