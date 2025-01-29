package com.example.siakuntansi;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JurnalUmumActivity extends AppCompatActivity {

    private TableLayout tlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal_umum);

        tlContent = findViewById(R.id.tlContent);

        // Load data from API
        loadDataFromAPI();
    }

    private void loadDataFromAPI() {
        String url = getString(R.string.base_url) + "jurnal_umum_get.php";// Ganti dengan URL API Anda

        // Request JSON Array
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Parse JSON and populate the TableLayout
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);

                                String tanggal = row.getString("tanggal");
                                String ref = row.getString("kode_akun");
                                String akun = row.getString("akun");
                                String debet = row.getString("debet");
                                String kredit = row.getString("kredit");
                                String deskripsi = row.getString("deskripsi");

                                addRowToTable(tanggal, ref, akun, debet, kredit, deskripsi);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(JurnalUmumActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(JurnalUmumActivity.this, "Error fetching data from API", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    private void addRowToTable(String tanggal, String ref, String akun, String debet, String kredit, String deskripsi) {
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
        tableRow.addView(createTextView(deskripsi, 2));

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
