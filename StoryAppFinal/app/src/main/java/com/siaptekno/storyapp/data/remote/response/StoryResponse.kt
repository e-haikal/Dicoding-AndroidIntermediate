package com.siaptekno.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Represents the response containing a list of stories.
data class StoryResponse(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>, // A list of story items.

    @field:SerializedName("error")
    val error: Boolean, // Indicates if there was an error fetching the stories.

    @field:SerializedName("message")
    val message: String // A message describing the fetch status.
)

// Represents an individual story item in the list of stories.
data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String, // URL of the story's photo.

    @field:SerializedName("createdAt")
    val createdAt: String, // Timestamp when the story was created.

    @field:SerializedName("name")
    val name: String, // The name of the user who created the story.

    @field:SerializedName("description")
    val description: String, // A description of the story.

    @field:SerializedName("lon")
    val lon: Double? = null, // Longitude of the story's location (nullable).

    @field:SerializedName("id")
    val id: String, // Unique identifier of the story.

    @field:SerializedName("lat")
    val lat: Double? = null // Latitude of the story's location (nullable).
)
