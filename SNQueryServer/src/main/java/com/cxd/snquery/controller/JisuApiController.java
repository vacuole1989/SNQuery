package com.cxd.snquery.controller;

import com.cxd.snquery.dao.AppTagRepository;
import com.cxd.snquery.dto.JSONResult;
import com.cxd.snquery.util.ConstantUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/appapi")
public class JisuApiController {

    @Autowired
    private RestTemplate restTemplate;


    public static final String APPKEY = "e395df2ecd6b6036";
    public static final String JSAPI = "http://api.jisuapi.com/";


    private String luckApiGet(String itype, String query, String value) {
        String url = JSAPI + itype + "/query" + "?" + query + "=" + value + "&appkey=" + APPKEY;
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping("/{appid}/{itype}")
    @ResponseBody
    public Object apple(@PathVariable(value = "appid") String appid, @PathVariable(value = "itype") String itype, String sn, String query, HttpServletRequest request) {

        String ss = luckApiGet(itype, query, sn);

        if (null == ss) {
            return new JSONResult(false, "查询失败");
        } else {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            Map map = gson.fromJson(ss, Map.class);
            return new JSONResult(true, "查询成功", map);
        }
    }


}
