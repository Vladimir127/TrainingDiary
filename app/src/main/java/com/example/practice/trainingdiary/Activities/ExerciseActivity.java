package com.example.practice.trainingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.practice.trainingdiary.Adapters.AttemptAdapter;
import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.R;

import java.util.ArrayList;

//TODO
public class ExerciseActivity extends AppCompatActivity {

    /** Адаптер, соединяющий данные со списком на экране */
    private AttemptAdapter mAdapter;

    /** Название упражнения для отображения в заголовке */
    private String mExerciseName;

    /** Список подходов данного упражнения */
    private ArrayList<AttemptModel> mAttemptsList;

    /** Код запроса результата операции AttemptActivity */
    private static final int REQUEST_CODE_ATTEMPT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        //TODO: Во всех операциях вынести в отдельный метод initializeListView
        mAttemptsList = new ArrayList<>();
//        mAttemptsList.add(new AttemptModel(50, 10));
//        mAttemptsList.add(new AttemptModel(60, 15));
//        mAttemptsList.add(new AttemptModel(70, 20));

        mAdapter = new AttemptAdapter(this, mAttemptsList);
        ListView attemptsListView = findViewById(R.id.list_view_attempts);
        attemptsListView.setAdapter(mAdapter);

        // Находим и устанавливаем empty view для listView -
        // надпись, которая отображается, когда в списке нет пунктов
        View emptyView = findViewById(R.id.empty_view);
        attemptsListView.setEmptyView(emptyView);

        // Получаем подходы из интента, присланного операцией TrainingActivity
        getAttempts();

        // Настраиваем панель приложения и устанавливаем ей заголовок
        initializeActionBar();

        // Настраиваем плавающую кнопку действия
        initializeFloatingActionButton();
    }

    /**
     * Получает подходы из интента, присланного операцией TrainingActivity,
     * и записывает их в коллекцию mAttemptsList для отображения на экране
     */
    private void getAttempts() {

        ArrayList<AttemptModel> attemptsList = getIntent().getParcelableArrayListExtra("attempts");

        mAttemptsList.clear();

        for (int i = 0; i < attemptsList.size(); i++){
            mAttemptsList.add(attemptsList.get(i));
        }

        mAdapter.notifyDataSetChanged();
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
                saveExercise();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Сохраняет введённые пользователем подходы для передачи в вызывающую операцию TrainingActivity
     */
    private void saveExercise() {
        // Создаём интент-результат, в который будут упакованы выбранные подходы
        Intent resultIntent = new Intent(this, TrainingActivity.class);

        // Помещаем название упражнения и список подходов в интент
        resultIntent.putExtra("exercise_name", mExerciseName);
        resultIntent.putParcelableArrayListExtra("attempts", mAttemptsList);

        // Устанавливаем результат и завершаем операцию
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * Инициализирует плавающую кнопку действия и устанавливает ей обработчик нажатия
     */
    private void initializeFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AttemptActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ATTEMPT);
                //startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ATTEMPT) {
            if (resultCode == RESULT_OK) {
                AttemptModel attempt = data.getParcelableExtra("attempt");
                int index = data.getIntExtra("index", -1);

                if (index == -1){
                    mAttemptsList.add(attempt);
                }else{
                    mAttemptsList.set(index, attempt);
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }
}
