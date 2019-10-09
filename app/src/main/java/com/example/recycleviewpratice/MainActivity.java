package com.example.recycleviewpratice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recycler_view;
    private MyAdapter adapter;
    private Button btnremove;
    private ArrayList<String> mData = new ArrayList<>();
    private ImageView photoview;
    private String data;
    private JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 準備資料，塞50個項目到ArrayList裡


        try {
            data = new GoodTask().execute("http://192.168.151.10:3000/home/=getphoto").get();
            jsonArray = new JSONArray(data);
            Log.d("item",jsonArray.getString(3));

            for(int i = 0; i < 50; i++) {
                mData.add(jsonArray.getString(i));

            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // 連結元件
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        // 設置RecyclerView為列表型態
        //recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));

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
        public String element;
        MyAdapter(List<String> data) {

            mData = data;
        }

        // 建立ViewHolder
        class ViewHolder extends RecyclerView.ViewHolder{
            // 宣告元件
            private TextView txtItem;
            private ImageView photoview;

            ViewHolder(View itemView) {
                super(itemView);
                txtItem = (TextView) itemView.findViewById(R.id.txtItem);
                btnremove= (Button) itemView.findViewById(R.id.btnRemove);
                photoview =(ImageView)itemView.findViewById(R.id.photoview);
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

            public ImageView getImage(){ return this.photoview;}
            //ref:https://www.learningsomethingnew.com/how-to-use-a-recycler-view-to-show-images-from-storage
            //get the idea from this website

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

            //holder.photoview.setImageResource(R.mipmap.ic_launcher_round);
            Log.d("image_path",holder.getImage().toString());
            //new DownloadImageTask(holder.getImage()).execute("http://192.168.151.10:3000/home/coder01/work/src/test/lux.png");
            //new DownloadImageTask(holder.getImage()).execute("http://192.168.151.10:3000/home/coder01/.cache/mozilla/firefox/xe23wves.default/thumbnails/353eb76660e389cf29df023936ddfbf4.png");
            new DownloadImageTask(holder.getImage()).execute(mData.get(position));
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



    //Ref :http://codenamker.pixnet.net/blog/post/161818334-%E3%80%90android%E3%80%91%E7%95%B0%E6%AD%A5%E5%9F%B7%E8%A1%8C%E7%B7%92asynctask-android-studio
    class GoodTask extends AsyncTask<String, Integer, String> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        private static final int TIME_OUT = 1000;

        String jsonString1 = "";

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... countTo) {
            // TODO Auto-generated method stub
            // 再背景中處理的耗時工作
            try {
                HttpURLConnection conn = null;
                URL url = new URL(countTo[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.connect();
                // 讀取資料
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                jsonString1 = reader.readLine();
                reader.close();

                if (Thread.interrupted()) {
                    throw new InterruptedException();

                }
                if (jsonString1.equals("")) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "網路中斷" + e;
            }
            //Log.e("data",jsonString1);
            return jsonString1;
        }
        public void onPostExecute(String result )
        { super.onPreExecute();
            // 背景工作處理完"後"需作的事
            //txv.setText("JSON:\r\n"+ result);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            // 背景工作處理"中"更新的事

        }
    }

    @SuppressLint("NewApi")
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream(); // 從網址上下載
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {





            bmImage.setImageBitmap(result); // 下載完成後載入結果
        }
    }


}