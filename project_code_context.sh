#!/bin/bash

OUTPUT_FILE="project_code_context.txt"

echo "PROJECT SOURCE CODE CONTEXT" > "$OUTPUT_FILE"
echo "===========================" >> "$OUTPUT_FILE"
echo "Oluşturulma Tarihi: $(date)" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Bulunduğun dizinden (.) aramaya başla
# 'target' klasörlerini HARIÇ TUT (Derlenmiş dosyalar)
# '.git' klasörünü HARIÇ TUT
# 'src/test' klasörünü HARIÇ TUT (Sadece ana kodları alalım)
find . -type f -name "*.java" \
  ! -path "*/target/*" \
  ! -path "*/.git/*" \
  ! -path "*/src/test/*" | sort | while read -r file; do
    echo "--------------------------------------------------------------------------------" >> "$OUTPUT_FILE"
    echo "FILE PATH: $file" >> "$OUTPUT_FILE"
    echo "--------------------------------------------------------------------------------" >> "$OUTPUT_FILE"
    cat "$file" >> "$OUTPUT_FILE"
    echo -e "\n\n" >> "$OUTPUT_FILE"
done

echo "✅ İşlem tamamlandı!"
echo "Tüm kaynak kodlar '$OUTPUT_FILE' dosyasına yazıldı."
echo "Lütfen bu dosyanın içeriğini kopyalayıp bana gönderin."