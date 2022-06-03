package com.example.practice.trainingdiary.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.practice.trainingdiary.Adapters.ExerciseCardAdapter;
import com.example.practice.trainingdiary.Adapters.ExerciseHistoryAdapter;
import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.DiaryDbHelper;
import com.example.practice.trainingdiary.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

/**
 * Операция, соответствующая экрану истории
 */
public class HistoryActivity extends AppCompatActivity {

    /** Название упражнения для отображения в заголовке */
    private String mExerciseName;

    /** Список упражнений */
    private ArrayList<ExerciseModel> mExerciseList;

    /** Адаптер, соединяющий коллекцию упражнений mExerciseList со списком на экране */
    ExerciseHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //TODO: EmptyView Нет истории
        // Настраиваем панель приложения и устанавливаем ей заголовок
        initializeActionBar();

        //TODO: Инициализацию адаптера - в отдельный метод
        // Инициализируем список упражнений, элемент ListView на экране и адаптер
        mExerciseList = new ArrayList<>();
        ListView listView = findViewById(R.id.list_view_exercises);
        mAdapter = new ExerciseHistoryAdapter(this, mExerciseList);
        listView.setAdapter(mAdapter);

        // Находим и устанавливаем empty view для listView -
        // надпись, которая отображается, когда в списке нет пунктов
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Получаем из базы упражнения и записываем их в список
        getExercises(mExerciseName);


    }

    /**
     * Добавляет на панель действий иконку "Назад" и заголовок
     */
    private void initializeActionBar() {
        // Получаем название упражнения из интента, присланного операцией TrainingActivity
        mExerciseName = getIntent().getStringExtra("exercise_name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mExerciseName);
        setSupportActionBar(toolbar);

        // Для отображения стрелки назад на панели приложения
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /** Вызывается при выборе одного из пунктов меню */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // В данном методе обрабатывается только один случай - нажатие на стрелку назад.
        // В этом случае текущая операция завершается, и пользователь попадает на главный экран.
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    /**
     * Получает из базы все упражнения по названию
     * @param exerciseName Название упражнения, историю которого необходимо получить
     */
    private void getExercises(String exerciseName) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересуют записи с конкретным названием упражнения
        String selection = dbHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = new String[]{exerciseName};

        // С помощью метода query запрашиваем запись с нужным ID
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_EXERCISES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()) {

            // Получаем индексы колонок таблицы
            int idColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_ID);
            int trainingIdColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_TRAINING_ID);
            int nameColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_NAME);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем ID и название упражнения
                long id = cursor.getLong(idColumnIndex);
                long trainingId = cursor.getLong(trainingIdColumnIndex);
                String name = cursor.getString(nameColumnIndex);

                // На основании данных, полученных из базы, создаём новую модель упражнения
                // и добавляем её в список упражнений этой тренировки
                ExerciseModel exercise = new ExerciseModel(name);
                exercise.setId(id);
                exercise.setTrainingId(trainingId);

                getAttempts(exercise);
                getDate(exercise);

                mExerciseList.add(exercise);


            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();

        //TODO: в других аналогичных местах также убрать цикл
//        for (int i = 0; i < mExerciseList.size(); i++){
//            ExerciseModel exercise = mExerciseList.get(i);
//            getAttempts(exercise);
//        }

        // Сортируем полученный лист по дате
        mExerciseList.sort(new ExerciseDateComparator());

        // Уведомляем адаптер об изменении списка для обновления списка на экране
        mAdapter.notifyDataSetChanged();
    }

    private void getDate(ExerciseModel exercise) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересуют записи с конкретным ID тренировк
        String selection = dbHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(exercise.getTrainingId())};



        // С помощью метода query запрашиваем запись с нужным ID
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_TRAININGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()) {

            // Получаем индекс колонки имени
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи

            // Считываем название упражнения
            String dateString = cursor.getString(dateColumnIndex);

            // Дата хранится в базе данных в строковом представлении, а нам необходимо привести её к типу Date.
            // Создаем новый SimpleDateFormat, в котором указываем исходный формат, в котором хранится дата
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

            // Создаём новый экзмпляр класса Date и присваиваем ему значение по умолчанию
            Date date = new Date(1970, 1, 1);
            try {
                // Преобразуем значение из базы данных в дату с использованием simpleDateFormat
                date = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            exercise.setDate(date);
        }
    }

    /**
     * Получает из базы подходы для данного упражнения
     * @param exercise Упражнение, подходы для которого необходимо получить
     */
    private void getAttempts(ExerciseModel exercise) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует запись с конкретным ID тренировки
        String selection = dbHelper.COLUMN_EXERCISE_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(exercise.getId())};

        // С помощью метода query запрашиваем запись с нужным ID
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_ATTEMPTS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()) {

            // Получаем индексы колонок таблицы
            int idColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_ID);
            int weightColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_WEIGHT);
            int countColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_COUNT);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем ID и название упражнения
                long id = cursor.getLong(idColumnIndex);
                int weight = cursor.getInt(weightColumnIndex);
                int count = cursor.getInt(countColumnIndex);

                // На основании данных, полученных из базы, создаём новую модель упражнения
                // и добавляем её в список упражнений этой тренировки
                AttemptModel attempt = new AttemptModel(weight, count);
                attempt.setId(id);
                exercise.mAttemptsList.add(attempt);
            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();
    }

    //TODO: Прокомментировать
    public class ExerciseDateComparator implements Comparator<ExerciseModel>
    {
        public int compare(ExerciseModel left, ExerciseModel right) {
            return left.getDate().compareTo(right.getDate());
        }
    }
}
