package com.example.slrcoding.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.text.InputFilter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.slrcoding.Adapter.MypageListAdapter;
import com.example.slrcoding.LoginActivity;
import com.example.slrcoding.MainActivity;
import com.example.slrcoding.Mypage_sub0_Comment;
import com.example.slrcoding.Mypage_sub0_mywrite;
import com.example.slrcoding.Mypage_sub2_Alarm;
import com.example.slrcoding.Mypage_sub3_Notice;
import com.example.slrcoding.Mypage_sub3_Question;
import com.example.slrcoding.Mypage_sub3_Rule;
import com.example.slrcoding.R;
import com.example.slrcoding.VO.ChildListData;
import com.example.slrcoding.VO.ParentListData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

import static android.app.Activity.RESULT_OK;
import static com.example.slrcoding.MainActivity.uservo;

// 최민철(수정 : 19.08.19)
public class MypageFragment extends Fragment {

    String submenu1[] = {"내가 쓴 글", "댓글 단 글"};
    String submenu2[] = {"프로필 이미지 변경", "닉네임 변경", "로그 아웃"};
    String submenu3[] = {"알림 설정"};
    String submenu4[] = {"문의하기", "공지사항", "커뮤니티 이용규칙"};

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();               // 파이어베이스 인증 객체 생성 및 선언
    private FirebaseFirestore firebasestore = FirebaseFirestore.getInstance();    // 파이어베이스 스토어 객체 생성 및 선언
    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();      // 파이어베이스 스토리지 객체 생성
    private StorageReference storageReference;

    private Uri filepath;
    private ArrayList<ParentListData> parentListData;
    private ExpandableListView parentListView;
    private TextView tv_username;
    private ImageView iv_profile;
    public MypageListAdapter adpater;
    private String prof_string = "_profileImage.png";
    private boolean push_alarm, comment_alarm, event_alarm;
    public boolean[] alarm_set = new boolean[3];
    private final int REQUEST_CODE1 = 100, REQUEST_CODE2 =200;

    public MypageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 알람 설정 값 변경시 적용
        if(requestCode == REQUEST_CODE1){
            if(resultCode == RESULT_OK){
                alarm_set = data.getBooleanArrayExtra("alarm_key");
                ((MainActivity)getActivity()).uservo.setPush_alarm(alarm_set[0]);
                ((MainActivity)getActivity()).uservo.setComment_alarm(alarm_set[1]);
                ((MainActivity)getActivity()).uservo.setEvent_alarm(alarm_set[2]);
                push_alarm = alarm_set[0];
                comment_alarm = alarm_set[1];
                event_alarm = alarm_set[2];
            }
        }

