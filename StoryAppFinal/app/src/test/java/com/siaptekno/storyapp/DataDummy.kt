package com.siaptekno.storyapp

import com.siaptekno.storyapp.data.remote.response.ListStoryItem
import java.util.Random

// Utility object to generate dummy data for testing purposes
object DataDummy {

    // Generates a list of 100 dummy stories with random details
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100) {
            // Creates a ListStoryItem with random longitude and latitude, unique photo URLs, and descriptions
            val story = ListStoryItem(
                photoUrl = "https://picsum.photos/200/300?random=$i", // Unique photo URL for each story
                createdAt = "2024-10-${i % 31 + 1}T12:34:56Z", // Randomized creation date
                name = "Story $i", // Unique name for each story
                description = "description for Story $i", // Description for testing
                lon = (100.0..140.0).random(), // Random longitude within range
                id = "$i", // Unique ID for the story
                lat = (-10.0..10.0).random() // Random latitude within range
            )
            items.add(story)
        }
        return items
    }

    // Extension function to generate a random double within a ClosedRange
    private fun ClosedRange<Double>.random() =
        Random().nextDouble() * (endInclusive - start) + start // Randomizes values within range
}
