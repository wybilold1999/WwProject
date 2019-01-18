package com.cyanbirds.wwjy.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cyanbirds.wwjy.R;
import com.cyanbirds.wwjy.activity.base.BaseActivity;
import com.cyanbirds.wwjy.manager.AppManager;
import com.cyanbirds.wwjy.utils.ProgressDialogUtils;
import com.cyanbirds.wwjy.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by wangyb on 2017/7/21.
 * 描述：
 */

public class FeedBackActivity extends BaseActivity {

    @BindView(R.id.content)
    EditText mContent;
    @BindView(R.id.text_num)
    TextView mTextNum;
    @BindView(R.id.submit)
    FancyButton mSubmit;
    @BindView(R.id.offical_qq)
    TextView mOfficalQQ;

    private int limit = 100;//最多允许输入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        Toolbar toolbar = getActionBarToolbar();
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_up);
        }
        setupView();
        setupEvent();
    }

    private void setupView() {
        //设置EditText的显示方式为多行文本输入
        mContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        //改变默认的单行模式
        mContent.setSingleLine(false);
        //水平滚动设置为False
        mContent.setHorizontallyScrolling(false);
        mContent.setFocusable(true);
        mTextNum.setText(String.valueOf(limit));
        if (AppManager.getClientUser().isShowDownloadVip) {
            mOfficalQQ.setVisibility(View.VISIBLE);
        } else {
            mOfficalQQ.setVisibility(View.GONE);
        }
    }

    private void setupEvent() {
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int value = limit - s.toString().length();
                mTextNum.setText(String.valueOf(value));
            }
        });

    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        if (TextUtils.isEmpty(mContent.getText().toString())) {
            ToastUtil.showMessage(R.string.please_input_suggestion);
        } else {
            ProgressDialogUtils.getInstance(this).show(R.string.wait);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ProgressDialogUtils.getInstance(FeedBackActivity.this).dismiss();
                    ToastUtil.showMessage(R.string.submit_success);
                    finish();
                }
            }, 800);
        }
    }
}
