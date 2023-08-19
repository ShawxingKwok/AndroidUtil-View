package pers.shawxingkwok.sample.ui.main

import android.annotation.SuppressLint
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.androidutil.view.onClick
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.ItemContactBinding
import pers.shawxingkwok.sample.databinding.ItemContactsNumberBinding
import pers.shawxingkwok.sample.databinding.ItemInitialBinding
import pers.shawxingkwok.sample.databinding.ItemSearchBinding

class ContactsAdapter : KRecyclerViewAdapter() {
    // mutable data source
    var contacts: List<Contact> = emptyList()

    /**
     * Not essential.
     *
     * Makes some bindings unrelated to [contacts] in freshly created viewHolders.
     * Though it's more efficient, I suggest only doing time-consuming tasks here.
     */
    override fun onHoldersCreated() {
        HolderProcessor(ItemSearchBinding::inflate){
            // make fixed process to search item
        }
        HolderProcessor(ItemContactBinding::inflate){
            // make fixed process to initial tag item
        }
        // ... process other items if needed
    }

    /**
     * Arranges viewHolders as [List] with
     *
     * `HolderBinder(XxBinding::inflate, id, contentId){ holder -> }`
     */
    @SuppressLint("SetTextI18n")
    override fun arrange() {
        // Search bar
        HolderBinder(
            ItemSearchBinding::inflate,
            id = null, /* Distinguishes among same kind of items.
                          id could be same across different kinds of items,
                          and null if the item's viewBinding type is unique as ItemSearchBinding in this case.
                       */
            contentId = null // Notifies content to update. contentId could be null if the content is fixed.
        ){
            it.itemView.onClick {
                // ...
            }
        }

        // New friends
        HolderBinder(ItemContactBinding::inflate, "New friends", null){
            it.binding.imgAvatar.setImageResource(R.drawable.newfriend)
            it.binding.tv.text = "New friend" // probably get from resources in real cases
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
            HolderBinder(
                inflate = ItemContactBinding::inflate,
                id = contact.id,
                contentId = contact.name // The item data's class is generally a data class,
                                         // allowing for using contact directly which is a little less efficient.
            ){
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