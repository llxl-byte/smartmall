<template>
	<view class="chat-container">
		<!-- 聊天记录区域 -->
		<scroll-view class="chat-messages" scroll-y="true" :scroll-top="scrollTop" @scrolltolower="onScrollToLower">
			<view v-if="chatHistory.length === 0" class="empty-chat">
				<image class="robot-image" src="/static/robot.png" mode="aspectFit"></image>
				<text class="welcome-text">您好，我是智能客服助手，有什么可以帮助您的吗？</text>
				<view class="faq-suggestions">
					<view class="faq-title">您可以向我咨询以下问题：</view>
					<view
						v-for="(faq, index) in faqs.slice(0, 5)"
						:key="index"
						class="faq-item"
						@click="selectFaq(faq.question)"
					>
						<text class="faq-icon">?</text>
						<text class="faq-text">{{ faq.question }}</text>
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
			<view class="input-tools">
				<text class="tool-icon" @click="showFaqList">💬</text>
			</view>
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

		<!-- FAQ列表弹出层 -->
		<view class="faq-popup" v-if="showFaqs">
			<view class="faq-popup-mask" @click="showFaqs = false"></view>
			<view class="faq-popup-content">
				<view class="faq-popup-header">
					<text class="faq-popup-title">常见问题</text>
					<text class="faq-popup-close" @click="showFaqs = false">✕</text>
				</view>
				<scroll-view class="faq-popup-list" scroll-y="true">
					<view
						class="faq-popup-item"
						v-for="(faq, index) in faqs"
						:key="index"
						@click="selectFaq(faq.question)"
					>
						{{ faq.question }}
					</view>
				</scroll-view>
			</view>
		</view>
	</view>
</template>

<script>
	// 导入API基础URL
	import { API_BASE_URL } from '@/config.js';

	export default {
		data() {
			return {
				userInfo: null,
				messageContent: '',
				chatHistory: [],
				faqs: [],
				scrollTop: 0,
				userAvatar: '/static/default-avatar.png',
				systemAvatar: '/static/robot.png',
				showFaqs: false,
				isTyping: false
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

				// 关闭FAQ弹出层
				this.showFaqs = false;

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
							// 使用模拟输入效果显示回复
							this.simulateTyping(res.data.reply);
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

						// 如果网络请求失败，显示默认回复
						this.simulateTyping('抱歉，网络连接失败，请稍后再试。');
					}
				});
			},

			// 选择常见问题
			selectFaq(question) {
				this.messageContent = question;
				this.showFaqs = false; // 关闭弹出层
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
			},

			// 显示FAQ列表
			showFaqList() {
				this.showFaqs = true;
			},

			// 模拟正在输入效果
			simulateTyping(message, callback) {
				this.isTyping = true;

				// 添加“正在输入”消息
				const typingMessage = {
					userId: this.userInfo.id,
					content: '正在输入...',
					isFromUser: false,
					isTyping: true,
					createTime: new Date()
				};

				this.chatHistory.push(typingMessage);
				this.scrollToBottom();

				// 模拟打字延迟
				setTimeout(() => {
					// 移除“正在输入”消息
					const index = this.chatHistory.findIndex(msg => msg === typingMessage);
					if (index !== -1) {
						this.chatHistory.splice(index, 1);
					}

					// 添加真正的回复消息
					this.chatHistory.push({
						userId: this.userInfo.id,
						content: message,
						isFromUser: false,
						createTime: new Date()
					});

					this.scrollToBottom();
					this.isTyping = false;

					if (callback) callback();
				}, 1500);
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
		color: #666;
		font-size: 28rpx;
	}

	.robot-image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 30rpx;
	}

	.welcome-text {
		font-size: 32rpx;
		color: #333;
		margin-bottom: 40rpx;
		text-align: center;
		padding: 0 40rpx;
	}

	.faq-suggestions {
		margin-top: 20rpx;
		width: 90%;
	}

	.faq-title {
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		color: #666;
		text-align: center;
	}

	.faq-item {
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
		color: #333;
		display: flex;
		align-items: center;
	}

	.faq-icon {
		width: 40rpx;
		height: 40rpx;
		background-color: #07c160;
		color: #fff;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-right: 20rpx;
		font-size: 24rpx;
	}

	.faq-text {
		flex: 1;
		font-size: 28rpx;
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
		align-items: center;
	}

	.input-tools {
		display: flex;
		align-items: center;
		margin-right: 10rpx;
	}

	.tool-icon {
		font-size: 40rpx;
		padding: 10rpx;
		color: #666;
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

	/* FAQ弹出层样式 */
	.faq-popup {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 999;
		display: flex;
		flex-direction: column;
		justify-content: flex-end;
	}

	.faq-popup-mask {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
	}

	.faq-popup-content {
		position: relative;
		background-color: #fff;
		border-top-left-radius: 20rpx;
		border-top-right-radius: 20rpx;
		max-height: 70vh;
		display: flex;
		flex-direction: column;
		animation: slideUp 0.3s ease;
	}

	@keyframes slideUp {
		from {
			transform: translateY(100%);
		}
		to {
			transform: translateY(0);
		}
	}

	.faq-popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
	}

	.faq-popup-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.faq-popup-close {
		font-size: 32rpx;
		color: #999;
	}

	.faq-popup-list {
		max-height: 60vh;
		padding: 20rpx 30rpx;
	}

	.faq-popup-item {
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f5f5f5;
		font-size: 28rpx;
		color: #333;
	}
</style>
