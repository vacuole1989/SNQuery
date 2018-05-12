var config = require('config')
App({
    onLaunch: function (ops) {
        // 登录
        wx.login({
            success: res => {
                this.globalData.code = res.code;
                // 发送 res.code 到后台换取 openId, sessionKey, unionId
            }
        })

    },
    onShow: function (ops) {
        var _this = this;
        if (ops.scene == 1044) {
            wx.getSystemInfo({
                success: function (ress) {
                    var mm = ress.model;
                    if (mm.indexOf('<') > -1) {
                        ress.model = mm.substring(0, mm.indexOf('<'));
                    }
                    wx.getUserInfo({
                        success: resu => {
                            resu.userInfo
                            wx.login({
                                success: resl => {
                                    wx.getShareInfo({
                                        shareTicket: ops.shareTicket,
                                        success(res) {
                                            console.info(res);
                                            wx.request({
                                                type: 'post',
                                                url: config.service.GetGroupId,
                                                data: {
                                                    code: resl.code,
                                                    encryptedData: res.encryptedData,
                                                    iv: res.iv,
                                                    avatarUrl: resu.userInfo.avatarUrl,
                                                    phone: ress.model
                                                },
                                                header: {
                                                    'content-type': 'application/json' // 默认值
                                                },
                                                success: function (res) {
                                                    console.log(res.data)
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






        }
    },
    globalData: {
        phone: { snNumber: '', title: '', itype: '' },
        code: null,
        init: { btns: [], insImg: '' }
    }

})
