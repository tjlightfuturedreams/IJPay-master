package com.javen.jpay.controller;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeOrderSettleModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.javen.jpay.alipay.AliPayApi;
import com.javen.jpay.alipay.AliPayApiConfig;
import com.javen.jpay.alipay.AliPayApiController;
import com.javen.jpay.util.StringUtils;
import com.javen.jpay.vo.AjaxResult;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
/**
 * @Email javen205@126.com
 * @author Javen
 * 2017年5月20日
 */
public class AliPayController extends AliPayApiController {
	private Log log = Log.getLog(AliPayController.class);
	
	private  final Prop prop = PropKit.use("alipay.properties");
	private  String charset = "UTF-8";
	private  String private_key = prop.get("privateKey");
	private  String alipay_public_key = prop.get("alipayPulicKey");
	private  String service_url = prop.get("serverUrl");
	private  String app_id = prop.get("appId");
	private  String sign_type = "RSA2";
	private  String notify_domain = prop.get("notify_domain");
	
	private AjaxResult result = new AjaxResult();

	@Override
	public AliPayApiConfig getApiConfig() {
		AliPayApiConfig aliPayApiConfig = AliPayApiConfig.New()
		.setAppId(app_id)
		.setAlipayPublicKey(alipay_public_key)
		.setCharset(charset)
		.setPrivateKey(private_key)
		.setServiceUrl(service_url)
		.setSignType(sign_type)
		.build();
		return aliPayApiConfig;
	}
	
	public void index() {
		renderText("欢迎使用IJPay 中的支付宝支付 -By Javen205");
	}

	/**
	 * app支付
	 */
	public void appPay() {
		try {
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody("我是测试数据-By Javen");
			model.setSubject("App支付测试-By Javen");
			model.setOutTradeNo(StringUtils.getOutTradeNo());
			model.setTimeoutExpress("30m");
			model.setTotalAmount("0.01");
			model.setPassbackParams("callback params");
			model.setProductCode("QUICK_MSECURITY_PAY");
			String orderInfo = AliPayApi.startAppPayStr(model, notify_domain + "/alipay/app_pay_notify");
			result.success(orderInfo);
			renderJson(result);

		} catch (AlipayApiException e) {
			e.printStackTrace();
			result.addError("system error");
		}
	}

