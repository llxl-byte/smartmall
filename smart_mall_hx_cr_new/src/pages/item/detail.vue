<template>
	<view class="item-detail-container">
		<!-- 加载状态 -->
		<view v-if="isLoading" class="loading-indicator">
			<text>加载中...</text>
		</view>
		<!-- 商品轮播图 -->
		<!-- 主要内容区域 -->
		<view v-if="!isLoading">
		<swiper class="swiper" indicator-dots indicator-color="rgba(255, 255, 255, 0.6)" indicator-active-color="#ff6700" autoplay circular>
			<swiper-item v-for="(image, index) in itemImages" :key="index">
				<image :src="image" mode="aspectFill" class="swiper-image" @click="previewImage(index)"></image>
			</swiper-item>
		</swiper>

		<!-- 商品基本信息 -->
		<view class="item-info">
			<view class="item-price-row">
				<view class="item-price">
					<text class="price">¥{{ item.price }}</text>
					<text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
				</view>
				<view class="collect-btn" @click="toggleCollect">
					<text class="collect-icon" :class="{ 'collected': isCollected }">♥</text>
					<text class="collect-text">{{ isCollected ? '已收藏' : '收藏' }}</text>
				</view>
			</view>
			<view class="item-name">{{ item.name }}</view>
			<view class="item-brief">{{ item.brief || '暂无简介' }}</view>
			<view class="item-stats">
				<text class="item-sales">销量: {{ item.sales || 0 }}</text>
				<text class="item-rating">评分: {{ item.rating || '暂无评分' }}</text>
			</view>
		</view>

		<!-- 商品规格选择 -->
		<view class="item-spec">
			<view class="spec-title">规格选择</view>
			<view class="spec-content">
				<!-- 颜色选择 -->
				<view class="spec-item" v-if="colors.length > 0">
					<text class="spec-label">颜色</text>
					<view class="spec-options">
						<view
							v-for="(color, index) in colors"
							:key="index"
							class="spec-option"
							:class="{ 'active': selectedColor === color }"
							@click="selectedColor = color"
						>
							{{ color }}
						</view>
					</view>
				</view>

				<!-- 尺寸选择 -->
				<view class="spec-item" v-if="sizes.length > 0">
					<text class="spec-label">尺寸</text>
					<view class="spec-options">
						<view
							v-for="(size, index) in sizes"
							:key="index"
							class="spec-option"
							:class="{ 'active': selectedSize === size }"
							@click="selectedSize = size"
						>
							{{ size }}
						</view>
					</view>
				</view>

				<!-- 数量选择 -->
				<view class="spec-item">
					<text class="spec-label">数量</text>
					<view class="quantity-selector">
						<view class="quantity-btn" @click="decreaseQuantity">-</view>
						<input type="number" v-model="quantity" class="quantity-input" />
						<view class="quantity-btn" @click="increaseQuantity">+</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 商品评价 -->
		<view class="item-reviews">
			<view class="section-header">
				<text class="section-title">商品评价</text>
				<text class="section-more" @click="viewAllReviews">查看全部 ></text>
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
						<text class="review-date">{{ review.createTime }}</text>
					</view>
					<view class="review-content">{{ review.content }}</view>
					<view class="review-images" v-if="review.images && review.images.length > 0">
						<image
							v-for="(img, imgIndex) in review.images"
							:key="imgIndex"
							:src="img"
							class="review-image"
							@click="previewReviewImage(review.images, imgIndex)"
						></image>
					</view>
				</view>
			</view>

			<!-- 暂无评价 -->
			<view class="empty-reviews" v-else>
				<text>暂无评价</text>
			</view>
		</view>

		<!-- 商品详情 -->
		<view class="item-detail">
			<view class="section-header">
				<text class="section-title">商品详情</text>
			</view>
			<view class="detail-content">
				<rich-text :nodes="item.detail || '暂无详情'"></rich-text>
			</view>
		</view>

		<!-- 相似商品推荐 -->
		<view class="similar-items" v-if="similarItems.length > 0">
			<view class="section-header">
				<text class="section-title">相似商品推荐</text>
			</view>
			<scroll-view scroll-x class="similar-scroll">
				<view
					class="similar-item"
					v-for="(item, index) in similarItems"
					:key="index"
					@click="goToItemDetail(item.id)"
				>
					<image class="similar-image" :src="item.mainImage || '/static/default_item.png'"></image>
					<view class="similar-name">{{item.name}}</view>
					<view class="similar-price">¥{{item.price}}</view>
				</view>
			</scroll-view>
		</view>

		<!-- 底部操作栏 -->
		</view>
		<view class="action-bar">
			<view class="action-left">
				<view class="action-item" @click="goToHome">
					<image src="/static/home.png" class="action-icon"></image>
					<text>首页</text>
				</view>
				<view class="action-item" @click="goToCart">
					<image src="/static/cart_normal.png" class="action-icon"></image>
					<text>购物车</text>
					<view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
				</view>
				<view class="action-item" @click="contactService">
					<image src="/static/service.png" class="action-icon"></image>
					<text>客服</text>
				</view>
			</view>
			<view class="action-right">
				<view class="action-btn add-cart" @click="addToCart">加入购物车</view>
				<view class="action-btn buy-now" @click="buyNow">立即购买</view>
			</view>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
		data() {
			return {
				itemId: null,
				item: {}, // 商品基本信息
				itemImages: [], // 商品轮播图
				quantity: 1, // 购买数量
				isLoading: true, // 是否加载中
				isCollected: false, // 是否已收藏 (新增)
				colors: [], // 可选颜色 (新增)
				selectedColor: null, // 已选颜色 (新增)
				sizes: [], // 可选尺寸 (新增)
				selectedSize: null, // 已选尺寸 (新增)
				reviews: [], // 商品评价列表 (新增)
				cartCount: 0, // 购物车数量 (新增，可能需要从全局状态获取)
				similarItems: [] // 相似商品推荐
			}
		},
		onLoad(options) {
			// 获取路由参数中的商品ID
			this.itemId = options.id;
			// 加载商品详情
			this.loadItemDetail();
			// 记录浏览行为
			this.recordViewBehavior();
		},
		methods: {
			// 加载商品详情
			loadItemDetail() {
				if (!this.itemId) {
					uni.showToast({
						title: '商品ID不存在',
						icon: 'none'
					});
					return;
				}

				this.isLoading = true;
				uni.request({
					url: `${API_BASE_URL}/selectByItemId?itemId=${this.itemId}`,
					success: (res) => {
						if (res.data) {
							this.item = res.data;
							// 处理商品图片路径
							const processImageUrl = (url) => {
								if (!url) return url;
								// 如果已经是完整URL则直接返回
								if (url.startsWith('http')) return url;
								// 处理相对路径
								if (url.startsWith('/static/')) return url;
								// 其他情况添加/static/前缀
								return `/static/${url}`;
							};

							// 处理主图
							if (this.item.mainImage) {
								this.itemImages = [processImageUrl(this.item.mainImage)];
								console.log('处理后的主图路径:', this.itemImages[0]);
							}
							
							// 处理图片数组
							if (this.item.images) {
								try {
									const imagesArray = JSON.parse(this.item.images);
									if (Array.isArray(imagesArray) && imagesArray.length > 0) {
										const processedImages = imagesArray.map(img => processImageUrl(img));
										this.itemImages = [...this.itemImages, ...processedImages];
										console.log('处理后的图片数组:', processedImages);
									}
								} catch (e) {
									console.error('解析商品图片数组失败', e);
								}
							}

							// 如果没有图片，使用默认图片
							if (this.itemImages.length === 0) {
								this.itemImages = ['/static/default-avatar.png'];
								console.log('使用默认图片');
							}

							// 加载商品评价
							this.loadReviews();

							// 加载购物车数量
							this.loadCartCount();

							// 加载相似商品
							this.loadSimilarItems();

						} else {
							uni.showToast({
								title: '商品不存在或已下架',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('获取商品详情失败', err);
						uni.showToast({
							title: '获取商品详情失败',
							icon: 'none'
						});
					},
					complete: () => {
						this.isLoading = false;
					}
				});
			},

			// 加载商品评价
			loadReviews() {
				if (!this.itemId) return;

				uni.request({
					url: `${API_BASE_URL}/comment/list`,
					method: 'GET',
					data: {
						itemId: this.itemId,
						page: 1,
						size: 2 // 只显示2条评价
					},
					success: (res) => {
						if (res.statusCode === 200 && res.data && res.data.success) {
							this.reviews = res.data.data || [];

							// 处理评价图片
							this.reviews.forEach(review => {
								if (review.images && typeof review.images === 'string') {
									review.images = review.images.split(',');
								}
							});
						}
					},
					fail: (err) => {
						console.error('获取评价列表失败', err);
					}
				});
			},

			// 增加数量
			increaseQuantity() {
				if (this.quantity < 99) { // 假设最大购买量为99
					this.quantity++;
				}
			},

			// 减少数量
			decreaseQuantity() {
				if (this.quantity > 1) {
					this.quantity--;
				}
			},

			// 跳转到购物车
			goToCart() {
				uni.switchTab({
					url: '/pages/cart/cart'
				});
			},

			// 添加到购物车
			addToCart() {
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({ url: '/pages/login/login' });
					return;
				}
				// TODO: 添加规格选择校验
				// if (this.colors.length > 0 && !this.selectedColor) { ... }

				// 显示加载提示
				uni.showLoading({
					title: '正在添加...'
				});

				// 确保数据类型正确
				const userId = parseInt(userInfo.id);
				const itemId = parseInt(this.itemId);
				const quantity = parseInt(this.quantity);

				uni.request({
					url: `${API_BASE_URL}/addCart`,
					method: 'POST',
					header: {
						'content-type': 'application/json' // 使用JSON格式
					},
					data: {
						userId: userId,
						itemId: itemId,
						quantity: quantity
					},
					success: (res) => {
						console.log('添加购物车响应:', res);
						// 检查HTTP状态码
						if (res.statusCode === 200) {
							// 检查返回的Result对象
							if (res.data && res.data.success) {
								// 记录加入购物车行为
								this.recordBehavior(2); // 2-加入购物车

								uni.showToast({
									title: res.data.message || '添加成功',
									icon: 'success'
								});
								// 更新购物车角标数量
								this.loadCartCount();
							} else {
								uni.showToast({
									title: res.data && res.data.message ? res.data.message : '添加失败，请重试',
									icon: 'none'
								});
							}
						} else {
							uni.showToast({
								title: `服务器错误: ${res.statusCode}`,
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('添加购物车失败', err);
						uni.showToast({
							title: '添加购物车失败',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 加载购物车数量
			loadCartCount() {
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) return;

				uni.request({
					url: `${API_BASE_URL}/selectByUserId?userId=${userInfo.id}`,
					success: (res) => {
						if (res.data && Array.isArray(res.data)) {
							this.cartCount = res.data.length;
						}
					}
				});
			},

			// 立即购买
			buyNow() {
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({ url: '/pages/login/login' });
					return;
				}
				// TODO: 添加规格选择校验

				// 记录购买行为
				this.recordBehavior(3); // 3-购买

				// 创建临时订单并跳转到订单确认页
				// 注意：需要将已选规格等信息传递给确认页
				uni.navigateTo({
					url: `/pages/order/confirm?itemId=${this.itemId}&quantity=${this.quantity}` // TODO: 添加规格参数
				});
			},

			// === 新增方法占位符 ===
			previewImage(index) {
				console.log('预览图片:', index);
				// uni.previewImage API 实现图片预览
				uni.previewImage({
					urls: this.itemImages,
					current: index
				});
			},
			toggleCollect() {
				console.log('切换收藏状态');
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({ url: '/pages/login/login' });
					return;
				}

				// 切换收藏状态
				this.isCollected = !this.isCollected;

				// 记录收藏行为
				if (this.isCollected) {
					this.recordBehavior(4); // 4-收藏
				}

				uni.showToast({
					title: this.isCollected ? '收藏成功' : '取消收藏',
					icon: 'none'
				});
			},
			viewAllReviews() {
				console.log('查看全部评价');
				// 跳转到评价列表页面
				uni.navigateTo({ url: `/pages/reviews/reviews?itemId=${this.itemId}` });
			},
			previewReviewImage(images, index) {
				console.log('预览评价图片:', index);
				uni.previewImage({
					urls: images,
					current: index
				});
			},
			goToHome() {
				console.log('返回首页');
				uni.switchTab({ url: '/pages/index/index' });
			},

			// 记录浏览行为
			recordViewBehavior() {
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !this.itemId) {
					return;
				}

				// 记录浏览行为
				this.recordBehavior(1); // 1-浏览
			},

			// 记录用户行为
			recordBehavior(behaviorType) {
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !this.itemId) {
					return;
				}

				// 确保数据类型正确
				const userId = parseInt(userInfo.id);
				const itemId = parseInt(this.itemId);

				// 调用后端接口记录行为
				uni.request({
					url: `${API_BASE_URL}/behavior/record`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						userId: userId,
						itemId: itemId,
						behaviorType: behaviorType
					},
					success: (res) => {
						console.log('记录行为成功:', behaviorType);
					},
					fail: (err) => {
						console.error('记录行为失败:', err);
					}
				});
			},
			contactService() {
				console.log('联系客服');
				// 检查是否登录
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({ url: '/pages/login/login' });
					return;
				}
				// 跳转到智能客服页面
				uni.navigateTo({
					url: '/pages/chat/chat'
				});
			},
			// 加载相似商品
			loadSimilarItems() {
				if (!this.itemId || !this.item.categoryId) return;

				// 使用分类推荐相似商品
				uni.request({
					url: `${API_BASE_URL}/recommend/category/${this.item.categoryId}?limit=6`,
					success: (res) => {
						if (res.data && res.data.success && res.data.data) {
							// 过滤掉当前商品
							this.similarItems = res.data.data.filter(item => item.id != this.itemId);
						}
					},
					fail: (err) => {
						console.error('获取相似商品失败', err);
					}
				});
			},

			// 跳转到商品详情
			goToItemDetail(id) {
				if (!id) return;

				// 如果是当前商品，不跳转
				if (id == this.itemId) return;

				uni.navigateTo({
					url: `/pages/item/detail?id=${id}`
				});
			},

			// === 新增方法占位符结束 ===
		}
	}
</script>

<style>

	.loading-indicator {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 300rpx; /* 给加载提示一个高度 */
		color: #999;
		font-size: 28rpx;
	}

	.item-detail-container {
		padding-bottom: 120rpx; /* 稍微增加底部留空 */
		background-color: #f8f8f8; /* 添加背景色 */
		min-height: 100vh; /* 确保背景色铺满 */
	}

	.swiper {
		height: 750rpx;
		width: 100%;
	}

	.swiper-image {
		width: 100%;
		height: 100%;
	}

	.item-info {
		padding: 20rpx 30rpx; /* 增加左右内边距 */
		background-color: #fff;
		margin-bottom: 20rpx; /* 保持下边距 */
	}

	/* 价格和收藏按钮行 */
	.item-price-row {
		display: flex;
		justify-content: space-between; /* 两端对齐 */
		align-items: flex-end; /* 底部对齐，让价格和收藏文字基线对齐 */
		margin-bottom: 15rpx; /* 增加与名称的间距 */
	}

	.item-price {
		display: flex;
		align-items: baseline; /* 让价格和原价基线对齐 */
	}

	.price {
		font-size: 48rpx; /* 加大价格字号 */
		color: #ff6700;
		font-weight: bold;
		margin-right: 15rpx; /* 调整与原价间距 */
	}

	.original-price {
		font-size: 28rpx;
		color: #999;
		text-decoration: line-through;
	}

	/* 收藏按钮 */
	.collect-btn {
		display: flex;
		align-items: center;
		padding: 5rpx 10rpx; /* 轻微内边距 */
		border: 1rpx solid #eee; /* 浅边框 */
		border-radius: 20rpx; /* 圆角 */
	}

	.collect-icon {
		font-size: 28rpx; /* 图标大小 */
		color: #999; /* 默认灰色 */
		margin-right: 8rpx;
	}

	.collect-icon.collected {
		color: #ff6700; /* 收藏后橙色 */
	}

	.collect-text {
		font-size: 24rpx;
		color: #666;
	}

	.item-name {
		font-size: 36rpx; /* 加大名称字号 */
		font-weight: bold;
		margin-bottom: 15rpx; /* 增加与简介间距 */
		line-height: 1.4; /* 增加行高 */
	}

	/* 商品简介 */
	.item-brief {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 20rpx; /* 增加与统计行间距 */
		line-height: 1.5;
	}

	/* 销量和评分行 */
	.item-stats {
		display: flex;
		align-items: center;
		font-size: 24rpx;
		color: #999;
	}

	.item-sales {
		margin-right: 30rpx; /* 增加销量和评分间距 */
	}

	.item-rating {
		/* 可以添加评分图标等 */
	}

	.item-spec {
		padding: 20rpx 30rpx; /* 增加左右内边距 */
		background-color: #fff;
		margin-bottom: 20rpx; /* 保持下边距 */
	}

	.spec-title {
		font-size: 30rpx; /* 稍加大标题字号 */
		font-weight: 500; /* 标准粗细 */
		margin-bottom: 25rpx; /* 增加与下方间距 */
	}

	.spec-item {
		display: flex;
		/* justify-content: space-between; */ /* 取消两端对齐，让标签和选项靠近 */
		align-items: center;
		padding: 15rpx 0; /* 增加垂直内边距 */
	}

	/* 规格标签 */
	.spec-label {
		width: 100rpx; /* 固定宽度 */
		font-size: 28rpx;
		color: #666;
		margin-right: 20rpx; /* 与选项的间距 */
		text-align: right; /* 右对齐 */
	}

	/* 规格选项容器 (用于颜色、尺寸等) */
	.spec-options {
		flex: 1; /* 占据剩余空间 */
		display: flex;
		flex-wrap: wrap; /* 允许换行 */
	}

	/* 单个规格选项按钮 */
	.spec-option {
		padding: 8rpx 20rpx;
		margin-right: 15rpx;
		margin-bottom: 15rpx; /* 选项间距 */
		border: 1rpx solid #eee;
		border-radius: 30rpx; /* 圆角 */
		font-size: 26rpx;
		color: #333;
		background-color: #f8f8f8;
	}

	.spec-option.active {
		background-color: #fff0e6; /* 选中背景色 */
		color: #ff6700; /* 选中文本色 */
		border-color: #ff6700; /* 选中边框色 */
	}

	.quantity-selector {
		display: flex;
		align-items: center;
	}

	.quantity-btn {
		width: 50rpx; /* 调整按钮大小 */
		height: 50rpx;
		line-height: 50rpx;
		text-align: center;
		background-color: #f0f0f0; /* 稍深背景 */
		color: #666; /* 按钮文字颜色 */
		border-radius: 50%; /* 圆形按钮 */
		font-size: 36rpx; /* 稍大加减号 */
		border: none;
		padding: 0; /* 移除默认padding */
	}

	.quantity-input {
		width: 70rpx; /* 调整输入框宽度 */
		height: 50rpx; /* 与按钮同高 */
		text-align: center;
		background-color: #fff; /* 白色背景 */
		border: 1rpx solid #eee; /* 添加边框 */
		margin: 0 15rpx; /* 调整与按钮间距 */
		font-size: 28rpx;
		border-radius: 6rpx; /* 轻微圆角 */
	}

	/* 为评价和详情区域添加样式 */
	.item-reviews, .item-detail {
		padding: 20rpx 30rpx; /* 增加左右内边距 */
		background-color: #fff;
		margin-bottom: 20rpx; /* 添加下边距 */
	}

	/* 区域标题公共样式 */
	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 25rpx; /* 标题与内容的间距 */
		padding-bottom: 15rpx; /* 标题下的空间 */
		border-bottom: 1rpx solid #f0f0f0; /* 分隔线 */
	}

	.section-title {
		font-size: 30rpx;
		font-weight: 500;
		color: #333;
	}

	.section-more {
		font-size: 26rpx;
		color: #999;
	}

	/* 评价列表样式 (可以根据需要细化) */
	.review-list {
		/* ... */
	}
	.review-item {
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx dashed #eee; /* 评价间分隔线 */
	}
	.review-item:last-child {
		border-bottom: none; /* 最后一个不显示分隔线 */
	}
	/* ... 其他评价相关样式 ... */


	/* 空状态提示 */
	.empty-reviews, .detail-content > rich-text[nodes='暂无详情'] /* 稍微复杂选择器针对rich-text */ {
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 150rpx; /* 给个最小高度 */
		color: #999;
		font-size: 28rpx;
	}
	/* 如果rich-text内容为空，也应用类似样式 (需要JS配合判断item.detail是否为空字符串) */
	.empty-detail-content {
		display: flex;
		justify-content: center;
		align-items: center;
		min-height: 150rpx;
		color: #999;
		font-size: 28rpx;
	}

	/* 商品详情内容 */
	.detail-content {
		font-size: 28rpx;
		line-height: 1.6;
		color: #333;
	}
	/* 可以针对 rich-text 内部的 p, img 等标签设置样式 */
	.detail-content image, .detail-content img {
		max-width: 100%;
		display: block; /* 防止图片下方有空隙 */
		margin: 10rpx 0;
	}

	.action-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		border-top: 1rpx solid #eee;
		z-index: 99; /* 确保显示在最上层 */
	}

	.action-left {
		display: flex;
		width: 40%; /* 左侧占40% */
	}

	.action-right {
		display: flex;
		width: 60%; /* 右侧占60% */
	}

	.action-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		font-size: 24rpx;
		position: relative; /* 为了定位角标 */
	}

	.action-icon {
		width: 40rpx;
		height: 40rpx;
		margin-bottom: 5rpx;
	}

	.cart-badge {
		position: absolute;
		top: 0;
		right: 50%; /* 定位到图标右上方 */
		transform: translateX(10rpx);
		background-color: #ff6700;
		color: #fff;
		font-size: 20rpx;
		min-width: 32rpx;
		height: 32rpx;
		line-height: 32rpx;
		text-align: center;
		border-radius: 16rpx;
		padding: 0 6rpx;
	}

	.action-btn {
		flex: 1;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		color: #fff;
		font-size: 28rpx;
	}

	.add-cart {
		background-color: #ff9500;
		margin-right: 2rpx; /* 两个按钮之间的间隙 */
	}

	.buy-now {
		background-color: #ff6700;
	}

	/* 相似商品推荐样式 */
	.similar-items {
		padding: 20rpx 30rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
	}

	.similar-scroll {
		white-space: nowrap;
		padding: 10rpx 0;
	}

	.similar-item {
		display: inline-block;
		width: 220rpx;
		margin-right: 20rpx;
		vertical-align: top;
	}

	.similar-image {
		width: 220rpx;
		height: 220rpx;
		border-radius: 10rpx;
	}

	.similar-name {
		font-size: 26rpx;
		color: #333;
		margin: 10rpx 0;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.similar-price {
		font-size: 28rpx;
		color: #ff6700;
		font-weight: bold;
	}
</style>