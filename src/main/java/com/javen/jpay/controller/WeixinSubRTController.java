package com.javen.jpay.controller;

import java.util.HashMap;
import java.util.Map;

import com.javen.jpay.weixin.api.WxPayApiConfig.PayModel;
import com.javen.jpay.weixin.api.hb.ReadHbModle;
import com.javen.jpay.weixin.api.hb.RedHbApi;
import com.javen.jpay.weixin.utils.IpKit;
import com.javen.jpay.weixin.utils.PaymentKit;
import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
/**
 * @Email javen205@126.com
 * @author Javen
 * 微信红包Demo
 */
public class WeixinSubRTController extends Controller {
	static Log log=Log.getLog(WeixinSubRTController.class);
	private static final Prop prop = PropKit.use("wxsubpay.properties");

	String mchId = prop.get("mch_id");
	String subMchId = prop.get("sub_mch_id");
	String wxAppId = prop.get("appId");
	String partnerKey = prop.get("partnerKey");
	String certPath = prop.get("certPath");
	/**
	 * 发送红包
	 */
	public void sendRedPack(){
		String ns = String.valueOf(System.currentTimeMillis());
		
		log.info(ns);
		
		Map<String, String> params = ReadHbModle.Builder()
		.setPayModel(PayModel.SERVICEMODE)
		.setNonceStr(ns)//有默认值可不设置 
		.setMchBillNo(ns)//有默认值可不设置  
		.setMchId(mchId)
		.setSubMchId(subMchId)
		.setWxAppId(wxAppId)
		.setMsgAppId(wxAppId)
		.setSendName("感谢您使用IJpay!")
		.setReOpenId("o5NJx1dVRilQI6uUVSaBDuLnM3iM")//根据实际的值修改
		.setTotalAmount(100)//单位分 红包的必须在1.00元到200.00元之间
		.setTotalNum(1)
		.setWishing("IJPay 发送红包测试 -By Javen")
		.setClientIp(IpKit.getRealIp(getRequest()))
		.setActName("测试")
		.setRemark("快来抢!")
		.setConsumeMchId(mchId)//常规模式下无效，服务商模式下选填，服务商模式下不填默认扣子商户的钱
		.setPaternerKey(partnerKey)
		.build();
		
		
		String sendRedPack = RedHbApi.sendRedPack(params, certPath, mchId);
		renderText(sendRedPack);
	}
	/**
	 * 发送裂变红包
	 */
	public void sendGroupRedPack(){
		String ns = String.valueOf(System.currentTimeMillis());
		
		log.info(ns);
		
		Map<String, String> params = ReadHbModle.Builder()
		.setPayModel(PayModel.SERVICEMODE)
		.setNonceStr(ns)//有默认值可不设置 
		.setMchBillNo(ns)//有默认值可不设置 
		.setMchId(mchId)
		.setSubMchId(subMchId)
		.setWxAppId(wxAppId)
		.setMsgAppId(wxAppId)
		.setSendName("感谢您使用IJpay!")
		.setReOpenId("o5NJx1dVRilQI6uUVSaBDuLnM3iM")
		.setTotalAmount(3000)//单位分 每个红包的平均金额必须在1.00元到200.00元之间.
		.setTotalNum(3)//total_num必须介于(包括)3到20之间
		
		.setWishing("IJPay 发送红包测试 -By Javen")
		.setActName("测试")
		.setRemark("快来抢!")
		.setConsumeMchId(mchId)//常规模式下无效，服务商模式下选填，服务商模式下不填默认扣子商户的钱
		.setPaternerKey(partnerKey)
		.build();
		
		params.put("amt_type", "ALL_RAND");
		params.put("sign", PaymentKit.createSign(params, partnerKey));
		
		String sendGroupRedPack = RedHbApi.sendGroupRedPack(params, certPath, mchId);
		renderText(sendGroupRedPack);
	}
	
	
	/**
	 * 查询
	 */
	public void gethbinfo(){
		
		Map<String, String> params = new HashMap<String,String>();
		
		params.put("nonce_str",String.valueOf(System.currentTimeMillis()));
		params.put("mch_billno", "");
		params.put("mch_id", mchId);
		params.put("appid",wxAppId);
		params.put("bill_type", "MCHT");
		params.put("sign", PaymentKit.createSign(params, partnerKey));
		
		String hbInfo = RedHbApi.getHbInfo(params, certPath, mchId);
		renderText(hbInfo);
	}
}
