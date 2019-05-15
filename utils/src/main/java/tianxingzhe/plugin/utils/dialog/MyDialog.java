package tianxingzhe.plugin.utils.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import tianxingzhe.plugin.utils.R;


public class MyDialog extends Dialog {

    private String titleMessage;
    private String contontMessage,mRightStr,mLeftStr;
    private TextView mLeft_button , mRight_button;

    public MyDialog(Context context, String title, String message) {
        super(context, R.style.dialog_custom);
        this.titleMessage = title;
        this.contontMessage = message;
    }
    public MyDialog(Context context, String titleMessage, String contontMessage, OnConfirmClickListener listen) {
       this(context,titleMessage,contontMessage);
        this.oncConfirmListener = listen ;
    }
    public MyDialog(Context context, String contontMessage, OnConfirmClickListener listen) {
       this(context,"",contontMessage,listen);
        this.oncConfirmListener = listen ;
    }
    public MyDialog(Context context, String titleMessage, String contontMessage, OnConfirmClickListener listen, String rStr, String lStr) {
       this(context,titleMessage,contontMessage,listen);
        this.mRightStr = rStr ;
        this.mLeftStr = lStr ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        setContentView(R.layout.make_sure_layout);
//        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        initView();
    }

    private void initView() {
        TextView diolog_title = (TextView) findViewById(R.id.tv_title);
        diolog_title.setText(titleMessage);
        final TextView diolog_contont = (TextView) findViewById(R.id.tv_content);
        diolog_contont.setText(contontMessage);
        mRight_button = (TextView) findViewById(R.id.dialog_right_button);
        mRight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

            }
        });
        mLeft_button = (TextView) findViewById(R.id.dialog_left_button);
        mLeft_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != oncConfirmListener) {
                    oncConfirmListener.confirm();
                    dismiss();
                }
            }
        });
    }


    public interface OnConfirmClickListener {
        void confirm();
    }

    public OnConfirmClickListener oncConfirmListener;

}
