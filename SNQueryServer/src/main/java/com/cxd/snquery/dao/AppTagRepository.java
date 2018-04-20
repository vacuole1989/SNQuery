package com.cxd.snquery.dao;

import com.cxd.snquery.bean.AppTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AppTagRepository extends CrudRepository<AppTag, String> {
    List<AppTag> findAppTagsByLoginName(String loginName);
}
