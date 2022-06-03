package com.example.practice.trainingdiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Вспомогательный класс для создания и версионирования базы данных
 */

public class DiaryDbHelper extends SQLiteOpenHelper {

    // --- Константы, определяющие имя и версию базы данных, и также имена таблиц и колонок ---
    /** Имя базы данных */
    public static final String DATABASE_NAME = "training_diary.db";

    /** Номер версии базы данных */
    public static final int DATABASE_VERSION = 8;

    /** Имя таблицы тренировок */
    public static final String TABLE_TRAININGS = "trainings";

    /** Имя таблицы упражнений */
    public static final String TABLE_EXERCISES = "exercises";

    /** Имя таблицы подходов */
    public static final String TABLE_ATTEMPTS = "attempts";

    /** Имя таблицы измерений */
    public static final String TABLE_MEASURES = "measures";

    /** Имя таблицы заметок */
    public static final String TABLE_NOTES = "notes";

    /** Имя колонки ID */
    public static final String COLUMN_ID = "_id";

    /** Имя колонки наименования */
    public static final String COLUMN_NAME = "name";

    /** Имя колонки комментария */
    public static final String COLUMN_COMMENT = "comment";

    /** Имя колонки даты */
    public static final String COLUMN_DATE = "date";

    /** Имя колонки текста */
    public static final String COLUMN_TEXT = "text";

    /** Имя колонки веса */
    public static final String COLUMN_WEIGHT = "weight";

    /** Имя колонки количества повторений */
    public static final String COLUMN_COUNT = "count";

    /** Имя колонки ID тренировки */
    public static final String COLUMN_TRAINING_ID = "training_id";

    /** Имя колонки ID упражнения */
    public static final String COLUMN_EXERCISE_ID = "exercise_id";

    /**
     * Создает экземпляр класса
     * @param context Контекст
     */
    public DiaryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Метод, вызываемый при создании базы данных
     * @param sqLiteDatabase Созданная база данных
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Создаём строку, которая содержит SQL-выражение для создания таблицы тренировок
        // CREATE TABLE trainings (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, comment TEXT, date TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');
        String SQL_CREATE_TRAININGS_TABLE = "CREATE TABLE " + TABLE_TRAININGS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_COMMENT + " TEXT, "
                + COLUMN_DATE + " TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAININGS_TABLE);

        // Создание таблицы измерений
        // CREATE TABLE measures (_id INTEGER PRIMARY KEY AUTOINCREMENT, date TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');
        String SQL_CREATE_MEASURES_TABLE = "CREATE TABLE " + TABLE_MEASURES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');";

        sqLiteDatabase.execSQL(SQL_CREATE_MEASURES_TABLE);

        // Создание таблицы заметок
        // CREATE TABLE notes (_id INTEGER PRIMARY KEY AUTOINCREMENT, text TEXT NOT NULL, date TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');
        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NOTES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TEXT + " TEXT NOT NULL, "
                + COLUMN_DATE + " TEXT NOT NULL DEFAULT '1970-01-01 00:00:00.000');";

        sqLiteDatabase.execSQL(SQL_CREATE_NOTES_TABLE);

        // Создание таблицы упражнений
        // CREATE TABLE exercises (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, training_id INTEGER NOT NULL);
        String SQL_CREATE_EXERCISES_TABLE = "CREATE TABLE " + TABLE_EXERCISES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_TRAINING_ID + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_EXERCISES_TABLE);

        // Создание таблицы подходов
        // CREATE TABLE attempts (_id INTEGER PRIMARY KEY AUTOINCREMENT, exercise_id INTEGER NOT NULL, weight INTEGER NOT NULL, count INTEGER NOT NULL);
        String SQL_CREATE_ATTEMPTS_TABLE = "CREATE TABLE " + TABLE_ATTEMPTS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_EXERCISE_ID + " INTEGER NOT NULL, "
                + COLUMN_WEIGHT + " INTEGER NOT NULL, "
                + COLUMN_COUNT + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_ATTEMPTS_TABLE);
    }

    /**
     * Метод, вызываемый при изменениии структуры базы данных
     * @param sqLiteDatabase Изенённая база данных
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_TRAININGS);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_EXERCISES);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_ATTEMPTS);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_MEASURES);
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NOTES);

        onCreate(sqLiteDatabase);
    }
}
