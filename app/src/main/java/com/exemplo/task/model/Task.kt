package com.exemplo.task.model

import android.os.Parcelable
import com.exemplo.task.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(

    var id: String = "",
    var taskDescription: String = "",
    var status: Int = 0

): Parcelable {

    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }

}