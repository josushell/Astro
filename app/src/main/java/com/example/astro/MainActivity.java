package com.example.astro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    StringBuilder urlBuilder;
    private static final String ASTROURL="http://apis.data.go.kr/B090041/openapi/service/AstroEventInfoService/getAstroEventInfo";
    private static final String APIKEY="";
    private static int originalLenth;
    RecyclerView recyclerView;
    AstroAdapter adapter;
    ArrayList<AstroItem> items=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mainDate=findViewById(R.id.mainDate);
        recyclerView=findViewById(R.id.recycler);
        adapter=new AstroAdapter(items,this);
        // 메인 액티비티에서 커스텀 리스너 객체 생성해서 처리
        adapter.setOnAstroClickListener(new AstroAdapter.onAstroClickListener() {
            @Override
            public void onItemClick(View v) {
                TextView query=v.findViewById(R.id.textTitle);
                Intent intent=new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,query.getText().toString());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        urlBuilder = new StringBuilder(ASTROURL);
        originalLenth=urlBuilder.length();

        Date thisMonth=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM//DD");
        String date=dateFormat.format(thisMonth);
        String[] formattedDate=date.split("/");
        String year=formattedDate[0];
        String month=formattedDate[1];
        String day=formattedDate[2];
        mainDate.setText(String.format("%s년 %s월 🔭",year,month));

        ImageButton pastButton=findViewById(R.id.pastbtn);
        pastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnotherDate();
            }
        });

        // url 설정
        makeUrlBuilder(year,month);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
    }
    public void makeUrlBuilder(String year, String month){
        Log.d("parsinglog","makeurlbuilder 실행됨");
        try{
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+APIKEY);
            urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); // 연도
            urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); // 월
            Log.d("parsinglog","url: "+urlBuilder.toString());

        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public void getAnotherDate(){
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String year=Integer.toString(i);
                String month=Integer.toString(i1+1);

                TextView mainDate=findViewById(R.id.mainDate);
                mainDate.setText(String.format("%s년 %s월 🔭",year,month));

                Log.d("parsinglog",year+" / "+month);
                urlBuilder.setLength(0);
                urlBuilder.append(ASTROURL);
                makeUrlBuilder(year,month);
                xmlparsing();
                adapter.notifyDataSetChanged();
            }
        },year,month,day).show();
    }


    @Override
    protected void onStart() {
        xmlparsing();
        super.onStart();
    }

    public void xmlparsing(){
        Log.d("parsinglog","xml parsing");
        try{
            URL url=new URL(urlBuilder.toString());

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
            cancel(true);
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
                items.clear();
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
                                    item.setAstroEvent(parser.getText());
                                    if(parser.getText()!=null)
                                        System.out.println(parser.getText());
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
                                    item.setAstroTitle(parser.getText());
                                }
                            }
                            else if(tagname.equals("locdate")){
                                parser.next();
                                if(item!=null){
                                    item.setLocdate(parser.getText());
                                    if(parser.getText()!=null)
                                        System.out.println(parser.getText());
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