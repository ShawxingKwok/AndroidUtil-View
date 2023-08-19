package pers.shawxingkwok.sample.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dylanc.viewbinding.nonreflection.binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pers.shawxingkwok.androidutil.view.collectOnResume
import pers.shawxingkwok.ktutil.fastLazy
import pers.shawxingkwok.sample.R
import pers.shawxingkwok.sample.databinding.FragmentMainBinding

class ContactsFragment : Fragment(R.layout.fragment_main) {
    private val binding by binding(FragmentMainBinding::bind)

    // simulated contacts sorted by initials in which special characters are at last,
    // got from database and updated from remote in real cases
    private val contacts: Flow<List<Contact>> =
        listOf(
            Contact(0, "Apollo", R.drawable.apollo),
            Contact(1, "Bob", R.drawable.bob),
            Contact(2, "Bill", R.drawable.bill),
            Contact(4, "David", R.drawable.david),
            Contact(5, "James", R.drawable.james),
            Contact(6, "John", R.drawable.john),
            Contact(7, "Lily", R.drawable.lily),
            Contact(8, "Luna", R.drawable.luna),
            Contact(9, "Mark", R.drawable.mark),
            Contact(10, "Michael", R.drawable.michael),
            Contact(11, "Richard", R.drawable.richard),
            Contact(12, "Robert", R.drawable.robert),
            Contact(13, "Thomas", R.drawable.thomas),
            Contact(14, "Tina", R.drawable.tina),
            Contact(15, "Sora", R.drawable.sora),
            Contact(16, "->Tiffany", R.drawable.tiffany),
            Contact(17, "☺Aki", R.drawable.aki),
        )
        .let(::flowOf)

    private val contactsAdapter by fastLazy(::ContactsAdapter)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.run {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        contacts.collectOnResume{
            contactsAdapter.contacts = it

            /**
             * Use `update()` or `update{ ... }` insteadOf `notify...` after data changes.
             *
             * The lambda is called after the recyclerview submits the update to screen.
             * Since data may change too frequently, the previous passed lambda may be omitted.
             */
            contactsAdapter.update()
        }
    }
}