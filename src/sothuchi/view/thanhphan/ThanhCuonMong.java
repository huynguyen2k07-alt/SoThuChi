package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/** Thanh cuộn mảnh, bo tròn, không có nút mũi tên hai đầu. */
public class ThanhCuonMong extends BasicScrollBarUI {

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(0xDAD3C5);
        trackColor = Color.WHITE;
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return nutAn();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return nutAn();
    }

    private JButton nutAn() {
        JButton nut = new JButton();
        Dimension khong = new Dimension(0, 0);
        nut.setPreferredSize(khong);
        nut.setMinimumSize(khong);
        nut.setMaximumSize(khong);
        return nut;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle vung) {
        g.setColor(trackColor);
        g.fillRect(vung.x, vung.y, vung.width, vung.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle vung) {
        if (vung.isEmpty()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(isThumbRollover() ? new Color(0xC2BAA9) : thumbColor);
        g2.fillRoundRect(vung.x + 2, vung.y + 2, vung.width - 4, vung.height - 4, 8, 8);
        g2.dispose();
    }
}
