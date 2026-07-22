const SYSTEM_PROMPT_BY_LANG = {
  kk: "Сен SAI — Snapar қосымшасының Қазақстан бойынша саяхат көмекшісісің. Қысқа (3-5 сөйлем), нақты, достық үнмен ТЕК қазақ тілінде жауап бер. Орындар, маршруттар, бюджет, ауа райы туралы нақты кеңес бер.",
  ru: "Ты SAI — туристический помощник приложения Snapar по Казахстану. Отвечай кратко (3-5 предложений), по делу, дружелюбно, ТОЛЬКО на русском языке. Советуй по местам, маршрутам, бюджету, погоде.",
  en: "You are SAI, Snapar's Kazakhstan travel assistant. Answer briefly (3-5 sentences), helpfully, in a friendly tone, ONLY in English. Give concrete advice on places, routes, budget, weather.",
}

function json(obj, status) {
  return new Response(JSON.stringify(obj), {
    status,
    headers: { "Content-Type": "application/json" },
  })
}

export default {
  async fetch(request, env) {
    if (request.method !== "POST") {
      return new Response("Snapar SAI proxy is running", { status: 200 })
    }

    let body
    try {
      body = await request.json()
    } catch {
      return json({ reply: "" }, 200)
    }

    const message = String(body.message || "").slice(0, 4000).trim()
    const language = ["kk", "ru", "en"].includes(body.language) ? body.language : "kk"
    if (!message) return json({ reply: "" }, 200)

    try {
      const apiUrl =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" +
        env.GEMINI_API_KEY

      const upstream = await fetch(apiUrl, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          systemInstruction: {
            parts: [{ text: SYSTEM_PROMPT_BY_LANG[language] }],
          },
          contents: [{ role: "user", parts: [{ text: message }] }],
          generationConfig: {
            temperature: 0.7,
            maxOutputTokens: 2048,
          },
        }),
      })

      if (!upstream.ok) {
        return json({ reply: "" }, 200)
      }

      const data = await upstream.json()
      const reply = (data && data.candidates && data.candidates[0] && data.candidates[0].content && data.candidates[0].content.parts || [])
        .map((part) => part.text || "")
        .join("")
        .trim()

      return json({ reply }, 200)
    } catch (err) {
      return json({ reply: "" }, 200)
    }
  },
}
