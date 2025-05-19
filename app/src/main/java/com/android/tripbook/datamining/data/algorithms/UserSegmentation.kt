package com.android.tripbook.datamining.data.algorithms

import com.android.tripbook.datamining.data.database.entities.UserPreference
import com.android.tripbook.datamining.data.model.UserSegment
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Advanced user segmentation algorithms using clustering techniques
 */
object UserSegmentation {

    /**
     * Segments users using K-means clustering
     * 
     * @param userPreferences Map of user IDs to their preferences
     * @param k Number of clusters/segments to create
     * @param maxIterations Maximum number of iterations for the algorithm
     * @return Map of segment IDs to user segments
     */
    fun kMeansSegmentation(
        userPreferences: Map<String, List<UserPreference>>,
        k: Int = 4,
        maxIterations: Int = 100
    ): Map<Int, UserSegment> {
        if (userPreferences.isEmpty() || k <= 0) {
            return emptyMap()
        }
        
        // Convert preferences to feature vectors
        val userVectors = userPreferences.mapValues { (_, prefs) ->
            preferencesToVector(prefs)
        }
        
        // Initialize centroids randomly
        val allFeatures = userVectors.values.flatMap { it.keys }.distinct()
        val centroids = (0 until k).map { i ->
            allFeatures.associateWith { Random.nextDouble(0.0, 1.0) }
        }
        
        // Assign users to clusters
        var clusterAssignments = assignToClusters(userVectors, centroids)
        
        // Iterate until convergence or max iterations
        var iteration = 0
        var changed = true
        
        while (changed && iteration < maxIterations) {
            // Update centroids
            val newCentroids = updateCentroids(userVectors, clusterAssignments, k)
            
            // Reassign users to clusters
            val newAssignments = assignToClusters(userVectors, newCentroids)
            
            // Check if assignments changed
            changed = newAssignments != clusterAssignments
            clusterAssignments = newAssignments
            iteration++
        }
        
        // Create user segments from clusters
        return createSegments(userPreferences, clusterAssignments, k)
    }
    
    /**
     * Segments users using hierarchical clustering
     * 
     * @param userPreferences Map of user IDs to their preferences
     * @param numSegments Number of segments to create
     * @return Map of segment IDs to user segments
     */
    fun hierarchicalSegmentation(
        userPreferences: Map<String, List<UserPreference>>,
        numSegments: Int = 4
    ): Map<Int, UserSegment> {
        if (userPreferences.isEmpty() || numSegments <= 0) {
            return emptyMap()
        }
        
        // Convert preferences to feature vectors
        val userVectors = userPreferences.mapValues { (_, prefs) ->
            preferencesToVector(prefs)
        }
        
        // Initialize each user as its own cluster
        var clusters = userVectors.keys.mapIndexed { index, userId ->
            index to setOf(userId)
        }.toMap()
        
        // Calculate initial distances between all pairs of users
        val distances = mutableMapOf<Pair<String, String>, Double>()
        for (user1 in userVectors.keys) {
            for (user2 in userVectors.keys) {
                if (user1 != user2) {
                    val distance = calculateDistance(userVectors[user1]!!, userVectors[user2]!!)
                    distances[Pair(user1, user2)] = distance
                }
            }
        }
        
        // Merge clusters until we have the desired number
        while (clusters.size > numSegments) {
            // Find the closest pair of clusters
            var minDistance = Double.MAX_VALUE
            var closestPair: Pair<Int, Int>? = null
            
            for (cluster1 in clusters.keys) {
                for (cluster2 in clusters.keys) {
                    if (cluster1 != cluster2) {
                        // Calculate average distance between clusters
                        val clusterDistance = calculateClusterDistance(
                            clusters[cluster1]!!, clusters[cluster2]!!, distances
                        )
                        
                        if (clusterDistance < minDistance) {
                            minDistance = clusterDistance
                            closestPair = Pair(cluster1, cluster2)
                        }
                    }
                }
            }
            
            // Merge the closest clusters
            closestPair?.let { (cluster1, cluster2) ->
                val mergedCluster = clusters[cluster1]!! + clusters[cluster2]!!
                val newClusters = clusters.toMutableMap()
                newClusters.remove(cluster1)
                newClusters.remove(cluster2)
                newClusters[cluster1] = mergedCluster
                clusters = newClusters
            }
        }
        
        // Convert cluster assignments to the format needed for createSegments
        val clusterAssignments = mutableMapOf<String, Int>()
        clusters.forEach { (clusterId, userIds) ->
            userIds.forEach { userId ->
                clusterAssignments[userId] = clusterId
            }
        }
        
        // Create user segments from clusters
        return createSegments(userPreferences, clusterAssignments, numSegments)
    }
    
