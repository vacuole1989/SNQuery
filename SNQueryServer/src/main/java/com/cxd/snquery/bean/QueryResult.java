package com.cxd.snquery.bean;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class QueryResult implements Serializable {
    private static final long serialVersionUID = -5872161912170942843L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long seqId;
    private String itype;
    private String query;
    private String sn;
    @Column(length = 4000)
    private String result;

    public String getResult() {
        return result;
    }

    public QueryResult setResult(String result) {
        this.result = result;
        return this;
    }

    public String getSn() {
        return sn;
    }

    public QueryResult setSn(String sn) {
        this.sn = sn;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public QueryResult setQuery(String query) {
        this.query = query;
        return this;
    }

    public String getItype() {
        return itype;
    }

    public QueryResult setItype(String itype) {
        this.itype = itype;
        return this;
    }

    public long getSeqId() {
        return seqId;
    }

    public QueryResult setSeqId(long seqId) {
        this.seqId = seqId;
        return this;
    }
}
