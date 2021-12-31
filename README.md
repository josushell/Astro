# Astro
#### 월별로 천문 관측 정보 받아보기
2021/09/01~2021/12/14

분명 api parsing을 가을에 했었는데 프로젝트는 겨울에 끝난 기묘한 이야기
<br><br>
### 1. Astro 🪐

울산에서 좀만 가면 경상남도에는 영남 알프스라는 산이 있다. 근데 이상하게도 거기 정상에는 뜬금없이 영화관이 하나 있다.
중학교 때 가족들이랑 심야영화를 보러 영남알프스 꼭대기에 갔었다. 근데 차에서 내리자마자 하늘에 별이 쏟아질 것 같다는 말이 무슨 뜻인지 그때 알게 되었다. 난 city girl 이었기 때문에 그런 별들을 처음 봤었다. 진짜 별이 엄청나게 많이 보이는데 책에서 봤던 알파별까지도 다 보여서 계절별 대별자리도 눈으로 볼 수 있을 정도였다. 그리고 난 그때의 기억이 너무 생생해서 아직까지도 그걸 다시 보기위해 살고 있을 정도이다. 
하여튼 그때의 경험으로 내가 천문학을 좋아하게되고 천체 관측을 취미로 하게 되었는데 매달 어떤 천문 현상이 일어나는지, 예를 들면 이번달에 월식이 일어나는데 그게 언제이고 몇시인지 등의 정보를 항상 뉴스를 통해서만 알다보니까 한계가 있었다.
 
다음은 주요 불편 사항들이었다.
1. 뉴스를 통해서만 쉽게 정보를 얻음
2. 알림을 직접 일일이 설정해야함
3. 모르는 건 또 검색을 해야함
 
이들만 개선해도 내 천체관측 라이프가 편해질 것 같았다. 그래서 가장 접근성이 높은 모바일 앱으로 만들고자 했다.    

메인 화면 screenshots.
<img width="1104" alt="image" src="https://user-images.githubusercontent.com/63590121/147815281-d97fcb9f-bf07-4d99-b1ec-b85cae27c47c.png">
 
 
<br><br>
### 2. 개발 과정

  
Tools
* Android Studio API 31  
* Java   
* astro information api parsing
 
 프로젝트 구조는 다음과 같다.    
<img width="300" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbaTJlu%2FbtrpffGWwAp%2FCyvhOwnXLpQfikFdn9eJU1%2Fimg.png">
 
일단 3가지 이슈들을 구현하기 위해서 다음과 같은 구조를 생각해냈다.
 
1. 한국 천문 연구원의 천문 현상 정보 api를 받아와서 이를 앱으로 쉽게 볼 수 있게 하자.   
* Xml parsing
* AsyncTask (비동기 스레드)
* DatePickerDialog  

2. 특정 이벤트를 눌렀을 때 바로 웹 검색이 이어지도록 하자.  
* Intent: SearchManager.QUERY    

3. 특정 이벤트에 대한 알림 서비스로 쉽게 기억하도록 하자  
* AlarmManager
* BroadcastReceiver
* Service
     
<br><br>
     
#### xml parsing

api는 공공 데이터 포털에서 가져왔다. 이는 한국천문연구원에서 공식으로 제공해주는 api여서 신뢰도가 있었다.

parsing을 하려면 xml의 구조를 알아야 한다. 나는 item 태그 별로 구분하여 하나의 천문 이벤트들을 각각 저장하였다.
이벤트들은 다시 시간, 날짜 등의 세부 정보들로 구분되고 이들을 모두 하나의 클래스로 구현하여 저장할 수 있게 하였다.
이 작업은 예전에 한번 해봤던 것이라 크게 어렵지 않았다. 그냥 태그별로 구분해주면 되는 것이었음둥
 
