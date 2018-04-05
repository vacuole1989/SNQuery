
App({
  onLaunch: function () {
    // 登录
    wx.login({
      success: res => {
        this.globalData.code = res.code;
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
  },
  globalData: {
    phone: { snNumber: '', title: '', itype: '' },
    code: null,
    init: { btns: [], insImg: '' }
  }
})
