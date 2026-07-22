package kz.snapar.app.data

import kz.snapar.app.BuildConfig
import kz.snapar.app.model.AppLanguage
import kz.snapar.app.model.TravelRoute
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class SaiReply(
    val text: String,
    val route: TravelRoute? = null,
    val source: String = "offline",
)

class SaiRepository {
    suspend fun answer(prompt: String, language: AppLanguage): SaiReply {
        val local = localAnswer(prompt, language)
        if (BuildConfig.SAI_BACKEND_URL.isBlank()) return local
        return withContext(Dispatchers.IO) { runCatching {
            val connection = URL(BuildConfig.SAI_BACKEND_URL).openConnection() as HttpURLConnection
            connection.connectTimeout = 12_000
            connection.readTimeout = 20_000
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            val body = JSONObject()
                .put("message", prompt)
                .put("language", language.code)
                .put("app", "Snapar")
                .toString()
            connection.outputStream.bufferedWriter().use { it.write(body) }
            try {
                if (connection.responseCode !in 200..299) error("SAI backend ${connection.responseCode}")
                val response = connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
                local.copy(
                    text = response.optString("reply").ifBlank { local.text },
                    source = "cloud",
                )
            } finally {
                connection.disconnect()
            }
        }.getOrElse { local } }
    }

