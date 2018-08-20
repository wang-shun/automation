package com.gome.test.order.process.com.gome.test.buess.Content;

import org.python.antlr.ast.Str;

/**
 * Created by liangwei-ds on 2017/2/27.
 */
public class Constant {


    /**
     * 线下
     */

    public static final String INDEXURI = "http://erm.atguat.com.cn/";
    public static final String LOGINURI = "http://erm.atguat.com.cn/erm/auth/loginSubmit.action";
    public static final String SEARCHORDER = "http://supplier.atguat.com.cn:3157/order/list";
    public static final String CHECK = "http://omscsc.atguat.com.cn/csc/report/orderReview/auditOrderByAjax.action";
    public static final String STATUSURI = "http://supplier.atguat.com.cn:3157/order/modStatus";
    public static final String ORDERCONFIRM = "http://supplier.atguat.com.cn:3157/order/modStatus2";

    public static final String SCINDEX = "http://login.atguat.com.cn/login";
    public static final String SCLOGINURI = "http://login.atguat.com.cn/sssssLogin.no";
    public static final String ORDERLIST = "http://order.atguat.com.cn/rorderlist/getReturnOrderList?callback=ckdata1&duration=0&_=1489054978488";
    public static final String INVOICE = "http://order.atguat.com.cn/orderdetail/getOrderDetailRightInfo?callback=ckdata2&shippingGroupId=&_=1489127521602&orderId=";
    public static final String MD5STR = "http://order.atguat.com.cn/returnSubmit/getSubmitROPageInitInfo?callback=ckdata2&_=1489127580100";

    public static final String SUBMIT = "http://order.atguat.com.cn/returnSubmit/submitReturnRequest?callback=ckdata3&returnProdState=11000000&returnProdCity=11010000&returnProdCounty=11010200&returnProdTown=110102002&returnProdDetailAddress=%E9%B9%8F%E6%B6%A6%E5%A4%A7%E5%8E%A6&returnProdPostCode=&returnProdConsignee=%E6%B5%8B%E8%AF%95&returnProdPhone=138****1566&returnReason=didNotLike&problemDesc=ss&attachment=ss&prodAppearance=0&prodwrapper=3&hasInvoice=false&hasInspectionReport=false&roUploadImageUrls=&returnMethod=2&mailByUserAddress=L188&maslocType=2&warrantType=0&invokeFrom=ESTORE&returnStoreCounty=&returnStoreId=&spotStoreId=&isMandatoryCollectInvoice=N&replShipState=11000000&replShipCity=11010000&replShipCounty=11010200&replShipTown=110102002&replShipDetailAddress=%E9%B9%8F%E6%B6%A6%E5%A4%A7%E5%8E%A6&replShipPostCode=&replShipConsignee=%E6%B5%8B%E8%AF%95&replShipPhone=138****1566&_=1489127592277&";

    /**
     * 安迅物流
     */
    public static final String LIMSINDEX = "http://10.128.11.135/gome-lmis-login/index.do";
    public static final String LMISLOGINURI = "http://10.128.11.135/gome-lmis-login/login.do";
    public static final String TRANSFEROS = "http://10.128.11.135/gome-lmis-login/gotoSubSystem.do";

    public static final String WAYBILLSTATUS = "http://10.128.31.10/lmis-tms-web/wayBillStatusController/totalQuery.do?okkWed%20Apr%2005%202017%2013:56:36%20GMT+0800&orderNo=&relatedBill1=&orderBeginTime=&orderEndTime=&waybillNo=&relatedBill2=&waybillBeginTime=&waybillEndTime=&consigneeName=&consigneePhone=&consigneeAddress=&wbstatusCarrierNo=&wbStatusJlsoCode=&schedulesStartTime=&schedulesEndTime=&page=1&rows=20&_=1491365362623&wbStatusJlDocCode=";
    public static final String BFWAYBILLSTATUS = "http://10.128.31.8/lmis-tms-web/wayBillStatusController/totalQuery.do?okkWed%20Apr%2005%202017%2013:56:36%20GMT+0800&orderNo=&relatedBill1=&orderBeginTime=&orderEndTime=&waybillNo=&relatedBill2=&waybillBeginTime=&waybillEndTime=&consigneeName=&consigneePhone=&consigneeAddress=&wbstatusCarrierNo=&wbStatusJlsoCode=&schedulesStartTime=&schedulesEndTime=&page=1&rows=20&_=1491365362623&readOnly=YES&wbStatusJlDocCode=";
    public static final String BFWAYBILLURI = "http://10.128.31.10/lmis-tms-web/setCurrentDC.do?currentDC=D2BC6672CA154A28B06CA3AC4CF1142F";

