package sothuchi.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import sothuchi.data.LuuTru;
import sothuchi.model.GiaoDich;
import sothuchi.model.KhoanChi;
import sothuchi.model.KhoanThu;

// Xử lý nghiệp vụ: thêm, sửa, xóa, lọc, thống kê.
public class QuanLyThuChi {

    private final List<GiaoDich> danhSach = new ArrayList<>();
    private final LuuTru luuTru;     // dùng interface, không phụ thuộc file hay database
    private int idTiepTheo = 1;

    public QuanLyThuChi(LuuTru luuTru) {
        this.luuTru = luuTru;
    }

    // --- Đọc / ghi ---

    public void taiDuLieu() throws IOException {
        danhSach.clear();
        danhSach.addAll(luuTru.doc());
        idTiepTheo = 1;
        for (GiaoDich gd : danhSach) {
            if (gd.getId() >= idTiepTheo) {
                idTiepTheo = gd.getId() + 1;
            }
        }
    }

    public void luuDuLieu() throws IOException {
        luuTru.luu(danhSach);
    }

    // --- Thêm / Sửa / Xóa ---

    public GiaoDich them(String loai, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        GiaoDich gd = GiaoDich.tao(loai, idTiepTheo, ngay, soTien, danhMuc, moTa);
        danhSach.add(gd);
        idTiepTheo++;
        return gd;
    }

    /** Sửa theo id. Đổi loại thì tạo đối tượng mới, giữ nguyên id. */
    public void sua(int id, String loai, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        int viTri = timViTri(id);
        if (viTri < 0) {
            throw new IllegalArgumentException("Không tìm thấy giao dịch có mã " + id);
        }
        GiaoDich moi = GiaoDich.tao(loai, id, ngay, soTien, danhMuc, moTa);
        danhSach.set(viTri, moi);
    }

    public boolean xoa(int id) {
        int viTri = timViTri(id);
        if (viTri < 0) {
            return false;
        }
        danhSach.remove(viTri);
        return true;
    }

    public GiaoDich timTheoId(int id) {
        int viTri = timViTri(id);
        return (viTri < 0) ? null : danhSach.get(viTri);
    }

    private int timViTri(int id) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    // --- Lọc ---

    /** Lọc theo từ khóa, loại, tháng (null = bỏ qua). Mới nhất lên đầu. */
    public List<GiaoDich> loc(String tuKhoa, String loai, YearMonth thang) {
        String tk = (tuKhoa == null) ? "" : tuKhoa.trim().toLowerCase();
        List<GiaoDich> ketQua = new ArrayList<>();

        for (GiaoDich gd : danhSach) {
            boolean dungLoai = (loai == null) || gd.getLoai().equals(loai);
            boolean dungThang = (thang == null) || YearMonth.from(gd.getNgay()).equals(thang);
            boolean dungTuKhoa = tk.isEmpty()
                    || gd.getMoTa().toLowerCase().contains(tk)
                    || gd.getDanhMuc().toLowerCase().contains(tk);

            if (dungLoai && dungThang && dungTuKhoa) {
                ketQua.add(gd);
            }
        }

        // Ngày mới trước, cùng ngày thì id lớn trước
        Collections.sort(ketQua, new Comparator<GiaoDich>() {
            @Override
            public int compare(GiaoDich a, GiaoDich b) {
                int soSanhNgay = b.getNgay().compareTo(a.getNgay());
                return (soSanhNgay != 0) ? soSanhNgay : Integer.compare(b.getId(), a.getId());
            }
        });
        return ketQua;
    }

    public List<GiaoDich> layTatCa() {
        return loc(null, null, null);
    }

    /** Các tháng có giao dịch, mới nhất trước. */
    public List<YearMonth> cacThangCoGiaoDich() {
        TreeSet<YearMonth> thang = new TreeSet<>(Collections.reverseOrder());
        for (GiaoDich gd : danhSach) {
            thang.add(YearMonth.from(gd.getNgay()));
        }
        return new ArrayList<>(thang);
    }

    // --- Thống kê ---

    public long tongThu(List<GiaoDich> ds) {
        long tong = 0;
        for (GiaoDich gd : ds) {
            if (gd instanceof KhoanThu) {
                tong += gd.getSoTien();
            }
        }
        return tong;
    }

    public long tongChi(List<GiaoDich> ds) {
        long tong = 0;
        for (GiaoDich gd : ds) {
            if (gd instanceof KhoanChi) {
                tong += gd.getSoTien();
            }
        }
        return tong;
    }

    /** Số dư = tổng tinhAnhHuong() của từng giao dịch (đa hình). */
    public long soDu(List<GiaoDich> ds) {
        long soDu = 0;
        for (GiaoDich gd : ds) {
            soDu += gd.tinhAnhHuong();
        }
        return soDu;
    }

    /** Tổng chi theo danh mục, lớn nhất trước. */
    public Map<String, Long> chiTheoDanhMuc(List<GiaoDich> ds) {
        final Map<String, Long> tong = new LinkedHashMap<>();
        for (GiaoDich gd : ds) {
            if (gd instanceof KhoanChi) {
                Long cu = tong.get(gd.getDanhMuc());
                tong.put(gd.getDanhMuc(), (cu == null ? 0 : cu) + gd.getSoTien());
            }
        }

        List<String> danhMuc = new ArrayList<>(tong.keySet());
        Collections.sort(danhMuc, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return Long.compare(tong.get(b), tong.get(a));
            }
        });

        Map<String, Long> daSapXep = new LinkedHashMap<>();
        for (String dm : danhMuc) {
            daSapXep.put(dm, tong.get(dm));
        }
        return daSapXep;
    }

    public int soLuong() {
        return danhSach.size();
    }

    // --- Dữ liệu mẫu ---

    /** Tạo vài giao dịch mẫu cho lần chạy đầu. */
    public void taoDuLieuMau() {
        LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
        LocalDate thangTruoc = dauThang.minusMonths(1);

        them(KhoanThu.LOAI, thangTruoc.plusDays(1), 4500000, "Làm thêm", "Lương part-time tháng trước");
        them(KhoanChi.LOAI, thangTruoc.plusDays(2), 1800000, "Nhà ở", "Tiền phòng trọ");
        them(KhoanChi.LOAI, thangTruoc.plusDays(9), 650000, "Ăn uống", "Đi chợ cả tuần");
        them(KhoanChi.LOAI, thangTruoc.plusDays(15), 350000, "Học tập", "Mua giáo trình Java");

        them(KhoanThu.LOAI, dauThang, 3000000, "Được cho", "Bố mẹ gửi");
        them(KhoanThu.LOAI, dauThang.plusDays(4), 4500000, "Làm thêm", "Lương part-time");
        them(KhoanChi.LOAI, dauThang.plusDays(1), 1800000, "Nhà ở", "Tiền phòng trọ");
        them(KhoanChi.LOAI, dauThang.plusDays(2), 120000, "Đi lại", "Đổ xăng");
        them(KhoanChi.LOAI, dauThang.plusDays(3), 45000, "Ăn uống", "Cơm trưa");
        them(KhoanChi.LOAI, dauThang.plusDays(6), 500000, "Ăn uống", "Đi chợ cả tuần");
        them(KhoanChi.LOAI, dauThang.plusDays(8), 250000, "Giải trí", "Xem phim với bạn");
        them(KhoanChi.LOAI, dauThang.plusDays(10), 390000, "Mua sắm", "Áo thun");
    }
}
