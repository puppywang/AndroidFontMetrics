package net.studymongolian.fontmetrics;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.namee.permissiongen.PermissionGen;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_OPEN_FILE = 1;

    private static final int RESULT_PERMISSION = 2;

    FontMetricsView myFontMetricsView; // custom view
    EditText mTextStringEditText;
    EditText mFontSizeEditText;
    CheckBox cbTop;
    CheckBox cbAscent;
    CheckBox cbBaseline;
    CheckBox cbDescent;
    CheckBox cbBottom;
    CheckBox cbBounds;
    CheckBox cbMeasuredWidth;

    TextView tvTop;
    TextView tvAscent;
    TextView tvBaseline;
    TextView tvDescent;
    TextView tvBottom;
    TextView tvBounds;
    TextView tvMeasuredWidth;
    TextView tvLeading;

    private void requestPermission() {
        //处理需要动态申请的权限
        PermissionGen.with(MainActivity.this)
                .addRequestCode(RESULT_PERMISSION)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .request();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myFontMetricsView = (FontMetricsView) findViewById(R.id.viewWindow);
        mTextStringEditText = (EditText) findViewById(R.id.etTextString);
        mFontSizeEditText = (EditText) findViewById(R.id.etFontSize);

        mTextStringEditText.setText("My text line");
        mFontSizeEditText.setText("200");


        findViewById(R.id.updateButton).setOnClickListener(this);
        cbTop = (CheckBox) findViewById(R.id.cbTop);
        cbAscent = (CheckBox) findViewById(R.id.cbAscent);
        cbBaseline = (CheckBox) findViewById(R.id.cbBaseline);
        cbDescent = (CheckBox) findViewById(R.id.cbDescent);
        cbBottom = (CheckBox) findViewById(R.id.cbBottom);
        cbBounds = (CheckBox) findViewById(R.id.cbTextBounds);
        cbMeasuredWidth = (CheckBox) findViewById(R.id.cbWidth);

        cbTop.setOnClickListener(this);
        cbAscent.setOnClickListener(this);
        cbBaseline.setOnClickListener(this);
        cbDescent.setOnClickListener(this);
        cbBottom.setOnClickListener(this);
        cbBounds.setOnClickListener(this);
        cbMeasuredWidth.setOnClickListener(this);

        tvTop = (TextView) findViewById(R.id.tvTop);
        tvAscent = (TextView) findViewById(R.id.tvAscent);
        tvBaseline = (TextView) findViewById(R.id.tvBaseline);
        tvDescent = (TextView) findViewById(R.id.tvDescent);
        tvBottom = (TextView) findViewById(R.id.tvBottom);
        tvBounds = (TextView) findViewById(R.id.tvTextBounds);
        tvMeasuredWidth = (TextView) findViewById(R.id.tvWidth);
        tvLeading = (TextView) findViewById(R.id.tvLeadingValue);

        findViewById(R.id.openFont).setOnClickListener(this);

        updateTextViews();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_OPEN_FILE) {
                Uri uri = data.getData();
                try {
                    if (uri != null) {
                        myFontMetricsView.setFont(getFileName(uri));
                    }
                } catch (RuntimeException e) {
                    Toast.makeText(this, "Failed to open font, err = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.updateButton:
                myFontMetricsView.setText(mTextStringEditText.getText().toString());
                int fontSize;
                try {
                    fontSize = Integer.valueOf(mFontSizeEditText.getText().toString());
                } catch (NumberFormatException e) {
                    fontSize = FontMetricsView.DEFAULT_FONT_SIZE_PX;
                }
                myFontMetricsView.setTextSizeInPixels(fontSize);
                updateTextViews();
                hideKeyboard(getCurrentFocus());
                break;
            case R.id.openFont:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //系统调用Action属性
                intent.setType("*/*");
                //设置文件类型
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // 添加Category属性
                startActivityForResult(intent, RESULT_OPEN_FILE);
                break;
            case R.id.cbTop:
                myFontMetricsView.setTopVisible(cbTop.isChecked());
                break;
            case R.id.cbAscent:
                myFontMetricsView.setAscentVisible(cbAscent.isChecked());
                break;
            case R.id.cbBaseline:
                myFontMetricsView.setBaselineVisible(cbBaseline.isChecked());
                break;
            case R.id.cbDescent:
                myFontMetricsView.setDescentVisible(cbDescent.isChecked());
                break;
            case R.id.cbBottom:
                myFontMetricsView.setBottomVisible(cbBottom.isChecked());
                break;
            case R.id.cbTextBounds:
                myFontMetricsView.setBoundsVisible(cbBounds.isChecked());
                break;
            case R.id.cbWidth:
                myFontMetricsView.setWidthVisible(cbMeasuredWidth.isChecked());
                break;
        }


    }

    public void updateTextViews() {
        tvTop.setText(String.valueOf(myFontMetricsView.getFontMetrics().top));
        tvAscent.setText(String.valueOf(myFontMetricsView.getFontMetrics().ascent));
        tvBaseline.setText(String.valueOf(0f));
        tvDescent.setText(String.valueOf(myFontMetricsView.getFontMetrics().descent));
        tvBottom.setText(String.valueOf(myFontMetricsView.getFontMetrics().bottom));
        tvBounds.setText("w = " + String.valueOf(myFontMetricsView.getTextBounds().width() +
                "     h = " + String.valueOf(myFontMetricsView.getTextBounds().height())));
        tvMeasuredWidth.setText(String.valueOf(myFontMetricsView.getMeasuredTextWidth()));
        tvLeading.setText(String.valueOf(myFontMetricsView.getFontMetrics().leading));
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
