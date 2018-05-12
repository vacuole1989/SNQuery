var config = require('../../config');
const app = getApp()

Page({
    data: {
        hisData: []
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
    onShowHis: function (event) {
        wx.navigateTo({
            url: '/pages/result/index?str=' + event.currentTarget.dataset.str,
        })
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
    onShow: function () {
        wx.setNavigationBarTitle({
            title: '历史查询记录'
        })
        var _this = this;
        if (_this.data.hisData.length == 0) {

            wx.showLoading({
                title: '数据加载中……',
            })
        }
        wx.login({
            success: res => {
                wx.request({
                    url: config.service.queryhistorylist,
                    data: { 'code': res.code },
                    success: function (res) {
                        wx.hideLoading();
                        console.info(res.data.data);
                        if (res.data.success) {
                            _this.setData({ hisData: res.data.data });
                        }
                    }
                })
            }
        })

    }


})
