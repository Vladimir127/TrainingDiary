package com.example.practice.trainingdiary.DataModels;

import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, представляющий собой дневниковую запись о тренировке
 */
public class TrainingModel extends DiaryEntryModel {

    /** Название тренировки*/
    private String mName;

    /** Комментарий к тренировке */
    private String mComment;

    public ArrayList<ExerciseModel> mExerciseList;

    /**
     * Создаёт экземпляр класса
     * @param date Дата тренировки
     */
    public TrainingModel(Date date) {
        super(date);
        mExerciseList = new ArrayList<>();
    }

    /**
     * Создаёт экземпляр класса
     * @param date Дата тренировки
     */
    public TrainingModel(Date date, String name) {
        super(date);
        this.mName = name;
        mExerciseList = new ArrayList<>();
    }

    public void setName(String value){
        this.mName = value;
    }

    public String getName(){
        return this.mName;
    }

    public void setComment(String value) {
        this.mComment = value;
    }

    public String getComment() {
        return mComment;
    }
}
