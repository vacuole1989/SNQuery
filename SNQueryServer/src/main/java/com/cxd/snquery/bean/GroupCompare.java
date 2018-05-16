package com.cxd.snquery.bean;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"openId", "openGId"})})
public class GroupCompare implements Serializable {
    private static final long serialVersionUID = 8600778640172482629L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;
    private String openId;
    private String openGId;
    private String appId;
    private String phone;
    private String avatarUrl;
    private String crtTim;
    private String nickName;

    public String getAppId() {
        return appId;
    }

    public GroupCompare setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getOpenGId() {
        return openGId;
    }

    public GroupCompare setOpenGId(String openGId) {
        this.openGId = openGId;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public GroupCompare setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCrtTim() {
        return crtTim;
    }

    public GroupCompare setCrtTim(String crtTim) {
        this.crtTim = crtTim;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public GroupCompare setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getOpenId() {
        return openId;
    }

    public GroupCompare setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public long getSeqId() {
        return seqId;
    }

    public GroupCompare setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public GroupCompare setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }
}
