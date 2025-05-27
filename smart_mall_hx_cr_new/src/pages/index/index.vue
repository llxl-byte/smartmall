<template>
	<view class="content">
		<!-- 搜索栏 -->
		<view class="search-bar">
			<view class="search-input-box" @click="goToSearch">
				<text class="search-icon">🔍</text>
				<text class="search-placeholder">搜索商品</text>
			</view>
		</view>

		<!-- 轮播图 -->
		<swiper class="banner" indicator-dots autoplay circular>
			<swiper-item>
				<image src="../../static/landscape.jpg" mode="aspectFill"></image>
			</swiper-item>
			<swiper-item>
				<image src="../../static/scenery.jpg" mode="aspectFill"></image>
			</swiper-item>
		</swiper>

		<!-- 分类导航 -->
		<view class="category-nav">
			<scroll-view scroll-x class="category-scroll">
				<view
					class="category-item"
					v-for="(category, index) in categoryList"
					:key="index"
					@click="categoryClick(category.id)"
					:class="{active: currentCategory === category.id}"
				>
					{{ category.name }}
				</view>
			</scroll-view>
		</view>

		<!-- 推荐商品 -->
		<view class="recommend-section">
			<view class="section-header">
				<text class="section-title">为您推荐</text>
				<text class="section-more" @click="goToMore('recommend')">更多 ></text>
			</view>
			<scroll-view scroll-x class="recommend-scroll" v-if="recommendItems.length > 0">
				<view
					class="recommend-item"
					v-for="(item, index) in recommendItems"
					:key="'rec-'+index"
					@click="goToDetail(item.id)"
				>
					<view class="recommend-image-container">
						<image class="recommend-image" :src="item.mainImage || '../../static/landscape.jpg'"></image>
						<view class="recommend-tag">推荐</view>
					</view>
					<view class="recommend-name">{{item.name}}</view>
					<view class="recommend-price">¥{{item.price}}</view>
				</view>
			</scroll-view>
			<view class="no-data-placeholder" v-else>
				<text>正在为您生成个性化推荐...</text>
			</view>
		</view>

		<!-- 热销商品 -->
		<view class="hot-section">
			<view class="section-header">
				<text class="section-title">热销商品</text>
				<text class="section-more" @click="goToMore('hot')">更多 ></text>
			</view>
			<scroll-view scroll-x class="hot-scroll" v-if="hotItems.length > 0">
				<view
					class="hot-item"
					v-for="(item, index) in hotItems"
					:key="'hot-'+index"
					@click="goToDetail(item.id)"
				>
					<view class="hot-image-container">
						<image class="hot-image" :src="item.mainImage || '../../static/scenery.jpg'"></image>
						<view class="hot-tag">热销</view>
						<view class="hot-rank" v-if="index < 3">{{index + 1}}</view>
					</view>
					<view class="hot-name">{{item.name}}</view>
					<view class="hot-price">¥{{item.price}}</view>
					<view class="hot-sales">销量: {{item.sales || 0}}</view>
				</view>
			</scroll-view>
			<view class="no-data-placeholder" v-else>
				<text>正在加载热销商品...</text>
			</view>
		</view>

		<!-- 分类商品列表 -->
		<view class="item-list">
			<view class="section-header" v-if="itemList.length > 0">
				<text class="section-title">分类商品</text>
			</view>
			<view
				class="item-card"
				v-for="(item, index) in itemList"
				:key="index"
				@click="goToDetail(item.id)"
			>
				<image class="item-image" :src="item.mainImage || '../../static/Xiaomi15Pro.png'"></image>
				<view class="item-info">
					<view class="item-name">{{item.name}}</view>
					<view class="item-price">¥{{item.price}}</view>
					<view class="item-sales">销量: {{item.sales || 0}}</view>
				</view>
			</view>
			<view class="no-data-placeholder" v-if="itemList.length === 0">
				<text>暂无分类商品</text>
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="itemList.length > 0">
			<text>{{loadingMore ? '加载中...' : '已经到底了'}}</text>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
		data() {
			return {
				categoryList: [],
				itemList: [],
				recommendItems: [], // 推荐商品
				hotItems: [], // 热销商品
				currentCategory: 0, // 当前选中的分类
				loadingMore: false, // 是否正在加载更多
				page: 1, // 当前页码
				pageSize: 10, // 每页数量
				hasMore: true, // 是否还有更多数据
				userInfo: null // 用户信息
			}
		},
		onLoad() {
			// 获取用户信息
			this.userInfo = uni.getStorageSync('userInfo');

			// 加载分类列表
			this.loadCategories();
			// 加载商品列表
			this.loadItems();
			// 加载热销商品
			this.loadHotItems();
			// 加载推荐商品
			this.loadRecommendItems();
		},
		onPullDownRefresh() {
			// 下拉刷新
			this.page = 1;
			this.hasMore = true;
			this.loadItems(true);
		},
		onReachBottom() {
			// 上拉加载更多
			if (this.hasMore && !this.loadingMore) {
				this.page++;
				this.loadItems();
			}
		},
		methods: {
			// 加载分类列表
			loadCategories() {
				uni.request({
					url: `${API_BASE_URL}/selectAll`,
					success: (res) => {
						console.log('获取分类列表成功', res);
						if (res.data && res.data.length > 0) {
							this.categoryList = res.data;
							// 默认选中第一个分类
							if (this.categoryList.length > 0 && !this.currentCategory) {
								this.currentCategory = this.categoryList[0].id;
							}
						}
					},
					fail: (err) => {
						console.error('获取分类列表失败', err);
						uni.showToast({
							title: '获取分类列表失败',
							icon: 'none'
						});
					}
				});
			},

			// 加载商品列表
			loadItems(replace = false) {
				if (this.loadingMore) return;

				this.loadingMore = true;

				// 准备模拟数据，以防接口调用失败
				const mockItems = [
					{
						id: 7,
						name: 'HuaWei-Mate70',
						price: 6999,
						mainImage: '../../static/HuaWei-Mate70.jpg',
						sales: 200
					},
					{
						id: 8,
						name: 'HuaWei-P70',
						price: 7999,
						mainImage: '../../static/HuaWei-P70.jpg',
						sales: 150
					},
					{
						id: 9,
						name: 'Xiaomi15Ultra',
						price: 8999,
						mainImage: '../../static/Xiaomi15Ultra.png',
						sales: 100
					},
					{
						id: 10,
						name: 'Xiaomi15Pro',
						price: 9999,
						mainImage: '../../static/Xiaomi15Pro.png',
						sales: 50
					}
				];

				// 检查登录状态
				const userInfo = uni.getStorageSync('userInfo');
				const token = localStorage.getItem('jwtToken');

				// 如果没有登录或没有令牌，使用模拟数据
				if (!userInfo || !token) {
					console.log('未登录，使用模拟商品列表数据');
					this.loadingMore = false;
					if (replace) {
						this.itemList = mockItems;
					} else if (this.page === 1) {
						this.itemList = mockItems;
					} else {
						this.hasMore = false;
					}
					uni.stopPullDownRefresh();
					return;
				}

				// 这里应该根据分类ID和页码加载商品
				// 由于后端可能没有分页接口，这里简化处理
				let url = `${API_BASE_URL}/selectByCategoryId?categoryId=${this.currentCategory || 0}`;

				uni.request({
					url: url,
					header: {
						'Authorization': `Bearer ${token}`
					},
					success: (res) => {
						this.loadingMore = false;

						if (res.data) {
							if (replace) {
								this.itemList = res.data;
							} else {
								// 模拟分页效果
								if (this.page === 1) {
									this.itemList = res.data;
								} else {
									// 如果已经加载过数据，就不再追加了
									this.hasMore = false;
								}
							}
						} else {
							console.log('未获取到商品列表数据，使用模拟数据');
							if (replace) {
								this.itemList = mockItems;
							} else if (this.page === 1) {
								this.itemList = mockItems;
							} else {
								this.hasMore = false;
							}
						}

						// 停止下拉刷新
						uni.stopPullDownRefresh();
					},
					fail: (err) => {
						console.error('获取商品列表失败', err);
						this.loadingMore = false;

						// 使用模拟数据
						console.log('使用模拟商品列表数据');
						if (replace) {
							this.itemList = mockItems;
						} else if (this.page === 1) {
							this.itemList = mockItems;
						} else {
							this.hasMore = false;
						}

						uni.stopPullDownRefresh();
					}
				});
			},

			// 点击分类
			categoryClick(id) {
				if (this.currentCategory === id) return;

				this.currentCategory = id;
				this.page = 1;
				this.hasMore = true;
				this.loadItems(true);
			},

			// 跳转到商品详情页
			goToDetail(id) {
				if (!id) return;

				uni.navigateTo({
					url: `/pages/item/detail?id=${id}`
				});
			},

			// 跳转到搜索页
			goToSearch() {
				uni.navigateTo({
					url: '/pages/search/search'
				});
			},

			// 跳转到更多页面
			goToMore(type) {
				uni.navigateTo({
					url: `/pages/more/more?type=${type}`
				});
			},

			// 加载热销商品
			loadHotItems() {
				// 先检查是否有登录信息
				const userInfo = uni.getStorageSync('userInfo');
				const token = localStorage.getItem('jwtToken');

				// 准备模拟数据，以防接口调用失败
				const mockHotItems = [
					{
						id: 1,
						name: 'Xiaomi15',
						price: 999,
						mainImage: '../../static/Xiaomi15.png',
						sales: 1000
					},
					{
						id: 2,
						name: 'HP-Shadow-Eif1010',
						price: 1999,
						mainImage: '../../static/HP-Shadow-Eif1010.jpg',
						sales: 800
					},
					{
						id: 3,
						name: 'Mechrevo-Dragon16',
						price: 2999,
						mainImage: '../../static/Mechrevo-Dragon16.jpg',
						sales: 600
					}
				];

				// 如果没有登录或没有令牌，使用模拟数据
				if (!userInfo || !token) {
					console.log('未登录，使用模拟热销商品数据');
					this.hotItems = mockHotItems;
					return;
				}

				// 如果已登录，尝试调用接口
				uni.request({
					url: `${API_BASE_URL}/recommend/hot?limit=10`,
					header: {
						'Authorization': `Bearer ${token}`
					},
					success: (res) => {
						if (res.data && res.data.data) {
							this.hotItems = res.data.data;
						} else {
							console.log('未获取到热销商品数据，使用模拟数据');
							this.hotItems = mockHotItems;
						}
					},
					fail: (err) => {
						console.error('获取热销商品失败', err);
						console.log('使用模拟热销商品数据');
						this.hotItems = mockHotItems;
					}
				});
			},

			// 加载推荐商品
			loadRecommendItems() {
				// 准备模拟数据，以防接口调用失败
				const mockRecommendItems = [
					{
						id: 4,
						name: 'Xiaomi15Pro',
						price: 3999,
						mainImage: '../../static/Xiaomi15Pro.png',
						sales: 500
					},
					{
						id: 5,
						name: 'HuaWei-Mate70',
						price: 4999,
						mainImage: '../../static/HuaWei-Mate70.jpg',
						sales: 400
					},
					{
						id: 6,
						name: 'HuaWei-P70',
						price: 5999,
						mainImage: '../../static/HuaWei-P70.jpg',
						sales: 300
					}
				];

				// 检查登录状态
				const userInfo = uni.getStorageSync('userInfo');
				const token = localStorage.getItem('jwtToken');

				// 如果用户未登录或没有令牌，使用模拟数据
				if (!userInfo || !token) {
					console.log('未登录，使用模拟推荐商品数据');
					this.recommendItems = mockRecommendItems;
					return;
				}

				// 如果用户已登录，尝试加载个性化推荐
				uni.request({
					url: `${API_BASE_URL}/recommend/user/${userInfo.id}?limit=10`,
					header: {
						'Authorization': `Bearer ${token}`
					},
					success: (res) => {
						if (res.data && res.data.data) {
							this.recommendItems = res.data.data;
						} else {
							console.log('未获取到个性化推荐数据，尝试热销推荐');
							// 尝试热销推荐
							this.tryHotRecommend(token, mockRecommendItems);
						}
					},
					fail: (err) => {
						console.error('获取个性化推荐失败', err);
						// 如果个性化推荐失败，回退到热销推荐
						this.tryHotRecommend(token, mockRecommendItems);
					}
				});
			},

			// 尝试热销推荐
			tryHotRecommend(token, fallbackData) {
				uni.request({
					url: `${API_BASE_URL}/recommend/hot?limit=10`,
					header: {
						'Authorization': `Bearer ${token}`
					},
					success: (res) => {
						if (res.data && res.data.data) {
							this.recommendItems = res.data.data;
						} else {
							console.log('未获取到热销推荐数据，使用模拟数据');
							this.recommendItems = fallbackData;
						}
					},
					fail: (err) => {
						console.error('获取热销推荐失败', err);
						console.log('使用模拟推荐数据');
						this.recommendItems = fallbackData;
					}
				});
			}
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	/* 搜索栏样式 */
	.search-bar {
		padding: 20rpx;
		background-color: #ff6700;
	}

	.search-input-box {
		height: 70rpx;
		background-color: #fff;
		border-radius: 35rpx;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
	}

	.search-icon {
		margin-right: 10rpx;
		font-size: 32rpx;
		color: #999;
	}

	.search-placeholder {
		color: #999;
		font-size: 28rpx;
	}

	/* 轮播图样式 */
	.banner {
		width: 100%;
		height: 350rpx;
	}

	.banner image {
		width: 100%;
		height: 100%;
	}

	/* 分类导航样式 */
	.category-nav {
		background-color: #fff;
		padding: 20rpx 0;
		margin-bottom: 20rpx;
	}

	.category-scroll {
		white-space: nowrap;
	}

	.category-item {
		display: inline-block;
		padding: 10rpx 30rpx;
		font-size: 28rpx;
		color: #666;
		position: relative;
	}

	.category-item.active {
		color: #ff6700;
		font-weight: bold;
	}

	.category-item.active::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		width: 40rpx;
		height: 4rpx;
		background-color: #ff6700;
		border-radius: 2rpx;
	}

	/* 商品列表样式 */
	.item-list {
		display: flex;
		flex-wrap: wrap;
		padding: 0 10rpx;
		margin-top: 20rpx;
	}

	/* 推荐商品样式 */
	.recommend-section, .hot-section {
		background-color: #fff;
		margin: 20rpx 0;
		padding: 20rpx 0;
		border-radius: 10rpx;
	}

	.section-header {
		padding: 0 20rpx 20rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		position: relative;
		padding-left: 20rpx;
	}

	.section-title::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 8rpx;
		height: 30rpx;
		background: linear-gradient(to bottom, #ff6700, #ff9500);
		border-radius: 4rpx;
	}

	.section-more {
		font-size: 24rpx;
		color: #999;
	}

	.recommend-scroll, .hot-scroll {
		white-space: nowrap;
		padding: 0 10rpx;
	}

	.recommend-item, .hot-item {
		display: inline-block;
		width: 220rpx;
		margin: 0 10rpx;
		vertical-align: top;
		padding-bottom: 15rpx;
		transition: transform 0.3s ease;
	}

	.recommend-item:hover, .hot-item:hover {
		transform: translateY(-5rpx);
	}

	.recommend-image-container, .hot-image-container {
		position: relative;
		width: 220rpx;
		height: 220rpx;
		overflow: hidden;
		border-radius: 10rpx;
		background-color: #fff;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.recommend-image, .hot-image {
		width: 100%;
		height: 100%;
		border-radius: 10rpx;
		transition: transform 0.3s ease;
		object-fit: cover; /* 确保图片正确填充容器 */
	}

	.recommend-item:hover .recommend-image, .hot-item:hover .hot-image {
		transform: scale(1.05);
	}

	.recommend-tag, .hot-tag {
		position: absolute;
		top: 10rpx;
		left: 10rpx;
		padding: 4rpx 12rpx;
		background-color: rgba(255, 103, 0, 0.8);
		color: white;
		font-size: 20rpx;
		border-radius: 20rpx;
	}

	.hot-rank {
		position: absolute;
		top: 10rpx;
		right: 10rpx;
		width: 40rpx;
		height: 40rpx;
		background-color: rgba(255, 0, 0, 0.8);
		color: white;
		font-size: 24rpx;
		font-weight: bold;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.recommend-name, .hot-name {
		font-size: 26rpx;
		color: #333;
		margin: 10rpx 0;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		padding: 0 5rpx;
		font-weight: 500;
	}

	.recommend-price, .hot-price {
		font-size: 30rpx;
		color: #ff6700;
		font-weight: bold;
		padding: 0 5rpx;
	}

	.hot-sales {
		font-size: 22rpx;
		color: #999;
		margin-top: 5rpx;
		padding: 0 5rpx;
	}

	.no-data-placeholder {
		padding: 20rpx;
		text-align: center;
		color: #999;
		font-size: 28rpx;
	}

	.item-card {
		width: calc(50% - 20rpx);
		margin: 10rpx;
		background-color: #fff;
		border-radius: 10rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.item-image {
		width: 100%;
		height: 350rpx;
	}

	.item-info {
		padding: 20rpx;
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
		margin-bottom: 10rpx;
	}

	.item-sales {
		font-size: 24rpx;
		color: #999;
	}

	/* 加载更多样式 */
	.load-more {
		text-align: center;
		padding: 30rpx 0;
		color: #999;
		font-size: 24rpx;
	}
</style>
