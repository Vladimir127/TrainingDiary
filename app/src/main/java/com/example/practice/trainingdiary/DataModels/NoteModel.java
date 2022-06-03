package com.example.practice.trainingdiary.DataModels;

import java.util.Date;

/**
 * Класс, представляющий собой дневниковую запись - заметку
 */
public class NoteModel extends DiaryEntryModel {

    /** Текст заметки */
    private String mText;

    /**
     * Создаёт экземпляр класса
     * @param date Дата заметки
     */
    public NoteModel(Date date) {
        super(date);
    }

    /**
     * Создаёт экземпляр класса
     * @param date Дата заметки
     * @param text Текст заметки
     */
    public NoteModel(Date date, String text) {
        super(date);
        this.mText = text;
    }

    public String getText() {
        return mText;
    }
}
