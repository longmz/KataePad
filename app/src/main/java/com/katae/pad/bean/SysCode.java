package com.katae.pad.bean;

public class SysCode {
    public final String mCodeNo;
    public final String mCodeName;
    public final String mComment;

    public SysCode(String codeNo, String codeName, String comment) {
        this.mCodeNo = codeNo;
        this.mCodeName = codeName;
        this.mComment = comment;
    }
}