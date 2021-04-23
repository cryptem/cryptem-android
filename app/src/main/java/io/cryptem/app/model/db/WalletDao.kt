package io.cryptem.app.model.db

import androidx.room.*
import io.cryptem.app.model.db.entity.WalletDbEntity
import io.cryptem.app.model.ui.WalletCoin

@Dao
interface WalletDao {

    @Insert
    suspend fun addWallet(walletDao: WalletDbEntity) : Long

    @Update
    suspend fun updateWallet(walletDao: WalletDbEntity)

    @Delete
    suspend fun deleteWallet(walletDao: WalletDbEntity)

    @Query("SELECT * FROM wallet ORDER BY coin, name")
    suspend fun getWallets() : List<WalletDbEntity>


    @Query("SELECT * FROM wallet WHERE coin == :coin ORDER BY name")
    suspend fun getWallets(coin : String) : List<WalletDbEntity>

    @Query("SELECT * FROM wallet WHERE id == :id")
    suspend fun getWallet(id : Long) : WalletDbEntity?

}