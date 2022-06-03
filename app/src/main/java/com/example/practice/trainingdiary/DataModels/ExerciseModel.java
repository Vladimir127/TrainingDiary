package com.example.practice.trainingdiary.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Класс, представляющий собой упражнение
 */

public class ExerciseModel implements Parcelable {

	/**
     * ID упражнения
     */
    private long mId;

    /**
     * ID тренировки
     */
    private long mTrainingId;

    /**
     * Название упражнения
     */
    private String mName;

    /**
     * Признак того, что упражнение отмечено в списке
     */
    private boolean mIsChecked;

    /**
     * Дата для отображения в истории
     */
    private Date mDate;

    public ArrayList<AttemptModel> mAttemptsList;

    /**
     * Создаёт экземпляр класса
     * @param name Название упражнения
     */
    public ExerciseModel(String name) {
        this.mName = name;
        mAttemptsList = new ArrayList<>();
    }

	/**
     * Конструктор класса на основе пакета parcel
     * @param in Пакет parcel
     */
    public ExerciseModel(Parcel in){
        this.mId = in.readLong();
        this.mName = in.readString();
        this.mIsChecked = in.readByte() != 0;	//TODO: Законспектировать

        mAttemptsList = new ArrayList<>();
    }

    /**
     * Возвращает название упражнения
     * @return Название упражненияы
     */
    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Упаковывает объект для передачи
     * @param parcel Пакет, в который должен быть записан объект
     * @param i Дополнительные флаги о том, как должен быть записан объект. Может быть 0 или PARCELABLE_WRITE_RETURN_VALUE. Если указан этот флаг, объект записывается как возвращаемое значение, которое является результатом функции, например, "Parcelable someFunction()", "void someFunction(out Parcelable)", или "void someFunction(inout Parcelable)". Некоторые реализации могут захотеть освободить ресурсы в этой точке.
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
		parcel.writeLong(mId);
		parcel.writeString(mName);
		parcel.writeByte((byte) (mIsChecked ? 1 : 0));     //if myBoolean == true, byte == 1
    }

	/**
     * Статическое поле Creator генерирует объект класса-передатчика
     */
    public static final Parcelable.Creator<ExerciseModel> CREATOR = new Parcelable.Creator<ExerciseModel>(){
        /**
         * Этот метод создает объект класса ExerciseModel на основе пакета
         * @param parcel Пакет parcel
         * @return Объект класса ExerciseModel, созданный на основе пакета
         */
        @Override
        public ExerciseModel createFromParcel(Parcel parcel) {
            return new ExerciseModel(parcel);
        }
        
		@Override
        public ExerciseModel[] newArray(int i) {
            return new ExerciseModel[i];
        }
    };

    //TODO: Переименовать value в isChecked
    public void setIsChecked(boolean value) {
        this.mIsChecked = value;
    }

    public boolean getIsChecked() {
        return mIsChecked;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public void setTrainingId(long trainingId) {
        this.mTrainingId = trainingId;
    }

    public long getTrainingId() {
        return mTrainingId;
    }
}
