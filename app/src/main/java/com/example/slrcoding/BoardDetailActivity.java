package com.example.slrcoding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

import static com.example.slrcoding.MainActivity.uservo;

public class BoardDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private ImageView board_image;

    //좋아요 댓글 수 데이터 교환 가능해야함
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView categoryTextView;
    private TextView nameTextView;
    private TextView dateTextView;
    private LikeButton likelyButton;
    private CardView kakaoLinkTextView;
    private String kakaoUrl;

    private RecyclerView mReplyRecyclerView;
    private List<BoardReplyVO> boardReplyVOList;
    private BoardReplyAdapter replyAdapter;
    private ImageButton replyButton;

    private String category;
    private String category2;
    private String idfrom;
    //private String id ;
    private String title;

    private String contents;
    private String name;
    private String regDate;
    String regDateModify = null; //수정된 날짜
    private Long replyCnt;
    private Long likeCnt;
    private String image;

    private TextView replyName;//닉네임
    private TextView replyContent;//댓글 내용
    private TextView replyRegDate; //댓글 작성 일자
    private TextView likeCntView;
    private int likeflag; //댓글 스위치 플래그(파이어베이스 연동)
    private TextView replyCntView; //댓글 수
    private EditText replyEditTextView;
    private String replyId;
    private String time1;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 로그인 정보 가져오기 위한 선언
    private FirebaseAuth firebaseAuth;           // 파이어베이스 인증 객체 생성
    private FirebaseUser currentUser;

    public static boolean likeuserconfirm; //좋아요 누른 사용자를 확인하는 플래그
    private String likeid;

    private String userEmail;


    public static boolean delete_flag;
    public static boolean delete_flag2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        userEmail = uservo.getUser_email();

        Log.i("userEmail: ", "메인에서받아온 userEmail: " + userEmail);

        titleTextView = findViewById(R.id.bard_title);

        contentTextView = findViewById(R.id.board_context);
        categoryTextView = findViewById(R.id.board_detail_category);

        nameTextView = findViewById(R.id.board_fname);
        dateTextView = findViewById(R.id.board_date);
        likelyButton = findViewById(R.id.board_like_button);
        replyButton = findViewById(R.id.board_reply_submit);
        replyCntView = findViewById(R.id.board_reply_cnt);  // 게시판 게시글 댓글 수
        likeCntView = findViewById(R.id.board_like_cnt2);
        kakaoLinkTextView = findViewById(R.id.board_detail_kakaoLink);

        replyName = findViewById(R.id.board_reply_name);
        replyContent = findViewById(R.id.board_reply_content);
        replyRegDate = findViewById(R.id.board_reply_date);

        replyEditTextView = findViewById(R.id.board_reply_edit);
        toolbar = (Toolbar) findViewById(R.id.board_toolbar3);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = getIntent().getStringExtra("category");
        idfrom = getIntent().getStringExtra("id");
        Log.i("idfrom: ", "idfRom" + idfrom);
        DocumentReference doc = db.collection(category).document(idfrom);
        doc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            title = (String) documentSnapshot.getData().get("title");
                            contents = (String) documentSnapshot.getData().get("contents");
                            category2 = (String) documentSnapshot.getData().get("category");
                            name = (String) documentSnapshot.getData().get("name");
                            regDate = (String) documentSnapshot.getData().get("regDate");

                            Calendar calendar = new GregorianCalendar(Locale.KOREA);
                            // 현재 년도일 경우 없애서 보여주고 작년 일 경우 년도 표시하기
                            int nYear = calendar.get(Calendar.YEAR);
                            String year = Integer.toString(nYear);
                            String regYear = regDate.substring(0, 4);

                            //현재 년도에 등록했을 때는 월/일 시간 만 보여주기
                            if (year.equals(regYear)) {
                                regDateModify = regDate.substring(6, 17);
                            } else { //현재 년도가 아닐 경우 즉 작년에 쓴글이라면 년도까지 표현하기!!
                                regDateModify = regDate.substring(0, 17);
                            }
                            replyCnt = (Long) documentSnapshot.getData().get("replyCnt");
                            likeCnt = (Long) documentSnapshot.getData().get("likeCnt");
                            image =(String) documentSnapshot.getData().get("image");

                            kakaoUrl = (String)documentSnapshot.getData().get("kakaolink");
                            if(kakaoUrl.equals("") || kakaoUrl.equals("https://open.kakao.com/o/")){
                                kakaoLinkTextView.setVisibility(View.INVISIBLE);
                            }

                            Log.i("title", "title: " + title);
                            Log.i("title", "contents: " + contents);
                            Log.i("title", "category2: " + category2);
                            Log.i("title", "name: " + name);
                            Log.i("title", "replyCnt: " + replyCnt);
                            Log.i("title", "image: " + image);

                        } else {
                            Log.i("error", "get Failed: " + task.getException());
                        }

                        titleTextView.setText(title);
                        contentTextView.setText(contents);
                        categoryTextView.setText(category2);
                        // 작성자 등록
                        nameTextView.setText(name);

                        dateTextView.setText(regDateModify);
                        replyCntView.setText(String.valueOf(replyCnt));
                        likeCntView.setText(String.valueOf(likeCnt));
                        // 중고나라 이미지 파베에 가져오기
                        downloadFile();

                    }
                });

        //댓글 등록시 실시간 불러오기로 받아올 곳
        mReplyRecyclerView = findViewById(R.id.board_reply_recycler);
        boardReplyVOList = new ArrayList<>();
        Query query = db.collection(category).document(idfrom).collection("reply");
        ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                    String replyId = (String) dc.getDocument().getData().get("id");
                    String replyContent = (String) dc.getDocument().getData().get("replyContent");
                    String replyName = (String) dc.getDocument().getData().get("replyName");
                    String replyDate = (String) dc.getDocument().getData().get("replyDate");
                    Calendar calendar = new GregorianCalendar(Locale.KOREA);
                    //현재 년도일 경우 없애서 보여주고 작년 일 경우 년도 표시하기
                    int nYear = calendar.get(Calendar.YEAR);
                    String year = Integer.toString(nYear);
                    String regYear = replyDate.substring(0, 4);
                    String replyDateModify = null;
                    //현재 년도에 등록했을 때는 월/일 시간 만 보여주기
                    if (year.equals(regYear)) {
                        replyDateModify = replyDate.substring(6, 17);
                    } else {
                        //현재 년도가 아닐 경우 즉 작년에 쓴글이라면 년도까지 보여주기
                        replyDateModify = replyDate.substring(0, 17);
                    }
                    Log.i("Reply:", "replyId: " + replyId);
                    Log.i("Reply:", "replyContent: " + replyContent);
                    Log.i("Reply:", "replyName: " + replyName);
                    Log.i("Reply:", "replyDate: " + replyDate);
                    BoardReplyVO replyVO = new BoardReplyVO(replyId, replyContent, replyName, replyDate, replyDateModify);
                    //datacopy = data;

                    boardReplyVOList.add(replyVO);
                }

                Collections.sort(boardReplyVOList, new CompareRegDateDesc());
                replyAdapter = new BoardReplyAdapter(boardReplyVOList);
                replyAdapter.notifyDataSetChanged();
                mReplyRecyclerView.setAdapter(replyAdapter);
                mReplyRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
        });

        //댓글 등록 시 댓글 수 실시간으로 보여주기
        final DocumentReference docRef = db.collection(category).document(idfrom);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    replyCnt = (Long) snapshot.getData().get("replyCnt");
                    replyCntView.setText(String.valueOf(replyCnt));
                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }
        });

        //좋아요 버튼 클릭 시 좋아요 수 실시간으로 보여주기
        final DocumentReference docRef2 = db.collection(category).document(idfrom);
        docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.i("idfrom","idfrom : "+idfrom);

                    likeCnt = (Long)snapshot.getData().get("likeCnt");
                    likeCntView.setText(String.valueOf(likeCnt));

                } else {
                    //Log.d(TAG, "Current data: null");
                }
            }

        });

        setClickEvent();
        setReplySubmit();

        //즐겨찾기 누른 사용자 있는지 확인 후 setLiked
        confirmLikeUser(userEmail, new BoardDetailActivity.BoardCallback() {
            @Override
            public void onCallback(boolean value) {
                if (value) {
                    likelyButton.setLiked(false);
                } else {
                    likelyButton.setLiked(true);
                }
            }
        });

        confirmdelete(userEmail, new BoardCallback() {
            @Override
            public void onCallback(boolean value) {
                delete_flag2 = value;
            }
        });
        //카카오 링크 클릭 이벤트
        kakaoLinkTextView.setOnClickListener(this);
    }
    //카카오 링크 클릭 이벤트트
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feed_detail_kakaoLink:
                //AsyncTask 함수 호출.
                BoardDetailActivity.KakaoLinkProgressTask task = new BoardDetailActivity.KakaoLinkProgressTask();
                task.execute();
                break;
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        
    }

    //AsyncTask 카카오 링크 프로그레스다이얼로그 띄우기
    private class KakaoLinkProgressTask extends AsyncTask<Void,Void,Void> {

        //        ProgressDialog asyncDialog = new ProgressDialog(
//                FeedDetailActivity.this);
        final SweetAlertDialog progressDialog = new SweetAlertDialog(BoardDetailActivity.this,SweetAlertDialog.PROGRESS_TYPE);
        @Override
        protected void onPreExecute() {
            progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progressDialog.setTitleText("오픈채팅방 링크 연결중....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();

        }
        @Override
        protected Void doInBackground(Void... strings) {
            try {
                for (int i = 0; i < 5; i++) {
//                    asyncDialog.setProgress(i * 40);

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//            asyncDialog.dismiss();
            progressDialog.dismiss();
            Intent intent = null;
            try {
                intent = Intent.parseUri(kakaoUrl,Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException e1) {
                Toasty.error(BoardDetailActivity.this,"오픈채팅방 연결 에러!!",Toasty.LENGTH_SHORT,true);

                e1.printStackTrace();
            }
            Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
            if(existPackage!=null){
                startActivity(intent);
            }
            super.onPostExecute(result);

        }

    }
    //댓글 등록 시 파베에 넣기
    //댓글 수도 업데이트하기..
    private void setReplySubmit() {
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format1 = new SimpleDateFormat("yyyy년 MM/dd HH:mm:ss");
                Date time = new Date();
                time1 = format1.format(time);

                //예외처리
                if (replyEditTextView.getText().toString().equals("")) {
                    Toasty.warning(BoardDetailActivity.this, "댓글 내용을 입력해주세요.", Toasty.LENGTH_SHORT).show();
                    return;
                }

                replyId = db.collection(category).document(idfrom).collection("reply").document().getId();
                Map<String, Object> post = new HashMap<>();
                post.put("id", replyId);
                post.put("replyDate", time1);
                post.put("replyContent", replyEditTextView.getText().toString());

                String userName = uservo.getUser_name();
                // 사용자 이름
                post.put("replyName", userName);

                db.collection(category)
                        .document(idfrom).collection("reply").document(replyId).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(BoardDetailActivity.this, "댓글이 등록되었습니다.", Toasty.LENGTH_SHORT).show();
                                // finish();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error adding document", e);
                                Toasty.error(BoardDetailActivity.this, "업로드 실패!!", Toasty.LENGTH_SHORT).show();
                            }
                        });

                db.collection(category).document(idfrom)
                        .update("replyCnt", replyCnt + 1L).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(BoardDetailActivity.this, "댓글 수 증가!!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(BoardDetailActivity.this, "댓글 수 증가 실패!!", Toast.LENGTH_SHORT).show();
                    }
                });
                replyEditTextView.setText(""); //댓글 쓰면 내용 비어줘기
            }
        });
    }

    private void setClickEvent() {
        likelyButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Context context = likeButton.getContext();

                // Toast.makeText(context, "좋아요 버튼 클릭!!", Toast.LENGTH_SHORT).show();

                //Firebase에 좋아요 수 1증가 update 실행
                db.collection(category).document(idfrom)
                        .update("likeCnt", likeCnt + 1L).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(BoardDetailActivity.this, "즐겨찾기 완료", Toasty.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(BoardDetailActivity.this, "즐겨찾기 실패", Toasty.LENGTH_SHORT).show();
                    }
                });

                Map<String, Object> post = new HashMap<>();
                post.put("userEmail", userEmail);

                db.collection(category)
                        .document(idfrom).collection("LikeUsers").document(userEmail).set(post)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(BoardDetailActivity.this, "즐겨찾기 유저 등록 완료", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(BoardDetailActivity.this, "즐겨찾기 유저 등록 실패", Toasty.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Context context = likeButton.getContext();

                //Toast.makeText(context, "좋아요 버튼 취소!!", Toast.LENGTH_SHORT).show();

                //Firebase에 좋아요 수 1감소 update 실행
                db.collection(category).document(idfrom)
                        .update("likeCnt", likeCnt - 1L).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        likeflag = 0;
                        Toasty.success(BoardDetailActivity.this, "즐겨찾기 취소", Toasty.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(BoardDetailActivity.this, "즐겨찾기 취소 실패", Toasty.LENGTH_SHORT).show();

                    }
                });
                //TOdo:LikeUsers 컬렉션 문서도 삭제한다.(해당 로그인 사용자 ID)

                db.collection(category)
                        .document(idfrom).collection("LikeUsers").document(userEmail).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Toast.makeText(BoardDetailActivity.this, "즐겨찾기 유저 삭제 완료", Toast.LENGTH_SHORT).show();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(BoardDetailActivity.this, "즐겨찾기 유저 삭제 실패", Toasty.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    //추가된 소스, ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.boarddetailmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.board_delete:
                final SweetAlertDialog progressDialog = new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
                progressDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                progressDialog.setTitleText("글 삭제중...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                if (delete_flag2) {
                    db.collection(category).document(idfrom)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toasty.success(BoardDetailActivity.this, "게시글 삭제 완료", Toasty.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("flag", 2);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.error(BoardDetailActivity.this,"게시글 삭제 실패",Toasty.LENGTH_SHORT,true).show();
                                }
                            });
                } else {
                    Toasty.warning(this, "작성자 외에는 삭제할 수 없습니다.", Toasty.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    static class CompareRegDateDesc implements Comparator<BoardReplyVO> {
        @Override
        public int compare(BoardReplyVO b1, BoardReplyVO b2) {

            return b2.getReplyDate().compareTo(b1.getReplyDate());
        }
    }

    //Todo: 콜백을 통한 likeusers 컬렉션에서 아이디가 존재하는지 확인 구현 중
    // 중복되는 아이디 존재 확인.(파이어베이스의 비동기 처리문제로 인해 외부에서 데이터 접근하기 위해 콜백함수 사용)
    public void confirmLikeUser(String userEmail, BoardDetailActivity.BoardCallback mycallback) {
        db.collection(category).document(idfrom).collection("LikeUsers").whereEqualTo("userEmail", userEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            likeuserconfirm = task.getResult().isEmpty();
                            mycallback.onCallback(likeuserconfirm);       // flag값이 수신됐을때 시스템에서 콜백함수 호출
                        }
                    }
                });
    }

    //Todo: 삭제 시 해당 유저가 쓴글인지 확인하여 flag를 콜백하는 함수.
    public void confirmdelete(String userEmail, BoardDetailActivity.BoardCallback mycallback2) {
        db.collection(category).document(idfrom).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (userEmail.equals(document.get("userEmail"))) {
                        delete_flag = true;
                    } else {
                        delete_flag = false;
                    }
                    mycallback2.onCallback(delete_flag);
                }
            }
        });
    }

    // 비동기 처리 해결하기 위해 생성한 콜백함수
    public interface BoardCallback {
        void onCallback(boolean value);
    }

    // FireStorage에 사진 다운로드
    public void downloadFile(){
        board_image = (ImageView) findViewById(R.id.board_image);


        //String board_image_url = Board2.getId();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://slrcoding.appspot.com/");
        //Log.i("메시지:", image);
        //다운로드할 파일을 가르키는 참조 만들기
        StorageReference pathReference = storageReference.child("Board images/" + image );
        //Log.i("메시지:", "파베 이미지 받기");

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(board_image);
                Log.i("메시지:", "파베 이미지 적용"+uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("메시지:", "실패");

                board_image.setImageResource(R.drawable.board_example);
            }
        });
    }
}
