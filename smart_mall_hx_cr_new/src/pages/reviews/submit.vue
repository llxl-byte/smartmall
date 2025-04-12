<template>
	<view class="submit-review-container">
		<!-- 页面标题 -->
		<view class="page-header">
			<view class="back-btn" @click="goBack">
				<text class="iconfont">&#xe60d;</text>
			</view>
			<view class="page-title">发表评价</view>
			<view class="placeholder"></view>
		</view>
		
		<!-- 商品信息 -->
		<view class="item-info">
			<image class="item-image" :src="item.mainImage || '/static/default_item.png'"></image>
			<view class="item-detail">
				<view class="item-name">{{ item.name }}</view>
				<view class="item-price">¥{{ item.price }}</view>
			</view>
		</view>
		
		<!-- 评分 -->
		<view class="rating-section">
			<view class="section-title">商品评分</view>
			<view class="rating-stars">
				<text 
					v-for="n in 5" 
					:key="n" 
					class="rating-star" 
					:class="{ 'active': n <= rating }"
					@click="setRating(n)"
				>★</text>
			</view>
			<view class="rating-text">{{ getRatingText() }}</view>
		</view>
		
		<!-- 评价内容 -->
		<view class="content-section">
			<view class="section-title">评价内容</view>
			<textarea 
				class="review-content" 
				v-model="content" 
				placeholder="请分享您对商品的使用体验，例如外观、使用感受等" 
				maxlength="500"
			></textarea>
			<view class="word-count">{{ content.length }}/500</view>
		</view>
		
		<!-- 上传图片 -->
		<view class="images-section">
			<view class="section-title">上传图片 (选填)</view>
			<view class="image-upload-area">
				<view 
					class="image-item" 
					v-for="(img, index) in images" 
					:key="index"
				>
					<image class="uploaded-image" :src="img"></image>
					<view class="delete-btn" @click="deleteImage(index)">×</view>
				</view>
				<view class="upload-btn" @click="chooseImage" v-if="images.length < 6">
					<text class="upload-icon">+</text>
					<text class="upload-text">上传图片</text>
				</view>
			</view>
			<view class="upload-tip">最多上传6张图片</view>
		</view>
		
		<!-- 匿名评价 -->
		<view class="anonymous-section">
			<view class="anonymous-switch">
				<text>匿名评价</text>
				<switch :checked="isAnonymous" @change="toggleAnonymous" color="#ff6700" />
			</view>
			<view class="anonymous-tip">开启后，评价中将不会显示您的昵称和头像</view>
		</view>
		
		<!-- 提交按钮 -->
		<view class="submit-btn-area">
			<button class="submit-btn" @click="submitReview" :disabled="!canSubmit">提交评价</button>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			itemId: null,
			orderId: null,
			item: {},
			rating: 5,
			content: '',
			images: [],
			isAnonymous: false,
			isSubmitting: false
		}
	},
	computed: {
		canSubmit() {
			return this.content.trim().length > 0 && this.rating > 0 && !this.isSubmitting;
		}
	},
	onLoad(options) {
		this.itemId = options.itemId;
		this.orderId = options.orderId;
		
		if (!this.itemId || !this.orderId) {
			uni.showToast({
				title: '参数错误',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
			return;
		}
		
		// 加载商品信息
		this.loadItemInfo();
		
		// 检查是否可以评价
		this.checkCanComment();
	},
	methods: {
		// 加载商品信息
		loadItemInfo() {
			uni.request({
				url: `${API_BASE_URL}/selectByItemId`,
				method: 'GET',
				data: {
					itemId: this.itemId
				},
				success: (res) => {
					if (res.statusCode === 200 && res.data) {
						this.item = res.data;
					} else {
						uni.showToast({
							title: '获取商品信息失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('获取商品信息失败', err);
					uni.showToast({
						title: '网络错误，请稍后重试',
						icon: 'none'
					});
				}
			});
		},
		
		// 检查是否可以评价
		checkCanComment() {
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({ url: '/pages/login/login' });
				}, 1500);
				return;
			}
			
			uni.request({
				url: `${API_BASE_URL}/comment/check`,
				method: 'GET',
				data: {
					userId: userInfo.id,
					itemId: this.itemId,
					orderId: this.orderId
				},
				success: (res) => {
					if (res.statusCode === 200 && res.data && res.data.success) {
						if (!res.data.data) {
							uni.showToast({
								title: '您已评价过该商品或无权评价',
								icon: 'none'
							});
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
						}
					}
				},
				fail: (err) => {
					console.error('检查评价权限失败', err);
				}
			});
		},
		
		// 设置评分
		setRating(rating) {
			this.rating = rating;
		},
		
		// 获取评分文字描述
		getRatingText() {
			switch (this.rating) {
				case 1: return '非常差';
				case 2: return '差';
				case 3: return '一般';
				case 4: return '好';
				case 5: return '非常好';
				default: return '';
			}
		},
		
		// 选择图片
		chooseImage() {
			uni.chooseImage({
				count: 6 - this.images.length,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					// 这里简单处理，实际项目中应该上传图片到服务器
					this.images = [...this.images, ...res.tempFilePaths];
				}
			});
		},
		
		// 删除图片
		deleteImage(index) {
			this.images.splice(index, 1);
		},
		
		// 切换匿名状态
		toggleAnonymous(e) {
			this.isAnonymous = e.detail.value;
		},
		
		// 提交评价
		submitReview() {
			if (!this.canSubmit) return;
			
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({ url: '/pages/login/login' });
				}, 1500);
				return;
			}
			
			this.isSubmitting = true;
			uni.showLoading({ title: '提交中...' });
			
			// 构建评价数据
			const reviewData = {
				userId: userInfo.id,
				itemId: this.itemId,
				orderId: this.orderId,
				content: this.content,
				rating: this.rating,
				isAnonymous: this.isAnonymous ? 1 : 0,
				images: this.images.length > 0 ? this.images.join(',') : null
			};
			
			// 发送评价请求
			uni.request({
				url: `${API_BASE_URL}/comment/add`,
				method: 'POST',
				header: {
					'content-type': 'application/json'
				},
				data: reviewData,
				success: (res) => {
					if (res.statusCode === 200 && res.data && res.data.success) {
						uni.showToast({
							title: '评价成功',
							icon: 'success'
						});
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: res.data?.message || '评价失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('提交评价失败', err);
					uni.showToast({
						title: '网络错误，请稍后重试',
						icon: 'none'
					});
				},
				complete: () => {
					this.isSubmitting = false;
					uni.hideLoading();
				}
			});
		},
		
		// 返回上一页
		goBack() {
			uni.navigateBack();
		}
	}
}
</script>

