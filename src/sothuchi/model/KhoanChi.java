package sothuchi.model;

import java.time.LocalDate;

// Khoản chi: kế thừa GiaoDich, làm GIẢM số dư.
public class KhoanChi extends GiaoDich {

    public static final String LOAI = "Chi";
    public static final String[] DANH_MUC = {"Ăn uống", "Đi lại", "Học tập", "Nhà ở", "Mua sắm", "Giải trí", "Khác"};

    public KhoanChi(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        super(id, ngay, soTien, danhMuc, moTa);
    }

    @Override
    public String getLoai() {
        return LOAI;
    }

    @Override
    public long tinhAnhHuong() {
        return -getSoTien();    // số âm
    }
}
