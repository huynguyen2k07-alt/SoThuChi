package sothuchi;

import java.awt.Font;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import sothuchi.data.FileLuuTru;
import sothuchi.service.QuanLyThuChi;
import sothuchi.view.ManHinhChinh;

// Điểm bắt đầu: đọc dữ liệu từ file rồi mở cửa sổ chính.
public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");   // chữ mịn hơn

        SwingUtilities.invokeLater(() -> {
            caiGiaoDien();

            FileLuuTru file = new FileLuuTru("data/sothuchi.csv");
            QuanLyThuChi quanLy = new QuanLyThuChi(file);
            try {
                if (file.tonTai()) {
                    quanLy.taiDuLieu();
                } else {                       // lần đầu chạy: tạo dữ liệu mẫu
                    quanLy.taoDuLieuMau();
                    quanLy.luuDuLieu();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Không đọc được file dữ liệu: " + e.getMessage());
            }

            new ManHinhChinh(quanLy).setVisible(true);
        });
    }

    // Dùng giao diện Nimbus có sẵn của Java, chữ cỡ 14, nút hộp thoại tiếng Việt.
    private static void caiGiaoDien() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            UIManager.getLookAndFeelDefaults().put("defaultFont", new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        } catch (Exception e) {
            // lỗi thì giữ giao diện mặc định
        }
        UIManager.put("OptionPane.yesButtonText", "Có");
        UIManager.put("OptionPane.noButtonText", "Không");
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
    }
}
