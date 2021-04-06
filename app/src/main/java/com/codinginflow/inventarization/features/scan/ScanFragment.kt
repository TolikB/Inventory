package com.codinginflow.inventarization.features.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codinginflow.inventarization.MainActivity
import com.codinginflow.inventarization.R
import com.codinginflow.inventarization.afterTextChanged
import com.codinginflow.inventarization.toEditable
import com.codinginflow.inventarization.databinding.FragmentScanningBinding
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ScanFragment : Fragment(R.layout.fragment_scanning),
    MainActivity.OnBottomNavigationFragmentReselectedListener {

    private val viewModel: ScanViewModel by viewModels()
    private var currentBinding: FragmentScanningBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBinding = FragmentScanningBinding.bind(view)
        setHasOptionsMenu(true)
        viewModel.currentInventar.observe(viewLifecycleOwner, Observer {
            binding.apply {
                inventarId.text = it.myid
                nameId.text = it.itemName
                userNameId.text = it.userName?.toEditable()
                otdelId.text = it.otdel
                departmantId.text = it.deportament
            }
        })
        binding.scanFab.setOnClickListener {
            openScanner()
            binding.save.visibility = View.GONE
        }
        viewModel.fillList()
        binding.userNameId.afterTextChanged {
            binding.save.visibility = View.VISIBLE
        }
        binding.save.setOnClickListener {
            var inv = viewModel.currentInventar.value
            inv?.let {
                inv.userName = binding.userNameId.text.toString()
                inv.notes = binding.notes.text.toString()
                inv.color = 1
                viewModel.update(inv)
            }
        }
        viewModel.error.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        isStoragePermissionGranted()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result.contents != null) {
            Log.e("onActivityResult"," !!!!!!!!!!!!!!!! "+Gson().toJson(result))
            viewModel.onActivityResult(result)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun openScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onBottomNavigationFragmentReselected() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_font_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.startNew -> {
                viewModel.startNewInventarization()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun isStoragePermissionGranted() {
        if (checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }
}