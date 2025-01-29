package com.example.siakuntansi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button barangButton = findViewById(R.id.BarangButton);
        barangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BarangActivity.class);
                startActivity(intent);
            }
        });

        Button AkunButton = findViewById(R.id.AkunButton);
        AkunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AkunActivity.class);
                startActivity(intent);
            }
        });

        Button CustButton = findViewById(R.id.CustomerButton);
        CustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                startActivity(intent);
            }
        });

        Button PenjualanButton = findViewById(R.id.inputTransaksiButton);
        PenjualanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PenjualanActivity.class);
                startActivity(intent);
            }
        });

        Button PendapatanLainButton = findViewById(R.id.pendapatanLainButton);
        PendapatanLainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PendapatanLainActivity.class);
                startActivity(intent);
            }
        });

        Button piutangButton = findViewById(R.id.piutangButton);
        piutangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PiutangActivity.class);
                startActivity(intent);
            }
        });

        Button bayarpiutangButton = findViewById(R.id.bayarpiutangButton);
        bayarpiutangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PembayaranPiutangActivity.class);
                startActivity(intent);
            }
        });

        Button jurnalumumButton = findViewById(R.id.jurnalumumButton);
        jurnalumumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, JurnalUmumActivity.class);
                startActivity(intent);
            }
        });

       Button bukubesarButton = findViewById(R.id.bukubesarButton);
        bukubesarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BukuBesarActivity.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}