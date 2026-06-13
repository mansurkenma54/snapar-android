package kz.snapar.app.data

import kz.snapar.app.R
import kz.snapar.app.model.CalendarOffer
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.LocalText
import kz.snapar.app.model.Place
import kz.snapar.app.model.RouteDay
import kz.snapar.app.model.RouteStop
import kz.snapar.app.model.TravelRoute
import kz.snapar.app.model.Weather

private fun l(kk: String, ru: String, en: String) = LocalText(kk, ru, en)

object SampleData {
    val places = listOf(
        Place(
            id = 1,
            name = l("Көлсай көлдері", "Кольсайские озёра", "Kolsai Lakes"),
            region = l("Алматы облысы", "Алматинская область", "Almaty Region"),
            description = l(
                "Тянь-Шань тауларындағы үш мөлдір көл. Қарағайлы орман, салқын ауа және тыныш жаяу бағыттар бір сапарда тоғысады.",
                "Три прозрачных озера в горах Тянь-Шаня. Сосновый лес, прохладный воздух и спокойные пешие маршруты.",
                "Three crystal lakes in the Tian Shan mountains, surrounded by pine forest and peaceful hiking trails.",
            ),
            image = R.drawable.kolsay,
            rating = 4.9,
            trustScore = 98,
            weather = Weather(18, l("Ашық", "Ясно", "Clear"), 48, 9, 1016, "05:18", "20:37"),
            distanceKm = 280,
            travelMinutes = 240,
            averageCost = 15_000,
            bestSeason = l("Мамыр – қыркүйек", "Май – сентябрь", "May – September"),
            difficulty = l("Орташа", "Средняя", "Moderate"),
            tags = listOf(
                l("Табиғат", "Природа", "Nature"),
                l("Көл", "Озеро", "Lake"),
                l("Тау", "Горы", "Mountains"),
            ),
            recommendation = l(
                "Сіз тау мен көл бағыттарын жиі ұнатасыз.",
                "Вы часто выбираете горы и озёра.",
                "You often like mountain and lake destinations.",
            ),
            latitude = 42.991,
            longitude = 78.325,
            communityImages = listOf(R.drawable.community_1, R.drawable.community_2, R.drawable.kolsay),
        ),
        Place(
            id = 2,
            name = l("Медеу", "Медеу", "Medeu"),
            region = l("Алматы", "Алматы", "Almaty"),
            description = l(
                "Әлемге әйгілі биік таудағы мұз айдыны және Шымбұлаққа бастайтын қақпа. Қалаға жақын бір күндік белсенді демалыс.",
                "Всемирно известный высокогорный каток и ворота к Шымбулаку. Активный отдых рядом с городом.",
                "A world-famous high-altitude ice rink and the gateway to Shymbulak, perfect for an active day trip.",
            ),
            image = R.drawable.medeu,
            rating = 4.8,
            trustScore = 96,
            weather = Weather(12, l("Бұлтты", "Облачно", "Cloudy"), 61, 12, 1009, "05:20", "20:34"),
            distanceKm = 25,
            travelMinutes = 45,
            averageCost = 8_000,
            bestSeason = l("Жыл бойы", "Круглый год", "All year"),
            difficulty = l("Жеңіл", "Лёгкая", "Easy"),
            tags = listOf(l("Спорт", "Спорт", "Sports"), l("Тау", "Горы", "Mountains")),
            recommendation = l(
                "Алматыдан тез жететін weekend бағыты.",
                "Быстрый маршрут выходного дня из Алматы.",
                "A quick weekend escape from Almaty.",
            ),
            latitude = 43.157,
            longitude = 77.059,
            communityImages = listOf(R.drawable.medeu, R.drawable.community_3),
        ),
        Place(
            id = 3,
            name = l("Үлкен Алматы көлі", "Большое Алматинское озеро", "Big Almaty Lake"),
            region = l("Іле Алатауы", "Иле-Алатау", "Ile-Alatau"),
            description = l(
                "Алматы маңындағы көгілдір альпі көлі. Таңғы уақытта су түсі мен тау көрінісі ерекше әсер береді.",
                "Бирюзовое альпийское озеро рядом с Алматы. Особенно красиво ранним утром.",
                "A turquoise alpine lake near Almaty, most spectacular in the early morning.",
            ),
            image = R.drawable.big_almaty_lake,
            rating = 4.9,
            trustScore = 97,
            weather = Weather(15, l("Күн ашық", "Солнечно", "Sunny"), 43, 8, 1013, "05:17", "20:38"),
            distanceKm = 35,
            travelMinutes = 70,
            averageCost = 10_000,
            bestSeason = l("Маусым – қазан", "Июнь – октябрь", "June – October"),
            difficulty = l("Орташа", "Средняя", "Moderate"),
            tags = listOf(l("Фото", "Фото", "Photography"), l("Көл", "Озеро", "Lake")),
            recommendation = l(
                "Сіз фото орындарын сақтадыңыз.",
                "Вы сохраняли места для фотосъёмки.",
                "You saved photography spots.",
            ),
            latitude = 43.050,
            longitude = 76.986,
            communityImages = listOf(R.drawable.big_almaty_lake, R.drawable.community_4),
        ),
        Place(
            id = 4,
            name = l("Шарын шатқалы", "Чарынский каньон", "Charyn Canyon"),
            region = l("Алматы облысы", "Алматинская область", "Almaty Region"),
            description = l(
                "Миллиондаған жыл қалыптасқан қызыл жартастар мекені. Күн батар шақтағы бағыт фотографтар үшін таптырмас орын.",
                "Красные скалы, сформированные миллионами лет. Идеальное место для фотографов на закате.",
                "Red rock formations shaped over millions of years, spectacular at sunset.",
            ),
            image = R.drawable.charyn,
            rating = 4.8,
            trustScore = 95,
            weather = Weather(24, l("Құрғақ", "Сухо", "Dry"), 28, 18, 1004, "05:11", "20:29"),
            distanceKm = 195,
            travelMinutes = 180,
            averageCost = 12_000,
            bestSeason = l("Сәуір – қазан", "Апрель – октябрь", "April – October"),
            difficulty = l("Орташа", "Средняя", "Moderate"),
            tags = listOf(l("Шатқал", "Каньон", "Canyon"), l("Фото", "Фото", "Photography")),
            recommendation = l(
                "Adventure және фото бағыттарына сай.",
                "Подходит для приключений и фотосъёмки.",
                "Matches your adventure and photography interests.",
            ),
            latitude = 43.350,
            longitude = 79.080,
            communityImages = listOf(R.drawable.charyn, R.drawable.route_charyn),
        ),
        Place(
            id = 5,
            name = l("Ақмешіт үңгірі", "Пещера Акмешит", "Akmeshit Cave"),
            region = l("Түркістан облысы", "Туркестанская область", "Turkistan Region"),
            description = l(
                "Күмбезі ашық, ішінде шағын тоғайы бар алып табиғи үңгір. Аңыз бен ерекше акустика тоғысқан жасырын орын.",
                "Огромная природная пещера с открытым куполом и рощей внутри. Скрытое место с легендами и особой акустикой.",
                "A vast natural cave with an open dome and a small grove inside, rich in legends and unique acoustics.",
            ),
            image = R.drawable.akmeshit,
            rating = 4.7,
            trustScore = 93,
            weather = Weather(27, l("Жылы", "Тепло", "Warm"), 31, 11, 1007, "05:32", "20:41"),
            distanceKm = 690,
            travelMinutes = 600,
            averageCost = 24_000,
            bestSeason = l("Наурыз – қараша", "Март – ноябрь", "March – November"),
            difficulty = l("Жеңіл", "Лёгкая", "Easy"),
            tags = listOf(l("Жасырын", "Скрытое", "Hidden"), l("Этно", "Этно", "Culture")),
            recommendation = l(
                "Hidden Kazakhstan топтамасынан.",
                "Из подборки Hidden Kazakhstan.",
                "From the Hidden Kazakhstan collection.",
            ),
            latitude = 42.358,
            longitude = 69.660,
            communityImages = listOf(R.drawable.akmeshit, R.drawable.community_1),
        ),
        Place(
            id = 6,
            name = l("Бектау Ата", "Бектау-Ата", "Bektau-Ata"),
            region = l("Қарағанды облысы", "Карагандинская область", "Karaganda Region"),
            description = l(
                "Балқаш маңындағы гранит таулар, үңгірлер және шөлейт пейзаж. Жұлдыз тамашалау мен кемпингке қолайлы.",
                "Гранитные горы, пещеры и пустынные пейзажи у Балхаша. Отлично подходит для кемпинга и наблюдения за звёздами.",
                "Granite mountains, caves and desert scenery near Balkhash, ideal for camping and stargazing.",
            ),
            image = R.drawable.bektau,
            rating = 4.7,
            trustScore = 92,
            weather = Weather(22, l("Желді", "Ветрено", "Windy"), 26, 21, 1005, "05:08", "20:54"),
            distanceKm = 610,
            travelMinutes = 540,
            averageCost = 28_000,
            bestSeason = l("Сәуір – қыркүйек", "Апрель – сентябрь", "April – September"),
            difficulty = l("Қиын", "Сложная", "Hard"),
            tags = listOf(l("Кемпинг", "Кемпинг", "Camping"), l("Тау", "Горы", "Mountains")),
            recommendation = l(
                "Сирек баратын табиғи орындарды ұнатасыз.",
                "Вы любите редкие природные места.",
                "You enjoy rare natural destinations.",
            ),
            latitude = 47.470,
            longitude = 74.770,
            communityImages = listOf(R.drawable.bektau, R.drawable.community_2),
        ),
    )

