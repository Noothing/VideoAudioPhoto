package com.example.videoaudiophoto.DialogFragment

import android.media.MediaPlayer
import android.media.MediaSync
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.videoaudiophoto.databinding.FragmentVideoViwerBinding
import com.google.android.material.snackbar.Snackbar


class VideoViewerFragment(private val videoUri: Uri) : DialogFragment() {

    private lateinit var binding: FragmentVideoViwerBinding
    private lateinit var timer: CountDownTimer
    private var paused = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoViwerBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog!!.setCanceledOnTouchOutside(true)


        binding.progressBar.isIndeterminate = true
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setMediaController(MediaController(requireActivity()))
        binding.videoView.setOnPreparedListener {
            initControlVideoContainer()
        }

        binding.videoView.setOnErrorListener { mp, what, extra ->
            Snackbar.make(
                requireView(),
                "Произошла ошибка!",
                Snackbar.LENGTH_LONG
            ).show()
            true
        }

        binding.videoView.setOnCompletionListener {
            this.dismiss()
        }
    }

    private fun initControlVideoContainer() {
        createProgressVideo(binding.videoView.duration)
        createPlayPause()
        timer.start()
        binding.videoView.requestFocus(0)
        binding.videoView.start()
        paused = false

        binding.progressBar.isIndeterminate = false
        binding.progressBar.visibility = View.GONE
    }

    private fun createPlayPause() {
        binding.videoView.setOnClickListener {
            if (paused) {
                binding.videoPauseCheckBox.isChecked = !paused
                binding.videoPauseCheckBox.visibility = View.GONE
                startVideo()
            } else {
                binding.videoPauseCheckBox.isChecked = !paused
                binding.videoPauseCheckBox.visibility = View.VISIBLE
                pauseVideo()
            }

            paused = !paused
        }
    }

    private fun startVideo() {
        timer.start()
        binding.videoView.start()
    }

    private fun pauseVideo() {
        timer.cancel()
        binding.videoView.pause()
    }

    private fun createProgressVideo(time: Int) {
        binding.videoProgress.max = time

        timer = object : CountDownTimer(100, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.videoProgress.progress = binding.videoView.currentPosition
            }

            override fun onFinish() {
                timer.start()
            }
        }

        binding.videoProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    timer.cancel()
                    binding.videoView.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseVideo()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.videoView.seekTo(seekBar!!.progress)
                startVideo()
            }

        })
    }

    companion object {
        const val TAG = "Video viewer fragment"
    }
}