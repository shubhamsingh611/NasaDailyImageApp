package com.example.nasaimagesapp

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nasaimagesapp.Api.DailyImageService
import com.example.nasaimagesapp.Api.RetrofitHelper
import com.example.nasaimagesapp.Model.DailyImageModel
import com.example.nasaimagesapp.Repository.DailyImageRepository
import com.example.nasaimagesapp.viewmodel.MainViewModel
import com.example.nasaimagesapp.viewmodel.MainViewModelFactory
import com.example.nasaimagesapp.databinding.ActivityMainBinding
import com.example.nasaimagesapp.Repository.Response
import com.example.nasaimagesapp.utils.NetworkUtils

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel
    lateinit var dailyImageModel: DailyImageModel
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //Initializing shared preferences
        sharedPreferences = getSharedPreferences("sharedprefs", Context.MODE_PRIVATE)
        //Swipe Refresh functionality
        binding.swipeRefresh.setOnRefreshListener {
            updateData()
            binding.swipeRefresh.isRefreshing = false
        }
        //Fetching date from upi and update the UI
        updateData()
    }

    private fun updateData() {
        //Showing progress bar on Data load
        binding.progressBar.visibility = View.VISIBLE
        binding.cardView.visibility = View.GONE

        //Checking Internet connection
        if (NetworkUtils.isInternetAvailable(this)) {
            //Api Calling
            val dailyImageService =
                RetrofitHelper.getInstance().create(DailyImageService::class.java)
            val repository = DailyImageRepository(dailyImageService)
            mainViewModel = ViewModelProvider(
                this,
                MainViewModelFactory(repository)
            ).get(MainViewModel::class.java)

            mainViewModel.dailyImage.observe(this, Observer {
                //Checking Response & handing expected error
                when (it) {
                    is Response.Error -> {
                        run {
                            it.errorMessage
                            Toast.makeText(
                                this@MainActivity,
                                it.errorMessage.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is Response.Loading -> {}

                    is Response.Success -> {
                        it.data?.let {
                            dailyImageModel = it
                            val title: String? = sharedPreferences.getString("TITLE", null)
                            if (title != null && title.equals(dailyImageModel.title)) {
                                setData(applicationContext, dailyImageModel)
                            } else {
                                cachingData(dailyImageModel)
                                setData(applicationContext, dailyImageModel)
                            }
                        }
                    }
                }

                binding.progressBar.visibility = View.GONE
                binding.cardView.visibility = View.VISIBLE
            })
        } else {
            //Showing Alert Dialog for no network
            val alertDialog = AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Network Error!")
                .setMessage("Internet is not available. Turn on Internet and Try again.")
                .show()
        }
    }

    //Storing data fetched from API to Shared preferences
    private fun cachingData(dailyImageModel: DailyImageModel) {
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("TITLE", dailyImageModel.title)
            putString("DATE", dailyImageModel.date)
            putString("DESCRIPTION", dailyImageModel.explanation)
        }.apply()
    }

    //Appending Date text to date response string
    private fun buildDate(dailyImageModel: DailyImageModel): String {
        val dateBuilder = StringBuilder("")
        dateBuilder.append("Date : ")
        dateBuilder.append(sharedPreferences.getString("DATE", null))
        return dateBuilder.toString()
    }

    //Setting data in views from Cache data
    private fun setData(context: Context, dailyImageModel: DailyImageModel) {
        //When statement for Handling Video Content
        when (dailyImageModel.media_type) {
            "image" -> {
                //Setting Image on text view with help of GLIDE Library
                Glide.with(context)
                    .load(dailyImageModel.url)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(binding.ivImage)
            }

            "video" -> {
                //Showing Video View only when Api response contains video
                val mediaController = MediaController(this)
                mediaController.setAnchorView(binding.videoView)
                binding.videoView.visibility = View.VISIBLE
                binding.videoView.setMediaController(mediaController)
                binding.videoView.setVideoURI(Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"))
                binding.videoView.requestFocus()
                binding.videoView.start()
            }

            else -> {
                Toast.makeText(this@MainActivity, "Unsupported Format!", Toast.LENGTH_SHORT).show()
            }

        }
        binding.tvTitle.setText(sharedPreferences.getString("TITLE", null))
        binding.tvDate.setText(buildDate(dailyImageModel))
        binding.tvDescription.setText(sharedPreferences.getString("DESCRIPTION", null))
    }

}

