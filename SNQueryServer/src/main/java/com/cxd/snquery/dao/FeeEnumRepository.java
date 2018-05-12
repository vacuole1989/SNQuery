package com.cxd.snquery.dao;

import com.cxd.snquery.bean.FeeEnum;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeeEnumRepository extends CrudRepository<FeeEnum, Long> {

    List<FeeEnum> findFeeEnumsByAppIdOrderByOrderNoAsc(String appId);

}
