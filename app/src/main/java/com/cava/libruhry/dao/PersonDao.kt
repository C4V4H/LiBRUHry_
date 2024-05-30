package com.cava.libruhry.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.cava.libruhry.dataclass.Category
import com.cava.libruhry.dataclass.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Upsert
    suspend fun upsertPerson(person: Person)

    @Query("SELECT * FROM people")
    fun getPersons(): Flow<List<Person>>

    @Query("SELECT * FROM people WHERE name = :name")
    suspend fun getPersonByName(name: String): Person?

}
