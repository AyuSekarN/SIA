-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jan 13, 2025 at 03:51 AM
-- Server version: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `siakuntansi`
--

-- --------------------------------------------------------

--
-- Table structure for table `akun`
--

CREATE TABLE `akun` (
  `kode_akun` int(11) NOT NULL,
  `nama_akun` varchar(50) NOT NULL,
  `saldo_akun` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `akun`
--

INSERT INTO `akun` (`kode_akun`, `nama_akun`, `saldo_akun`) VALUES
(101, 'Akun', '0'),
(102, 'Piutang Usaha', '0'),
(201, 'Hutang Usaha', '0'),
(301, 'Modal Pemilik', '0'),
(401, 'Pendapatan Utama', '0'),
(402, 'Pendapatan lain-lain', '0'),
(501, 'Beban Operasional', '0');

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id_barang` int(11) NOT NULL,
  `nama_barang` varchar(50) NOT NULL,
  `harga_barang` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id_barang`, `nama_barang`, `harga_barang`) VALUES
(1, 'Sepatu Ballet Poppy', '500000'),
(2, 'Sepatu Basket Cloud9', '700000');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `id_customer` int(11) NOT NULL,
  `nama_customer` varchar(50) NOT NULL,
  `alamat_customer` varchar(100) NOT NULL,
  `no_telp` int(13) NOT NULL,
  `email_customer` varchar(50) NOT NULL,
  `batas_kredit` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`id_customer`, `nama_customer`, `alamat_customer`, `no_telp`, `email_customer`, `batas_kredit`) VALUES
(1, 'Daniel', 'Jl. Surabaya no 18, Malang', 821546878, 'daniel@gmail.com', '20000000'),
(2, 'Lily', 'Jl. Mawar no.89, Jakarta', 87856432, 'lilylily22@gmail.com', '20000000');

-- --------------------------------------------------------

--
-- Table structure for table `detail_transaksi`
--

