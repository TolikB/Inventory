package com.codinginflow.inventarization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.codinginflow.inventarization.features.invt.InventarListFragment
import com.codinginflow.inventarization.features.scan.ScanFragment
import com.codinginflow.inventarization.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var inventarListFragment: InventarListFragment
    private lateinit var scanningFragment: ScanFragment


    private val fragments: Array<Fragment>
        get() = arrayOf(
            scanningFragment,
            inventarListFragment

        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }
        transaction.commit()

        title = when (selectedFragment) {
            is ScanFragment -> "Scan"
            is InventarListFragment -> "List"
            else -> ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            scanningFragment = ScanFragment()
            inventarListFragment = InventarListFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, scanningFragment, TAG_SCANNING_FRAGMENT)
                .add(R.id.fragment_container, inventarListFragment, TAG_LIST_FRAGMENT)
                .commit()
        } else {
            inventarListFragment =
                supportFragmentManager.findFragmentByTag(TAG_LIST_FRAGMENT) as InventarListFragment
            scanningFragment =
                supportFragmentManager.findFragmentByTag(TAG_SCANNING_FRAGMENT) as ScanFragment

            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
        }

        selectFragment(selectedFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_list -> inventarListFragment
                R.id.nav_scan -> scanningFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            if (selectedFragment === fragment) {
                if (fragment is OnBottomNavigationFragmentReselectedListener) {
                    fragment.onBottomNavigationFragmentReselected()
                }
            } else {
                selectFragment(fragment)
            }
            true
        }
    }

    interface OnBottomNavigationFragmentReselectedListener {
        fun onBottomNavigationFragmentReselected()
    }

    override fun onBackPressed() {
        if (selectedIndex != 0) {
            binding.bottomNav.selectedItemId = R.id.nav_scan
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }

}

private const val TAG_LIST_FRAGMENT = "TAG_LIST_FRAGMENT"
private const val TAG_SCANNING_FRAGMENT = "TAG_SCANNING_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"
