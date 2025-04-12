<template>
	<view class="chat-container">
		<!-- 聊天记录区域 -->
		<scroll-view class="chat-messages" scroll-y="true" :scroll-top="scrollTop" @scrolltolower="onScrollToLower">
			<view v-if="chatHistory.length === 0" class="empty-chat">
				<text>您可以向智能客服咨询任何问题</text>
				<view class="faq-suggestions">
					<view class="faq-title">常见问题：</view>
					<view 
						v-for="(faq, index) in faqs.slice(0, 5)" 
						:key="index" 
						class="faq-item"
						@click="selectFaq(faq.question)"
					>
						{{ faq.question }}
					</view>
				</view>
			</view>
			<view v-else>
				<view 
					v-for="(message, index) in chatHistory" 
					:key="index" 
					:class="['message-item', message.isFromUser ? 'user-message' : 'system-message']"
				>
					<view class="message-avatar">
						<image :src="message.isFromUser ? userAvatar : systemAvatar" mode="aspectFill"></image>
					</view>
					<view class="message-content">
						<text>{{ message.content }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 输入区域 -->
		<view class="input-area">
			<input 
				class="message-input" 
				type="text" 
				v-model="messageContent" 
				placeholder="请输入您的问题" 
				confirm-type="send"
				@confirm="sendMessage"
			/>
			<button class="send-button" @click="sendMessage" :disabled="!messageContent.trim()">发送</button>
		</view>
	</view>
</template>

<script>
	// 导入API基础URL
	import { API_BASE_URL } from '@/config/api.js';
	
	export default {
		data() {
			return {
				userInfo: null,
				messageContent: '',
				chatHistory: [],
				faqs: [],
				scrollTop: 0,
				userAvatar: '/static/default-avatar.png',
				systemAvatar: '/static/default-avatar.png'
			}
		},
		onLoad() {
			// 检查用户是否登录
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/login'
					});
				}, 1500);
				return;
			}
			
			this.userInfo = userInfo;
			
			// 加载聊天历史
			this.loadChatHistory();
			
			// 加载常见问题
			this.loadFaqs();
		},
		methods: {
			// 加载聊天历史
			loadChatHistory() {
				// 使用模拟数据
				const mockChatHistory = [
					{
						id: 1,
						userId: this.userInfo.id,
						content: '你好，我想了解一下退款政策',
						isFromUser: true,
						createTime: new Date(Date.now() - 3600000)
					},
					{
						id: 2,
						userId: this.userInfo.id,
						content: '在"我的订单"中找到相应订单，点击"申请退款"，填写退款原因后提交。',
						isFromUser: false,
						createTime: new Date(Date.now() - 3500000)
					}
				];
				this.chatHistory = mockChatHistory;
				this.scrollToBottom();
			},
			
			// 加载常见问题
			loadFaqs() {
				// 使用模拟数据
				const mockFaqs = [
					{
						id: 1,
						question: '如何注册账号?',
						answer: '点击首页右下角"我的"，然后点击"注册"按钮，填写相关信息即可完成注册。',
						sort: 1
					},
					{
						id: 2,
						question: '如何修改个人信息?',
						answer: '登录后，进入"我的"页面，点击头像或用户名，进入个人信息页面进行修改。',
						sort: 2
					},
					{
						id: 3,
						question: '如何查看订单?',
						answer: '登录后，进入"我的"页面，点击"我的订单"即可查看所有订单。',
						sort: 3
					},
					{
						id: 4,
						question: '如何添加商品到购物车?',
						answer: '在商品详情页，点击"加入购物车"按钮即可。',
						sort: 4
					},
					{
						id: 5,
						question: '如何提交订单?',
						answer: '在购物车页面，选择要购买的商品，点击"结算"，确认订单信息后点击"提交订单"。',
						sort: 5
					},
					{
						id: 6,
						question: '支持哪些支付方式?',
						answer: '目前支持微信支付、支付宝支付和银行卡支付。',
						sort: 6
					},
					{
						id: 7,
						question: '如何申请退款?',
						answer: '在"我的订单"中找到相应订单，点击"申请退款"，填写退款原因后提交。',
						sort: 7
					},
					{
						id: 8,
						question: '商品何时发货?',
						answer: '正常情况下，我们会在您付款后24小时内发货。',
						sort: 8
					},
					{
						id: 9,
						question: '如何添加收货地址?',
						answer: '在"我的"页面，点击"收货地址"，然后点击"添加新地址"。',
						sort: 9
					},
					{
						id: 10,
						question: '优惠券如何使用?',
						answer: '在结算页面，可以选择使用符合条件的优惠券。',
						sort: 10
					}
				];
				this.faqs = mockFaqs;
			},
			
			// 发送消息
			sendMessage() {
				if (!this.messageContent.trim()) return;
				
				const content = this.messageContent.trim();
				this.messageContent = ''; // 清空输入框
				
				// 添加用户消息到聊天记录（本地显示）
				this.chatHistory.push({
					userId: this.userInfo.id,
					content: content,
					isFromUser: true,
					createTime: new Date()
				});
				
				// 滚动到底部
				this.scrollToBottom();
				
				// 模拟服务器响应
				setTimeout(() => {
					// 查找匹配的FAQ
					let reply = '';
					const matchedFaq = this.faqs.find(faq => 
						faq.question.includes(content) || 
						content.includes(faq.question.replace(/[?？]/g, ''))
					);
					
					if (matchedFaq) {
						reply = matchedFaq.answer;
					} else {
						reply = '抱歉，我无法理解您的问题。您可以尝试其他问题，或者联系人工客服。';
					}
					
					// 添加系统回复到聊天记录
					this.chatHistory.push({
						userId: this.userInfo.id,
						content: reply,
						isFromUser: false,
						createTime: new Date()
					});
					
					// 滚动到底部
					this.scrollToBottom();
				}, 500);
			},
			
			// 选择常见问题
			selectFaq(question) {
				this.messageContent = question;
				this.sendMessage();
			},
			
			// 滚动到底部
			scrollToBottom() {
				setTimeout(() => {
					// 使用一个很大的值来确保滚动到底部
					this.scrollTop = 100000;
				}, 100);
			},
			
			// 滚动到底部事件处理
			onScrollToLower() {
				console.log('已滚动到底部');
			}
		}
	}
