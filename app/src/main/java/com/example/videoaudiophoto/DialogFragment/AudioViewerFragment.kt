package com.example.videoaudiophoto.DialogFragment

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import com.example.videoaudiophoto.R
import com.example.videoaudiophoto.databinding.FragmentAudioViewerBinding
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.util.concurrent.TimeUnit

class AudioViewerFragment(private val uri: Uri) : DialogFragment() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: FragmentAudioViewerBinding
    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mediaPlayer = MediaPlayer()
        binding = FragmentAudioViewerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog!!.setCanceledOnTouchOutside(true)

        try {
            mediaPlayer.setDataSource(requireContext(), uri)
            mediaPlayer.prepare()
            mediaPlayer.setOnPreparedListener {
                initAllControl()
                mediaPlayer.start()
            }
            mediaPlayer.setOnCompletionListener {
                this.dismiss()
            }
        }catch (e: IOException){
            this.dismiss()
            Snackbar.make(
                requireView(),
                "Произошла ошибка!",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun initAllControl() {
        initTime()
        initSeekbar()
        initPauseButton()
        timer.start()
        mediaPlayer.start()
    }

    private fun initPauseButton() {
        binding.audioPauseCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                pauseAudio()
            }else{
                resumeAudio()
            }
        }
    }

    private fun initSeekbar() {
        binding.audioSeekBar.max = mediaPlayer.duration
        binding.audioSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(binding.audioSeekBar.progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                resumeAudio()
            }
        })

        timer = object : CountDownTimer(100, 1000){
            override fun onTick(millisUntilFinished: Long) {
                val out = mediaPlayer.duration - mediaPlayer.currentPosition
                var sec = ""
                if (((out / 1000) % 60) > 10){
                    sec = ((out / 1000) % 60).toString()
                }else{
                    sec = "0${(out / 1000) % 60}"
                }
                binding.audioTime.text = "${TimeUnit.MILLISECONDS.toMinutes(out.toLong())}:$sec"

                binding.audioSeekBar.progress = mediaPlayer.currentPosition
            }

            override fun onFinish() {
                timer.start()
            }

        }
    }

    private fun initTime() {
        val ms = mediaPlayer.currentPosition.toLong()
        binding.audioTime.text = "${TimeUnit.MILLISECONDS.toMinutes(ms)} : ${ms / 1000}"
    }

    private fun pauseAudio() {
        mediaPlayer.pause()
        timer.cancel()
    }

    private fun resumeAudio() {
        mediaPlayer.start()
        timer.start()
    }

    override fun onDetach() {
        mediaPlayer.stop()
        timer.cancel()
        mediaPlayer.release()
        super.onDetach()
    }

    override fun onPause() {
        binding.audioPauseCheckBox.isChecked = true
        pauseAudio()
        super.onPause()
    }

    companion object {
       const val TAG = "Audio viewer fragment"
    }
}