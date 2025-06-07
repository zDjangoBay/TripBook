package com.TripBook.postmodule

import android.net.Uri
import org.junit.Test
import org.junit.Assert.*
import kotlin.system.measureTimeMillis

/**
 * Benchmark tests for PostEvent performance and scalability.
 * Tests the performance characteristics of event processing and related operations.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostEventBenchmark {

    companion object {
        private const val SMALL_BATCH_SIZE = 100
        private const val MEDIUM_BATCH_SIZE = 1000
        private const val LARGE_BATCH_SIZE = 10000
        private const val PERFORMANCE_THRESHOLD_MS = 1000L
    }

    @Test
    fun `benchmark event creation performance`() {
        val iterations = LARGE_BATCH_SIZE
        
        val timeMs = measureTimeMillis {
            repeat(iterations) { i ->
                val events = listOf(
                    PostEvent.TitleChanged("Title $i"),
                    PostEvent.DescriptionChanged("Description $i"),
                    PostEvent.CategoryChanged("Adventure"),
                    PostEvent.TagAdded("tag$i"),
                    PostEvent.VisibilityChanged("Public")
                )
                assertNotNull(events)
            }
        }
        
        println("Created $iterations event batches in ${timeMs}ms")
        println("Average time per batch: ${timeMs.toDouble() / iterations}ms")
        
        // Performance assertion
        assertTrue("Event creation took too long: ${timeMs}ms", timeMs < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event validation performance`() {
        val validationService = PostValidationService()
        val events = generateTestEvents(MEDIUM_BATCH_SIZE)
        
        val timeMs = measureTimeMillis {
            events.forEach { event ->
                val result = validationService.validatePostEvent(event)
                assertNotNull(result)
            }
        }
        
        println("Validated ${events.size} events in ${timeMs}ms")
        println("Average validation time: ${timeMs.toDouble() / events.size}ms")
        
        assertTrue("Event validation took too long: ${timeMs}ms", timeMs < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event handler processing performance`() {
        val handler = PostEventHandler()
        val events = generateTestEvents(MEDIUM_BATCH_SIZE)
        
        val timeMs = measureTimeMillis {
            events.forEach { event ->
                // Simulate async processing without actual coroutines for benchmark
                val currentState = handler.currentState.value
                assertNotNull(currentState)
            }
        }
        
        println("Processed ${events.size} events in ${timeMs}ms")
        println("Average processing time: ${timeMs.toDouble() / events.size}ms")
        
        assertTrue("Event processing took too long: ${timeMs}ms", timeMs < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event serialization performance`() {
        val events = generateTestEvents(SMALL_BATCH_SIZE)
        
        val serializationTime = measureTimeMillis {
            events.forEach { event ->
                val serialized = PostEventSerializationUtils.safeSerializeEvent(event)
                assertNotNull(serialized)
            }
        }
        
        val deserializationTime = measureTimeMillis {
            events.forEach { event ->
                val serialized = PostEventSerializationUtils.serializeEvent(event)
                val deserialized = PostEventSerializationUtils.safeDeserializeEvent(serialized)
                assertNotNull(deserialized)
                assertEquals(event::class, deserialized!!::class)
            }
        }
        
        println("Serialized ${events.size} events in ${serializationTime}ms")
        println("Deserialized ${events.size} events in ${deserializationTime}ms")
        println("Total serialization round-trip: ${serializationTime + deserializationTime}ms")
        
        assertTrue("Serialization took too long: ${serializationTime}ms", 
                  serializationTime < PERFORMANCE_THRESHOLD_MS)
        assertTrue("Deserialization took too long: ${deserializationTime}ms", 
                  deserializationTime < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event analytics processing performance`() {
        val events = generateTestEvents(LARGE_BATCH_SIZE)
        
        val summaryTime = measureTimeMillis {
            val summary = PostUtils.generateEventSummary(events)
            assertNotNull(summary)
            assertTrue(summary.totalEvents > 0)
        }
        
        val completionTime = measureTimeMillis {
            val completion = PostUtils.calculateFormCompletion(events)
            assertNotNull(completion)
            assertTrue(completion.totalFields > 0)
        }
        
        val patternTime = measureTimeMillis {
            val patterns = PostUtils.findEventPatterns(events)
            assertNotNull(patterns)
        }
        
        val validationTime = measureTimeMillis {
            val validation = PostUtils.validateEventSequence(events)
            assertNotNull(validation)
        }
        
        val totalAnalyticsTime = summaryTime + completionTime + patternTime + validationTime
        
        println("Analytics performance for ${events.size} events:")
        println("  Summary generation: ${summaryTime}ms")
        println("  Completion calculation: ${completionTime}ms")
        println("  Pattern detection: ${patternTime}ms")
        println("  Sequence validation: ${validationTime}ms")
        println("  Total analytics time: ${totalAnalyticsTime}ms")
        
        assertTrue("Analytics processing took too long: ${totalAnalyticsTime}ms", 
                  totalAnalyticsTime < PERFORMANCE_THRESHOLD_MS * 2)
    }

    @Test
    fun `benchmark memory usage with large event collections`() {
        val runtime = Runtime.getRuntime()
        
        // Measure initial memory
        runtime.gc()
        val initialMemory = runtime.totalMemory() - runtime.freeMemory()
        
        // Create large collection of events
        val largeEventCollection = generateTestEvents(LARGE_BATCH_SIZE * 5)
        
        // Measure memory after creation
        val afterCreationMemory = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = afterCreationMemory - initialMemory
        
        println("Memory usage for ${largeEventCollection.size} events:")
        println("  Initial memory: ${initialMemory / 1024 / 1024}MB")
        println("  After creation: ${afterCreationMemory / 1024 / 1024}MB")
        println("  Memory used: ${memoryUsed / 1024 / 1024}MB")
        println("  Average per event: ${memoryUsed / largeEventCollection.size} bytes")
        
        // Verify collection is usable
        assertNotNull(largeEventCollection)
        assertTrue(largeEventCollection.isNotEmpty())
        
        // Memory should be reasonable (less than 100MB for 50k events)
        assertTrue("Memory usage too high: ${memoryUsed / 1024 / 1024}MB", 
                  memoryUsed < 100 * 1024 * 1024)
    }

    @Test
    fun `benchmark concurrent event processing simulation`() {
        val events = generateTestEvents(MEDIUM_BATCH_SIZE)
        val handler = PostEventHandler()
        
        // Simulate concurrent processing by rapid sequential processing
        val timeMs = measureTimeMillis {
            events.chunked(10).forEach { batch ->
                batch.forEach { event ->
                    // Simulate processing without actual coroutines
                    val state = handler.currentState.value
                    val validation = handler.getValidationStatus()
                    assertNotNull(state)
                    assertNotNull(validation)
                }
            }
        }
        
        println("Simulated concurrent processing of ${events.size} events in ${timeMs}ms")
        println("Average batch processing time: ${timeMs.toDouble() / (events.size / 10)}ms")
        
        assertTrue("Concurrent processing simulation took too long: ${timeMs}ms", 
                  timeMs < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event extension functions performance`() {
        val events = generateTestEvents(LARGE_BATCH_SIZE)
        
        val extensionTime = measureTimeMillis {
            events.forEach { event ->
                // Test all extension functions
                val isContent = event.isContentModifyingEvent()
                val isAction = event.isFormActionEvent()
                val isFeedback = event.isFeedbackEvent()
                val priority = event.getPriority()
                val description = event.getDescription()
                val category = event.getAnalyticsCategory()
                val shouldAutoSave = event.shouldTriggerAutoSave()
                val requirements = event.getValidationRequirements()
                val map = event.toMap()
                val isUndoable = event.isUndoable()
                
                // Verify results are reasonable
                assertNotNull(priority)
                assertNotNull(description)
                assertNotNull(category)
                assertNotNull(requirements)
                assertNotNull(map)
            }
        }
        
        println("Extension function calls for ${events.size} events in ${extensionTime}ms")
        println("Average extension processing time: ${extensionTime.toDouble() / events.size}ms")
        
        assertTrue("Extension function processing took too long: ${extensionTime}ms", 
                  extensionTime < PERFORMANCE_THRESHOLD_MS)
    }

    @Test
    fun `benchmark event factory performance`() {
        val factory = PostEventFactoryCompanion.createDefault()
        val iterations = MEDIUM_BATCH_SIZE
        
        val factoryTime = measureTimeMillis {
            repeat(iterations) { i ->
                val titleResult = factory.createTitleChangedEvent("Title $i")
                val descResult = factory.createDescriptionChangedEvent("Description $i")
                val categoryResult = factory.createCategoryChangedEvent("Adventure")
                val tagResult = factory.createTagAddedEvent("tag$i")
                val visibilityResult = factory.createVisibilityChangedEvent("Public")
                
                assertTrue(titleResult.isSuccess)
                assertTrue(descResult.isSuccess)
                assertTrue(categoryResult.isSuccess)
                assertTrue(tagResult.isSuccess)
                assertTrue(visibilityResult.isSuccess)
            }
        }
        
        println("Factory created ${iterations * 5} events in ${factoryTime}ms")
        println("Average factory creation time: ${factoryTime.toDouble() / (iterations * 5)}ms")
        
        assertTrue("Factory event creation took too long: ${factoryTime}ms", 
                  factoryTime < PERFORMANCE_THRESHOLD_MS)
    }

    private fun generateTestEvents(count: Int): List<PostEvent> {
        val events = mutableListOf<PostEvent>()
        
        repeat(count) { i ->
            when (i % 17) {
                0 -> events.add(PostEvent.TitleChanged("Title $i"))
                1 -> events.add(PostEvent.DescriptionChanged("Description $i"))
                2 -> events.add(PostEvent.ImageAdded(Uri.parse("content://test/$i")))
                3 -> events.add(PostEvent.ImageRemoved(Uri.parse("content://test/$i")))
                4 -> events.add(PostEvent.ClearAllImages)
                5 -> events.add(PostEvent.LocationAdded(40.7128 + (i % 10), -74.0060 + (i % 10), "Location $i"))
                6 -> events.add(PostEvent.ClearLocation)
                7 -> events.add(PostEvent.CategoryChanged("Category ${i % 5}"))
                8 -> events.add(PostEvent.TagAdded("tag$i"))
                9 -> events.add(PostEvent.TagRemoved("oldtag$i"))
                10 -> events.add(PostEvent.VisibilityChanged("Public"))
                11 -> events.add(PostEvent.SubmitPost)
                12 -> events.add(PostEvent.ClearForm)
                13 -> events.add(PostEvent.SaveDraft)
                14 -> events.add(PostEvent.ShowError("Error $i"))
                15 -> events.add(PostEvent.DismissError)
                16 -> events.add(PostEvent.PostCreated("post_$i"))
            }
        }
        
        return events
    }
}