	/**
	 * Wap支付
	 */
	public void wapPay() {
		String body = "我是测试数据-By Javen";
		String subject = "Javen Wap支付测试";
		String totalAmount = "1";
		String passbackParams = "1";
		String returnUrl = notify_domain + "/alipay/return_url";
		String notifyUrl = notify_domain + "/alipay/notify_url";

		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setBody(body);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setPassbackParams(passbackParams);
		String outTradeNo = StringUtils.getOutTradeNo();
		System.out.println("wap outTradeNo>"+outTradeNo);
		model.setOutTradeNo(outTradeNo);
		model.setProductCode("QUICK_WAP_PAY");

		try {
			AliPayApi.wapPay(getResponse(), model, returnUrl, notifyUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}
	
	/**
	 * PC支付
	 */
	public void pcPay(){
		try {
			String totalAmount = "88.88"; 
			String outTradeNo =StringUtils.getOutTradeNo();
			log.info("pc outTradeNo>"+outTradeNo);
			
			String returnUrl = notify_domain + "/alipay/return_url";
			String notifyUrl = notify_domain + "/alipay/notify_url";
			AlipayTradePayModel model = new AlipayTradePayModel();
			
			model.setOutTradeNo(outTradeNo);
			model.setProductCode("FAST_INSTANT_TRADE_PAY");
			model.setTotalAmount(totalAmount);
			model.setSubject("Javen PC支付测试");
			model.setBody("Javen IJPay PC支付测试");
			
			AliPayApi.tradePage(getResponse(),model , notifyUrl, returnUrl);
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}

	

	/**
	 * 条形码支付
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.Yhpibd&
	 * treeId=194&articleId=105170&docType=1#s4
	 */
	public void tradePay() {
		String authCode = getPara("auth_code");
		String subject = "Javen 支付宝条形码支付测试";
		String totalAmount = "100";
		String notifyUrl = notify_domain + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("bar_code");
		try {
			String resultStr = AliPayApi.tradePay(model,notifyUrl);
			renderText(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 声波支付
	 * https://doc.open.alipay.com/docs/doc.htm?treeId=194&articleId=105072&docType=1#s2
	 */
	public void tradeWavePay() {
		String authCode = getPara("auth_code");
		String subject = "Javen 支付宝声波支付测试";
		String totalAmount = "100";
		String notifyUrl = notify_domain + "/alipay/notify_url";

		AlipayTradePayModel model = new AlipayTradePayModel();
		model.setAuthCode(authCode);
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		model.setScene("wave_code");
		try {
			String resultStr = AliPayApi.tradePay(model,notifyUrl);
			renderText(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 扫码支付
	 */
	public void tradePrecreatePay() {
		String subject = "Javen 支付宝扫码支付测试";
		String totalAmount = "86";
		String storeId = "123";
		String notifyUrl = notify_domain + "/alipay/precreate_notify_url";

		AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
		model.setSubject(subject);
		model.setTotalAmount(totalAmount);
		model.setStoreId(storeId);
		model.setTimeoutExpress("5m");
		model.setOutTradeNo(StringUtils.getOutTradeNo());
		try {
			String resultStr = AliPayApi.tradePrecreatePay(model, notifyUrl);
			JSONObject jsonObject = JSONObject.parseObject(resultStr);
			String qr_code = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
			renderText(qr_code);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 单笔转账到支付宝账户
	 * https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.54Ty29&
	 * treeId=193&articleId=106236&docType=1
	 */
	public void transfer() {
		boolean isSuccess = false;
		String total_amount = "100";
		AlipayFundTransToaccountTransferModel model = new AlipayFundTransToaccountTransferModel();
		model.setOutBizNo(StringUtils.getOutTradeNo());
		model.setPayeeType("ALIPAY_LOGONID");
		model.setPayeeAccount("abpkvd0206@sandbox.com");
		model.setAmount(total_amount);
		model.setPayerShowName("测试退款");
		model.setPayerRealName("沙箱环境");
		model.setRemark("javen测试单笔转账到支付宝");

		try {
			isSuccess = AliPayApi.transfer(model);
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(isSuccess);
	}

	/**
	 * 下载对账单
	 */
	public void dataDataserviceBill() {
		String para = getPara("billDate");
		try {
			AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
			model.setBillType("trade");
			model.setBillDate(para);
			String resultStr = AliPayApi.billDownloadurlQuery(model);
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 退款
	 */
	public void tradeRefund() {

		try {
			AlipayTradeRefundModel model = new AlipayTradeRefundModel();
			model.setOutTradeNo("042517111114931");
			model.setTradeNo("2017042521001004200200236813");
			model.setRefundAmount("86.00");
			model.setRefundReason("正常退款");
			String resultStr = AliPayApi.tradeRefund(model);
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 撤销订单
	 */
	public void tradeCancel() {
		try {
			AlipayTradeCancelModel model = new AlipayTradeCancelModel();
			model.setOutTradeNo("042518024814931");
			model.setTradeNo("2017042521001004200200236814");

			boolean isSuccess = AliPayApi.isTradeCancel(model);
			renderJson(isSuccess);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 交易查询
	 */
	public void tradeQuery() {
		try {
			AlipayTradeQueryModel model = new AlipayTradeQueryModel();
			model.setOutTradeNo("042518024814931");
			model.setTradeNo("2017042521001004200200236814");

			boolean isSuccess = AliPayApi.isTradeQuery(model);
			renderJson(isSuccess);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}

	public void tradeQueryByStr() {
		String out_trade_no = getPara("out_trade_no");
		// String trade_no = getPara("trade_no");

		AlipayTradeQueryModel model = new AlipayTradeQueryModel();
		model.setOutTradeNo(out_trade_no);

		try {
			String resultStr = AliPayApi.tradeQuery(model).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 创建订单
	 */
	public void tradeCreate(){
		String outTradeNo = getPara("outTradeNo");
		
		String notifyUrl = notify_domain+ "/alipay/notify_url";;
		
		AlipayTradeCreateModel model = new AlipayTradeCreateModel();
		model.setOutTradeNo(outTradeNo);
		model.setTotalAmount("86.68");
		model.setBody("Body");
		model.setSubject("Javen 测试统一收单交易创建接口");
		model.setBuyerLogonId("abpkvd0206@sandbox.com");//买家支付宝账号，和buyer_id不能同时为空
		try {
			
			AlipayTradeCreateResponse response = AliPayApi.tradeCreate(model,notifyUrl);
			renderJson(response.getBody());
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 关闭订单
	 */
	public void tradeClose(){
		String outTradeNo = getPara("outTradeNo");
		String tradeNo = getPara("tradeNo");
		try {
			AlipayTradeCloseModel model = new AlipayTradeCloseModel();
			model.setOutTradeNo(outTradeNo);
			
			model.setTradeNo(tradeNo);
			
			String resultStr = AliPayApi.tradeClose(model).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 结算
	 */
	public void tradeOrderSettle(){
		String tradeNo = getPara("tradeNo");//支付宝订单号
		try {
			AlipayTradeOrderSettleModel model = new AlipayTradeOrderSettleModel();
			model.setOutRequestNo(StringUtils.getOutTradeNo());
			model.setTradeNo(tradeNo);
			
			String resultStr = AliPayApi.tradeOrderSettle(model ).getBody();
			renderText(resultStr);
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
	}
	
	public void return_url() {
		try {
			// 获取支付宝GET过来反馈信息
			Map<String, String> map = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(map, alipay_public_key, charset,
					sign_type);

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码
				System.out.println("return_url 验证成功");
				renderText("success");
				return;
			} else {
				System.out.println("return_url 验证失败");
				// TODO
				renderText("failure");
				return;
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}

	public void notify_url() {
		try {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = AliPayApi.toMap(getRequest());

			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}

			boolean verify_result = AlipaySignature.rsaCheckV1(params, alipay_public_key, charset,
					sign_type);

			if (verify_result) {// 验证成功
				// TODO 请在这里加上商户的业务逻辑程序代码 异步通知可能出现订单重复通知 需要做去重处理
				System.out.println("notify_url 验证成功succcess");
				renderText("success");
				return;
			} else {
				System.out.println("notify_url 验证失败");
				// TODO
				renderText("failure");
				return;
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}
	//=======其实异步通知实现的方法都一样  但是通知中无法区分支付的方式(没有提供支付方式的参数)======================================================================
	/**
	 * App支付支付回调通知
	 * https://doc.open.alipay.com/docs/doc.htm?treeId=54&articleId=106370&
	 * docType=1#s3
	 */
	public void app_pay_notify() {
		try {
			// 获取支付宝POST过来反馈信息
			Map<String, String> params = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				System.out.println(entry.getKey() + " = " + entry.getValue());
			}
			// 切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
			// boolean AlipaySignature.rsaCheckV1(Map<String, String> params,
			// String publicKey, String charset, String sign_type)
			boolean flag = AlipaySignature.rsaCheckV1(params, alipay_public_key, charset,
					sign_type);
			if (flag) {
				// TODO
				System.out.println("success");
				renderText("success");
				return;
			} else {
				// TODO
				System.out.println("failure");
				renderText("failure");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}
	/**
	 * 扫码支付通知
	 */
	public void precreate_notify_url(){
		try {
			Map<String, String> map = AliPayApi.toMap(getRequest());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey()+" = "+entry.getValue());
			}
			boolean flag = AlipaySignature.rsaCheckV1(map, alipay_public_key, charset,
					sign_type);
			if (flag) {
				// TODO
				System.out.println("precreate_notify_url success");
				renderText("success");
				return;
			} else {
				// TODO
				System.out.println("precreate_notify_url failure");
				renderText("failure");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			renderText("failure");
		}
	}

	
}
