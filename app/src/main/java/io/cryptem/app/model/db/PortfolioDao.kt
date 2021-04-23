package io.cryptem.app.model.db

import androidx.room.*
import io.cryptem.app.model.db.entity.PortfolioDbEntity

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
}