#!/bin/sh
# Biên dịch và chạy Sổ Thu Chi (Linux / macOS)
cd "$(dirname "$0")" || exit 1
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java") && java -cp bin sothuchi.Main
