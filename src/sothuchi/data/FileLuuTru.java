package sothuchi.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import sothuchi.model.GiaoDich;

// Lưu giao dịch vào file CSV. Mỗi dòng: loai;id;ngay;soTien;danhMuc;moTa
public class FileLuuTru implements LuuTru {

    private final Path file;

    public FileLuuTru(String tenFile) {
        this.file = Paths.get(tenFile);
    }

    public boolean tonTai() {
        return Files.exists(file);
    }

    @Override
    public void luu(List<GiaoDich> danhSach) throws IOException {
        List<String> cacDong = new ArrayList<>();
        for (GiaoDich gd : danhSach) {
            cacDong.add(gd.getLoai() + ";" + gd.getId() + ";" + gd.getNgay() + ";" + gd.getSoTien()
                    + ";" + lamSach(gd.getDanhMuc()) + ";" + lamSach(gd.getMoTa()));
        }
        Files.createDirectories(file.toAbsolutePath().getParent());   // tạo thư mục data/ nếu chưa có
        Files.write(file, cacDong, StandardCharsets.UTF_8);
    }

    @Override
    public List<GiaoDich> doc() throws IOException {
        List<GiaoDich> danhSach = new ArrayList<>();
        if (!tonTai()) {
            return danhSach;
        }
        for (String dong : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String[] cot = dong.split(";", -1);
            if (cot.length < 6) {
                continue;               // bỏ qua dòng trống hoặc thiếu cột
            }
            try {
                danhSach.add(GiaoDich.tao(cot[0], Integer.parseInt(cot[1]), LocalDate.parse(cot[2]),
                        Long.parseLong(cot[3]), cot[4], cot[5]));
            } catch (RuntimeException e) {
                System.err.println("Bỏ qua dòng lỗi: " + dong);
            }
        }
        return danhSach;
    }

    // Đổi dấu ";" và xuống dòng trong chữ người dùng nhập để không làm hỏng file
    private String lamSach(String chu) {
        return chu.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }
}