        // 프로필 이미지 변경시 적용
        if(requestCode == REQUEST_CODE2){
            if(resultCode == RESULT_OK){
                try {
                    filepath = data.getData();
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    uploadFile();       // FireStorage에 이미지 업로드
                    iv_profile.setImageBitmap(img);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View MyPage_View = inflater.inflate(R.layout.fragment_mypage, container, false);
        parentListView = (ExpandableListView) MyPage_View.findViewById(R.id.mypage_parentListView);
        tv_username = (TextView) MyPage_View.findViewById(R.id.mypage_userid);
        iv_profile = (ImageView) MyPage_View.findViewById(R.id.mypage_profile_image);
        registerForContextMenu(parentListView);

        // MainActivity의 uservo객체를 이용하여 초기 user id와 알림값 세팅
        tv_username.setText(((MainActivity) getActivity()).uservo.getUser_id());
        push_alarm = ((MainActivity) getActivity()).uservo.isPush_alarm();
        comment_alarm = ((MainActivity) getActivity()).uservo.isComment_alarm();
        event_alarm = ((MainActivity) getActivity()).uservo.isEvent_alarm();

        // 프로필 이미지 파베에 가져오기
        downloadFile();

        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        parentListView.setIndicatorBounds(width - 200, width);
        parentListData = new ArrayList<>();

        ParentListData parentListData_community = new ParentListData();
        ParentListData parentListData_account = new ParentListData();
        ParentListData parentListData_setting = new ParentListData();
        ParentListData parentListData_info = new ParentListData();
        parentListData_community.setName("커뮤니티");
        parentListData_account.setName("계정");
        parentListData_setting.setName("앱 설정");
        parentListData_info.setName("앱 정보");

        parentListData.add(parentListData_community);
        parentListData.add(parentListData_account);
        parentListData.add(parentListData_setting);
        parentListData.add(parentListData_info);

        // submenu1 등록
        for (int i = 0; i < submenu1.length; i++) {
            ChildListData childListData1 = new ChildListData();
            childListData1.setTitle(submenu1[i]);
            parentListData.get(0).childListData.add(childListData1);
        }

        // submenu2 등록
        for (int i = 0; i < submenu2.length; i++) {
            ChildListData childListData2 = new ChildListData();
            childListData2.setTitle(submenu2[i]);
            parentListData.get(1).childListData.add(childListData2);
        }

        // submenu3 등록
        for (int i = 0; i < submenu3.length; i++) {
            ChildListData childListData3 = new ChildListData();
            childListData3.setTitle(submenu3[i]);
            parentListData.get(2).childListData.add(childListData3);
        }

        // submenu4 등록
        for (int i = 0; i < submenu4.length; i++) {
            ChildListData childListData4 = new ChildListData();
            childListData4.setTitle(submenu4[i]);
            parentListData.get(3).childListData.add(childListData4);
        }

        adpater = new MypageListAdapter(getActivity(), parentListData);
        parentListView.setAdapter(adpater);

        // 초기에 펼쳐진 상태
        for (int i = 0; i < adpater.getGroupCount(); i++)
            parentListView.expandGroup(i);

        // 자식 리스트를 눌렀을 때 이벤트 처리
        parentListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                switch (i) {
                    case 0:
                        switch (i1) {
                            case 0:
                                Intent intent1 = new Intent(getActivity(), Mypage_sub0_mywrite.class);
                                startActivity(intent1);
                                return true;
                            case 1:
                                Intent intent2 = new Intent(getActivity(), Mypage_sub0_Comment.class);
                                startActivity(intent2);
                                return true;
                        }
                        return true;
                    case 1:
                        switch (i1) {
                            case 0:
                                showDialog1();  // 프로필 이미지 변경 Dialog
                                return true;
                            case 1:
                                showDialog2();  // 닉네임 변경 Dialog
                                return true;
                            case 2:
                                showDialog3();  // 로그아웃 Dialog
                                return true;
                        }
                        return true;
                    case 2:
                        switch (i1) {
                            case 0:
                                Intent intent4 = new Intent(getActivity(), Mypage_sub2_Alarm.class);
                                intent4.putExtra("comment_alarm", comment_alarm);
                                intent4.putExtra("push_alarm", push_alarm);
                                intent4.putExtra("event_alarm", event_alarm);
                                intent4.putExtra("user_email", ((MainActivity) getActivity()).uservo.getUser_email());
                                startActivityForResult(intent4, REQUEST_CODE1);
                                return true;
                        }
                        return true;
                    case 3:
                        switch (i1) {
                            case 0:
                                Intent intent6 = new Intent(getActivity(), Mypage_sub3_Question.class);
                                intent6.putExtra("user_email", ((MainActivity) getActivity()).uservo.getUser_email());
                                startActivity(intent6);
                                return true;
                            case 1:
                                Intent intent7 = new Intent(getActivity(), Mypage_sub3_Notice.class);
                                startActivity(intent7);
                                return true;
                            case 2:
                                Intent intent8 = new Intent(getActivity(), Mypage_sub3_Rule.class);
                                startActivity(intent8);
                                return true;
                        }
                        return true;
                }
                return false;
            }
        });

