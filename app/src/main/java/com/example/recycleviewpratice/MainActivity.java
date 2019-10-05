package com.example.recycleviewpratice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recycler_view;
    private MyAdapter adapter;
    private Button btnremove;
    private ArrayList<String> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 準備資料，塞50個項目到ArrayList裡
        for(int i = 0; i < 50; i++) {
            mData.add("項目"+i);

        }


        // 連結元件
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        // 設置RecyclerView為列表型態
        //recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setLayoutManager(new GridLayoutManager(this, 4));

        // 設置格線
        //recycler_view.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycler_view.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));

        // 將資料交給adapter
        adapter = new MyAdapter(mData);
        // 設置adapter給recycler_view
        recycler_view.setAdapter(adapter);
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<String> mData;

        MyAdapter(List<String> data) {
            mData = data;
        }

        // 建立ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder{
            // 宣告元件
            private TextView txtItem;

            ViewHolder(View itemView) {
                super(itemView);
                txtItem = (TextView) itemView.findViewById(R.id.txtItem);
                btnremove= (Button) itemView.findViewById(R.id.btnRemove);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        Toast.makeText(view.getContext(),
                                "click " +getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        // 新增一個項目
                        adapter.addItem("New Item");
                        return false;
                    }
                });


                btnremove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 移除項目，getAdapterPosition為點擊的項目位置
                        removeItem(getAdapterPosition());
                    }
                });



            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // 連結項目布局檔list_item
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // 設置txtItem要顯示的內容
            holder.txtItem.setText(mData.get(position));
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }


        // 未更動的程式省略..

        // 新增項目
        public void addItem(String text) {
            // 為了示範效果，固定新增在位置3。若要新增在最前面就把3改成0
            mData.add(3,text);
            Log.d("insert","insert");
            notifyItemInserted(3);
        }

        // 刪除項目
        public void removeItem(int position){
            mData.remove(position);
            notifyItemRemoved(position);
        }
    }
}