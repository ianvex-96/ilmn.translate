# ilmn.translate

Aplikasi Android untuk terjemahan layar dan terjemahan teks terpilih.

## Fitur

- **Terjemahan Layar (Overlay)** - Tampilkan terjemahan sebagai overlay yang bisa dipindah di atas aplikasi lain
- **Terjemahan Teks Terpilih** - Sorot teks di aplikasi manapun, lalu pilih "Terjemahkan" dari menu konteks untuk menerjemahkan
- **On-Device Translation** - Menggunakan Google ML Kit Translate untuk terjemahan offline (setelah download model)
- **Multi Bahasa** - Mendukung berbagai bahasa populer
- **Deteksi Otomatis** - Mendeteksi bahasa sumber secara otomatis

## Cara Pakai

### Terjemahan Layar (Overlay)
1. Buka aplikasi ilmn.translate
2. Beri izin "Tampilkan di atas aplikasi lain"
3. Tekan "Mulai Terjemahan Layar"
4. Overlay akan muncul dan bisa dipindah-pindah
5. Tekan X untuk menghentikan

### Terjemahan Teks Terpilih
1. Buka aplikasi apapun (browser, chat, dll)
2. Sorot/pilih teks yang ingin diterjemahkan
3. Pada menu konteks, pilih "Terjemahkan" (atau "ilmn.translate")
4. Aplikasi akan muncul dengan hasil terjemahan
5. Bisa salin hasil terjemahan atau tutup

## Build

```bash
cd ilmn.translate
./gradlew assembleDebug
```

APK akan tersedia di `app/build/outputs/apk/debug/app-debug.apk`

## Teknologi

- Kotlin
- Android SDK 24+
- Google ML Kit Translate
- Google ML Kit Language Identification
- Material Design 3

## Catatan

- Untuk terjemahan on-device, model bahasa perlu diunduh saat pertama kali digunakan (disarankan pakai WiFi)
- Izin `SYSTEM_ALERT_WINDOW` diperlukan untuk fitur overlay
- Izin `POST_NOTIFICATIONS` diperlukan untuk Android 13+
- Untuk fitur "Terjemahkan" di menu konteks, Android akan otomatis menampilkannya saat teks dipilih (berkat `ACTION_PROCESS_TEXT`)
