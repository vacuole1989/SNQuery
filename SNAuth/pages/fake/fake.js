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
    onShow: function (ops) {
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
    bindGetUserInfo: function (e) {
        var _this = this;
        //getUserInfo:fail auth deny
        if (e.detail.errMsg == 'getUserInfo:ok') {
            _this.setData({
                showAuth: false
            })
            wx.getSystemInfo({
                success: function (ress) {
                    var mm = ress.model;
                    if (mm.indexOf('<') > -1) {
                        ress.model = mm.substring(0, mm.indexOf('<'));
                    }
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
                                            avatarUrl: e.detail.userInfo.avatarUrl,
                                            phone: ress.model,
                                            nickName: e.detail.userInfo.nickName
                                        },
                                        header: {
                                            'content-type': 'application/json' // 默认值
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
            })
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
                    ress.screenHeight = ress.screenHeight * ress.pixelRatio;
                    ress.screenWidth = ress.screenWidth * ress.pixelRatio;
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
                                                    'content-type': 'application/json' // 默认值
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
