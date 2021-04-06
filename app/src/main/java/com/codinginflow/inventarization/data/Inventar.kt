package com.codinginflow.inventarization.data

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inventar")
data class Inventar(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    var databaceId:Int,
    @SerializedName("myid")
    var myid: String?,
    @SerializedName("itemName")
    var itemName: String?,
    @SerializedName("userName")
    var userName: String?,
    @SerializedName("discription")
    var discription: String?,
    @SerializedName("otdel")
    var otdel: String?,
    @SerializedName("deportament")
    var deportament: String?,
    @SerializedName("notes")
    var notes: String?,
    @SerializedName("color")
    var color: Int?
)
