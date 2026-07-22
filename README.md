# Snapar Android

Қазақстанға бағытталған AI travel guide қолданбасының native Android MVP нұсқасы.

## Скриншот

<img src="docs/screenshots/discover-screen.png" alt="Snapar Discover screen" width="320" />

## Жүктемей-ақ тексеру (браузерде ашу)

**[Try it live on Appetize.io →](https://appetize.io/app/b_w3ymx4xn6gun7gpqvwfi6xmcju)**

Сілтемені ашыңыз, "Tap to Start" басыңыз — қосымша браузерде виртуалды Android телефонда (Pixel 7, Android 13) тікелей іске қосылады. Ешнәрсе орнатудың қажеті жоқ.

## Технология

- Kotlin
- Jetpack Compose + Material 3
- Coil
- Android SDK 35
- Min SDK 26

## MVP экрандары

- Discover: іздеу, категориялар, ұсыныстар, лайк және сақтау
- Destination Detail: толық ақпарат, маусым, бюджет, SAI әрекеті
- Routes: карта, бағыт карточкалары, AI Route Builder
- GeoSnap: community feed және телефоннан фото таңдау
- Passport: XP, Қазақстан картасы, статистика және жетістіктер
- Profile: статистика, тіл/хабарлама баптаулары, бизнес режим
- SAI: интерактивті демо чат және жылдам сұраулар
- Live Weather: Open-Meteo арқылы нақты уақыттағы ауа райы және 7 күндік болжам

## Іске қосу

Жобаны Android Studio арқылы ашыңыз:

```text
C:\Users\Lenovo\OneDrive\Desktop\travel\snapar-android
```

Немесе PowerShell арқылы:

```powershell
.\gradlew.bat :app:assembleDebug
```

Debug APK:

```text
artifacts\Snapar-debug.apk
```

Телефон USB debugging арқылы қосылғанда:

```powershell
C:\Users\Lenovo\AppData\Local\Android\Sdk\platform-tools\adb.exe install -r artifacts\Snapar-debug.apk
```

Толық өнімдік жоспар: [docs/IMPLEMENTATION_PLAN.md](docs/IMPLEMENTATION_PLAN.md)

## Cloud SAI қосу

Қосымша API кілтін APK ішіне сақтамайды. Нақты LLM қолдану үшін кілт серверде тұруы керек,
ал Android тек қауіпсіз HTTPS endpoint-ке сұраныс жібереді:

```powershell
.\gradlew.bat :app:assembleDebug -PSNAPAR_SAI_BACKEND_URL=https://example.com/api/sai
```

Endpoint `POST {"message":"...","language":"kk","app":"Snapar"}` қабылдап,
`{"reply":"..."}` қайтарады. URL берілмесе, SAI толық офлайн жоспарлаушымен жұмыс істейді.
