package com.example.events

object EventRepository {
    val eventList = listOf(
        Event(
            id = 1,
            imageResId = R.drawable.music_concert,
            title = "Music Concert",
            description = "Experience live music with top artists.",
            artist = "Ed Sheeran",
            dateTime = "April 20, 2025 | 7:00 PM",
            venue = "City Arena, Mumbai",
            price = "10 - ₹50"
        ),
        Event(
            id = 2,
            imageResId = R.drawable.talk_show,
            title = "Talk Show",
            description = "Engage with industry experts in an interactive talk show.",
            artist = "Sandeep Maheshwari",
            dateTime = "April 25, 2025 | 5:00 PM",
            venue = "Auditorium, Delhi",
            price = "20 - ₹80"
        ),
        Event(
            id = 3,
            imageResId = R.drawable.comedy_night,
            title = "Comedy Night",
            description = "Laugh out loud with top stand-up comedians.",
            artist = "Zakir Khan",
            dateTime = "April 30, 2025 | 8:00 PM",
            venue = "Laugh Club, Bangalore",
            price = "30 - ₹90"
        ),
        Event(
            id = 4,
            imageResId = R.drawable.movie_mania,
            title = "Movie Mania",
            description = "Exclusive movie screenings with special guests.",
            artist = "Christopher Nolan (Director)",
            dateTime = "May 5, 2025 | 6:30 PM",
            venue = "IMAX Theatre, Chennai",
            price = "40 - ₹100"
        ),
        Event(
            id = 5,
            imageResId = R.drawable.story_teller,
            title = "Story Teller",
            description = "Hear mesmerizing stories from famous storytellers.",
            artist = "Ruskin Bond",
            dateTime = "May 10, 2025 | 4:00 PM",
            venue = "Book Cafe, Kolkata",
            price = "50 - ₹110"
        ),
        Event(
            id = 6,
            imageResId = R.drawable.horror_show,
            title = "Horror Show",
            description = "Experience spine-chilling horror stories live.",
            artist = "Horror Fiction Writers",
            dateTime = "May 15, 2025 | 9:00 PM",
            venue = "Mystery Hall, Pune",
            price = "60 - ₹130"
        ),
        Event(
            id = 7,
            imageResId = R.drawable.celebrity_event,
            title = "Celebrity Event",
            description = "Meet and greet with your favorite celebrities.",
            artist = "Bollywood Celebrities",
            dateTime = "May 20, 2025 | 7:00 PM",
            venue = "JW Marriott, Hyderabad",
            price = "2,999 - ₹9,999"
        )
    )
}
