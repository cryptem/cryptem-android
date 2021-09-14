package io.cryptem.app.model.db

import androidx.room.*
import io.cryptem.app.model.db.entity.PortfolioDbEntity
import io.cryptem.app.model.db.entity.PortfolioSnapshotEntity

@Dao
interface PortfolioDao {
    @Query("SELECT * FROM portfolio")
    suspend fun getPortfolioCoins(): List<PortfolioDbEntity>

    @Query("SELECT * FROM portfolio WHERE id = :id LIMIT 1")
    suspend fun getPortfolioCoin(id : String): PortfolioDbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPortfolioCoin(coin : PortfolioDbEntity)

    @Update
    suspend fun updatePortfolioCoin(coin : PortfolioDbEntity)

    @Query("DELETE FROM portfolio WHERE id = :id")
    suspend fun removePortfolioCoin(id: String)

    @Query("SELECT MAX(timestamp) FROM snapshot")
    suspend fun getLastSnapshot(): Long?

    @Query("SELECT * FROM snapshot WHERE timestamp > :since ORDER BY timestamp ASC")
    suspend fun getSnapshots(since : Long): List<PortfolioSnapshotEntity>

    @Insert
    suspend fun addSnapshot(coin : PortfolioSnapshotEntity)

    @Query("DELETE FROM snapshot")
    suspend fun clearSnapshots()

}