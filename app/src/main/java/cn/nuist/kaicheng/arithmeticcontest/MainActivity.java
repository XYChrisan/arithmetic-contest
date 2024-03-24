package cn.nuist.kaicheng.arithmeticcontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    int num1, num2, result;
    int rightNum = 0, wrongNum = 0;
    SoundPool sp;
    HashMap<Integer, Integer> hm;
    int currStreamId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSoundPool();
        TextView txvWelcomeName = findViewById(R.id.txv_welcome_name);

        if (getIntent().getStringExtra("IF_ALREADY_EXISTS").equals("true")) {
            txvWelcomeName.setText(getResources().getString(R.string.main_welcome_back) + getIntent().getStringExtra("CHALLENGER_NAME") + "!");
        } else {
            txvWelcomeName.setText(getResources().getString(R.string.main_welcome) + getIntent().getStringExtra("CHALLENGER_NAME") + "!");
        }

        Button btnCheck = findViewById(R.id.btn_check);
        btnCheck.setOnClickListener(view -> checkResult());

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(view -> generateNum());

        FloatingActionButton fabGameRecord = findViewById(R.id.fab_game_record);
        fabGameRecord.setOnClickListener(view -> toGameRecord());

        generateNum();

        btnNext.setEnabled(false);

        if (DatabaseService.checkWhetherToday()) {
            rightNum = DatabaseService.getRightNum(getIntent().getStringExtra("CHALLENGER_NAME"));
            wrongNum = DatabaseService.getWrongNum(getIntent().getStringExtra("CHALLENGER_NAME"));
        } else {
            rightNum = 0;
            wrongNum = 0;
            DatabaseService.initUserRecord(getIntent().getStringExtra("CHALLENGER_NAME"));
            DatabaseService.initUserStatistic(getIntent().getStringExtra("CHALLENGER_NAME"));
        }
    }

    private void initSoundPool() {
        sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        hm = new HashMap<Integer, Integer>();
        hm.put(1, sp.load(this, R.raw.right, 1));
        hm.put(2, sp.load(this, R.raw.wrong, 1));
    }

    private void playSound(int sound, int loop) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        currStreamId = sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
    }

    private void checkResult() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String formatDate = sdf.format(date);
        Button btnCheck = findViewById(R.id.btn_check);
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(true);
        TextView txvInfo = findViewById(R.id.txv_info);
        EditText edtResult = findViewById(R.id.edt_result);
        String input = edtResult.getText().toString().trim();
        if (String.valueOf(result).trim().equals(input)) {
            //答案正确，给出正确提示，播放正确音效，更新正确数和正确率
            txvInfo.setText(getResources().getString(R.string.main_right_info));
            playSound(1, 0);
            rightNum++;
            DatabaseService.updateUserRecord(getIntent().getStringExtra("CHALLENGER_NAME"), rightNum, wrongNum, formatDate);
            DatabaseService.updateUserStatistics(getIntent().getStringExtra("CHALLENGER_NAME"), rightNum, wrongNum, formatDate);
        } else {
            //答案错误，给出错误提示，播放错误音效，更新错误数和正确率
            txvInfo.setText(getResources().getString(R.string.main_wrong_info_1)
                    + " " + result
                    + getResources().getString(R.string.main_wrong_info_2));
            playSound(2, 0);
            wrongNum++;
            DatabaseService.updateUserRecord(getIntent().getStringExtra("CHALLENGER_NAME"), rightNum, wrongNum, formatDate);
            DatabaseService.updateUserStatistics(getIntent().getStringExtra("CHALLENGER_NAME"), rightNum, wrongNum, formatDate);
        }
        btnCheck.setEnabled(false);
    }

    private void toGameRecord() {
        Intent intent = new Intent(this, GameRecordActivity.class);
        intent.putExtra("CHALLENGER_NAME", getIntent().getStringExtra("CHALLENGER_NAME"));
        startActivity(intent);
    }

    private void generateNum() {
        Button check = findViewById(R.id.btn_check);
        //设置检查按钮为可用
        check.setEnabled(true);
        TextView info = findViewById(R.id.txv_info);
        info.setText("");
        Button next = findViewById(R.id.btn_next);
        //生成随机数
        num1 = (int) ((Math.random() * (10 - 0)) + 0);
        num2 = (int) ((Math.random() * (10 - 0)) + 0);
        //生成随机运算符
        int operator = (int) ((Math.random() * (2 - 0)) + 0);
        TextView txt_num1 = findViewById(R.id.txv_num1);
        TextView txt_num2 = findViewById(R.id.txv_num2);
        TextView txt_operator = findViewById(R.id.txv_operator);
        txt_num1.setText(String.valueOf(num1));
        txt_num2.setText(String.valueOf(num2));
        if (operator == 0) {
            txt_operator.setText("+");
            result = num1 + num2;
        } else {
            txt_operator.setText("-");
            result = num1 - num2;
        }
        //设置下一题按钮为不可用
        next.setEnabled(false);
        EditText resultInput = findViewById(R.id.edt_result);
        resultInput.setText("");
    }
}