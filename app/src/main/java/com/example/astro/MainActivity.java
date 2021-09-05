package com.example.astro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AstroAdapter adapter;
    private static final String rssUrl = "http://apis.data.go.kr/B090041/openapi/service/AstroEventInfoService/getAstroEventInfo?serviceKey=Syu%2BtqGals35ZDKELPRhuQ0knXp0t%2FUZlA%2FBUGs3GvEfWohCs%2F0pzrHpjN%2B5WOS41N7bbVuPb7o6Xw1wsl%2B%2FPw%3D%3D&solYear=2017&solMonth=09";
    ArrayList<AstroItem> items=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler);
        adapter=new AstroAdapter(items,this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        //xmlparsing();
        //adapter.notifyDataSetChanged();


        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xmlparsing();
            }
        });


    }
    public void xmlparsing(){
        try{
            URL url=new URL(rssUrl);

            XMLTask task=new XMLTask();
            task.execute(url);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
    }
    class XMLTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        // 파라미터는 doInBackground 의 return 값임
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL myurl=urls[0];
            try{
                InputStream is= myurl.openStream();
                XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
                XmlPullParser parser=factory.newPullParser();

                parser.setInput(is,"UTF8");
                int eventType=parser.getEventType();

                AstroItem item=null;
                String tagname=null;

                while(eventType!=XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            tagname=parser.getName();

                            if(tagname.equals("item")){
                                item=new AstroItem();
                            }
                            else if(tagname.equals("astroEvent")){
                                parser.next();
                                if(item!=null){
                                    item.setAstroTitle(parser.getText());
                                }
                            }
                            else if(tagname.equals("astroTime")){
                                parser.next();
                                if(item!=null){
                                    item.setAstroTime(parser.getText());
                                }
                            }
                            else if(tagname.equals("astroTitle")){
                                parser.next();
                                if(item!=null){
                                    item.setAstroTime(parser.getText());
                                }
                            }
                            else if(tagname.equals("locdate")){
                                parser.next();
                                if(item!=null){
                                    item.setAstroTime(parser.getText());
                                }
                            }
                            else if(tagname.equals("seq")){
                                parser.next();
                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:
                            tagname=parser.getName();
                            if(tagname.equals("item")){
                                items.add(item);
                                item=null;
                                publishProgress();
                            }
                            break;
                    }
                    eventType=parser.next();
                }
            }
            catch (XmlPullParserException e){
                e.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            return "parsing complete";
        }
    }
}