package com.example.siakuntansi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class BarangAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> barangList;

    public BarangAdapter(@NonNull Context context, ArrayList<String> barangList) {
        super(context, R.layout.list_item, barangList);
        this.context = context;
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView textViewItem = convertView.findViewById(R.id.textViewItem);
        Button deleteButton = convertView.findViewById(R.id.deleteButton);
        Button editButton = convertView.findViewById(R.id.editButton);

        String item = barangList.get(position);
        textViewItem.setText(item);

        deleteButton.setOnClickListener(v -> {
            String idBarang = item.split(" - ")[0].trim();

            if (context instanceof BarangActivity) {
                ((BarangActivity) context).deleteData(idBarang); // Panggil fungsi delete di Activity
                barangList.remove(position); // Hapus item dari daftar
                notifyDataSetChanged(); // Perbarui tampilan
            } else {
                Toast.makeText(context, "Error: Context tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            String[] data = item.split(" - ");
            String idBarang = data[0].trim();
            String namaBarang = data[1].trim();
            String hargaBarang = data[2].replace("Rp", "").trim();

            Intent intent = new Intent(context, EditBarangActivity.class);
            intent.putExtra("id_barang", idBarang);
            intent.putExtra("nama_barang", namaBarang);
            intent.putExtra("harga_barang", hargaBarang);
            context.startActivity(intent);
        });

        return convertView;
    }
}



