package com.example.practice.trainingdiary.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.practice.trainingdiary.DataModels.DiaryEntryModel;
import com.example.practice.trainingdiary.DataModels.MeasureModel;
import com.example.practice.trainingdiary.DataModels.NoteModel;
import com.example.practice.trainingdiary.DataModels.TrainingModel;
import com.example.practice.trainingdiary.DiaryDbHelper;
import com.example.practice.trainingdiary.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Операция, соответствующая главному экрану
 */
public class MainActivity extends AppCompatActivity {

    /** Календарь */
    private CompactCalendarView mCalendarView;

    /** Надпись, в которой будет храниться заголовок календаря */
    private TextView mMonthTextView;

    /** Меню из плавающих кнопок действия */
    FloatingActionMenu floatingActionMenu;

    /** Плавающие кнопки действия */
    FloatingActionButton trainingButton, measureButton, noteButton;

    /** Список всех записей для отображения в календаре */
    ArrayList<DiaryEntryModel> mEntryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настраиваем панель действий
        initializeActionBar();

        // Инициализируем элементы управления и другие поля класса
        initializeCalendar();
        mEntryList = new ArrayList<>();

        // Инициализируем меню из плавающих кнопок действия и сами эти кнопки
        initializeFloatingActionButtons();
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                String dateString = simpleDateFormat.format(new Date());
                intent.putExtra("date", dateString);
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
     * Устанавливает заголовок панели действий и включает её отображение на экране
     */
    private void initializeActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Дневник тренировок");
        setSupportActionBar(toolbar);
    }

    /**
     * Этот метод вызывается, когда операция MainActivity становится видимой и получает фокус.
     * Мы используем этот метод, чтобы повторно загрузить данные из базы и обновить календарь
     * (после создания тренировки и закрытия операции TrainingActivity)
     */
    @Override
    protected void onResume() {
        super.onResume();

        getDatabaseInfo();
    }

    /**
     * Получает данные из базы и загружает их в календарь   //TODO: Ещё раз продумать механизм закрытия операции
     */
    private void getDatabaseInfo() {
        // Очищаем список записей
        mEntryList.clear();

        // Получаем из базы данных тренировки, измерения и заметки и записываем их в список mEntryList
        getTrainings();
        getMeasures();
        getNotes();

        // Из этого списка заполняем календарь, отображаемый на экране
        fillCalendar();
    }

    /** Получает из базы тренировки и записывает их в список mEntryList */
    private void getTrainings() {

        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        // Получаем базу данных для чтения
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Определяем массив колонок, данные из которых нам нужны
        // Для отображения в календаре нам необходима только дата
        String[] columns = new String[]{ dbHelper.COLUMN_DATE };

        // С помощью метода query запрашиваем все записи из таблицы тренировок
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_TRAININGS,
                columns,
                null,
                null,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индекс колонки даты
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем дату тренировки
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
                TrainingModel trainingModel = new TrainingModel(date);

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

        // Определяем массив колонок, данные из которых нам нужны
        // Для отображения в календаре нам необходима только дата
        String[] columns = new String[]{ dbHelper.COLUMN_DATE };

        // С помощью метода query запрашиваем все записи из таблицы измерений
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_MEASURES,
                columns,
                null,
                null,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индекс колонки даты
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем дату измерения
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

        // Определяем массив колонок, данные из которых нам нужны
        // Для отображения в календаре нам необходима только дата
        String[] columns = new String[]{ dbHelper.COLUMN_DATE };

        // С помощью метода query запрашиваем все записи из таблицы заметок
        // Результат запроса записывается в объект Cursor
        Cursor cursor = database.query(dbHelper.TABLE_NOTES,
                columns,
                null,
                null,
                null,
                null,
                null);

        // Если в курсоре присутствуют запси
        if (cursor.moveToFirst()){

            // Получаем индекс колонки даты
            int dateColumnIndex = cursor.getColumnIndex(dbHelper.COLUMN_DATE);

            // Пока в курсоре есть записи, выполняем цикл для каждой записи
            do {
                // Считываем дату заметки
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
                NoteModel noteModel = new NoteModel(date);

                // Добавляем вновь созданную запись в общий список
                mEntryList.add(noteModel);
            } while (cursor.moveToNext());
        }

        // После чтения данных закрываем базу
        dbHelper.close();
    }

    /** Инициализирует календарь */
    private void initializeCalendar(){
        // Инициализируем календарь
        mCalendarView = findViewById(R.id.compact_calendar_view);
        mCalendarView.setUseThreeLetterAbbreviation(true);

        // Инициализируем текстовую надпись - заголовок календаря
        mMonthTextView = findViewById(R.id.text_view_month);

        // Получаем первый день текущего месяца (для определения месяца)
        Date firstDayOfMonth = mCalendarView.getFirstDayOfCurrentMonth();
        setHeading(firstDayOfMonth);

        // Добавляем обработчики событий выбора дня и прокрутки месяца
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                // При нажатии на конкретный день
                // запускаем операцию DayActivity, предварительно передав туда выбранную дату в строковом формате
                Context context = getApplicationContext();
                Intent intent = new Intent(context, DayActivity.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
                String dateString = simpleDateFormat.format(dateClicked);
                intent.putExtra("date", dateString);
                startActivity(intent);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // При прокрутке на другой месяц меняем заголовок календаря
                setHeading(firstDayOfNewMonth);
            }
        });

        // Инициализируем кнопки для перехода к предыдущему/следующему месяцам
        // и устанавливаем им обработчики нажатия
        ImageButton previousMonthButton = findViewById(R.id.button_previous_month);
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.showPreviousMonth();
            }
        });

        ImageButton nextMonthButton = findViewById(R.id.button_next_month);
        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCalendarView.showNextMonth();
            }
        });
    }

    /**
     * Устанавливает заголовок календаря в формате "Май 2018"
     * @param firstDayOfMonth Дата, соответствующая первому дню месяца, для определения текущего месяца
     * @return Заголовок для календаря в строковой форме
     */
    private void setHeading(Date firstDayOfMonth) {
        // Инициализируем строку-результат
        String resultString = new String();

        // Определяем индекс месяца (от 0 до 1)
        int monthIndex = firstDayOfMonth.getMonth();

        // В зависимости от индекса добавляем в строку-результат его название
        switch (monthIndex){
            case 0:
                resultString = "Январь";
                break;
            case 1:
                resultString = "Февраль";
                break;
            case 2:
                resultString = "Март";
                break;
            case 3:
                resultString = "Апрель";
                break;
            case 4:
                resultString = "Май";
                break;
            case 5:
                resultString = "Июнь";
                break;
            case 6:
                resultString = "Июль";
                break;
            case 7:
                resultString = "Август";
                break;
            case 8:
                resultString = "Сентябрь";
                break;
            case 9:
                resultString = "Октябрь";
                break;
            case 10:
                resultString = "Ноябрь";
                break;
            case 11:
                resultString = "Декабрь";
                break;
        }

        // Добавляем пробел между месяцем и годом
        resultString = resultString + " ";

        // Определяем год
        // Метод getYear возвращает годы с 1900 года, поэтому, чтобы получить 2018 год,
        // к полученному числу нужно прибавить 1900
        int year = firstDayOfMonth.getYear() + 1900;

        // Добавляем год к строке-результату
        resultString = resultString + year;

        // Устанавливаем строку-результат в качестве заголовка календаря
        mMonthTextView.setText(resultString);
    }

    /**
     * Заполняет календарь на главном экране данными из списка mEntryList
     */
    private void fillCalendar() {
        // Удаляем из календаря все существующие события
        mCalendarView.removeAllEvents();

        // Проходим циклом по всему списку mEntryList
        for (int i = 0; i < mEntryList.size(); i++){
            // Инициализируем запись (тренировку, измерение или заметку), с которой будем работать
            DiaryEntryModel entryModel = mEntryList.get(i);

            // Получаем дату и время этой записи в виде числи миллисекунд,
            // поскольку для вставки в календарь требуется именно такое представление
            long timeInMilliseconds = entryModel.getDate().getTime();

            // Создаём и инициализируем переменную color, котораи будет хранить цвет маркера
            int color = -1;

            // Определяем тип записи и в зависимости от него назначаем цвет
            if (entryModel instanceof TrainingModel){
                color = ContextCompat.getColor(getApplicationContext(), R.color.blue);
            } else if (entryModel instanceof MeasureModel){
                color = ContextCompat.getColor(getApplicationContext(), R.color.green);
            } else if (entryModel instanceof NoteModel){
                color = ContextCompat.getColor(getApplicationContext(), R.color.orange);
            }

            // Если в переменной color изменилось значение,
            // значит, тип записи удалось определить
            if (color != -1){
                // Добавляем в календарь новое событие с указанным цветом и временем в миллисекундах
                mCalendarView.addEvent(new Event(color, timeInMilliseconds, null));
            }
        }
    }

    //TODO: Следующие 5 методов предназначены для работы с тестовыми данными и будут удалены
    /**
     * Вставляет в базу тестовые данные
     */
    private void insertDummyData() {
        clearDatabase();

        // Создадим несколько записей об измерениях, тренировках и заметках
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 1).getTime(), "Руки"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 4).getTime(), "Ноги/плечи"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 6).getTime(), "Грудь/спина"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 8).getTime(), "Руки"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 11).getTime(), "Ноги/плечи"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 13).getTime(), "Грудь/спина"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 15).getTime(), "Руки"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 18).getTime(), "Ноги/плечи"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 20).getTime(), "Грудь/спина"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 22).getTime(), "Руки"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 25).getTime(), "Ноги/плечи"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 27).getTime(), "Грудь/спина"));
        insertDummyTraining(new TrainingModel(new GregorianCalendar(2018, Calendar.JUNE, 29).getTime(), "Руки"));

        insertDummyMeasure(new MeasureModel(new GregorianCalendar(2018, Calendar.JUNE, 1).getTime()));
        insertDummyMeasure(new MeasureModel(new GregorianCalendar(2018, Calendar.JUNE, 8).getTime()));
        insertDummyMeasure(new MeasureModel(new GregorianCalendar(2018, Calendar.JUNE, 15).getTime()));
        insertDummyMeasure(new MeasureModel(new GregorianCalendar(2018, Calendar.JUNE, 22).getTime()));
        insertDummyMeasure(new MeasureModel(new GregorianCalendar(2018, Calendar.JUNE, 29).getTime()));

        insertDummyNote(new NoteModel(new GregorianCalendar(2018, Calendar.JUNE, 1).getTime(), "Заметка от 1 июня"));
        insertDummyNote(new NoteModel(new GregorianCalendar(2018, Calendar.JUNE, 14).getTime(), "Заметка от 14 июня"));
        insertDummyNote(new NoteModel(new GregorianCalendar(2018, Calendar.JUNE, 27).getTime(), "Заметка от 27 июня"));

        getDatabaseInfo();
    }

    public void insertDummyTraining(TrainingModel model){
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_NAME, model.getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String dateString = simpleDateFormat.format(model.getDate());
        values.put(dbHelper.COLUMN_DATE, dateString);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.insert(dbHelper.TABLE_TRAININGS, null, values);

        dbHelper.close();
    }

    public void insertDummyMeasure(MeasureModel model){
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        ContentValues values = new ContentValues();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String dateString = simpleDateFormat.format(model.getDate());
        values.put(dbHelper.COLUMN_DATE, dateString);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.insert(dbHelper.TABLE_MEASURES, null, values);

        dbHelper.close();
    }

    public void insertDummyNote(NoteModel model){
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);

        ContentValues values = new ContentValues();

        values.put(dbHelper.COLUMN_TEXT, model.getText());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String dateString = simpleDateFormat.format(model.getDate());
        values.put(dbHelper.COLUMN_DATE, dateString);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.insert(dbHelper.TABLE_NOTES, null, values);

        dbHelper.close();

        Date date = model.getDate();
        long timeInMillis = date.getTime();
    }

    private void clearDatabase() {
        DiaryDbHelper dbHelper = new DiaryDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(dbHelper.TABLE_TRAININGS, null, null);
        database.delete(dbHelper.TABLE_MEASURES, null, null);
        database.delete(dbHelper.TABLE_NOTES, null, null);
        dbHelper.close();
    }
}
