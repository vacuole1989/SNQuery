package com.cxd.snquery.controller;

import com.cxd.snquery.bean.QueryResult;
import com.cxd.snquery.dao.QueryLogsRepository;
import com.cxd.snquery.dao.QueryResultRepository;
import com.cxd.snquery.dto.JSONResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/appapi")
public class JisuApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(JisuApiController.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private QueryResultRepository queryResultRepository;


    public static final String APPKEY = "e395df2ecd6b6036";
    public static final String JSAPI = "http://api.jisuapi.com/";


    private String luckApiGet(String itype, String query, String value) {
        String url = JSAPI + itype + "/query" + "?" + query + "=" + value + "&appkey=" + APPKEY;
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }


    @RequestMapping("/{appid}/{itype}")
    @ResponseBody
    public Object apple(@PathVariable(value = "appid") String appid, @PathVariable(value = "itype") String itype, String sn, String query, HttpServletRequest request) throws UnsupportedEncodingException {
        sn = new String(sn.getBytes("iso8859-1"), "utf8");

        List<QueryResult> bySnAndItypeAndQuery = queryResultRepository.findBySnAndItypeAndQuery(sn, itype, query);
        String ss;
        if (bySnAndItypeAndQuery.size() > 0) {
            ss = bySnAndItypeAndQuery.get(0).getResult();
        } else {
            ss = luckApiGet(itype, query, sn);
            try {
                queryResultRepository.save(new QueryResult().setItype(itype).setQuery(query).setResult(ss).setSn(sn));
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }


        if (null == ss) {
            return new JSONResult(false, "查询失败");
        } else {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Map map = gson.fromJson(ss, Map.class);
            return new JSONResult(true, "查询成功", map);
        }
    }


}
