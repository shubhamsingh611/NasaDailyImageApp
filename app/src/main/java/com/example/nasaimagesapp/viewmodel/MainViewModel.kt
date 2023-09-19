package com.example.nasaimagesapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasaimagesapp.Model.DailyImageModel
import com.example.nasaimagesapp.Repository.DailyImageRepository
import com.example.nasaimagesapp.Repository.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: DailyImageRepository) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO){
            repository.getDailyImage()
        }
    }

    val dailyImage : LiveData<Response<DailyImageModel>>
        get() = repository.dailyImage
}