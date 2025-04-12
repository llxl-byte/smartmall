<template>
	<view class="order-list-container">
		<!-- 状态过滤器 -->
		<view class="status-filter">
			<view
				v-for="(status, index) in statusOptions"
				:key="index"
				:class="['status-item', currentStatus === status.value ? 'active' : '']"
				@click="filterByStatus(status.value)"
			>
				{{ status.label }}
			</view>
		</view>

		<!-- 加载状态 -->
		<view v-if="isLoading" class="loading-indicator">
			<text>正在加载订单...</text>
		</view>

		<!-- 订单列表 -->
		<view v-else-if="filteredOrderList.length > 0" class="order-list">
			<view class="order-item" v-for="(order, index) in filteredOrderList" :key="order.id || index">
				<view class="order-header">
					<text class="order-no">订单号: {{ order.orderNo }}</text>
					<text class="order-status">{{ formatOrderStatus(order.status) }}</text>
				</view>
				<view class="order-body">
					<!-- 订单商品列表 -->
					<view class="order-items" v-if="order.items && order.items.length > 0">
						<view class="order-item-product" v-for="(item, itemIndex) in order.items" :key="itemIndex">
							<image class="item-image" :src="item.itemImage || '/static/default_item.png'"></image>
							<view class="item-info">
								<text class="item-name">{{ item.itemName }}</text>
								<view class="item-price-qty">
									<text class="item-price">¥{{ item.price }}</text>
									<text class="item-qty">x{{ item.quantity }}</text>
								</view>
							</view>
						</view>
					</view>
					<view class="order-summary">
						<text class="order-total">订单金额: ¥{{ order.totalAmount }}</text>
						<text class="order-count" v-if="order.items && order.items.length > 0">共{{ getTotalQuantity(order.items) }}件商品</text>
					</view>
				</view>
				<view class="order-footer">
					<text class="order-time">下单时间: {{ formatOrderTime(order.createTime) }}</text>
					<view class="order-actions">
						<!-- 已完成订单显示评价按钮 -->
						<button
							v-if="order.status === 3"
							class="action-btn review-btn"
							@click="goToReview(order)"
						>评价商品</button>

						<!-- 待付款订单显示付款按钮 -->
						<button
							v-if="order.status === 0"
							class="action-btn pay-btn"
							@click="payOrder(order)"
						>立即付款</button>

						<!-- 待收货订单显示确认收货按钮 -->
						<button
							v-if="order.status === 2"
							class="action-btn confirm-btn"
							@click="confirmReceive(order)"
						>确认收货</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view v-else class="empty-orders">
			<text v-if="currentStatus !== -1">没有{{ getStatusLabel(currentStatus) }}的订单~</text>
			<text v-else>您还没有订单哦~</text>
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
				userId: null, // 用户ID
				currentStatus: -1, // 当前选中的状态过滤器，-1表示全部
				statusOptions: [
					{ label: '全部', value: -1 },
					{ label: '待付款', value: 0 },
					{ label: '待发货', value: 1 },
					{ label: '待收货', value: 2 },
					{ label: '已完成', value: 3 },
					{ label: '已取消', value: 4 }
				]
			}
		},
		computed: {
			// 根据当前选中的状态过滤订单列表
			filteredOrderList() {
				if (this.currentStatus === -1) {
					// 如果选择“全部”，返回完整列表
					return this.orderList;
				} else {
					// 否则根据状态过滤
					return this.orderList.filter(order => order.status === this.currentStatus);
				}
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
			// 根据状态过滤订单
			filterByStatus(status) {
				this.currentStatus = status;
			},

			// 获取状态标签
			getStatusLabel(statusValue) {
				const status = this.statusOptions.find(s => s.value === statusValue);
				return status ? status.label : '未知状态';
			},

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
			},

			// 计算订单商品总数量
			getTotalQuantity(items) {
				if (!items || !Array.isArray(items)) return 0;
				return items.reduce((total, item) => total + (item.quantity || 0), 0);
			},

			// 跳转到评价页面
			goToReview(order) {
				if (!order || !order.items || order.items.length === 0) {
					uni.showToast({
						title: '没有可评价的商品',
						icon: 'none'
					});
					return;
				}

				// 如果只有一个商品，直接跳转到评价页面
				if (order.items.length === 1) {
					const item = order.items[0];
					uni.navigateTo({
						url: `/pages/reviews/submit?itemId=${item.itemId}&orderId=${order.id}`
					});
					return;
				}

				// 如果有多个商品，弹出选择对话框
				uni.showActionSheet({
					itemList: order.items.map(item => item.itemName),
					success: (res) => {
						const selectedItem = order.items[res.tapIndex];
						uni.navigateTo({
							url: `/pages/reviews/submit?itemId=${selectedItem.itemId}&orderId=${order.id}`
						});
					}
				});
			},

			// 支付订单
			payOrder(order) {
				if (!order || !order.id) {
					uni.showToast({
						title: '订单信息不完整',
						icon: 'none'
					});
					return;
				}

				uni.showLoading({ title: '正在支付...' });

				// 模拟支付成功
				setTimeout(() => {
					// 更新订单状态（前端模拟）
					order.status = 1; // 将状态更新为已付款/待发货

					// 将更新后的订单保存到本地列表中
					const index = this.orderList.findIndex(o => o.id === order.id);
					if (index !== -1) {
						this.orderList.splice(index, 1, order);
					}

					uni.hideLoading();
					uni.showToast({
						title: '支付成功',
						icon: 'success'
					});

					// 刷新订单列表，但保留当前列表状态
					// 如果后端接口正常，可以取消注释下面的代码
					// setTimeout(() => {
					//   this.loadOrders();
					// }, 1500);
				}, 2000);
			},

			// 确认收货
			confirmReceive(order) {
				if (!order || !order.id) {
					uni.showToast({
						title: '订单信息不完整',
						icon: 'none'
					});
					return;
				}

				uni.showModal({
					title: '确认收货',
					content: '您确定已经收到商品吗？',
					success: (res) => {
						if (res.confirm) {
							uni.showLoading({ title: '处理中...' });

							// 模拟确认收货成功
							setTimeout(() => {
								// 更新订单状态（前端模拟）
								order.status = 3; // 将状态更新为已完成

								// 将更新后的订单保存到本地列表中
								const index = this.orderList.findIndex(o => o.id === order.id);
								if (index !== -1) {
									this.orderList.splice(index, 1, order);
								}

								uni.hideLoading();
								uni.showToast({
									title: '确认收货成功',
									icon: 'success'
								});

								// 刷新订单列表，但保留当前列表状态
								// 如果后端接口正常，可以取消注释下面的代码
								// setTimeout(() => {
								//   this.loadOrders();
								// }, 1500);
							}, 2000);
						}
					}
				});
			}
		}
	}
