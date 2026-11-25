package sothuchi;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

import sothuchi.data.FileLuuTru;
import sothuchi.service.QuanLyThuChi;
import sothuchi.view.ManHinhChinh;
import sothuchi.view.thanhphan.BangMau;

// Điểm bắt đầu: đọc dữ liệu rồi mở cửa sổ chính.
public class Main {

    private static final String FILE_DU_LIEU = "data/sothuchi.csv";

    public static void main(String[] args) {
        // Làm mịn chữ (đặt trước khi tạo giao diện)
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Chạy giao diện trên luồng Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            caiDatGiaoDien();

            FileLuuTru luuTru = new FileLuuTru(FILE_DU_LIEU);
            QuanLyThuChi quanLy = new QuanLyThuChi(luuTru);

            try {
                if (luuTru.tonTai()) {
                    quanLy.taiDuLieu();
                } else {
                    // Lần đầu chạy: tạo dữ liệu mẫu
                    quanLy.taoDuLieuMau();
                    quanLy.luuDuLieu();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Không đọc được file dữ liệu: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            new ManHinhChinh(quanLy).setVisible(true);
        });
    }

    /** Cài font và màu chung cho Swing. */
    private static void caiDatGiaoDien() {
        try {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // nếu lỗi thì giữ giao diện mặc định
        }

        // Font chung
        FontUIResource font = new FontUIResource(BangMau.font(Font.PLAIN, 14));
        for (Object khoa : new ArrayList<>(Collections.list(UIManager.getDefaults().keys()))) {
            if (UIManager.get(khoa) instanceof FontUIResource) {
                UIManager.put(khoa, font);
            }
        }

        // Hộp thoại: nền kem, nút phẳng
        ColorUIResource nen = new ColorUIResource(BangMau.NEN);
        UIManager.put("OptionPane.background", nen);
        UIManager.put("Panel.background", nen);
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(BangMau.CHU));
        UIManager.put("Button.gradient", null);
        UIManager.put("Button.background", new ColorUIResource(java.awt.Color.WHITE));
        UIManager.put("Button.select", new ColorUIResource(BangMau.XANH_NHAT));
        UIManager.put("Button.focus", new ColorUIResource(BangMau.XANH));

        // Việt hóa nút hộp thoại
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.cancelButtonText", "Hủy");
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
    }
}
