package sothuchi.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

// Chuyển tiền, ngày, tháng sang chữ để hiển thị và ngược lại.
public final class DinhDang {

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // STRICT: chặn ngày không có thật như 31/02
    private static final DateTimeFormatter NGAY_NHAP =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter THANG_NHAP =
            DateTimeFormatter.ofPattern("M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private DinhDang() {
    }

    // 1500000 -> "1.500.000 đ"
    public static String tien(long soTien) {
        return String.format(Locale.US, "%,d đ", soTien).replace(',', '.');
    }

    // Thêm dấu + cho số dương: "+1.500.000 đ" (dùng trong bảng)
    public static String tienCoDau(long soTien) {
        return (soTien > 0 ? "+" : "") + tien(soTien);
    }

    public static String ngay(LocalDate ngay) {
        return ngay.format(NGAY);
    }

    public static String thang(YearMonth thang) {
        return String.format("Tháng %02d/%d", thang.getMonthValue(), thang.getYear());
    }

    // "50000", "50.000", "50,000 đ" -> 50000
    public static long docTien(String chu) {
        String so = chu.replaceAll("[.,\\sđĐ]", "");
        if (!so.matches("\\d{1,15}")) {
            throw new IllegalArgumentException("Số tiền phải là số, ví dụ 50000 hoặc 50.000");
        }
        return Long.parseLong(so);
    }

    // "18/11/2026" hoặc "5/3/2026" -> LocalDate
    public static LocalDate docNgay(String chu) {
        try {
            return LocalDate.parse(chu.trim(), NGAY_NHAP);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày phải có dạng ngày/tháng/năm, ví dụ 18/11/2026");
        }
    }

    // "11/2026" -> YearMonth
    public static YearMonth docThang(String chu) {
        try {
            return YearMonth.parse(chu.trim(), THANG_NHAP);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Tháng phải có dạng tháng/năm, ví dụ 11/2026");
        }
    }
}
