# 救援业务接口文档

## 1. 派单接口

**接口地址:** `https://sos.jsecode.com/api/order/queryOrderByCaseId/{caseId}`

**请求示例:**
```json
{
  "caseId": "2604282700013",
  "caseStatus": 10,
  "vehNo": "苏A12975",
  "vehIdentiNo": "LE4TG4DB4FL01467",
  "tonCount": 0,
  "seatCount": 1,
  "carKindCode": 1,
  "caseFromFlag": 2,
  "caseFromOrgId": 1069,
  "agentCompanyId": 1000,
  "policyNo": "",
  "userType": 0,
  "ownExpenseFlag": 0,
  "additionalOrderFlag": 0,
  "isUseCranes": 0,
  "rescueSpecialFlag": 0,
  "executeFlag": 0,
  "blackFlag": 1,
  "multiRescueCount": 0,
  "isRepeatRept": 0,
  "freeBackMileage": 9999,
  "rescueType": 1,
  "rescueCategory": 3,
  "userName": "迪迪测试单",
  "userPhone": "15365112275",
  "userWeixinId": "",
  "caseReptTime": "2026-04-28 09:32:48",
  "acceptTime": "2026-04-28 09:32:48",
  "rescueProvinceCode": "320000",
  "rescueCityCode": "320100",
  "rescueCountyCode": "320104",
  "rescueAddress": "鸿信大厦",
  "rescueLon": "118.78",
  "rescueLat": "32.03",
  "gridNo": "C32004817",
  "autoDispatchCheckTime": "2026-04-28 09:32:48",
  "zuoxiCode": "18102999000006",
  "zuoxiName": "kefugm",
  "caseToOrgId": 1057,
  "caseToOrgTime": "2026-04-28 09:33:20",
  "caseBackFlag": 0,
  "isAddSmallWheel": 0,
  "isDragFloor": 0,
  "isSimpleDilemma": 0,
  "isNeedTrailCar": 0,
  "workId": "",
  "workSubId": "",
  "isSendRepair": 0,
  "piccOrderDetailNo": "",
  "piccServiceNo": "",
  "needCheckVerifyCode": 0,
  "originalRescueCategory": 3,
  "originalRescueCategoryName": "搭电",
  "caseStatusName": "已派单，等待救援",
  "carKindName": "客车",
  "caseFromFlagName": "迪迪平台",
  "caseFromOrgName": "一路保险",
  "agentCompanyName": "亿科达迪迪",
  "userTypeName": "VIP1",
  "insuredPlaceName": "",
  "rescueTypeName": "故障",
  "rescueCategoryName": "搭电",
  "rescueProvinceName": "江苏省",
  "rescueCityName": "南京市",
  "rescueCountyName": "秦淮区",
  "destProvinceName": "",
  "destCityName": "",
  "destCountyName": "",
  "caseToOrgName": "123",
  "costAcctId": "",
  "costRealName": "无",
  "commFlagCase": "0",
  "commAssignCase": "0",
  "showEndedCancelButton": 0,
  "showAddRescueButton": 0,
  "isSendRepairDesc": "否",
  "isSupportChangeBookRescue": 1,
  "showEcarTimeoutButton": 0,
  "showRefundButton": 0,
  "extendInfo": {},
  "tonCountTon": "",
  "insureStartDate_yyyy_MM_dd": "",
  "insureEndDate_yyyy_MM_dd": ""
}
```

## 2. 签收接口

**接口地址:** `https://sos.jsecode.com/api/order/queryOrderByCaseId/{caseId}`

**请求示例:**
```json
{
  "caseId": "2604282700013",
  "caseStatus": 20,
  "vehNo": "苏A12975",
  "vehIdentiNo": "LE4TG4DB4FL01467",
  "tonCount": 0,
  "seatCount": 1,
  "carKindCode": 1,
  "caseFromFlag": 2,
  "caseFromOrgId": 1069,
  "agentCompanyId": 1000,
  "policyNo": "",
  "userType": 0,
  "ownExpenseFlag": 0,
  "additionalOrderFlag": 0,
  "isUseCranes": 0,
  "rescueSpecialFlag": 0,
  "executeFlag": 0,
  "blackFlag": 1,
  "multiRescueCount": 0,
  "isRepeatRept": 0,
  "freeBackMileage": 9999,
  "rescueType": 1,
  "rescueCategory": 3,
  "userName": "迪迪测试单",
  "userPhone": "15365112275",
  "userWeixinId": "",
  "caseReptTime": "2026-04-28 09:32:48",
  "acceptTime": "2026-04-28 09:32:48",
  "rescueProvinceCode": "320000",
  "rescueCityCode": "320100",
  "rescueCountyCode": "320104",
  "rescueAddress": "鸿信大厦",
  "rescueLon": "118.78",
  "rescueLat": "32.03",
  "gridNo": "C32004817",
  "autoDispatchCheckTime": "2026-04-28 09:32:48",
  "zuoxiCode": "18102999000006",
  "zuoxiName": "kefugm",
  "caseToOrgId": 1057,
  "caseToOrgTime": "2026-04-28 09:33:20",
  "caseToOrgSignTime": "2026-04-28 09:49:42",
  "caseBackFlag": 0,
  "isAddSmallWheel": 0,
  "isDragFloor": 0,
  "isSimpleDilemma": 0,
  "isNeedTrailCar": 0,
  "workId": "",
  "workSubId": "",
  "isSendRepair": 0,
  "piccOrderDetailNo": "",
  "piccServiceNo": "",
  "needCheckVerifyCode": 0,
  "originalRescueCategory": 3,
  "originalRescueCategoryName": "搭电",
  "caseStatusName": "供应商已签收",
  "carKindName": "客车",
  "caseFromFlagName": "迪迪平台",
  "caseFromOrgName": "一路保险",
  "agentCompanyName": "亿科达迪迪",
  "userTypeName": "VIP1",
  "insuredPlaceName": "",
  "rescueTypeName": "故障",
  "rescueCategoryName": "搭电",
  "rescueProvinceName": "江苏省",
  "rescueCityName": "南京市",
  "rescueCountyName": "秦淮区",
  "destProvinceName": "",
  "destCityName": "",
  "destCountyName": "",
  "caseToOrgName": "123",
  "costAcctId": "",
  "costRealName": "无",
  "commFlagCase": "0",
  "commAssignCase": "0",
  "showEndedCancelButton": 0,
  "showAddRescueButton": 0,
  "isSendRepairDesc": "否",
  "isSupportChangeBookRescue": 1,
  "showEcarTimeoutButton": 0,
  "showRefundButton": 0,
  "extendInfo": {},
  "tonCountTon": "",
  "insureStartDate_yyyy_MM_dd": "",
  "insureEndDate_yyyy_MM_dd": ""
}
```

