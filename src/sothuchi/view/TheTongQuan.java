package sothuchi.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.Timer;

import sothuchi.util.DinhDang;
import sothuchi.view.thanhphan.BangMau;
import sothuchi.view.thanhphan.HieuUng;
import sothuchi.view.thanhphan.KhungBo;

// Thẻ Tổng thu / Tổng chi / Số dư, con số chạy dần tới giá trị mới.
public class TheTongQuan extends KhungBo {

    private final Color mau;
    private final Color mauNhat;
    private final char kyHieu;   // '+', '-' hoặc '='

    private final JLabel lblGiaTri = new JLabel("0 đ");
    private final JLabel lblGhiChu = new JLabel(" ");
    private long giaTriDangHien = 0;
    private Timer hieuUng;

    public TheTongQuan(String tieuDe, Color mau, Color mauNhat, char kyHieu) {
        super();   // khung trắng bo góc
        this.mau = mau;
        this.mauNhat = mauNhat;
        this.kyHieu = kyHieu;

        setLayout(new BorderLayout(0, 2));
        setBorder(BorderFactory.createEmptyBorder(14, 88, 17, 16));   // chừa chỗ bên trái để vẽ biểu tượng

        JLabel lblTieuDe = new JLabel(tieuDe);
        lblTieuDe.setFont(BangMau.font(Font.PLAIN, 14));
        lblTieuDe.setForeground(BangMau.CHU_PHU);

        lblGiaTri.setFont(BangMau.font(Font.BOLD, 25));
        lblGiaTri.setForeground(mau);

        lblGhiChu.setFont(BangMau.font(Font.PLAIN, 12));
        lblGhiChu.setForeground(BangMau.XAM);

        add(lblTieuDe, BorderLayout.NORTH);
        add(lblGiaTri, BorderLayout.CENTER);
        add(lblGhiChu, BorderLayout.SOUTH);
    }

    /** Chạy số từ giá trị cũ tới mới trong 0,6s. */
    public void setGiaTri(final long giaTriMoi) {
        if (hieuUng != null) {
            hieuUng.stop();
        }
        final long giaTriCu = giaTriDangHien;
        hieuUng = HieuUng.chay(600, t -> {
            giaTriDangHien = giaTriCu + Math.round((giaTriMoi - giaTriCu) * (double) t);
            lblGiaTri.setText(DinhDang.tien(giaTriDangHien));
        }, null);
    }

    public void setMauGiaTri(Color mauChu) {
        lblGiaTri.setForeground(mauChu);
    }

    public void setGhiChu(String ghiChu) {
        lblGhiChu.setText(ghiChu);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   // lớp cha vẽ nền trắng bo góc

        // Biểu tượng tròn với dấu + / - / =
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int duongKinh = 48;
        int x = 24;
        int y = (getHeight() - 3 - duongKinh) / 2;
        g2.setColor(mauNhat);
        g2.fillOval(x, y, duongKinh, duongKinh);

        g2.setColor(mau);
        g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int tamX = x + duongKinh / 2;
        int tamY = y + duongKinh / 2;
        if (kyHieu == '+') {
            g2.drawLine(tamX - 9, tamY, tamX + 9, tamY);
            g2.drawLine(tamX, tamY - 9, tamX, tamY + 9);
        } else if (kyHieu == '-') {
            g2.drawLine(tamX - 9, tamY, tamX + 9, tamY);
        } else {
            g2.drawLine(tamX - 9, tamY - 4, tamX + 9, tamY - 4);
            g2.drawLine(tamX - 9, tamY + 4, tamX + 9, tamY + 4);
        }
        g2.dispose();
    }
}