    private val kolsaiItinerary = listOf(
        RouteDay(
            1,
            l("Көлсайға жол", "Дорога к Кольсаю", "Road to Kolsai"),
            listOf(
                RouteStop("07:00", l("Алматыдан шығу", "Выезд из Алматы", "Leave Almaty"), l("Минивэн немесе жеке көлік", "Минивэн или авто", "Minivan or private car"), 5_000),
                RouteStop("12:30", l("Көлсай-1", "Кольсай-1", "Kolsai-1"), l("Жаяу серуен және түскі ас", "Прогулка и обед", "Hike and lunch"), 4_500),
                RouteStop("19:00", l("Қонақ үй", "Гостевой дом", "Guest house"), l("Саты ауылында түнеу", "Ночь в селе Саты", "Night in Saty village"), 12_000),
            ),
        ),
        RouteDay(
            2,
            l("Қайыңды көлі", "Озеро Каинды", "Kaindy Lake"),
            listOf(
                RouteStop("08:30", l("Қайыңдыға трансфер", "Трансфер к Каинды", "Transfer to Kaindy"), l("Жол талғамайтын көлік", "Внедорожник", "Off-road vehicle"), 5_000),
                RouteStop("10:00", l("Су астындағы орман", "Подводный лес", "Sunken forest"), l("Фото және қысқа треккинг", "Фото и треккинг", "Photography and short hike"), 0),
                RouteStop("16:00", l("Алматыға қайту", "Возвращение в Алматы", "Return to Almaty"), l("Кешкі ауа райы: +16°C", "Вечером: +16°C", "Evening weather: 16°C"), 5_000),
            ),
        ),
    )

