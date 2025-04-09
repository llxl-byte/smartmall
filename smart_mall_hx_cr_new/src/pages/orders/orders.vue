<template>
	<view class="order-list-container">
		<!-- 加载状态 -->
		<view v-if="isLoading" class="loading-indicator">
			<text>正在加载订单...</text>
		</view>

		<!-- 订单列表 -->
		<view v-else-if="orderList.length > 0" class="order-list">
			<view class="order-item" v-for="(order, index) in orderList" :key="order.id || index">
				<view class="order-header">
					<text class="order-no">订单号: {{ order.orderNo }}</text>
					<text class="order-status">{{ formatOrderStatus(order.status) }}</text>
				</view>
				<view class="order-body">
					<!-- 这里可以添加商品信息展示，暂时留空 -->
					<text>订单金额: ¥{{ order.totalAmount }}</text>
				</view>
				<view class="order-footer">
					<text class="order-time">下单时间: {{ formatOrderTime(order.createTime) }}</text>
					<!-- 这里可以添加操作按钮，暂时留空 -->
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty-orders">
			<text>您还没有订单哦~</text>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
		data() {
			return {
				orderList: [], // 订单列表
				isLoading: true, // 是否正在加载
				userId: null // 用户ID
			}
		},
		onLoad() {
			// 页面加载时获取用户信息并加载订单
			const userInfo = uni.getStorageSync('userInfo');
			if (userInfo && userInfo.id) {
				this.userId = userInfo.id;
				this.loadOrders();
			} else {
				// 如果未登录，可以提示用户去登录
				uni.showToast({
					title: '请先登录',
					icon: 'none',
					duration: 2000,
					complete: () => {
						setTimeout(() => {
							uni.navigateTo({ url: '/pages/login/login' });
						}, 2000);
					}
				});
				this.isLoading = false; // 停止加载状态
			}
		},
		methods: {
			// 加载订单列表
			loadOrders() {
				if (!this.userId) return; // 确保 userId 存在

				this.isLoading = true;
				uni.request({
					// 使用配置文件中的 API_BASE_URL
					url: `${API_BASE_URL}/orders?userId=${this.userId}`,
					method: 'GET',
					success: (res) => {
						console.log('订单列表响应:', res.data); // 打印响应方便调试
						if (res.data && res.data.success && Array.isArray(res.data.data)) {
							this.orderList = res.data.data;
						} else {
							// 处理接口返回错误或数据格式不正确的情况
							uni.showToast({
								title: res.data.message || '加载订单失败',
								icon: 'none'
							});
							this.orderList = []; // 清空列表
						}
					},
					fail: (err) => {
						console.error('请求订单列表失败:', err);
						uni.showToast({
							title: '网络错误，请稍后重试',
							icon: 'none'
						});
						this.orderList = []; // 清空列表
					},
					complete: () => {
						this.isLoading = false; // 无论成功失败，结束加载状态
					}
				});
			},

			// 格式化订单状态 (将数字转换为文字)
			formatOrderStatus(status) {
				switch (status) {
					case 0: return '待付款';
					case 1: return '待发货';
					case 2: return '待收货';
					case 3: return '已完成';
					case 4: return '已取消';
					default: return '未知状态';
				}
			},

			// 格式化时间 (简化显示)
			formatOrderTime(timeStr) {
				if (!timeStr) return '';
				// 简单地截取日期和小时分钟部分
				return timeStr.substring(0, 16).replace('T', ' ');
			}
		}
	}
</script>

<style>
.order-list-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.loading-indicator, .empty-orders {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 300rpx;
	color: #999;
	font-size: 28rpx;
}

.order-list {
	padding: 20rpx;
}

.order-item {
	background-color: #fff;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
	padding: 20rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 15rpx;
	padding-bottom: 15rpx;
	border-bottom: 1rpx solid #eee;
}

.order-no {
	font-size: 26rpx;
	color: #666;
}

.order-status {
	font-size: 26rpx;
	color: #ff6700; /* 橙色突出状态 */
	font-weight: bold;
}

.order-body {
	margin-bottom: 15rpx;
	font-size: 28rpx;
	color: #333;
}

.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 24rpx;
	color: #999;
}
</style>