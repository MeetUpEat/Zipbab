package com.bestapp.zipbab.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.bestapp.zipbab.R
import com.bestapp.zipbab.databinding.ActivityMainBinding
import com.bestapp.zipbab.ui.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import com.bestapp.zipbab.ui.setting.SettingFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUi()
        setNavController()
    }

    private fun setUi() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.bnv.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = 0)
            insets
        }
    }

    private fun setNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fcv) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bnv.setupWithNavController(navController)

        // destination에 따라 BottomNavigationView show and hide
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.settingFragment, R.id.meetUpMapFragment -> {
                    binding.bnv.visibility = View.VISIBLE
                }

                else -> {
                    binding.bnv.visibility = View.GONE
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.fcv)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return super.dispatchTouchEvent(ev)
        }
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fcv)?.childFragmentManager?.fragments?.lastOrNull()
                ?: run {
                    return super.dispatchTouchEvent(ev)
                }

        return when (fragment) {
            is ProfileFragment -> {
                if (fragment.dispatchTouchEvent(ev)) {
                    true
                } else {
                    super.dispatchTouchEvent(ev)
                }
            }

            else -> return super.dispatchTouchEvent(ev)
        }
    }
}