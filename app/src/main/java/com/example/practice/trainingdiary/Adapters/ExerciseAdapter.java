package com.example.practice.trainingdiary.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.example.practice.trainingdiary.Activities.HistoryActivity;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.R;

import java.util.List;

/**
 * Адаптер, соединяющий коллекцию упражнений со списком на экране операции SelectExerciseActivity
 */

public class ExerciseAdapter extends ArrayAdapter<ExerciseModel> {

    private Context mContext;

    /**
     * Создаёт экземпляр класса
     * @param context Контекст
     * @param exercises Список упражнений
     */
    public ExerciseAdapter(@NonNull Context context,List<ExerciseModel> exercises) {
        super(context, 0, exercises);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ExerciseModel exercise = getItem(position);

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.exercise_layout, parent, false);

        CheckBox checkBox = rootView.findViewById(R.id.checkbox);
        checkBox.setText(exercise.getName());
        checkBox.setChecked(exercise.getIsChecked());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                exercise.setIsChecked(b);
            }
        });

        final ImageButton historyButton = rootView.findViewById(R.id.button_history);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, HistoryActivity.class);
                intent.putExtra("exercise_name", exercise.getName());
                mContext.startActivity(intent);
            }
        });

        return rootView;
    }
}
