package io.cryptem.app.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteCoinDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String
) {

}