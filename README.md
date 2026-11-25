# Sổ Thu Chi Cá Nhân (Java Swing)

Đồ án môn **Lập trình hướng đối tượng**: ứng dụng ghi lại các khoản thu và chi, tự tính số dư, tìm kiếm, lọc theo tháng và vẽ biểu đồ chi tiêu theo danh mục.

## Chức năng

- Thêm, sửa, xóa giao dịch (xóa có hỏi xác nhận).
- Bấm vào một dòng trong bảng thì dữ liệu hiện lên form để sửa.
- Ba thẻ tổng quan: **Tổng thu**, **Tổng chi**, **Số dư** (số dư âm sẽ chuyển sang màu cam).
- Tìm theo mô tả hoặc danh mục, lọc theo loại (Thu/Chi) và theo tháng.
- Trang **Thống kê**: biểu đồ cột cho biết tiền chi vào danh mục nào nhiều nhất.
- Kiểm tra dữ liệu nhập: số tiền phải là số lớn hơn 0, ngày phải đúng dạng `ngày/tháng/năm`.
- Tự lưu vào file `data/sothuchi.csv` sau mỗi thao tác. Lần chạy đầu tiên app tạo sẵn 12 giao dịch mẫu.

## Cách chạy

Cần **JDK 8 trở lên** (khuyên dùng JDK 17 hoặc 21).

**Trong VS Code**
1. Cài extension **Extension Pack for Java** (của Microsoft).
2. `File → Open Folder…` rồi chọn thư mục `SoThuChi`.
3. Mở `src/sothuchi/Main.java` và bấm **Run** phía trên hàm `main`, hoặc nhấn **F5**.

**Bằng Terminal**
- Linux / macOS: `./run.sh`
- Windows: bấm đúp vào `run.bat`

Muốn quay về dữ liệu mẫu ban đầu thì xóa file `data/sothuchi.csv` rồi chạy lại.

## Cấu trúc thư mục

```
src/sothuchi/
├── Main.java                 Điểm bắt đầu: đọc dữ liệu rồi mở cửa sổ
├── model/                    DỮ LIỆU  (phần OOP chính)
│   ├── GiaoDich.java         Lớp trừu tượng (abstract) cho 1 giao dịch
│   ├── KhoanThu.java         extends GiaoDich
│   └── KhoanChi.java         extends GiaoDich
├── data/                     LƯU TRỮ
│   ├── LuuTru.java           Interface: luu() và doc()
│   └── FileLuuTru.java       implements LuuTru, lưu ra file CSV
├── service/                  XỬ LÝ NGHIỆP VỤ
│   └── QuanLyThuChi.java     Thêm/sửa/xóa, lọc, tính tổng, thống kê
├── util/
│   └── DinhDang.java         Định dạng tiền "1.500.000 đ", đọc ngày, đọc số tiền
└── view/                     GIAO DIỆN
    ├── ManHinhChinh.java     Cửa sổ chính (extends JFrame)
    ├── KhungChao.java        Dải chào màu xanh ở trên cùng
    ├── TheTongQuan.java      Thẻ Tổng thu/chi/số dư (extends KhungBo)
    ├── BieuDoPanel.java      Biểu đồ tự vẽ (extends JPanel)
    └── thanhphan/            Các "viên gạch" giao diện tự vẽ cho đẹp, KHÔNG chứa logic
        ├── BangMau.java      Bảng màu + font dùng chung
        ├── HieuUng.java      Công cụ tạo chuyển động (dùng javax.swing.Timer)
        ├── KhungBo.java      JPanel bo góc có bóng mờ
        ├── NutBo.java        Nút bo tròn, rê chuột vào đổi màu mượt
        ├── NutChon.java      Nút công tắc (Khoản chi | Khoản thu, Danh sách | Thống kê)
        ├── ONhap.java        Ô nhập bo tròn có chữ gợi ý mờ
        ├── VienBo.java       Viền bo tròn, đổi màu khi ô được chọn
        ├── HopChon.java      Combo box bo tròn
        ├── BangGiaoDich.java JTable đã trang trí (nhãn Thu/Chi, tô sáng dòng khi rê chuột)
        ├── ThanhCuonMong.java Thanh cuộn mảnh
        ├── KhungChuyenCanh.java Chuyển trang có hiệu ứng mờ dần
        └── ThongBaoNho.java  Thông báo nhỏ trượt lên rồi mờ dần
```

Luồng chạy: **Giao diện (view)** gọi **QuanLyThuChi (service)**, service làm việc với các đối tượng **GiaoDich (model)** và nhờ **LuuTru (data)** ghi ra file. Giao diện không tự tính toán hay đọc/ghi file.

## Giao diện và hiệu ứng

Tông màu xanh lá và kem cho nhẹ mắt; khung, nút và ô nhập đều bo tròn. Tất cả hiệu ứng dùng chung lớp `HieuUng`: một `javax.swing.Timer` gọi lại mỗi 15ms, tính tiến độ `t` từ 0 đến 1, rồi cập nhật giá trị và vẽ lại.

