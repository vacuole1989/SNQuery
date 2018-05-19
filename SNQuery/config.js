var appTag = 'wx0e4212d51d3eff43'; 
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
        getGroupId: `${host}/group/groupid`,
        hasGroupId: `${host}/group/hasgroupid`,
        instructions: `${host}/instructions`,
        querypay: `${host}/unifiedorder`,
        refundpay: `${host}/refund`,
        queryhistory: `${host}/queryhistory`,
        queryhistorylist: `${host}/queryhistorylist`,
        initBtns: `${host}/initBtns`,
        mobileluck: `${host2}/mobileluck`,
        lsplateluck: `${host2}/lsplateluck`
    }
};

module.exports = config;
