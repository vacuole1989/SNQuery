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
                    _this.initLogin(ress.model,true,e.detail.userInfo.avatarUrl,e.detail.userInfo.nickName)
                }
            })
        }
    },
    GetGroupIdFnc:function(code,encryptedData,iv,model,avatarUrl,nickName){
        var _this = this;
        wx.request({
            method: 'POST',
            url: config.service.getGroupId,
            data: {
                code: code,
                encryptedData: encryptedData,
                iv: iv,
                phone: model,
                avatarUrl: avatarUrl,
                nickName: nickName
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
        });
    },
    ifUserAuth:function(code,encryptedData,iv,model){
        var _this = this;
        wx.getSetting({
            success(resa) {
                if (resa.authSetting['scope.userInfo']) {
                    _this.setData({
                        userAuth: true
                    });
                    wx.getUserInfo({
                        success(resu) {
                            _this.GetGroupIdFnc(code,encryptedData,iv,model,resu.userInfo.avatarUrl,resu.userInfo.nickName);
                        }
                    });
                } else {
                    _this.setData({
                        userAuth: false,
                        show: true,
                        showList: false
                    });
                }
            }
        })
    },
    ifUserAuthBefore:function(code,encryptedData,iv,model){
        var _this = this;
        wx.request({
            method: 'POST',
            url: config.service.hasGroupId,
            data: {
                code: code,
                encryptedData: encryptedData,
                iv: iv,
                phone: model
            },
            success: function (res) {
                if (res.data.success) {
                    _this.setData({
                        show: true,
                        showList: true,
                        compList: res.data.data
                    })
                } else {
                    _this.ifUserAuth(code,encryptedData,iv,model);
                }
            }
        });
    },
    initLogin:function(model,isPressBtn,avatarUrl,nickName){
        var _this = this;
        wx.login({
            success(resl) {
                app.globalData.code = resl.code;
                wx.getShareInfo({
                    shareTicket: app.globalData.ticket,
                    success(resi) {
                        if(isPressBtn){
                            _this.GetGroupIdFnc(resl.code,resi.encryptedData,resi.iv,model,avatarUrl,nickName);
                        }else{
                            _this.ifUserAuthBefore(resl.code,resi.encryptedData,resi.iv,model);
                        }
                    }
                })
            }
        })
    },
    onLoad: function () {
        var _this = this;
        wx.showShareMenu({
            withShareTicket: true
        });
        _this.setData({
            showAuth: app.globalData.showAuth
        });

        wx.getSystemInfo({
            success(ress) {
                var mm = ress.model;
                if (mm.indexOf('<') > -1) {
                    ress.model = mm.substring(0, mm.indexOf('<'));
                }
                ress.screenHeight = ress.screenHeight * ress.pixelRatio;
                ress.screenWidth = ress.screenWidth * ress.pixelRatio;
                ress.language = locale[ress.language];
                _this.setData({
                    sysData: ress
                });

                if (null != app.globalData.ticket) {
                    _this.initLogin(ress.model,false,null,null);
                } else {
                    _this.setData({
                        show: true
                    })
                }
            }
        })
    }


})
