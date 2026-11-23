package sothuchi.view;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import sothuchi.model.GiaoDich;
import sothuchi.model.KhoanChi;
import sothuchi.model.KhoanThu;
import sothuchi.service.QuanLyThuChi;
import sothuchi.util.DinhDang;

// Giao diện dòng lệnh: in menu, đọc lựa chọn rồi gọi QuanLyThuChi làm việc.
public class MenuConsole {

    private final QuanLyThuChi quanLy;
    private final Scanner banPhim = new Scanner(System.in);
    private YearMonth thangDangXem = YearMonth.now();     // mở app là xem tháng hiện tại

    public MenuConsole(QuanLyThuChi quanLy) {
        this.quanLy = quanLy;
    }

    // ===== 1. Vòng lặp menu =====

    public void chay() {
        while (true) {
            System.out.println();
            System.out.println("===== SỔ THU CHI - " + DinhDang.thang(thangDangXem) + " =====");
            System.out.println("1. Xem giao dịch trong tháng");
            System.out.println("2. Thêm khoản thu");
            System.out.println("3. Thêm khoản chi");
            System.out.println("4. Sửa giao dịch");
            System.out.println("5. Xóa giao dịch");
            System.out.println("6. Tìm kiếm");
            System.out.println("7. Thống kê chi theo danh mục");
            System.out.println("8. Tổng kết theo tháng");
            System.out.println("9. Đổi tháng đang xem");
            System.out.println("0. Thoát");
            String chon = nhap("Chọn: ");
            try {
                switch (chon) {
                    case "1": xemThang(); break;
                    case "2": them(KhoanThu.LOAI); break;
                    case "3": them(KhoanChi.LOAI); break;
                    case "4": sua(); break;
                    case "5": xoa(); break;
                    case "6": timKiem(); break;
                    case "7": thongKe(); break;
                    case "8": tongKetTheoThang(); break;
                    case "9": doiThang(); break;
                    case "0":
                        System.out.println("Tạm biệt!");
                        return;
                    default:
                        System.out.println("Không có lựa chọn này.");
                }
            } catch (IllegalArgumentException e) {
                // Dữ liệu nhập sai: GiaoDich và DinhDang ném lỗi kèm lời nhắn tiếng Việt
                System.out.println("Lỗi: " + e.getMessage());
            }
        }
    }

    // ===== 2. Xem =====

    private void xemThang() {
        List<GiaoDich> ds = quanLy.loc("", null, thangDangXem);
        inBang(ds);
        System.out.println("Tổng thu:  " + DinhDang.tien(quanLy.tongThu(ds)));
        System.out.println("Tổng chi:  " + DinhDang.tien(quanLy.tongChi(ds)));
        System.out.println("Thu - chi: " + DinhDang.tienCoDau(quanLy.soDu(ds)));
        System.out.println("Số dư cuối tháng (cộng dồn các tháng trước): "
                + DinhDang.tien(quanLy.soDuDenHetThang(thangDangXem)));
    }

    private void timKiem() {
        inBang(quanLy.loc(nhap("Từ khóa (mô tả hoặc danh mục): "), null, null));
    }

    private void thongKe() {
        Map<String, Long> chi = quanLy.chiTheoDanhMuc(quanLy.loc("", null, thangDangXem));
        if (chi.isEmpty()) {
            System.out.println("Tháng này chưa có khoản chi nào.");
            return;
        }
        long lonNhat = chi.values().iterator().next();     // đã sắp giảm dần nên mục đầu lớn nhất
        for (Map.Entry<String, Long> muc : chi.entrySet()) {
            int doDai = (int) Math.max(1, muc.getValue() * 30 / lonNhat);   // cột dài tối đa 30 dấu #
            System.out.printf("%-10s %-30s %s%n", muc.getKey(), "#".repeat(doDai), DinhDang.tien(muc.getValue()));
        }
    }

    private void tongKetTheoThang() {
        System.out.printf("%-14s %15s %15s %16s %17s%n", "Tháng", "Thu", "Chi", "Thu - chi", "Số dư cuối tháng");
        for (YearMonth thang : quanLy.cacThang()) {
            List<GiaoDich> ds = quanLy.loc("", null, thang);
            System.out.printf("%-14s %15s %15s %16s %17s%n", DinhDang.thang(thang),
                    DinhDang.tien(quanLy.tongThu(ds)), DinhDang.tien(quanLy.tongChi(ds)),
                    DinhDang.tienCoDau(quanLy.soDu(ds)), DinhDang.tien(quanLy.soDuDenHetThang(thang)));
        }
    }

    private void doiThang() {
        String chu = nhap("Tháng muốn xem (tháng/năm, ví dụ 10/2026; Enter = tháng này): ");
        thangDangXem = chu.isEmpty() ? YearMonth.now() : DinhDang.docThang(chu);
    }

    // ===== 3. Thêm / Sửa / Xóa (xong là tự lưu file) =====

