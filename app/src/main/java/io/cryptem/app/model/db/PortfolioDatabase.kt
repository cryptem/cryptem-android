package io.cryptem.app.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.cryptem.app.model.db.entity.PortfolioDbEntity

@Database(entities = [PortfolioDbEntity::class], version = 2)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun dao(): PortfolioDao
}