    /**
     * Segments users based on specific features (e.g., travel style, budget)
     * 
     * @param userPreferences Map of user IDs to their preferences
     * @param featureType Type of feature to segment by (e.g., "travel_style", "budget")
     * @return Map of segment IDs to user segments
     */
    fun featureBasedSegmentation(
        userPreferences: Map<String, List<UserPreference>>,
        featureType: String
    ): Map<Int, UserSegment> {
        if (userPreferences.isEmpty()) {
            return emptyMap()
        }
        
        // Group users by their primary preference for the given feature
        val featureValues = mutableMapOf<String, MutableList<String>>()
        
        userPreferences.forEach { (userId, prefs) ->
            // Find the strongest preference of the specified type
            val strongestPref = prefs
                .filter { it.preferenceType == featureType }
                .maxByOrNull { it.preferenceStrength }
            
            if (strongestPref != null) {
                val value = strongestPref.preferenceValue
                featureValues.getOrPut(value) { mutableListOf() }.add(userId)
            }
        }
        
        // Create segments from the feature groups
        return featureValues.entries.mapIndexed { index, (value, userIds) ->
            val segment = UserSegment(
                id = index,
                name = "$featureType: $value",
                description = "Users who prefer $value $featureType",
                userIds = userIds,
                primaryFeatures = listOf(Pair(featureType, value)),
                size = userIds.size
            )
            
            index to segment
        }.toMap()
    }
    
    /**
     * Converts user preferences to a feature vector
     */
    private fun preferencesToVector(preferences: List<UserPreference>): Map<String, Double> {
        return preferences.associate { 
            "${it.preferenceType}:${it.preferenceValue}" to it.preferenceStrength.toDouble()
        }
    }
    
    /**
     * Assigns users to the nearest centroid
     */
    private fun assignToClusters(
        userVectors: Map<String, Map<String, Double>>,
        centroids: List<Map<String, Double>>
    ): Map<String, Int> {
        val assignments = mutableMapOf<String, Int>()
        
        userVectors.forEach { (userId, userVector) ->
            var minDistance = Double.MAX_VALUE
            var closestCluster = 0
            
            centroids.forEachIndexed { index, centroid ->
                val distance = calculateDistance(userVector, centroid)
                if (distance < minDistance) {
                    minDistance = distance
                    closestCluster = index
                }
            }
            
            assignments[userId] = closestCluster
        }
        
        return assignments
    }
    
    /**
     * Updates centroids based on current cluster assignments
     */
    private fun updateCentroids(
        userVectors: Map<String, Map<String, Double>>,
        clusterAssignments: Map<String, Int>,
        k: Int
    ): List<Map<String, Double>> {
        val newCentroids = mutableListOf<Map<String, Double>>()
        
        for (clusterId in 0 until k) {
            // Get users in this cluster
            val clusterUsers = clusterAssignments.filter { it.value == clusterId }.keys
            
            if (clusterUsers.isEmpty()) {
                // If cluster is empty, keep the old centroid
                newCentroids.add(emptyMap())
                continue
            }
            
            // Calculate average of all features
            val allFeatures = clusterUsers.flatMap { userId ->
                userVectors[userId]?.keys ?: emptyList()
            }.distinct()
            
            val centroid = allFeatures.associateWith { feature ->
                val sum = clusterUsers.sumOf { userId ->
                    userVectors[userId]?.get(feature) ?: 0.0
                }
                sum / clusterUsers.size
            }
            
            newCentroids.add(centroid)
        }
        
        return newCentroids
    }
    
