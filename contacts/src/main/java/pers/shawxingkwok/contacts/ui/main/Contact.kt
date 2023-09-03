package pers.shawxingkwok.contacts.ui.main

import androidx.annotation.DrawableRes

data class Contact(
    val id: Long, // is commonly used with the phone number
    val name: String,
    @DrawableRes val avatar: Int, // bitmap path in most real cases
)