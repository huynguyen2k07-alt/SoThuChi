package sothuchi.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

// Định dạng và đọc tiền, ngày, tháng. Vd: DinhDang.tien(150000) -> "150.000 đ"
public final class DinhDang {

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // STRICT: chặn ngày sai như 31/02
    private static final DateTimeFormatter NGAY_NHAP =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DinhDang() {
        // không cho tạo đối tượng
    }

    /** 1500000 -> "1.500.000 đ" */
    public static String tien(long soTien) {
        String chuoi = String.format("%,d", Math.abs(soTien)).replace(',', '.');
        return (soTien < 0 ? "-" : "") + chuoi + " đ";
    }

    /** Tiền có dấu +/- (dùng trong bảng). */
    public static String tienCoDau(long soTien) {
        return (soTien >= 0 ? "+" : "") + tien(soTien);
    }

    public static String ngay(LocalDate ngay) {
        return ngay.format(NGAY);
    }

    public static String thang(YearMonth thang) {
        return String.format("Tháng %02d/%d", thang.getMonthValue(), thang.getYear());
    }

    /** Đọc "50000", "50.000", "50,000 đ". */
    public static long docTien(String chuoi) {
        String so = chuoi.replace(".", "").replace(",", "").replace("đ", "")
                .replace("Đ", "").replace(" ", "").trim();
        if (so.isEmpty()) {
            throw new IllegalArgumentException("Hãy nhập số tiền.");
        }
        try {
            return Long.parseLong(so);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số tiền chỉ được gồm chữ số, ví dụ 50000 hoặc 50.000");
        }
    }

    /** Đọc ngày dạng 18/11/2026 hoặc 5/3/2026. */
    public static LocalDate docNgay(String chuoi) {
        try {
            return LocalDate.parse(chuoi.trim(), NGAY_NHAP);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày phải có dạng ngày/tháng/năm, ví dụ 18/11/2026");
        }
    }
}
