package sothuchi.view.thanhphan;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

// Viền bo tròn, đổi màu xanh khi focus.
public class VienBo extends AbstractBorder {

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int rong, int cao) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean dangChon = c.isFocusOwner();
        g2.setColor(dangChon ? BangMau.XANH : BangMau.VIEN);
        g2.setStroke(new BasicStroke(dangChon ? 2f : 1.3f));
        g2.drawRoundRect(x + 1, y + 1, rong - 3, cao - 3, 16, 16);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(8, 14, 8, 10);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(8, 14, 8, 10);
        return insets;
    }
}
