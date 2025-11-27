package com.example.events

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table WHERE id = 1")  // Assumes user with id = 1 (can be any logic)
    fun getUserProfile(): User
}
