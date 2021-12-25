package io.cryptem.app.model.db

import androidx.room.*
import io.cryptem.app.model.db.entity.FavoriteCoinDbEntity

@Dao
interface FavoriteCoinsDao {

    @Insert
    suspend fun addFavorite(coin : FavoriteCoinDbEntity) : Long

    @Delete
    suspend fun removeFavorite(favorite: FavoriteCoinDbEntity)

    @Query("SELECT * FROM favorite")
    suspend fun getFavorites() : List<FavoriteCoinDbEntity>


}