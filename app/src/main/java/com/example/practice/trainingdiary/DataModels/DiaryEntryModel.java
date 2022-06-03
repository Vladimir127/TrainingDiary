package com.example.practice.trainingdiary.DataModels;

import java.util.Date;

/**
 * Класс, представляющий собой дневниковую запись
 */
public class DiaryEntryModel {

    //TODO: Удалить всё ненужное
    private static final int TYPE_TRAINING = 0;
    private static final int TYPE_MEASURE = 1;
    private static final int TYPE_NOTE = 2;

    String mHeading;
    String mSubheading;

    private long Id = 0;
    private Date Date;

    public DiaryEntryModel(String heading, String subheading, int id_) {
        this.mHeading = heading;
        this.mSubheading = subheading;
        this.Id = id_;
    }

    /**
     * Создаёт экземпляр класса
     * @param date
     */
    public DiaryEntryModel(Date date){
        this.Date = date;
    }

    /**
     * Возвращает заголовок карточки TODO
     * @return Заголовок карточки
     */
    public String getHeading() {
        return mHeading;
    }

    /**
     * Возвращает подзаголовок карточки TODO
     * @return подзаголовок карточки
     */
    public String getSubheading() {
        return mSubheading;
    }

    /**
     * Возвращает ID карточки TODO
     * @return ID карточки
     */
    public long getId() {
        return Id;
    }

    public void setId(long id) {
        this.Id = id;
    }

    public Date getDate() {
        return this.Date;
    }
}
