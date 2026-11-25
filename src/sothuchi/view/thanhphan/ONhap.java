package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

// Ô nhập bo tròn, có chữ gợi ý mờ khi trống.
public class ONhap extends JTextField {

    private final String goiY;

    public ONhap(String goiY) {
        this.goiY = goiY;
        setOpaque(false);
        setBorder(new VienBo());
        setFont(BangMau.font(Font.PLAIN, 14));
        setForeground(BangMau.CHU);
        setCaretColor(BangMau.CHU);
        setSelectionColor(BangMau.XANH_NHAT);
        setSelectedTextColor(BangMau.CHU);

        // Vẽ lại khi focus đổi
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isFocusOwner() ? Color.WHITE : new Color(0xFCFBF7));
        g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
        g2.dispose();

        super.paintComponent(g);   // vẽ chữ và con trỏ như bình thường

        if (getText().isEmpty() && !isFocusOwner() && goiY != null) {
            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g3.setColor(BangMau.XAM);
            g3.setFont(getFont());
            FontMetrics fm = g3.getFontMetrics();
            Insets le = getInsets();
            g3.drawString(goiY, le.left, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g3.dispose();
        }
    }
}
