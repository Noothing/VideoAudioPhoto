package com.example.videoaudiophoto

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.videoaudiophoto.DialogFragment.AudioViewerFragment
import com.example.videoaudiophoto.DialogFragment.PhotoViewerFragment
import com.example.videoaudiophoto.DialogFragment.VideoViewerFragment
import com.example.videoaudiophoto.databinding.ActivityMainBinding
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.InputStream


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageLoader: ImageLoader
    private var isCamera = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val config = ImageLoaderConfiguration
            .Builder(this)
            .build()

        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)

        binding.staticPhoto.setOnClickListener {
            val imageBitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
            openImageFragment(imageBitmap)
        }

        binding.urlPhoto.setOnClickListener {
            val url =
                "https://sun9-39.userapi.com/impg/EzY30IDIBmjPFVGfVhEhfNdtL7Pfhxv6cTyNqA/D2oq44KqdXc.jpg?size=1080x1080&quality=96&sign=b77526500a38b66a5309f44755d2e69b&type=album"
            imageLoader.loadImage(url, object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(
                    imageUri: String?,
                    view: View?,
                    loadedImage: Bitmap?
                ) {
                    if (loadedImage != null) {
                        openImageFragment(loadedImage)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Не удалость загрузить изображение",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
        }

        binding.selectPhoto.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )

            startActivityForResult(i, 0)
        }

        binding.makePhoto.setOnClickListener {
            takePhoto()
        }

        binding.staticVideo.setOnClickListener {
            val url = "http://wsk2019.mad.hakta.pro/uploads/files/mad.mp4"
            val uri = Uri.parse(url)
            openVideoFragment(uri)
        }

        binding.selectVideo.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 2)
        }

        binding.staticAudio.setOnClickListener {
            val url = "http://wsk2019.mad.hakta.pro/uploads/files/song1.mp3"
            val uri = Uri.parse(url)
            openAudioFragment(uri)
        }

        binding.selectAudio.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(intent, 4)
        }
    }

    private fun takeVideo() {
        isCamera = false
        if (hasCameraAccess()){
            val intent = Intent(
                MediaStore.ACTION_VIDEO_CAPTURE
            )

            startActivityForResult(intent, 3)
        }
    }

    private fun takePhoto() {
        isCamera = true
        if (hasCameraAccess()) {
            val i = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            startActivityForResult(i, 1)
        }
    }

    private fun hasCameraAccess(): Boolean {
        return if (EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)) {
            true
        } else {
            EasyPermissions.requestPermissions(
                this,
                "We need this",
                3,
                android.Manifest.permission.CAMERA
            )
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            0 -> {
                if (resultCode == RESULT_OK && data != null && data.data != null) {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                    openImageFragment(bitmap)
                }
            }

            1 -> {
                if (resultCode == RESULT_OK && data != null) {
                    val bimap = data.extras?.get("data") as Bitmap
                    openImageFragment(bimap)
                }
            }

           2 -> {
                if (resultCode == RESULT_OK && data != null && data.data != null){
                    openVideoFragment(data.data as Uri)
                }
            }

            3 -> {
                if (resultCode == RESULT_OK && data != null){
                    val uri = data.data as Uri

                    openVideoFragment(data.data as Uri)
                }
            }

            4 -> {
                if (resultCode == RESULT_OK && data != null){
                 openAudioFragment(data.data as Uri)
                }
            }
        }
    }

    private fun openAudioFragment(uri: Uri) {
        val fragment = AudioViewerFragment(uri)
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun openVideoFragment(uri: Uri){
        val fragment = VideoViewerFragment(uri)
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun openImageFragment(imageBitmap: Bitmap) {
        val fragment = PhotoViewerFragment(imageBitmap)
        fragment.show(supportFragmentManager, fragment.tag)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            3 -> {
                takePhoto()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog
                .Builder(this)
                .setTitle("Недостаточно прав")
                .setRationale("Недостаточно прав для выполнения функции")
                .build()
                .show()
        }
    }
}