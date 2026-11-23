package sothuchi.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;

import sothuchi.util.DinhDang;

// Biểu đồ cột ngang: tiền chi theo từng danh mục.
// Kế thừa JPanel và ghi đè (override) paintComponent để tự vẽ.
public class BieuDoPanel extends JPanel {

    private static final Color[] MAU = {
        new Color(0xD9774B), new Color(0xE9B949), new Color(0x5B8C5A), new Color(0x3F7D8C),
        new Color(0xB5838D), new Color(0x8D7B68), new Color(0x9AAB89)
    };
    private static final int X_COT = 130;      // cột bắt đầu sau phần tên danh mục

    private Map<String, Long> duLieu = new LinkedHashMap<>();

    public BieuDoPanel() {
        setBackground(Color.WHITE);
    }

    public void setDuLieu(Map<String, Long> duLieu) {
        this.duLieu = duLieu;
        repaint();      // báo Swing vẽ lại -> gọi paintComponent
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g2.drawString("Tiền đã đi đâu? (chi tiêu theo danh mục)", 20, 35);

        if (duLieu.isEmpty()) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            g2.drawString("Chưa có khoản chi nào.", 20, 70);
            return;
        }

        long tong = 0;
        long lonNhat = 0;
        for (long tien : duLieu.values()) {
            tong += tien;
            lonNhat = Math.max(lonNhat, tien);
        }

        int rongToiDa = Math.max(50, getWidth() - X_COT - 220);   // chừa 220px bên phải cho số tiền
        int y = 60;
        int i = 0;
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        for (Map.Entry<String, Long> muc : duLieu.entrySet()) {
            int rongCot = (int) Math.max(4, rongToiDa * muc.getValue() / lonNhat);   // cột lớn nhất dài nhất
            long phanTram = Math.round(muc.getValue() * 100.0 / tong);

            g2.setColor(Color.DARK_GRAY);
            g2.drawString(muc.getKey(), 20, y + 18);
            g2.setColor(MAU[i % MAU.length]);
            g2.fillRoundRect(X_COT, y, rongCot, 26, 12, 12);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(DinhDang.tien(muc.getValue()) + "  (" + phanTram + "%)", X_COT + rongCot + 10, y + 18);

            y += 40;
            i++;
        }

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        g2.drawString("Tổng chi: " + DinhDang.tien(tong), 20, y + 20);
    }
}
