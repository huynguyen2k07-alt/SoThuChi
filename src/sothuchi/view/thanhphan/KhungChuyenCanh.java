package sothuchi.view.thanhphan;

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JComponent;
import javax.swing.JPanel;

// Chứa nhiều trang (CardLayout), chuyển trang có hiệu ứng mờ + trượt.
public class KhungChuyenCanh extends JPanel {

    private final CardLayout cacTrang = new CardLayout();
    private float doRo = 1f;   // 0 = trong suốt, 1 = hiện rõ

    public KhungChuyenCanh() {
        setLayout(cacTrang);
        setOpaque(false);
    }

    public void themTrang(String ten, JComponent trang) {
        add(trang, ten);
    }

    public void chuyenToi(String ten) {
        cacTrang.show(this, ten);
        HieuUng.chay(320, t -> {
            doRo = t;
            repaint();
        }, null);
    }

    /** Ghi đè paint() để làm mờ và dịch cả khung. */
    @Override
    public void paint(Graphics g) {
        if (doRo >= 1f) {
            super.paint(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(doRo));
        g2.translate(0, Math.round((1 - doRo) * 18));
        super.paint(g2);
        g2.dispose();
    }
}
