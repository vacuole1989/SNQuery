package com.cxd.snquery.dao;

import com.cxd.snquery.bean.GroupCompare;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupCompareRepository extends CrudRepository<GroupCompare, String> {

    List<GroupCompare> findByOpenGIdAndOpenId(String openGId,String openId);
    List<GroupCompare> findByOpenGId(String openGId);



}
