package com.cyanbirds.wwjy.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.config.ValueKey;
import com.cyanbirds.wwjy.listener.ModifyUserInfoListener;
import com.cyanbirds.wwjy.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ModifySignatureActivity extends BaseActivity implements TextWatcher {
    private EditText mText;
    private static final int MAX_SIZE = 150;//最多允许输入20个字符
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_signature);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
        setupData();
    }

    private void setupView(){
        mText = findViewById(R.id.signature);
    }

    private void setupEvent(){
        mText.addTextChangedListener(this);
    }

    private void setupData(){
        String signature = getIntent().getStringExtra(ValueKey.SIGNATURE);
        if(!TextUtils.isEmpty(signature)){
            mText.setText(signature);
            mText.setSelection(mText.getText().toString().length());//设置光标在文字最后
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!TextUtils.isEmpty(mText.getText().toString().trim())) {
            ModifyUserInfoListener.getInstance().notifyModifyUserSignatureListener(mText.getText().toString().trim());
            finish();
        } else {
            ToastUtil.showMessage("还是说点什么吧");
        }
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //最多允许输入20个字符
        if(s.length() > MAX_SIZE){
            ToastUtil.showMessage("输入已经超过最大限制");
            mText.setText(s.subSequence(0, MAX_SIZE));
            mText.setSelection(MAX_SIZE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getName());
        MobclickAgent.onPause(this);
    }

}
