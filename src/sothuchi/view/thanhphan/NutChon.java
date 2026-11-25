package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

// Nút công tắc (vd: Khoản chi | Khoản thu).
public class NutChon extends JToggleButton {

    private final Color mauKhiChon;

    public NutChon(String chu, Color mauKhiChon) {
        super(chu);
        this.mauKhiChon = mauKhiChon;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(BangMau.font(Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(118, 36));
    }

    /** Gom các nút thành nhóm chỉ chọn 1. */
    public static KhungBo taoNhom(NutChon... cacNut) {
        KhungBo nen = new KhungBo(40, BangMau.XAM_NHAT, false);
        nen.setLayout(new GridLayout(1, cacNut.length, 4, 0));
        nen.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        ButtonGroup nhom = new ButtonGroup();
        for (NutChon nut : cacNut) {
            nhom.add(nut);
            nen.add(nut);
        }
        return nen;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int cao = getHeight() - 2;

        if (isSelected()) {
            g2.setColor(new Color(90, 70, 40, 22));
            g2.fillRoundRect(0, 2, getWidth(), cao, cao, cao);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), cao, cao, cao);
            g2.setColor(mauKhiChon);
        } else {
            g2.setColor(getModel().isRollover() ? BangMau.CHU : BangMau.CHU_PHU);
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (cao + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
