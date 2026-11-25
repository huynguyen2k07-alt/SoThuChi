package sothuchi.model;

import java.time.LocalDate;

// Khoản chi: làm giảm số dư.
public class KhoanChi extends GiaoDich {

    public static final String LOAI = "Chi";

    /** Các danh mục gợi ý cho khoản chi. */
    public static final String[] DANH_MUC = {
        "Ăn uống", "Đi lại", "Học tập", "Nhà ở", "Mua sắm", "Giải trí", "Khác"
    };

    public KhoanChi(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        super(id, ngay, soTien, danhMuc, moTa);
    }

    @Override
    public String getLoai() {
        return LOAI;
    }

    /** Trả về số âm. */
    @Override
    public long tinhAnhHuong() {
        return -getSoTien();
    }
}
