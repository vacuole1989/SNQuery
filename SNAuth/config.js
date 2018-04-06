var appTag = 'wx8b93a71bc9c9c411'; 
var host = 'https://applebaoxiu.wang/SNQueryServer/app/'+appTag;


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
        instructions: `${host}/instructions`,
        querypay: `${host}/unifiedorder`,
        refundpay: `${host}/refund`,
        queryhistory: `${host}/queryhistory`,
        initBtns:`${host}/initBtns`
    }
};

module.exports = config;
