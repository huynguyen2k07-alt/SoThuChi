package sothuchi.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import sothuchi.model.GiaoDich;
import sothuchi.model.KhoanChi;
import sothuchi.model.KhoanThu;
import sothuchi.service.QuanLyThuChi;
import sothuchi.util.DinhDang;

// Cửa sổ chính:
//   [ Tổng thu ] [ Tổng chi ] [ Số dư ]          (của tháng đang xem)
//   [ Form nhập ] [ Tab: Danh sách | Theo tháng | Thống kê ]
public class ManHinhChinh extends JFrame {

    private static final Color XANH = new Color(0x2E7D32);   // khoản thu
    private static final Color CAM = new Color(0xD84315);    // khoản chi
    private static final Color LAM = new Color(0x1565C0);    // số dư

    private final QuanLyThuChi quanLy;
    private int idDangChon = -1;                        // -1 = chưa chọn dòng nào (chế độ Thêm)
    private List<YearMonth> dsThang = new ArrayList<>();  // các tháng, mới nhất trước
    private YearMonth thangHienTai = YearMonth.now();     // để biết khi nào sang tháng mới

    // Thẻ tổng quan
    private final JLabel lblTongThu = new JLabel();
    private final JLabel lblTongChi = new JLabel();
    private final JLabel lblSoDu = new JLabel();

    // Form nhập
    private final JLabel lblTieuDe = new JLabel();
    private final JRadioButton rdoChi = new JRadioButton("Khoản chi", true);
    private final JRadioButton rdoThu = new JRadioButton("Khoản thu");
    private final JTextField txtNgay = new JTextField();
    private final JTextField txtSoTien = new JTextField();
    private final JComboBox<String> cboDanhMuc = new JComboBox<>();
    private final JTextField txtMoTa = new JTextField();
    private final JButton btnThem = new JButton("Thêm");
    private final JButton btnCapNhat = new JButton("Cập nhật");
    private final JButton btnXoa = new JButton("Xóa");
    private final JButton btnLamMoi = new JButton("Làm mới");
    private final JLabel lblThongBao = new JLabel(" ");

    // Bộ lọc + các tab
    private final JTextField txtTimKiem = new JTextField(12);
    private final JComboBox<String> cboLocLoai = new JComboBox<>(new String[] {"Tất cả", "Thu", "Chi"});
    private final JComboBox<String> cboLocThang = new JComboBox<>();
    private final JButton btnThangTruoc = new JButton("◀");
    private final JButton btnThangSau = new JButton("▶");
    private final JTabbedPane tab = new JTabbedPane();
    private final DefaultTableModel moHinhBang = taoMoHinh("Mã", "Ngày", "Loại", "Danh mục", "Mô tả", "Số tiền");
    private final JTable bang = new JTable(moHinhBang);
    private final DefaultTableModel moHinhThang =
            taoMoHinh("Tháng", "Tổng thu", "Tổng chi", "Thu - Chi", "Số dư cuối tháng");
    private final JTable bangThang = new JTable(moHinhThang);
    private final BieuDoPanel bieuDo = new BieuDoPanel();

    public ManHinhChinh(QuanLyThuChi quanLy) {
        this.quanLy = quanLy;
        setTitle("Sổ Thu Chi Cá Nhân");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 700);
        setLocationRelativeTo(null);    // mở giữa màn hình

        JPanel goc = new JPanel(new BorderLayout(12, 12));
        goc.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        goc.add(taoCacThe(), BorderLayout.NORTH);
        goc.add(taoForm(), BorderLayout.WEST);
        goc.add(taoTab(), BorderLayout.CENTER);
        setContentPane(goc);

        ganSuKien();
        napThang();
        chonThang(thangHienTai);        // mở app là xem ngay tháng hiện tại
        lamMoiForm();
        lamMoiGiaoDien();

