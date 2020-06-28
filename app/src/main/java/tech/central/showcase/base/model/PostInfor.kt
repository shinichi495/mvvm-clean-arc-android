package tech.central.showcase.base.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PostInfor(
        val id : Int,
        val title: String,
        val body: String,
        val name: String,
        val email: String
) : Parcelable