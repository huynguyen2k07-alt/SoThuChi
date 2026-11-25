package sothuchi.view.thanhphan;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

// Combo box bo tròn.
public class HopChon extends JComboBox<String> {

    public HopChon(String[] luaChon) {
        super(luaChon);
        setUI(new GiaoDienHopChon());
        setBorder(new VienBo());
        setOpaque(false);
        setBackground(Color.WHITE);
        setForeground(BangMau.CHU);
        setFont(BangMau.font(Font.PLAIN, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setRenderer(new ODong());

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

    public HopChon() {
        this(new String[0]);
    }

    /** Vẽ từng dòng trong danh sách xổ. */
    private static class ODong extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, false);
            // index -1 = ô đang hiện: để trong suốt
            setOpaque(index >= 0);
            setBorder(BorderFactory.createEmptyBorder(7, index < 0 ? 0 : 14, 7, 14));
            setBackground(isSelected ? BangMau.XANH_NHAT : Color.WHITE);
            setForeground(BangMau.CHU);
            return this;
        }
    }

    /** Giao diện riêng: mũi tên mảnh, không nền xanh. */
    private static class GiaoDienHopChon extends BasicComboBoxUI {

        @Override
        protected JButton createArrowButton() {
            JButton muiTen = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(BangMau.CHU_PHU);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int x = getWidth() / 2;
                    int y = getHeight() / 2;
                    g2.drawLine(x - 4, y - 2, x, y + 2);
                    g2.drawLine(x, y + 2, x + 4, y - 2);
                    g2.dispose();
                }
            };
            muiTen.setBorder(BorderFactory.createEmptyBorder());
            muiTen.setContentAreaFilled(false);
            muiTen.setFocusPainted(false);
            muiTen.setOpaque(false);
            return muiTen;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // không tô nền
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false);
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox);
            popup.setBorder(BorderFactory.createLineBorder(BangMau.VIEN));
            return popup;
        }
    }
}
