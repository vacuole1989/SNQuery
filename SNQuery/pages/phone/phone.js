var config = require('../../config')
const app = getApp()

Page({
    data: {
        hasResult: false,
        phoneNbr: '',
        resData: {}
    },
    onShow: function () {
        wx.setNavigationBarTitle({
            title: '手机号测凶吉'
        })
    },
    removeAllSpace: function (str) {
        return str.replace(/\s+/g, "");
    },
    searchTag: function () {
        var _this = this;
        this.setData({
            phoneNbr: this.removeAllSpace(this.data.phoneNbr)
        });
        var sn = this.data.phoneNbr;
        var regSN = new RegExp('^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$');
        if (null === sn || '' === sn) {
            this.showAlert('提示', '请输入手机号码');
        } else if (regSN.test(sn)) {
            wx.showLoading({
                title: '查询中'
            });
            var data = {
                'sn': sn,
                'query': 'mobile'
            };
            wx.request({
                url: config.service['mobileluck'],
                data: data,
                success: function (res) {
                    console.info(res);
                    wx.hideLoading();
                    if (res.data.success) {
                        _this.setData({
                            hasResult: true,
                            resData: res.data.data.result
                        });

                    } else {
                        _this.showAlert('错误', '查询失败，请稍后重试。');
                    }
                }
            })
        } else {
            _this.showAlert('手机号有误', '您输入的手机号码错误，请核对后重试');
        }
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
    inputNbr: function (e) {
        this.data.phoneNbr = e.detail.value;
    },
    onShareAppMessage: function (res) {
        return {
            title: '序列号查询',
            path: '/pages/index/index',
            imageUrl: '../share_bg.gif',
            success: function (res) {
            },
            fail: function (res) {
            }
        }
    },
    onLoad: function () {

    }


})
