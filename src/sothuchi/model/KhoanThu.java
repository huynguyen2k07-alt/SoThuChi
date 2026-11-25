package sothuchi.model;

import java.time.LocalDate;

// Khoản thu: làm tăng số dư.
public class KhoanThu extends GiaoDich {

    public static final String LOAI = "Thu";

    /** Các danh mục gợi ý cho khoản thu. */
    public static final String[] DANH_MUC = {
        "Lương", "Thưởng", "Làm thêm", "Được cho", "Bán đồ", "Khác"
    };

    public KhoanThu(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        super(id, ngay, soTien, danhMuc, moTa);   // gọi constructor của lớp cha
    }

    @Override
    public String getLoai() {
        return LOAI;
    }

    /** Trả về số dương. */
    @Override
    public long tinhAnhHuong() {
        return getSoTien();
    }
}
