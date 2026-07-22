package kz.snapar.app.data

import kz.snapar.app.R
import kz.snapar.app.model.BookingSlot
import kz.snapar.app.model.CalendarOffer
import kz.snapar.app.model.CommunityPost
import kz.snapar.app.model.FairnessVerdict
import kz.snapar.app.model.LocalText
import kz.snapar.app.model.Place
import kz.snapar.app.model.PriceFairness
import kz.snapar.app.model.Review
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
            id = 7,
            name = l("Бурабай ұлттық паркі", "Нацпарк Бурабай", "Burabay National Park"),
            region = l("Ақмола облысы", "Акмолинская область", "Akmola Region"),
            description = l(
                "Қарағай орманы мен мөлдір көлдер арасындағы «Қазақстанның Швейцариясы». Оқжетпес тауы, Жұмбақтас тасы — таптырмас табиғат көрінісі.",
                "«Швейцария Казахстана» среди сосновых лесов и прозрачных озёр. Скалы Окжетпес и Жумбактас — неповторимые природные памятники.",
                "Kazakhstan's 'Switzerland' nestled among pine forests and crystal lakes. Okzhetpes cliff and Zhumbaktas rock are iconic natural landmarks.",
            ),
            image = R.drawable.bektau,
            rating = 4.8,
            trustScore = 94,
            weather = Weather(16, l("Бұлтты", "Облачно", "Partly cloudy"), 56, 10, 1011, "05:02", "21:05"),
            distanceKm = 240,
            travelMinutes = 210,
            averageCost = 22_000,
            bestSeason = l("Маусым – қыркүйек", "Июнь – сентябрь", "June – September"),
            difficulty = l("Жеңіл", "Лёгкая", "Easy"),
            tags = listOf(
                l("Табиғат", "Природа", "Nature"),
                l("Орман", "Лес", "Forest"),
                l("Отбасылық", "Семейный", "Family"),
            ),
            recommendation = l(
                "Отбасымен демалысқа ең танымал орын.",
                "Самое популярное место для отдыха с семьёй.",
                "The most popular destination for a family getaway.",
            ),
            latitude = 53.053,
            longitude = 70.277,
            communityImages = listOf(R.drawable.bektau, R.drawable.community_2),
        ),
        Place(
            id = 8,
            name = l("Байтерек мұнарасы", "Монумент Байтерек", "Baiterek Tower"),
            region = l("Астана", "Астана", "Astana"),
            description = l(
                "Астананың символы — 105 метрлік алтын шар. Жоғарыдан бүкіл Астана паркі мен Есіл өзені айқын көрінеді. Кешкі жарықта ерекше сұлу.",
                "Символ Астаны — золотой шар на высоте 105 метров. Открывается панорама Астаны и реки Есиль. Особенно красив вечером.",
                "The symbol of Astana — a golden sphere at 105 m. Stunning panorama of the city and Yesil River, especially at night.",
            ),
            image = R.drawable.baiterek,
            rating = 4.7,
            trustScore = 96,
            weather = Weather(20, l("Ашық", "Ясно", "Clear"), 42, 14, 1010, "05:00", "21:10"),
            distanceKm = 1_190,
            travelMinutes = 720,
            averageCost = 5_000,
            bestSeason = l("Жыл бойы", "Круглый год", "All year"),
            difficulty = l("Жеңіл", "Лёгкая", "Easy"),
            tags = listOf(
                l("Астана", "Астана", "Astana"),
                l("Архитектура", "Архитектура", "Architecture"),
                l("Фото", "Фото", "Photography"),
            ),
            recommendation = l(
                "Астана сапарының міндетті нүктесі.",
                "Обязательная точка любого визита в Астану.",
                "A must-see on any Astana visit.",
            ),
            latitude = 51.128,
            longitude = 71.430,
            communityImages = listOf(R.drawable.baiterek, R.drawable.community_4),
        ),
        Place(
            id = 9,
            name = l("Алтын-Емел ұлттық паркі", "Нацпарк Алтын-Эмель", "Altyn-Emel National Park"),
            region = l("Алматы облысы", "Алматинская область", "Almaty Region"),
            description = l(
                "Айқайлайтын қызыл кумдар, сирек жануарлар мекені. Ең танымалы — желде дыбыс шығаратын Ән қумдары. Арқар мен қырғауылды тірі көресіз.",
                "Поющие пески, редкие животные. Главная достопримечательность — «Поющий бархан», издающий гул при ветре. Можно встретить архаров и фазанов.",
                "Singing sand dunes and rare wildlife. The famous Singing Dune roars in the wind. Spot argali sheep and ring-necked pheasants live.",
            ),
            image = R.drawable.charyn,
            rating = 4.8,
            trustScore = 91,
            weather = Weather(28, l("Ыстық", "Жарко", "Hot"), 22, 20, 1002, "05:05", "20:20"),
            distanceKm = 175,
            travelMinutes = 170,
            averageCost = 18_000,
            bestSeason = l("Наурыз – мамыр, қыркүйек – қараша", "Март – май, сент. – ноябрь", "March – May, Sept – Nov"),
            difficulty = l("Орташа", "Средняя", "Moderate"),
            tags = listOf(
                l("Шөл", "Пустыня", "Desert"),
                l("Жасырын", "Скрытое", "Hidden"),
                l("Жануарлар", "Животные", "Wildlife"),
            ),
            recommendation = l(
                "Adventure және жасырын орындарды іздейтіндерге арналған.",
                "Для искателей приключений и скрытых мест.",
                "Perfect for adventure seekers and hidden gem hunters.",
            ),
            latitude = 43.820,
            longitude = 79.095,
            communityImages = listOf(R.drawable.charyn, R.drawable.community_1),
        ),
        Place(
            id = 10,
            name = l("Шымбұлақ тау курорты", "Горный курорт Шымбулак", "Shymbulak Mountain Resort"),
            region = l("Алматы", "Алматы", "Almaty"),
            description = l(
                "2 200 метр биіктіктегі Орталық Азияның ең ірі тау курорты. Қыста шаңғы, жазда треккинг пен гондол. Медеуден тек 4 минуттық кресло жолы.",
                "Крупнейший горный курорт Центральной Азии на 2 200 м. Зимой — лыжи, летом — трекинг и гондола. От Медеу 4 минуты на кресельном подъёмнике.",
                "Central Asia's largest mountain resort at 2,200 m. Skiing in winter, trekking and gondola in summer. Just 4 minutes by gondola from Medeu.",
            ),
            image = R.drawable.medeu,
            rating = 4.7,
            trustScore = 93,
            weather = Weather(8, l("Қарлы", "Снежно", "Snowy"), 70, 15, 1007, "05:25", "20:30"),
            distanceKm = 28,
            travelMinutes = 55,
            averageCost = 25_000,
            bestSeason = l("Желт. – нау. (шаңғы), мам. – қыр. (трекинг)", "Дек. – март (лыжи), июнь – сент. (трекинг)", "Dec – Mar (ski), Jun – Sep (trekking)"),
            difficulty = l("Орташа", "Средняя", "Moderate"),
            tags = listOf(
                l("Шаңғы", "Лыжи", "Skiing"),
                l("Тау", "Горы", "Mountains"),
                l("Белсенді", "Активный", "Adventure"),
            ),
            recommendation = l(
                "Белсенді демалыс пен тау табиғатын ұнататындарға.",
                "Для любителей активного отдыха и горной природы.",
                "Ideal for those who love active mountain recreation.",
            ),
            latitude = 43.135,
            longitude = 77.079,
            communityImages = listOf(R.drawable.medeu, R.drawable.community_3),
        ),
        Place(
            id = 11,
            name = l("Қожа Ахмет Ясауи кесенесі", "Мавзолей Х.А. Ясауи", "Mausoleum of Khoja Ahmed Yasawi"),
            region = l("Түркістан", "Туркестан", "Turkistan"),
            description = l(
                "ЮНЕСКО Дүниежүзілік мұра тізімінде тіркелген ортағасырлық ғажайып. Тимур 1389 жылы сала бастаған 37 метрлік көк күмбез Орталық Азияның ең ірі кесенесі.",
                "Шедевр из Списка Всемирного наследия ЮНЕСКО. Построен Тимуром в 1389 году — 37-метровый купол, крупнейший мавзолей Центральной Азии.",
                "A UNESCO World Heritage masterpiece. Built by Timur in 1389, the 37-metre turquoise dome is the largest mausoleum in Central Asia.",
            ),
            image = R.drawable.akmeshit,
            rating = 4.9,
            trustScore = 98,
            weather = Weather(29, l("Ыстық", "Жарко", "Hot"), 28, 12, 1005, "05:35", "20:48"),
            distanceKm = 700,
            travelMinutes = 660,
            averageCost = 20_000,
            bestSeason = l("Наурыз – маусым, қыр. – қазан", "Март – июнь, сент. – окт.", "March – June, Sept – Oct"),
            difficulty = l("Жеңіл", "Лёгкая", "Easy"),
            tags = listOf(
                l("ЮНЕСКО", "ЮНЕСКО", "UNESCO"),
                l("Тарих", "История", "History"),
                l("Этно", "Этно", "Culture"),
            ),
            recommendation = l(
                "Қазақстан мәдениеті мен тарихын тереңірек тануды ұнататындарға.",
                "Для тех, кто хочет глубже познакомиться с историей и культурой Казахстана.",
                "A unique destination to dive deep into Kazakhstan's history and culture.",
            ),
            latitude = 43.300,
            longitude = 68.268,
            communityImages = listOf(R.drawable.akmeshit, R.drawable.community_2),
        ),
        Place(
            id = 12,
            name = l("Маңғыстау жартастары", "Каньоны Мангистау", "Mangystau Canyons"),
            region = l("Маңғыстау облысы", "Мангистауская область", "Mangystau Region"),
            description = l(
                "Желмен жонылған ақ бор жартастар, жерасты шіркеулер мен Каспий жағалауы. Ай бетін еске түсіретін Қазақстанның ең сирек бағыттарының бірі.",
                "Белые меловые скалы, выточенные ветром, подземные мечети и Каспийское море. Один из самых редких и фантастических маршрутов Казахстана.",
                "White chalk cliffs carved by wind, underground mosques and the Caspian Sea. One of Kazakhstan's most remote and otherworldly destinations.",
            ),
            image = R.drawable.bektau,
            rating = 4.8,
            trustScore = 90,
            weather = Weather(32, l("Ыстық, желді", "Жарко, ветрено", "Hot and windy"), 24, 25, 1000, "05:40", "20:10"),
            distanceKm = 2_700,
            travelMinutes = 2_400,
            averageCost = 50_000,
            bestSeason = l("Сәуір – мамыр, қыр. – қараша", "Апрель – май, сент. – ноябрь", "April – May, September – November"),
            difficulty = l("Қиын", "Сложная", "Hard"),
            tags = listOf(
                l("Жасырын", "Скрытое", "Hidden"),
                l("Кемпинг", "Кемпинг", "Camping"),
                l("Каспий", "Каспий", "Caspian"),
            ),
            recommendation = l(
                "Батыл саяхатшыларға арналған ерекше бағыт.",
                "Уникальный маршрут для смелых путешественников.",
                "An extraordinary route for the truly adventurous.",
            ),
            latitude = 44.100,
            longitude = 51.800,
            communityImages = listOf(R.drawable.bektau, R.drawable.community_1),
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

    val reviews: Map<Int, List<Review>> = mapOf(
        1 to listOf(
            Review("r1-1", "Айдос Н.", 5.0f, l("Таңертең ертерек барыңыз — тыныш, тамаша! Жолдың өзі де керемет.", "Приезжайте рано утром — тихо и великолепно! Сама дорога тоже красивая.", "Go early morning — peaceful and stunning! Even the drive is beautiful."), gpsVerified = true, date = l("3 күн бұрын", "3 дня назад", "3 days ago"), helpful = 24),
            Review("r1-2", "Saltanat.kz", 4.0f, l("Ауа-райы тез өзгереді, жылы киім алыңыз. Жол жаяу болса да тұрарлықтай.", "Погода переменчива, берите тёплую одежду. Пешая тропа того стоит.", "Weather changes fast, bring warm layers. The hiking trail is worth every step."), gpsVerified = true, date = l("1 апта бұрын", "1 нед. назад", "1 week ago"), helpful = 17),
            Review("r1-3", "TravelKZ", 5.0f, l("Өмірдегі ең таза ауа! Маршрутты 2 сағатқа жоспарлаңыз.", "Самый чистый воздух в жизни! Планируйте маршрут на 2 часа.", "Freshest air I've ever breathed! Budget 2 hours for the trail."), gpsVerified = false, date = l("2 апта бұрын", "2 нед. назад", "2 weeks ago"), helpful = 9),
        ),
        2 to listOf(
            Review("r2-1", "Еркін М.", 5.0f, l("Таңертеңгі Медеу тамаша! Адам аз, ауа сергек. Шымбұлаққа міндетті барыңыз.", "Медеу с утра волшебно! Мало людей, воздух свежий. Обязательно поднимитесь на Шымбулак.", "Morning Medeu is magical! Few crowds, fresh air. Definitely go up to Shymbulak."), gpsVerified = true, date = l("2 күн бұрын", "2 дня назад", "2 days ago"), helpful = 31),
            Review("r2-2", "Dana.travel", 4.0f, l("Шымбұлаққа дейінгі гондол 3 500 ₸. Жазда трекинг жақсы, қыста шаңғы.", "Гондола до Шымбулака 3 500 ₸. Летом хорошо для трекинга, зимой — лыжи.", "Gondola to Shymbulak costs 3 500 ₸. Great summer trekking, amazing skiing in winter."), gpsVerified = true, date = l("5 күн бұрын", "5 дней назад", "5 days ago"), helpful = 19),
        ),
        3 to listOf(
            Review("r3-1", "AlmatyPhoto", 5.0f, l("Таңда барыңыз — суы айна тәрізді. Ең жақсы фото нүктесі шығыс жағалауда.", "Идите на рассвете — вода как зеркало. Лучшая точка для фото — восточный берег.", "Come at sunrise — mirror-still water. Best photo spot is the east shore."), gpsVerified = true, date = l("1 күн бұрын", "1 день назад", "1 day ago"), helpful = 42),
            Review("r3-2", "NomadKZ", 4.0f, l("КПП-дан кіруге рұқсат керек. Жол таудан өтеді, 4x4 ұсынылады.", "Нужен пропуск на КПП. Дорога горная, рекомендуется 4x4.", "You need a permit at the checkpoint. Mountain road — 4x4 recommended."), gpsVerified = false, date = l("4 күн бұрын", "4 дня назад", "4 days ago"), helpful = 28),
        ),
        4 to listOf(
            Review("r4-1", "Photo.kz", 5.0f, l("Күн батуы кезінде барыңыз — оттай қызыл! Сағат 18:00-де келіңіз.", "Приходите на закате — огненно-красный! Приезжайте к 18:00.", "Come at sunset — fiery red rock! Arrive around 18:00 for the best light."), gpsVerified = true, date = l("1 күн бұрын", "1 день назад", "1 day ago"), helpful = 48),
            Review("r4-2", "AdventureKZ", 4.0f, l("Жазда 28°C болады. Су мен қалпақ алыңыз. Қамалдар аңғары ең жақсысы.", "Летом 28°C. Возьмите воду и головной убор. Долина замков — лучший участок.", "Summer hits 28°C. Bring water and a hat. Valley of Castles is the best section."), gpsVerified = true, date = l("4 күн бұрын", "4 дня назад", "4 days ago"), helpful = 22),
            Review("r4-3", "Almaty2024", 5.0f, l("Сарысу өзенінен 4 км жаяу. Жасыл және қызыл тастардың үйлесімі тамаша.", "4 км пешком от реки Сарысу. Сочетание зелёных и красных скал — великолепно.", "4 km walk from the Sarysu river. The mix of green and red rocks is spectacular."), gpsVerified = false, date = l("1 апта бұрын", "1 нед. назад", "1 week ago"), helpful = 15),
        ),
        7 to listOf(
            Review("r7-1", "FamilyTrip.kz", 5.0f, l("Балалармен барған ең жақсы орын! Тазалық жоғары, инфрақұрылым бар.", "Лучшее место с детьми! Чистота на высоте, инфраструктура есть.", "Best place with kids! Very clean, good infrastructure."), gpsVerified = true, date = l("3 күн бұрын", "3 дня назад", "3 days ago"), helpful = 36),
            Review("r7-2", "BuraBay2024", 4.0f, l("Жазда өте қызу болады. Ерте барыңыз. Оқжетпес жартасы ғажайып!", "Летом очень жарко. Приходите пораньше. Скала Окжетпес потрясающая!", "Very hot in summer. Come early. Okzhetpes cliff is breathtaking!"), gpsVerified = true, date = l("5 күн бұрын", "5 дней назад", "5 days ago"), helpful = 21),
        ),
        8 to listOf(
            Review("r8-1", "AstanaLife", 5.0f, l("Кешкі жарыққа дейін күте тұрыңыз — Байтерек тіпті сұлу болады! Жоғарыдан Ханшатыр көрінеді.", "Дождитесь вечерней подсветки — Байтерек становится ещё красивее! Сверху виден Хан-Шатыр.", "Wait for the evening lights — Baiterek becomes even more magical! Khan Shatyr visible from top."), gpsVerified = true, date = l("2 күн бұрын", "2 дня назад", "2 days ago"), helpful = 33),
            Review("r8-2", "Traveler_KZ", 4.0f, l("Кезек 20 минут. Жоғары билет 1 500 ₸. Күндіз де, кешке де барсаңыз болады.", "Очередь 20 минут. Билет наверх 1 500 ₸. Можно посетить и днём, и вечером.", "Queue about 20 min. Top ticket is 1 500 ₸. Worth visiting both day and evening."), gpsVerified = false, date = l("1 апта бұрын", "1 нед. назад", "1 week ago"), helpful = 18),
        ),
        11 to listOf(
            Review("r11-1", "HistoryKZ", 5.0f, l("Тарихи ескерткіш рухани сезім береді. Мұражай ішінде де бар. Маусым-қыркүйек айлары ең жақсы.", "Исторический памятник дарит духовное ощущение. Внутри есть музей. Лучшие месяцы — июнь–сентябрь.", "A deeply spiritual monument. There's a museum inside. June–September are the best months."), gpsVerified = true, date = l("4 күн бұрын", "4 дня назад", "4 days ago"), helpful = 29),
            Review("r11-2", "TurkistanTrip", 5.0f, l("ЮНЕСКО мұрасы лайықты! Ішінде фото түсіруге тыйым салынған. Сыртқы архитектура ғажайып.", "Наследие ЮНЕСКО по праву! Съёмка внутри запрещена. Внешняя архитектура потрясающая.", "UNESCO heritage well deserved! Photography inside is restricted. External architecture is breathtaking."), gpsVerified = true, date = l("1 апта бұрын", "1 нед. назад", "1 week ago"), helpful = 41),
        ),
    )

    fun reviewsFor(placeId: Int): List<Review> = reviews[placeId].orEmpty()

    fun bookingSlotsFor(place: Place): List<BookingSlot> {
        val base = place.averageCost
        return listOf(
            BookingSlot(l("14 маусым", "14 июня", "14 June"), (base * 1.00).toInt()),
            BookingSlot(l("15 маусым", "15 июня", "15 June"), (base * 0.85).toInt()),
            BookingSlot(l("16 маусым", "16 июня", "16 June"), (base * 1.10).toInt()),
            BookingSlot(l("17 маусым", "17 июня", "17 June"), (base * 0.90).toInt()),
            BookingSlot(l("18 маусым", "18 июня", "18 June"), (base * 1.05).toInt(), available = false),
        )
    }

    fun priceFairnessFor(place: Place): PriceFairness {
        val marketAvg = when {
            place.distanceKm < 50 -> 9_000
            place.distanceKm < 250 -> 16_000
            place.distanceKm < 700 -> 28_000
            else -> 48_000
        }
        val ratio = place.averageCost.toFloat() / marketAvg
        return when {
            ratio < 0.80f -> PriceFairness(
                marketAvg = marketAvg,
                verdict = FairnessVerdict.Cheap,
                score = 91,
                tip = l("Бұл бағыт бюджетке тиімді — нарық бағасынан арзан!", "Это направление дешевле рынка — бюджетный вариант!", "This destination is below market average — a great budget pick!"),
            )
            ratio < 1.20f -> PriceFairness(
                marketAvg = marketAvg,
                verdict = FairnessVerdict.Fair,
                score = 78,
                tip = l("Баға нарыққа сай. Ерте брондасаңыз 10–15% үнемдеуге болады.", "Цена соответствует рынку. Бронируйте заранее — сэкономите 10–15%.", "Price matches the market. Book early to save 10–15%."),
            )
            else -> PriceFairness(
                marketAvg = marketAvg,
                verdict = FairnessVerdict.Overpriced,
                score = 52,
                tip = l("Баға нарыққа қарағанда жоғарырақ. Жаңсары немесе қыркүйекте арзанырақ болады.", "Цена выше рыночной. В межсезонье (май или сентябрь) будет дешевле.", "Price is above market. Off-season (May or September) will be cheaper."),
            )
        }
    }

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