        // Inflate the layout for this fragment
        return MyPage_View;
    }

    // 프로필 이미지 변경 Dialog 띄우기
    public void showDialog1(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        final CharSequence[] AltDlg_Items = {"프로필 이미지 변경", "프로필 이미지 삭제"};
        builder.setItems(AltDlg_Items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, REQUEST_CODE2);
                        break;
                    case 1:
                        iv_profile.setImageResource(R.drawable.defaultimage);

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReferenceFromUrl("gs://slrcoding.appspot.com/");

                        // 삭제할 파일을 가르키는 참조 만들기
                        StorageReference pathReference = storageReference.child("Profile Images/"+((MainActivity) getActivity()).uservo.getUser_id() + prof_string);
                        pathReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Todo: 리얼타임데이터베이스 users 컬렉션도 삭제해주기. by 이정찬
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                ref.child("users").child(firebaseAuth.getCurrentUser().getUid()).removeValue();

                                Toast.makeText(getContext(),"이미지 삭제완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // FireStorage에 프로필 업로드
    public void uploadFile(){

        if(filepath!=null) {

            // 업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            // 파일명 포맷
            String filename = ((MainActivity) getActivity()).uservo.getUser_id() + prof_string;

            // 사진 업로드
            storageReference = firebaseStorage.getReferenceFromUrl("gs://slrcoding.appspot.com/").child("Profile Images/" + filename);
            storageReference.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 이미지 uri를 통해 바로 이미지 뷰에 세팅
                            Glide.with(getContext())
                                    .load(storageReference.getDownloadUrl().toString())
                                    .into(iv_profile);

                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //@SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });

        }
    }

    // FireStorage에 프로필 다운로드
    public void downloadFile(){

        // 업로드 진행 Dialog 보이기
        final ProgressDialog progressDialog = new ProgressDialog(this.getActivity());
        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://slrcoding.appspot.com/");

        //다운로드할 파일을 가르키는 참조 만들기
        StorageReference pathReference = storageReference.child("Profile Images/"+((MainActivity) getActivity()).uservo.getUser_id() + prof_string);

        // download url을 가져와 사진이 존재하면 세팅 없으면 디폴트 이미지
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext())
                        .load(uri.toString())
                        .into(iv_profile);
                //Log.i("메시지:", "파베 이미지 적용"+uri.toString());
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iv_profile.setImageResource(R.drawable.defaultimage);
                progressDialog.dismiss();
            }
        });
    }

    // 닉네임 변경 Dialog 띄우기
    public void showDialog2(){
        final EditText et = new EditText(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        InputFilter[] EditFilter = new InputFilter[1];

        EditFilter[0] = new InputFilter.LengthFilter(10);       // 글자수 10자리로 제한
        builder.setTitle("닉네임 변경");             // dialog 제목 설정
        et.setSingleLine();                         // EditText를 한 줄로 제한
        et.setBackground(null);                     // 밑줄 안보이게 하기
        et.setFilters(EditFilter);                  // 글자수 제한
        builder.setView(et);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(et.getText().toString().length()>0){             // 입력 받은 값 username 설정 But, 입력을 아무것도 안할시 dialog 종료
                    tv_username.setText(et.getText().toString());
                    firebasestore.collection("사용자 정보").document((((MainActivity) getActivity()).uservo.getUser_email())).update("user_id", et.getText().toString());
                    ((MainActivity)getActivity()).uservo.setUser_id(et.getText().toString()); // uservo 객체에 아이디 변경
                    //Todo: 여기서 리얼타임 데이터베이스 users의 아이디도 변경시켜주기 by 이정찬
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference usersRef = ref.child("users");
                    DatabaseReference userskeyrRef = usersRef.child(firebaseAuth.getCurrentUser().getUid());
                    Map<String,Object> map = new HashMap<>();
                    map.put("user_id",et.getText().toString());
                    userskeyrRef.updateChildren(map);
                }
                else{
                    dialogInterface.dismiss();
                }
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();  // dialog 닫기
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show(); // 창 띄우기
    }

    // 로그아웃 Dialog 띄우기
    public void showDialog3(){
        final EditText et = new EditText(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle("로그아웃");                   // dialog 제목 설정
        builder.setMessage("로그아웃 하시겠습니까?");     // dialog 내용 설정

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();  // dialog 닫기
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show(); // 창 띄우기
    }

}
