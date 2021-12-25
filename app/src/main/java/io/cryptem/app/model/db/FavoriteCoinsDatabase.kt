package io.cryptem.app.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.cryptem.app.model.db.entity.FavoriteCoinDbEntity

@Database(entities = [FavoriteCoinDbEntity::class], version = 1)
abstract class FavoriteCoinsDatabase : RoomDatabase() {
    abstract fun dao(): FavoriteCoinsDao
}