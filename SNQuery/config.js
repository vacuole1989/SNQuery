var appTag = 'wxc3d91607ed8afa1d'; 
var host = 'https://applebaoxiu.wang/SNQueryServer/app/'+appTag;
var host2 = 'https://applebaoxiu.wang/SNQueryServer/appapi/'+appTag;


var config = {
    service: {
        host,
        imei: `${host}/imei`,
        apple: `${host}/apple`,
        appraisal: `${host}/appraisal`,
        sold: `${host}/sold`,
        activationlock: `${host}/activationlock`,
        icloud: `${host}/icloud`,
        repair: `${host}/repair`,
        cn: `${host}/cn`,
        serial: `${host}/serial`,
        simlock: `${host}/simlock`,
        mpn: `${host}/mpn`,
        gsx: `${host}/gsx`,
        instructions: `${host}/instructions`,
        querypay: `${host}/unifiedorder`,
        refundpay: `${host}/refund`,
        queryhistory: `${host}/queryhistory`,
        queryhistorylist: `${host}/queryhistorylist`,
        initBtns:`${host}/initBtns`,
        mobileluck:`${host2}/mobileluck`
    }
};

module.exports = config;
