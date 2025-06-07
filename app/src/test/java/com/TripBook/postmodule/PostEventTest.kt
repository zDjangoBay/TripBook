package com.TripBook.postmodule

import android.net.Uri
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for PostEvent sealed class.
 * Tests all event types and their properties for correctness.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
class PostEventTest {

    @Test
    fun `TitleChanged should store the new title correctly`() {
        val newTitle = "My Amazing Trip to Paris"
        val event = PostEvent.TitleChanged(newTitle)
        assertEquals(newTitle, event.newTitle)
    }

    @Test
    fun `DescriptionChanged should store the new description correctly`() {
        val newDescription = "This was an incredible journey through the streets of Paris"
        val event = PostEvent.DescriptionChanged(newDescription)
        assertEquals(newDescription, event.newDescription)
    }

    @Test
    fun `LocationAdded should store coordinates and name correctly`() {
        val latitude = 48.8566
        val longitude = 2.3522
        val locationName = "Paris, France"
        
        val event = PostEvent.LocationAdded(latitude, longitude, locationName)
        
        assertEquals(latitude, event.latitude, 0.0001)
        assertEquals(longitude, event.longitude, 0.0001)
        assertEquals(locationName, event.locationName)
    }

    @Test
    fun `LocationAdded should handle null location name`() {
        val latitude = 48.8566
        val longitude = 2.3522
        
        val event = PostEvent.LocationAdded(latitude, longitude, null)
        
        assertEquals(latitude, event.latitude, 0.0001)
        assertEquals(longitude, event.longitude, 0.0001)
        assertNull(event.locationName)
    }

    @Test
    fun `CategoryChanged should store the category correctly`() {
        val category = "Adventure"
        val event = PostEvent.CategoryChanged(category)
        assertEquals(category, event.category)
    }

    @Test
    fun `TagAdded should store the tag correctly`() {
        val tag = "travel"
        val event = PostEvent.TagAdded(tag)
        assertEquals(tag, event.tag)
    }

    @Test
    fun `TagRemoved should store the tag correctly`() {
        val tag = "outdated"
        val event = PostEvent.TagRemoved(tag)
        assertEquals(tag, event.tag)
    }

    @Test
    fun `VisibilityChanged should store the visibility correctly`() {
        val visibility = "Private"
        val event = PostEvent.VisibilityChanged(visibility)
        assertEquals(visibility, event.visibility)
    }

    @Test
    fun `ShowError should store the error message correctly`() {
        val errorMessage = "Network connection failed"
        val event = PostEvent.ShowError(errorMessage)
        assertEquals(errorMessage, event.message)
    }

    @Test
    fun `PostCreated should store the post ID correctly`() {
        val postId = "post_12345"
        val event = PostEvent.PostCreated(postId)
        assertEquals(postId, event.postId)
    }

    @Test
    fun `object events should be singleton instances`() {
        val clearImages1 = PostEvent.ClearAllImages
        val clearImages2 = PostEvent.ClearAllImages
        assertSame(clearImages1, clearImages2)

        val clearLocation1 = PostEvent.ClearLocation
        val clearLocation2 = PostEvent.ClearLocation
        assertSame(clearLocation1, clearLocation2)

        val submitPost1 = PostEvent.SubmitPost
        val submitPost2 = PostEvent.SubmitPost
        assertSame(submitPost1, submitPost2)

        val clearForm1 = PostEvent.ClearForm
        val clearForm2 = PostEvent.ClearForm
        assertSame(clearForm1, clearForm2)

        val saveDraft1 = PostEvent.SaveDraft
        val saveDraft2 = PostEvent.SaveDraft
        assertSame(saveDraft1, saveDraft2)

        val dismissError1 = PostEvent.DismissError
        val dismissError2 = PostEvent.DismissError
        assertSame(dismissError1, dismissError2)
    }

    @Test
    fun `all events should be instances of PostEvent`() {
        val events = listOf(
            PostEvent.TitleChanged("Test"),
            PostEvent.DescriptionChanged("Test"),
            PostEvent.LocationAdded(0.0, 0.0, null),
            PostEvent.CategoryChanged("Test"),
            PostEvent.TagAdded("test"),
            PostEvent.TagRemoved("test"),
            PostEvent.VisibilityChanged("Public"),
            PostEvent.ShowError("Error"),
            PostEvent.PostCreated("123"),
            PostEvent.ClearAllImages,
            PostEvent.ClearLocation,
            PostEvent.SubmitPost,
            PostEvent.ClearForm,
            PostEvent.SaveDraft,
            PostEvent.DismissError
        )

        events.forEach { event ->
            assertTrue("$event should be instance of PostEvent", event is PostEvent)
        }
    }

