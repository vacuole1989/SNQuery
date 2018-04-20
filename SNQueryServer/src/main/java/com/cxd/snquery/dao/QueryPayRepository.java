package com.cxd.snquery.dao;

import com.cxd.snquery.bean.QueryPay;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QueryPayRepository extends CrudRepository<QueryPay, Long> {

    List<QueryPay> getQueryPayByNonceStr(String nonceStr);

}
