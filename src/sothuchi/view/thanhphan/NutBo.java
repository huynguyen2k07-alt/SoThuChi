package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.Timer;

// Nút bo tròn, rê chuột thì đổi màu mượt.
public class NutBo extends JButton {

    private final Color mauGoc;
    private Color mauHienTai;
    private Timer hieuUng;

    public NutBo(String chu, Color mau) {
        super(chu);
        this.mauGoc = mau;
        this.mauHienTai = mau;

        // Tắt vẽ mặc định
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setFont(BangMau.font(Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(120, 40));
        setMinimumSize(new Dimension(60, 34));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    doiMau(BangMau.tron(mauGoc, Color.BLACK, 0.15f));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                doiMau(mauGoc);
            }
        });
    }

    /** Đổi màu dần trong 160ms. */
    private void doiMau(final Color mauDich) {
        if (hieuUng != null) {
            hieuUng.stop();
        }
        final Color mauBatDau = mauHienTai;
        hieuUng = HieuUng.chay(160, t -> {
            mauHienTai = BangMau.tron(mauBatDau, mauDich, t);
            repaint();
        }, null);
    }

    @Override
    public void setEnabled(boolean batDuoc) {
        super.setEnabled(batDuoc);
        if (hieuUng != null) {
            hieuUng.stop();
        }
        mauHienTai = mauGoc;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Color nen = isEnabled() ? mauHienTai : BangMau.tron(mauGoc, BangMau.THE, 0.6f);
        if (getModel().isPressed()) {
            nen = BangMau.tron(nen, Color.BLACK, 0.12f);
        }
        int cao = getHeight();
        g2.setColor(nen);
        g2.fillRoundRect(0, 0, getWidth(), cao, cao, cao);

        // Chữ ở giữa nút
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (cao + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
