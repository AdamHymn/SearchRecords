package com.hymn.xa.a.lhr;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @FileName MainActivity
 * @Author 【向下扎根，向上结果】
 * @Email ayang139@qq.com
 * @Date 2018/10/8
 * @Description
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    /**
     * 历史数据列表的适配器
     */
    PopAdapter popAdapter = null;
    /**
     * 分割后的本地搜索历史列表
     */
    LinkedList<String> list = new LinkedList<>();
    /**
     * 本地搜索历史元数据，即sharedPERFERENCE中存储的带#####数据，需要先分割存放到list中使用。
     */
    String historyRaw = "";
    /**
     * 用于拼接list中的数据和#####。用来存储与本地
     */
    StringBuffer sb = new StringBuffer();
    /**
     * 匹配上色历史数据的容器
     */
    Map<Integer, String> map = new HashMap<>();
    /**
     * 匹配上色历史数据的容器
     */
    String temp = "";
    /**
     * 当前展示的历史数据列表
     */
    LinkedList<String> tempList = new LinkedList<>();
    /**
     * 当前搜索框中的关键字
     */
    private String tempEtSousou = "";
    /**
     * 本地存储的键名
     */
    private static String KEY_SEARCH = "search";

    private FrameLayout flSearch;
    private EditText etSousou;
    private ImageView ivSearch;
    private LinearLayout llPop;
    private ListView lvPop;
    private TextView tvClear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        initView();
    }

    private void initView() {
        flSearch = (FrameLayout) findViewById(R.id.fl_search);
        etSousou = (EditText) findViewById(R.id.et_sousou);
        ivSearch = (ImageView) findViewById(R.id.iv_search);
        llPop = (LinearLayout) findViewById(R.id.ll_pop);
        lvPop = (ListView) findViewById(R.id.lv_pop);
        tvClear = (TextView) findViewById(R.id.tv_clear);

        //获取本地搜索数据
        getHistoryData(list);
        ivSearch.setOnClickListener(this);
        etSousou.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (list.size() > 0) {
                    tempEtSousou = s.toString();
                    etSousou.post(updateRunnable);
                } else {
                    hideSousou();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 搜索框关键字变动匹配的子线程
     */
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            tempList.clear();
            tempList.addAll(list);
            if (!TextUtils.isEmpty(tempEtSousou)) {
                //搜索框的关键字
                char[] etTemp = tempEtSousou.toCharArray();
                //匹配当前本地历史列表
                for (int i = 0; i < tempList.size(); i++) {
                    char[] local = tempList.get(i).toCharArray();
                    //逐个匹配搜索框关键字和本地历史数据
                    for (int j = 0; j < etTemp.length; j++) {
                        for (int k = 0; k < local.length; k++) {
                            //搜索框的字符==当前的字符，则涂上橘黄色
                            if (etTemp[j] == local[k]) {
                                map.put(k, "<font color=\"#F9B567\">" + local[k] + "</font>");
                            } else {
                                //匹配失败时，如果关键字的字符已经匹配过当前字符，则跳过
                                if (map.get(k) != null && map.get(k).length() > 1) {
                                    continue;
                                }
                                //从未匹配过的，依然按顺序收藏
                                map.put(k, local[k] + "");
                            }
                        }

                    }
                    //将MAP中的字符串，按顺序取出，拼接到temp字符串变量
                    if (map.size() > 0) {
                        for (int j = 0; j < map.size(); j++) {
                            temp += map.get(j);
                        }
                    }
                    //移出列表中当前位置的历史数据
                    tempList.remove(i);
                    //替换为匹配上色后的历史数据
                    tempList.add(i, temp);
                    //清空容器变量
                    temp = "";
                    map.clear();
                }
            }
            if (popAdapter != null) {
                showSousou();
                popAdapter.notifyDataSetChanged();
            } else {
                initPopAdapter();
            }
        }
    };


    /**
     * 获取本地历史数据，存储到list和raw变量里
     *
     * @param list
     */
    private void getHistoryData(LinkedList<String> list) {
        list.clear();
        //搜索历史是按#####分割拼接成一个字符串，存入本地键值是search的Sharedpreferences;
        historyRaw = FileSharedUtil.getString(this, KEY_SEARCH);
        if (!TextUtils.isEmpty(historyRaw)) {
            if (historyRaw.contains("#####")) {
                String[] items = historyRaw.split("#####");
                for (int i = 0; i < items.length; i++) {
                    list.addLast(items[i]);
                }
            } else {
                list.add(historyRaw);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tempEtSousou = etSousou.getText().toString();
        if (list.size() > 0 && !TextUtils.isEmpty(tempEtSousou)) {
            etSousou.post(updateRunnable);
        } else {
            hideSousou();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //开始搜索
            case R.id.iv_search:
                if (TextUtils.isEmpty(etSousou.getText().toString())) {
                    ToastUtil.showToast(this, "请输入搜索关键字");
                    return;
                }
                //存储当前关键字到历史记录
                list.addFirst(etSousou.getText().toString());
                for (int i = 0; i < list.size(); i++) {
                    //去除重复
                    if (i == 0 || !list.get(0).equals(list.get(i))) {
                        sb.append(list.get(i));
                    } else {
                        break;
                    }
                    if (i == 4 || i == (list.size() - 1)) {
                        break;
                    }
                    sb.append("#####");
                }
                FileSharedUtil.putString(this, KEY_SEARCH, sb.toString());
                historyRaw = sb.toString();
                //清空当前容器
                sb.delete(0, sb.length());
                //刷新数据列表
                getHistoryData(list);
                //传递搜索关键字并跳转页面
                etSousou.setText("");
                break;
            default:
                break;

        }
    }

    /**
     * 弹窗历史记录
     */
    private void initPopAdapter() {
        showSousou();
        popAdapter = new PopAdapter(this, tempList);
        lvPop.setAdapter(popAdapter);
        llPop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSousou();
                return false;
            }
        });
        lvPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etSousou.setText(list.get(position));
                tempEtSousou = etSousou.getText().toString();
                //传递搜索关键字并跳转页面
            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                tempList.clear();
                FileSharedUtil.putString(MainActivity.this, KEY_SEARCH, "");
                popAdapter.notifyDataSetChanged();
                hideSousou();
            }
        });


    }

    /**
     * 显示本地历史数据列表，并调整输入框样式
     */
    private void showSousou() {
        if (llPop.getVisibility() == View.GONE) {
            flSearch.setBackgroundColor(Color.WHITE);
            etSousou.setBackground(getResources().getDrawable(R.mipmap.input_grey));
            ivSearch.setImageDrawable(getResources().getDrawable(R.mipmap.icon_search));
            etSousou.setHintTextColor(getResources().getColor(R.color.grey));
            etSousou.setFocusable(true);
            llPop.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 隐藏本地历史数据列表，并调整输入框样式
     */
    public void hideSousou() {
        if (llPop.getVisibility() == View.VISIBLE) {
            flSearch.setBackgroundColor(Color.TRANSPARENT);
            etSousou.setBackground(getResources().getDrawable(R.mipmap.input));
            ivSearch.setImageDrawable(getResources().getDrawable(R.mipmap.icon_sousuo));
            etSousou.setHintTextColor(getResources().getColor(R.color.white));
            llPop.setVisibility(View.GONE);
        }
    }

    /**
     * 搜索历史的Adapter
     */
    private class PopAdapter extends CommonBaseAdapter<String> {
        public PopAdapter(Context mContext, List<String> mDatas) {
            super(mContext, mDatas, R.layout.v_search_pop_history_item);
        }

        @Override
        public void convert(CommonViewHolder holder, int position, String s) {
            TextView textView = holder.getView(R.id.tv_pop);
            textView.setText(Html.fromHtml(s));
        }
    }

}
