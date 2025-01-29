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

public class AkunAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> akunList;

    public AkunAdapter(@NonNull Context context, ArrayList<String> akunList) {
        super(context, R.layout.list_item, akunList);
        this.context = context;
        this.akunList = akunList;
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

        String item = akunList.get(position);
        textViewItem.setText(item);

        deleteButton.setOnClickListener(v -> {
            String kodeAkun = item.split(" - ")[0].trim();

            if (context instanceof AkunActivity) {
                ((AkunActivity) context).deleteData(kodeAkun); // Panggil fungsi delete di Activity
                akunList.remove(position); // Hapus item dari daftar
                notifyDataSetChanged(); // Perbarui tampilan
            } else {
                Toast.makeText(context, "Error: Context tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            String[] data = item.split(" - ");
            String kodeAkun = data[0].trim();
            String namaAkun = data[1].trim();
            String saldoAkun = data[2].replace("Rp", "").trim();

            // Pastikan context yang dipakai valid
            Intent intent = new Intent(context, EditAkunActivity.class);
            intent.putExtra("kode_akun", kodeAkun);
            intent.putExtra("nama_akun", namaAkun);
            intent.putExtra("saldo_akun", saldoAkun);
            context.startActivity(intent);
        });

        return convertView;
    }
}