        // Mỗi phút kiểm tra 1 lần: hết tháng thì tự chuyển sang tháng mới
        new Timer(60_000, e -> {
            if (!YearMonth.now().equals(thangHienTai)) {
                thangHienTai = YearMonth.now();
                napThang();
                chonThang(thangHienTai);
                lamMoiGiaoDien();
            }
        }).start();
    }

    // ================= 1. DỰNG GIAO DIỆN =================

    private JPanel taoCacThe() {
        JPanel cacThe = new JPanel(new GridLayout(1, 3, 12, 0));
        cacThe.add(taoThe("Tổng thu", lblTongThu, XANH));
        cacThe.add(taoThe("Tổng chi", lblTongChi, CAM));
        cacThe.add(taoThe("Số dư", lblSoDu, LAM));
        return cacThe;
    }

    // Một thẻ: nền trắng, vạch màu bên trái, tiêu đề ở trên, con số to ở dưới
    private JPanel taoThe(String tieuDe, JLabel lblGiaTri, Color mau) {
        lblGiaTri.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblGiaTri.setForeground(mau);
        JPanel the = new JPanel(new BorderLayout());
        the.setBackground(Color.WHITE);
        the.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, mau),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)));
        the.add(new JLabel(tieuDe), BorderLayout.NORTH);
        the.add(lblGiaTri, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoForm() {
        ButtonGroup nhomLoai = new ButtonGroup();     // chỉ chọn được 1 trong 2
        nhomLoai.add(rdoChi);
        nhomLoai.add(rdoThu);
        JPanel chonLoai = new JPanel(new GridLayout(1, 2));
        chonLoai.add(rdoChi);
        chonLoai.add(rdoThu);

        lblTieuDe.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        JPanel oNhap = new JPanel(new GridLayout(0, 1, 0, 4));    // mỗi dòng 1 ô, xếp từ trên xuống
        oNhap.add(lblTieuDe);
        oNhap.add(chonLoai);
        oNhap.add(new JLabel("Ngày (ngày/tháng/năm)"));
        oNhap.add(txtNgay);
        oNhap.add(new JLabel("Số tiền (VNĐ)"));
        oNhap.add(txtSoTien);
        oNhap.add(new JLabel("Danh mục"));
        oNhap.add(cboDanhMuc);
        oNhap.add(new JLabel("Mô tả"));
        oNhap.add(txtMoTa);

        JPanel cacNut = new JPanel(new GridLayout(2, 2, 8, 8));
        cacNut.add(btnThem);
        cacNut.add(btnCapNhat);
        cacNut.add(btnXoa);
        cacNut.add(btnLamMoi);
        lblThongBao.setForeground(XANH);

        JPanel form = new JPanel(new BorderLayout(0, 12));
        form.add(oNhap, BorderLayout.NORTH);
        form.add(cacNut, BorderLayout.CENTER);
        form.add(lblThongBao, BorderLayout.SOUTH);

        JPanel cot = new JPanel(new BorderLayout());   // đặt ở NORTH để form không bị kéo giãn
        cot.setPreferredSize(new Dimension(300, 0));
        cot.add(form, BorderLayout.NORTH);
        return cot;
    }

    private JTabbedPane taoTab() {
        btnThangTruoc.setToolTipText("Tháng trước");
        btnThangSau.setToolTipText("Tháng sau");
        JPanel thanhLoc = new JPanel(new FlowLayout(FlowLayout.LEFT));
        thanhLoc.add(new JLabel("Tháng:"));
        thanhLoc.add(btnThangTruoc);
        thanhLoc.add(cboLocThang);
        thanhLoc.add(btnThangSau);
        thanhLoc.add(new JLabel("   Loại:"));
        thanhLoc.add(cboLocLoai);
        thanhLoc.add(new JLabel("   Tìm:"));
        thanhLoc.add(txtTimKiem);

        // Tab Danh sách: các giao dịch của tháng đang chọn
        bang.setRowHeight(28);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] doRong = {50, 100, 60, 110, 260, 130};
        for (int i = 0; i < doRong.length; i++) {
            bang.getColumnModel().getColumn(i).setPreferredWidth(doRong[i]);
        }
        toMauCotTien(bang, 5);

        JPanel trangDanhSach = new JPanel(new BorderLayout());
        trangDanhSach.add(thanhLoc, BorderLayout.NORTH);
        trangDanhSach.add(new JScrollPane(bang), BorderLayout.CENTER);

        // Tab Theo tháng: mỗi tháng 1 dòng, bấm vào dòng để xem chi tiết tháng đó
        bangThang.setRowHeight(28);
        bangThang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        toMauCotTien(bangThang, 1, 2, 3, 4);

        tab.addTab("Danh sách", trangDanhSach);
        tab.addTab("Theo tháng", new JScrollPane(bangThang));
        tab.addTab("Thống kê", bieuDo);
        return tab;
    }

    // Cột tiền: căn phải, số dương (+) màu xanh, số âm (-) màu cam
    private void toMauCotTien(JTable bangCanTo, int... cacCot) {
        DefaultTableCellRenderer oTien = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object giaTri, boolean dangChon,
                                                           boolean focus, int dong, int cot) {
                super.getTableCellRendererComponent(t, giaTri, dangChon, focus, dong, cot);
                if (!dangChon) {
                    setForeground(giaTri.toString().startsWith("-") ? CAM : XANH);
                }
                return this;
            }
        };
        oTien.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int cot : cacCot) {
            bangCanTo.getColumnModel().getColumn(cot).setCellRenderer(oTien);
        }
    }

    // Mô hình bảng không cho sửa trực tiếp trên ô
    private static DefaultTableModel taoMoHinh(String... tenCot) {
        return new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int dong, int cot) {
                return false;
            }
        };
    }

    // ================= 2. GẮN SỰ KIỆN =================

    private void ganSuKien() {
        btnThem.addActionListener(e -> them());
        btnCapNhat.addActionListener(e -> capNhat());
        btnXoa.addActionListener(e -> xoa());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        // Đổi Thu/Chi -> đổi danh sách danh mục
        rdoThu.addActionListener(e -> napDanhMuc());
        rdoChi.addActionListener(e -> napDanhMuc());

        // Bấm vào 1 dòng -> đưa dữ liệu dòng đó lên form để sửa
        bang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonDong();
            }
        });

        // Nút ◀ ▶ chuyển tháng (danh sách tháng xếp mới nhất trước nên ◀ là +1)
        btnThangTruoc.addActionListener(e -> doiThang(+1));
        btnThangSau.addActionListener(e -> doiThang(-1));

        // Bấm 1 tháng ở tab "Theo tháng" -> mở danh sách của tháng đó
        bangThang.getSelectionModel().addListSelectionListener(e -> {
            int dong = bangThang.getSelectedRow();
            if (!e.getValueIsAdjusting() && dong >= 0) {
                chonThang(dsThang.get(dong));
                tab.setSelectedIndex(0);
                bangThang.clearSelection();     // để lần sau bấm lại đúng tháng này vẫn được
            }
        });

        // Gõ tìm kiếm hoặc đổi bộ lọc -> lọc lại ngay
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                lamMoiGiaoDien();
            }
        });
        cboLocLoai.addActionListener(e -> lamMoiGiaoDien());
        cboLocThang.addActionListener(e -> lamMoiGiaoDien());
    }

    // ================= 3. THÊM / SỬA / XÓA =================

    private void them() {
        try {
            GiaoDich gd = quanLy.them(loaiDangChon(), DinhDang.docNgay(txtNgay.getText()),
                    DinhDang.docTien(txtSoTien.getText()), (String) cboDanhMuc.getSelectedItem(), txtMoTa.getText());
            luuVaLamMoi("Đã thêm giao dịch #" + gd.getId());
        } catch (IllegalArgumentException loi) {
            baoLoi(loi.getMessage());       // lỗi nhập liệu từ DinhDang hoặc GiaoDich
        }
    }

    private void capNhat() {
        try {
            int id = idDangChon;
            quanLy.sua(id, loaiDangChon(), DinhDang.docNgay(txtNgay.getText()),
                    DinhDang.docTien(txtSoTien.getText()), (String) cboDanhMuc.getSelectedItem(), txtMoTa.getText());
            luuVaLamMoi("Đã cập nhật giao dịch #" + id);
        } catch (IllegalArgumentException loi) {
            baoLoi(loi.getMessage());
        }
    }

    private void xoa() {
        int id = idDangChon;
        int traLoi = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa giao dịch #" + id + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (traLoi == JOptionPane.YES_OPTION) {
            quanLy.xoa(id);
            luuVaLamMoi("Đã xóa giao dịch #" + id);
        }
    }

    // Sau mỗi thao tác: lưu file -> cập nhật màn hình -> xóa trắng form
    private void luuVaLamMoi(String thongBao) {
        try {
            quanLy.luuDuLieu();
        } catch (IOException loi) {
            baoLoi("Không lưu được file dữ liệu: " + loi.getMessage());
        }
        napThang();
        lamMoiGiaoDien();
        lamMoiForm();
        lblThongBao.setText(thongBao);
    }

    // ================= 4. CẬP NHẬT HIỂN THỊ =================

    // Lọc theo bộ lọc rồi vẽ lại bảng, 3 thẻ tổng quan và biểu đồ
    private void lamMoiGiaoDien() {
        String loai = (cboLocLoai.getSelectedIndex() == 0) ? null : (String) cboLocLoai.getSelectedItem();
        List<GiaoDich> ds = quanLy.loc(txtTimKiem.getText(), loai, thangDangChon());

        moHinhBang.setRowCount(0);
        for (GiaoDich gd : ds) {
            moHinhBang.addRow(new Object[] {
                gd.getId(), DinhDang.ngay(gd.getNgay()), gd.getLoai(), gd.getDanhMuc(), gd.getMoTa(),
                DinhDang.tienCoDau(gd.tinhAnhHuong())
            });
        }

        long soDu = quanLy.soDu(ds);
        lblTongThu.setText(DinhDang.tien(quanLy.tongThu(ds)));
        lblTongChi.setText(DinhDang.tien(quanLy.tongChi(ds)));
        lblSoDu.setText(DinhDang.tien(soDu));
        lblSoDu.setForeground(soDu < 0 ? CAM : LAM);    // âm thì đổi sang màu cam

        bieuDo.setDuLieu(quanLy.chiTheoDanhMuc(ds));
    }

    // Nạp lại danh sách tháng: ô chọn tháng + bảng "Theo tháng". Vẫn giữ tháng đang chọn.
    private void napThang() {
        YearMonth thangCu = thangDangChon();
        dsThang = quanLy.cacThang();
        DefaultComboBoxModel<String> moHinh = new DefaultComboBoxModel<>();
        moHinh.addElement("Tất cả");
        moHinhThang.setRowCount(0);
        for (YearMonth thang : dsThang) {
            moHinh.addElement(DinhDang.thang(thang));

            List<GiaoDich> dsCuaThang = quanLy.loc("", null, thang);
            long thu = quanLy.tongThu(dsCuaThang);
            long chi = quanLy.tongChi(dsCuaThang);
            moHinhThang.addRow(new Object[] {
                DinhDang.thang(thang), DinhDang.tienCoDau(thu), DinhDang.tienCoDau(-chi),
                DinhDang.tienCoDau(thu - chi), DinhDang.tienCoDau(quanLy.soDuDenHetThang(thang))
            });
        }
        if (thangCu != null && dsThang.contains(thangCu)) {
            moHinh.setSelectedItem(DinhDang.thang(thangCu));
        }
        cboLocThang.setModel(moHinh);
    }

    // Vị trí 0 là "Tất cả", vị trí 1 trở đi ứng với dsThang
    private YearMonth thangDangChon() {
        int viTri = cboLocThang.getSelectedIndex();
        return (viTri <= 0) ? null : dsThang.get(viTri - 1);
    }

    private void chonThang(YearMonth thang) {
        cboLocThang.setSelectedIndex(dsThang.indexOf(thang) + 1);
    }

    private void doiThang(int buoc) {
        int viTriMoi = cboLocThang.getSelectedIndex() + buoc;
        if (viTriMoi >= 1 && viTriMoi < cboLocThang.getItemCount()) {
            cboLocThang.setSelectedIndex(viTriMoi);
        }
    }

    // Đưa dữ liệu của dòng đang chọn lên form
    private void chonDong() {
        int dong = bang.getSelectedRow();
        GiaoDich gd = (dong < 0) ? null : quanLy.timTheoId((Integer) moHinhBang.getValueAt(dong, 0));
        idDangChon = (gd == null) ? -1 : gd.getId();
        if (gd != null) {
            if (gd instanceof KhoanThu) {
                rdoThu.setSelected(true);
            } else {
                rdoChi.setSelected(true);
            }
            napDanhMuc();
            cboDanhMuc.setSelectedItem(gd.getDanhMuc());
            txtNgay.setText(DinhDang.ngay(gd.getNgay()));
            txtSoTien.setText(DinhDang.tien(gd.getSoTien()));
            txtMoTa.setText(gd.getMoTa());
            lblThongBao.setText(" ");
        }
        capNhatCheDo();
    }

    // Xóa trắng form, quay về chế độ Thêm
    private void lamMoiForm() {
        bang.clearSelection();
        idDangChon = -1;
        txtNgay.setText(DinhDang.ngay(LocalDate.now()));
        txtSoTien.setText("");
        txtMoTa.setText("");
        lblThongBao.setText(" ");
        napDanhMuc();
        capNhatCheDo();
    }

    // Chưa chọn dòng: chế độ Thêm. Đã chọn dòng: chế độ Sửa, bật nút Cập nhật và Xóa
    private void capNhatCheDo() {
        boolean dangSua = idDangChon >= 0;
        lblTieuDe.setText(dangSua ? "Sửa giao dịch #" + idDangChon : "Thêm giao dịch");
        btnCapNhat.setEnabled(dangSua);
        btnXoa.setEnabled(dangSua);
    }

    private void napDanhMuc() {
        cboDanhMuc.setModel(new DefaultComboBoxModel<>(rdoThu.isSelected() ? KhoanThu.DANH_MUC : KhoanChi.DANH_MUC));
    }

    private String loaiDangChon() {
        return rdoThu.isSelected() ? KhoanThu.LOAI : KhoanChi.LOAI;
    }

    private void baoLoi(String noiDung) {
        JOptionPane.showMessageDialog(this, noiDung, "Chưa đúng rồi", JOptionPane.WARNING_MESSAGE);
    }
}
