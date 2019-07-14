package com.example.slrcoding;

// Board(중고나라) VO모델
// 김연준
public class Board2 {
    private String id;
    private String category;
    private String title;
    private String contents;
    private String name;
    private String regDate;
    private Long replyCnt;
    public Board2(){

    }

    public Board2(String id, String category, String title, String contents, String name, String regDate, Long replyCnt) {
        this.id = id;
        this.category= category;
        this.title = title;
        this.contents = contents;
        this.name = name;
        this.regDate =regDate;
        this.replyCnt = replyCnt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory(){return category;}

    public void setCategory(String category){this.category=category;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public Long getReplyCnt() {
        return replyCnt;
    }

    public void setReplyCnt(Long replyCnt) {
        this.replyCnt = replyCnt;
    }

    @Override
    public String toString() {
        return "Board2{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", name='" + name + '\'' +
                ", regDate='" + regDate + '\'' +
                ", replyCnt=" + replyCnt +
                '}';
    }
}