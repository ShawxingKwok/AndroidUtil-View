package pers.shawxingkwok.androidutil.view.demo

import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import pers.shawxingkwok.androidutil.view.KRecyclerViewAdapter
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemFooterBinding
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemHeaderBinding
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemPurpleBinding
import pers.shawxingkwok.androidutil.view.demo.databinding.ItemTealBinding

class RvAdapter(scope: CoroutineScope, var users: List<User>) : KRecyclerViewAdapter(scope) {
    override fun onBindHolders(binders: MutableList<HolderBinder<ViewBinding>>) {
        binders += HolderBinder(ItemHeaderBinding::class, null, null){}

        binders += users.mapIndexed { i, user ->
            if (i % 2 == 0)
                HolderBinder(
                    bindingKClass = ItemPurpleBinding::class,
                    id = user.id,
                    contentId = user
                ){
                    it.binding.tv.text = user.toString()
                }
            else
                HolderBinder(
                    bindingKClass = ItemTealBinding::class,
                    id = user.id,
                    contentId = user
                ){
                    it.binding.tv.text = user.toString()
                }
        }

        binders += HolderBinder(ItemFooterBinding::class, null, null){}
    }
}