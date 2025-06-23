class DestinationPrefHelper(context: Context) {
    private val prefs = context.getSharedPreferences("tripbook_preferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveDestinationsPref(userId: String, destinations: List<String>) {
        val json = gson.toJson(destinations)
        prefs.edit().putString("pref_$userId", json).apply()
    }

    fun getDestinationsPref(userId: String): List<String> {
        val json = prefs.getString("pref_$userId", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}