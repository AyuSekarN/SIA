package com.example.siakuntansi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PenjualanAdapter extends RecyclerView.Adapter<PenjualanAdapter.PenjualanViewHolder> {
    private final Context context;
    private final ArrayList<PenjualanItem> transaksiList;

    public PenjualanAdapter(Context context, ArrayList<PenjualanItem> transaksiList) {
        this.context = context;
        this.transaksiList = transaksiList;
    }

    @NonNull
    @Override
    public PenjualanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new PenjualanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PenjualanViewHolder holder, int position) {
        PenjualanItem item = transaksiList.get(position);

        // Set teks pada TextView
        String displayText = item.getNamaBarang() + " - " + item.getJumlah() + " x Rp" + item.getHarga();
        holder.textViewItem.setText(displayText);

        int totalHarga = item.getJumlah() * item.getHarga();
        holder.textViewPrice.setText("Total: Rp" + totalHarga);

        // Listener tombol hapus
        holder.deleteButton.setOnClickListener(v -> {
            transaksiList.remove(position); // Hapus item dari daftar
            notifyItemRemoved(position); // Notifikasi perubahan data
            notifyItemRangeChanged(position, transaksiList.size());
            Toast.makeText(context, "Item dihapus: " + item.getNamaBarang(), Toast.LENGTH_SHORT).show();
        });

        holder.editButton.setOnClickListener(v -> {
            // Buat dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Inflate layout dialog
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.dialog_tambah_item, null);
            builder.setView(dialogView);

            // Dapatkan referensi ke elemen UI dalam dialog
            Spinner spinnerItem = dialogView.findViewById(R.id.spinnerItem);
            EditText etHargaItem = dialogView.findViewById(R.id.etHargaItem);
            EditText etJumlahItem = dialogView.findViewById(R.id.etJumlahItem);
            Button btnSimpanItem = dialogView.findViewById(R.id.btnSimpanItem);

            // Isi data awal jika diperlukan
            etHargaItem.setText(String.valueOf(item.getHarga()));
            etJumlahItem.setText(String.valueOf(item.getJumlah()));

            List<String> itemNames = new ArrayList<>();
            String URL = context.getString(R.string.base_url) + "barang.php";

            RequestQueue queue = Volley.newRequestQueue(context);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null,
                    response -> {
                        try {
                            // Iterasi data dari API
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonItem = response.getJSONObject(i); // Ganti 'item' menjadi 'jsonItem'
                                String itemName = jsonItem.getString("nama_barang"); // Sesuaikan dengan struktur JSON Anda
                                itemNames.add(itemName);
                            }

                            // Pasang data ke Spinner
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                    context,
                                    android.R.layout.simple_spinner_item,
                                    itemNames
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerItem.setAdapter(spinnerAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Gagal memuat data barang", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(context, "Kesalahan koneksi: " + error.getMessage(), Toast.LENGTH_SHORT).show()
            );

            queue.add(request);
            // Tampilkan dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    // ViewHolder untuk RecyclerView
    static class PenjualanViewHolder extends RecyclerView.ViewHolder {
        TextView textViewItem;
        TextView textViewPrice;
        Button deleteButton;
        Button editButton;

        public PenjualanViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textViewItem);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}



