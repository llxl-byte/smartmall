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
				userAvatar: '/static/user_avatar.png',
				systemAvatar: '/static/service_avatar.png'
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
				uni.request({
					url: `${API_BASE_URL}/chat/history/${this.userInfo.id}`,
					method: 'GET',
					success: (res) => {
						if (res.data && res.data.success) {
							this.chatHistory = res.data.data;
							this.scrollToBottom();
						} else {
							console.error('获取聊天历史失败:', res.data.message);
						}
					},
					fail: (err) => {
						console.error('获取聊天历史请求失败:', err);
					}
				});
			},
			
			// 加载常见问题
			loadFaqs() {
				uni.request({
					url: `${API_BASE_URL}/chat/faqs`,
					method: 'GET',
					success: (res) => {
						if (res.data && res.data.success) {
							this.faqs = res.data.data;
						} else {
							console.error('获取常见问题失败:', res.data.message);
						}
					},
					fail: (err) => {
						console.error('获取常见问题请求失败:', err);
					}
				});
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
				
				// 发送消息到服务器
				uni.request({
					url: `${API_BASE_URL}/chat/send`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						userId: this.userInfo.id,
						content: content
					},
					success: (res) => {
						if (res.data && res.data.success) {
							// 添加系统回复到聊天记录
							this.chatHistory.push({
								userId: this.userInfo.id,
								content: res.data.reply,
								isFromUser: false,
								createTime: new Date()
							});
							
							// 滚动到底部
							this.scrollToBottom();
						} else {
							uni.showToast({
								title: '发送消息失败',
								icon: 'none'
							});
							console.error('发送消息失败:', res.data.message);
						}
					},
					fail: (err) => {
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						});
						console.error('发送消息请求失败:', err);
					}
				});
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
