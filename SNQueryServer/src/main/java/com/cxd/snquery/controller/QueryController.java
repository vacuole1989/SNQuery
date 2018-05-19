package com.cxd.snquery.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cxd.snquery.BizException;
import com.cxd.snquery.bean.*;
import com.cxd.snquery.dao.*;
import com.cxd.snquery.dto.JSONResult;
import com.cxd.snquery.util.*;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;


@RestController
@RequestMapping("/app")
public class QueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryController.class);
    private static final String SESSION_KEY = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private QueryLogsRepository queryLogsRepository;
    @Autowired
    private QueryPayRepository queryPayRepository;
    @Autowired
    private ActivationlockRepository activationlockRepository;
    @Autowired
    private AppraisalRepository appraisalRepository;
    @Autowired
    private CnRepository cnRepository;
    @Autowired
    private IcloudRepository icloudRepository;
    @Autowired
    private IndexRepository indexDao;
    @Autowired
    private RepairRepository repairRepository;
    @Autowired
    private SerialRepository serialRepository;
    @Autowired
    private SimlockRepository simlockRepository;
    @Autowired
    private SoldRepository soldRepository;
    @Autowired
    private QueryPayLogsRepository queryPayLogsRepository;
    @Autowired
    private MpnRepository mpnRepository;
    @Autowired
    private AppTagRepository appTagRepository;
    @Autowired
    private FeeEnumRepository feeEnumRepository;
    @Autowired
    private GsxRepository gsxRepository;
    @Autowired
    private InsImgRepository insImgRepository;
    @Autowired
    private GroupCompareRepository groupCompareRepository;

    @RequestMapping("/{appid}/{itype}")
    @ResponseBody
    public Object apple(@PathVariable(value = "appid") String appid, @PathVariable(value = "itype") String itype, String sn, String imei, String nonceStr, String code, HttpServletRequest request) {
        ConstantUtil constantUtil = new ConstantUtil(appid, appTagRepository);


        Map map = restTemplate.getForObject("{sessionkey}?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code", Map.class, SESSION_KEY, constantUtil.getAppId(), constantUtil.getAppSecret(), code);
        Object openid = map.get("openid");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("key", constantUtil.getKey3023());
        HttpEntity requestEntity = new HttpEntity(null, requestHeaders);
        String stype = (null == imei) ? "sn" : "imei";
        String value = (null == imei) ? sn : imei;

        String url = ("mpn".equalsIgnoreCase(itype) || "gsx".equalsIgnoreCase(itype)) ? constantUtil.getApi3023data() : constantUtil.getApi3023();

        ResponseEntity<String> exchange = this.restTemplate.exchange("gsx".equalsIgnoreCase(itype) ? "{url}{type}?{stype}={value}&app=details&lang=zh" : "{url}{type}?{stype}={value}", HttpMethod.GET, requestEntity, String.class, url, itype, stype, value);
        String body = "{}";
        try {
            body = new String(exchange.getBody().getBytes("iso8859-1"), "utf8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }
        String ip = IpUtil.getIpAddress(request);
        QueryLogs queryLogs = new QueryLogs();
        queryLogs.setOpenId(null == openid ? null : openid.toString());
        queryLogs.setCrtTim(DateUtil.format(new Date()));
        queryLogs.setImei(imei);
        queryLogs.setSn(sn);
        queryLogs.setIpAddr(ip);
        queryLogs.setReturnBody(body);
        queryLogs.setNonceStr(nonceStr);
        queryLogs.setUrl(url + itype + "?" + stype + "=" + value);
        queryLogs.setItype(itype);
        queryLogs.setIsn(null == imei);
        queryLogs.setAppId(appid);


        JSONObject jsonObject = JSON.parseObject(body);
        if ("apple".equalsIgnoreCase(itype) || "imei".equalsIgnoreCase(itype)) {
            if (StringUtils.isEmpty(jsonObject.get("code"))) {
                indexDao.save(new IndexQuery()
                        .setCapacity(jsonObject.get("capacity") + "")
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setColor(jsonObject.get("color") + "")
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("color") + "")
                        .setReturnBody(body)
                        .setSn(stype.equals("sn") ? value : ""));
                queryLogs.setModel(jsonObject.get("color") + "");

            } else {
                indexDao.save(new IndexQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("serial".equalsIgnoreCase(itype)) {
            if (StringUtils.isEmpty(jsonObject.get("code"))) {
                serialRepository.save(new SerialQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                serialRepository.save(new SerialQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("appraisal".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                appraisalRepository.save(new AppraisalQuery().setApplecare(jsonObject.get("applecare") + "")
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setDaysleft(jsonObject.get("daysleft") + "")
                        .setImei(stype.equals("sn") ? "" : value)
                        .setIpAddr(ip)
                        .setManufactureFactory(JSON.parseObject(jsonObject.get("manufacture") + "").get("factory") + "")
                        .setPurchaseDate(JSON.parseObject(jsonObject.get("purchase") + "").get("date") + "")
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setSn(stype.equals("sn") ? value : "")
                        .setStatus(jsonObject.get("status") + "")
                        .setStorage(jsonObject.get("storage") + "")
                        .setSupport(jsonObject.get("support") + "")
                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                appraisalRepository.save(new AppraisalQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("cn".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                cnRepository.save(new CnQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setCoverage(jsonObject.get("coverage") + "")
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setPurchaseCountry(JSON.parseObject(jsonObject.get("purchase") + "").get("country") + "")
                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                cnRepository.save(new CnQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("activationlock".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                activationlockRepository.save(new ActivationlockQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setLocked(jsonObject.get("locked") + "")
                        .setTime(jsonObject.get("time") + "")

                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                activationlockRepository.save(new ActivationlockQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("icloud".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                icloudRepository.save(new IcloudQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setIcloud(jsonObject.get("icloud") + "")
                        .setLocked(jsonObject.get("locked") + "")

                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                icloudRepository.save(new IcloudQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("repair".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                repairRepository.save(new RepairQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setSales(jsonObject.get("sales") + "")
                        .setStatus(jsonObject.get("status") + "")

                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                repairRepository.save(new RepairQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("sold".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                soldRepository.save(new SoldQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setPurchaseCountry(JSON.parseObject(jsonObject.get("purchase") + "").get("country") + "")
                        .setStorage(jsonObject.get("storage") + "")
                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                soldRepository.save(new SoldQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }

        } else if ("simlock".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                simlockRepository.save(new SimlockQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setModel(jsonObject.get("model") + "")
                        .setReturnBody(body)
                        .setSimlock(jsonObject.get("simlock") + "")

                );
                queryLogs.setModel(jsonObject.get("model") + "");
            } else {
                simlockRepository.save(new SimlockQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("mpn".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                jsonObject = JSON.parseObject(jsonObject.get("data") + "");
                mpnRepository.save(new MpnQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setIpAddr(ip)
                        .setReturnBody(body)
                        .setProduct(jsonObject.get("product") + "")
                        .setMpn(jsonObject.get("mpn") + "")
                        .setType(jsonObject.get("type") + "")
                        .setCountry(JSON.parseObject(jsonObject.get("country") + "").get("zh") + "")

                );
                queryLogs.setModel(jsonObject.get("product") + "");
            } else {
                mpnRepository.save(new MpnQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        } else if ("gsx".equalsIgnoreCase(itype)) {
            if ("0".equals(jsonObject.get("code") + "")) {
                gsxRepository.save(new GsxQuery()
                        .setCode("0")
                        .setNonceStr(nonceStr)
                        .setCrtTim(DateUtil.format(new Date()))
                        .setImei(stype.equals("sn") ? "" : value)
                        .setSn(stype.equals("sn") ? value : "")
                        .setContent(jsonObject.get("data") + "")
                        .setIpAddr(ip)
                        .setReturnBody(body)

                );
                String aa = jsonObject.get("data") + "";

                String[] zz = aa.split("<br>");
                for (String s : zz) {
                    if (s.indexOf("型号") > -1) {
                        queryLogs.setModel(s.replace("型号：", ""));
                    }
                    if (s.indexOf("零件说明") > -1) {
                        queryLogs.setModel(s.replace("零件说明：", ""));
                    }
                }
            } else {
                gsxRepository.save(new GsxQuery().setCode(jsonObject.get("code") + "").setMessage(jsonObject.get("message") + ""));
            }
        }

        queryLogsRepository.save(queryLogs);

        JSONObject checkbody = JSON.parseObject(body);
        if (!"apple".equals(itype) && !"imei".equals(itype)) {
            List<QueryPay> queryPayByNonceStr = queryPayRepository.getQueryPayByNonceStr(nonceStr);
            if (!StringUtils.isEmpty(checkbody.get("message"))) {
                if ("302311".equals(checkbody.get("code") + "") || "302312".equals(checkbody.get("code") + "") || "302313".equals(checkbody.get("code") + "") || "302314".equals(checkbody.get("code") + "") || (checkbody.get("message") + "").contains("不扣费")) {
                    CommonUtil.refund(queryPayByNonceStr, constantUtil);
                }
            }
            boolean aaa = ("serial".equalsIgnoreCase(itype) && StringUtils.isEmpty(checkbody.get("code"))) || (!"serial".equalsIgnoreCase(itype) && "0".equals(checkbody.get("code") + "")) || (!"simlock".equalsIgnoreCase(itype) && "302315".equals(checkbody.get("code") + ""));
            if (aaa) {
                if (queryPayByNonceStr.size() > 0) {
                    FeeEnum feeEnum1 = new FeeEnum(appid, feeEnumRepository);
                    QueryPay queryPay = queryPayByNonceStr.get(0);
                    FeeEnum feeByType = feeEnum1.getFeeByType(itype);
                    CommonUtil.sendTemplate(restTemplate, queryPay.getTotalFee(), "序列号查询-" + feeByType.getName(), sn, queryPay.getOpenid(), queryPay.getOutTradeNo(), queryPay.getPrepayId(), nonceStr, constantUtil);
                }
            }
        }

        return new JSONResult(true, JSON.parseObject(body));
    }


    @RequestMapping("/{appid}/queryhistory")
    @ResponseBody
    public Object queryhistory(@PathVariable(value = "appid") String appid, String nonceStr) {
        List<QueryLogs> queryLogs = queryLogsRepository.getQueryLogsByNonceStr(nonceStr);
        if (queryLogs.size() > 0) {
            QueryLogs queryLogs1 = queryLogs.get(0);
            if ((DateUtil.format(queryLogs1.getCrtTim()).getTime() + 48 * 60 * 60 * 1000L) < System.currentTimeMillis()) {
                return new JSONResult(false, "数据已过期，请重新查询。");
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("success", true);
                map.put("isn", queryLogs1.isIsn());
                map.put("itype", queryLogs1.getItype());
                map.put("data", JSON.parseObject(queryLogs1.getReturnBody()));
                return map;
            }
        }
        return null;
    }

    @RequestMapping("/{appid}/queryhistorylist")
    @ResponseBody
    public Object queryhistorylist(@PathVariable(value = "appid") String appid, String code) {

        ConstantUtil constantUtil = new ConstantUtil(appid, appTagRepository);
        Map map = restTemplate.getForObject("{sessionkey}?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code", Map.class, SESSION_KEY, constantUtil.getAppId(), constantUtil.getAppSecret(), code);
        Object openid = map.get("openid");
        if (null != openid) {
            List<QueryLogs> queryLogs = queryLogsRepository.getQueryLogsByOpenIdAndNonceStrNotNullOrderByCrtTimDesc(openid.toString());
            return new JSONResult(true, "查询成功", queryLogs);
        }


        return null;
    }

    @RequestMapping("/{appid}/initBtns")
    @ResponseBody
    public Object initBtns(@PathVariable(value = "appid") String appid) {
        FeeEnum feeEnum1 = new FeeEnum(appid, feeEnumRepository);
        AppTag appTag = appTagRepository.findOne(appid);
        List<FeeEnum> feeEnums = feeEnum1.validateSwitchNotAll(appTag);
        InsImg one = insImgRepository.findOne(appid);
        return new JSONResult(true, feeEnums, one);
    }

    @RequestMapping("/{appid}/unifiedorder")
    @ResponseBody
    public Object querypay(HttpServletRequest request, @PathVariable(value = "appid") String appid, String itype, String code) {
        ConstantUtil constantUtil = new ConstantUtil(appid, appTagRepository);
        FeeEnum feeEnum1 = new FeeEnum(appid, feeEnumRepository);
        String totalFee = feeEnum1.getFeeByType(itype).getFee();
        String description = constantUtil.getPayDesc();
        String ip = IpUtil.getIpAddress(request);
        Object obj = restTemplate.getForObject("{sessionkey}?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code", Object.class, constantUtil.getGetSessionKeyUrl(), constantUtil.getAppId(), constantUtil.getAppSecret(), code);
        String openid = (String) JSON.parseObject(JSON.toJSONString(obj), Map.class).get("openid");

        String outTradeNo = DateUtil.format(new Date(), "yyyyMMddHHmmssSS") + UUID.randomUUID().toString().substring(0, 5);

        String url = constantUtil.getUnifiedorderUrl();
        String xml = CommonUtil.WXParamGenerate(description, outTradeNo, totalFee, ip, openid, constantUtil);
        String res = HttpUtil.httpsRequest(url, "POST", xml);


        try {
            QueryPayLogs queryPayLogs = new QueryPayLogs();
            queryPayLogs.setCrtTim(DateUtil.format(new Date()));
            queryPayLogs.setIpAddr(ip);
            queryPayLogs.setSendXml(xml);
            queryPayLogs.setUrl(url);
            queryPayLogs.setDescription(description);
            queryPayLogs.setOutTradeNo(outTradeNo);
            queryPayLogs.setTotalFee(totalFee);
            queryPayLogs.setReturnBody(res);
            queryPayLogsRepository.save(queryPayLogs);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


        Map<String, String> data;
        try {
            data = CommonUtil.doXMLParse(res);
        } catch (Exception e) {
            throw new BizException("订单提交失败，请重试");
        }


        String noneceStr = CommonUtil.GetNonceStr();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        try {
            QueryPay querypay = new QueryPay();
            querypay.setCode(code)
                    .setDescription(description)
                    .setIp(ip)
                    .setNonceStr(noneceStr)
                    .setOpenid(openid)
                    .setOutTradeNo(outTradeNo)
                    .setPrepayId(data.get("prepay_id"))
                    .setReciveXml(res)
                    .setResultCode(data.get("result_code"))
                    .setReturnCode(data.get("return_code"))
                    .setReturnMsg(data.get("return_msg"))
                    .setSendXml(xml)
                    .setTimeStamp(timeStamp)
                    .setCrtTim(DateUtil.format(new Date()))
                    .setTotalFee(totalFee);
            queryPayRepository.save(querypay);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            /**
             * 防止数据库插入错误影响查询。
             */
        }
       // LOGGER.info(DateUtil.format(new Date()) + " 统一下单发送成功。");
        if (data.get("return_code").equals("SUCCESS") && data.get("result_code").equals("SUCCESS")) {
            Map<String, String> param = new HashMap<>();
            param.put("appId", constantUtil.getAppId());
            param.put("timeStamp", timeStamp);
            param.put("nonceStr", noneceStr);
            param.put("package", "prepay_id=" + data.get("prepay_id"));
            param.put("signType", "MD5");

            String sign = CommonUtil.GetSign(param, constantUtil);

            param.put("sign", sign);
            param.put("outTradeNo", outTradeNo);

            return new JSONResult(true, param);
        } else {
            return new JSONResult(false, data);
        }


    }


    @RequestMapping("/{appid}/refund")
    @ResponseBody
    public void refund(@PathVariable(value = "appid") String appid, String nonceStr) {
        List<QueryPay> queryPayByNonceStr = queryPayRepository.getQueryPayByNonceStr(nonceStr);
        CommonUtil.refund(queryPayByNonceStr, new ConstantUtil(appid, appTagRepository));
    }

    @RequestMapping("/{appid}/group/groupid")
    @ResponseBody
    public Object getGroupId(@PathVariable(value = "appid") String appid, @RequestBody Map map) {
        String encryptedData = map.get("encryptedData") + "";
        String iv = map.get("iv") + "";
        String code = map.get("code") + "";
        String avatarUrl = map.get("avatarUrl") + "";
        String phone = map.get("phone") + "";
        String nickName = map.get("nickName") + "";
        ConstantUtil constantUtil = new ConstantUtil(appid, appTagRepository);
        Map usermap = restTemplate.getForObject("{sessionkey}?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code", Map.class, SESSION_KEY, constantUtil.getAppId(), constantUtil.getAppSecret(), code);
        String session_key = usermap.get("session_key") + "";
        String openid = usermap.get("openid") + "";

        try {
            Gson gson = new Gson();
            byte[] resultByte = AES.decrypt(Base64.decodeBase64(encryptedData),
                    Base64.decodeBase64(session_key),
                    Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                String userInfo = new String(resultByte, "UTF-8");
                String openGId = gson.fromJson(userInfo, Map.class).get("openGId") + "";
                List<GroupCompare> byOpenGIdAndOpenId = groupCompareRepository.findByOpenGIdAndOpenId(openGId, openid);
                if (byOpenGIdAndOpenId.size() > 0) {
                    GroupCompare groupCompare = byOpenGIdAndOpenId.get(0);
                    groupCompare.setPhone(phone);
                    groupCompare.setCrtTim(DateUtil.format(new Date()));
                    groupCompareRepository.save(groupCompare);
                } else {
                    groupCompareRepository.save(
                            new GroupCompare()
                                    .setAppId(appid)
                                    .setOpenGId(openGId)
                                    .setPhone(phone)
                                    .setAvatarUrl(avatarUrl)
                                    .setOpenId(openid)
                                    .setNickName(nickName)
                                    .setCrtTim(DateUtil.format(new Date())));
                }
                List<GroupCompare> byOpenGId = groupCompareRepository.findByOpenGId(openGId);
                return new JSONResult(true, "查询成功", byOpenGId);
            }
        } catch (InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }

        return new JSONResult(false, "查询失败");

    }

    @RequestMapping("/{appid}/group/hasgroupid")
    @ResponseBody
    public Object hasGroupId(@PathVariable(value = "appid") String appid, @RequestBody Map map) {
        String encryptedData = map.get("encryptedData") + "";
        String iv = map.get("iv") + "";
        String code = map.get("code") + "";
        String phone = map.get("phone") + "";
        ConstantUtil constantUtil = new ConstantUtil(appid, appTagRepository);
        Map usermap = restTemplate.getForObject("{sessionkey}?appid={APPID}&secret={SECRET}&js_code={JSCODE}&grant_type=authorization_code", Map.class, SESSION_KEY, constantUtil.getAppId(), constantUtil.getAppSecret(), code);
        String session_key = usermap.get("session_key") + "";
        String openid = usermap.get("openid") + "";

        try {
            Gson gson = new Gson();
            byte[] resultByte = AES.decrypt(Base64.decodeBase64(encryptedData),
                    Base64.decodeBase64(session_key),
                    Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                String userInfo = new String(resultByte, "UTF-8");
                String openGId = gson.fromJson(userInfo, Map.class).get("openGId") + "";
                List<GroupCompare> byOpenGIdAndOpenId = groupCompareRepository.findByOpenGIdAndOpenId(openGId, openid);
                if (byOpenGIdAndOpenId.size() > 0) {
                    GroupCompare groupCompare = byOpenGIdAndOpenId.get(0);
                    groupCompare.setPhone(phone);
                    groupCompare.setCrtTim(DateUtil.format(new Date()));
                    groupCompareRepository.save(groupCompare);
                    List<GroupCompare> byOpenGId = groupCompareRepository.findByOpenGId(openGId);
                    return new JSONResult(true, "查询成功", byOpenGId);
                }
            }
        } catch (InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
        }

        return new JSONResult(false, "查询失败");

    }

    @RequestMapping("/notify")
    @ResponseBody
    public void notify(HttpServletRequest request) {
        try {
            String s = stream2String(request.getInputStream());
            LOGGER.info(s);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private String stream2String(ServletInputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
        StringBuffer sb = new StringBuffer("");
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        br.close();
        return sb.toString();
    }

}
