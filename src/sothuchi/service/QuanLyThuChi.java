package sothuchi.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import sothuchi.data.LuuTru;
import sothuchi.model.GiaoDich;
import sothuchi.model.KhoanChi;
import sothuchi.model.KhoanThu;

// Xử lý nghiệp vụ: thêm, sửa, xóa, lọc, thống kê. Giao diện chỉ gọi các hàm ở đây.
public class QuanLyThuChi {

    private final List<GiaoDich> danhSach = new ArrayList<>();
    private final LuuTru luuTru;        // dùng interface: không cần biết lưu file hay database
    private int idTiepTheo = 1;

    public QuanLyThuChi(LuuTru luuTru) {
        this.luuTru = luuTru;
    }

    // ===== Đọc / ghi =====

    public void taiDuLieu() throws IOException {
        danhSach.clear();
        danhSach.addAll(luuTru.doc());
        for (GiaoDich gd : danhSach) {
            idTiepTheo = Math.max(idTiepTheo, gd.getId() + 1);
        }
    }

    public void luuDuLieu() throws IOException {
        luuTru.luu(danhSach);
    }

    // ===== Thêm / Sửa / Xóa =====

    public GiaoDich them(String loai, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        GiaoDich gd = GiaoDich.tao(loai, idTiepTheo, ngay, soTien, danhMuc, moTa);
        danhSach.add(gd);
        idTiepTheo++;
        return gd;
    }

    // Sửa = thay đối tượng cũ bằng đối tượng mới cùng id (nên đổi được cả Thu <-> Chi)
    public void sua(int id, String loai, LocalDate ngay, long soTien, String danhMuc, String moTa) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getId() == id) {
                danhSach.set(i, GiaoDich.tao(loai, id, ngay, soTien, danhMuc, moTa));
            }
        }
    }

    public void xoa(int id) {
        danhSach.removeIf(gd -> gd.getId() == id);
    }

    public GiaoDich timTheoId(int id) {
        for (GiaoDich gd : danhSach) {
            if (gd.getId() == id) {
                return gd;
            }
        }
        return null;
    }

    // ===== Lọc =====

    // loai = null hoặc thang = null nghĩa là không lọc theo điều kiện đó. Ngày mới nhất lên đầu.
    public List<GiaoDich> loc(String tuKhoa, String loai, YearMonth thang) {
        String tk = tuKhoa.trim().toLowerCase();
        List<GiaoDich> ketQua = new ArrayList<>();
        for (GiaoDich gd : danhSach) {
            boolean dungLoai = (loai == null) || gd.getLoai().equals(loai);
            boolean dungThang = (thang == null) || YearMonth.from(gd.getNgay()).equals(thang);
            boolean dungTuKhoa = (gd.getMoTa() + " " + gd.getDanhMuc()).toLowerCase().contains(tk);
            if (dungLoai && dungThang && dungTuKhoa) {
                ketQua.add(gd);
            }
        }
        ketQua.sort(Comparator.comparing(GiaoDich::getNgay).thenComparingInt(GiaoDich::getId).reversed());
        return ketQua;
    }

    // Các tháng có giao dịch + tháng hiện tại, mới nhất trước.
    // Luôn có tháng hiện tại nên sang tháng mới là tự có tháng mới (bắt đầu từ 0 đ).
    public List<YearMonth> cacThang() {
        TreeSet<YearMonth> thang = new TreeSet<>(Comparator.reverseOrder());
        thang.add(YearMonth.now());
        for (GiaoDich gd : danhSach) {
            thang.add(YearMonth.from(gd.getNgay()));
        }
        return new ArrayList<>(thang);
    }

    // ===== Thống kê =====

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

    // Đa hình: không cần if/else, mỗi giao dịch tự biết mình cộng (+) hay trừ (-)
    public long soDu(List<GiaoDich> ds) {
        long soDu = 0;
        for (GiaoDich gd : ds) {
            soDu += gd.tinhAnhHuong();
        }
        return soDu;
    }

    // Số dư tích lũy từ đầu đến hết tháng: số dư tháng trước được chuyển sang tháng sau
    public long soDuDenHetThang(YearMonth thang) {
        long soDu = 0;
        for (GiaoDich gd : danhSach) {
            if (!YearMonth.from(gd.getNgay()).isAfter(thang)) {
                soDu += gd.tinhAnhHuong();
            }
        }
        return soDu;
    }

    // Tổng chi theo từng danh mục, danh mục chi nhiều nhất đứng đầu
    public Map<String, Long> chiTheoDanhMuc(List<GiaoDich> ds) {
        Map<String, Long> tong = new HashMap<>();
        for (GiaoDich gd : ds) {
            if (gd instanceof KhoanChi) {
                tong.put(gd.getDanhMuc(), tong.getOrDefault(gd.getDanhMuc(), 0L) + gd.getSoTien());
            }
        }
        List<Map.Entry<String, Long>> cacMuc = new ArrayList<>(tong.entrySet());
        cacMuc.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        Map<String, Long> ketQua = new LinkedHashMap<>();     // LinkedHashMap giữ đúng thứ tự đã sắp
        for (Map.Entry<String, Long> muc : cacMuc) {
            ketQua.put(muc.getKey(), muc.getValue());
        }
        return ketQua;
    }

    // ===== Dữ liệu mẫu cho lần chạy đầu =====

    public void taoDuLieuMau() {
        LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
        LocalDate thangTruoc = dauThang.minusMonths(1);
        them("Thu", thangTruoc.plusDays(1), 4500000, "Làm thêm", "Lương part-time tháng trước");
        them("Chi", thangTruoc.plusDays(2), 1800000, "Nhà ở", "Tiền phòng trọ");
        them("Chi", thangTruoc.plusDays(9), 650000, "Ăn uống", "Đi chợ cả tuần");
        them("Chi", thangTruoc.plusDays(15), 350000, "Học tập", "Mua giáo trình Java");
        them("Thu", dauThang, 3000000, "Được cho", "Bố mẹ gửi");
        them("Thu", dauThang.plusDays(4), 4500000, "Làm thêm", "Lương part-time");
        them("Chi", dauThang.plusDays(1), 1800000, "Nhà ở", "Tiền phòng trọ");
        them("Chi", dauThang.plusDays(2), 120000, "Đi lại", "Đổ xăng");
        them("Chi", dauThang.plusDays(3), 45000, "Ăn uống", "Cơm trưa");
        them("Chi", dauThang.plusDays(6), 500000, "Ăn uống", "Đi chợ cả tuần");
        them("Chi", dauThang.plusDays(8), 250000, "Giải trí", "Xem phim với bạn");
        them("Chi", dauThang.plusDays(10), 390000, "Mua sắm", "Áo thun");
    }
}
