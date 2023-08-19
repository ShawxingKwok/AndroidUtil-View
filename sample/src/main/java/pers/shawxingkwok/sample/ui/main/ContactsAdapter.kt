package pers.shawxingkwok.sample.ui.main

import android.annotation.SuppressLint
import android.util.Log
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter.HolderBinder
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter.HolderCreator
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.ItemContactBinding
import pers.shawxingkwok.sample.databinding.ItemContactsNumberBinding
import pers.shawxingkwok.sample.databinding.ItemInitialBinding
import pers.shawxingkwok.sample.databinding.ItemSearchBinding
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class ContactsAdapter : KRecyclerViewAdapter() {
    // source data
    var contacts = emptyList<Contact>()

    /**
     * Build process-required [HolderCreator]s in this function.
     * It's more efficient but not essential. I suggest only doing time-consuming tasks here.
     */
    override fun registerProcessRequiredHolderCreators() {
        // Take the example of making fixed process to search item.
        // ItemSearchBinding is the generated ViewBinding class.
        HolderCreator(ItemSearchBinding::inflate){
            // ...
        }
    }

    /**
     * Build [HolderBinder] in this function according to the order.
     */
    @SuppressLint("SetTextI18n")
    override fun arrangeHolderBinders() {
        // Search bar
        HolderBinder(
            inflate = ItemSearchBinding::inflate,
            id = null, // Distinguishes among HolderBinders sharing same [inflate].
                       // id is suggested null if the [inflate] is unique.
            contentId = null // Notifies content to update.
                             // contentId is suggested null if the content is fixed.
        ){
            it.itemView.onClick {
                // ...
            }
        }

        // New friends
        HolderBinder(ItemContactBinding::inflate, "New friends", null){
            it.binding.imgAvatar.setImageResource(R.drawable.newfriend)
            it.binding.tv.text = "New friends" // probably got from resources in real cases
            it.itemView.onClick {
                // ...
            }
        }

        // Group chats
        HolderBinder(ItemContactBinding::inflate, "Group chats", null){
            it.binding.imgAvatar.setImageResource(R.drawable.groupchat)
            it.binding.tv.text = "Group chats"
            it.itemView.onClick {
                // ...
            }
        }

        // Contacts mixed with initial tags
        var initialTag: Char? = null
        contacts.forEach { contact ->
            val nameInitial = contact.name.first().uppercaseChar().takeIf { it.isLetter() } ?: '#'

            // initial tag
            if (initialTag != nameInitial) {
                initialTag = nameInitial
                HolderBinder(ItemInitialBinding::inflate, initialTag, initialTag){
                    // don't call the mutable initialTag in this lambda
                    it.binding.tv.text = nameInitial.toString()
                }
            }

            // contact
            HolderBinder(ItemContactBinding::inflate, contact.id, contact){
                it.binding.imgAvatar.setImageResource(contact.avatar)
                it.binding.tv.text = contact.name
                it.itemView.onClick {
                    // ...
                }
            }
        }

        // Contacts number
        HolderBinder(ItemContactsNumberBinding::inflate, null, null){
            // root is TextView in this case
            it.binding.root.text = "${contacts.size} friends"
        }
    }
}