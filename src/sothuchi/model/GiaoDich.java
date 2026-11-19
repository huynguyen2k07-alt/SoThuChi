package sothuchi.model;

import java.time.LocalDate;

// Lớp trừu tượng (abstract): phần chung của mọi giao dịch.
// Không tạo trực tiếp được, chỉ tạo lớp con KhoanThu hoặc KhoanChi.
public abstract class GiaoDich {

    // Đóng gói: thuộc tính private, bên ngoài chỉ đọc qua getter
    private final int id;
    private final LocalDate ngay;
    private final long soTien;      // VNĐ nên dùng số nguyên
    private final String danhMuc;
    private final String moTa;

    public GiaoDich(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        // Kiểm tra dữ liệu ngay khi tạo đối tượng
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }
        if (soTien <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        if (danhMuc == null || danhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Hãy chọn danh mục.");
        }
        this.id = id;
        this.ngay = ngay;
        this.soTien = soTien;
        this.danhMuc = danhMuc.trim();
        this.moTa = (moTa == null) ? "" : moTa.trim();
    }

    // Lớp con bắt buộc viết lại 2 hàm này (đa hình)
    public abstract String getLoai();       // "Thu" hoặc "Chi"
    public abstract long tinhAnhHuong();    // thu: +soTien, chi: -soTien

    // Tạo đúng lớp con theo loại ("Thu" -> KhoanThu, "Chi" -> KhoanChi)
    public static GiaoDich tao(String loai, int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        if (KhoanThu.LOAI.equals(loai)) {
            return new KhoanThu(id, ngay, soTien, danhMuc, moTa);
        }
        if (KhoanChi.LOAI.equals(loai)) {
            return new KhoanChi(id, ngay, soTien, danhMuc, moTa);
        }
        throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + loai);
    }

    public int getId() { return id; }
    public LocalDate getNgay() { return ngay; }
    public long getSoTien() { return soTien; }
    public String getDanhMuc() { return danhMuc; }
    public String getMoTa() { return moTa; }
}
