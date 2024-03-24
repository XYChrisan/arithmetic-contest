package cn.nuist.kaicheng.arithmeticcontest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GameRecordActivity extends AppCompatActivity {

    ListView lvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_record);
        TextView txvGreet = findViewById(R.id.txv_greet);
        txvGreet.setText(getResources().getString(R.string.game_record_greet_1)
                + getIntent().getStringExtra("CHALLENGER_NAME")
                + getResources().getString(R.string.game_record_greet_2)
        );
        lvRecord = findViewById(R.id.lv_record);
        viewAll();

        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //打印当天正确数、错误数、正确率
                String date = (String) parent.getItemAtPosition(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameRecordActivity.this);
                dialog.setTitle(getResources().getString(R.string.game_record_dialog_title));
                int rightNum = DatabaseService.getRightNum(getIntent().getStringExtra("CHALLENGER_NAME"), date);
                int wrongNum = DatabaseService.getWrongNum(getIntent().getStringExtra("CHALLENGER_NAME"), date);
                double accuracy = DatabaseService.getAccuracy(getIntent().getStringExtra("CHALLENGER_NAME"), date);
                String s = getResources().getString(R.string.game_record_dialog_right)
                        + rightNum
                        + "\n"
                        + getResources().getString(R.string.game_record_dialog_wrong)
                        + wrongNum
                        + "\n"
                        + getResources().getString(R.string.game_record_dialog_accuracy)
                        + accuracy;
                dialog.setMessage(s);
                dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewAll();
                    }
                });
                dialog.create();
                dialog.show();
            }
        });
    }


    private void viewAll() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this
                , android.R.layout.simple_list_item_1
                , DatabaseService.getAllDate(getIntent().getStringExtra("CHALLENGER_NAME")));
        lvRecord.setAdapter(adapter);
    }
}