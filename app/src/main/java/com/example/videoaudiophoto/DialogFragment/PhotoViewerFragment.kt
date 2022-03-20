package com.example.videoaudiophoto.DialogFragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.videoaudiophoto.R
import com.example.videoaudiophoto.databinding.FragmentPhotoViewerBinding
import com.squareup.picasso.Picasso

class PhotoViewerFragment(private val imageBitmap: Bitmap) : DialogFragment() {

    private lateinit var binding: FragmentPhotoViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPhotoViewerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageContainer.setImageBitmap(imageBitmap)
        this.dialog!!.setCanceledOnTouchOutside(true)
    }

    companion object {
        const val TAG = "Photo viewer dialog fragment"
    }

}