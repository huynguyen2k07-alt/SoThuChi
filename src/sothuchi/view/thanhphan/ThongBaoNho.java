package sothuchi.view.thanhphan;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.Timer;

// Thông báo nhỏ cuối cửa sổ: trượt lên, đứng 1,8s rồi mờ dần.
public class ThongBaoNho extends JComponent {

    private String noiDung = "";
    private Color mauCham = BangMau.XANH;
    private float doRo = 0f;
    private Timer hieuUng;
    private Timer henGio;

    public ThongBaoNho() {
        setOpaque(false);
    }

    public void hien(String noiDung, Color mauCham) {
        this.noiDung = noiDung;
        this.mauCham = mauCham;
        dungHet();
        setVisible(true);

        final float batDau = doRo;
        hieuUng = HieuUng.chay(260, t -> {
            doRo = batDau + (1 - batDau) * t;
            repaint();
        }, () -> {
            henGio = new Timer(1800, e -> an());
            henGio.setRepeats(false);
            henGio.start();
        });
    }

    private void an() {
        hieuUng = HieuUng.chay(450, t -> {
            doRo = 1 - t;
            repaint();
        }, () -> setVisible(false));
    }

    private void dungHet() {
        if (hieuUng != null) {
            hieuUng.stop();
        }
        if (henGio != null) {
            henGio.stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (doRo <= 0f || noiDung.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.SrcOver.derive(Math.min(1f, doRo)));
        g2.setFont(BangMau.font(Font.PLAIN, 14));

        FontMetrics fm = g2.getFontMetrics();
        int cao = 44;
        int rong = fm.stringWidth(noiDung) + 60;
        int x = (getWidth() - rong) / 2;
        int y = getHeight() - cao - 30 + Math.round((1 - doRo) * 16);   // trượt nhẹ từ dưới lên

        g2.setColor(BangMau.CHU);
        g2.fillRoundRect(x, y, rong, cao, cao, cao);
        g2.setColor(mauCham);
        g2.fillOval(x + 20, y + cao / 2 - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.drawString(noiDung, x + 40, y + (cao + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
