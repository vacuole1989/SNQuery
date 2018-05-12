package com.cxd.snquery.dao;

import com.cxd.snquery.bean.QueryLogs;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QueryLogsRepository extends CrudRepository<QueryLogs, Long> {
    List<QueryLogs> getQueryLogsByNonceStr(String nonceStr);

    List<QueryLogs> getQueryLogsByOpenIdAndNonceStrNotNullOrderByCrtTimDesc(String openId);
}
