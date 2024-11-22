package com.siaptekno.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

// Represents the detailed response of a story retrieved from the API.
data class DetailResponse(

    @field:SerializedName("error")
    val error: Boolean, // Indicates if there was an error in fetching the details.

    @field:SerializedName("message")
    val message: String, // A message describing the status of the request.

    @field:SerializedName("story")
    val story: Story // Contains detailed information about the story.
)

// Represents the details of an individual story.
data class Story(

    @field:SerializedName("photoUrl")
    val photoUrl: String, // URL of the story's photo.

    @field:SerializedName("createdAt")
    val createdAt: String, // Timestamp when the story was created.

    @field:SerializedName("name")
    val name: String, // The name of the user who created the story.

    @field:SerializedName("description")
    val description: String, // A description of the story.

    @field:SerializedName("lon")
    val lon: Any, // Longitude of the story's location (nullable or Any type).

    @field:SerializedName("id")
    val id: String, // Unique identifier of the story.

    @field:SerializedName("lat")
    val lat: Any // Latitude of the story's location (nullable or Any type).
)
