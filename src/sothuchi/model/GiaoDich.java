package sothuchi.model;

import java.time.LocalDate;

// Lớp abstract cho 1 giao dịch. Lớp con: KhoanThu, KhoanChi.
public abstract class GiaoDich {

    private int id;
    private LocalDate ngay;
    private long soTien;     // tính bằng VNĐ nên dùng số nguyên long
    private String danhMuc;
    private String moTa;

    public GiaoDich(int id, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        this.id = id;
        setNgay(ngay);
        setSoTien(soTien);
        setDanhMuc(danhMuc);
        setMoTa(moTa);
    }

    // --- Lớp con tự viết lại ---

    /** Trả về "Thu" hoặc "Chi". */
    public abstract String getLoai();

    /** +soTien nếu là thu, -soTien nếu là chi. */
    public abstract long tinhAnhHuong();

    // --- Tạo đối tượng ---

    /** Tạo KhoanThu hoặc KhoanChi theo loại. */
    public static GiaoDich tao(String loai, int id, LocalDate ngay, long soTien,
                               String danhMuc, String moTa) {
        if (KhoanThu.LOAI.equals(loai)) {
            return new KhoanThu(id, ngay, soTien, danhMuc, moTa);
        }
        if (KhoanChi.LOAI.equals(loai)) {
            return new KhoanChi(id, ngay, soTien, danhMuc, moTa);
        }
        throw new IllegalArgumentException("Loại giao dịch không hợp lệ: " + loai);
    }

    // --- Getter / Setter ---

    public int getId() {
        return id;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public void setNgay(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày không được để trống.");
        }
        this.ngay = ngay;
    }

    public long getSoTien() {
        return soTien;
    }

    public void setSoTien(long soTien) {
        if (soTien <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        this.soTien = soTien;
    }

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        if (danhMuc == null || danhMuc.trim().isEmpty()) {
            throw new IllegalArgumentException("Hãy chọn danh mục.");
        }
        this.danhMuc = danhMuc.trim();
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = (moTa == null) ? "" : moTa.trim();
    }

    @Override
    public String toString() {
        return getLoai() + " #" + id + " | " + ngay + " | " + danhMuc + " | " + soTien;
    }
}
