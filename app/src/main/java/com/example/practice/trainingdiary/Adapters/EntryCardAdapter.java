package com.example.practice.trainingdiary.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import com.example.practice.trainingdiary.Activities.TrainingActivity;
import com.example.practice.trainingdiary.DataModels.DiaryEntryModel;
import com.example.practice.trainingdiary.DataModels.ExerciseModel;
import com.example.practice.trainingdiary.DataModels.MeasureModel;
import com.example.practice.trainingdiary.DataModels.NoteModel;
import com.example.practice.trainingdiary.DataModels.TrainingModel;
import com.example.practice.trainingdiary.DiaryDbHelper;
import com.example.practice.trainingdiary.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Адаптер, соединяющий коллекцию дневниковых записей (mEntryList) со списком карточек на экране операции DayActivity
 */

public class EntryCardAdapter extends ArrayAdapter<DiaryEntryModel>{

    /** Код запроса результата операций, вызываемых из этой операции */
    private static final int REQUEST_CODE_ENTRY = 1;

    private Context mContext;
    private List<DiaryEntryModel> mEntries;

    /**
     * Создаёт экземпляр класса
     * @param context Контекст
     * @param entries Список записей
     */
    public EntryCardAdapter(@NonNull Context context, List<DiaryEntryModel> entries) {
        super(context, 0, entries);
        mContext = context;
        mEntries = entries;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Получаем дневниковую запись, находящуюся на определённой позиции в списке
        final DiaryEntryModel entry = getItem(position);

        // Инициализируем rootView - макет карточки, которая будет отображаться на экране
        // и содержать данные об этой записи.
        View rootView;

        // Если тип записи - тренировка
        if (entry instanceof TrainingModel) {

            // Приводим запись к типу TrainingModel для получения данных о тренировке
            TrainingModel training = (TrainingModel) entry;

            // Заполняем макет card_training_layout
            rootView = LayoutInflater.from(mContext).inflate(R.layout.card_training_layout, parent, false);

            // Получаем из макета первый элемент TextView и устанавливаем ему текст - название тренировки
            TextView textViewName = rootView.findViewById(R.id.text_view_heading);
            textViewName.setText(training.getName());

            // Получаем из макета второй элемент TextView и устанавливаем ему текст - дату тренировки,
            // предварительно приведя её к нужному формату.
            TextView textViewDate = rootView.findViewById(R.id.text_view_subheading);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = simpleDateFormat.format(training.getDate());
            textViewDate.setText(dateString);

            //TODO: Возможно, этот кусок можно будет сделать общим для всех трёх случаев
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
                                    // При выборе пункта меню "Редактировать" открываем операцию TrainingActivity,
                                    // предварительно передав ID записи, по которой щёлкнули
                                    Intent intent = new Intent(mContext, TrainingActivity.class);
                                    intent.putExtra("id", entry.getId());
                                    mContext.startActivity(intent);
                                    return true;
                                case R.id.action_delete:
                                    // При выборе пункта меню "Удалить"
                                    // отображаем диалог подтверждения удаления
                                    showDeleteConfirmationDialog(entry);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popupMenu.show();
                }
            });

            // Устанавливаем обработчик нажатия на сам макет - то есть, на карточку
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // При нажатии на карточку открываем операцию TrainingActivity,
                    // предварительно передав ID записи, по которой щёлкнули
                    Intent intent = new Intent(mContext, TrainingActivity.class);
                    intent.putExtra("id", entry.getId());
                    mContext.startActivity(intent);
                }
            });

        // Если тип записи - измерение
        } else if (entry instanceof MeasureModel) {

            // Приводим запись к типу MeasureModel для получения данных об измерении
            MeasureModel measure = (MeasureModel) entry;

            // Заполняем макет card_measure_layout
            rootView = LayoutInflater.from(mContext).inflate(R.layout.card_measure_layout, parent, false);

            //TODO: Этот фрагмент будет готов, когда данные о тренировках будут храниться в базе
//            // Получаем из макета первый элемент TextView и устанавливаем ему текст - название тренировки
//            TextView textViewName = rootView.findViewById(R.id.text_view_heading);
//            textViewName.setText(training.getName());

            // Получаем из макета второй элемент TextView и устанавливаем ему текст - дату измерения,
            // предварительно приведя её к нужному формату.
            TextView textViewDate = rootView.findViewById(R.id.text_view_subheading);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String dateString = simpleDateFormat.format(measure.getDate());
            textViewDate.setText(dateString);

            //TODO: Возможно, этот кусок можно будет сделать общим для всех трёх случаев
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
                                    return true;
                                case R.id.action_delete:
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popupMenu.show();
                }
            });

            // Устанавливаем обработчик нажатия на сам макет - то есть, на карточку
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // При нажатии на карточку открываем операцию MeasureActivity
//                    Intent intent = new Intent(mContext, TrainingActivity.class);
//                    mContext.startActivity(intent);   //TODO:
                }
            });
        // Если тип записи - заметка
        } else {
            // Приводим запись к типу NoteModel для получения данных об измерении
            NoteModel note = (NoteModel) entry;

            // Заполняем макет card_note_layout
            rootView = LayoutInflater.from(mContext).inflate(R.layout.card_note_layout, parent, false);

            //TODO: Этот фрагмент будет готов, когда данные о тренировках будут храниться в базе
            // Получаем из макета элемент TextView и устанавливаем ему текст - текст заметки
            TextView textViewName = rootView.findViewById(R.id.text_view_body);
            textViewName.setText(note.getText());

            //TODO: Возможно, этот кусок можно будет сделать общим для всех трёх случаев
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
                                    return true;
                                case R.id.action_delete:
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });

                    popupMenu.show();
                }
            });

            // Устанавливаем обработчик нажатия на сам макет - то есть, на карточку
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // При нажатии на карточку открываем операцию MeasureActivity
//                    Intent intent = new Intent(mContext, TrainingActivity.class);
//                    mContext.startActivity(intent);   //TODO:
                }
            });
        }
        return rootView;
    }

    private void showDeleteConfirmationDialog(final DiaryEntryModel entry) {
        // Создаём AlertDialog.Builder и устанавливаем сообщение и обработчики нажатий
        // для положительной и отрицательной кнопок
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Удалить запись?");
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // При нажатии кнопки "Удалить" удаляем запись из списки
                // и оповещаем адаптер о том, что данные изменились,
                // чтобы он обновил список на экране
                mEntries.remove(entry);
                removeTrainingFromDatabase(entry);
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

    private void removeTrainingFromDatabase(DiaryEntryModel entry) {
        TrainingModel training = (TrainingModel) entry;

        //TODO: Прокомментировать
        // TODO: Создать аналогичные методы для двух остальных типов тренировок
        DiaryDbHelper dbHelper = new DiaryDbHelper(mContext);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String idString = String.valueOf(training.getId());
        String whereClause = dbHelper.COLUMN_ID + " = ?";
        String[] whereArgs = new String[] { idString };

        deleteExercises(training);

        //TODO: Отследить количество удаленых строк
        database.delete(dbHelper.TABLE_TRAININGS, whereClause, whereArgs);

        dbHelper.close();


    }

    /**
     * Удаляет из базы все упражнения данной тренировки
     * @param training Тренировка, упражнения для которой необходимо удалить
     */
    private void deleteExercises(TrainingModel training) {
        //При удалении сюда не заходит
        // Сначала удаляем из базы все подходы данной тренировки
        for (int i = 0; i < training.mExerciseList.size(); i++) {
            ExerciseModel exercise = training.mExerciseList.get(i);
            deleteAttempts(exercise);
        }

        // Далее удаляем саму тренировку
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(mContext);

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Выражение WHERE и массив аргументов для подстановки в запрос
        String whereClause = dbHelper.COLUMN_TRAINING_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(training.getId()) };

        //TODO: Отследить количество удаленых строк
        // Удаляем из базы все упражнения, соответствующие условиям
        int rowsDeleted = database.delete(dbHelper.TABLE_EXERCISES, whereClause, whereArgs);

        // После удаления закрываем базу
        dbHelper.close();
    }

    /**
     * Удаляет из базы все подходы для данного упражнения
     * @param exercise Упражнение, подходы для которого необходимо удалить
     */
    private void deleteAttempts(ExerciseModel exercise) {
        // Создаём экземпляр вспомогательного класса DiaryDbHelper для работы с базой данных
        DiaryDbHelper dbHelper = new DiaryDbHelper(mContext);

        // Получаем базу данных для записи
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Выражение WHERE и массив аргументов для подстановки в запрос
        String whereClause = dbHelper.COLUMN_EXERCISE_ID + " = ?";
        String[] whereArgs = new String[] { String.valueOf(exercise.getId()) };

        //TODO: Отследить количество удаленых строк
        // Удаляем из базы все упражнения, соответствующие условиям
        int rowsDeleted = database.delete(dbHelper.TABLE_ATTEMPTS, whereClause, whereArgs);

        // После удаления закрываем базу
        dbHelper.close();
    }
}
