package com.cxd.snquery.bean;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author cxd
 */
public class JisuApiLogs implements Serializable {
    private static final long serialVersionUID = -6224978214696388335L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;
    private String queryType;

    public long getSeqId() {
        return seqId;
    }

    public JisuApiLogs setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }

    public String getQueryType() {
        return queryType;
    }

    public JisuApiLogs setQueryType(String queryType) {
        this.queryType = queryType;
        return this;
    }
}
