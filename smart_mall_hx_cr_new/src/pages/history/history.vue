<template>
	<view class="history-container">
		<view class="header">
			<text class="title">浏览历史</text>
			<text class="clear-btn" @click="clearHistory">清空</text>
		</view>

		<!-- 加载中 -->
		<view class="loading" v-if="loading">
			<text>加载中...</text>
		</view>

		<!-- 空状态 -->
		<view class="empty-state" v-else-if="historyItems.length === 0">
			<image class="empty-icon" src="/static/empty_history.png" mode="aspectFit"></image>
			<text class="empty-text">暂无浏览记录</text>
			<button class="go-shopping-btn" @click="goToHome">去逛逛</button>
		</view>

		<!-- 历史列表 -->
		<view class="history-list" v-else>
			<view class="history-date" v-for="(group, date) in groupedHistory" :key="date">
				<view class="date-header">
					<text class="date-text">{{ formatDate(date) }}</text>
				</view>
				<view
					class="history-item"
					v-for="(item, index) in group"
					:key="index"
					@click="goToDetail(item.id)"
				>
					<image class="item-image" :src="item.mainImage || '/static/default_item.png'" mode="aspectFill"></image>
					<view class="item-info">
						<text class="item-name">{{ item.name }}</text>
						<text class="item-price">¥{{ item.price }}</text>
						<view class="item-actions">
							<view class="action-btn add-cart-btn" @click.stop="addToCart(item)">加入购物车</view>
							<view class="action-btn favorite-btn" @click.stop="toggleFavorite(item)">
								<text>{{ item.isFavorite ? '已收藏' : '收藏' }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			loading: true,
			historyItems: [],
			userInfo: null,
			favoriteIds: new Set() // 用于存储已收藏的商品ID
		}
	},
	computed: {
		// 按日期分组的历史记录
		groupedHistory() {
			const groups = {};

			this.historyItems.forEach(item => {
				// 获取日期部分（不包含时间）
				const date = item.viewTime ? item.viewTime.split(' ')[0] : '未知日期';

				// 添加收藏状态
				item.isFavorite = this.favoriteIds.has(item.id);

				if (!groups[date]) {
					groups[date] = [];
				}

				groups[date].push(item);
			});

			return groups;
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

		// 加载浏览历史
		this.loadHistory();

		// 加载收藏商品ID
		this.loadFavoriteIds();
	},
	methods: {
		// 加载浏览历史
		loadHistory() {
			if (!this.userInfo || !this.userInfo.id) return;

			this.loading = true;

			uni.request({
				url: `${API_BASE_URL}/behavior/items?userId=${this.userInfo.id}&behaviorType=1`,
				success: (res) => {
					this.loading = false;
					if (res.data && res.data.success) {
						this.historyItems = res.data.data || [];
					} else {
						uni.showToast({
							title: '获取历史记录失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					this.loading = false;
					console.error('获取历史记录失败', err);
					uni.showToast({
						title: '获取历史记录失败',
						icon: 'none'
					});
				}
			});
		},

		// 加载收藏商品ID
		loadFavoriteIds() {
			if (!this.userInfo || !this.userInfo.id) return;

			uni.request({
				url: `${API_BASE_URL}/behavior/items?userId=${this.userInfo.id}&behaviorType=4`,
				success: (res) => {
					if (res.data && res.data.success && res.data.data) {
						// 将收藏商品ID存入Set
						this.favoriteIds = new Set(res.data.data.map(item => item.id));
					}
				}
			});
		},

		// 格式化日期
		formatDate(dateStr) {
			const today = new Date();
			today.setHours(0, 0, 0, 0);

			const yesterday = new Date(today);
			yesterday.setDate(yesterday.getDate() - 1);

			const date = new Date(dateStr);

			if (date.getTime() === today.getTime()) {
				return '今天';
			} else if (date.getTime() === yesterday.getTime()) {
				return '昨天';
			} else {
				return dateStr;
			}
		},

		// 跳转到商品详情
		goToDetail(id) {
			if (!id) return;

			uni.navigateTo({
				url: `/pages/item/detail?id=${id}`
			});
		},

		// 添加到购物车
		addToCart(item) {
			if (!this.userInfo || !this.userInfo.id || !item || !item.id) return;

			uni.showLoading({
				title: '正在添加...'
			});

			uni.request({
				url: `${API_BASE_URL}/addCart`,
				method: 'POST',
				header: {
					'content-type': 'application/json'
				},
				data: {
					userId: this.userInfo.id,
					itemId: item.id,
					quantity: 1
				},
				success: (res) => {
					uni.hideLoading();
					if (res.statusCode === 200 && res.data === true) {
						uni.showToast({
							title: '已加入购物车',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: '添加失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					uni.hideLoading();
					console.error('添加购物车失败', err);
					uni.showToast({
						title: '添加失败',
						icon: 'none'
					});
				}
			});
		},

		// 切换收藏状态
		toggleFavorite(item) {
			if (!this.userInfo || !this.userInfo.id || !item || !item.id) return;

			const isFavorite = this.favoriteIds.has(item.id);

			if (isFavorite) {
				// 取消收藏
				uni.request({
					url: `${API_BASE_URL}/behavior/remove`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						userId: this.userInfo.id,
						itemId: item.id,
						behaviorType: 4 // 4-收藏
					},
					success: (res) => {
						if (res.data && res.data.success) {
							this.favoriteIds.delete(item.id);
							item.isFavorite = false;
							uni.showToast({
								title: '已取消收藏',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: '操作失败',
								icon: 'none'
							});
						}
					}
				});
			} else {
				// 添加收藏
				uni.request({
					url: `${API_BASE_URL}/behavior/record`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						userId: this.userInfo.id,
						itemId: item.id,
						behaviorType: 4 // 4-收藏
					},
					success: (res) => {
						if (res.data && res.data.success) {
							this.favoriteIds.add(item.id);
							item.isFavorite = true;
							uni.showToast({
								title: '已收藏',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: '操作失败',
								icon: 'none'
							});
						}
					}
				});
			}
		},

		// 清空历史记录
		clearHistory() {
			if (!this.userInfo || !this.userInfo.id) return;

			uni.showModal({
				title: '提示',
				content: '确定要清空所有浏览记录吗？',
				success: (res) => {
					if (res.confirm) {
						uni.showLoading({
							title: '处理中...'
						});

						uni.request({
							url: `${API_BASE_URL}/behavior/clear`,
							method: 'POST',
							header: {
								'content-type': 'application/x-www-form-urlencoded'
							},
							data: {
								userId: this.userInfo.id,
								behaviorType: 1 // 1-浏览
							},
							success: (res) => {
								uni.hideLoading();
								if (res.data && res.data.success) {
									this.historyItems = [];
									uni.showToast({
										title: '已清空历史记录',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: '操作失败',
										icon: 'none'
									});
								}
							},
							fail: (err) => {
								uni.hideLoading();
								console.error('清空历史记录失败', err);
								uni.showToast({
									title: '操作失败',
									icon: 'none'
								});
							}
						});
					}
				}
			});
		},

		// 跳转到首页
		goToHome() {
			uni.switchTab({
				url: '/pages/index/index'
			});
		}
	}
}
</script>

<style>
.history-container {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 30rpx;
}

.header {
	background-color: #fff;
	padding: 30rpx;
	border-bottom: 1rpx solid #eee;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

.clear-btn {
	font-size: 28rpx;
	color: #999;
}

.loading, .empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 30rpx;
	color: #999;
	margin-bottom: 30rpx;
}

.go-shopping-btn {
	background-color: #ff6700;
	color: #fff;
	font-size: 28rpx;
	padding: 15rpx 40rpx;
	border-radius: 40rpx;
	border: none;
}

.history-list {
	padding: 20rpx;
}

.history-date {
	margin-bottom: 30rpx;
}

.date-header {
	margin-bottom: 15rpx;
}

.date-text {
	font-size: 28rpx;
	color: #999;
	background-color: #f5f5f5;
	padding: 10rpx 20rpx;
	border-radius: 20rpx;
}

.history-item {
	display: flex;
	background-color: #fff;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
	padding: 20rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.item-image {
	width: 180rpx;
	height: 180rpx;
	border-radius: 10rpx;
	margin-right: 20rpx;
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
	margin-bottom: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.item-price {
	font-size: 32rpx;
	color: #ff6700;
	font-weight: bold;
	margin-bottom: 20rpx;
}

.item-actions {
	display: flex;
	justify-content: space-between;
}

.action-btn {
	padding: 10rpx 20rpx;
	border-radius: 30rpx;
	font-size: 24rpx;
	text-align: center;
}

.add-cart-btn {
	background-color: #ff9500;
	color: #fff;
	flex: 1;
	margin-right: 20rpx;
}

.favorite-btn {
	background-color: #f5f5f5;
	color: #666;
	flex: 1;
}
</style>
