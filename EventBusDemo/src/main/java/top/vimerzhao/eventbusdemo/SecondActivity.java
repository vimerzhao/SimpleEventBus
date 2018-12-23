package top.vimerzhao.eventbusdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import top.vimerzhao.simpleevnetbus.EventBus;

/**
 * Created by vimerzhao on 18-12-23
 */
public class SecondActivity extends AppCompatActivity {
    TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.tv_demo);
        mTextView.setText("SecondActivity,点击返回");
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.mContent = "从SecondActivity返回";
                EventBus.getDefault().post(message);
                finish();
            }
        });
    }

}
