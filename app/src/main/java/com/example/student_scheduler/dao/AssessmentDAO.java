package com.example.student_scheduler.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.student_scheduler.entities.Assessment;

import java.util.List;

/**
 * This interface defines the database operations that can be performed on the Assessment entity.
 */
@Dao
public interface AssessmentDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM ASSESSMENT ORDER BY assessmentID ASC")
    List<Assessment> getAllAssessments();

    @Query("SELECT * FROM ASSESSMENT WHERE assessmentID= :assessmentID ORDER BY assessmentID ASC")
    List<Assessment> getAssociatedAssessments(int assessmentID);
}