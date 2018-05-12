var config = require('../../config');
var locale = require('../../utils/locale.js');

const app = getApp()

Page({
    data: {
        sysData: {
            model: ''
        }
    },
    onShow:function(){
        wx.showShareMenu({
            withShareTicket: true
        })
        wx.setNavigationBarTitle({
            title: '手机真伪鉴定'
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
    onShareAppMessage: function (res) {
        return {
            title: '看看大家都在用什么手机？',
            success: function (res) {
                wx.showShareMenu({
                    // 要求小程序返回分享目标信息
                    withShareTicket: true
                });
            },
            fail: function (res) {
            }
        }
    },
    onLoad: function () {
        var _this = this;
        wx.showShareMenu({
            withShareTicket: true
        });
        wx.getSystemInfo({
            success: function (res) {
                var mm = res.model;
                if (mm.indexOf('<') > -1) {
                    res.model = mm.substring(0, mm.indexOf('<'));
                }
                res.language = locale[res.language];
                _this.setData({
                    sysData: res
                })
            }
        })
    }


})
