package com.example.siakuntansi;

public class PenjualanItem {
    private String namaBarang;
    private int harga;
    private int jumlah;
    private int IdBarang;

    // Constructor
    public PenjualanItem(String namaBarang, int harga, int jumlah) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif");
        }
        this.namaBarang = namaBarang;
        this.harga = harga;
        this.jumlah = jumlah;
    }

    public PenjualanItem(TransaksiItem transaksiItem) {
        this.IdBarang = transaksiItem.getIdBarang();
    }

public int getIdBarang() {
        return IdBarang;
}

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        if (harga < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        this.harga = harga;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        if (jumlah < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif");
        }
        this.jumlah = jumlah;
    }

    @Override
    public String toString() {
        return namaBarang + " - " + jumlah + " x Rp" + harga;
    }
}


