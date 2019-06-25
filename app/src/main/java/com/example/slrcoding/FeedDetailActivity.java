package com.example.slrcoding;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

public class FeedDetailActivity extends AppCompatActivity {
    //좋아요 댓글 수 데이터 교환 가능해야함
    private TextView titleTextView;
    private TextView contentTextView;
    private TextView categoryTextView;
    private TextView dateTextView;
    private LikeButton likelyButton;

    private RecyclerView mReplyRecyclerView;
    private List<FeedReplyVO> feedReplyVOList;
    private ReplyAdapter replyAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);

        titleTextView = findViewById(R.id.detail_item_title_text);
        contentTextView = findViewById(R.id.feed_context);
        categoryTextView =findViewById(R.id.feed_detail_category);
        dateTextView = findViewById(R.id.feed_date);
        likelyButton = findViewById(R.id.like_button2);

        //툴바에 적용
        toolbar = (Toolbar)findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF9CCC65));
        //넘어온 인텐트!!
        //피드에서 넘어온 데이터(카테고리 명,제목,내용,날짜,좋아요수,댓글수 등)
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String category = intent.getStringExtra("category");
        String date = intent.getStringExtra("date");

        titleTextView.setText(title);
        contentTextView.setText(content);
        categoryTextView.setText(category);
        dateTextView.setText(date);

        //댓글 리사이클러 뷰
        mReplyRecyclerView = findViewById(R.id.feed_reply_recycler);
        /* String replyId; //댓글 아이디
         String feedId; //피드 아이디(외래키)
         String replyContent; //댓글 내용
         String replyName;// 댓글 작성자 어차피 익명!!
         String replyDate;//날짜*/
        feedReplyVOList = new ArrayList<>();
        feedReplyVOList.add(new FeedReplyVO(null,null,"안녕하세요 첫 댓글","익명","2019/05/14"));
        feedReplyVOList.add(new FeedReplyVO(null,null,"안녕하세요 첫 댓글","익명","2019/05/14"));
        feedReplyVOList.add(new FeedReplyVO(null,null,"안녕하세요 첫 댓글","익명","2019/05/14"));
        feedReplyVOList.add(new FeedReplyVO(null,null,"안녕하세요 첫 댓글","익명","2019/05/14"));
        feedReplyVOList.add(new FeedReplyVO(null,null,"안녕하세요 첫 댓글","익명","2019/05/14"));
        replyAdapter = new ReplyAdapter(feedReplyVOList);
        mReplyRecyclerView.setAdapter(replyAdapter);
        mReplyRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

        //좋아요 버튼 클릭
        setClickEvent();
    }
    private void setClickEvent(){
        likelyButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                Context context = likeButton.getContext();

                Toast.makeText(context, "좋아요 버튼 클릭!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Context context = likeButton.getContext();

                Toast.makeText(context, "좋아요 버튼 취소!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //추가된 소스, ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.feeddetailmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete:
                Toast.makeText(getApplicationContext(), "피드 삭제!!!", Toast.LENGTH_SHORT).show();


        }
        return super.onOptionsItemSelected(item);
    }
}