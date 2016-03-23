package com.rotatingdisk;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rotatingdisk.views.RotatingDiskView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements View.OnClickListener {
    @Bind(R.id.tv_radom)
    Button tvRadom;
    @Bind(R.id.tv_defined)
    Button tvDefined;
    @Bind(R.id.edt_index)
    EditText edtIndex;
    @Bind(R.id.iv_go)
    ImageView ivGo;
    @Bind(R.id.rotatdisk)
    RotatingDiskView rotatdisk;
    @Bind(R.id.tv_random_no)
    TextView tvRandomNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivGo.setOnClickListener(this);
        tvRadom.setOnClickListener(this);
        tvDefined.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_go:
                rotatdisk.startWithStopDefined(0);
                break;
            case R.id.tv_radom:
                int randomNum = (int) Math.floor(Math.random() * 9);
                tvRandomNo.setText(randomNum+"");
                rotatdisk.startWithStopDefined(randomNum);
                break;
            case R.id.tv_defined:
                int writeNum = 8;
                if (TextUtils.isEmpty(edtIndex.getText().toString())){
                    Toast.makeText(this,"请输入停止位置",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    writeNum = Integer.parseInt(edtIndex.getText().toString());
                    if (writeNum>8||writeNum<0){
                        Toast.makeText(this,"请输入0~8的整数",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                rotatdisk.startRotation(8);
                rotatdisk.setStopIndex(writeNum);
                break;

        }
    }
}
