package cn.nuist.kaicheng.arithmeticcontest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnStartChallenge = findViewById(R.id.btn_start_challenge);
        btnStartChallenge.setOnClickListener(view -> toMain());

        TextView errorMsg = findViewById(R.id.txv_error_msg);
        errorMsg.setVisibility(View.INVISIBLE);

        DatabaseService.initDB(this);

    }

    //前往游戏界面
    private void toMain() {
        EditText edt_name = findViewById(R.id.edt_name);//获取用户名输入框
        String challengerName = edt_name.getText().toString();//用户名字符串
        TextView errorMsg = findViewById(R.id.txv_error_msg);//获取提示信息框

        if (challengerName.equals("")) {
            //若用户名为空，设置错误信息
            errorMsg.setText(getResources().getString(R.string.welcome_error_msg_invalid_user_name));
            errorMsg.setVisibility(View.VISIBLE);
            edt_name.setText("");
        } else {
            //若不为空，隐藏错误信息，并开启MainActivity
            errorMsg.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("CHALLENGER_NAME", challengerName);
            if (DatabaseService.addUser(challengerName)) {
                intent.putExtra("IF_ALREADY_EXISTS", "true");
            } else {
                intent.putExtra("IF_ALREADY_EXISTS", "false");
            }
            if (!DatabaseService.checkWhetherToday()) {
                //如果当天是第一次开启游戏，则对用户数据进行初始化
                DatabaseService.initUserRecord(challengerName);
                DatabaseService.initUserStatistic(challengerName);
            }
            startActivity(intent);
            finish();
        }
    }
}