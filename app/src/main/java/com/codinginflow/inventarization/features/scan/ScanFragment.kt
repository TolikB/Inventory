package com.codinginflow.inventarization.features.scan

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.codinginflow.inventarization.*
import com.codinginflow.inventarization.FileSelector.MP3
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
                nameId.text = it.itemName?.toEditable()
                userNameId.text = it.userName?.toEditable()
                otdelId.text = it.otdel?.toEditable()
                departmantId.text = it.deportament?.toEditable()
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
        if (requestCode == 9999 && resultCode == RESULT_OK) {
            val fileUris = data?.data ?: data?.data
            if (fileUris != null) {
                viewModel.loadNewFile(fileUris.toString(),requireContext())
            }
        }
        if (requestCode == 8887 && result.contents != null) {
            Log.e("onActivityResult", " !!!!!!!!!!!!!!!! " + Gson().toJson(result))
            viewModel.onActivityResult(result)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openScanner() {
        IntentIntegrator.forSupportFragment(this).setRequestCode(8887).initiateScan()
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
            R.id.newFile -> {
                loadNewFile()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openStorageAccess() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(Intent.ACTION_OPEN_DOCUMENT_TREE, true)
        }
        startActivityForResult(Intent.createChooser(intent, "Choose file"), 9999)
    }

    private fun loadNewFile() {
        openStorageAccess()
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
        if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                2
            )
        }
    }
}