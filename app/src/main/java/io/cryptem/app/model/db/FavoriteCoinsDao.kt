package io.cryptem.app.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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