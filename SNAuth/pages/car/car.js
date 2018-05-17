var config = require('../../config')
const app = getApp()

Page({
    data: {
        hasResult: false,
        carNbr: '',
        resData: {}
    },
    onShow: function () {
        wx.setNavigationBarTitle({
            title: '车牌号测凶吉'
        })
    },
    removeAllSpace: function (str) {
        return str.replace(/\s+/g, "");
    },
    searchTag: function () {
        var _this = this;
        this.setData({
            hasResult:false,
            carNbr: this.removeAllSpace(this.data.carNbr)
        });
        var sn = this.data.carNbr;
        if (null === sn || '' === sn) {
            this.showAlert('提示', '请输入车牌号码');
        } else{
            wx.showLoading({
                title: '查询中'
            });
            var data = {
                'sn': sn,
                'query': 'lsplate'
            };
            wx.request({
                url: config.service['lsplateluck'],
                data: data,
                success: function (res) {
                    wx.hideLoading();
                    if (res.data.success) {
                        if(res.data.data.status=='0'){
                            _this.setData({
                                hasResult: true,
                                resData: res.data.data.result
                            });
                        }else{
                            _this.showAlert('错误', res.data.data.msg);
                        }
                        

                    } else {
                        _this.showAlert('错误', '查询失败，请稍后重试。');
                    }
                }
            })
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
    carNbr: function (e) {
        this.data.carNbr = e.detail.value;
    },
    onShareAppMessage: function (res) {
    }
})
