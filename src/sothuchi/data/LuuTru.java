package sothuchi.data;

import java.io.IOException;
import java.util.List;

import sothuchi.model.GiaoDich;

// Interface lưu trữ: chỉ quy định luu() và doc().
// Muốn đổi sang database thì viết lớp mới implements LuuTru.
public interface LuuTru {

    void luu(List<GiaoDich> danhSach) throws IOException;

    List<GiaoDich> doc() throws IOException;
}
