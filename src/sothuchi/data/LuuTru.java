package sothuchi.data;

import java.io.IOException;
import java.util.List;

import sothuchi.model.GiaoDich;

// Interface: chỉ quy định "lưu được" và "đọc được", không quan tâm lưu ở đâu.
// Muốn đổi sang database thì viết lớp mới implements LuuTru, không phải sửa chỗ khác.
public interface LuuTru {

    void luu(List<GiaoDich> danhSach) throws IOException;

    List<GiaoDich> doc() throws IOException;
}
