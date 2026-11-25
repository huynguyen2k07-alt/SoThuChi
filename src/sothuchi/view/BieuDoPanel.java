package sothuchi.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

import sothuchi.util.DinhDang;
import sothuchi.view.thanhphan.BangMau;
import sothuchi.view.thanhphan.HieuUng;

// Biểu đồ cột ngang: tổng chi theo danh mục, cột mọc dần khi dữ liệu đổi.
public class BieuDoPanel extends JPanel {

    private static final Color[] MAU = {
        new Color(0xD9774B), new Color(0xE9B949), new Color(0x5B8C5A), new Color(0x3F7D8C),
        new Color(0xB5838D), new Color(0x8D7B68), new Color(0x9AAB89)
    };

    private Map<String, Long> duLieu = new LinkedHashMap<>();
    private String moTaKhoangThoiGian = "";
    private float tienDo = 1f;   // 0 -> 1: cột đang mọc dài ra
    private Timer hieuUng;

    public BieuDoPanel() {
        setOpaque(false);
    }

    /** Cập nhật dữ liệu, chỉ chạy hiệu ứng khi dữ liệu khác. */
    public void setDuLieu(Map<String, Long> duLieuMoi, String moTa) {
        boolean khac = !duLieuMoi.equals(duLieu) || !moTa.equals(moTaKhoangThoiGian);
        this.duLieu = duLieuMoi;
        this.moTaKhoangThoiGian = moTa;
        if (khac) {
            chayHieuUng();
        }
    }

    public void chayHieuUng() {
        if (hieuUng != null) {
            hieuUng.stop();
        }
        hieuUng = HieuUng.chay(750, t -> {
            tienDo = t;
            repaint();
        }, null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int le = 12;
        int y = 30;

        // Tiêu đề
        g2.setColor(BangMau.CHU);
        g2.setFont(BangMau.font(Font.BOLD, 19));
        g2.drawString("Tiền đã đi đâu?", le, y);
        g2.setColor(BangMau.CHU_PHU);
        g2.setFont(BangMau.font(Font.PLAIN, 14));
        g2.drawString("Chi tiêu theo danh mục · " + moTaKhoangThoiGian, le, y + 24);
        y += 58;

        if (duLieu.isEmpty()) {
            g2.drawString("Chưa có khoản chi nào trong khoảng thời gian này.", le, y + 12);
            g2.dispose();
            return;
        }

        // Tìm max (độ dài cột) và tổng (tính %)
        long lonNhat = 0;
        long tong = 0;
        for (long giaTri : duLieu.values()) {
            lonNhat = Math.max(lonNhat, giaTri);
            tong += giaTri;
        }

        int rongNhan = 104;                                  // cột tên danh mục
        int rongSo = 170;                                    // cột số tiền bên phải
        int xCot = le + rongNhan;
        int rongToiDa = Math.max(80, getWidth() - le * 2 - rongNhan - rongSo);
        int caoCot = 26;
        int i = 0;

        for (Map.Entry<String, Long> muc : duLieu.entrySet()) {
            long giaTri = muc.getValue();
            int rongCot = (int) Math.max(caoCot, rongToiDa * giaTri / lonNhat * tienDo);
            long phanTram = Math.round(giaTri * 100.0 / tong);
            int giuaDong = y + caoCot / 2;

            // Tên danh mục
            g2.setFont(BangMau.font(Font.PLAIN, 14));
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(BangMau.CHU);
            g2.drawString(muc.getKey(), le, giuaDong + (fm.getAscent() - fm.getDescent()) / 2);

            // Rãnh nền + cột màu
            g2.setColor(BangMau.XAM_NHAT);
            g2.fillRoundRect(xCot, y, rongToiDa, caoCot, caoCot, caoCot);
            g2.setColor(MAU[i % MAU.length]);
            g2.fillRoundRect(xCot, y, rongCot, caoCot, caoCot, caoCot);

            // Số tiền và phần trăm
            int xSo = xCot + rongToiDa + 16;
            g2.setColor(BangMau.CHU);
            g2.setFont(BangMau.font(Font.BOLD, 14));
            String tien = DinhDang.tien(giaTri);
            g2.drawString(tien, xSo, giuaDong + (fm.getAscent() - fm.getDescent()) / 2);
            g2.setColor(BangMau.CHU_PHU);
            g2.setFont(BangMau.font(Font.PLAIN, 13));
            g2.drawString(phanTram + "%", xSo + g2.getFontMetrics(BangMau.font(Font.BOLD, 14)).stringWidth(tien) + 10,
                    giuaDong + (fm.getAscent() - fm.getDescent()) / 2);

            y += caoCot + 16;
            i++;
        }

        // Dòng tổng kết
        String nhieuNhat = duLieu.keySet().iterator().next();
        long phanTramNhieuNhat = Math.round(duLieu.get(nhieuNhat) * 100.0 / tong);
        y += 8;
        g2.setColor(BangMau.VIEN);
        g2.drawLine(le, y, getWidth() - le, y);
        g2.setColor(BangMau.CHU);
        g2.setFont(BangMau.font(Font.BOLD, 15));
        g2.drawString("Tổng chi: " + DinhDang.tien(tong), le, y + 30);
        g2.setColor(BangMau.CHU_PHU);
        g2.setFont(BangMau.font(Font.PLAIN, 14));
        g2.drawString("Bạn chi nhiều nhất cho \"" + nhieuNhat + "\", chiếm " + phanTramNhieuNhat + "% tổng chi.",
                le, y + 54);
        g2.dispose();
    }
}