| Hiệu ứng | Ở đâu |
| --- | --- |
| Số tiền ở 3 thẻ chạy dần tới giá trị mới | `TheTongQuan.setGiaTri()` |
| Cột biểu đồ mọc dài ra | `BieuDoPanel.chayHieuUng()` |
| Chuyển Danh sách ⇄ Thống kê mờ dần và trượt lên | `KhungChuyenCanh.chuyenToi()` |
| Nút đổi màu từ từ khi rê chuột | `NutBo.doiMau()` |
| Thông báo "Đã thêm giao dịch" trượt lên rồi biến mất | `ThongBaoNho.hien()` |

Các lớp trong `thanhphan` đều **kế thừa** thành phần Swing có sẵn (`JPanel`, `JButton`, `JTable`...) rồi **ghi đè** `paintComponent()` để tự vẽ. Đây cũng là một ví dụ về kế thừa và đa hình: Swing gọi `paintComponent()` mà không cần biết đó là lớp nào, và phiên bản của lớp con sẽ được chạy.

## Sơ đồ lớp

```
              <<abstract>>
               GiaoDich
   - id, ngay, soTien, danhMuc, moTa
   + getLoai() : String        {abstract}
   + tinhAnhHuong() : long     {abstract}
                 ▲
         ┌───────┴────────┐
     KhoanThu          KhoanChi
  getLoai()="Thu"    getLoai()="Chi"
  tinhAnhHuong()=+    tinhAnhHuong()=-

   <<interface>>                QuanLyThuChi
      LuuTru      ◄──dùng───   - danhSach : List<GiaoDich>
   + luu(ds)                   - luuTru : LuuTru
   + doc()                     + them / sua / xoa / loc
       ▲                       + tongThu / tongChi / soDu
       │ implements            + chiTheoDanhMuc
   FileLuuTru

   ManHinhChinh (JFrame) ──dùng──► QuanLyThuChi
   ├── KhungChao (JPanel)
   ├── TheTongQuan (KhungBo ◄ JPanel) x3
   └── BieuDoPanel (JPanel)
```

## 4 tính chất OOP nằm ở đâu?

| Tính chất | Trong code | Giải thích ngắn |
| --- | --- | --- |
| **Đóng gói** | `GiaoDich`: thuộc tính `private`, có getter/setter | `setSoTien()` từ chối số ≤ 0, nên không ai gán bậy được số tiền âm. |
| **Kế thừa** | `KhoanThu`, `KhoanChi` **extends** `GiaoDich`. Ngoài ra `ManHinhChinh extends JFrame`, `TheTongQuan extends KhungBo extends JPanel` (kế thừa nhiều tầng) | Lớp con dùng lại toàn bộ thuộc tính và getter/setter của lớp cha, chỉ viết thêm phần khác biệt. |
| **Đa hình** | `QuanLyThuChi.soDu()` gọi `gd.tinhAnhHuong()` | Cùng một lời gọi nhưng Thu trả về số dương, Chi trả về số âm. Không cần `if` kiểm tra loại. `BieuDoPanel` ghi đè `paintComponent()` cũng là đa hình. |
| **Trừu tượng** | `abstract class GiaoDich` và `interface LuuTru` | Không thể `new GiaoDich()`. `LuuTru` chỉ nói "phải lưu và đọc được", còn lưu bằng file hay database là việc của lớp cài đặt. |

## Câu hỏi thầy cô hay hỏi

**Tại sao `GiaoDich` là abstract class mà không phải interface?**
Vì Thu và Chi có chung thuộc tính (ngày, số tiền, danh mục, mô tả) và chung code kiểm tra dữ liệu. Abstract class chứa được thuộc tính và code dùng chung, còn interface thì không có thuộc tính.

**Interface `LuuTru` để làm gì?**
Để `QuanLyThuChi` không phụ thuộc vào cách lưu. Muốn đổi sang MySQL chỉ cần viết `DatabaseLuuTru implements LuuTru` rồi sửa chỗ tạo đối tượng lưu trữ trong `Main`. `QuanLyThuChi` và toàn bộ giao diện giữ nguyên.

**Đổi một giao dịch từ Chi sang Thu thì sao?**
Hàm `sua()` tạo đối tượng mới đúng lớp (`KhoanThu`) với cùng mã, rồi thay vào đúng vị trí cũ trong danh sách.

**Tại sao số tiền dùng `long` mà không dùng `double`?**
Tiền Việt không có phần lẻ. `double` có thể sinh sai số khi cộng (ví dụ `0.1 + 0.2`), còn `long` thì cộng luôn chính xác.

**Dữ liệu lưu ở đâu?**
Ở file `data/sothuchi.csv`, mỗi dòng một giao dịch: `loai;id;ngay;soTien;danhMuc;moTa`. Mở bằng Notepad là đọc được.
