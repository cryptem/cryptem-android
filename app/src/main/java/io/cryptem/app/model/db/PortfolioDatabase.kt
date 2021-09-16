package io.cryptem.app.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.cryptem.app.model.db.entity.PortfolioDbEntity
import io.cryptem.app.model.db.entity.PortfolioSnapshotEntity

@Database(entities = [PortfolioDbEntity::class, PortfolioSnapshotEntity::class], version = 3)
abstract class PortfolioDatabase : RoomDatabase() {
    abstract fun dao(): PortfolioDao

    companion object{
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE 'snapshot' ('timestamp' INTEGER NOT NULL, 'deposit' INTEGER NOT NULL, 'fiat' INTEGER NOT NULL, 'btc' REAL NOT NULL, 'percent' REAL NOT NULL, PRIMARY KEY('timestamp'))")
                database.execSQL("ALTER TABLE 'portfolio' ADD COLUMN 'price_change_percentage_1y_usd' REAL")
                database.execSQL("ALTER TABLE 'portfolio' ADD COLUMN 'price_change_percentage_1y_btc' REAL")
            }
        }
    }
}

