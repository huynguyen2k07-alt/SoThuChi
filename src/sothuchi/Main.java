package sothuchi;

import java.io.IOException;

import sothuchi.data.FileLuuTru;
import sothuchi.service.QuanLyThuChi;
import sothuchi.view.MenuConsole;

// Điểm bắt đầu: đọc dữ liệu từ file rồi mở menu trên terminal.
public class Main {

    public static void main(String[] args) {
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
            System.out.println("Không đọc được file dữ liệu: " + e.getMessage());
        }

        new MenuConsole(quanLy).chay();
    }
}
