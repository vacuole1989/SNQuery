var config = require('../../config');
const app = getApp()

Page({
  data: {
    hasResult: false,
    snNumber: '',
    resData: {},
    isImei: false,
    btns: [],
    insImg: '',
    show: 'home',
    sysData: {
      model:''
    }
  },
  btnTag: function (event) {
    app.globalData.phone.snNumber = this.data.snNumber;
    app.globalData.phone.title = event.target.dataset.title;
    app.globalData.phone.itype = event.target.dataset.itype;
    wx.navigateTo({
      url: '/pages/search/index'
    });
  },
  setHome: function () {
    this.setData({
      show: 'home'
    })
  },
  setJianding: function () {
    this.setData({
      show: 'jianding'
    })
  },
  setHistory: function () {
    this.setData({
      show: 'history'
    })
  },
  setPhone: function () {
    this.setData({
      show: 'phone'
    })
  },
  removeAllSpace: function (str) {
    return str.replace(/\s+/g, "");
  },
  searchTag: function () {
    var _this = this;
    this.setData({
      snNumber: this.removeAllSpace(this.data.snNumber)
    });
    var sn = this.data.snNumber;
    var regIMEI = new RegExp('^[0-9]{15}$');
    var regSN = new RegExp('^[0-9a-zA-z]{11,12}$');
    if (null === sn || '' === sn) {
      this.showAlert('提示', '请输入序列号或IEMI码');
    } else if (regIMEI.test(sn) || regSN.test(sn)) {
      var stype = 'apple';
      var data = {
        'sn': sn
      };
      if (regIMEI.test(sn)) {
        this.setData({
          isImei: true
        })
        stype = 'imei';
        data = {
          'imei': sn
        };
      } else {
        this.setData({
          isImei: false
        })
      }

      wx.showLoading({
        title: '查询中'
      });
      wx.request({
        url: config.service[stype],
        data: data,
        success: function (res) {
          console.info(res);
          wx.hideLoading();
          if (res.data.success) {
            if (undefined == res.data.message.code || null == res.data.message.code || 0 == res.data.message.code) {
              _this.setData({
                resData: ((undefined == res.data.message.data || null == res.data.message.data) ? res.data.message : res.data.message.data),
                hasResult: true
              });
            } else {
              _this.showAlert('序列号/IMEI码有误', '您输入的序列号/IEMI码错误，请核对后重试');
            }
          }
        }
      })
    } else {
      _this.showAlert('序列号/IMEI码有误', '您输入的序列号/IEMI码错误，请核对后重试');
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
  inputSN: function (e) {
    this.data.snNumber = e.detail.value;
  },
  onShareAppMessage: function (res) {
    if (res.from === 'button') {

    }else{
      return {
        title: '序列号查询',
        path: '/pages/index/index',
        imageUrl: '../share_bg.gif',
        success: function (res) {
        },
        fail: function (res) {
        }
      }
    }
   
  },
  onLoad: function () {
    var _this = this;
    wx.request({
      url: config.service.initBtns,
      data: { 'icheck': true },
      success: function (res) {
        console.info(res);
        if (res.data.success) {
          app.globalData.init.btns = res.data.message;
          app.globalData.init.insImg = (null != res.data.data ? res.data.data.baseStr : '');
          _this.setData({ btns: app.globalData.init.btns });
          _this.setData({ insImg: app.globalData.init.insImg });
        }
      }
    })
    wx.getSystemInfo({
      success: function (res) {
        var mm=res.model;
        if (mm.indexOf('<')>-1){
          res.model = mm.substring(0, mm.indexOf('<'));
        }
        _this.setData({
          sysData: res
        })
      }
    })
  }


})
