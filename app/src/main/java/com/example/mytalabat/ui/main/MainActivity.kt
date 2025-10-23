package com.example.mytalabat.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mytalabat.R
import com.example.mytalabat.databinding.ActivityMainBinding
import com.example.mytalabat.ui.home.HomeFragment
import com.example.mytalabat.ui.profile.ProfileFragment

// NOTE: You will likely need to create an OrdersFragment for the 'navOrders' item
// import com.example.mytalabat.ui.orders.OrdersFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    /**
     * Sets up click listeners for the custom navigation bar items (LinearLayouts)
     * instead of using a standard BottomNavigationView.
     */
    private fun setupBottomNavigation() {
        // The IDs from the XML are navShop, navOrders, and navProfile.

        // 1. Setup 'Shop' (Home) button click
        binding.navShop.setOnClickListener {
            loadFragment(HomeFragment())
            // You may add logic here to highlight the 'Shop' item visually
        }

        // 2. Setup 'Orders' button click
        binding.navOrders.setOnClickListener {
            // NOTE: You need to create and import OrdersFragment
            // loadFragment(OrdersFragment())

            // For now, it will load the HomeFragment, replace this later:
            loadFragment(HomeFragment()) // Placeholder: Replace with OrdersFragment()
            // You may add logic here to highlight the 'Orders' item visually
        }

        // 3. Setup 'Profile' button click
        binding.navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
            // You may add logic here to highlight the 'Profile' item visually
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
