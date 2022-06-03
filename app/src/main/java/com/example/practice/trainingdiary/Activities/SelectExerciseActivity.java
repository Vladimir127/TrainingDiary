package com.example.practice.trainingdiary.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.practice.trainingdiary.Adapters.ExerciseAdapter;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.R;

import java.util.ArrayList;

public class SelectExerciseActivity extends AppCompatActivity {

    /** Адаптер, соединяющий данные со списком на экране */
    private ExerciseAdapter mAdapter;
    private ArrayList<ExerciseModel> mExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercise);

        mExerciseList = new ArrayList<>();
        mExerciseList.add(new ExerciseModel("Приседания со штангой на плечах"));
        mExerciseList.add(new ExerciseModel("Подъём штанги на бицепс стоя"));
        mExerciseList.add(new ExerciseModel("Жим от груди в тренажёре сидя"));
        mExerciseList.add(new ExerciseModel("Жим гантелей сидя"));
        mExerciseList.add(new ExerciseModel("Тяга штанги в наклоне"));
        mExerciseList.add(new ExerciseModel("Французский жим лёжа"));
        mExerciseList.add(new ExerciseModel("Выпады с гантелями"));
        mExerciseList.add(new ExerciseModel("Жим гантелей лёжа на наклонной скамье"));
        mExerciseList.add(new ExerciseModel("Жим ногами"));
        mExerciseList.add(new ExerciseModel("Жим штанги лёжа"));

        mAdapter = new ExerciseAdapter(getApplicationContext(), mExerciseList);

        ListView recyclerView = findViewById(R.id.exercise_list_view);
        recyclerView.setAdapter(mAdapter);

        // Настраиваем панель приложения и устанавливаем ей заголовок
        initializeActionBar();

        getCheckedExercises();
    }

    /** Получает выбранные упражнения из операции TrainingActivity, чтобы проставить в них галки */
    private void getCheckedExercises() {
        // Получаем выбранные упражнения из интента, который пришёл из операции TrainingActivity
        ArrayList<ExerciseModel> checkedExercises = getIntent().getParcelableArrayListExtra("exercises");

        for (int i = 0; i < checkedExercises.size(); i++){
            ExerciseModel checkedExercise = checkedExercises.get(i);
            String checkedExerciseName = checkedExercise.getName();

            for (int j = 0; j < mExerciseList.size(); j++) {
                ExerciseModel exercise = mExerciseList.get(j);
                String exerciseName = exercise.getName();

                if (checkedExerciseName.equals(exerciseName)){
                    exercise.setIsChecked(true);
                    break;
                }
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Добавляет на панель действий иконку "Назад" и заголовок
     */
    private void initializeActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Выберите упражнения");
        setSupportActionBar(toolbar);

        // Для отображения стрелки назад на панели приложения
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveCheckedExercises();
                return true;
            case android.R.id.home:
                finish();
                return true;
                default:
                    return false;
        }
    }

    /** Сохраняет выбранные упражнения для передачи в вызывающую операцию TrainingActivity */
    private void saveCheckedExercises() {
        // Сформируем список выбранных упражнений для возврата в операцию TrainingActivity
        ArrayList<ExerciseModel> checkedExercisesList = new ArrayList<>();

        // Перебираем общий список упражнний
        for (int i = 0; i < mExerciseList.size(); i++) {
            ExerciseModel exercise = mExerciseList.get(i);

            // Если упражнение отмечено галочкой,
            if (exercise.getIsChecked()) {
                // добавляем его в список выбранных упражнений
                checkedExercisesList.add(exercise);
            }
        }

        // Создаём интент-результат, в который будут упакованы выбранные упражнения
        Intent resultIntent = new Intent(this, TrainingActivity.class);

        // Помещаем сформированный список в интент
        resultIntent.putParcelableArrayListExtra("exercises", checkedExercisesList);

        // Устанавливаем результат и завершаем операцию
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