    val routes = listOf(
        TravelRoute(
            id = 101,
            title = l("Көлсай + Қайыңды", "Кольсай + Каинды", "Kolsai + Kaindy"),
            image = R.drawable.route_kolsay,
            days = 2,
            rating = 4.9,
            trust = 98,
            price = 31_500,
            distanceKm = 580,
            interests = setOf("nature", "photo", "family"),
            itinerary = kolsaiItinerary,
        ),
        TravelRoute(
            id = 102,
            title = l("Шарын экспедициясы", "Экспедиция в Чарын", "Charyn Expedition"),
            image = R.drawable.route_charyn,
            days = 1,
            rating = 4.8,
            trust = 95,
            price = 18_000,
            distanceKm = 390,
            interests = setOf("nature", "photo", "adventure"),
            itinerary = listOf(
                RouteDay(
                    1,
                    l("Шатқал күні", "День каньона", "Canyon day"),
                    listOf(
                        RouteStop("07:30", l("Алматыдан шығу", "Выезд из Алматы", "Leave Almaty"), l("Топтық автобус", "Групповой автобус", "Group bus"), 7_000),
                        RouteStop("11:00", l("Қамалдар аңғары", "Долина замков", "Valley of Castles"), l("4 км жаяу маршрут", "Пеший маршрут 4 км", "4 km hike"), 2_000),
                        RouteStop("18:30", l("Күн батуы", "Закат", "Sunset"), l("Фото нүктесі", "Фототочка", "Photo spot"), 0),
                    ),
                ),
            ),
        ),
    )

