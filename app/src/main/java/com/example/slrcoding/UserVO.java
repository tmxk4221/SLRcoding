package com.example.slrcoding;

// 최민철(수정 : 19.08.09)
// 박영철(수정 : 19.08.20)
// 이정찬(수정 : 19.08.26)
public class UserVO {

    private String user_id;
    private String user_email;
    private String user_name;
    private String user_year;
    private String user_month;
    private String user_day;
    private String user_phone_num;
    private String user_sex;
    private boolean push_alarm;
    private boolean comment_alarm;
    private boolean event_alarm;
    private String user_profile_image_uri;
    private String user_meeting_profile_image_uri;
    private boolean use_meeting_service;
    //이정찬 unique_id 추가
    private String unique_id;
    private String user_intro_string;

    public UserVO(){}

    public void UserVO(String user_id, String user_email, String user_name, String user_year,
                       String user_month, String user_day, String user_phone_num, String user_sex, boolean push_alarm,
                       boolean comment_alarm, boolean event_alarm,String unique_id){
        this.user_id=user_id;
        this.user_email=user_email;
        this.user_name=user_name;
        this.user_year=user_year;
        this.user_month=user_month;
        this.user_day=user_day;
        this.user_phone_num=user_phone_num;
        this.user_phone_num=user_phone_num;
        this.user_sex=user_sex;
        this.push_alarm=push_alarm;
        this.comment_alarm=comment_alarm;
        this.event_alarm=event_alarm;
        this.unique_id = unique_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_year() {
        return user_year;
    }

    public void setUser_year(String user_year) {
        this.user_year = user_year;
    }

    public String getUser_month() {
        return user_month;
    }

    public void setUser_month(String user_month) {
        this.user_month = user_month;
    }

    public String getUser_day() {
        return user_day;
    }

    public void setUser_day(String user_day) {
        this.user_day = user_day;
    }

    public String getUser_phone_num() {
        return user_phone_num;
    }

    public void setUser_phone_num(String user_phone_num) {
        this.user_phone_num = user_phone_num;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public boolean isPush_alarm() {
        return push_alarm;
    }

    public boolean isComment_alarm() {
        return comment_alarm;
    }

    public void setPush_alarm(boolean push_alarm) {
        this.push_alarm = push_alarm;
    }

    public void setComment_alarm(boolean comment_alarm) {
        this.comment_alarm = comment_alarm;
    }

    public boolean isEvent_alarm() {
        return event_alarm;
    }

    public void setEvent_alarm(boolean event_alarm) {
        this.event_alarm = event_alarm;
    }

    public boolean isUse_meeting_service() {
        return use_meeting_service;
    }

    public void setUse_meeting_service(boolean use_meeting_service) {
        this.use_meeting_service = use_meeting_service;
    }

    public String getUser_profile_image_uri() {
        return user_profile_image_uri;
    }

    public String getUser_meeting_profile_image_uri() {
        return user_meeting_profile_image_uri;
    }

    public void setUser_profile_image_uri(String user_profile_image_uri) {
        this.user_profile_image_uri = user_profile_image_uri;
    }

    public void setUser_meeting_profile_image_uri(String user_meeting_profile_image_uri) {
        this.user_meeting_profile_image_uri = user_meeting_profile_image_uri;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getUser_intro_string() {
        return user_intro_string;
    }

    public void setUser_intro_string(String user_intro_string) {
        this.user_intro_string = user_intro_string;
    }
}