</script>

<style>
	.chat-container {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f5f5f5;
	}
	
	.chat-messages {
		flex: 1;
		padding: 20rpx;
		overflow-y: auto;
	}
	
	.empty-chat {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		height: 70vh;
		color: #999;
		font-size: 28rpx;
	}
	
	.faq-suggestions {
		margin-top: 40rpx;
		width: 90%;
	}
	
	.faq-title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		color: #666;
	}
	
	.faq-item {
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
		color: #333;
	}
	
	.message-item {
		display: flex;
		margin-bottom: 30rpx;
	}
	
	.user-message {
		flex-direction: row-reverse;
	}
	
	.system-message {
		flex-direction: row;
	}
	
	.message-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		overflow: hidden;
		margin: 0 20rpx;
	}
	
	.message-avatar image {
		width: 100%;
		height: 100%;
	}
	
	.message-content {
		max-width: 70%;
		padding: 20rpx;
		border-radius: 10rpx;
		word-break: break-all;
	}
	
	.user-message .message-content {
		background-color: #07c160;
		color: #fff;
		border-top-right-radius: 0;
	}
	
	.system-message .message-content {
		background-color: #fff;
		color: #333;
		border-top-left-radius: 0;
	}
	
	.input-area {
		display: flex;
		padding: 20rpx;
		background-color: #fff;
		border-top: 1rpx solid #eee;
	}
	
	.message-input {
		flex: 1;
		height: 80rpx;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		padding: 0 30rpx;
		font-size: 28rpx;
	}
	
	.send-button {
		width: 120rpx;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		background-color: #07c160;
		color: #fff;
		border-radius: 40rpx;
		margin-left: 20rpx;
		font-size: 28rpx;
	}
	
	.send-button[disabled] {
		background-color: #ccc;
	}
</style>
