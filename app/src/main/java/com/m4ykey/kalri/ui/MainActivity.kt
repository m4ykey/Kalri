package com.m4ykey.kalri.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.m4ykey.kalri.R
import com.m4ykey.kalri.databinding.ActivityMainBinding
import com.m4ykey.kalri.databinding.LayoutAddItemDialogBinding
import com.m4ykey.kalri.metronome.MetronomeViewModel
import com.m4ykey.kalri.ui.presets.MetronomeAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel : MetronomeViewModel by viewModel()

    private val metronomeAdapter by lazy {
        MetronomeAdapter(
            onItemClick = { item ->
                viewModel.setBpm(item.bpm)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
    }

    private fun setupViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.bpm.collect { bpm ->
                        binding.txtBpm.text = bpm.toString()

                        if (binding.sliderBPM.value != bpm.toFloat()) {
                            binding.sliderBPM.value = bpm.toFloat()
                        }
                    }
                }

                launch {
                    viewModel.isRunning.collect { isRunning ->
                        binding.btnStart.text = if (isRunning) "Stop" else "Start"
                    }
                }

                launch {
                    viewModel.isSwitched.collect { isSwitched ->
                        if (binding.switchFilters.isChecked != isSwitched) {
                            binding.switchFilters.isChecked = isSwitched
                        }

                        binding.linearLayoutFilters.visibility = if (isSwitched) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.frequency.collect { freq ->
                        if (binding.sliderFreq.value != freq) {
                            binding.sliderFreq.value = freq
                        }
                    }
                }

                launch {
                    viewModel.gain.collect { gain ->
                        if (binding.sliderGain.value != gain) {
                            binding.sliderGain.value = gain
                        }
                    }
                }

                launch {
                    viewModel.presets.collect { presets ->
                        metronomeAdapter.submitList(presets)
                    }
                }
            }
        }
    }

    private fun setupUI() {
        binding.apply {
            val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private val deleteColor = android.graphics.Color.RED.toDrawable()
                private val deleteIcon = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.ic_delete
                )?.apply {
                    setTint(android.graphics.Color.WHITE)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.bindingAdapterPosition
                    val item = metronomeAdapter.currentList.getOrNull(position)

                    if (item != null) {
                        viewModel.deletePreset(item)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    if (dX < 0) {
                        val cornerRadius = 24f
                        val margin = 8f

                        val background = RectF(
                            itemView.right + dX + margin,
                            itemView.top + margin,
                            itemView.right - margin,
                            itemView.bottom - margin
                        )

                        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            color = Color.RED
                        }

                        c.drawRoundRect(
                            background,
                            cornerRadius,
                            cornerRadius,
                            paint
                        )

                        deleteIcon?.let { icon ->
                            val iconSize = (itemView.height * 0.35f).toInt()
                            val iconLeft = itemView.right - iconSize - (itemView.height - iconSize) / 2
                            val iconTop = itemView.top + (itemView.height - iconSize) / 2

                            icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize)
                            icon.draw(c)
                        }
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

            ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewBpms)

            recyclerViewBpms.apply {
                adapter = metronomeAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }

            fabAdd.setOnClickListener { displayDialog() }

            sliderBPM.addOnChangeListener { _, value, _ ->
                viewModel.setBpm(value.toInt())
            }

            btnAdd.setOnClickListener {
                viewModel.setBpm(viewModel.bpm.value + 1)
            }

            btnMinus.setOnClickListener {
                viewModel.setBpm(viewModel.bpm.value - 1)
            }

            btnStart.setOnClickListener {
                if (viewModel.isRunning.value) {
                    viewModel.stop()
                } else {
                    viewModel.start()
                }
            }

            sliderFreq.addOnChangeListener { _, value, _ ->
                viewModel.setFilterParams(value, sliderGain.value)
            }

            sliderGain.addOnChangeListener { _, value, _ ->
                viewModel.setFilterParams(sliderFreq.value, value)
            }

            switchFilters.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setSwitched(isChecked)
            }
        }
    }

    private fun displayDialog() {
        val dialogBinding = LayoutAddItemDialogBinding.inflate(layoutInflater)

        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(R.string.add_item)
            .setPositiveButton(R.string.save) { dialog, _ ->
                val bpm = dialogBinding.etBpm.text.toString().toIntOrNull()
                val name = dialogBinding.etName.text.toString()

                when {
                    bpm == null || bpm !in 1..300 -> {
                        dialogBinding.etBpm.error = getString(R.string.invalid_bpm)
                    }
                    name.isBlank() -> {
                        dialogBinding.etName.error = getString(R.string.name_required)
                    }
                    else -> {
                        viewModel.addPreset(name = name, bpm = bpm)
                        dialog.dismiss()
                    }
                }
            }
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        init {
            System.loadLibrary("kalri")
        }
    }
}