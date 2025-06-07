package com.TripBook.postmodule

import android.net.Uri
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/**
 * Custom serializer for PostEvent sealed class to enable JSON serialization/deserialization.
 * Handles all PostEvent types with proper type safety and validation.
 *
 * @author Feukoun Marel
 * @version 1.0
 * @since TripBook v1.0
 */
@Serializer(forClass = PostEvent::class)
object PostEventSerializer : KSerializer<PostEvent> {
    
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("PostEvent") {
        element<String>("type")
        element<JsonElement>("data")
        element<Long>("timestamp", isOptional = true)
    }
    
    override fun serialize(encoder: Encoder, value: PostEvent) {
        val output = encoder.beginStructure(descriptor)
        
        when (value) {
            is PostEvent.TitleChanged -> {
                output.encodeStringElement(descriptor, 0, "TitleChanged")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("newTitle", value.newTitle)
                })
            }
            is PostEvent.DescriptionChanged -> {
                output.encodeStringElement(descriptor, 0, "DescriptionChanged")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("newDescription", value.newDescription)
                })
            }
            is PostEvent.ImageAdded -> {
                output.encodeStringElement(descriptor, 0, "ImageAdded")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("imageUri", value.imageUri.toString())
                })
            }
            is PostEvent.ImageRemoved -> {
                output.encodeStringElement(descriptor, 0, "ImageRemoved")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("imageUri", value.imageUri.toString())
                })
            }
            is PostEvent.ClearAllImages -> {
                output.encodeStringElement(descriptor, 0, "ClearAllImages")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.LocationAdded -> {
                output.encodeStringElement(descriptor, 0, "LocationAdded")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("latitude", value.latitude)
                    put("longitude", value.longitude)
                    value.locationName?.let { put("locationName", it) }
                })
            }
            is PostEvent.ClearLocation -> {
                output.encodeStringElement(descriptor, 0, "ClearLocation")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.CategoryChanged -> {
                output.encodeStringElement(descriptor, 0, "CategoryChanged")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("category", value.category)
                })
            }
            is PostEvent.TagAdded -> {
                output.encodeStringElement(descriptor, 0, "TagAdded")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("tag", value.tag)
                })
            }
            is PostEvent.TagRemoved -> {
                output.encodeStringElement(descriptor, 0, "TagRemoved")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("tag", value.tag)
                })
            }
            is PostEvent.VisibilityChanged -> {
                output.encodeStringElement(descriptor, 0, "VisibilityChanged")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("visibility", value.visibility)
                })
            }
            is PostEvent.SubmitPost -> {
                output.encodeStringElement(descriptor, 0, "SubmitPost")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.ClearForm -> {
                output.encodeStringElement(descriptor, 0, "ClearForm")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.SaveDraft -> {
                output.encodeStringElement(descriptor, 0, "SaveDraft")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.ShowError -> {
                output.encodeStringElement(descriptor, 0, "ShowError")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("message", value.message)
                })
            }
            is PostEvent.DismissError -> {
                output.encodeStringElement(descriptor, 0, "DismissError")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {})
            }
            is PostEvent.PostCreated -> {
                output.encodeStringElement(descriptor, 0, "PostCreated")
                output.encodeSerializableElement(descriptor, 1, JsonObject.serializer(), buildJsonObject {
                    put("postId", value.postId)
                })
            }
        }
        
        // Add timestamp
        output.encodeLongElement(descriptor, 2, System.currentTimeMillis())
        
        output.endStructure(descriptor)
    }
    
    override fun deserialize(decoder: Decoder): PostEvent {
        val input = decoder.beginStructure(descriptor)
        
        var type: String? = null
        var data: JsonObject? = null
        
        while (true) {
            when (val index = input.decodeElementIndex(descriptor)) {
                0 -> type = input.decodeStringElement(descriptor, 0)
                1 -> data = input.decodeSerializableElement(descriptor, 1, JsonObject.serializer())
                2 -> input.decodeLongElement(descriptor, 2) // timestamp (ignored for now)
                CompositeDecoder.DECODE_DONE -> break
                else -> error("Unexpected index: $index")
            }
        }
        
        input.endStructure(descriptor)
        
        val eventType = type ?: error("Missing event type")
        val eventData = data ?: error("Missing event data")
        
        return when (eventType) {
            "TitleChanged" -> {
                val newTitle = eventData["newTitle"]?.jsonPrimitive?.content
                    ?: error("Missing newTitle")
                PostEvent.TitleChanged(newTitle)
            }
            "DescriptionChanged" -> {
                val newDescription = eventData["newDescription"]?.jsonPrimitive?.content
                    ?: error("Missing newDescription")
                PostEvent.DescriptionChanged(newDescription)
            }
            "ImageAdded" -> {
                val imageUriString = eventData["imageUri"]?.jsonPrimitive?.content
                    ?: error("Missing imageUri")
                PostEvent.ImageAdded(Uri.parse(imageUriString))
            }
            "ImageRemoved" -> {
                val imageUriString = eventData["imageUri"]?.jsonPrimitive?.content
                    ?: error("Missing imageUri")
                PostEvent.ImageRemoved(Uri.parse(imageUriString))
            }
            "ClearAllImages" -> PostEvent.ClearAllImages
            "LocationAdded" -> {
                val latitude = eventData["latitude"]?.jsonPrimitive?.double
                    ?: error("Missing latitude")
                val longitude = eventData["longitude"]?.jsonPrimitive?.double
                    ?: error("Missing longitude")
                val locationName = eventData["locationName"]?.jsonPrimitive?.content
                PostEvent.LocationAdded(latitude, longitude, locationName)
            }
            "ClearLocation" -> PostEvent.ClearLocation
            "CategoryChanged" -> {
                val category = eventData["category"]?.jsonPrimitive?.content
                    ?: error("Missing category")
                PostEvent.CategoryChanged(category)
            }
            "TagAdded" -> {
                val tag = eventData["tag"]?.jsonPrimitive?.content
                    ?: error("Missing tag")
                PostEvent.TagAdded(tag)
            }
            "TagRemoved" -> {
                val tag = eventData["tag"]?.jsonPrimitive?.content
                    ?: error("Missing tag")
                PostEvent.TagRemoved(tag)
            }
            "VisibilityChanged" -> {
                val visibility = eventData["visibility"]?.jsonPrimitive?.content
                    ?: error("Missing visibility")
                PostEvent.VisibilityChanged(visibility)
            }
            "SubmitPost" -> PostEvent.SubmitPost
            "ClearForm" -> PostEvent.ClearForm
            "SaveDraft" -> PostEvent.SaveDraft
            "ShowError" -> {
                val message = eventData["message"]?.jsonPrimitive?.content
                    ?: error("Missing message")
                PostEvent.ShowError(message)
            }
            "DismissError" -> PostEvent.DismissError
            "PostCreated" -> {
                val postId = eventData["postId"]?.jsonPrimitive?.content
                    ?: error("Missing postId")
                PostEvent.PostCreated(postId)
            }
            else -> error("Unknown event type: $eventType")
        }
    }
}

