package com.example.photoeditor.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.R
import com.example.photoeditor.databinding.BsdCropBinding
import com.example.photoeditor.databinding.BsdDetailsBinding
import com.example.photoeditor.databinding.FragmentEditScreenBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


class EditScreen : Fragment() {

    private lateinit var binding: FragmentEditScreenBinding
    private val viewModel by activityViewModels<HomeScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetDialog = BottomSheetDialog(requireContext())
        setUpObserver()
        setUpClicks()
    }

    private fun setUpObserver() {
        viewModel.imageBitmap.observe(viewLifecycleOwner) {
            binding.ivImage.setImageBitmap(it)
        }
    }

    private fun setUpClicks() {
        binding.btnSave.setOnClickListener { saveFile() }
        binding.ivFlipLeft.setOnClickListener {
            viewModel.flipLeft()
        }
        binding.ivFlipRight.setOnClickListener {
            viewModel.flipRight()
        }
        binding.ivCrop.setOnClickListener {
            cropDialogue()
        }
        binding.btnCancel.setOnClickListener { findNavController().navigate(EditScreenDirections.actionEditScreenToHomeScreen()) }
        binding.ivInfo.setOnClickListener {
            viewModel.imageBitmap.value?.run {
                viewModel.setPhotoDetails(
                    viewModel.getPhotoDetails()
                        .copy(resolution = "$width X $height")
                )
            }
            showDetails()
        }
    }


    private fun saveFile() {
        viewModel.imageBitmap.value?.let { image ->
            lifecycleScope.launch(Dispatchers.IO) {
                val path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).path + File.separator.toString() + "PE_${System.currentTimeMillis()}.jpg"
                val fos =
                    FileOutputStream(File(path))
                image.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.image_saved, path),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private fun showDetails() {
        val bottomSheetDialogLayoutBinding = BsdDetailsBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomSheetDialogLayoutBinding.root)
        with(viewModel.getPhotoDetails()) {
            bottomSheetDialogLayoutBinding.tvName.text = getString(R.string.name_data, name)
            bottomSheetDialogLayoutBinding.tvType.text = getString(R.string.type_data, type)
            bottomSheetDialogLayoutBinding.tvResolution.text =
                getString(R.string.resolution_data, resolution)
            bottomSheetDialogLayoutBinding.tvSize.text = getString(R.string.size_data, size)
            bottomSheetDialogLayoutBinding.tvCreatedOn.text =
                getString(R.string.created_on_data, createdOn)
        }
        bottomSheetDialog.show()
    }

    private fun cropDialogue() {
        val bsdCropBinding = BsdCropBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bsdCropBinding.root)
        bsdCropBinding.btnSave.setOnClickListener {
            if (bsdCropBinding.tietXAxis.text?.isBlank() == true
            ) {
                bsdCropBinding.tilXAxis.error = getString(R.string.cannot_be_empty)
            } else if (bsdCropBinding.tietYAxis.text?.isBlank() == true
            ) {
                bsdCropBinding.tilYAxis.error = getString(R.string.cannot_be_empty)
            } else if (bsdCropBinding.tietWidth.text?.isBlank() == true || bsdCropBinding.tietWidth.text.toString()
                    .toInt() == 0
            ) {
                bsdCropBinding.tilWidth.error = getString(R.string.cannot_be_empty_or_zero)
            } else if (bsdCropBinding.tietHeight.text?.isBlank() == true || bsdCropBinding.tietHeight.text.toString()
                    .toInt() == 0
            ) {
                bsdCropBinding.tietHeight.error = getString(R.string.cannot_be_empty_or_zero)
            } else {
                with(viewModel.imageBitmap.value) {
                    this?.let {
                        if ((bsdCropBinding.tietXAxis.text.toString()
                                .toLong() + bsdCropBinding.tietWidth.text.toString()
                                .toLong()) > width
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Sum of ${getString(R.string.x_axis)} + ${getString(R.string.width)} must be lesser than $width",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (bsdCropBinding.tietYAxis.text.toString()
                                .toLong() + bsdCropBinding.tietHeight.text.toString()
                                .toLong() > height
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Sum of ${getString(R.string.y_axis)} + ${getString(R.string.height)} must be lesser than $height",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.cropImage(
                                bsdCropBinding.tietXAxis.text.toString().toInt(),
                                bsdCropBinding.tietYAxis.text.toString().toInt(),
                                bsdCropBinding.tietWidth.text.toString().toInt(),
                                bsdCropBinding.tietHeight.text.toString().toInt()
                            )
                            bottomSheetDialog.cancel()
                        }
                    }
                }
            }

            bsdCropBinding.tietXAxis.doOnTextChanged { _, _, _, _ ->
                bsdCropBinding.tilXAxis.error = null
            }
            bsdCropBinding.tietYAxis.doOnTextChanged { _, _, _, _ ->
                bsdCropBinding.tilYAxis.error = null
            }
            bsdCropBinding.tietWidth.doOnTextChanged { _, _, _, _ ->
                bsdCropBinding.tilWidth.error = null
            }
            bsdCropBinding.tietHeight.doOnTextChanged { _, _, _, _ ->
                bsdCropBinding.tilHeight.error = null
            }
        }
        bottomSheetDialog.show()
    }

}