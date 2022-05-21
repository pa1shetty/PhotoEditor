package com.example.photoeditor.ui

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.photoeditor.databinding.FragmentHomeScreenBinding
import com.example.photoeditor.utils.Common


class HomeScreen : Fragment() {

    private lateinit var binding: FragmentHomeScreenBinding
    private val viewModel by activityViewModels<HomeScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeScreenBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cvPhotoSelector.setOnClickListener {
            contract.launch("image/*")
        }
    }

    private val contract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            imageUri?.let {
                DocumentFile.fromSingleUri(requireContext(), imageUri)?.run {
                    viewModel.setPhotoDetails(
                        viewModel.getPhotoDetails().copy(
                            name = name.toString(),
                            path = parentFile.toString(),
                            size = Common.humanReadableByteCountSI(length()),
                            createdOn = Common.convertDate(lastModified().toString()),
                            type = type.toString()
                        )
                    )
                }
                viewModel.saveBitmap(getBitmap(requireActivity().contentResolver, imageUri))
                findNavController().navigate(HomeScreenDirections.actionHomeScreenToEditScreen())
            }
        }


    @Suppress("DEPRECATION")
    private fun getBitmap(contentResolver: ContentResolver, fileUri: Uri): Bitmap {
        return when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                contentResolver,
                fileUri
            )
            else -> {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri))
            }
        }
    }

}