근데 문제는 이걸 메인스레드에서 처리할 수 없기에 (가끔 xml의 트래픽 때문인지 느려짐) 다른 스레드에서 처리했어야 했는데
이걸 비동기로 처리하자고 생각했음.
그래서 AsyncTask로 이를 구현했다.    
   
    
이러한 정보들을 사용자가 보기 쉽게 정렬하기 위해서 recycler view를 사용했다.
근데 문제는 view item에 대한 클릭 이벤트를 처리해야 되는데 나는 이게 activity로 이어지는 거라 recycler 클래스에서 이를 구현할 수가 없었다.
그래서 굳이굳이 커스텀 인터페이스를 만들어야 했다.
~~~
 class XMLTask extends AsyncTask<URL, Void, String> {
	@Override
	protected String doInBackground(URL... urls) {
		// parsing 여기서 진행됨
~~~
다음과 같이 onAstroClickListener 라는 커스텀 인터페이스를 만들고 그걸 ViewHolder 클래스 내부에서 클릭 이벤트를 처리할 때 커스텀 인터페이스로 이어지도록 구현했다.
~~~
public interface onAstroClickListener{
    void onItemClick(View v);
    void onLongClick(View v);
}

public void setOnAstroClickListener(onAstroClickListener listener){
    this.astroClickListener=listener;
}
~~~
그 다음 MainActivity에서 커스텀 인터페이스의 메서드를 구현해서 등록해뒀다.
커스텀 인터페이스를 처음으로 사용해봤는데 생각해둔 코드 구조가 많이 망가지지 않아서 다행이다.
<br><br>
#### Alarm Service
다음은 꽤나 자잘한 이슈들이 많았던 noti 알람이다.<br>
<img width="400" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmPfkW%2FbtrpjxUEKL6%2FbOsELvM4scqYqs81jsNJ0k%2Fimg.png">

LongClick을 활용해서 커스텀 인터페이스에서 설정한 item을 길게 클릭했을 때의 커스텀 메서드를 구현하여 사용자 대화창을 설정했다.
여기서 ok를 하게 되면 해당 이벤트가 있는 날 0시 5분에 상단바 noti 알림을 보내준다.
이때 월 마다 있는 main event에 대한 알림은 설정을 제한했다. 왜냐면 Main event는 다른 event와 달리 event 정보가 있기 때문이었음. 이 구현을 하기 위해서 event의 String 길이를 가져와서 2보다 작은 경우( main event가 아닌 경우) 알림을 설정하고, 아닌 경우 알림 설정 제한하는 식으로 코드를 작성했다.
 
알림 처리 구조: alarmManager -> BroadcastReceiver -> Service

상단바 noti 알림
일단 여기서 생겼던 문제들은 Intent 이슈들이었다.
아니 분명 제대로 extra에 이벤트 정보들을 담아서 브로드 캐스트 수신자로 보내고, 서비스로 보내는데
대체 왜 거기서는 수신이 안되는 건지 궁금해 죽을 지경이었다.
근데 어찌어찌 해결을 하긴 했다.
난 명시적 인텐트로만 설정을 해뒀는데, action도 설정해두는 것으로 해결했다.
나와 같은 이슈를 겪고 있는 사람들이라면 나의 포스팅을 참조하길 바란다.<br>
https://josushell.tistory.com/38

<br><br>
### 3. screenshots
<img width="400" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdK9KTi%2Fbtrputjpbha%2FnjM6gUdgBHDnnUTeqjvXhk%2Fimg.gif">
<img width="400" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FPdMMu%2FbtrpjxnwUmG%2FD0s5JGKYHspQl3w7pqygp0%2Fimg.gif">
<img width="400" alt="image" src="https://user-images.githubusercontent.com/63590121/147816259-4eab816b-2d3d-4193-a858-7067a7597f50.gif">
<img width="400" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbD87pF%2FbtrpmfNxCDn%2FsZg39ORNE8aq7FOgpuPep1%2Fimg.gif">
<img width="400" alt="image" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbaabRq%2FbtrpoyTvtS6%2F4iApuGVylIhfzBZSVFCdvk%2Fimg.gif">
<br>
모아두니 요란하다
<br><br>
     
### 4. 후기
처음부터 끝까지 내가 계획하고 구조도 내가 짜고 코드도 다 짜고 오픈소스도 안써본 프로젝트이다. 그리고 나랑 제일 관련있고 제일 흥미있는 것이기도 했다. 안드로이드에서 가장 중요한게 액티비티 생명주기랑 스레드, 동기화라고 생각하는데 이런 부분들을 가장 신경 써서 만들었다. 이건 플레이 스토어에 출시 해봐야겠다. 사람들이 쓸지는,,,의문이지만 적어도 나는 쓸 것 같다.
아 근데 나는 ios라서 사실 공기계에서만 사용 가능함...