    @Test
    fun `events should work in when expressions`() {
        val events = listOf(
            PostEvent.TitleChanged("Test"),
            PostEvent.DescriptionChanged("Test"),
            PostEvent.ImageAdded(Uri.parse("test://uri")),
            PostEvent.ImageRemoved(Uri.parse("test://uri")),
            PostEvent.LocationAdded(0.0, 0.0, null),
            PostEvent.CategoryChanged("Test"),
            PostEvent.TagAdded("test"),
            PostEvent.TagRemoved("test"),
            PostEvent.VisibilityChanged("Public"),
            PostEvent.ShowError("Error"),
            PostEvent.PostCreated("123"),
            PostEvent.ClearAllImages,
            PostEvent.ClearLocation,
            PostEvent.SubmitPost,
            PostEvent.ClearForm,
            PostEvent.SaveDraft,
            PostEvent.DismissError
        )

        events.forEach { event ->
            val result = when (event) {
                is PostEvent.TitleChanged -> "title"
                is PostEvent.DescriptionChanged -> "description"
                is PostEvent.LocationAdded -> "location"
                is PostEvent.CategoryChanged -> "category"
                is PostEvent.TagAdded -> "tag_add"
                is PostEvent.TagRemoved -> "tag_remove"
                is PostEvent.VisibilityChanged -> "visibility"
                is PostEvent.ShowError -> "error"
                is PostEvent.PostCreated -> "created"
                is PostEvent.ImageAdded -> "image_add"
                is PostEvent.ImageRemoved -> "image_remove"
                is PostEvent.ClearAllImages -> "clear_images"
                is PostEvent.ClearLocation -> "clear_location"
                is PostEvent.SubmitPost -> "submit"
                is PostEvent.ClearForm -> "clear_form"
                is PostEvent.SaveDraft -> "save_draft"
                is PostEvent.DismissError -> "dismiss_error"
            }
            assertNotNull("When expression should handle $event", result)
        }
    }

    @Test
    fun `data events should implement equals and hashCode correctly`() {
        val event1 = PostEvent.TitleChanged("Same Title")
        val event2 = PostEvent.TitleChanged("Same Title")
        val event3 = PostEvent.TitleChanged("Different Title")

        assertEquals(event1, event2)
        assertNotEquals(event1, event3)
        assertEquals(event1.hashCode(), event2.hashCode())
        assertNotEquals(event1.hashCode(), event3.hashCode())
    }

    @Test
    fun `LocationAdded should handle edge case coordinates`() {
        // Test North Pole
        val northPole = PostEvent.LocationAdded(90.0, 0.0, "North Pole")
        assertEquals(90.0, northPole.latitude, 0.0001)

        // Test South Pole
        val southPole = PostEvent.LocationAdded(-90.0, 0.0, "South Pole")
        assertEquals(-90.0, southPole.latitude, 0.0001)

        // Test International Date Line
        val dateLine = PostEvent.LocationAdded(0.0, 180.0, "Date Line")
        assertEquals(180.0, dateLine.longitude, 0.0001)

        // Test Prime Meridian
        val primeMeridian = PostEvent.LocationAdded(0.0, 0.0, "Prime Meridian")
        assertEquals(0.0, primeMeridian.longitude, 0.0001)
    }

    @Test
    fun `events should handle empty and special strings`() {
        // Test empty strings
        val emptyTitle = PostEvent.TitleChanged("")
        assertEquals("", emptyTitle.newTitle)

        val emptyDescription = PostEvent.DescriptionChanged("")
        assertEquals("", emptyDescription.newDescription)

        // Test strings with special characters
        val specialTitle = PostEvent.TitleChanged("Title with émojis 🌍 and spëcial chars!")
        assertEquals("Title with émojis 🌍 and spëcial chars!", specialTitle.newTitle)

        // Test very long strings
        val longString = "a".repeat(1000)
        val longTitle = PostEvent.TitleChanged(longString)
        assertEquals(longString, longTitle.newTitle)
    }

    @Test
    fun `events should be immutable`() {
        val originalTitle = "Original Title"
        val event = PostEvent.TitleChanged(originalTitle)
        
        // The event should store the original value
        assertEquals(originalTitle, event.newTitle)
        
        // Creating a new event with different value should not affect the original
        val newEvent = PostEvent.TitleChanged("New Title")
        assertEquals(originalTitle, event.newTitle)
        assertEquals("New Title", newEvent.newTitle)
    }

    @Test
    fun `toString should provide meaningful output for debugging`() {
        val titleEvent = PostEvent.TitleChanged("Test Title")
        val titleString = titleEvent.toString()
        assertTrue("toString should contain class name", titleString.contains("TitleChanged"))
        assertTrue("toString should contain the title", titleString.contains("Test Title"))

        val locationEvent = PostEvent.LocationAdded(48.8566, 2.3522, "Paris")
        val locationString = locationEvent.toString()
        assertTrue("toString should contain class name", locationString.contains("LocationAdded"))
        assertTrue("toString should contain coordinates", locationString.contains("48.8566"))
        assertTrue("toString should contain location name", locationString.contains("Paris"))
    }
}