</script>

<style>
.order-list-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

/* 状态过滤器样式 */
.status-filter {
	display: flex;
	overflow-x: auto;
	background-color: #fff;
	padding: 20rpx 10rpx;
	border-bottom: 1rpx solid #eee;
}

.status-item {
	flex: 0 0 auto;
	padding: 10rpx 30rpx;
	margin: 0 10rpx;
	border-radius: 30rpx;
	font-size: 28rpx;
	color: #666;
	background-color: #f5f5f5;
}

.status-item.active {
	color: #fff;
	background-color: #ff6700;
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

/* 订单商品列表样式 */
.order-items {
	margin-bottom: 15rpx;
}

.order-item-product {
	display: flex;
	padding: 10rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.order-item-product:last-child {
	border-bottom: none;
}

.item-image {
	width: 120rpx;
	height: 120rpx;
	border-radius: 8rpx;
	margin-right: 15rpx;
}

.item-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.item-name {
	font-size: 28rpx;
	color: #333;
	line-height: 1.4;
	margin-bottom: 10rpx;
	/* 文字超出部分显示省略号 */
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.item-price-qty {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.item-price {
	font-size: 26rpx;
	color: #ff6700;
}

.item-qty {
	font-size: 24rpx;
	color: #999;
}

/* 订单汇总信息 */
.order-summary {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 15rpx;
	border-top: 1rpx solid #f5f5f5;
}

.order-total {
	font-size: 28rpx;
	color: #333;
	font-weight: bold;
}

.order-count {
	font-size: 24rpx;
	color: #999;
}

.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 24rpx;
	color: #999;
	padding-top: 15rpx;
	border-top: 1rpx solid #f5f5f5;
}

/* 订单操作按钮 */
.order-actions {
	display: flex;
}

.action-btn {
	margin-left: 15rpx;
	padding: 0 20rpx;
	height: 60rpx;
	line-height: 60rpx;
	font-size: 24rpx;
	border-radius: 30rpx;
	background-color: #fff;
	border: 1rpx solid #ddd;
}

.review-btn {
	color: #ff6700;
	border-color: #ff6700;
}

.pay-btn {
	color: #fff;
	background-color: #ff6700;
	border-color: #ff6700;
}

.confirm-btn {
	color: #fff;
	background-color: #67c23a;
	border-color: #67c23a;
}
</style>