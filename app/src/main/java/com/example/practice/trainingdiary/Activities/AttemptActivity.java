package com.example.practice.trainingdiary.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.R;


public class AttemptActivity extends AppCompatActivity {

    private EditText mWeightEditText;
    private EditText mCountEditText;

    /** Индекс подхода в коллекции */
    private int mIndex;

    /** Подход, для которого отображаются параметры */
    private AttemptModel mAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt);

        initializeActionBar();

        mWeightEditText = findViewById(R.id.edit_text_weight);
        mCountEditText = findViewById(R.id.edit_text_count);

        // Получаем вес и количество повторений в подходе
        getParameters();
    }

    /**
     * Получает вес и количество повторений из интента, поступившего от операции ExerciseActivity
     */
    private void getParameters() {

        mAttempt = getIntent().getParcelableExtra("attempt");
        mIndex = getIntent().getIntExtra("index", -1);

        if(mAttempt == null){
            mAttempt = new AttemptModel();
        } else {
            mWeightEditText.setText(String.valueOf(mAttempt.getWeight()));
            mCountEditText.setText(String.valueOf(mAttempt.getCount()));
        }
    }

    /**
     * Добавляет на панель стрелку и заголовок
     */
    private void initializeActionBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Установите значения");
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveAttempt();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO: Если пользователь при вводе нажимает на клавиатуре Enter, при закрытии операции
    // новый пункт не появляется в списке.
    // А если просто тыкает в одно поле, в другое поле и нажимает галочку, - то появляется.

    private void saveAttempt() {
        // TODO: Проверить на заполненность полей
        //TODO: Проверить на ноль

        String weightString = mWeightEditText.getText().toString();
        String countString = mCountEditText.getText().toString();

        TextInputLayout weightLayout = findViewById(R.id.weight_layout);
        if (weightString.isEmpty()){
            weightLayout.setError("Введите вес");
            return;
        } else {
            weightLayout.setError(null);
        }

        TextInputLayout countLayout = findViewById(R.id.count_layout);
        if (countString.isEmpty()){
            countLayout.setError("Введите количество");
            return;
        } else {
            countLayout.setError(null);
        }

        int weight = Integer.parseInt(mWeightEditText.getText().toString());
        int count = Integer.parseInt(mCountEditText.getText().toString());



        mAttempt.setWeight(weight);
        mAttempt.setCount(count);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("index", mIndex);
        resultIntent.putExtra("attempt", mAttempt);

        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

