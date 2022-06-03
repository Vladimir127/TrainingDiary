package com.example.practice.trainingdiary.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.practice.trainingdiary.Activities.ExerciseActivity;
import com.example.practice.trainingdiary.Activities.HistoryActivity;
import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Адаптер, соединяющий коллекцию упражнений со списком на экране операции HistoryActivity
 */

public class ExerciseHistoryAdapter extends ArrayAdapter<ExerciseModel> {
    /** Код запроса результата операции ExerciseActivity */
    private static final int REQUEST_CODE_ATTEMPTS = 2;

    /** Контекст */
    private Context mContext;

    /**
     * Создаёт экземпляр класса
     * @param context Контекст
     * @param exercises Список упражнений
     */
    public ExerciseHistoryAdapter(@NonNull Context context, List<ExerciseModel> exercises) {
        super(context, 0, exercises);
        mContext = context; //TODO: Сделать так же во всех остальных случаях
    }

    //TODO: Написать комментарии
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ExerciseModel exercise = getItem(position);

        //TODO: Переименовать card_exercise_layout
        final View rootView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_history_layout, parent, false);

        // Получаем из макета второй элемент TextView и устанавливаем ему текст - дату упражнения,
        // предварительно приведя её к нужному формату.
        TextView textViewDate = rootView.findViewById(R.id.text_view_name);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String dateString = simpleDateFormat.format(exercise.getDate());
        textViewDate.setText(dateString);

        // Инициализируем таблицу подходов и заполняем её данными
        final TableLayout table = rootView.findViewById(R.id.table_layout);
        fillTableLayout(table, exercise);

        //TODO: Сделать кнопку недоступной, если нет подходов
        //TODO: Запоминать свёрнутость/развёрнутость упражнений
        // Устанавливаем обработчик нажатия на кнопку сворачивания/разворачивания
        // При нажатии на эту кнопку меняем видимость таблицы и внешний вид кнопки
        final ImageButton expandButton = rootView.findViewById(R.id.button_expand);
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (table.getVisibility() == View.GONE){
                    table.setVisibility(View.VISIBLE);
                    expandButton.setImageResource(R.drawable.ic_expand_less_black_24dp);
                } else if (table.getVisibility() == View.VISIBLE){
                    table.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.ic_expand_more_black_24dp);
                }
            }
        });

        return rootView;
    }

    private void fillTableLayout(TableLayout table, ExerciseModel exercise) {
        // TODO: Что будет, если поместить в строки слишком большие числа
        // TODO: Нужно ли предупреждение об удалении упражнения, в котором есть подходы
        // TODO: Нужно ли предупреждение при снятии галочки со списка упражнений
        // TODO: Добавить рябь к элементам списка
        // TODO: Будет ли открываться экран упражнения при нажатии на таблицу подходв? Если нет, перенести обработчик события только на заголовок



        // Добавляем в таблицу строки
        TableRow rowTitle = table.findViewById(R.id.row_title);
        TableRow rowWeight = table.findViewById(R.id.row_weight);
        TableRow rowCount = table.findViewById(R.id.row_count);

        for (int i = 0; i < exercise.mAttemptsList.size(); i++){
            AttemptModel attempt = exercise.mAttemptsList.get(i);

            FrameLayout frameLayout = new FrameLayout(mContext);
            TableRow.LayoutParams params0 = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            params0.gravity = Gravity.CENTER;
            frameLayout.setLayoutParams(params0);



            // Добавляем содержимое в строку с номерами подходов
            TextView indexTextView = new TextView(mContext);
            indexTextView.setText(String.valueOf(i + 1));
            indexTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            indexTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            indexTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
            indexTextView.setTypeface(Typeface.SANS_SERIF);
            indexTextView.setGravity(Gravity.CENTER);
            indexTextView.setBackground(mContext.getDrawable(R.drawable.circle_orange_small));  //TODO: Кружок растягиваеся при длином тексте

            TableRow.LayoutParams params1 = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params1.setMargins(convertDpToPx(8),
                    convertDpToPx(4),
                    convertDpToPx(8),
                    convertDpToPx(4));

            indexTextView.setLayoutParams(params1);
            frameLayout.addView(indexTextView);
            rowTitle.addView(frameLayout);

            // Добавляем содержимое в строку с весом
            TextView weightTextView = new TextView(mContext);
            weightTextView.setText(String.valueOf(attempt.getWeight()));
            weightTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            weightTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            weightTextView.setTextColor(ContextCompat.getColor(mContext, R.color.primary_text));
            weightTextView.setTypeface(Typeface.SANS_SERIF);
            weightTextView.setGravity(Gravity.CENTER_VERTICAL);

            TableRow.LayoutParams params2 = new TableRow.LayoutParams();

            params2.setMargins(convertDpToPx(8),
                    convertDpToPx(4),
                    convertDpToPx(8),
                    convertDpToPx(4));

            weightTextView.setLayoutParams(params2);
            rowWeight.addView(weightTextView);

            // Добавляем содержимое в строку с весом
            TextView countTextView = new TextView(mContext);
            countTextView.setText(String.valueOf(attempt.getCount()));
            countTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            countTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            countTextView.setTextColor(ContextCompat.getColor(mContext, R.color.primary_text));
            countTextView.setTypeface(Typeface.SANS_SERIF);
            countTextView.setGravity(Gravity.CENTER_VERTICAL);

            TableRow.LayoutParams params3 = new TableRow.LayoutParams();

            params3.setMargins(convertDpToPx(8),
                    convertDpToPx(4),
                    convertDpToPx(8),
                    convertDpToPx(4));

            countTextView.setLayoutParams(params3);
            rowCount.addView(countTextView);
        }
    }

    private int convertDpToPx(int dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        int px = (int)(dp * scale + 0.5f);
        return px;
    }
}
