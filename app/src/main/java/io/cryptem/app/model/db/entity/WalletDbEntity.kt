package io.cryptem.app.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletDbEntity(
    @PrimaryKey(autoGenerate = true) val id : Long?,
    @ColumnInfo(name = "coin") val coin: String,
    @ColumnInfo(name = "name") val privateName: String,
    @ColumnInfo(name = "address") val address: String,
) {

}