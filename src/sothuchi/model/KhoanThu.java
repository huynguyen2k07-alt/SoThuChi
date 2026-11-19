package sothuchi.model;

import java.time.LocalDate;

// Khoản thu: kế thừa GiaoDich, làm TĂNG số dư.
public class KhoanThu extends GiaoDich {

    public static final String LOAI = "Thu";
    public static final String[] DANH_MUC = {"Lương", "Thưởng", "Làm thêm", "Được cho", "Bán đồ", "Khác"};

    public KhoanThu(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        super(id, ngay, soTien, danhMuc, moTa);   // gọi constructor lớp cha
    }

    @Override
    public String getLoai() {
        return LOAI;
    }

    @Override
    public long tinhAnhHuong() {
        return getSoTien();     // số dương
    }
}
