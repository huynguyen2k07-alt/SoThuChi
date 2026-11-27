#!/bin/sh
# Đóng gói Sổ Thu Chi thành app cài trên máy (Linux).
# Kết quả nằm trong thư mục dist/:
#   SoThuChi.jar          chạy được trên mọi máy có Java 11+ (bấm đúp hoặc: java -jar SoThuChi.jar)
#   sothuchi_*.deb        gói cài đặt cho Ubuntu/Debian (đã kèm sẵn Java, máy khác không cần cài Java)
#   SoThuChi/             app chạy ngay không cần cài: SoThuChi/bin/SoThuChi
set -e
cd "$(dirname "$0")"

TEN="SoThuChi"
PHIEN_BAN="1.0.0"

echo "==> Biên dịch mã nguồn"
rm -rf build dist
mkdir -p build/classes dist
javac -encoding UTF-8 --release 11 -d build/classes $(find src -name "*.java")

echo "==> Tạo file $TEN.jar"
jar --create --file "dist/$TEN.jar" --main-class sothuchi.Main -C build/classes .

if ! command -v jpackage >/dev/null 2>&1; then
    echo "Không có jpackage (cần JDK 17 trở lên), chỉ tạo được file .jar."
    exit 0
fi

mkdir -p build/input
cp "dist/$TEN.jar" build/input/

# Các tham số dùng chung cho jpackage
dong_goi() {
    jpackage "-J-Djava.io.tmpdir=${TMPDIR:-/tmp}" \
        --name "$TEN" --app-version "$PHIEN_BAN" --input build/input \
        --add-modules java.base,java.desktop,jdk.charsets \
        --main-jar "$TEN.jar" --main-class sothuchi.Main \
        --java-options "-Dfile.encoding=UTF-8" \
        --description "Sổ thu chi cá nhân" --vendor "VKU" \
        --icon packaging/sothuchi.png --dest dist "$@"
}

echo "==> Tạo app chạy ngay (dist/$TEN/)"
dong_goi --type app-image

echo "==> Tạo gói cài đặt .deb"
if command -v dpkg-deb >/dev/null 2>&1 && command -v fakeroot >/dev/null 2>&1; then
    dong_goi --type deb \
        --linux-package-name sothuchi --linux-shortcut --linux-menu-group Office \
        --linux-app-category office
else
    echo "Thiếu dpkg-deb hoặc fakeroot nên bỏ qua bước tạo .deb"
fi

echo
echo "Xong! Các file trong dist/:"
ls -1 dist
