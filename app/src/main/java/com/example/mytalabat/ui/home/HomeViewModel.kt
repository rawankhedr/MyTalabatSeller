package com.example.mytalabat.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytalabat.data.model.UserProfile
import com.example.mytalabat.data.repository.AuthRepository
import com.example.mytalabat.data.repository.UserRepository
import com.example.mytalabat.util.Resource
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<Resource<UserProfile>>()
    val userProfile: LiveData<Resource<UserProfile>> = _userProfile

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val uid = authRepository.getCurrentUser()?.uid
        if (uid == null) {
            _userProfile.value = Resource.Error("User not authenticated")
            return
        }

        _userProfile.value = Resource.Loading()

        viewModelScope.launch {
            val result = userRepository.getUserProfile(uid)
            _userProfile.value = result
        }
    }
}