/**
 * Utility class for PostEvent serialization operations
 */
object PostEventSerializationUtils {
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }
    
    /**
     * Serializes a PostEvent to JSON string
     */
    fun serializeEvent(event: PostEvent): String {
        return json.encodeToString(PostEventSerializer, event)
    }
    
    /**
     * Deserializes a PostEvent from JSON string
     */
    fun deserializeEvent(jsonString: String): PostEvent {
        return json.decodeFromString(PostEventSerializer, jsonString)
    }
    
    /**
     * Serializes a list of PostEvents to JSON string
     */
    fun serializeEvents(events: List<PostEvent>): String {
        return json.encodeToString(ListSerializer(PostEventSerializer), events)
    }
    
    /**
     * Deserializes a list of PostEvents from JSON string
     */
    fun deserializeEvents(jsonString: String): List<PostEvent> {
        return json.decodeFromString(ListSerializer(PostEventSerializer), jsonString)
    }
    
    /**
     * Safely serializes an event, returning null if serialization fails
     */
    fun safeSerializeEvent(event: PostEvent): String? {
        return try {
            serializeEvent(event)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Safely deserializes an event, returning null if deserialization fails
     */
    fun safeDeserializeEvent(jsonString: String): PostEvent? {
        return try {
            deserializeEvent(jsonString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Converts PostEvent to a simple map for basic serialization
     */
    fun eventToMap(event: PostEvent): Map<String, Any?> {
        return event.toMap()
    }
    
    /**
     * Creates PostEvent from a map using PostEventFactory
     */
    fun eventFromMap(map: Map<String, Any?>, factory: PostEventFactory): PostEvent? {
        return factory.createEventFromMap(map).getOrNull()
    }
}
