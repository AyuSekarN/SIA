package com.example.siakuntansi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTransaksiActivity extends AppCompatActivity {

    private EditText etNamaBarang, etHargaBarang, etJumlahBarang;
    private int idTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaksi);

        // Inisialisasi view
        etNamaBarang = findViewById(R.id.etNamaBarang);
        etHargaBarang = findViewById(R.id.etHargaBarang);
        etJumlahBarang = findViewById(R.id.etJumlahBarang);
        Button btnSimpan = findViewById(R.id.btnSimpan);

        // Ambil data dari intent
        Intent intent = getIntent();
        idTransaksi = intent.getIntExtra("id_transaksi", -1);
        String namaBarang = intent.getStringExtra("nama_barang");
        int hargaBarang = intent.getIntExtra("harga_barang", 0);
        int jumlahBarang = intent.getIntExtra("jumlah_barang", 0);

        // Set data ke EditText
        etNamaBarang.setText(namaBarang);
        etHargaBarang.setText(String.valueOf(hargaBarang));
        etJumlahBarang.setText(String.valueOf(jumlahBarang));

        // Listener tombol simpan
        btnSimpan.setOnClickListener(v -> {
            String updatedNamaBarang = etNamaBarang.getText().toString();
            int updatedHargaBarang = Integer.parseInt(etHargaBarang.getText().toString());
            int updatedJumlahBarang = Integer.parseInt(etJumlahBarang.getText().toString());

            if (updatedNamaBarang.isEmpty() || updatedHargaBarang <= 0 || updatedJumlahBarang <= 0) {
                Toast.makeText(EditTransaksiActivity.this, "Semua data harus valid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim hasil edit kembali ke aktivitas sebelumnya
            Intent resultIntent = new Intent();
            resultIntent.putExtra("id_transaksi", idTransaksi);
            resultIntent.putExtra("nama_barang", updatedNamaBarang);
            resultIntent.putExtra("harga_barang", updatedHargaBarang);
            resultIntent.putExtra("jumlah_barang", updatedJumlahBarang);
            setResult(RESULT_OK, resultIntent);

            Toast.makeText(EditTransaksiActivity.this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show();
            finish(); // Tutup aktivitas
        });
    }
}