    private fun localAnswer(prompt: String, language: AppLanguage): SaiReply {
        val lower = prompt.lowercase()
        val days = Regex("""\b([1-7])\s*(күн|дн|день|day)?""")
            .find(lower)?.groupValues?.getOrNull(1)?.toIntOrNull()?.coerceIn(1, 5) ?: 3
        val budget = parseBudget(lower)
        val origin = listOf(
            "Алматы", "Астана", "Шымкент", "Қарағанды", "Түркістан", "Бурабай",
            "Ақтау", "Атырау", "Семей", "Өскемен",
            "Astana", "Almaty", "Shymkent", "Aktau", "Burabay",
        ).firstOrNull { lower.contains(it.lowercase()) }
            ?: local(language, "Алматы", "Алматы", "Almaty")

        val interests = buildSet {
            if (hasAny(lower, "отбас", "семей", "family", "балалар", "дети")) add("family")
            if (hasAny(lower, "фото", "photo", "камера", "сурет")) add("photo")
            if (hasAny(lower, "adventure", "белсенді", "экстрим", "приключ")) add("adventure")
            if (hasAny(lower, "этно", "мәдени", "культур", "history", "тарих", "ясауи", "түркістан")) add("culture")
            if (hasAny(lower, "табиғат", "природ", "nature", "тау", "көл", "орман")) add("nature")
            if (hasAny(lower, "шаңғы", "лыж", "ski", "шымбұлақ", "shymbulak")) add("adventure")
            if (hasAny(lower, "кемпинг", "camping", "маңғыстау", "каспий", "caspian")) add("adventure")
            if (isEmpty()) add("nature")
        }

        val namedPlace = SampleData.places.firstOrNull { place ->
            listOf(place.name.kk, place.name.ru, place.name.en).any { lower.contains(it.lowercase()) }
        }

        // Арнайы орын сұрақтары
        if (hasAny(lower, "бурабай", "бурабай", "borovoe", "burabay")) {
            val p = SampleData.places.firstOrNull { it.id == 7 } ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "Бурабай — Қазақстанның ең сұлу орандарының бірі! ${p.distanceKm} км (${p.travelMinutes / 60} сағ). Ең жақсы маусым: ${p.bestSeason.kk}. Орташа бюджет: ${formatBudget(p.averageCost)}. Отбасылық демалысқа өте қолайлы.",
                "Бурабай — одно из красивейших мест Казахстана! ${p.distanceKm} км (${p.travelMinutes / 60} ч). Лучший сезон: ${p.bestSeason.ru}. Средний бюджет: ${formatBudget(p.averageCost)}. Идеально для семейного отдыха.",
                "Burabay is one of Kazakhstan's most beautiful destinations! ${p.distanceKm} km (${p.travelMinutes / 60} hrs). Best season: ${p.bestSeason.en}. Avg budget: ${formatBudget(p.averageCost)}. Perfect for family trips.",
            ))
        }

        if (hasAny(lower, "байтерек", "baiterek", "астана", "нур-султан", "нурсултан")) {
            val p = SampleData.places.firstOrNull { it.id == 8 } ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "Астана — Қазақстанның заманауи астанасы! Байтерек мұнарасы (${p.averageCost} ₸), Ханшатыр, Expo алаңы міндетті. Авиарейс шамамен 50 000 ₸ (Алматыдан). Ең жақсы маусым — маусым-тамыз.",
                "Астана — современная столица Казахстана! Обязательно посетите Байтерек (${p.averageCost} ₸), Хан-Шатыр, площадь EXPO. Авиабилет ~50 000 ₸ из Алматы. Лучший сезон — июнь–август.",
                "Astana is Kazakhstan's modern capital! Must-see: Baiterek Tower (${p.averageCost} ₸), Khan Shatyr, EXPO site. Flight ~50,000 ₸ from Almaty. Best season is June–August.",
            ))
        }

        if (hasAny(lower, "маңғыстау", "мангистау", "mangystau", "actau", "ақтау", "актау")) {
            val p = SampleData.places.firstOrNull { it.id == 12 } ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "Маңғыстау — ғаламат пейзаждар! Ай бетіндей ақ жартастар, жерасты мешіттер, Каспий жағалауы. ${p.distanceKm} км. Ең жақсы маусым: ${p.bestSeason.kk}. Ұшумен + джип жалдаумен барған дұрыс.",
                "Мангистау — фантастические пейзажи! Белые скалы, подземные мечети, Каспийское море. ${p.distanceKm} км. Лучший сезон: ${p.bestSeason.ru}. Лучше лететь + арендовать джип.",
                "Mangystau — fantastic landscapes! White chalk cliffs, underground mosques, Caspian Sea. ${p.distanceKm} km. Best season: ${p.bestSeason.en}. Best to fly + rent a jeep.",
            ))
        }

        if (hasAny(lower, "түркістан", "туркестан", "turkistan", "ясауи", "yasawi")) {
            val p = SampleData.places.firstOrNull { it.id == 11 } ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "Түркістан — рухани қала. Қожа Ахмет Ясауи кесенесі ЮНЕСКО тізімінде! ${p.distanceKm} км, Алматыдан ұшатын рейс бар. Орташа шығын: ${formatBudget(p.averageCost)}. Наурыз-маусым ең қолайлы.",
                "Туркестан — духовный город. Мавзолей Ходжи Ахмеда Ясауи в списке ЮНЕСКО! ${p.distanceKm} км, есть прямые рейсы из Алматы. Средний бюджет: ${formatBudget(p.averageCost)}. Лучше март–июнь.",
                "Turkistan is a spiritual city. Khoja Ahmed Yasawi mausoleum is UNESCO listed! ${p.distanceKm} km, direct flights from Almaty. Budget: ${formatBudget(p.averageCost)}. Best: March–June.",
            ))
        }

        if (hasAny(lower, "алтын-емел", "алтын емел", "altyn emel", "ән құм", "singing dune")) {
            return SaiReply(local(
                language,
                "Алтын-Емел ұлттық паркі — Қазақстанның кереметі! Ән қумдары желде «ырлайды». Алматыдан 175 км. Маусым: наурыз–мамыр немесе қыркүйек–қараша. Мәшине жалдау ұсынылады.",
                "Алтын-Эмель — чудо Казахстана! Поющий бархан «поёт» на ветру. 175 км от Алматы. Сезон: март–май или сентябрь–ноябрь. Рекомендуется аренда авто.",
                "Altyn-Emel is a Kazakhstan wonder! The Singing Dune 'sings' in the wind. 175 km from Almaty. Season: March–May or September–November. Car rental recommended.",
            ))
        }

        if (hasAny(lower, "ауа рай", "погода", "weather", "температур")) {
            val place = namedPlace ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "${place.name.kk}: қазір шамамен ${place.weather.temperature}°C, ${place.weather.condition.kk.lowercase()}. Жел ${place.weather.windKmh} км/с. Нақты жаңартуды орын бетінен көре аласыз.",
                "${place.name.ru}: сейчас около ${place.weather.temperature}°C, ${place.weather.condition.ru.lowercase()}. Ветер ${place.weather.windKmh} км/ч. Точный прогноз — на странице места.",
                "${place.name.en}: around ${place.weather.temperature}°C and ${place.weather.condition.en.lowercase()}, wind ${place.weather.windKmh} km/h. Live data on the destination page.",
            ))
        }

        if (hasAny(lower, "фото", "камера", "сурет", "ракурс", "photo", "photography")) {
            val place = namedPlace ?: SampleData.places.first()
            return SaiReply(local(
                language,
                "${place.name.kk} үшін ең жұмсақ жарық таң шыққаннан кейінгі алғашқы сағатта болады. Кең бұрышты объектив қолданып, суға немесе аңғарға қарай бағыттаңыз.",
                "Для ${place.name.ru} мягчайший свет — в первый час после рассвета. Снимайте широкоугольным объективом, направляя вниз к воде или ущелью.",
                "At ${place.name.en}, the softest light is in the first hour after sunrise. Use a wide lens aimed down toward water or the valley floor.",
            ))
        }

        if (hasAny(lower, "жақын", "рядом", "near", "бүгін", "сегодня", "бір күн", "однодневный", "day trip")) {
            val nearest = SampleData.places.sortedBy { it.distanceKm }.take(3)
            return SaiReply(local(
                language,
                "Жақын нұсқалар: ${nearest.joinToString { "${it.name.kk} (${it.distanceKm} км)" }}. Бір күнге Медеу немесе Шымбұлақ ең ыңғайлы.",
                "Ближайшие: ${nearest.joinToString { "${it.name.ru} (${it.distanceKm} км)" }}. Для однодневной поездки лучше всего Медеу или Шымбулак.",
                "Nearby: ${nearest.joinToString { "${it.name.en} (${it.distanceKm} km)" }}. For a day trip, Medeu or Shymbulak are easiest.",
            ))
        }

        if (hasAny(lower, "отбас", "балалар", "семей", "family", "children", "kids")) {
            val family = SampleData.places.filter { p -> p.tags.any { it.en.contains("Family", true) || it.kk.contains("Отбасылық") } }
            val names = family.take(3).joinToString { it.name.value(language) }
            return SaiReply(local(
                language,
                "Отбасымен демалысқа: $names. Бурабай өте ұсынылады — таза, қауіпсіз, инфрақұрылымы бар.",
                "Для семейного отдыха: $names. Особенно рекомендуется Бурабай — чисто, безопасно, хорошая инфраструктура.",
                "Great family destinations: $names. Burabay is highly recommended — clean, safe and well-equipped.",
            ))
        }

        val routeRequest = hasAny(lower, "күн", "день", "day", "тур", "route", "маршрут", "бюджет", "budget", "саяхат", "поездка", "trip")
        if (routeRequest) {
            val route = SampleData.generateRoute(origin, days, budget, interests)
            return SaiReply(
                text = local(
                    language,
                    "Дайын! $origin қаласынан $days күнге ${formatBudget(budget)} бюджетпен бағыт құрдым. Көлік, тамақ, қону және ауа райы жоспарда көрсетілді.",
                    "Готово! Маршрут из $origin на $days дн. с бюджетом ${formatBudget(budget)}. Транспорт, питание, ночлег и погода включены в план.",
                    "Done! A $days-day route from $origin with a ${formatBudget(budget)} budget. Transport, meals, lodging and weather included.",
                ),
                route = route,
            )
        }

        return SaiReply(local(
            language,
            "Мен маршрут, ауа райы, бюджет, орын сипаттамасы және фото ракурсы бойынша көмектесемін.\n\nМысалы:\n• «Алматыдан 3 күнге 60 000 ₸ табиғат турын құр»\n• «Бурабайда не бар?»\n• «Маңғыстауға қалай баруға болады?»",
            "Я помогу с маршрутом, погодой, бюджетом, описанием мест и фоторакурсами.\n\nНапример:\n• «Создай тур на природу из Алматы на 3 дня за 60 000 ₸»\n• «Что есть в Бурабае?»\n• «Как добраться до Мангистау?»",
            "I help with routes, weather, budgets, place info and photo tips.\n\nTry:\n• 'Build a 3-day nature trip from Almaty for 60,000 ₸'\n• 'What's there to do in Burabay?'\n• 'How do I get to Mangystau?'",
        ))
    }

    private fun parseBudget(text: String): Int {
        val match = Regex("""(\d[\d\s]{2,8})\s*(₸|тг|тенге|k|мың|тыс)?""").findAll(text).toList().lastOrNull()
        val raw = match?.groupValues?.getOrNull(1)?.replace(" ", "")?.toIntOrNull() ?: 75_000
        val suffix = match?.groupValues?.getOrNull(2).orEmpty()
        if (raw < 1000 && suffix.isBlank()) return 75_000
        return (if (suffix in setOf("k", "мың", "тыс") && raw < 1000) raw * 1000 else raw)
            .coerceIn(15_000, 1_000_000)
    }

    private fun hasAny(text: String, vararg needles: String): Boolean = needles.any { text.contains(it) }
    private fun formatBudget(value: Int): String = "%,d ₸".format(value).replace(',', ' ')
    private fun local(language: AppLanguage, kk: String, ru: String, en: String) = when (language) {
        AppLanguage.Kazakh -> kk
        AppLanguage.Russian -> ru
        AppLanguage.English -> en
    }
}
