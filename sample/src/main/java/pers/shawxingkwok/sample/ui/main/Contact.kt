package pers.shawxingkwok.sample.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

data class Contact(
    val id: Long,
    val name: String,
    @DrawableRes val avatar: Int, // bitmap path in real cases
)