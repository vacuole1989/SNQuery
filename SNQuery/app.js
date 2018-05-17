
App({
  onLaunch: function () {
    wx.login({
      success: res => {
        this.globalData.code = res.code;
      }
    })
  },
  onShow: function (ops) {
      var _this = this;
      if (ops.scene == 1044) {
          _this.globalData.ticket = ops.shareTicket;
          _this.globalData.showAuth = true;
      } else {
          _this.globalData.showAuth = false;
      }
  },
  globalData: {
    phone: { snNumber: '', title: '', itype: '' },
    code: null,
    init: { btns: [], insImg: '' },
    ticket: null,
    showAuth: false
  }
})
