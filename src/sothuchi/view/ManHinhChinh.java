package sothuchi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import sothuchi.model.GiaoDich;
import sothuchi.model.KhoanChi;
import sothuchi.model.KhoanThu;
import sothuchi.service.QuanLyThuChi;
import sothuchi.util.DinhDang;
import sothuchi.view.thanhphan.BangGiaoDich;
import sothuchi.view.thanhphan.BangMau;
import sothuchi.view.thanhphan.HopChon;
import sothuchi.view.thanhphan.KhungBo;
import sothuchi.view.thanhphan.KhungChuyenCanh;
import sothuchi.view.thanhphan.NutBo;
import sothuchi.view.thanhphan.NutChon;
import sothuchi.view.thanhphan.ONhap;
import sothuchi.view.thanhphan.ThanhCuonMong;
import sothuchi.view.thanhphan.ThongBaoNho;

// Cửa sổ chính:
//   [ Dải chào ]
//   [ Tổng thu ] [ Tổng chi ] [ Số dư ]
//   [ Form nhập ] [ Danh sách | Thống kê ]
public class ManHinhChinh extends JFrame {

    private static final String TAT_CA = "Tất cả";
    private static final String TRANG_DANH_SACH = "danhSach";
    private static final String TRANG_THONG_KE = "thongKe";

    private final QuanLyThuChi quanLy;
    private int idDangChon = -1;               // -1 nghĩa là chưa chọn dòng nào
    private boolean dangNapCombo = false;      // tránh lọc lại khi đang nạp ô chọn tháng
    private final List<YearMonth> dsThang = new ArrayList<>();

    // ----- Phần đầu -----
    private final KhungChao khungChao = new KhungChao();
    private final TheTongQuan theThu = new TheTongQuan("Tổng thu", BangMau.XANH, BangMau.XANH_NHAT, '+');
    private final TheTongQuan theChi = new TheTongQuan("Tổng chi", BangMau.CAM, BangMau.CAM_NHAT, '-');
    private final TheTongQuan theSoDu = new TheTongQuan("Số dư", BangMau.LAM, BangMau.LAM_NHAT, '=');

    // ----- Form nhập -----
    private final JLabel lblTieuDeForm = new JLabel();
    private final NutChon chonChi = new NutChon("Khoản chi", BangMau.CAM);
    private final NutChon chonThu = new NutChon("Khoản thu", BangMau.XANH);
    private final ONhap txtNgay = new ONhap("ngày/tháng/năm");
    private final ONhap txtSoTien = new ONhap("Ví dụ: 50.000");
    private final HopChon cboDanhMuc = new HopChon();
    private final ONhap txtMoTa = new ONhap("Ví dụ: Cơm trưa");
    private final NutBo btnThem = new NutBo("Thêm", BangMau.XANH);
    private final NutBo btnCapNhat = new NutBo("Cập nhật", BangMau.LAM);
    private final NutBo btnXoa = new NutBo("Xóa", BangMau.CAM);
    private final NutBo btnLamMoi = new NutBo("Làm mới", BangMau.XAM);

    // ----- Danh sách + thống kê -----
    private final NutChon tabDanhSach = new NutChon("Danh sách", BangMau.CHU);
    private final NutChon tabThongKe = new NutChon("Thống kê", BangMau.CHU);
    private final KhungChuyenCanh khungTrang = new KhungChuyenCanh();
    private final ONhap txtTimKiem = new ONhap("Tìm mô tả, danh mục...");
    private final HopChon cboLocLoai = new HopChon(new String[] {TAT_CA, KhoanThu.LOAI, KhoanChi.LOAI});
    private final HopChon cboLocThang = new HopChon();
    private final JLabel lblSoLuong = new JLabel();
    private final DefaultTableModel moHinhBang = new DefaultTableModel(
            new String[] {"Mã", "Ngày", "Loại", "Danh mục", "Mô tả", "Số tiền"}, 0) {
        @Override
        public boolean isCellEditable(int dong, int cot) {
            return false;   // không cho sửa trực tiếp trên bảng
        }
    };
    private final BangGiaoDich bang = new BangGiaoDich(moHinhBang);
    private final BieuDoPanel bieuDo = new BieuDoPanel();
    private final ThongBaoNho thongBao = new ThongBaoNho();

