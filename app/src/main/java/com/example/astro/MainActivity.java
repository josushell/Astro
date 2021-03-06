package com.example.astro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private StringBuilder urlBuilder;
    private static final String ASTROURL="http://apis.data.go.kr/B090041/openapi/service/AstroEventInfoService/getAstroEventInfo";
    private static final String APIKEY="";
    private RecyclerView recyclerView;
    private AstroAdapter adapter;
    private ArrayList<AstroItem> items=new ArrayList<>();
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // textview
        TextView mainDate=findViewById(R.id.mainDate);

        // choose another date button
        ImageButton pastButton=findViewById(R.id.pastbtn);
        pastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAnotherDate();
            }
        });

        // xml parsing ??? Url builder
        urlBuilder = new StringBuilder(ASTROURL);

        // recyclerview ???????????? ???????????? item click listener
        recyclerView=findViewById(R.id.recycler);
        adapter=new AstroAdapter(items,this);
        // ?????? ?????????????????? ????????? ????????? ?????? ???????????? ??????
        adapter.setOnAstroClickListener(new AstroAdapter.onAstroClickListener() {
            // ?????? ?????? -> ??? ???????????? ?????????
            @Override
            public void onItemClick(View v) {
                TextView query=v.findViewById(R.id.textTitle);
                Intent intent=new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,query.getText().toString());
                startActivity(intent);
            }

            // ?????? ?????? -> ?????? ???????????? ?????????
            @Override
            public void onLongClick(View v) {
                setDialog(v);
            }
        });
        recyclerView.setAdapter(adapter);


        // main xml ?????? ????????? ?????? ??????
        Date thisMonth=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM//DD");
        String date=dateFormat.format(thisMonth);
        String[] formattedDate=date.split("/");
        String year=formattedDate[0];
        String month=formattedDate[1];
        mainDate.setText(String.format("%s??? %s??? ????",year,month));


        // url ??????
        makeUrlBuilder(year,month);
        xmlparsing();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
    }

    // item??? ?????? ????????? ???????????? ?????????
    private void setDialog(View v){
        TextView textView=v.findViewById(R.id.textTitle);
        String event=textView.getText().toString();

        TextView firstItem=v.findViewById(R.id.textevent);
        String isFirstItem=firstItem.getText().toString();

        if(isFirstItem.length()<2){
            AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("[ "+event+" ] ??? ?????? \n????????? ????????????????");
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    setAlarm(v);
                    Toast.makeText(MainActivity.this, "?????? ??????!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            AlertDialog alertDialog= builder.create();
            alertDialog.show();
        }
        else{
            // main event??? ?????? ?????? ??????
        }

    }
    public void makeUrlBuilder(String year, String month){
        Log.d("parsinglog","makeurlbuilder ?????????");
        try{
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "="+APIKEY);
            urlBuilder.append("&" + URLEncoder.encode("solYear","UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); // ??????
            urlBuilder.append("&" + URLEncoder.encode("solMonth","UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); // ???
            Log.d("parsinglog","url: "+urlBuilder.toString());

        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private void setAlarm(View item){
        Intent alarmIntent=new Intent(MainActivity.this,AlarmReceiver.class);
        alarmIntent.setAction("com.example.astro.ALARM_RECEIVER");

        TextView title=item.findViewById(R.id.textTitle);
        TextView loctime=item.findViewById(R.id.textTime);
        TextView dayText=item.findViewById(R.id.textLocdate);


        String content=String.format("?????? %s??? ???????????? ?????????",loctime.getText().toString());
        String time=dayText.getText().toString()+" 00:05:00"; // ???????????? ?????? ??? 0??? 5?????? ????????? ??????

        alarmIntent.putExtra("title",title.getText().toString());
        alarmIntent.putExtra("content",content);
        PendingIntent pendingIntent=PendingIntent.getBroadcast
                (MainActivity.this,0,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_MUTABLE);

        // alarm manager ??????
        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date TargetTime=null;
        try{
            TargetTime=dateFormat.parse(time);
        }catch (ParseException e){
            e.printStackTrace();
        }

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(TargetTime);

        Log.d("alarmtest","setAlarm()");
        alarmManager.set(AlarmManager.RTC,calendar.getTimeInMillis(),pendingIntent);
    }

    // ????????? ?????? ?????? ?????? ????????? ???????????? xml parsing
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
                mainDate.setText(String.format("%s??? %s??? ????",year,month));

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
        // ??????????????? doInBackground ??? return ??????
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