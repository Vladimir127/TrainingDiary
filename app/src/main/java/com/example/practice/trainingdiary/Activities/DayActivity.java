package com.example.practice.trainingdiary.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.practice.trainingdiary.Adapters.EntryCardAdapter;
import com.example.practice.trainingdiary.DataModels.DiaryEntryModel;
import com.example.practice.trainingdiary.DataModels.MeasureModel;
import com.example.practice.trainingdiary.DataModels.NoteModel;
import com.example.practice.trainingdiary.DataModels.TrainingModel;
import com.example.practice.trainingdiary.DiaryDbHelper;
import com.example.practice.trainingdiary.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Операция, соответствующая экрану дня
 */

public class DayActivity extends AppCompatActivity {

    /** Адаптер, соединяющий данные со списком на экране */
    EntryCardAdapter mAdapter;

    /** Дата, за которую отображаются записи, в формате "yyyy-MM-dd HH:mm:ss.sss" */
    private String mDateString;

    /** Список карточек на экране */
    private ListView mListView;

    /** Меню из плавающих кнопок действия */
    FloatingActionMenu floatingActionMenu;

    /** Плавающие кнопки действия */
    FloatingActionButton trainingButton, measureButton, noteButton;

    /** Список всех записей за день для отображения на экране */
    ArrayList<DiaryEntryModel> mEntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        // Получаем дату, за которую необходимо отобразить записи,
        // из интента, переданного операцией MainActivity
        mDateString = getIntent().getStringExtra("date");

        // Настраиваем панель действий
        initializeActionBar();

        // Инициализируем элементы управления и другие поля класса
        mEntryList = new ArrayList<>();

        mListView = findViewById(R.id.recycler_view_entries);
        mListView.setDivider(null);

        // Находим и устанавливаем empty view для mListView -
        // надпись, которая отображается, когда в списке нет пунктов
        View emptyView = findViewById(R.id.empty_view);
        mListView.setEmptyView(emptyView);

        // Получаем данные из базы
        getDatabaseInfo();

        // Инициализируем меню из плавающих кнопок действия и сами эти кнопки
        initializeFloatingActionButtons();
    }

    /**
     * Добавляет на панель действий заголовок и иконку "Назад"
     */
    private void initializeActionBar() {
        // Инициализируем панель действий
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Преобразуем дату из исходного формата в формат для отображения в заголовке
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        Date date;

        try {
            date = inputFormat.parse(mDateString);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String dateString = outputFormat.format(date);

            // Устанавливаем полученную строку с датой в качестве заголовка на панели приложения
            toolbar.setTitle(dateString);
        } catch (ParseException e){
            e.printStackTrace();
        }

        // Устанавливаем текущую панель в качестве основной панели действий
        setSupportActionBar(toolbar);

        // Для отображения стрелки назад на панели действий
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Инициализирует плавающие кнопки действия и устанавливает им обработчики нажатия
     */
    private void initializeFloatingActionButtons() {

        floatingActionMenu = findViewById(R.id.floating_action_menu);
        trainingButton = findViewById(R.id.floating_action_item_1);
        measureButton = findViewById(R.id.floating_action_item_2);
        noteButton = findViewById(R.id.floating_action_item_3);

        // Устанавливаем обработчики нажатия плавающим кнопкам действия
        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Intent intent = new Intent(getApplicationContext(), TrainingActivity.class);
                intent.putExtra("date", mDateString);
                startActivity(intent);
            }
        });

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Toast.makeText(getApplicationContext(), "Пока не реализовано", Toast.LENGTH_SHORT);
            }
        });

        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingActionMenu.close(true);
                Toast.makeText(getApplicationContext(), "Пока не реализовано", Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * Этот метод вызывается, когда операция DayActivity становится видимой и получает фокус.
     * Мы используем этот метод, чтобы повторно загрузить данные из базы и обновить календарь
     * (после создания тренировки и закрытия операции TrainingActivity)
     */
    @Override
    protected void onResume() {
        super.onResume();

        getDatabaseInfo();
    }

    //TODO: Почему здесь нет OnCreateOptionsMenu?
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
     * Получает данные из базы и загружает их в список
     */
    private void getDatabaseInfo() {

        // Очищаем список записей
        mEntryList.clear();

        // Получаем из базы данных тренировки, измерения и заметки и записываем их в список mEntryList
        getTrainings();
        getMeasures();
        getNotes();

        // Инициализируем адаптер, соединяющий данные со списком на экране
        mAdapter = new EntryCardAdapter(this, mEntryList);

        // Устанавливаем этот адаптер элементу mListView
        mListView.setAdapter(mAdapter);
    }

    /** Получает из базы тренировки и записывает их в список mEntryList */
    private void getTrainings() {

        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует, чтобы дата записи в базе была равна выбранному дню,
        // хранящемуся в переменной mDateString
        String selection = dbHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = new String[]{ mDateString };

        //TODO: Запрашивать из базы только нужные колонки

        // С помощью метода query запрашиваем записи за нужную дату из таблицы тренировок
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_TRAININGS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индексы колонок таблицы
            int idColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_NAME);
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем ID, название и дату тренировки
                int id = cursor.getInt(idColumnIndex);
                String name = cursor.getString(nameColumnIndex);
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

                // На основании данных, полученных из базы, создаём новую модель тренировки
                TrainingModel trainingModel = new TrainingModel(date, name);
                trainingModel.setId(id);

                // Добавляем вновь созданную запись в общий список
                mEntryList.add(trainingModel);
            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();
    }

    /** Получает из базы измерения и записывает их в список mEntryList */
    private void getMeasures() {

        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует, чтобы дата записи в базе была равна выбранному дню,
        // хранящемуся в переменной mDateString
        String selection = "date = ?";
        String[] selectionArgs = new String[]{ mDateString };

        // С помощью метода query запрашиваем записи за нужную дату из таблицы заметок
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_MEASURES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индексы колонок таблицы
            int idColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_ID);
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {

                // Считываем ID и дату измерения
                int id = cursor.getInt(idColumnIndex);
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

                // На основании данных, полученных из базы, создаём новую модель измерения
                MeasureModel measureModel = new MeasureModel(date);
                measureModel.setId(id);

                // Добавляем вновь созданную запись в общий список
                mEntryList.add(measureModel);
            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();
    }

    /** Получает из базы заметки и записывает их в список mEntryList */
    private void getNotes() {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем условие запроса с помощью selection и selectionArgs
        // selection - это выражение WHERE в запросе.
        // selectionArgs - массив аргументов, которые будут подставлены
        // вместо знаков вопроса в строку selection.
        // В данном случае нас интересует, чтобы дата записи в базе была равна выбранному дню,
        // хранящемуся в переменной mDateString
        String selection = "date = ?";
        String[] selectionArgs = new String[]{ mDateString };

        // С помощью метода query запрашиваем записи за нужную дату из таблицы заметок
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_NOTES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индексы колонок таблицы
            int idColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_ID);
            int textColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_TEXT);
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем ID, текст и дату заметки
                int id = cursor.getInt(idColumnIndex);
                String text = cursor.getString(textColumnIndex);
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

                // На основании данных, полученных из базы, создаём новую модель заметки
                NoteModel noteModel = new NoteModel(date, text);
                noteModel.setId(id);

                // Добавляем вновь созданную запись в общий список
                mEntryList.add(noteModel);
            } while (cursor.moveToNext());
        }

        // Добавляем вновь созданную запись в общий список
        dbHelper.close();
    }
}
