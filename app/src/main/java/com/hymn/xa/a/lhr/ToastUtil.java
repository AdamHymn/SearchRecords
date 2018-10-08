package com.hymn.xa.a.lhr;

import android.content.Context;
import android.widget.Toast;

/**
 * @FileName ToastUtil
 * @Author 【向下扎根，向上结果】
 * @Email ayang139@qq.com
 * @Date 2018/10/8
 * @Description
 */

public class ToastUtil {
    public static Toast toast;

    public static void showToast(Context context,String msg){
        if(toast == null){
            toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
