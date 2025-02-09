package com.example.adminwaveoffood.model

import android.location.Address
import android.provider.ContactsContract

data class UserModel(
    val userName: String?=null,
    val restaurantName: String?=null,
    val password: String?=null,
    val email: String?=null,
    val address: String?=null,
    val location: Address?=null,
    val phone: ContactsContract.CommonDataKinds.Phone?=null

)
