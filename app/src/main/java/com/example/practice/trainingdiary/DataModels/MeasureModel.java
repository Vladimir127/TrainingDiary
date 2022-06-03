package com.example.practice.trainingdiary.DataModels;

import java.util.Date;

/**
 * Класс, представляющий собой дневниковую запись об измерении
 */

public class MeasureModel extends DiaryEntryModel {

    /**
     * Создаёт экземпляр класса
     * @param date Дата измерения
     */
    public MeasureModel(Date date) {
        super(date);
    }
}
