package io.cryptem.app.model.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snapshot")
class PortfolioSnapshotEntity(
    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    val timestamp: Long =  System.currentTimeMillis(),
    @ColumnInfo(name = "deposit")
    var deposit: Long,
    @ColumnInfo(name = "fiat")
    val fiat: Long,
    @ColumnInfo(name = "btc")
    var btc: Double,
    @ColumnInfo(name = "percent")
    var percent: Double)
