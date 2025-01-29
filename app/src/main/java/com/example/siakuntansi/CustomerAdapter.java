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

public class CustomerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> custList;

    public CustomerAdapter(@NonNull Context context, ArrayList<String> customerList) {
        super(context, R.layout.list_item, customerList);
        this.context = context;
        this.custList = customerList;
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

        // Set teks untuk item
        String item = custList.get(position);
        textViewItem.setText(item);

        // Tombol delete: ambil ID dari teks dan hapus item
        deleteButton.setOnClickListener(v -> {
            String idCustomer = item.split(" - ")[0].trim();

            if (context instanceof CustomerActivity) {
                ((CustomerActivity) context).deleteData(idCustomer); // Panggil fungsi delete di Activity
                custList.remove(position); // Hapus item dari daftar
                notifyDataSetChanged(); // Perbarui tampilan
            } else {
                Toast.makeText(context, "Error: Context tidak valid!", Toast.LENGTH_SHORT).show();
            }
        });

        editButton.setOnClickListener(v -> {
            String[] data = item.split(" - ");
            String idCustomer = data[0].trim();
            String namaCustomer = data[1].trim();
            String alamatCustomer = data[2].trim();
            String noTelp = data[3].trim();
            String emailCustomer = data[4].trim();
            String batasKredit = data[5].trim();

            // Intent ke EditCustomerActivity dengan data customer
            Intent intent = new Intent(context, EditCustomerActivity.class);
            intent.putExtra("id_customer", idCustomer);
            intent.putExtra("nama_customer", namaCustomer);
            intent.putExtra("alamat_customer", alamatCustomer);
            intent.putExtra("no_telp", noTelp);
            intent.putExtra("email_customer", emailCustomer);
            intent.putExtra("batas_kredit", batasKredit);
            context.startActivity(intent);
        });

        return convertView;
    }
}

