package com.codinginflow.inventarization.features.invt

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinginflow.inventarization.MainActivity
import com.codinginflow.inventarization.R
import com.codinginflow.inventarization.databinding.FragmentInventarListBinding
import com.codinginflow.inventarization.features.invt.shared.InventarListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InventarListFragment : Fragment(R.layout.fragment_inventar_list),
    MainActivity.OnBottomNavigationFragmentReselectedListener {

    private val viewModel: InventarListViewModel by viewModels()

    private var currentBinding: FragmentInventarListBinding? = null
    private val binding get() = currentBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentBinding = FragmentInventarListBinding.bind(view)
        createFolder()
        setHasOptionsMenu(true)
        val invtAdapter = InventarListAdapter(
            onItemClick = { invt ->

            }
        )
        binding.apply {
            recyclerView.apply {
                adapter = invtAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.invtList.collect {
                    val result = it ?: return@collect
                    recyclerView.isVisible = !result.isNullOrEmpty()
                    invtAdapter.submitList(result) {
                        if (viewModel.pendingScrollToTopAfterRefresh) {
                            recyclerView.scrollToPosition(0)
                            viewModel.pendingScrollToTopAfterRefresh = false
                        }
                    }
                }
            }
        }
        viewModel.path.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            sendEmailWithAttachment(it)
        })
    }


    override fun onBottomNavigationFragmentReselected() {
        binding.recyclerView.scrollToPosition(0)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                viewModel.sendResultToMail()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        currentBinding = null
    }

    private fun sendEmailWithAttachment(path: String?) {
        val imageUri = FileProvider.getUriForFile(
            requireContext(),
            "com.codinginflow.inventarization.provider",
            File(path)
        )
        val sdf = SimpleDateFormat("yyyy:MM:dd_HH:mm")
        val date = sdf.format(Date())
        val intent1 = Intent(Intent.ACTION_SEND)
        intent1.type = "application/excel"
        intent1.putExtra(Intent.EXTRA_SUBJECT, "Инвентаризационный лист")
        intent1.putExtra(Intent.EXTRA_TEXT, date)
        intent1.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(intent1, "Send email..."))
    }

    private fun createFolder() {
        requireContext().applicationContext.getExternalFilesDir("exel")
    }
}