## 3. 调度技师接口

**接口地址:** `https://sos.jsecode.com/api/rms/supplierDispatch`

**请求示例:**
```json
{
  "caseId": "2604282700013",
  "dataTransMethod": 0,
  "rescueJsId": "23090182000001",
  "rescueVehId": 4,
  "realRescueCategory": "3",
  "projectUnit": ""
}
```

## 4. 技师签收接口

**接口地址:** `https://sos.jsecode.com/api/rms/jis/jisSign`

**请求示例:**
```json
{
  "appVersion": "android app v3.9.9",
  "carOwnerObjection": 0,
  "caseFromOrgId": 1069,
  "caseId": "2604202700022",
  "dataTransMethod": 1,
  "deviceToken": "54424e5f-912b-4aa3-80e0-48706bb82a88",
  "dragFloorNum": 0,
  "estArrDur": 0,
  "handleMethod": "32",
  "handleOrgId": "1057",
  "handleUserId": "18102999000003",
  "handleUserName": "diaodu123@1057",
  "rescueRealAddressLat": 31.976737663396086,
  "rescueRealAddressLon": 118.77101255236312,
  "rescueReason": 1,
  "rescueType": "1",
  "rescueVehId": 0,
  "roadType": 0,
  "taskId": "26042885000019",
  "version": "3.9.9",
  "waitTimelenMin": 0,
  "wheelGroupNum": 0
}
```

## 5. 技师到达接口

**接口地址:** `https://sos.jsecode.com/api/rms/jis/jisArrive`

**请求示例:**
```json
{
  "appVersion": "android app v3.9.9",
  "carOwnerObjection": 0,
  "caseFromOrgId": 1069,
  "caseId": "2604202700022",
  "dataTransMethod": 1,
  "deviceToken": "54424e5f-912b-4aa3-80e0-48706bb82a88",
  "dragFloorNum": 0,
  "estArrDur": 0,
  "handleMethod": "32",
  "handleOrgId": "1057",
  "handleUserId": "18102999000003",
  "handleUserName": "diaodu123@1057",
  "rescueRealAddressLat": 31.976737663396086,
  "rescueRealAddressLon": 118.77101255236312,
  "rescueReason": 1,
  "rescueType": "1",
  "rescueVehId": 0,
  "roadType": 0,
  "taskId": "26042885000019",
  "version": "3.9.9",
  "waitTimelenMin": 0,
  "wheelGroupNum": 0
}
```

## 6. 技师开始接口

**接口地址:** `https://sos.jsecode.com/api/rms/jis/jisStart`

**请求示例:**
```json
{
  "appVersion": "android app v3.9.9",
  "carOwnerObjection": 0,
  "caseFromOrgId": 1069,
  "caseId": "2604202700022",
  "dataTransMethod": 1,
  "deviceToken": "54424e5f-912b-4aa3-80e0-48706bb82a88",
  "dragFloorNum": 0,
  "estArrDur": 0,
  "handleMethod": "32",
  "handleOrgId": "1057",
  "handleUserId": "18102999000003",
  "handleUserName": "diaodu123@1057",
  "rescueRealAddressLat": 31.976737663396086,
  "rescueRealAddressLon": 118.77101255236312,
  "rescueReason": 1,
  "rescueType": "1",
  "rescueVehId": 0,
  "roadType": 0,
  "taskId": "26042885000019",
  "version": "3.9.9",
  "waitTimelenMin": 0,
  "wheelGroupNum": 0
}
```

## 7. 技师完成接口

**接口地址:** `https://sos.jsecode.com/api/rms/jis/jisComplete`

**请求示例:**
```json
{
  "appVersion": "android app v3.9.9",
  "carOwnerObjection": 0,
  "caseFromOrgId": 1069,
  "caseId": "2604202700022",
  "dataTransMethod": 1,
  "deviceToken": "54424e5f-912b-4aa3-80e0-48706bb82a88",
  "dragFloorNum": 0,
  "estArrDur": 0,
  "handleMethod": "32",
  "handleOrgId": "1057",
  "handleUserId": "18102999000003",
  "handleUserName": "diaodu123@1057",
  "rescueRealAddressLat": 31.976737663396086,
  "rescueRealAddressLon": 118.77101255236312,
  "rescueReason": 1,
  "rescueType": "1",
  "rescueVehId": 0,
  "roadType": 0,
  "taskId": "26042885000019",
  "version": "3.9.9",
  "waitTimelenMin": 0,
  "wheelGroupNum": 0
}
```