CREATE TABLE `detail_transaksi` (
  `id_detail` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `id_barang` int(11) NOT NULL,
  `jumlah_barang` int(11) NOT NULL,
  `sub_total` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jurnal_umum`
--

CREATE TABLE `jurnal_umum` (
  `id_jurnal` int(11) NOT NULL,
  `tanggal` date DEFAULT NULL,
  `kode_akun` int(11) DEFAULT NULL,
  `akun` varchar(100) NOT NULL,
  `debet` decimal(15,0) DEFAULT NULL,
  `kredit` decimal(15,0) DEFAULT NULL,
  `deskripsi` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jurnal_umum`
--

INSERT INTO `jurnal_umum` (`id_jurnal`, `tanggal`, `kode_akun`, `akun`, `debet`, `kredit`, `deskripsi`) VALUES
(22, '2025-01-12', 101, '0', '1000000', '0', 'Kas'),
(23, '2025-01-12', 402, '0', '0', '1000000', 'Pendapatan Lain-Lain'),
(24, '2025-01-12', 101, '0', '500000', '0', 'Kas'),
(25, '2025-01-12', 402, '0', '0', '500000', 'Pendapatan Lain-Lain'),
(26, '2025-01-12', 101, '0', '0', '50000', 'Piutang Usaha'),
(27, '2025-01-12', 102, '0', '50000', '0', 'Kas/Bank'),
(28, '2025-01-13', 102, 'Piutang Usaha', '1110000', '0', 'Piutang Usaha Dari Pendapatan'),
(29, '2025-01-13', 401, 'Pendapatan Penjualan', '0', '1110000', 'Pendapatan Dari Penjualan'),
(30, '2025-01-13', 101, 'Kas', '1110000', '0', 'Kas Dari Pendapatan'),
(31, '2025-01-13', 401, 'Pendapatan Penjualan', '0', '1110000', 'Pendapatan Dari Penjualan');

-- --------------------------------------------------------

--
-- Table structure for table `pembayaran_piutang`
--

CREATE TABLE `pembayaran_piutang` (
  `kode_akun` int(11) NOT NULL,
  `id_pembayaranpiutang` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `tanggal_pembayaran` date NOT NULL,
  `jumlah_piutang` decimal(10,0) NOT NULL,
  `jumlah_pembayaran` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pembayaran_piutang`
--

INSERT INTO `pembayaran_piutang` (`kode_akun`, `id_pembayaranpiutang`, `id_penjualan`, `tanggal_pembayaran`, `jumlah_piutang`, `jumlah_pembayaran`) VALUES
(102, 1, 0, '2025-01-12', '1110000', '500000'),
(102, 2, 72, '2025-01-12', '1110000', '200000'),
(102, 3, 72, '2025-01-12', '1110000', '100000'),
(102, 4, 82, '2025-01-12', '1554000', '50000');

-- --------------------------------------------------------

--
-- Table structure for table `piutang`
--

CREATE TABLE `piutang` (
  `id_piutang` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `jumlah_piutang` decimal(10,0) NOT NULL,
  `sisa_piutang` decimal(10,0) NOT NULL,
  `tanggal_jatuhtempo` date NOT NULL,
  `status_piutang` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `piutang`
--

INSERT INTO `piutang` (`id_piutang`, `id_penjualan`, `id_customer`, `jumlah_piutang`, `sisa_piutang`, `tanggal_jatuhtempo`, `status_piutang`) VALUES
(1, 72, 1, '1110000', '1110000', '2025-02-11', 'Belum Lunas'),
(2, 73, 1, '555000', '555000', '2025-02-11', 'Belum Lunas'),
(3, 74, 1, '1110000', '1110000', '2025-02-11', 'Belum Lunas'),
(5, 77, 1, '1110000', '1110000', '2025-02-11', 'Belum Lunas'),
(6, 78, 1, '1110000', '1110000', '2025-02-11', 'Belum Lunas'),
(7, 79, 1, '555000', '555000', '2025-02-11', 'Belum Lunas'),
(8, 80, 1, '1110000', '1110000', '2025-02-11', 'Belum Lunas'),
(9, 82, 2, '1554000', '1554000', '2025-02-11', 'Belum Lunas'),
(10, 83, 2, '1110000', '1110000', '2025-02-12', 'Belum Lunas'),
(11, 84, 1, '1110000', '1110000', '2025-02-12', 'Belum Lunas');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_pendapatanlain_lain`
--

CREATE TABLE `transaksi_pendapatanlain_lain` (
  `kode_akun` int(11) NOT NULL,
  `id_pendapatanlain_lain` int(11) NOT NULL,
  `tanggal_transaksi` date NOT NULL,
  `nama_pendapatan` varchar(100) NOT NULL,
  `jumlah_pendapatan` decimal(10,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi_pendapatanlain_lain`
--

INSERT INTO `transaksi_pendapatanlain_lain` (`kode_akun`, `id_pendapatanlain_lain`, `tanggal_transaksi`, `nama_pendapatan`, `jumlah_pendapatan`) VALUES
(402, 1, '2025-01-11', 'Hadiah', '2000000'),
(402, 2, '2025-01-11', 'Hadiah dari bank', '1500000'),
(402, 3, '2025-01-12', 'Hadiah teman', '2000000'),
(402, 4, '2025-01-12', 'Bunga bank', '1500000'),
(402, 5, '2025-01-12', 'Hadiah gane', '500000'),
(402, 6, '2025-01-12', 'Hadiah ulang tahun', '1000000'),
(402, 7, '2025-01-12', 'Hadiah undian', '1000000'),
(402, 8, '2025-01-12', 'Lomba', '1000000'),
(402, 9, '2025-01-12', 'Bunga Bank 2', '500000');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi_penjualan`
--

CREATE TABLE `transaksi_penjualan` (
  `kode_akun` int(11) NOT NULL,
  `id_penjualan` int(11) NOT NULL,
  `tanggal_transaksi` date NOT NULL,
  `id_customer` int(11) NOT NULL,
  `sub_total` decimal(10,0) NOT NULL,
  `diskon_barang` decimal(10,0) NOT NULL,
  `pajak` decimal(10,0) NOT NULL,
  `total_bayar` decimal(10,0) NOT NULL,
  `jenis_bayar` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `transaksi_penjualan`
--

INSERT INTO `transaksi_penjualan` (`kode_akun`, `id_penjualan`, `tanggal_transaksi`, `id_customer`, `sub_total`, `diskon_barang`, `pajak`, `total_bayar`, `jenis_bayar`) VALUES
(401, 36, '2025-01-11', 1, '3000000', '300000', '330000', '3030000', 'lunas'),
(401, 37, '2025-01-11', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 38, '2025-01-11', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 39, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 40, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 41, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 42, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 43, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 44, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 45, '2025-01-12', 2, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 46, '2025-01-12', 2, '2000000', '0', '220000', '2220000', 'lunas'),
(401, 47, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 48, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 49, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 50, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 51, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 52, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 53, '2025-01-12', 1, '1500000', '0', '165000', '1665000', 'lunas'),
(401, 54, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 55, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 56, '2025-01-12', 1, '500000', '0', '55000', '555000', 'lunas'),
(401, 57, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 58, '2025-01-12', 1, '500000', '0', '55000', '555000', 'lunas'),
(401, 59, '2025-01-12', 1, '500000', '0', '55000', '555000', 'lunas'),
(401, 60, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 61, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 62, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 63, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 64, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 65, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 66, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 67, '2025-01-12', 1, '500000', '0', '55000', '555000', 'kredit'),
(401, 68, '2025-01-12', 1, '500000', '0', '55000', '555000', 'lunas'),
(401, 69, '2025-01-12', 1, '500000', '0', '55000', '555000', 'lunas'),
(401, 70, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 71, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 72, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 73, '2025-01-12', 1, '500000', '0', '55000', '555000', 'kredit'),
(401, 74, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 75, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 76, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 77, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 78, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 79, '2025-01-12', 1, '500000', '0', '55000', '555000', 'kredit'),
(401, 80, '2025-01-12', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 81, '2025-01-12', 2, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 82, '2025-01-12', 2, '1400000', '0', '154000', '1554000', 'kredit'),
(401, 83, '2025-01-13', 2, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 84, '2025-01-13', 1, '1000000', '0', '110000', '1110000', 'kredit'),
(401, 85, '2025-01-13', 1, '1000000', '0', '110000', '1110000', 'lunas'),
(401, 86, '2025-01-13', 1, '1000000', '0', '110000', '1110000', 'lunas');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `akun`
--
ALTER TABLE `akun`
  ADD PRIMARY KEY (`kode_akun`);

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`id_customer`);

--
-- Indexes for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD PRIMARY KEY (`id_detail`),
  ADD KEY `id_penjualan` (`id_penjualan`),
  ADD KEY `id_barang` (`id_barang`);

--
-- Indexes for table `jurnal_umum`
--
ALTER TABLE `jurnal_umum`
  ADD PRIMARY KEY (`id_jurnal`),
  ADD KEY `kode_akun` (`kode_akun`);

--
-- Indexes for table `pembayaran_piutang`
--
ALTER TABLE `pembayaran_piutang`
  ADD PRIMARY KEY (`id_pembayaranpiutang`),
  ADD KEY `kode_akun` (`kode_akun`),
  ADD KEY `id_penjualan` (`id_penjualan`);

--
-- Indexes for table `piutang`
--
ALTER TABLE `piutang`
  ADD PRIMARY KEY (`id_piutang`),
  ADD KEY `id_penjualan` (`id_penjualan`),
  ADD KEY `id_customer` (`id_customer`);

--
-- Indexes for table `transaksi_pendapatanlain_lain`
--
ALTER TABLE `transaksi_pendapatanlain_lain`
  ADD PRIMARY KEY (`id_pendapatanlain_lain`),
  ADD KEY `kode_akun` (`kode_akun`);

--
-- Indexes for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  ADD PRIMARY KEY (`id_penjualan`),
  ADD KEY `id_customer` (`id_customer`),
  ADD KEY `kode_akun` (`kode_akun`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  MODIFY `id_detail` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `jurnal_umum`
--
ALTER TABLE `jurnal_umum`
  MODIFY `id_jurnal` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `pembayaran_piutang`
--
ALTER TABLE `pembayaran_piutang`
  MODIFY `id_pembayaranpiutang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `piutang`
--
ALTER TABLE `piutang`
  MODIFY `id_piutang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `transaksi_pendapatanlain_lain`
--
ALTER TABLE `transaksi_pendapatanlain_lain`
  MODIFY `id_pendapatanlain_lain` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  MODIFY `id_penjualan` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=87;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detail_transaksi`
--
ALTER TABLE `detail_transaksi`
  ADD CONSTRAINT `detail_transaksi_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`),
  ADD CONSTRAINT `detail_transaksi_ibfk_2` FOREIGN KEY (`id_penjualan`) REFERENCES `transaksi_penjualan` (`id_penjualan`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `jurnal_umum`
--
ALTER TABLE `jurnal_umum`
  ADD CONSTRAINT `jurnal_umum_ibfk_1` FOREIGN KEY (`kode_akun`) REFERENCES `akun` (`kode_akun`);

--
-- Constraints for table `piutang`
--
ALTER TABLE `piutang`
  ADD CONSTRAINT `piutang_ibfk_1` FOREIGN KEY (`id_penjualan`) REFERENCES `transaksi_penjualan` (`id_penjualan`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `piutang_ibfk_2` FOREIGN KEY (`id_customer`) REFERENCES `customer` (`id_customer`);

--
-- Constraints for table `transaksi_pendapatanlain_lain`
--
ALTER TABLE `transaksi_pendapatanlain_lain`
  ADD CONSTRAINT `transaksi_pendapatanlain_lain_ibfk_1` FOREIGN KEY (`kode_akun`) REFERENCES `akun` (`kode_akun`);

--
-- Constraints for table `transaksi_penjualan`
--
ALTER TABLE `transaksi_penjualan`
  ADD CONSTRAINT `transaksi_penjualan_ibfk_1` FOREIGN KEY (`kode_akun`) REFERENCES `akun` (`kode_akun`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
