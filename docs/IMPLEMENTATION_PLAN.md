# Snapar implementation plan

## Қазір қолданба ішінде дайын

- KK / RU / EN onboarding және интерфейс
- Discover personalization: search, filters, like, dislike, save, explanation
- Shorts: vertical feed, photo, video, draggable 360, like, comments, share
- Destination: check-in, save, share, live weather, calendar, budget, gallery
- Routes: search, sorting, map markers, route details, external navigation
- Route Builder: city, date, days, budget, interests, transport, lodging, pace
- SAI: text, voice input, offline intent parsing and itinerary generation
- GeoSnap: persistent photo/video URI, GPS, caption, comments and feed
- Passport: XP, levels, achievements and visited-place map markers
- Profile: statistics, saved content, language, notifications, theme, accessibility
- Business Mode: analytics, venue photo, create, edit, delete and review status
- Local persistence with Android DataStore

## Сыртқы кілтсіз жұмыс істейтін қызмет

- Weather: Open-Meteo HTTPS API, API key қажет емес.
- Navigation: Android-та орнатылған карта қолданбасын `geo:` intent арқылы ашады.
- SAI: offline planner әрқашан қолжетімді.

## Production үшін кейін қосылатын инфрақұрылым

1. **Cloud AI backend**
   - LLM provider API key тек серверде сақталады.
   - Android `SNAPAR_SAI_BACKEND_URL` арқылы HTTPS endpoint-ке қосылады.
   - Endpoint contract: `POST /api/sai`.

2. **Account және cloud database**
   - Телефон/Google/Apple sign-in.
   - Places, routes, passport, comments, likes and business venues.
   - Suggested options: Supabase немесе Firebase.

3. **Media storage and moderation**
   - GeoSnap photo/video upload.
   - EXIF/GPS verification, compression, thumbnail generation.
   - Abuse report and moderation queue.

4. **Embedded interactive map**
   - Mapbox немесе Google Maps SDK key.
   - Marker clustering, route polyline, offline tiles and turn-by-turn navigation.
   - Қазіргі жеңіл нұсқа system map қолданбасын ашады.

5. **Push notifications**
   - Firebase Cloud Messaging.
   - Weather alerts, saved-route price changes and Passport challenges.

## SAI backend contract

Request:

```json
{
  "message": "Алматыдан 3 күнге табиғат турын құр",
  "language": "kk",
  "app": "Snapar"
}
```

Response:

```json
{
  "reply": "Сізге 3 күндік Көлсай және Қайыңды бағытын ұсынамын."
}
```

Android API key қабылдамайды. Provider key server environment variable ретінде сақталуы керек.