    public ManHinhChinh(QuanLyThuChi quanLy) {
        this.quanLy = quanLy;

        setTitle("Sổ Thu Chi Cá Nhân");
        setIconImage(taoBieuTuong());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Vừa với màn hình
        Rectangle vungMan = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setSize(Math.min(1220, vungMan.width - 40), Math.min(800, vungMan.height - 40));
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);   // mở giữa màn hình

        JPanel goc = new JPanel(new BorderLayout(0, 16));
        goc.setBackground(BangMau.NEN);
        goc.setBorder(BorderFactory.createEmptyBorder(18, 20, 20, 20));
        goc.add(taoPhanDau(), BorderLayout.NORTH);

        JPanel than = new JPanel(new BorderLayout(16, 0));
        than.setOpaque(false);
        than.add(taoFormNhap(), BorderLayout.WEST);
        than.add(taoPhanNoiDung(), BorderLayout.CENTER);
        goc.add(than, BorderLayout.CENTER);
        setContentPane(goc);

        // Lớp phủ để hiện thông báo
        setGlassPane(thongBao);

        ganSuKien();
        lamMoiForm();
        lamMoiGiaoDien();
    }

    // ===== 1. Dựng giao diện =====

    private JPanel taoPhanDau() {
        JPanel cacThe = new JPanel(new GridLayout(1, 3, 16, 0));
        cacThe.setOpaque(false);
        cacThe.add(theThu);
        cacThe.add(theChi);
        cacThe.add(theSoDu);

        JPanel phanDau = new JPanel(new BorderLayout(0, 16));
        phanDau.setOpaque(false);
        phanDau.add(khungChao, BorderLayout.NORTH);
        phanDau.add(cacThe, BorderLayout.CENTER);
        return phanDau;
    }

    private JPanel taoFormNhap() {
        KhungBo form = new KhungBo();
        form.setLayout(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 22, 20, 22));
        form.setPreferredSize(new Dimension(330, 0));

        lblTieuDeForm.setFont(BangMau.font(Font.BOLD, 19));
        lblTieuDeForm.setForeground(BangMau.CHU);

        chonChi.setSelected(true);

        // Ngày + Số tiền chung 1 hàng
        JPanel hangNgayTien = new JPanel(new GridLayout(1, 2, 10, 0));
        hangNgayTien.setOpaque(false);
        hangNgayTien.add(taoTruong("Ngày", txtNgay));
        hangNgayTien.add(taoTruong("Số tiền (VNĐ)", txtSoTien));

        JPanel cacNut = new JPanel(new GridLayout(2, 2, 10, 10));
        cacNut.setOpaque(false);
        cacNut.add(btnThem);
        cacNut.add(btnCapNhat);
        cacNut.add(btnXoa);
        cacNut.add(btnLamMoi);

        // Xếp các dòng từ trên xuống
        Component[] cacDong = {
            lblTieuDeForm,
            NutChon.taoNhom(chonChi, chonThu),
            hangNgayTien,
            taoTruong("Danh mục", cboDanhMuc),
            taoTruong("Mô tả", txtMoTa),
            cacNut
        };
        int[] khoangCachTren = {0, 12, 12, 12, 12, 20};

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        for (int i = 0; i < cacDong.length; i++) {
            c.gridy = i;
            c.insets = new Insets(khoangCachTren[i], 0, 0, 0);
            form.add(cacDong[i], c);
        }

        // Ô trống đẩy mọi thứ lên trên
        c.gridy = cacDong.length;
        c.weighty = 1;
        form.add(new JLabel(), c);
        return form;
    }

    /** Nhãn ở trên + ô nhập ở dưới. */
    private JPanel taoTruong(String nhan, Component o) {
        JLabel lbl = new JLabel(nhan);
        lbl.setFont(BangMau.font(Font.BOLD, 13));
        lbl.setForeground(BangMau.CHU_PHU);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));

        o.setPreferredSize(new Dimension(0, 40));
        o.setMinimumSize(new Dimension(0, 34));

        JPanel cum = new JPanel(new BorderLayout(0, 5));
        cum.setOpaque(false);
        cum.add(lbl, BorderLayout.NORTH);
        cum.add(o, BorderLayout.CENTER);
        return cum;
    }

    private JPanel taoPhanNoiDung() {
        // Hàng trên: tab + số lượng
        tabDanhSach.setSelected(true);
        JPanel bocTab = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bocTab.setOpaque(false);
        bocTab.add(NutChon.taoNhom(tabDanhSach, tabThongKe));

        lblSoLuong.setFont(BangMau.font(Font.PLAIN, 13));
        lblSoLuong.setForeground(BangMau.CHU_PHU);

        JPanel hangTren = new JPanel(new BorderLayout());
        hangTren.setOpaque(false);
        hangTren.add(bocTab, BorderLayout.WEST);
        hangTren.add(lblSoLuong, BorderLayout.EAST);

        // Trang danh sách: bộ lọc + bảng
        txtTimKiem.setPreferredSize(new Dimension(250, 40));
        txtTimKiem.setMaximumSize(new Dimension(320, 40));
        cboLocLoai.setPreferredSize(new Dimension(120, 40));
        cboLocLoai.setMaximumSize(new Dimension(120, 40));
        cboLocThang.setPreferredSize(new Dimension(175, 40));
        cboLocThang.setMaximumSize(new Dimension(175, 40));

        JPanel thanhLoc = new JPanel();
        thanhLoc.setLayout(new BoxLayout(thanhLoc, BoxLayout.X_AXIS));
        thanhLoc.setOpaque(false);
        thanhLoc.add(txtTimKiem);
        thanhLoc.add(Box.createHorizontalStrut(10));
        thanhLoc.add(cboLocLoai);
        thanhLoc.add(Box.createHorizontalStrut(10));
        thanhLoc.add(cboLocThang);
        thanhLoc.add(Box.createHorizontalGlue());

        bang.datDoRongCot(55, 105, 80, 110, 240, 140);
        JScrollPane cuon = new JScrollPane(bang);
        cuon.setBorder(BorderFactory.createEmptyBorder());
        cuon.getViewport().setBackground(Color.WHITE);
        cuon.getVerticalScrollBar().setUI(new ThanhCuonMong());
        cuon.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        JPanel goc = new JPanel();
        goc.setBackground(Color.WHITE);
        cuon.setCorner(JScrollPane.UPPER_RIGHT_CORNER, goc);

        JPanel trangDanhSach = new JPanel(new BorderLayout(0, 12));
        trangDanhSach.setOpaque(false);
        trangDanhSach.add(thanhLoc, BorderLayout.NORTH);
        trangDanhSach.add(cuon, BorderLayout.CENTER);

        // Gộp 2 trang
        khungTrang.themTrang(TRANG_DANH_SACH, trangDanhSach);
        khungTrang.themTrang(TRANG_THONG_KE, bieuDo);

        KhungBo khung = new KhungBo();
        khung.setLayout(new BorderLayout(0, 14));
        khung.setBorder(BorderFactory.createEmptyBorder(16, 18, 20, 18));
        khung.add(hangTren, BorderLayout.NORTH);
        khung.add(khungTrang, BorderLayout.CENTER);
        return khung;
    }

    // ===== 2. Gắn sự kiện =====

    private void ganSuKien() {
        btnThem.addActionListener(e -> them());
        btnCapNhat.addActionListener(e -> capNhat());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> {
            lamMoiForm();
            bang.clearSelection();
        });

        // Đổi Thu/Chi -> đổi danh mục
        chonThu.addActionListener(e -> napDanhMuc());
        chonChi.addActionListener(e -> napDanhMuc());

        // Chuyển tab Danh sách / Thống kê
        tabDanhSach.addActionListener(e -> khungTrang.chuyenToi(TRANG_DANH_SACH));
        tabThongKe.addActionListener(e -> {
            khungTrang.chuyenToi(TRANG_THONG_KE);
            bieuDo.chayHieuUng();
        });

        // Chọn dòng -> đưa lên form
        bang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonDong();
            }
        });

        // Gõ hoặc đổi bộ lọc -> lọc ngay
        txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { lamMoiGiaoDien(); }
            @Override public void removeUpdate(DocumentEvent e) { lamMoiGiaoDien(); }
            @Override public void changedUpdate(DocumentEvent e) { lamMoiGiaoDien(); }
        });
        cboLocLoai.addActionListener(e -> lamMoiGiaoDien());
        cboLocThang.addActionListener(e -> {
            if (!dangNapCombo) {
                lamMoiGiaoDien();
            }
        });
    }

    // ===== 3. Thêm / Sửa / Xóa =====

    private void them() {
        try {
            String loai = loaiDangChon();
            LocalDate ngay = DinhDang.docNgay(txtNgay.getText());
            long soTien = DinhDang.docTien(txtSoTien.getText());
            String danhMuc = (String) cboDanhMuc.getSelectedItem();
            String moTa = txtMoTa.getText();

            GiaoDich moi = quanLy.them(loai, ngay, soTien, danhMuc, moTa);
            luuVaLamMoi();
            lamMoiForm();
            bang.clearSelection();
            thongBao.hien("Đã thêm giao dịch #" + moi.getId(), BangMau.XANH);
        } catch (IllegalArgumentException loi) {
            baoLoi(loi.getMessage());
        }
    }

    private void capNhat() {
        if (idDangChon < 0) {
            baoLoi("Hãy chọn 1 dòng trong bảng trước khi cập nhật.");
            return;
        }
        try {
            String loai = loaiDangChon();
            LocalDate ngay = DinhDang.docNgay(txtNgay.getText());
            long soTien = DinhDang.docTien(txtSoTien.getText());
            String danhMuc = (String) cboDanhMuc.getSelectedItem();
            String moTa = txtMoTa.getText();

            int id = idDangChon;
            quanLy.sua(id, loai, ngay, soTien, danhMuc, moTa);
            luuVaLamMoi();
            thongBao.hien("Đã cập nhật giao dịch #" + id, BangMau.LAM);
        } catch (IllegalArgumentException loi) {
            baoLoi(loi.getMessage());
        }
    }

    private void xoa() {
        if (idDangChon < 0) {
            baoLoi("Hãy chọn 1 dòng trong bảng để xóa.");
            return;
        }
        int id = idDangChon;
        int traLoi = JOptionPane.showConfirmDialog(this,
                "Bạn chắc chắn muốn xóa giao dịch #" + id + "?", "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (traLoi == JOptionPane.YES_OPTION) {
            quanLy.xoa(id);
            lamMoiForm();
            luuVaLamMoi();
            thongBao.hien("Đã xóa giao dịch #" + id, BangMau.CAM);
        }
    }

    /** Lưu file rồi làm mới giao diện. */
    private void luuVaLamMoi() {
        try {
            quanLy.luuDuLieu();
        } catch (IOException loi) {
            baoLoi("Không lưu được file dữ liệu: " + loi.getMessage());
        }
        lamMoiGiaoDien();
    }

    // ===== 4. Cập nhật hiển thị =====

    /** Lọc rồi cập nhật bảng, thẻ, biểu đồ. */
    private void lamMoiGiaoDien() {
        napComboThang();

        String loai = cboLocLoai.getSelectedIndex() == 0 ? null : (String) cboLocLoai.getSelectedItem();
        YearMonth thang = thangDangChon();
        List<GiaoDich> ds = quanLy.loc(txtTimKiem.getText(), loai, thang);

        // 1. Bảng
        int idCanGiu = idDangChon;
        moHinhBang.setRowCount(0);
        for (GiaoDich gd : ds) {
            moHinhBang.addRow(new Object[] {
                gd.getId(),
                DinhDang.ngay(gd.getNgay()),
                gd.getLoai(),
                gd.getDanhMuc(),
                gd.getMoTa(),
                DinhDang.tienCoDau(gd.tinhAnhHuong())
            });
        }
        chonDongTheoId(idCanGiu);
        lblSoLuong.setText(ds.size() + " giao dịch");

        // 2. Thẻ tổng quan
        String khoang = (thang == null) ? "Tất cả thời gian" : DinhDang.thang(thang);
        long soDu = quanLy.soDu(ds);
        theThu.setGiaTri(quanLy.tongThu(ds));
        theChi.setGiaTri(quanLy.tongChi(ds));
        theSoDu.setGiaTri(soDu);
        theSoDu.setMauGiaTri(soDu < 0 ? BangMau.CAM : BangMau.LAM);
        theThu.setGhiChu(khoang);
        theChi.setGhiChu(khoang);
        theSoDu.setGhiChu(soDu < 0 ? "Đang tiêu nhiều hơn thu đó!" : "Thu trừ chi");
        khungChao.setDangXem(khoang);

        // 3. Biểu đồ
        bieuDo.setDuLieu(quanLy.chiTheoDanhMuc(ds), khoang);
    }

    /** Nạp danh sách tháng, giữ tháng đang chọn. */
    private void napComboThang() {
        YearMonth dangChon = thangDangChon();
        dangNapCombo = true;

        dsThang.clear();
        dsThang.addAll(quanLy.cacThangCoGiaoDich());
        DefaultComboBoxModel<String> moHinh = new DefaultComboBoxModel<>();
        moHinh.addElement(TAT_CA);
        for (YearMonth t : dsThang) {
            moHinh.addElement(DinhDang.thang(t));
        }
        cboLocThang.setModel(moHinh);

        int viTri = (dangChon == null) ? -1 : dsThang.indexOf(dangChon);
        cboLocThang.setSelectedIndex(viTri < 0 ? 0 : viTri + 1);
        dangNapCombo = false;
    }

    private YearMonth thangDangChon() {
        int viTri = cboLocThang.getSelectedIndex();
        if (viTri <= 0 || viTri > dsThang.size()) {
            return null;
        }
        return dsThang.get(viTri - 1);
    }

    /** Đưa dòng đang chọn lên form. */
    private void chonDong() {
        int dong = bang.getSelectedRow();
        if (dong < 0) {
            idDangChon = -1;
            capNhatTrangThaiForm();
            return;
        }
        int id = (Integer) moHinhBang.getValueAt(dong, 0);
        GiaoDich gd = quanLy.timTheoId(id);
        if (gd == null) {
            return;
        }
        idDangChon = id;

        if (gd instanceof KhoanThu) {
            chonThu.setSelected(true);
        } else {
            chonChi.setSelected(true);
        }
        napDanhMuc();
        chonDanhMuc(gd.getDanhMuc());
        txtNgay.setText(DinhDang.ngay(gd.getNgay()));
        txtSoTien.setText(DinhDang.tien(gd.getSoTien()));
        txtMoTa.setText(gd.getMoTa());
        capNhatTrangThaiForm();
    }

    private void chonDongTheoId(int id) {
        for (int i = 0; i < moHinhBang.getRowCount(); i++) {
            if ((Integer) moHinhBang.getValueAt(i, 0) == id) {
                bang.setRowSelectionInterval(i, i);
                bang.scrollRectToVisible(bang.getCellRect(i, 0, true));
                return;
            }
        }
        idDangChon = -1;
        capNhatTrangThaiForm();
    }

    /** Xóa trắng form. */
    private void lamMoiForm() {
        idDangChon = -1;
        txtNgay.setText(DinhDang.ngay(LocalDate.now()));
        txtSoTien.setText("");
        txtMoTa.setText("");
        napDanhMuc();
        capNhatTrangThaiForm();
    }

    /** Nạp danh mục theo loại Thu/Chi. */
    private void napDanhMuc() {
        String[] ds = chonThu.isSelected() ? KhoanThu.DANH_MUC : KhoanChi.DANH_MUC;
        cboDanhMuc.setModel(new DefaultComboBoxModel<>(ds));
    }

    private void chonDanhMuc(String danhMuc) {
        DefaultComboBoxModel<String> moHinh = (DefaultComboBoxModel<String>) cboDanhMuc.getModel();
        if (moHinh.getIndexOf(danhMuc) < 0) {
            moHinh.addElement(danhMuc);   // danh mục lạ đọc từ file thì thêm vào
        }
        cboDanhMuc.setSelectedItem(danhMuc);
    }

    /** Đổi tiêu đề form, bật/tắt nút theo chế độ thêm/sửa. */
    private void capNhatTrangThaiForm() {
        boolean dangSua = idDangChon >= 0;
        lblTieuDeForm.setText(dangSua ? "Sửa giao dịch #" + idDangChon : "Thêm giao dịch");
        btnCapNhat.setEnabled(dangSua);
        btnXoa.setEnabled(dangSua);
    }

    private String loaiDangChon() {
        return chonThu.isSelected() ? KhoanThu.LOAI : KhoanChi.LOAI;
    }

    private void baoLoi(String thongBaoLoi) {
        JOptionPane.showMessageDialog(this, thongBaoLoi, "Chưa đúng rồi", JOptionPane.WARNING_MESSAGE);
    }

    /** Icon cửa sổ: ô xanh chữ S. */
    private static Image taoBieuTuong() {
        BufferedImage anh = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = anh.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(BangMau.XANH);
        g2.fillRoundRect(0, 0, 64, 64, 20, 20);
        g2.setColor(Color.WHITE);
        g2.setFont(BangMau.font(Font.BOLD, 40));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("S", (64 - fm.stringWidth("S")) / 2, (64 + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
        return anh;
    }
}
