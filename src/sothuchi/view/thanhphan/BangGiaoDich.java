package sothuchi.view.thanhphan;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

// Bảng giao dịch: tô sáng dòng khi rê chuột, nhãn Thu/Chi bo tròn.
public class BangGiaoDich extends JTable {

    // Vị trí cột (khớp với tiêu đề trong ManHinhChinh)
    public static final int COT_MA = 0;
    public static final int COT_LOAI = 2;
    public static final int COT_TIEN = 5;

    private static final Color MAU_DONG_LE = new Color(0xFCFBF7);
    private static final Color MAU_TRO_CHUOT = new Color(0xF4F8F1);

    private int dongTroChuot = -1;

    public BangGiaoDich(TableModel moHinh) {
        super(moHinh);
        setRowHeight(40);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        setBackground(Color.WHITE);
        setFont(BangMau.font(Font.PLAIN, 14));
        setDefaultRenderer(Object.class, new OBang());

        getTableHeader().setDefaultRenderer(new ODauBang());
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setPreferredSize(new Dimension(0, 40));
        getTableHeader().setBackground(Color.WHITE);

        // Tô sáng dòng dưới chuột
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int dong = rowAtPoint(e.getPoint());
                if (dong != dongTroChuot) {
                    dongTroChuot = dong;
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                dongTroChuot = -1;
                repaint();
            }
        });
    }

    public void datDoRongCot(int... doRong) {
        for (int i = 0; i < doRong.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getRowCount() == 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(BangMau.CHU_PHU);
            g2.setFont(BangMau.font(Font.PLAIN, 14));
            String chu = "Chưa có giao dịch nào ở đây";
            int x = (getWidth() - g2.getFontMetrics().stringWidth(chu)) / 2;
            g2.drawString(chu, x, 70);
            g2.dispose();
        }
    }

    private static int canhLe(int cot) {
        if (cot == COT_TIEN) {
            return SwingConstants.RIGHT;
        }
        if (cot == COT_MA || cot == COT_LOAI) {
            return SwingConstants.CENTER;
        }
        return SwingConstants.LEFT;
    }

    /** Vẽ từng ô. */
    private class OBang extends DefaultTableCellRenderer {

        private Color mauNhan;   // khác null thì vẽ nhãn bo tròn phía sau chữ

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
            setHorizontalAlignment(canhLe(column));

            if (isSelected) {
                setBackground(BangMau.XANH_NHAT);
            } else if (row == dongTroChuot) {
                setBackground(MAU_TRO_CHUOT);
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : MAU_DONG_LE);
            }

            boolean laThu = "Thu".equals(table.getModel().getValueAt(row, COT_LOAI));
            mauNhan = null;
            setForeground(BangMau.CHU);
            setFont(BangMau.font(Font.PLAIN, 14));

            if (column == COT_LOAI) {
                mauNhan = laThu ? BangMau.XANH_NHAT : BangMau.CAM_NHAT;
                setForeground(laThu ? BangMau.XANH : BangMau.CAM);
                setFont(BangMau.font(Font.BOLD, 13));
            } else if (column == COT_TIEN) {
                setForeground(laThu ? BangMau.XANH : BangMau.CAM);
                setFont(BangMau.font(Font.BOLD, 14));
            } else if (column == COT_MA) {
                setForeground(BangMau.XAM);
            }
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (mauNhan == null) {
                super.paintComponent(g);
                return;
            }
            // Cột Loại: vẽ nhãn bo tròn
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int rong = fm.stringWidth(getText()) + 24;
            int cao = 24;
            int x = (getWidth() - rong) / 2;
            int y = (getHeight() - cao) / 2;
            g2.setColor(mauNhan);
            g2.fillRoundRect(x, y, rong, cao, cao, cao);
            g2.setColor(getForeground());
            g2.drawString(getText(), x + 12, y + (cao + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }

    /** Vẽ tiêu đề bảng. */
    private static class ODauBang extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setBackground(Color.WHITE);
            setForeground(BangMau.CHU_PHU);
            setFont(BangMau.font(Font.BOLD, 13));
            setHorizontalAlignment(canhLe(column));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BangMau.VIEN),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14)));
            return this;
        }
    }
}
