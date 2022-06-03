package com.example.practice.trainingdiary.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Класс, представляющий собой запись о подходе
 */
 public class AttemptModel implements Parcelable {
	
	/** ID подхода */
	private long mId;

	/** ID упражнения, к которому относится данный подход */
	private long mExerciseId;

	/** Вес, кг */
	private int mWeight;

	/** Количество повторений, раз. */
	private int mCount;

	/**
	* Создаёт экземпляр класса
	*/
	public AttemptModel() {

	}

	/**
	* Создаёт экземпляр класса
	*/
	public AttemptModel(int weight, int count) {
		this.mWeight = weight;
		this.mCount = count;
	}

	/**
	 * Конструктор класса на основе пакета parcel
	 * @param in Пакет parcel
	 */
	public AttemptModel (Parcel in){
		this.mId = in.readLong();
		this.mExerciseId = in.readLong();
		this.mWeight = in.readInt();
		this.mCount = in.readInt();
	}

    public int getWeight() {
        return mWeight;
    }

    public int getCount() {
        return mCount;
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
		parcel.writeLong(mExerciseId);
		parcel.writeInt(mWeight);
		parcel.writeInt(mCount);
	}

	/**
	 * Статическое поле Creator генерирует объект класса-передатчика
	 */
	public static final Parcelable.Creator<AttemptModel> CREATOR = new Parcelable.Creator<AttemptModel>(){
		/**
		 * Этот метод создает объект класса AttemptModel на основе пакета
		 * @param parcel Пакет parcel
		 * @return Объект класса AttemptModel, созданный на основе пакета
		 */
		@Override
		public AttemptModel createFromParcel(Parcel parcel) {
			return new AttemptModel(parcel);
		}

		@Override
		public AttemptModel[] newArray(int i) {
			return new AttemptModel[i];
		}
	};

	public void setId(long id) {
		this.mId = id;
	}

	public void setWeight(int weight) {
		this.mWeight = weight;
	}

	public void setCount(int count) {
		this.mCount = count;
	}
}