    public static final String GETDICODE = "http://10.128.31.8/lmis-tms-web/getDcs.do?";
    public static final String CURRENTDC = "http://10.128.31.10/lmis-tms-web/setCurrentDC.do";
    //取派分单查询
    public static final String GRTORDER = "http://10.128.31.8/lmis-tms-web/partBill/getDataList.do?cache=0.32646334656535236&orderNo=&relatedBill1=&waybillNo=AX61691586499&isAll=&readOnly=YES&Login_SessionId=SESSION_BD5C27CE730A4C2386262878A619C77F&page=1&rows=20&_=1490166402027";
    public static final String DELIVERDESIGNATE = "http://10.128.31.8/lmis-tms-web/partBill/deliverDesignate.do";
    public static final String QUERY = "http://10.128.31.8/lmis-tms-web/tmTake/querySendWaybills.do";
    public static final String ADDTOSEND = "http://10.128.31.8/lmis-tms-web/tmTake/queryAddTakeDeatails.do";
    public static final String SAVE = "http://10.128.31.8/lmis-tms-web/tmTake/editTakePlans.do";

    //EX 出库
    public static final String EX = "http://10.128.11.10:50000/sap/xi/adapter_plain?namespace=http://gome.com/ELC/TEST/Outbound&interface=SI_WMS002_ECC_Out_Asy&service=BC_LMIS&party&agency&scheme&QOS=EO&sap-user=LMISUSER&sap-password=LMISUSER&sap-client=001&sap-language=EN";
    public static final String SAP = "http://10.128.11.10:50000/sap/xi/adapter_plain?namespace=http://gome.com/ELC/TEST/Outbound&interface=SI_ELC010_ELC_Out_Asy&service=BC_LMIS&party&agency&scheme&QOS=EO&sap-user=LMISUSER&sap-password=LMISUSER&sap-client=001&sap-language=EN";


    public static final String APPLYNEWURI = "http://roms.atguat.com.cn/returnRequest/applyNew.action";
    public static final String SUBMITREVERSEURI = "http://roms.atguat.com.cn/returnRequest/dealSubmit.action";
    public static final String AUDITURI = "http://roms.atguat.com.cn/returnAudit/auditSubmit.action";
    public static final String REVERSESTATUSURI = "http://supplier.atguat.com.cn:3157/order/modStatus";
    public static final String REFUNDURI = "http://omsrefund.atguat.com.cn/oms-audit-refund/search/returnRefundlist.action";
    public static final String AUDITREFUNDURI = "http://omsrefund.atguat.com.cn/oms-audit-refund/audit/auditRefund.action";
    public static final String SEARCHEXURI = "http://roms.atguat.com.cn/reverseOrder/detail.action";
    public static final String CONFIRMRECEIPT = "http://order.atguat.com.cn/orderlist/confirmOrderButton?callback=ckdataConfirm&shippingGroupId=&_=1490928935028";
    public static final String RETURNTYPEURI = "http://order.atguat.com.cn/orderdetail/getHeadInfo?callback=ckdata1&shippingGroupId=&_=1491360475472";

    public static final String APPLYREVERSEURI = "http://omscsc.atguat.com.cn/csc/order/detail.action?orderId=";
    public static final String APPLYREQUESTURI = "http://roms.atguat.com.cn/returnRequest/getReturnOrder.action?orderId=";
    public static final String APPLYURI = "http://roms.atguat.com.cn";
    public static final String APPLYSUBMITURI = "http://roms.atguat.com.cn/returnRequest/applySubmit.action";

    public static final String AUTOAUDITURI = "http://ordermg.atguat.com.cn/dyn/redoAuditOrders.action";
    public static final String AUDITSEARCHURI = "http://omscsc.atguat.com.cn/csc/order/searchOrders.action";

    public static final String REVERSRSTATUSURI = "http://roms.atguat.com.cn/reverseOrder/doSearch.action";



}