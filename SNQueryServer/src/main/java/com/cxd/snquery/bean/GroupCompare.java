package com.cxd.snquery.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class GroupCompare implements Serializable {
    private static final long serialVersionUID = 8600778640172482629L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;
    private String appId;
    private String openGId;
    private String phone;

    public long getSeqId() {
        return seqId;
    }

    public GroupCompare setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

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
}