    /**
     * Calculates Euclidean distance between two feature vectors
     */
    private fun calculateDistance(vec1: Map<String, Double>, vec2: Map<String, Double>): Double {
        // Get all features from both vectors
        val allFeatures = vec1.keys + vec2.keys
        
        // Calculate squared differences for each feature
        val sumSquaredDiff = allFeatures.sumOf { feature ->
            val val1 = vec1[feature] ?: 0.0
            val val2 = vec2[feature] ?: 0.0
            (val1 - val2).pow(2)
        }
        
        return sqrt(sumSquaredDiff)
    }
    
    /**
     * Calculates average distance between two clusters
     */
    private fun calculateClusterDistance(
        cluster1: Set<String>,
        cluster2: Set<String>,
        distances: Map<Pair<String, String>, Double>
    ): Double {
        var totalDistance = 0.0
        var count = 0
        
        for (user1 in cluster1) {
            for (user2 in cluster2) {
                val distance = distances[Pair(user1, user2)] ?: distances[Pair(user2, user1)] ?: continue
                totalDistance += distance
                count++
            }
        }
        
        return if (count > 0) totalDistance / count else Double.MAX_VALUE
    }
    
    /**
     * Creates user segments from cluster assignments
     */
    private fun createSegments(
        userPreferences: Map<String, List<UserPreference>>,
        clusterAssignments: Map<String, Int>,
        numClusters: Int
    ): Map<Int, UserSegment> {
        val segments = mutableMapOf<Int, UserSegment>()
        
        for (clusterId in 0 until numClusters) {
            // Get users in this cluster
            val userIds = clusterAssignments.filter { it.value == clusterId }.keys.toList()
            
            if (userIds.isEmpty()) continue
            
            // Find common preferences in this cluster
            val commonPreferences = findCommonPreferences(userIds, userPreferences)
            
            // Create segment name and description based on common preferences
            val segmentName = if (commonPreferences.isNotEmpty()) {
                "Travelers interested in ${commonPreferences.first().second}"
            } else {
                "Traveler Segment ${clusterId + 1}"
            }
            
            val segmentDescription = if (commonPreferences.size > 1) {
                "Users who prefer ${commonPreferences.joinToString(", ") { it.second }}"
            } else {
                "A group of travelers with similar preferences"
            }
            
            segments[clusterId] = UserSegment(
                id = clusterId,
                name = segmentName,
                description = segmentDescription,
                userIds = userIds,
                primaryFeatures = commonPreferences,
                size = userIds.size
            )
        }
        
        return segments
    }
    
    /**
     * Finds common preferences among a group of users
     */
    private fun findCommonPreferences(
        userIds: List<String>,
        userPreferences: Map<String, List<UserPreference>>
    ): List<Pair<String, String>> {
        // Count preference occurrences
        val preferenceCounts = mutableMapOf<Pair<String, String>, Int>()
        
        userIds.forEach { userId ->
            val prefs = userPreferences[userId] ?: return@forEach
            
            prefs.forEach { pref ->
                val key = Pair(pref.preferenceType, pref.preferenceValue)
                preferenceCounts[key] = preferenceCounts.getOrDefault(key, 0) + 1
            }
        }
        
        // Find preferences that occur in at least 50% of users
        val threshold = userIds.size / 2
        return preferenceCounts
            .filter { it.value >= threshold }
            .map { it.key }
            .sortedByDescending { preferenceCounts[it] }
    }
}
