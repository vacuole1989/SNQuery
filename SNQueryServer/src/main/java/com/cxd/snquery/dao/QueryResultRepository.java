package com.cxd.snquery.dao;

import com.cxd.snquery.bean.QueryResult;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QueryResultRepository extends CrudRepository<QueryResult, Long> {

    List<QueryResult> findBySnAndItypeAndQuery(String sn,String itype,String query);


}
