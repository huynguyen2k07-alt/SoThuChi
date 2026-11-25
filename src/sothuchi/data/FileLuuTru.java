package sothuchi.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import sothuchi.model.GiaoDich;

// Lưu/đọc giao dịch bằng file CSV, mỗi dòng: loai;id;ngay;soTien;danhMuc;moTa
public class FileLuuTru implements LuuTru {

    private static final String PHAN_CACH = ";";
    private final Path duongDan;

    public FileLuuTru(String tenFile) {
        this.duongDan = Paths.get(tenFile);
    }

    public boolean tonTai() {
        return Files.exists(duongDan);
    }

    @Override
    public void luu(List<GiaoDich> danhSach) throws IOException {
        // Tạo thư mục nếu chưa có
        Path thuMuc = duongDan.toAbsolutePath().getParent();
        if (thuMuc != null) {
            Files.createDirectories(thuMuc);
        }

        try (BufferedWriter ghi = Files.newBufferedWriter(duongDan, StandardCharsets.UTF_8)) {
            for (GiaoDich gd : danhSach) {
                String dong = gd.getLoai() + PHAN_CACH
                        + gd.getId() + PHAN_CACH
                        + gd.getNgay() + PHAN_CACH
                        + gd.getSoTien() + PHAN_CACH
                        + lamSach(gd.getDanhMuc()) + PHAN_CACH
                        + lamSach(gd.getMoTa());
                ghi.write(dong);
                ghi.newLine();
            }
        }
    }

    @Override
    public List<GiaoDich> doc() throws IOException {
        List<GiaoDich> ketQua = new ArrayList<>();
        if (!tonTai()) {
            return ketQua;   // chưa có file thì trả về danh sách rỗng
        }

        try (BufferedReader doc = Files.newBufferedReader(duongDan, StandardCharsets.UTF_8)) {
            String dong;
            int soDong = 0;
            while ((dong = doc.readLine()) != null) {
                soDong++;
                if (dong.trim().isEmpty()) {
                    continue;
                }
                String[] cot = dong.split(PHAN_CACH, -1);
                if (cot.length < 6) {
                    System.err.println("Bỏ qua dòng " + soDong + " vì thiếu cột: " + dong);
                    continue;
                }
                try {
                    GiaoDich gd = GiaoDich.tao(
                            cot[0],
                            Integer.parseInt(cot[1]),
                            LocalDate.parse(cot[2]),
                            Long.parseLong(cot[3]),
                            cot[4],
                            cot[5]);
                    ketQua.add(gd);
                } catch (RuntimeException e) {
                    System.err.println("Bỏ qua dòng " + soDong + " bị lỗi: " + e.getMessage());
                }
            }
        }
        return ketQua;
    }

    /** Bỏ ";" và xuống dòng để không hỏng file. */
    private String lamSach(String s) {
        return s.replace(PHAN_CACH, ",").replace("\n", " ").replace("\r", " ");
    }
}
