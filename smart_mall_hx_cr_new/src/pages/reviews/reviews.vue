<template>
	<view class="reviews-container">
		<!-- 页面标题 -->
		<view class="page-header">
			<view class="back-btn" @click="goBack">
				<text class="iconfont">&#xe60d;</text>
			</view>
			<view class="page-title">商品评价</view>
			<view class="placeholder"></view>
		</view>
		
		<!-- 评分统计 -->
		<view class="rating-summary" v-if="stats">
			<view class="avg-rating">
				<text class="rating-number">{{ stats.avgRating ? stats.avgRating.toFixed(1) : '0.0' }}</text>
				<view class="rating-stars">
					<text v-for="n in 5" :key="n" class="rating-star" :class="{ 'active': n <= Math.round(stats.avgRating || 0) }">★</text>
				</view>
			</view>
			<view class="rating-bars">
				<view class="rating-bar-item" v-for="i in 5" :key="i">
					<text class="rating-label">{{ 6-i }}星</text>
					<view class="rating-bar">
						<view class="rating-bar-fill" :style="{ width: getStarPercentage(6-i) + '%' }"></view>
					</view>
					<text class="rating-count">{{ getStarCount(6-i) }}</text>
				</view>
			</view>
		</view>
		
		<!-- 评价列表 -->
		<view class="review-list" v-if="reviews.length > 0">
			<view class="review-item" v-for="(review, index) in reviews" :key="index">
				<view class="review-header">
					<image class="reviewer-avatar" :src="review.avatar || '/static/default_avatar.png'"></image>
					<view class="reviewer-info">
						<text class="reviewer-name">{{ review.username }}</text>
						<view class="review-rating">
							<text v-for="n in 5" :key="n" class="rating-star" :class="{ 'active': n <= review.rating }">★</text>
						</view>
					</view>
					<text class="review-date">{{ formatDate(review.createTime) }}</text>
				</view>
				<view class="review-content">{{ review.content }}</view>
				<view class="review-images" v-if="review.images && review.images.length > 0">
					<image
						v-for="(img, imgIndex) in review.images"
						:key="imgIndex"
						:src="img"
						class="review-image"
						@click="previewImage(review.images, imgIndex)"
					></image>
				</view>
			</view>
		</view>
		
		<!-- 加载更多 -->
		<view class="load-more" v-if="reviews.length > 0">
			<text v-if="hasMore && !isLoading" @click="loadMoreReviews">加载更多</text>
			<text v-else-if="isLoading">加载中...</text>
			<text v-else>没有更多评价了</text>
		</view>
		
		<!-- 暂无评价 -->
		<view class="empty-reviews" v-if="reviews.length === 0 && !isLoading">
			<image class="empty-icon" src="/static/empty.png"></image>
			<text>暂无评价</text>
		</view>
		
		<!-- 加载中 -->
		<view class="loading" v-if="isLoading && reviews.length === 0">
			<text>加载中...</text>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			itemId: null,
			reviews: [],
			stats: null,
			page: 1,
			size: 10,
			hasMore: true,
			isLoading: false
		}
	},
	onLoad(options) {
		this.itemId = options.itemId;
		if (!this.itemId) {
			uni.showToast({
				title: '商品ID不存在',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
			return;
		}
		
		// 加载评价列表和统计数据
		this.loadReviews();
		this.loadStats();
	},
	methods: {
		// 加载评价列表
		loadReviews() {
			if (this.isLoading) return;
			
			this.isLoading = true;
			uni.request({
				url: `${API_BASE_URL}/comment/list`,
				method: 'GET',
				data: {
					itemId: this.itemId,
					page: this.page,
					size: this.size
				},
				success: (res) => {
					if (res.statusCode === 200 && res.data && res.data.success) {
						const newReviews = res.data.data || [];
						
						// 如果是第一页，直接替换数据
						if (this.page === 1) {
							this.reviews = newReviews;
						} else {
							// 否则追加数据
							this.reviews = [...this.reviews, ...newReviews];
						}
						
						// 判断是否还有更多数据
						this.hasMore = newReviews.length >= this.size;
					} else {
						uni.showToast({
							title: res.data?.message || '获取评价失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('获取评价列表失败', err);
					uni.showToast({
						title: '网络错误，请稍后重试',
						icon: 'none'
					});
				},
				complete: () => {
					this.isLoading = false;
				}
			});
		},
		
		// 加载评分统计
		loadStats() {
			uni.request({
				url: `${API_BASE_URL}/comment/stats`,
				method: 'GET',
				data: {
					itemId: this.itemId
				},
				success: (res) => {
					if (res.statusCode === 200 && res.data && res.data.success) {
						this.stats = res.data.data || {};
					}
				},
				fail: (err) => {
					console.error('获取评分统计失败', err);
				}
			});
		},
		
		// 加载更多评价
		loadMoreReviews() {
			if (this.hasMore && !this.isLoading) {
				this.page++;
				this.loadReviews();
			}
		},
		
		// 获取星级百分比
		getStarPercentage(star) {
			if (!this.stats || !this.stats.totalCount || this.stats.totalCount === 0) {
				return 0;
			}
			
			let count = 0;
			switch (star) {
				case 5: count = this.stats.fiveStarCount || 0; break;
				case 4: count = this.stats.fourStarCount || 0; break;
				case 3: count = this.stats.threeStarCount || 0; break;
				case 2: count = this.stats.twoStarCount || 0; break;
				case 1: count = this.stats.oneStarCount || 0; break;
			}
			
			return (count / this.stats.totalCount) * 100;
		},
		
		// 获取星级数量
		getStarCount(star) {
			if (!this.stats) return 0;
			
			switch (star) {
				case 5: return this.stats.fiveStarCount || 0;
				case 4: return this.stats.fourStarCount || 0;
				case 3: return this.stats.threeStarCount || 0;
				case 2: return this.stats.twoStarCount || 0;
				case 1: return this.stats.oneStarCount || 0;
				default: return 0;
			}
		},
		
		// 预览图片
		previewImage(images, index) {
			uni.previewImage({
				urls: images,
				current: index
			});
		},
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return '';
			
			const date = new Date(dateStr);
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
		},
		
		// 返回上一页
		goBack() {
			uni.navigateBack();
		}
	},
	// 下拉刷新
	onPullDownRefresh() {
		this.page = 1;
		this.hasMore = true;
		this.loadReviews();
		this.loadStats();
		setTimeout(() => {
			uni.stopPullDownRefresh();
		}, 1000);
	},
	// 触底加载更多
	onReachBottom() {
		this.loadMoreReviews();
	}
}
</script>

<style>
.reviews-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.page-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 30rpx;
	background-color: #fff;
	position: sticky;
	top: 0;
	z-index: 100;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.back-btn {
	width: 60rpx;
	height: 60rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.page-title {
	font-size: 32rpx;
	font-weight: 500;
}

.placeholder {
	width: 60rpx;
}

.rating-summary {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
}

.avg-rating {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-right: 40rpx;
	min-width: 150rpx;
}

.rating-number {
	font-size: 48rpx;
	font-weight: bold;
	color: #ff6700;
	line-height: 1.2;
}

.rating-stars {
	display: flex;
	margin-top: 10rpx;
}

.rating-star {
	font-size: 24rpx;
	color: #ddd;
	margin: 0 2rpx;
}

.rating-star.active {
	color: #ff6700;
}

.rating-bars {
	flex: 1;
}

.rating-bar-item {
	display: flex;
	align-items: center;
	margin-bottom: 10rpx;
}

.rating-label {
	width: 60rpx;
	font-size: 24rpx;
	color: #666;
}

.rating-bar {
	flex: 1;
	height: 16rpx;
	background-color: #eee;
	border-radius: 8rpx;
	overflow: hidden;
	margin: 0 20rpx;
}

.rating-bar-fill {
	height: 100%;
	background-color: #ff6700;
}

.rating-count {
	width: 60rpx;
	font-size: 24rpx;
	color: #999;
	text-align: right;
}

.review-list {
	background-color: #fff;
}

.review-item {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.review-header {
	display: flex;
	align-items: center;
	margin-bottom: 20rpx;
}

.reviewer-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 20rpx;
}

.reviewer-info {
	flex: 1;
}

.reviewer-name {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 6rpx;
}

.review-rating {
	display: flex;
}

.review-date {
	font-size: 24rpx;
	color: #999;
}

.review-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.5;
	margin-bottom: 20rpx;
}

.review-images {
	display: flex;
	flex-wrap: wrap;
}

.review-image {
	width: 160rpx;
	height: 160rpx;
	margin-right: 10rpx;
	margin-bottom: 10rpx;
	border-radius: 8rpx;
}

.load-more {
	text-align: center;
	padding: 30rpx;
	color: #999;
	font-size: 28rpx;
}

.empty-reviews {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	color: #999;
	font-size: 28rpx;
}

.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 20rpx;
}

.loading {
	text-align: center;
	padding: 30rpx;
	color: #999;
	font-size: 28rpx;
}
</style>
