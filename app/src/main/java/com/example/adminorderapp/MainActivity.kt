package com.example.adminorderapp

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AlertDialogLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.adminorderapp.databinding.ActivityMainBinding
import com.example.adminorderapp.ui.BottomSheetDialogFragmentManager
import com.example.adminorderapp.ui.HomeFragmentDirections
import com.example.adminorderapp.util.DataStoreUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(),OnNavigateToHomeListener{
    private lateinit var binding : ActivityMainBinding
    @Inject lateinit var dataStoreUtil: DataStoreUtil
    private lateinit var navController : NavController
    private val viewModel by viewModels<ActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val toolBar = binding.toolBar
        setSupportActionBar(toolBar)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHost.navController

        val graph : NavGraph = if(dataStoreUtil.getToken() != null){
            viewModel.navigateToHome()
            navController.navInflater.inflate(R.navigation.home_graph)
        } else{
            navController.navInflater.inflate(R.navigation.nav_graph)
        }
        toolBar.setupWithNavController(navController,AppBarConfiguration(setOf(R.id.home_graph)))
        navController.graph = graph

        viewModel.uiState.observe(this){
            binding.toolBar.visibility = if(it.isShownToolbar) View.VISIBLE else View.GONE
        }

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_menu_button -> {
                AlertDialog
                    .Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_message))
                    .setPositiveButton(getString(R.string.cancel_label)) {
                            dialog, _ -> dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.yes_label)){ _, _ ->
                            dataStoreUtil.deleteUserInfo()
                            val graph = navController.navInflater.inflate(R.navigation.nav_graph)
                            navController.graph = graph
                            viewModel.navigateToLogin()

                    }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigateToHome() {
        viewModel.navigateToHome()
    }
}
interface OnNavigateToHomeListener{
    fun onNavigateToHome()
}
