package com.cxd.snquery.bean;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class MpnQuery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;

    private String sn;

    private String imei;

    private String url;

    private String product;
    private String type;
    private String mpn;
    private String country;

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

    public MpnQuery setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public MpnQuery setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public MpnQuery setImei(String imei) {
        this.imei = imei;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public MpnQuery setUrl(String url) {
        this.url = url;
        return this;
    }


    public String getReturnBody() {
        return returnBody;
    }

    public MpnQuery setReturnBody(String returnBody) {
        this.returnBody = returnBody;
        return this;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public MpnQuery setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
        return this;
    }

    public String getCrtTim() {
        return crtTim;
    }

    public MpnQuery setCrtTim(String crtTim) {
        this.crtTim = crtTim;
        return this;
    }

    public String getCode() {
        return code;
    }

    public MpnQuery setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MpnQuery setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public MpnQuery setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public MpnQuery setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getType() {
        return type;
    }

    public MpnQuery setType(String type) {
        this.type = type;
        return this;
    }

    public String getMpn() {
        return mpn;
    }

    public MpnQuery setMpn(String mpn) {
        this.mpn = mpn;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public MpnQuery setCountry(String country) {
        this.country = country;
        return this;
    }
}
