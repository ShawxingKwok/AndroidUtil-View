package pers.shawxingkwok.contacts.ui.main

import android.annotation.SuppressLint
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.contacts.R
import pers.shawxingkwok.contacts.databinding.ItemContactBinding
import pers.shawxingkwok.contacts.databinding.ItemContactsNumberBinding
import pers.shawxingkwok.contacts.databinding.ItemInitialBinding
import pers.shawxingkwok.contacts.databinding.ItemSearchBinding

class ContactsAdapter : KRecyclerViewAdapter() {
    // source data
    var contacts = emptyList<Contact>()

    /**
     * [HolderCreator] could be automatically created for building
     * [KRecyclerViewAdapter.ViewBindingHolder] which is the subclass of [ViewHolder].
     * But there is no initial process. You could register process-required [HolderCreator]s
     * in this function to do some fixed tasks only once. It's more efficient but not essential.
     * I suggest only doing time-consuming tasks here.
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
            inflate = ItemSearchBinding::inflate, // is the function inflate in the
                                                  // corresponding ViewBinding subclass
            id = null, // Distinguishes among HolderBinders sharing same [inflate].
                       // id is suggested null if [inflate] is unique.
            contentId = null // Notifies content to update.
                             // contentId is suggested null if the content is fixed.
        ){ holder, _ ->
            holder.itemView.onClick {
                // ...
            }
        }

        // New friends
        HolderBinder(ItemContactBinding::inflate, "New friends", null){ holder , _ ->
            holder.binding.imgAvatar.setImageResource(R.drawable.newfriend)
            holder.binding.tv.text = "New friends" // probably got from resources in real cases
            holder.itemView.onClick {
                // ...
            }
        }

        // Group chats
        HolderBinder(ItemContactBinding::inflate, "Group chats", null){ holder , _ ->
            holder.binding.imgAvatar.setImageResource(R.drawable.groupchat)
            holder.binding.tv.text = "Group chats"
            holder.itemView.onClick {
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
                HolderBinder(ItemInitialBinding::inflate, initialTag, initialTag){ holder, _ ->
                    // don't call the mutable initialTag in this lambda
                    holder.binding.tv.text = nameInitial.toString()
                }
            }

            // contact
            HolderBinder(ItemContactBinding::inflate, contact.id, contact){ holder, _ ->
                holder.binding.imgAvatar.setImageResource(contact.avatar)
                holder.binding.tv.text = contact.name
                holder.itemView.onClick {
                    // ...
                }
            }
        }

        // Contacts number
        HolderBinder(ItemContactsNumberBinding::inflate, null, contacts.size){ holder, _ ->
            // root is TextView in this case
            holder.binding.root.text = "${contacts.size} friends"
        }
    }
}