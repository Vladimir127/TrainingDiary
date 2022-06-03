package com.example.practice.trainingdiary.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.practice.trainingdiary.Activities.AttemptActivity;
import com.example.practice.trainingdiary.Activities.ExerciseActivity;
import com.example.practice.trainingdiary.Activities.TrainingActivity;
import com.example.practice.trainingdiary.DataModels.AttemptModel;
import com.example.practice.trainingdiary.DataModels.DiaryEntryModel;
import com.example.practice.trainingdiary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер, соединяющий коллекцию подходов со списком на экране операции ExerciseActivity
 */

public class AttemptAdapter extends ArrayAdapter<AttemptModel> {

    /** Код запроса результата операции AttemptActivity */
    private static final int REQUEST_CODE_ATTEMPT = 1;

    /** Контекст */
    private Context mContext;

    private List<AttemptModel> mAttemptList;

    /**
     * Создаёт экземпляр класса
     * @param context Контекст
     * @param attempts Список подходов
     */
    public AttemptAdapter(@NonNull Context context, List<AttemptModel> attempts) {
        super(context, 0, attempts);
        mContext = context;
        mAttemptList = attempts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final AttemptModel attempt = getItem(position);
        final int index = position;

        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.item_attempt, parent, false);

        TextView textView1 = rootView.findViewById(R.id.text_view_value1);
        int weight = attempt.getWeight();
        String weightString = String.valueOf(weight);
        textView1.setText(weightString);   //TODO: Форматирование чисел

        TextView textView2 = rootView.findViewById(R.id.text_view_value2);
        textView2.setText(String.valueOf(attempt.getCount()));

        // Получаем из макета кнопку с тремя точками и настраиваем контекстное меню
        final ImageButton menuButton = rootView.findViewById(R.id.button_menu);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, menuButton);
                popupMenu.getMenuInflater().inflate(R.menu.menu_entry_popup, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_edit:
                                // При выборе пункта меню "Редактировать"
                                // Запускаем операцию AttemptActivity,
                                // предварительно передав в неё сам объект подхода и его индекс в коллекции
                                Intent intent = new Intent(mContext, AttemptActivity.class);
                                intent.putExtra("attempt", attempt);
                                intent.putExtra("index", index);

                                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_ATTEMPT);
                                return true;
                            case R.id.action_delete:
                                // При выборе пункта меню "Удалить"
                                // отображаем диалог подтверждения удаления
                                showDeleteConfirmationDialog(attempt);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });

        // Устанавливаем обработчик нажатия на rootView, то есть, на сам пункт списка
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Запускаем операцию AttemptActivity,
                // предварительно передав в неё сам объект подхода и его индекс в коллекции
                Intent intent = new Intent(mContext, AttemptActivity.class);
                intent.putExtra("attempt", attempt);
                intent.putExtra("index", index);

                ((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_ATTEMPT);

            }
        });

        return rootView;
    }

    private void showDeleteConfirmationDialog(final AttemptModel attempt) {
        // Создаём AlertDialog.Builder и устанавливаем сообщение и обработчики нажатий
        // для положительной и отрицательной кнопок
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Удалить подход?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // При нажатии кнопки "Удалить" удаляем запись из списки
                // и оповещаем адаптер о том, что данные изменились,
                // чтобы он обновил список на экране
                mAttemptList.remove(attempt);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // При нажатии кнопки "Отмена" закрываем диалог
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Создаём и показываем AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