    private void them(String loai) {
        LocalDate ngay = nhapNgay(LocalDate.now());
        long soTien = DinhDang.docTien(nhap("Số tiền: "));
        String danhMuc = chonDanhMuc(loai, null);
        String moTa = nhap("Mô tả: ");
        GiaoDich gd = quanLy.them(loai, ngay, soTien, danhMuc, moTa);
        luu();
        System.out.println("Đã thêm khoản " + loai.toLowerCase() + " #" + gd.getId() + ".");
    }

    private void sua() {
        GiaoDich cu = quanLy.timTheoId(docSo(nhap("ID cần sửa: ")));
        if (cu == null) {
            System.out.println("Không có giao dịch này.");
            return;
        }
        System.out.println("Bấm Enter để giữ nguyên giá trị trong [ ].");
        String loai = nhapLoai(cu.getLoai());
        LocalDate ngay = nhapNgay(cu.getNgay());
        String chuTien = nhap("Số tiền [" + DinhDang.tien(cu.getSoTien()) + "]: ");
        long soTien = chuTien.isEmpty() ? cu.getSoTien() : DinhDang.docTien(chuTien);
        String danhMuc = chonDanhMuc(loai, loai.equals(cu.getLoai()) ? cu.getDanhMuc() : null);
        String chuMoTa = nhap("Mô tả [" + cu.getMoTa() + "]: ");
        String moTa = chuMoTa.isEmpty() ? cu.getMoTa() : chuMoTa;
        quanLy.sua(cu.getId(), loai, ngay, soTien, danhMuc, moTa);
        luu();
        System.out.println("Đã sửa giao dịch #" + cu.getId() + ".");
    }

    private void xoa() {
        GiaoDich gd = quanLy.timTheoId(docSo(nhap("ID cần xóa: ")));
        if (gd == null) {
            System.out.println("Không có giao dịch này.");
            return;
        }
        String dongY = nhap("Xóa #" + gd.getId() + " (" + gd.getMoTa() + ", " + DinhDang.tien(gd.getSoTien())
                + ")? (c/k): ");
        if (dongY.equalsIgnoreCase("c")) {
            quanLy.xoa(gd.getId());
            luu();
            System.out.println("Đã xóa.");
        } else {
            System.out.println("Đã hủy.");
        }
    }

    // ===== 4. Hàm phụ: in bảng, đọc dữ liệu nhập =====

    private void inBang(List<GiaoDich> ds) {
        if (ds.isEmpty()) {
            System.out.println("(Không có giao dịch nào)");
            return;
        }
        String mau = "%-4s %-11s %-5s %15s  %-10s %s%n";
        System.out.printf(mau, "ID", "Ngày", "Loại", "Số tiền", "Danh mục", "Mô tả");
        for (GiaoDich gd : ds) {
            // tinhAnhHuong(): thu ra số dương, chi ra số âm (đa hình)
            System.out.printf(mau, gd.getId(), DinhDang.ngay(gd.getNgay()), gd.getLoai(),
                    DinhDang.tienCoDau(gd.tinhAnhHuong()), gd.getDanhMuc(), gd.getMoTa());
        }
    }

    // In lời nhắc rồi đọc 1 dòng. Hết dữ liệu nhập (Ctrl+D) thì coi như chọn Thoát.
    private String nhap(String loiNhac) {
        System.out.print(loiNhac);
        if (!banPhim.hasNextLine()) {
            return "0";
        }
        return banPhim.nextLine().trim();
    }

    private int docSo(String chu) {
        try {
            return Integer.parseInt(chu);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Hãy nhập một số.");
        }
    }

    private LocalDate nhapNgay(LocalDate macDinh) {
        String chu = nhap("Ngày [" + DinhDang.ngay(macDinh) + "]: ");
        return chu.isEmpty() ? macDinh : DinhDang.docNgay(chu);
    }

    private String nhapLoai(String cu) {
        String chu = nhap("Loại [" + cu + "] (Thu/Chi): ");
        if (chu.isEmpty()) {
            return cu;
        }
        if (chu.equalsIgnoreCase(KhoanThu.LOAI)) {
            return KhoanThu.LOAI;
        }
        if (chu.equalsIgnoreCase(KhoanChi.LOAI)) {
            return KhoanChi.LOAI;
        }
        throw new IllegalArgumentException("Loại chỉ được là Thu hoặc Chi.");
    }

    // In danh mục đánh số theo loại; cu khác null thì Enter = giữ danh mục cũ
    private String chonDanhMuc(String loai, String cu) {
        String[] ds = KhoanThu.LOAI.equals(loai) ? KhoanThu.DANH_MUC : KhoanChi.DANH_MUC;
        for (int i = 0; i < ds.length; i++) {
            System.out.println("  " + (i + 1) + ". " + ds[i]);
        }
        String chu = nhap(cu == null ? "Danh mục (số): " : "Danh mục (số) [" + cu + "]: ");
        if (chu.isEmpty() && cu != null) {
            return cu;
        }
        int so = docSo(chu);
        if (so < 1 || so > ds.length) {
            throw new IllegalArgumentException("Hãy chọn số từ 1 đến " + ds.length + ".");
        }
        return ds[so - 1];
    }

    private void luu() {
        try {
            quanLy.luuDuLieu();
        } catch (IOException e) {
            System.out.println("Không lưu được file: " + e.getMessage());
        }
    }
}
