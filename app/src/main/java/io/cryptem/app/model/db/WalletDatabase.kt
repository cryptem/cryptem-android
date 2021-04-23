package io.cryptem.app.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.cryptem.app.model.db.entity.WalletDbEntity

@Database(entities = [WalletDbEntity::class], version = 1)
abstract class WalletDatabase : RoomDatabase() {
    abstract fun dao(): WalletDao
}