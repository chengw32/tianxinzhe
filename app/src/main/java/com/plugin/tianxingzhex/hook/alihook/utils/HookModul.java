package com.plugin.tianxingzhex.hook.alihook.utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenguowu on 2018/12/16.
 */

public class HookModul implements Serializable {
	
	private ExtraInfo extraInfo;
	private Content content;
	private String messageInfo;
	
	public String getMessageInfo() {
		return messageInfo == null ? "" : messageInfo;
	}
	
	public void setMessageInfo(String messageInfo) {
		this.messageInfo = messageInfo;
	}
	
	public ExtraInfo getExtraInfo() {
		return extraInfo;
	}
	
	public void setExtraInfo(ExtraInfo extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public Content getContent() {
		return content;
	}
	
	public void setContent(Content content) {
		this.content = content;
	}
	
	public static class ExtraInfo implements Serializable {
		
		
		/**
		 * actions : [{"name":"","url":""},{"name":"查看详情","url":"alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20181217200040011100090041581833&bizType=D_TRANSFER"}]
		 * ad : []
		 * amountTip :
		 * bizMonitor : {"businessId":"PAY_HELPER_CARD_2088332093168324","expireLink":"","gmtCreate":1545028650976,"gmtValid":1547620650972,"hiddenSum":"0","homePageTitle":"支付助手: ￥1.00 二维码收款到账通知","icon":"https://gw.alipayobjects.com/zos/rmsportal/EMWIWDsKUkuXYdvKDdaZ.png","id":"aa9368ddfbb93ead99bd021cf8b14300003200059_00094_zfzs0012088332093168324","link":"alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20181217200040011100090041581833&bizType=D_TRANSFER?tagid=MB_SEND_PH","linkName":"","msgId":"aa9368ddfbb93ead99bd021cf8b143000032","msgType":"NOTICE","operate":"UPDATE","status":"","templateCode":"00059_00094_zfzs001","templateId":"WALLET-BILL@BLPaymentHelper","templateName":"支付助手","templateType":"BN","title":"支付助手","userId":"2088332093168324"}
		 * content : [{"content":"累计收款金额5.10元，累计收款6笔","title":"今日汇总："},{"content":"陈国武 157******70","title":"付款人："},{"content":"20181217143712580004","title":"收款理由："},{"content":"2018-12-17 14:37","title":"到账时间："}]
		 * date : 12月17日
		 * failTip :
		 * goto : alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20181217200040011100090041581833&bizType=D_TRANSFER
		 * infoTip :
		 * money : 1.00
		 * status : 二维码收款到账通知
		 * unit : 元
		 */
		
		private String amountTip;
		private String bizMonitor;
		private String date;
		private String failTip;
		private String infoTip;
		private String money;
		private String status;
		private String orderNo;
		//默认为0 为1的时候就是表示已经与后台确认完成
		private int orderState;
		private String unit;
		private List<ActionsBean> actions;
		private List<?> ad;
		private List<ContentBean> content;
		
		public int getOrderState() {
			return orderState;
		}
		
		public void setOrderState(int orderState) {
			this.orderState = orderState;
		}
		
		public String getOrderNo() {
			return orderNo;
		}
		
		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}
		
		public String getAmountTip() {
			return amountTip;
		}
		
		public void setAmountTip(String amountTip) {
			this.amountTip = amountTip;
		}
		
		public String getBizMonitor() {
			return bizMonitor;
		}
		
		public void setBizMonitor(String bizMonitor) {
			this.bizMonitor = bizMonitor;
		}
		
		public String getDate() {
			return date;
		}
		
		public void setDate(String date) {
			this.date = date;
		}
		
		public String getFailTip() {
			return failTip;
		}
		
		public void setFailTip(String failTip) {
			this.failTip = failTip;
		}
		
		
		public String getInfoTip() {
			return infoTip;
		}
		
		public void setInfoTip(String infoTip) {
			this.infoTip = infoTip;
		}
		
		public String getMoney() {
			return money;
		}
		
		public void setMoney(String money) {
			this.money = money;
		}
		
		public String getStatus() {
			return status;
		}
		
		public void setStatus(String status) {
			this.status = status;
		}
		
		public String getUnit() {
			return unit;
		}
		
		public void setUnit(String unit) {
			this.unit = unit;
		}
		
		public List<ActionsBean> getActions() {
			return actions;
		}
		
		public void setActions(List<ActionsBean> actions) {
			this.actions = actions;
		}
		
		public List<?> getAd() {
			return ad;
		}
		
		public void setAd(List<?> ad) {
			this.ad = ad;
		}
		
		public List<ContentBean> getContent() {
			return content;
		}
		
		public void setContent(List<ContentBean> content) {
			this.content = content;
		}
		
		public static class ActionsBean  implements Serializable {
			/**
			 * name :
			 * url :
			 */
			
			private String name;
			private String url;
			
			public String getName() {
				return name;
			}
			
			public void setName(String name) {
				this.name = name;
			}
			
			public String getUrl() {
				return url;
			}
			
			public void setUrl(String url) {
				this.url = url;
			}
		}
		
		public static class ContentBean   implements Serializable {
			/**
			 * content : 累计收款金额5.10元，累计收款6笔
			 * title : 今日汇总：
			 */
			
			private String content;
			private String title;
			
			public String getContent() {
				return content;
			}
			
			public void setContent(String content) {
				this.content = content;
			}
			
			public String getTitle() {
				return title;
			}
			
			public void setTitle(String title) {
				this.title = title;
			}
		}
	}
	
	public static class Content   implements Serializable {
		
		
		/**
		 * content : ￥1.00
		 * assistMsg1 : 二维码收款到账通知
		 * assistMsg2 : 20181217143712580004
		 * linkName :
		 * buttonLink :
		 * templateId : WALLET-FWC@remindDefaultText
		 */
		
		private String content;
		private String assistMsg1;
		private String assistMsg2;
		private String linkName;
		private String buttonLink;
		private String templateId;
		
		public String getContent() {
			return content;
		}
		
		public void setContent(String content) {
			this.content = content;
		}
		
		public String getAssistMsg1() {
			return assistMsg1;
		}
		
		public void setAssistMsg1(String assistMsg1) {
			this.assistMsg1 = assistMsg1;
		}
		
		public String getAssistMsg2() {
			return assistMsg2;
		}
		
		public void setAssistMsg2(String assistMsg2) {
			this.assistMsg2 = assistMsg2;
		}
		
		public String getLinkName() {
			return linkName;
		}
		
		public void setLinkName(String linkName) {
			this.linkName = linkName;
		}
		
		public String getButtonLink() {
			return buttonLink;
		}
		
		public void setButtonLink(String buttonLink) {
			this.buttonLink = buttonLink;
		}
		
		public String getTemplateId() {
			return templateId;
		}
		
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
	}
	
	@Override
	public String toString() {
		return "HookModul{" +
				"extraInfo=" + extraInfo +
				", content=" + content +
				'}';
	}
}