    val posts = listOf(
        CommunityPost(
            id = "p1",
            user = "Айдос Н.",
            location = l("Алматы · 2 сағ бұрын", "Алматы · 2 ч назад", "Almaty · 2h ago"),
            caption = l(
                "Шарын шатқалының керемет көрінісі! 360° турды қарап, өзіңіз баға беріңіз.",
                "Потрясающий вид Чарынского каньона! Оцените панораму 360°.",
                "An incredible view of Charyn Canyon. Explore the 360° panorama.",
            ),
            image = R.drawable.charyn,
            likes = 1200,
            comments = listOf("Керемет!", "Қай мезгілде бардыңыз?"),
            is360 = true,
        ),
        CommunityPost(
            id = "p2",
            user = "Еркін М.",
            location = l("Астана · 5 сағ бұрын", "Астана · 5 ч назад", "Astana · 5h ago"),
            caption = l(
                "Астананың жүрегі — Бәйтерек. Кешкі серуен үшін міндетті орын.",
                "Сердце Астаны — Байтерек. Обязательное место для вечерней прогулки.",
                "The heart of Astana — Baiterek. A must-see evening walk.",
            ),
            image = R.drawable.baiterek,
            likes = 342,
            comments = listOf("Әдемі кадр!"),
        ),
        CommunityPost(
            id = "p3",
            user = "Dana.travel",
            location = l("Көлсай · кеше", "Кольсай · вчера", "Kolsai · yesterday"),
            caption = l(
                "Таңғы тыныштық пен таудың судағы шағылысы.",
                "Утренняя тишина и отражение гор в воде.",
                "Morning silence and mountain reflections.",
            ),
            image = R.drawable.kolsay,
            likes = 856,
            comments = listOf("Wow", "Маршрутты бөлісіңізші"),
        ),
    )

    val calendarOffers = listOf(
        CalendarOffer(14, 18, l("Ашық", "Ясно", "Clear"), 14_000, 62),
        CalendarOffer(15, 16, l("Бұлтты", "Облачно", "Cloudy"), 12_500, 45),
        CalendarOffer(16, 20, l("Күн ашық", "Солнечно", "Sunny"), 15_000, 78),
        CalendarOffer(17, 17, l("Жаңбыр", "Дождь", "Rain"), 10_000, 28),
        CalendarOffer(18, 19, l("Ашық", "Ясно", "Clear"), 13_500, 54),
    )

    fun generateRoute(
        origin: String,
        days: Int,
        budget: Int,
        interests: Set<String>,
    ): TravelRoute {
        val preferred = routes.maxByOrNull { route -> route.interests.intersect(interests).size } ?: routes.first()
        val requestedDays = days.coerceIn(1, 5)
        val baseDays = preferred.itinerary.ifEmpty { kolsaiItinerary }
        val expanded = (1..requestedDays).map { day ->
            val template = baseDays[(day - 1) % baseDays.size]
            template.copy(day = day)
        }
        return preferred.copy(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            title = l(
                "$origin қаласынан $requestedDays күндік AI тур",
                "AI-тур на $requestedDays дн. из $origin",
                "$requestedDays-day AI trip from $origin",
            ),
            days = requestedDays,
            price = budget.coerceAtLeast(15_000),
            itinerary = expanded,
        )
    }
}
