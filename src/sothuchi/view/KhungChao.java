package sothuchi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sothuchi.util.DinhDang;
import sothuchi.view.thanhphan.BangMau;
import sothuchi.view.thanhphan.KhungBo;

// Dải chào trên cùng: lời chào theo buổi + khoảng thời gian đang xem.
public class KhungChao extends JPanel {

    private final JLabel lblDangXem = new JLabel();

    public KhungChao() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 26));

        JLabel lblTen = new JLabel("Sổ Thu Chi");
        lblTen.setFont(BangMau.font(Font.BOLD, 26));
        lblTen.setForeground(Color.WHITE);

        LocalDate homNay = LocalDate.now();
        JLabel lblChao = new JLabel(loiChao() + "  Hôm nay là " + thu(homNay) + ", " + DinhDang.ngay(homNay) + ".");
        lblChao.setFont(BangMau.font(Font.PLAIN, 14));
        lblChao.setForeground(new Color(0xE8F2E4));

        JPanel chu = new JPanel(new GridLayout(2, 1, 0, 2));
        chu.setOpaque(false);
        chu.add(lblTen);
        chu.add(lblChao);

        // Nhãn "Đang xem"
        lblDangXem.setFont(BangMau.font(Font.BOLD, 13));
        lblDangXem.setForeground(Color.WHITE);
        KhungBo vienDangXem = new KhungBo(34, new Color(255, 255, 255, 46), false);
        vienDangXem.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        vienDangXem.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        vienDangXem.add(lblDangXem);

        JPanel benPhai = new JPanel(new GridLayout(1, 1));
        benPhai.setOpaque(false);
        benPhai.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        benPhai.add(vienDangXem);

        add(chu, BorderLayout.WEST);
        add(benPhai, BorderLayout.EAST);
    }

    public void setDangXem(String khoangThoiGian) {
        lblDangXem.setText("Đang xem: " + khoangThoiGian);
    }

    public String getDangXem() {
        return lblDangXem.getText();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int rong = getWidth();
        int cao = getHeight();

        // Nền chuyển màu xanh
        g2.setPaint(new GradientPaint(0, 0, BangMau.XANH, rong, cao, new Color(0x8DB580)));
        g2.fillRoundRect(0, 0, rong, cao, 28, 28);

        // Hình tròn trang trí
        g2.setClip(0, 0, rong, cao);
        g2.setColor(new Color(255, 255, 255, 26));
        g2.fillOval(rong - 230, -90, 220, 220);
        g2.fillOval(rong - 420, cao - 40, 140, 140);
        g2.setColor(new Color(255, 255, 255, 18));
        g2.fillOval((int) (rong * 0.42), -40, 90, 90);
        g2.dispose();
    }

    private static String loiChao() {
        int gio = LocalTime.now().getHour();
        if (gio >= 5 && gio < 11) {
            return "Chào buổi sáng!";
        }
        if (gio >= 11 && gio < 14) {
            return "Chào buổi trưa!";
        }
        if (gio >= 14 && gio < 18) {
            return "Chào buổi chiều!";
        }
        return "Chào buổi tối!";
    }

    private static String thu(LocalDate ngay) {
        String[] ten = {"thứ Hai", "thứ Ba", "thứ Tư", "thứ Năm", "thứ Sáu", "thứ Bảy", "Chủ nhật"};
        return ten[ngay.getDayOfWeek().getValue() - 1];
    }
}
