var config = require('../../config');
var locale = require('../../utils/locale.js');
const app = getApp()

Page({
    data: {
        sysData: {
            model: ''
        },
        compList: [],
        showList: false,
        show: false,
        userAuth: true,
        showAuth: false
    },
    onShow: function () {
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
        if (null != app.globalData.ticket) {
            wx.getSystemInfo({
                success: function (ress) {
                    var mm = ress.model;
                    if (mm.indexOf('<') > -1) {
                        ress.model = mm.substring(0, mm.indexOf('<'));
                    }
                    wx.getUserInfo({
                        success: function (resu) {
                            wx.login({
                                success: resl => {
                                    app.globalData.code = resl.code;
                                    wx.getShareInfo({
                                        shareTicket: app.globalData.ticket,
                                        success(res) {
                                            wx.request({
                                                method: 'POST',
                                                url: config.service.GetGroupId,
                                                data: {
                                                    code: resl.code,
                                                    encryptedData: res.encryptedData,
                                                    iv: res.iv,
                                                    avatarUrl: resu.userInfo.avatarUrl,
                                                    phone: ress.model,
                                                    nickName: resu.userInfo.nickName
                                                },
                                                header: {
                                                    'content-type': 'application/json'
                                                },
                                                success: function (res) {
                                                    if (res.data.success) {
                                                        _this.setData({
                                                            show: true,
                                                            showList: true,
                                                            compList: res.data.data
                                                        })
                                                    } else {
                                                        _this.setData({
                                                            show: true,
                                                            showList: false
                                                        })
                                                    }
                                                }
                                            })
                                        },
                                        fail() { },
                                        complete() { }
                                    });
                                }
                            })
                        }
                    });
                }
            })
        } else {
            _this.setData({
                show: true
            })
        }
        _this.setData({
            showAuth: app.globalData.showAuth
        })
        wx.getSetting({
            success(res) {
                if (res.authSetting['scope.userInfo']) {
                    _this.setData({
                        userAuth: true
                    })
                } else {
                    _this.setData({
                        userAuth: false
                    })
                }
            }
        })
        wx.getSystemInfo({
            success: function (res) {
                var mm = res.model;
                if (mm.indexOf('<') > -1) {
                    res.model = mm.substring(0, mm.indexOf('<'));
                }
                res.screenHeight = res.screenHeight * res.pixelRatio;
                res.screenWidth = res.screenWidth * res.pixelRatio;
                res.language = locale[res.language];
                _this.setData({
                    sysData: res
                })
            }
        })
    }


})
