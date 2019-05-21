package com.example.test.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.example.test.model.Post
import javax.inject.Singleton

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(post: List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(post: Post)

    @Query("SELECT * FROM Post WHERE groupId = :groupId ORDER BY createdAt DESC")
    fun allPosts(userId: String): DataSource.Factory<Int, Post>

    @Query("DELETE FROM Post")
    fun deleteAll()

}