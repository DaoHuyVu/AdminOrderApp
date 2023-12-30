package com.example.adminorderapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.adminorderapp.util.DataStoreUtil
import com.example.adminorderapp.R
import com.example.adminorderapp.databinding.FragmentHomeBinding
import com.example.adminorderapp.ui.category.CategoryFragment
import com.example.adminorderapp.ui.manager.ManagerFragment
import com.example.adminorderapp.ui.menuItem.MenuItemFragment
import com.example.adminorderapp.ui.revenue.RevenueFragment
import com.example.adminorderapp.ui.shipper.ShipperFragment
import com.example.adminorderapp.ui.store.StoreFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var dataStoreUtil: DataStoreUtil
    private lateinit var viewPager : ViewPager2
    private lateinit var tab : TabLayout
    private lateinit var tabLabel : List<String>
    private val viewModel by viewModels<HomeViewModel>()
    private var positionTab = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        tabLabel = resources.getStringArray(R.array.tab_label).toList()
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tab = binding.tabLayout
        viewPager = binding.pager
        val adapter = HomeStateAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tab,viewPager){ tab,position ->
            tab.text = tabLabel[position]
        }.attach()
        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: Tab?) {
                positionTab = tab!!.position
                when(positionTab){
                    5 -> viewModel.navigateToRevenue()
                    else -> viewModel.navigateToOther()
                }
            }
            override fun onTabUnselected(tab: Tab?) {}
            override fun onTabReselected(tab: Tab?) {}
        })
        binding.fab.setOnClickListener {
            val navController = findNavController()
            when(positionTab){
                0 -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToCreateManagerFragment())
                1 -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToCreateShipperFragment())
                2 -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToCreateMenuItemFragment())
                3 -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToCreateStoreFragment())
                4 -> navController.navigate(HomeFragmentDirections.actionHomeFragmentToCreateCategoryFragment2())
                5 -> {}
            }
        }
        viewModel.isShownFab.observe(viewLifecycleOwner){
            binding.fab.visibility = if(it) View.VISIBLE else View.INVISIBLE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    class HomeStateAdapter(private val fragment : HomeFragment) : FragmentStateAdapter(fragment){
        override fun getItemCount(): Int {
            return fragment.tabLabel.size
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ManagerFragment()
                1 -> ShipperFragment()
                2 -> MenuItemFragment()
                3 -> StoreFragment()
                4 -> CategoryFragment()
                5 -> RevenueFragment()
                else -> throw IllegalStateException()
            }
        }

    }
}