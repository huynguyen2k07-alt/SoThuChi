# Sổ Thu Chi Cá Nhân (Java, chạy trên terminal)

App ghi thu/chi, tự tính số dư, xem theo tháng và thống kê chi tiêu theo danh mục.
Không có giao diện cửa sổ: mọi thao tác là gõ số chọn trong menu.

## Chức năng

```
===== SỔ THU CHI - Tháng 11/2026 =====
1. Xem giao dịch trong tháng      (kèm tổng thu, tổng chi, số dư)
2. Thêm khoản thu
3. Thêm khoản chi
4. Sửa giao dịch                  (Enter để giữ nguyên giá trị cũ)
5. Xóa giao dịch                  (hỏi lại trước khi xóa)
6. Tìm kiếm                       (theo mô tả hoặc danh mục)
7. Thống kê chi theo danh mục     (biểu đồ cột bằng dấu #)
8. Tổng kết theo tháng            (số dư cuối tháng cộng dồn tháng trước)
9. Đổi tháng đang xem
0. Thoát
```

- Mở app là xem tháng hiện tại.
- Tự lưu vào `data/sothuchi.csv` sau mỗi lần thêm/sửa/xóa (chưa có file thì tạo dữ liệu mẫu).
- Nhập sai (số tiền có chữ, ngày 31/02, tháng 13…) thì báo lỗi rồi quay lại menu.

## Chạy

Cần JDK 11+. Chạy `./run.sh` (Linux/macOS), `run.bat` (Windows), hoặc mở `src/sothuchi/Main.java` trong VS Code và nhấn F5.

## Cấu trúc (9 file)

```
src/sothuchi/
├── Main.java               Mở app: đọc file dữ liệu rồi chạy menu
├── model/                  DỮ LIỆU
│   ├── GiaoDich.java       Lớp trừu tượng (abstract)
│   ├── KhoanThu.java       extends GiaoDich, tinhAnhHuong() = +soTien
│   └── KhoanChi.java       extends GiaoDich, tinhAnhHuong() = -soTien
├── data/                   LƯU TRỮ
│   ├── LuuTru.java         Interface: luu() và doc()
│   └── FileLuuTru.java     implements LuuTru, lưu file CSV
├── service/
│   └── QuanLyThuChi.java   Nghiệp vụ: thêm/sửa/xóa, lọc, tính tổng, thống kê
├── util/
│   └── DinhDang.java       Định dạng tiền, đọc ngày/tháng
└── view/
    └── MenuConsole.java    Menu trên terminal (Scanner)
```

Luồng: `view` → `service` → `model` + `data`.

## Gợi ý thứ tự thuyết trình

1. **Chạy app** cho xem: chọn 1 (xem tháng), 3 (thêm khoản chi), 4 (sửa), 5 (xóa), 7 (thống kê), 8 (tổng kết theo tháng).
2. **`GiaoDich`** – lớp trừu tượng: thuộc tính `private` + getter (đóng gói), kiểm tra dữ liệu trong constructor, 2 hàm `abstract`.
3. **`KhoanThu` / `KhoanChi`** – kế thừa (`extends`, `super(...)`), mỗi lớp viết lại `tinhAnhHuong()` theo cách riêng.
4. **`QuanLyThuChi.soDu()`** – đa hình: cộng `tinhAnhHuong()` của mọi giao dịch, không cần if/else Thu hay Chi. `soDuDenHetThang()` dùng cùng cách để tính số dư chuyển sang tháng sau.
5. **`LuuTru` / `FileLuuTru`** – interface: service chỉ biết `LuuTru`, muốn đổi sang database chỉ cần viết lớp mới.
6. **`MenuConsole`** – đọc theo 4 phần có tiêu đề: vòng lặp menu → xem → thêm/sửa/xóa → hàm phụ đọc dữ liệu nhập. Lỗi nhập sai được bắt bằng `try/catch IllegalArgumentException`.
