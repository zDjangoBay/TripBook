// File: FilterEngineCard.kt
// Location: app/src/main/java/com/tripbook/reservations/ui/components/FilterEngineCard.kt
package com.tripbook.reservations.ui.components

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat

/**
 * Filter Engine UI Card for TripBook Reservations Module
 * Provides a comprehensive filtering interface for travel search results
 */
class FilterEngineCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    // UI Components
    private lateinit var filterContainer: LinearLayout
    private lateinit var headerLayout: LinearLayout
    private lateinit var filterTitle: TextView
    private lateinit var clearAllButton: TextView
    private lateinit var expandCollapseIcon: TextView
    private lateinit var filtersContent: LinearLayout
    
    // Filter Components
    private lateinit var priceRangeLayout: LinearLayout
    private lateinit var priceMinInput: EditText
    private lateinit var priceMaxInput: EditText
    private lateinit var priceRangeLabel: TextView
    private lateinit var destinationLayout: LinearLayout
    private lateinit var durationSpinner: Spinner
    private lateinit var ratingLayout: LinearLayout
    private lateinit var ratingValue: TextView
    private lateinit var amenitiesLayout: LinearLayout
    private lateinit var applyFiltersButton: Button
    
    // Data Models
    private var filterData = FilterData()
    private var isExpanded = true
    private var onFiltersAppliedListener: OnFiltersAppliedListener? = null
    
    // Color Resources
    private val primaryPurple = Color.parseColor("#6B46C1")
    private val lightPurple = Color.parseColor("#E0E7FF")
    private val darkPurple = Color.parseColor("#4C1D95")
    private val whiteColor = Color.WHITE
    private val grayColor = Color.parseColor("#9CA3AF")
    private val lightGray = Color.parseColor("#F3F4F6")

    init {
        initializeView()
        setupCardAppearance()
        setupEventListeners()
        populateDefaultData()
    }

    /**
     * Initialize the main view layout
     */
    private fun initializeView() {
        filterContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dpToPx(24), dpToPx(20), dpToPx(24), dpToPx(20))
        }
        
        setupHeaderSection()
        setupFiltersContent()
        addView(filterContainer)
    }

    /**
     * Setup the collapsible header section
     */
    private fun setupHeaderSection() {
        headerLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dpToPx(16))
            gravity = Gravity.CENTER_VERTICAL
        }

        filterTitle = TextView(context).apply {
            text = "Filter Results"
            textSize = 20f
            setTextColor(darkPurple)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        clearAllButton = TextView(context).apply {
            text = "Clear All"
            textSize = 14f
            setTextColor(primaryPurple)
            setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8))
            background = createRippleDrawable()
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        expandCollapseIcon = TextView(context).apply {
            text = "▲"
            textSize = 16f
            setTextColor(primaryPurple)
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        headerLayout.addView(filterTitle)
        headerLayout.addView(clearAllButton)
        headerLayout.addView(expandCollapseIcon)
        filterContainer.addView(headerLayout)
    }

    /**
     * Setup the main filters content section
     */
    private fun setupFiltersContent() {
        filtersContent = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        setupPriceRangeFilter()
        setupDestinationFilter()
        setupDurationFilter()
        setupRatingFilter()
        setupAmenitiesFilter()
        setupApplyButton()

        filterContainer.addView(filtersContent)
    }

    /**
     * Setup price range filter
     */
    private fun setupPriceRangeFilter() {
        val priceSection = createFilterSection("Price Range")
        
        priceRangeLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        priceMinInput = EditText(context).apply {
            hint = "Min"
            setText("50")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8))
            background = createInputBackground()
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(40), 1f).apply {
                rightMargin = dpToPx(8)
            }
        }

        val dashText = TextView(context).apply {
            text = " - "
            setTextColor(grayColor)
            gravity = Gravity.CENTER
        }

        priceMaxInput = EditText(context).apply {
            hint = "Max"
            setText("500")
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8))
            background = createInputBackground()
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(40), 1f).apply {
                leftMargin = dpToPx(8)
            }
        }

        priceRangeLayout.addView(priceMinInput)
        priceRangeLayout.addView(dashText)
        priceRangeLayout.addView(priceMaxInput)

        priceSection.addView(priceRangeLayout)
        filtersContent.addView(priceSection)
    }

    /**
     * Setup destination filter
     */
    private fun setupDestinationFilter() {
        val destinationSection = createFilterSection("Popular Destinations")
        
        destinationLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val destinations = listOf("Lagos", "Accra", "Nairobi", "Cape Town", "Cairo", "Marrakech")
        
        // Create rows of chips (2 per row)
        for (i in destinations.indices step 2) {
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(8)
                }
            }

            val chip1 = createFilterChip(destinations[i])
            rowLayout.addView(chip1)

            if (i + 1 < destinations.size) {
                val chip2 = createFilterChip(destinations[i + 1])
                rowLayout.addView(chip2)
            }

            destinationLayout.addView(rowLayout)
        }

        destinationSection.addView(destinationLayout)
        filtersContent.addView(destinationSection)
    }

    /**
     * Setup duration spinner filter
     */
    private fun setupDurationFilter() {
        val durationSection = createFilterSection("Trip Duration")
        
        val durations = arrayOf("Any Duration", "1-3 days", "4-7 days", "1-2 weeks", "2+ weeks")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, durations).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        durationSpinner = Spinner(context).apply {
            this.adapter = adapter
            setPadding(0, dpToPx(12), 0, dpToPx(12))
            background = createInputBackground()
        }

        durationSection.addView(durationSpinner)
        filtersContent.addView(durationSection)
    }

    /**
     * Setup rating filter
     */
    private fun setupRatingFilter() {
        val ratingSection = createFilterSection("Minimum Rating")
        
        ratingLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        // Create star buttons
        for (i in 1..5) {
            val starButton = TextView(context).apply {
                text = "★"
                textSize = 24f
                setTextColor(grayColor)
                setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8))
                tag = i
                setOnClickListener { updateRating(i) }
            }
            ratingLayout.addView(starButton)
        }

        ratingValue = TextView(context).apply {
            text = "No minimum rating"
            textSize = 14f
            setTextColor(grayColor)
            setPadding(dpToPx(16), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        ratingLayout.addView(ratingValue)
        ratingSection.addView(ratingLayout)
        filtersContent.addView(ratingSection)
    }

    /**
     * Setup amenities filter
     */
    private fun setupAmenitiesFilter() {
        val amenitiesSection = createFilterSection("Amenities")
        
        amenitiesLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        val amenities = listOf("WiFi", "Pool", "Breakfast", "Parking", "Gym", "Spa", "Restaurant", "Bar")
        
        // Create rows of chips (2 per row)
        for (i in amenities.indices step 2) {
            val rowLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dpToPx(8)
                }
            }

            val chip1 = createFilterChip(amenities[i])
            rowLayout.addView(chip1)

            if (i + 1 < amenities.size) {
                val chip2 = createFilterChip(amenities[i + 1])
                rowLayout.addView(chip2)
            }

            amenitiesLayout.addView(rowLayout)
        }

        amenitiesSection.addView(amenitiesLayout)
        filtersContent.addView(amenitiesSection)
    }

    /**
     * Setup apply filters button
     */
    private fun setupApplyButton() {
        applyFiltersButton = Button(context).apply {
            text = "Apply Filters"
            textSize = 16f
            setTextColor(whiteColor)
            background = createButtonBackground()
            setPadding(0, dpToPx(16), 0, dpToPx(16))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(24)
            }
        }

        filtersContent.addView(applyFiltersButton)
    }

    /**
     * Create a filter section with title
     */
    private fun createFilterSection(title: String): LinearLayout {
        val section = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dpToPx(16), 0, dpToPx(8))
        }

        val sectionTitle = TextView(context).apply {
            text = title
            textSize = 16f
            setTextColor(darkPurple)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, dpToPx(8))
        }

        section.addView(sectionTitle)
        return section
    }

    /**
     * Create a styled filter chip
     */
    private fun createFilterChip(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = 14f
            setTextColor(primaryPurple)
            setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
            background = createChipBackground(false)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                if (this@apply != this@createFilterChip) {
                    rightMargin = dpToPx(8)
                }
            }
            tag = false // Track selection state
            setOnClickListener {
                val isSelected = tag as Boolean
                toggleChip(this, !isSelected)
            }
        }
    }

    /**
     * Toggle chip selection state
     */
    private fun toggleChip(chip: TextView, isSelected: Boolean) {
        chip.tag = isSelected
        if (isSelected) {
            chip.background = createChipBackground(true)
            chip.setTextColor(whiteColor)
        } else {
            chip.background = createChipBackground(false)
            chip.setTextColor(primaryPurple)
        }
    }

    /**
     * Update rating selection
     */
    private fun updateRating(rating: Int) {
        filterData.minimumRating = rating.toFloat()
        
        for (i in 0 until ratingLayout.childCount - 1) { // -1 to exclude the text view
            val star = ratingLayout.getChildAt(i) as TextView
            val starRating = star.tag as Int
            star.setTextColor(if (starRating <= rating) primaryPurple else grayColor)
        }
        
        ratingValue.text = if (rating > 0) "$rating+ stars" else "No minimum rating"
    }

    /**
     * Setup card appearance and styling
     */
    private fun setupCardAppearance() {
        radius = dpToPx(16).toFloat()
        cardElevation = dpToPx(8).toFloat()
        setCardBackgroundColor(whiteColor)
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
        }
    }

    /**
     * Setup event listeners for interactive elements
     */
    private fun setupEventListeners() {
        // Header click to expand/collapse
        headerLayout.setOnClickListener {
            toggleExpansion()
        }

        // Clear all filters
        clearAllButton.setOnClickListener {
            clearAllFilters()
        }

        // Apply filters button
        applyFiltersButton.setOnClickListener {
            collectFilterData()
            onFiltersAppliedListener?.onFiltersApplied(filterData)
        }
    }

    /**
     * Toggle card expansion state
     */
    private fun toggleExpansion() {
        isExpanded = !isExpanded
        filtersContent.visibility = if (isExpanded) View.VISIBLE else View.GONE
        expandCollapseIcon.text = if (isExpanded) "▲" else "▼"
    }

    /**
     * Clear all applied filters
     */
    private fun clearAllFilters() {
        priceMinInput.setText("50")
        priceMaxInput.setText("500")
        durationSpinner.setSelection(0)
        updateRating(0)
        
        // Clear destination chips
        clearChipGroup(destinationLayout)
        
        // Clear amenities chips
        clearChipGroup(amenitiesLayout)
        
        filterData = FilterData()
    }

    /**
     * Clear all chips in a layout
     */
    private fun clearChipGroup(layout: LinearLayout) {
        for (i in 0 until layout.childCount) {
            val rowLayout = layout.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val chip = rowLayout.getChildAt(j) as TextView
                toggleChip(chip, false)
            }
        }
    }

    /**
     * Collect current filter data
     */
    private fun collectFilterData() {
        filterData.apply {
            priceRange = Pair(
                priceMinInput.text.toString().toIntOrNull() ?: 0,
                priceMaxInput.text.toString().toIntOrNull() ?: 1000
            )
            
            selectedDestinations = getSelectedChips(destinationLayout)
            duration = durationSpinner.selectedItem.toString()
            selectedAmenities = getSelectedChips(amenitiesLayout)
        }
    }

    /**
     * Get selected chips from a layout
     */
    private fun getSelectedChips(layout: LinearLayout): List<String> {
        val selectedChips = mutableListOf<String>()
        for (i in 0 until layout.childCount) {
            val rowLayout = layout.getChildAt(i) as LinearLayout
            for (j in 0 until rowLayout.childCount) {
                val chip = rowLayout.getChildAt(j) as TextView
                if (chip.tag as Boolean) {
                    selectedChips.add(chip.text.toString())
                }
            }
        }
        return selectedChips
    }

    /**
     * Create various drawable backgrounds
     */
    private fun createRippleDrawable(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(8).toFloat()
            setColor(lightPurple)
        }
    }

    private fun createInputBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(8).toFloat()
            setColor(lightGray)
            setStroke(2, grayColor)
        }
    }

    private fun createChipBackground(isSelected: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(20).toFloat()
            if (isSelected) {
                setColor(primaryPurple)
            } else {
                setColor(whiteColor)
                setStroke(2, primaryPurple)
            }
        }
    }

    private fun createButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(8).toFloat()
            setColor(primaryPurple)
        }
    }

    /**
     * Convert dp to pixels
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    /**
     * Populate default filter data
     */
    private fun populateDefaultData() {
        filterData = FilterData(
            priceRange = Pair(50, 500),
            selectedDestinations = emptyList(),
            duration = "Any Duration",
            minimumRating = 0f,
            selectedAmenities = emptyList()
        )
    }

    /**
     * Public API methods
     */
    fun setOnFiltersAppliedListener(listener: OnFiltersAppliedListener) {
        this.onFiltersAppliedListener = listener
    }

    fun getFilterData(): FilterData {
        collectFilterData()
        return filterData
    }

    fun applyFilterData(data: FilterData) {
        this.filterData = data
        
        // Update UI elements
        priceMinInput.setText(data.priceRange.first.toString())
        priceMaxInput.setText(data.priceRange.second.toString())
        
        // Update other UI elements as needed
        updateRating(data.minimumRating.toInt())
        
        // Update spinner selection
        val spinnerAdapter = durationSpinner.adapter as ArrayAdapter<String>
        for (i in 0 until spinnerAdapter.count) {
            if (spinnerAdapter.getItem(i) == data.duration) {
                durationSpinner.setSelection(i)
                break
            }
        }
    }

    /**
     * Data class to hold filter information
     */
    data class FilterData(
        var priceRange: Pair<Int, Int> = Pair(0, 1000),
        var selectedDestinations: List<String> = emptyList(),
        var duration: String = "Any Duration",
        var minimumRating: Float = 0f,
        var selectedAmenities: List<String> = emptyList()
    ) {
        fun hasActiveFilters(): Boolean {
            return priceRange != Pair(0, 1000) ||
                    selectedDestinations.isNotEmpty() ||
                    duration != "Any Duration" ||
                    minimumRating > 0f ||
                    selectedAmenities.isNotEmpty()
        }

        fun getFilterSummary(): String {
            val activeFilters = mutableListOf<String>()
            
            if (priceRange != Pair(0, 1000)) {
                activeFilters.add("Price: $${priceRange.first}-${priceRange.second}")
            }
            if (selectedDestinations.isNotEmpty()) {
                activeFilters.add("Destinations: ${selectedDestinations.size}")
            }
            if (duration != "Any Duration") {
                activeFilters.add("Duration: $duration")
            }
            if (minimumRating > 0f) {
                activeFilters.add("Rating: ${minimumRating.toInt()}+")
            }
            if (selectedAmenities.isNotEmpty()) {
                activeFilters.add("Amenities: ${selectedAmenities.size}")
            }
            
            return if (activeFilters.isEmpty()) {
                "No active filters"
            } else {
                activeFilters.joinToString(", ")
            }
        }
    }

    /**
     * Interface for filter application callbacks
     */
    interface OnFiltersAppliedListener {
        fun onFiltersApplied(filterData: FilterData)
    }

    /**
     * Companion object for factory methods and constants
     */
    companion object {
        const val DEFAULT_MIN_PRICE = 0
        const val DEFAULT_MAX_PRICE = 1000
        const val DEFAULT_RATING = 0f

        fun createDefault(context: Context): FilterEngineCard {
            return FilterEngineCard(context)
        }

        fun createWithListener(
            context: Context,
            listener: OnFiltersAppliedListener
        ): FilterEngineCard {
            return FilterEngineCard(context).apply {
                setOnFiltersAppliedListener(listener)
            }
        }
    }
}