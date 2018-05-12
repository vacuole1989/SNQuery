package com.cxd.snquery.bean;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class GsxQuery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;

    private String sn;

    private String imei;

    private String url;
    @Column(length = 4000)
    private String content;

    @Column(length = 4000)
    private String returnBody;

    private String ipAddr;

    private String crtTim;

    private String code;

    private String message;
    private String nonceStr;

    public long getSeqId() {
        return seqId;
    }

    public GsxQuery setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public GsxQuery setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public GsxQuery setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public GsxQuery setUrl(String url) {
        this.url = url;
        return this;
    }


    public String getReturnBody() {
        return returnBody;
    }

    public GsxQuery setReturnBody(String returnBody) {
        this.returnBody = returnBody;
        return this;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public GsxQuery setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
        return this;
    }

    public String getCrtTim() {
        return crtTim;
    }

    public GsxQuery setCrtTim(String crtTim) {
        this.crtTim = crtTim;
        return this;
    }

    public String getCode() {
        return code;
    }

    public GsxQuery setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public GsxQuery setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public GsxQuery setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
        return this;
    }


    public String getContent() {
        return content;
    }

    public GsxQuery setContent(String content) {
        this.content = content;
        return this;
    }
}