<style>
.submit-review-container {
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

.item-info {
	display: flex;
	padding: 30rpx;
	background-color: #fff;
	margin-bottom: 20rpx;
}

.item-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
}

.item-detail {
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
}

.item-price {
	font-size: 32rpx;
	color: #ff6700;
	font-weight: bold;
}

.rating-section, .content-section, .images-section, .anonymous-section {
	background-color: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 30rpx;
	color: #333;
	font-weight: 500;
	margin-bottom: 20rpx;
}

.rating-stars {
	display: flex;
	justify-content: center;
	margin-bottom: 20rpx;
}

.rating-star {
	font-size: 60rpx;
	color: #ddd;
	margin: 0 10rpx;
}

.rating-star.active {
	color: #ff6700;
}

.rating-text {
	text-align: center;
	font-size: 28rpx;
	color: #ff6700;
}

.review-content {
	width: 100%;
	height: 240rpx;
	padding: 20rpx;
	box-sizing: border-box;
	border: 1rpx solid #eee;
	border-radius: 8rpx;
	font-size: 28rpx;
	line-height: 1.5;
}

.word-count {
	text-align: right;
	font-size: 24rpx;
	color: #999;
	margin-top: 10rpx;
}

.image-upload-area {
	display: flex;
	flex-wrap: wrap;
}

.image-item, .upload-btn {
	width: 160rpx;
	height: 160rpx;
	margin-right: 20rpx;
	margin-bottom: 20rpx;
	border-radius: 8rpx;
	overflow: hidden;
	position: relative;
}

.uploaded-image {
	width: 100%;
	height: 100%;
}

.delete-btn {
	position: absolute;
	top: 0;
	right: 0;
	width: 40rpx;
	height: 40rpx;
	background-color: rgba(0, 0, 0, 0.5);
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 24rpx;
	border-bottom-left-radius: 8rpx;
}

.upload-btn {
	border: 1rpx dashed #ddd;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.upload-icon {
	font-size: 48rpx;
	color: #999;
	margin-bottom: 10rpx;
}

.upload-text {
	font-size: 24rpx;
	color: #999;
}

.upload-tip {
	font-size: 24rpx;
	color: #999;
	margin-top: 10rpx;
}

.anonymous-switch {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 30rpx;
	color: #333;
	margin-bottom: 10rpx;
}

.anonymous-tip {
	font-size: 24rpx;
	color: #999;
}

.submit-btn-area {
	padding: 30rpx;
}

.submit-btn {
	width: 100%;
	height: 80rpx;
	line-height: 80rpx;
	background-color: #ff6700;
	color: #fff;
	font-size: 30rpx;
	border-radius: 40rpx;
}

.submit-btn[disabled] {
	background-color: #ffc1a1;
}
</style>
