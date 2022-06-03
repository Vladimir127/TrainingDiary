package com.example.practice.trainingdiary.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.practice.trainingdiary.Adapters.ExerciseCardAdapter;
import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.DataModels.TrainingModel;
import com.example.practice.trainingdiary.DiaryDbHelper;
import com.example.practice.trainingdiary.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TrainingActivity extends AppCompatActivity {

    /** Адаптер, соединяющий данные со списком на экране */
    ExerciseCardAdapter mAdapter;

    /** Код запроса результата операции SelectExerciseActivity */
    private static final int REQUEST_CODE_EXERCISES = 1;

    /** Код запроса результата операции AttemptActivity */
    private static final int REQUEST_CODE_ATTEMPTS = 2;

    /** Календарь, который будет использован для выбора даты */
    Calendar mCalendar;

    /** Поля ввода */
    EditText mNameEditText;
    EditText mCommentEditText;
    EditText mDateEditText;

    // TODO
    DatePickerDialog.OnDateSetListener dateSetListener;

    /** Модель тренировки, данные которой отображаются на экране */
    public TrainingModel mTraining;

    /** Список выбранных упражнений */
    private ArrayList<ExerciseModel> mCheckedExercisesList;

    /** Дата тренировки в формате "yyyy-MM-dd HH:mm:ss.sss" */ //TODO: Возможно, от неё можно будет избавиться
    private String mDateString;

    /** Признак того, что в тренировку были внесены изменения */
    private boolean mIsChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: При возврате с экрана выбора тренировок данные из полей ввода стираются
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        // Настраиваем панель действий, плавающую кнопку действия и календарь
        initializeActionBar();
        initializeFloatingActionButton();
        initializeCalendar();

        //TODO: Инициализацию адаптера - в отдельный метод
        // Инициализируем список упражнений, элемент ListView на экране и адаптер
        mCheckedExercisesList = new ArrayList<>();
        ListView listView = findViewById(R.id.list_view_exercises);
        mAdapter = new ExerciseCardAdapter(this, mCheckedExercisesList);
        listView.setAdapter(mAdapter);

        // Находим и устанавливаем empty view для listView -
        // надпись, которая отображается, когда в списке нет пунктов
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // Получаем ID тренировки из интента, который был передан из вызывающей операции
        long id = getIntent().getLongExtra("id", 0);

        if (id == 0){

            // Если ID = 0, значит, тренировка новая.
            // Получаем из интента строку с датой, приводим её к типу Date и создаём новую тренировку с этой датой.
            mDateString = getIntent().getStringExtra("date");

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
            Date inputDate;

            try {
                inputDate = inputFormat.parse(mDateString);
                mTraining = new TrainingModel(inputDate);

                // Снова приводим дату к строке для отображения и выводим на экран в элемент mDateEditText
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
                String outputDateString = outputFormat.format(inputDate);
                mDateEditText.setText(outputDateString);
            } catch (ParseException e){
                e.printStackTrace();
            }
        } else {
            // Если ID тренировки не равен нулю, значит, тренировка уже существует в базе
            // Считываем из базы данные о тренировке
            getTraining(id);

            // Выводим полученные данные о тренировке на экран
            mNameEditText.setText(mTraining.getName());
            mCommentEditText.setText(mTraining.getComment());

            // Приводим дату к строковому типу для отображения и выводим на экран в элемент mDateEditText
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
            String outputDateString = outputFormat.format(mTraining.getDate());
            mDateEditText.setText(outputDateString);

            // Добавлено для того, чтобы при старте этой операции в EditText не устанавливался курсор
            RelativeLayout relativeLayout = findViewById(R.id.relative_layout);
            relativeLayout.setFocusable(true);
            relativeLayout.setFocusableInTouchMode(true);
        }

        // TODO: Доделать
        mIsChanged = false;





    }

    /**
     * Вызывается в результате завершения операции SelectExercisesActivity (выборка упражнений)
     * @param requestCode Код запроса
     * @param resultCode Код результата
     * @param data Интент с данными, поступившими из операции SelectExercisesActivity (выбранными упражнениями)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EXERCISES) {
            if (resultCode == RESULT_OK) {

                // TODO: Изменить алгоритм: если к упражнениям уже добавлены подходы, то всё стирается

                // Получаем выбранные упражнения из интента-результата и записываем их в отдельный список
                ArrayList<ExerciseModel> checkedExercises = data.getParcelableArrayListExtra("exercises");

                boolean isFound;

                // Перебираем полученный список упражнений.
                // Для каждого упражнения из этого списка проверяем,
                // есть ли такое упражнение в коллекции mExerciseList
                for (int i = 0; i < checkedExercises.size(); i++) {
                    ExerciseModel checkedExercise = checkedExercises.get(i);

                    isFound = false;

                    for (int j = 0; j < mTraining.mExerciseList.size(); j++) {
                        ExerciseModel exercise = mTraining.mExerciseList.get(j);

                        // Если совпадение найдено
                        if (exercise.getName().equals(checkedExercise.getName())){
                            // Прерываем цикл
                            isFound = true;
                            break;
                        }
                    }

                    if (!isFound){
                        // Если совпадение не будет найдено, цикл не прервётся, и программа прийдёт сюда
                        // Добавляем в коллекцию новое упражнение
                        mTraining.mExerciseList.add(checkedExercise);
                        mCheckedExercisesList.add(checkedExercise);
                    }
                }

                // Возможна и обратная ситуация: упражнение было в списке,
                // но пользователь снял с него галочку. В этом случае, наоборот,
                // ищем в коллекции mTraining.mExerciseList те упражнения,
                // которых нет в списке с галочками - checkedExercises, и удаляем их
                for (int i = 0; i < mTraining.mExerciseList.size(); i++) {
                    ExerciseModel exercise = mTraining.mExerciseList.get(i);

                    isFound = false;

                    for (int j = 0; j < checkedExercises.size(); j++) {
                        ExerciseModel checkedExercise = checkedExercises.get(j);

                        // Если совпадение найдено
                        if (exercise.getName().equals(checkedExercise.getName())){
                            // Прерываем цикл
                            isFound = true;
                            break;
                        }
                    }

                    if (!isFound){
                        // Если совпадение не будет найдено, цикл не прервётся, и программа прийдёт сюда
                        // Добавляем в коллекцию новое упражнение
                        mTraining.mExerciseList.remove(exercise);
                        mCheckedExercisesList.remove(exercise);
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_ATTEMPTS) {
            if (resultCode == RESULT_OK) {
                // Получаем выбранные подходы, а также название упражнения из интента-результата
                String exerciseName = data.getStringExtra("exercise_name");
                ArrayList<AttemptModel> attemptsList = data.getParcelableArrayListExtra("attempts");


                // Ищем в списке упражнение с нужным названием
                for (int i = 0; i < mTraining.mExerciseList.size(); i++){
                    ExerciseModel exercise = mTraining.mExerciseList.get(i);

                    // Если упражнение найдено
                    if (exercise.getName().equals(exerciseName)){

                        // Предварительно очищаем список
                        exercise.mAttemptsList.clear();

                        // Добавляем в список его подходов все подходы, полученные из интента
                        for (int j = 0; j < attemptsList.size(); j++){
                            AttemptModel attempt = attemptsList.get(j);
                            exercise.mAttemptsList.add(attempt);
                        }

                        break;
                    }
                }
                        // TODO: Убрать фокус ввода в AttemptActivity
                        // TODO: Более чётко разделить бизнес-логику и графическую оболчку: выяснилось, например, что у класса TrainingModel даже нет своего списка упражнений

//                // TODO: После сохранения другого количества подходов информация на экране не обновляется
//                mCheckedExercisesList.clear();
//
//                for (int i = 0; i < mTraining.mExerciseList.size(); i++){
//                    mCheckedExercisesList.add(mTraining.mExerciseList.get(i));
//                }
            }
        }

        // Оповещаем адаптер об изменении списка для обноления информации на экране
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Добавляет на панель действий иконку "Назад" и инициализирует текстовые поля, находящиеся на ней
     */
    private void initializeActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Для отображения стрелки назад на панели действий
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Находим текстовые поля для обработки их значений
        mNameEditText = findViewById(R.id.edit_text_name);
        mCommentEditText = findViewById(R.id.edit_text_comment);
        mDateEditText = findViewById(R.id.edit_text_date);
    }

    /**
     * Инициализирует плавающую кнопку действия и устанавливает ей обработчик нажатия
     */
    private void initializeFloatingActionButton() {
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SelectExerciseActivity.class);
                intent.putParcelableArrayListExtra("exercises", mTraining.mExerciseList);
                startActivityForResult(intent, REQUEST_CODE_EXERCISES);
            }
        });
    }

    /**
     * Инициализирует календарь, используемый для выбора даты тренировки
     */
    private void initializeCalendar() {
        mCalendar = Calendar.getInstance();
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                mDateEditText.setText(sdf.format(mCalendar.getTime()));
            }
        };
    }

    /**
     * Получает из базы тренировке с указанным ID
     * @param id ID тренировки
     */
    private void getTraining(long id) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует запись с конкретным ID
        String selection = dbHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

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

            // Получаем индексы колонок таблицы
            int nameColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_NAME);
            int commentColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_COMMENT);
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Считываем название, комментарий и дату тренировки
            String name = cursor.getString(nameColumnIndex);
            String comment = cursor.getString(commentColumnIndex);
            String dateString = cursor.getString(dateColumnIndex);

            // Дата хранится в базе данных в строковом представлении, а нам необходимо привести её к типу Date.
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

            // Создаём новый экзмпляр класса Date и присваиваем ему значение по умолчанию
            Date date = new Date(1970, 1, 1);
            try {
                // Преобразуем значение из базы данных в дату с использованием simpleDateFormat
                date = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // На основании данных, полученных из базы, создаём новую модель тренировки
            mTraining = new TrainingModel(date, name);
            mTraining.setComment(comment);
            mTraining.setId(id);
        }

        // После чтения данных закрываем базу
        dbHelper.close();

        // Получаем из базы упражнения и записываем их в список
        getExercises(id);
    }

    /**
     * Получает из базы упражнения для данной тренировки
     * @param trainingId ID тренировки, упражнения для которой нужно получить
     */
    private void getExercises(long trainingId) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует запись с конкретным ID тренировки
        String selection = dbHelper.COLUMN_TRAINING_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(trainingId)};

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
            int nameColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_NAME);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем ID и название упражнения
                long id = cursor.getLong(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);

                // На основании данных, полученных из базы, создаём новую модель упражнения
                // и добавляем её в список упражнений этой тренировки
                ExerciseModel exercise = new ExerciseModel(name);
                exercise.setId(id);

                //TODO: Отдельно запихиваем в тренировку, а потом из неё отдельно отображаем
                mTraining.mExerciseList.add(exercise);
                mCheckedExercisesList.add(exercise);
            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();

        for (int i = 0; i < mTraining.mExerciseList.size(); i++){
            ExerciseModel exercise = mTraining.mExerciseList.get(i);
            getAttempts(exercise);
        }

        // Уведомляем адаптер об изменении списка для обновления списка на экране
        mAdapter.notifyDataSetChanged();
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

    /**
     * Для создания меню
     * @param menu Меню
     * @return True в случае успешного создания, иначе False
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    /**
     * Вызывается при выборе пункта меню
     * @param item Выбранный пункт меню
     * @return True в случае успешной обработки, иначе False
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveTraining();
                
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Осуществляет сохранение тренировки в базу данных
     */
    private void saveTraining() {
        // TODO: После изменения названия существующей тренировки и её сохранения в списке в dayActivity название не меняется.

        // Считываем данные из полей ввода
        // Используем метод trim(), чтобы удалить лишние пробелы
        String nameString = mNameEditText.getText().toString().trim();
        String commentString = mCommentEditText.getText().toString().trim();

        // Для значения даты потребуется дополнительная обработка,
        // т.к. она записана не в том формате, в котором хранится в базе данных
        String inputDateString = mDateEditText.getText().toString().trim();
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");

        Date date;
        String outputDateString;

        try {
            date = inputFormat.parse(inputDateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
            outputDateString = outputFormat.format(date);

            TextInputLayout nameLayout = findViewById(R.id.name_layout);

            // Проверяем заполнение необходимых полей
            if (TextUtils.isEmpty(nameString)){

                nameLayout.setError("Введите название");
                return; //TODO: Выдать пользователю сообщение об ошибке
            } else {
                nameLayout.setError(null);
            }

            // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
            DiaryDbHelper dbHelper = new DiaryDbHelper(this);

            // Создаем объект ContentValues для записи в базу, в котором имена колонок - ключи,
            // а атрибуты записи - значения, введённые пользователем.
            ContentValues values = new ContentValues();
            values.put(dbHelper.COLUMN_NAME, nameString);
            values.put(dbHelper.COLUMN_COMMENT, commentString);
            values.put(dbHelper.COLUMN_DATE, outputDateString);

            // Получаем базу данных для записи
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            // Если ID записи равен 0 (значение по умолчанию), то это новая запись.
            // Выполняем вставку новой записив базу.
            if (mTraining.getId() == 0) {
                // Вставляем в таблицу строку с данными о новой тренировке.
                // Возвращаемое значение этого метода - ID вставленной строки в базе
                long rowId = database.insert(dbHelper.TABLE_TRAININGS, null, values);

                if (rowId == 0) {
                    // Если ID вставленной строки = 0, значит, строка не была вставлена.
                    // Выводим сообщние об ошибке
                    Toast.makeText(this, "Произошла ошибка при сохранении", Toast.LENGTH_SHORT).show();
                } else {
                    // Если ID вставленной строки отличается от нуля, значит, строка была вставлена.

                    mTraining.setId(rowId);



                    // Выводим сообщние об успешном сохранении
                    //Toast.makeText(this, "Тренировка сохранена", Toast.LENGTH_SHORT).show(); //TODO: Переставить в другое место


                }
            } else {
                // Если ID записи отличается от нуля, значит, этат трнировка уже существует в базе,
                // и её необходимо только обновить.

                // Выражение WHERE и массив аргументов для подстановки в запрос
                // и поиска обновлённой записи по ID
                String whereClause = "_id = ?";
                String[] whereArgs = new String[]{String.valueOf(mTraining.getId())};

                // Обновляем в таблице строку с тренировкой, найденной по ID.
                // Возвращаемое значение этого метода - количество обновлённых строк
                int rowsAffected = database.update(dbHelper.TABLE_TRAININGS,
                        values,
                        whereClause,
                        whereArgs);

                if (rowsAffected == 0) {
                    // Если количество обновлённых строк равно нулю, значит, строка не была обновлена.
                    // Выводим сообщние об ошибке
                    Toast.makeText(this, "Произошла ошибка при сохранении", Toast.LENGTH_SHORT).show();
                } else {
                    // Если количество обновлённых строк отличается от нуля, значит, строка была обновлена.
                    // Выводим сообщние об успешном сохранении
                    Toast.makeText(this, "Тренировка сохранена", Toast.LENGTH_SHORT).show();
                }

                // После записи данных закрываем базу
                dbHelper.close();


            }

            saveExercises();

            // Устанавливаем результат и завершаем операцию
            setResult(RESULT_OK);
            finish();
        } catch(ParseException e) {
            //TODO: Обработать исключение
        }
    }

    /**
    * Сохраняет в базу данных упражнения для данной тренировки
    */
    private void saveExercises() {
        // Пользователь может удалять, добавлять упражнения и подходы в разных комбинациях.
        // При этом каждое его действие не фиксируется в базе данных.
        // Поэтому наиболее удобный способ сохранения упражнений и подходов -
        // сначала удалить из базы все упражнения для данной тренировки, а затем заново записать их в базу

        // Удаляем из базы все упражнения для данной тренировки
        deleteExercises();

        // Каждое упражнение, введённое пользователем, сохраняем в базу
        for (int i = 0; i < mTraining.mExerciseList.size(); i++) {
            ExerciseModel exercise = mTraining.mExerciseList.get(i);
            saveExercise(exercise);
        }
    }

    /**
     * Сохраняет упражнение в базу
     * @param exercise Сохраняемое упражнение
     */
    private void saveExercise(ExerciseModel exercise) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Создаем объект ContentValues, в котором имена колонок - ключи,
        // а атрибуты записи - значения полей тренировки
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_NAME, exercise.getName());
        values.put(dbHelper.COLUMN_TRAINING_ID, mTraining.getId());

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Вставляем в таблицу строку с данными о новом упражнении
        // Возвращаемое значение - ID вставленного упражнения.
        long id = database.insert(dbHelper.TABLE_EXERCISES, null, values);

        // Присваиваем новый id упражнению
        exercise.setId(id);

        // После записи данных закрываем базу
        dbHelper.close();

        saveAttempts(exercise);
    }

    /**
     * Сохраняет в базу данных подходы для данного упражнения
     * @param exercise Упражнение
     */
    private void saveAttempts(ExerciseModel exercise) {
        // Удаляем из базы все подходы для данного упражнения
        deleteAttempts(exercise);

        // Каждый подход, введённый пользователем, сохраняем в базу
        for (int i = 0; i < exercise.mAttemptsList.size(); i++) {
            AttemptModel attempt = exercise.mAttemptsList.get(i);
            saveAttempt(exercise, attempt);
        }
    }

    /**
     * Удаляет из базы все подходы для данного упражнения
     * @param exercise Упражнение, подходы которого нужно удалить
     */
    private void deleteAttempts(ExerciseModel exercise) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Определяем ID упражнения и задаём выражение WHERE
        String idString = String.valueOf(exercise.getId());
        String whereClause = dbHelper.COLUMN_EXERCISE_ID + " = ?";
        String[] whereArgs = new String[] { idString };

        // Удаляем упражнения
        database.delete(dbHelper.TABLE_ATTEMPTS, whereClause, whereArgs);

        // После удаления закрываем базу
        dbHelper.close();
    }

    /**
     * Сохраняет подход в базу
     * @param exercise Упражнение, которому принадлежит сохраняемый подход
     * @param attempt Сохраняемый подход
     */
    private void saveAttempt(ExerciseModel exercise, AttemptModel attempt) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Создаем объект ContentValues, в котором имена колонок - ключи,
        // а атрибуты записи - значения полей тренировки
        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_EXERCISE_ID, exercise.getId());
        values.put(dbHelper.COLUMN_WEIGHT, attempt.getWeight());
        values.put(dbHelper.COLUMN_COUNT, attempt.getCount());

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Вставляем в таблицу строку с данными о новом подходе
        long rowId = database.insert(dbHelper.TABLE_ATTEMPTS, null, values);

        // После записи данных закрываем базу
        dbHelper.close();
    }

    /**
     * Удаляет из базы все упражнения для данной тренировки
     */
    private void deleteExercises(){
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Определяем ID тренировки и задаём выражение WHERE
        String idString = String.valueOf(mTraining.getId());
        String whereClause = dbHelper.COLUMN_TRAINING_ID + " = ?";
        String[] whereArgs = new String[] { idString };

        // Удаляем упражнения
        database.delete(dbHelper.TABLE_EXERCISES, whereClause, whereArgs);

        // После удаления закрываем базу
        dbHelper.close();
    }

    public void onDateEditTextClicked(View view) {
        new DatePickerDialog(TrainingActivity.this, dateSetListener, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
