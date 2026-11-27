@echo off
chcp 65001 >nul
rem Đóng gói Sổ Thu Chi trên Windows (cần JDK 17 trở lên).
rem Kết quả trong thư mục dist\:
rem   SoThuChi.jar      chạy trên máy có Java (bấm đúp)
rem   SoThuChi\         app chạy ngay: SoThuChi\SoThuChi.exe (đã kèm Java)
rem   SoThuChi-*.exe    bộ cài đặt (chỉ tạo được nếu máy có cài WiX Toolset)
cd /d "%~dp0"

if exist build rmdir /s /q build
if exist dist rmdir /s /q dist
mkdir build\classes build\input dist

echo ==^> Bien dich ma nguon
dir /s /b src\*.java > build\sources.txt
javac -encoding UTF-8 --release 11 -d build\classes @build\sources.txt || goto :loi

echo ==^> Tao file SoThuChi.jar
jar --create --file dist\SoThuChi.jar --main-class sothuchi.Main -C build\classes . || goto :loi
copy dist\SoThuChi.jar build\input\ >nul

echo ==^> Tao app chay ngay
jpackage --type app-image --name SoThuChi --app-version 1.0.0 --input build\input ^
  --main-jar SoThuChi.jar --main-class sothuchi.Main --java-options -Dfile.encoding=UTF-8 ^
  --icon packaging\sothuchi.ico --dest dist || goto :loi

echo ==^> Tao bo cai dat .exe (bo qua neu chua co WiX)
jpackage --type exe --name SoThuChi --app-version 1.0.0 --input build\input ^
  --main-jar SoThuChi.jar --main-class sothuchi.Main --java-options -Dfile.encoding=UTF-8 ^
  --icon packaging\sothuchi.ico --dest dist --win-shortcut --win-menu --win-dir-chooser ^
  || echo (Khong tao duoc .exe - can cai WiX Toolset. Van dung duoc thu muc dist\SoThuChi)

echo.
echo Xong! Xem thu muc dist\
pause
exit /b 0

:loi
echo Co loi khi dong goi.
pause
exit /b 1
