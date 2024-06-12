package com.prafull.chatbuddy.mainApp.home.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    var imageUri: MutableList<Bitmap> = mutableListOf(),
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.createTypedArrayList(Bitmap.CREATOR)!!,
            Participant.valueOf(parcel.readString()!!),
            parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(text)
        parcel.writeTypedList(imageUri)
        parcel.writeString(participant.name)
        parcel.writeByte(if (isPending) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }
}

enum class Participant {
    USER, MODEL, ERROR
}