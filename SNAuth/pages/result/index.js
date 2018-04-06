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
        wx.redirectTo({
          url: '/pages/index/index'
        });
      }
    });
  },
  btnToIndex:function(){
    wx.redirectTo({
      url: '/pages/index/index'
    });
  },
  onLoad: function (options) {
    var _this=this;
    wx.request({
      url: config.service.queryhistory,
      data: {
        nonceStr: options.str
      },
      success: function (res) {
        if (res.data.success) {
          _this.setData({
            itype: res.data.itype,
            isImei: !res.data.isn,
            hasResult: true,
            resData: (null == res.data.data.code ? res.data.data : res.data.data.data)
          });
        } else {
          _this.showAlert('提示',res.data.message);
        }

      }
    });
  }

})
