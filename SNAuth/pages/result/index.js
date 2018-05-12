var config = require('../../config')
const app = getApp()
Page({

    /**
     * 页面的初始数据
     */
    data: {
        itype: null,
        resData: {},
        isImei: false,
        hasResult: false
    },
    showAlert: function (title, text) {
        wx.showModal({
            title: title,
            content: text,
            showCancel: false,
            confirmText: '知道了',
            success: function (res) {
                wx.switchTab({
                    url: '/pages/index/index'
                });
            }
        });
    },
    btnToIndex: function () {
        wx.switchTab({
            url: '/pages/index/index'
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
    onLoad: function (options) {
        wx.showLoading({
            title: '数据加载中……',
        })
        var _this = this;
        wx.request({
            url: config.service.queryhistory,
            data: {
                nonceStr: options.str
            },
            success: function (res) {
                if (res.data.success) {
                    
                    var resultData = (null == res.data.data.code ? res.data.data : res.data.data.data);
                    if (undefined != resultData.color && null != resultData.color) {
                        resultData.color = _this.replaceWithChinese(resultData.color);
                    }
                    if (undefined != resultData.model && null != resultData.model) {
                        resultData.model = _this.replaceWithChinese(resultData.model);
                    }
                    
                    if (res.data.itype == 'mpn') {
                        if (undefined != resultData.product && null != resultData.product) {
                            resultData.product = _this.replaceWithChinese(resultData.product);
                        }
                        if (undefined != resultData.type && null != resultData.type) {
                            resultData.type = _this.replaceWithType(resultData.type);
                        }
                        _this.setData({
                            itype: res.data.itype,
                            isImei: !res.data.isn,
                            hasResult: true,
                            resData: resultData
                        });
                    }else if (res.data.itype == 'gsx') {
                        if (undefined != resultData && null != resultData) {
                            resultData = _this.replaceWithChinese(resultData);
                        }
                        resultData = resultData.replace('：On', '：开启');
                        resultData = resultData.replace('：Off', '：关闭');
                        resultData = resultData.replace('：Clean', '：白名单');
                        resultData = resultData.replace('：Lost', '：黑名单');
                        _this.setData({
                            itype: res.data.itype,
                            isImei: !res.data.isn,
                            hasResult: true,
                            resData: resultData.split('<br>')
                        });
                    } else {
                        _this.setData({
                            itype: res.data.itype,
                            isImei: !res.data.isn,
                            hasResult: true,
                            resData: resultData
                        });
                    }


                } else {
                    _this.showAlert('提示', res.data.message);
                }
                wx.hideLoading();
            }
        });
    }

})
