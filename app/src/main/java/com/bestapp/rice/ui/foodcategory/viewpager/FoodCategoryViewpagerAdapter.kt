package com.bestapp.rice.ui.foodcategory.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bestapp.rice.model.FilterUiState


//어댑터 작성시 최대한 List를 생성자로 넘기지 말것!! 넘길 이유가 없다.
//정말 넘겨야 된다면 함수로 넘길것!!
class FoodCategoryViewpagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
//    private val foodUiStateList: List<FilterUiState.FoodUiState>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = mutableListOf<Fragment>()

    // 페이지 갯수 설정
    override fun getItemCount(): Int = fragments.size

    // 불러올 Fragment 정의
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment() {
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
    }

//    fun getTabTitle(position: Int): String = foodUiStateList[position].name
//    fun getTabIcon(position: Int): String = foodUiStateList[position].icon
}