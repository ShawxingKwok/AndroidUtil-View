package pers.shawxingkwok.sample.ui.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dylanc.viewbinding.nonreflection.binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
            Contact(151_9054_8591, "Apollo", R.drawable.apollo),
            Contact(161_9054_8535, "Bob", R.drawable.bob),
            Contact(161_9154_8535, "Bill", R.drawable.bill),
            Contact(131_9054_8535, "David", R.drawable.david),
            Contact(161_9054_8555, "Jack", R.drawable.jack),
            Contact(194_9054_8535, "James", R.drawable.james),
            Contact(199_9054_8534, "John", R.drawable.john),
            Contact(131_9054_8535, "Lily", R.drawable.lily),
            Contact(122_9054_8535, "Luna", R.drawable.luna),
            Contact(141_9054_8535, "Mark", R.drawable.mark),
            Contact(125_9054_8535, "Michael", R.drawable.michael),
            Contact(135_9054_8535, "Richard", R.drawable.richard),
            Contact(133_9054_8535, "Robert", R.drawable.robert),
            Contact(169_9054_8535, "Thomas", R.drawable.thomas),
            Contact(175_9054_8535, "Tina", R.drawable.tina),
            Contact(162_9054_8535, "Sora", R.drawable.sora),
            Contact(157_9054_8535, "->Tiffany", R.drawable.tiffany),
            Contact(186_9054_8535, "☺Aki", R.drawable.aki),
        )
        .let(::flowOf)

    private val contactsAdapter = ContactsAdapter()

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
             * Since data may change too frequently, the previous passed lambda may be omitted,
             * which is an optimization.
             */
            contactsAdapter.update()
        }
    }
}