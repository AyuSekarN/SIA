package com.example.siakuntansi;

public class TransaksiItem {
    private int idBarang;
    private int jumlah;
    private double harga;

    public TransaksiItem(int idBarang, int jumlah, double harga) {
        this.idBarang = idBarang;
        this.jumlah = jumlah;
        this.harga = harga;
    }

    public int getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(int idBarang) {
        this.idBarang = idBarang;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}

