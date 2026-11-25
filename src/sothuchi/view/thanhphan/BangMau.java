package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

// Màu và font dùng chung.
public final class BangMau {

    public static final Color NEN = new Color(0xF5F2EA);        // nền kem
    public static final Color THE = Color.WHITE;                 // nền các khung
    public static final Color CHU = new Color(0x2F3B30);        // chữ chính (xanh rêu đậm)
    public static final Color CHU_PHU = new Color(0x7D8679);    // chữ phụ
    public static final Color VIEN = new Color(0xE5DFD3);       // đường viền
    public static final Color XAM = new Color(0xA8A294);
    public static final Color XAM_NHAT = new Color(0xF1EDE4);

    public static final Color XANH = new Color(0x5B8C5A);       // khoản thu, màu chủ đạo
    public static final Color XANH_NHAT = new Color(0xE5F0E1);
    public static final Color CAM = new Color(0xD9774B);        // khoản chi
    public static final Color CAM_NHAT = new Color(0xFBE8DD);
    public static final Color LAM = new Color(0x3F7D8C);        // số dư
    public static final Color LAM_NHAT = new Color(0xDDEEF1);

    private static final String TEN_FONT = chonFont();

    private BangMau() {
    }

    public static Font font(int kieu, int co) {
        return new Font(TEN_FONT, kieu, co);
    }

    /** Trộn 2 màu theo t (0 = a, 1 = b). */
    public static Color tron(Color a, Color b, float t) {
        int r = Math.round(a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    /** Chọn font có sẵn, gõ được tiếng Việt. */
    private static String chonFont() {
        String[] uuTien = {"Nunito", "Quicksand", "Ubuntu", "Noto Sans", "Segoe UI", "Helvetica Neue", "Arial"};
        String thuTiengViet = "Sổ Thu Chi Cá Nhân đặng ươ ữ ỹ";
        try {
            Set<String> coSan = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String ten : uuTien) {
                if (coSan.contains(ten) && new Font(ten, Font.PLAIN, 14).canDisplayUpTo(thuTiengViet) == -1) {
                    return ten;
                }
            }
        } catch (Exception e) {
            // không lấy được danh sách font thì dùng font mặc định
        }
        return Font.SANS_SERIF;
    }
}
