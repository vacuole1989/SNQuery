var config = require('../../config')
const app = getApp()

Page({
    data: {
        hasResult: false,
        snNumber: '',
        title: '',
        resData: {},
        itype: 'apple',
        isImei: false,
        btns: [],
        insImg: ''
    },
    onShareAppMessage: function (res) {
        return {
            title: '序列号查询',
            path: '/pages/index/index',
            imageUrl: '../share_bg.gif',
            success: function (res) {

                // 转发成功
            },
            fail: function (res) {

                // 转发失败
            }
        }
    },
    removeAllSpace: function (str) {
        return str.replace(/\s+/g, "");
    },
    S4: function () {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    },
    guid: function () {
        return (S4() + S4() + "" + S4() + "" + S4() + "" + S4() + "" + S4() + S4() + S4());
    },
    btnTag: function (event) {
        app.globalData.phone.snNumber = this.data.snNumber;
        app.globalData.phone.title = event.target.dataset.title;
        app.globalData.phone.itype = event.target.dataset.itype;
        wx.redirectTo({
            url: '/pages/search/index'
        });
    },
    replaceWithChinese: function (str) {
        var data = {};
        data["Jet Black"] = "亮黑色";
        data["Rose Gold"] = "玫瑰金色";
        data["Space Gray"] = "深空灰色";
        data["Gray"] = "灰色";
        data["Black"] = "黑色";
        data["White"] = "白色";
        data["Gold"] = "金色";
        data["Silver"] = "银色";
        data["Blue"] = "蓝色";
        data["Pink"] = "粉色";
        data["Green"] = "绿色";
        data["Yellow"] = "黄色";
        data["Red"] = "红色";
        for (var cc in data) {
            str = str.replace(cc + '', data[cc]);
        }
        return str;
    },
    replaceWithType: function (str) {
        var data = {};
        data["retail"] = "零售机";
        data["refurbished"] = "官换机";
        data["repaired"] = "官修机（官翻机）";
        data["exception"] = "异常机（翻新机）";
        data["display"] = "展示机";
        data["personalized"] = "定制机";
        for (var cc in data) {
            str = str.replace(cc + '', data[cc]);
        }
        return str;
    },
    searchTag: function () {
        var _this = this;
        _this.setData({
            snNumber: _this.removeAllSpace(_this.data.snNumber)
        });
        var sn = _this.data.snNumber;
        var regIMEI = new RegExp('^[0-9]{15}$');
        var regSN = new RegExp('^[0-9a-zA-z]{11,12}$');
        if (null === sn || '' === sn) {
            _this.showAlert('提示', '请输入序列号或IEMI码');
        } else if (regIMEI.test(sn) || regSN.test(sn)) {
            //统一下单接口开始
            wx.showLoading({
                title: '查询中'
            });
            _this.getUserCode();
        } else {
            _this.showAlert('序列号/IMEI码有误', '您输入的序列号/IEMI码错误，请核对后重试');
        }
    },
    getUserCode: function () {
        var _this = this;
        wx.login({
            success: res => {
                _this.undefierPay(res.code);
            }
        });
    },
    undefierPay: function (code) {
        var _this = this;
        wx.request({
            url: config.service.querypay,
            data: {
                itype: _this.data.itype,
                code: code
            },
            success: function (res) {
                if (res.data.success) {
                    var retData = res.data.message;
                    _this.payMoney(retData);
                } else {
                    _this.showAlert('接口调用失败', res.data.message.return_msg);
                }

            }
        });
    },
    payMoney: function (retData) {
        console.info(retData);
        var _this = this;
        wx.requestPayment({
            'timeStamp': retData.timeStamp,
            'nonceStr': retData.nonceStr,
            'package': retData.package,
            'signType': retData.signType,
            'paySign': retData.sign,
            'success': function (res) {
                _this.queryData(retData.nonceStr);
            },
            fail: function (res) {
                console.info(res);
                wx.hideLoading();
                _this.showAlert('提示', '支付失败，请重试。');
            }
        });
    },
    queryData: function (nonceStr) {
        var _this = this;
        var regIMEI = new RegExp('^[0-9]{15}$');
        var sn = _this.data.snNumber;
        var data = { 'sn': sn };
        if (regIMEI.test(sn)) {
            if ('serial' == _this.data.itype) {
                data = { 'imei': sn };
            }
            _this.setData({
                isImei: true
            });
        } else {
            _this.setData({
                isImei: false
            });
        }
        data['nonceStr'] = nonceStr;

        var tmpTyp = _this.data.itype;
        wx.showLoading({
            title: '查询中'
        });

        wx.login({
            success: coderes => {
                data['code'] = coderes.code;
                wx.request({
                    url: config.service[tmpTyp],
                    data: data,
                    success: function (res) {
                        if (res.data.success) {
                            var resCode = res.data.message.code;
                            var resMesData = res.data.message.data;

                            if (undefined == resCode || null == resCode || 0 == resCode || ('simlock' == tmpTyp && '302315' == resCode)) {
                                var resultData = ((undefined == resMesData || null == resMesData) ? res.data.message : resMesData);
                                if (undefined != resultData.color && null != resultData.color) {
                                    resultData.color = _this.replaceWithChinese(resultData.color);
                                }
                                if (undefined != resultData.model && null != resultData.model) {
                                    resultData.model = _this.replaceWithChinese(resultData.model);
                                }



                                if (tmpTyp == 'mpn') {
                                    if (undefined != resultData.product && null != resultData.product) {
                                        resultData.product = _this.replaceWithChinese(resultData.product);
                                    }
                                    if (undefined != resultData.type && null != resultData.type) {
                                        resultData.type = _this.replaceWithType(resultData.type);
                                    }
                                    _this.setData({
                                        resData: resultData,
                                        hasResult: true
                                    });
                                } else if (tmpTyp == 'gsx') {
                                    if (undefined != resultData && null != resultData) {
                                        resultData = _this.replaceWithChinese(resultData);
                                    }
                                    resultData = resultData.replace('：On', '：开启');
                                    resultData = resultData.replace('：Off', '：关闭');
                                    resultData = resultData.replace('：Clean', '：白名单');
                                    resultData = resultData.replace('：Lost', '：黑名单');
                                    resultData = resultData.replace('零件说明：', '零件说明，销售地：');

                                    var arrs=resultData.split('<br>');

                                    for (var i = arrs.length-1; i >=0; --i) {
                                        var arr = arrs[i];
                                        if(arr.indexOf('产品销售人')>-1){
                                            arrs.splice(i,1);
                                        }
                                        if(arr.indexOf('网络锁')>-1){
                                            arrs.splice(i,1);
                                        }

                                    }

                                    _this.setData({
                                        resData: resultData.split('<br>'),
                                        hasResult: true
                                    });

                                } else {
                                    _this.setData({
                                        resData: resultData,
                                        hasResult: true
                                    });
                                }


                                // _this.setData({
                                //     resData: resultData,
                                //     hasResult: true
                                // });
                            } else {

                                _this.showAlert('提示', (res.data.message.message).replace('[2018-01-03]', ''));
                            }
                        }
                        wx.hideLoading();
                    },
                    fail: function (res) {

                        wx.hideLoading();
                        _this.showAlert('提示', '查询失败');
                    }
                });

            }
        })



    },
    showAlert: function (title, text) {
        wx.showModal({
            title: title,
            content: text,
            showCancel: false,
            confirmText: '知道了',
            success: function (res) {

            }
        });
    },
    inputSN: function (e) {
        this.data.snNumber = e.detail.value;
    },
    onLoad: function () {
        this.setData({ snNumber: app.globalData.phone.snNumber });
        this.setData({ title: app.globalData.phone.title });
        this.setData({ itype: app.globalData.phone.itype });
        this.setData({ btns: app.globalData.init.btns });
        this.setData({ insImg: app.globalData.init.insImg });
    }
})
