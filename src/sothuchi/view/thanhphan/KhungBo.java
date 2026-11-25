package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

// JPanel bo góc có bóng mờ.
public class KhungBo extends JPanel {

    private final int doBo;
    private final boolean coBong;
    private Color mauNen;

    public KhungBo(int doBo, Color mauNen, boolean coBong) {
        this.doBo = doBo;
        this.mauNen = mauNen;
        this.coBong = coBong;
        setOpaque(false);   // không tô nền hình chữ nhật mặc định
    }

    /** Khung trắng bo góc 24, có bóng. */
    public KhungBo() {
        this(24, BangMau.THE, true);
    }

    public void setMauNen(Color mauNen) {
        this.mauNen = mauNen;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int rong = getWidth();
        int cao = getHeight();

        if (coBong) {
            g2.setColor(new Color(90, 70, 40, 16));   // bóng nâu rất nhạt
            g2.fillRoundRect(0, 3, rong, cao - 3, doBo, doBo);
            cao -= 3;
        }
        g2.setColor(mauNen);
        g2.fillRoundRect(0, 0, rong, cao, doBo, doBo);
        g2.dispose();
    }
}
