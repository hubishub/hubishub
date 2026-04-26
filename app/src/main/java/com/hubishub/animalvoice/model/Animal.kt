package com.hubishub.animalvoice.model

import androidx.annotation.DrawableRes
import com.hubishub.animalvoice.R

data class Animal(
    val id: Int,
    val name: String,
    val emoji: String,
    val pitch: Float,       // Pitch multiplier (1.0 = normal)
    val speed: Float,       // Speed multiplier (1.0 = normal)
    val description: String,
    @DrawableRes val iconRes: Int = R.drawable.ic_paw
)

object AnimalData {
    val animals = listOf(
        Animal(
            id = 1,
            name = "Dog",
            emoji = "🐶",
            pitch = 1.4f,
            speed = 1.2f,
            description = "Woof! Playful & excited",
            iconRes = R.drawable.ic_dog
        ),
        Animal(
            id = 2,
            name = "Cat",
            emoji = "🐱",
            pitch = 1.7f,
            speed = 0.85f,
            description = "Meow~ Soft & mysterious",
            iconRes = R.drawable.ic_cat
        ),
        Animal(
            id = 3,
            name = "Lion",
            emoji = "🦁",
            pitch = 0.55f,
            speed = 0.75f,
            description = "ROAR! Deep & powerful",
            iconRes = R.drawable.ic_lion
        ),
        Animal(
            id = 4,
            name = "Bird",
            emoji = "🐦",
            pitch = 2.8f,
            speed = 1.4f,
            description = "Tweet! Tiny & chirpy",
            iconRes = R.drawable.ic_bird
        ),
        Animal(
            id = 5,
            name = "Elephant",
            emoji = "🐘",
            pitch = 0.35f,
            speed = 0.65f,
            description = "TRUMPET! Massive & majestic",
            iconRes = R.drawable.ic_elephant
        ),
        Animal(
            id = 6,
            name = "Monkey",
            emoji = "🐒",
            pitch = 1.5f,
            speed = 1.45f,
            description = "Ooh ooh! Wild & funny",
            iconRes = R.drawable.ic_monkey
        ),
        Animal(
            id = 7,
            name = "Wolf",
            emoji = "🐺",
            pitch = 0.65f,
            speed = 0.9f,
            description = "Awoo~ Dark & howling",
            iconRes = R.drawable.ic_wolf
        ),
        Animal(
            id = 8,
            name = "Duck",
            emoji = "🦆",
            pitch = 1.65f,
            speed = 1.15f,
            description = "Quack! Funny & waddly",
            iconRes = R.drawable.ic_duck
        ),
        Animal(
            id = 9,
            name = "Frog",
            emoji = "🐸",
            pitch = 0.8f,
            speed = 1.0f,
            description = "Ribbit~ Croaky & chill",
            iconRes = R.drawable.ic_frog
        ),
        Animal(
            id = 10,
            name = "Bear",
            emoji = "🐻",
            pitch = 0.45f,
            speed = 0.7f,
            description = "GROWL! Huge & grumbly",
            iconRes = R.drawable.ic_bear
        ),
        Animal(
            id = 11,
            name = "Dolphin",
            emoji = "🐬",
            pitch = 2.2f,
            speed = 1.3f,
            description = "Eeee! Smart & squeaky",
            iconRes = R.drawable.ic_dolphin
        ),
        Animal(
            id = 12,
            name = "Parrot",
            emoji = "🦜",
            pitch = 1.9f,
            speed = 1.1f,
            description = "Squawk! Loud & colorful",
            iconRes = R.drawable.ic_parrot
        )
    )
}
