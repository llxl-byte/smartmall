<template>
	<view class="content">
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="back-icon" @click="goBack">
				<text>← 返回</text>
			</view>
			<view class="nav-title">{{ title }}</view>
		</view>

		<!-- 商品列表 -->
		<view class="item-list" v-if="items.length > 0">
			<view
				class="item-card"
				v-for="(item, index) in items"
				:key="index"
				@click="goToDetail(item.id)"
			>
				<image class="item-image" :src="item.mainImage || '../../static/landscape.jpg'"></image>
				<view class="item-info">
					<view class="item-name">{{ item.name }}</view>
					<view class="item-price">¥{{ item.price }}</view>
					<view class="item-sales">销量: {{ item.sales || 0 }}</view>
				</view>
			</view>
		</view>

		<!-- 无数据提示 -->
		<view class="no-data-placeholder" v-else>
			<text>暂无商品数据</text>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="items.length > 0">
			<text>{{ loadingMore ? '加载中...' : hasMore ? '点击加载更多' : '已经到底了' }}</text>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			type: '', // recommend 或 hot
			title: '', // 页面标题
			items: [], // 商品列表
			page: 1, // 当前页码
			pageSize: 20, // 每页数量
			hasMore: true, // 是否还有更多数据
			loadingMore: false // 是否正在加载更多
		};
	},
	onLoad(options) {
		// 获取传入的类型参数
		this.type = options.type || 'recommend';
		// 设置页面标题
		this.title = this.type === 'recommend' ? '为您推荐' : '热销商品';
		// 设置导航栏标题
		uni.setNavigationBarTitle({
			title: this.title
		});
		// 加载商品数据
		this.loadItems();
	},
	onReachBottom() {
		// 上拉加载更多
		if (this.hasMore && !this.loadingMore) {
			this.page++;
			this.loadItems();
		}
	},
	methods: {
		// 返回上一页
		goBack() {
			uni.navigateBack();
		},
		// 加载商品列表
		loadItems() {
			if (this.loadingMore) return;
			this.loadingMore = true;

			// 检查登录状态
			const userInfo = uni.getStorageSync('userInfo');
			const token = localStorage.getItem('jwtToken');

			// 准备模拟数据，以防接口调用失败
			const mockRecommendItems = [
				{ id: 4, name: 'Xiaomi15Pro', price: 3999, mainImage: '../../static/Xiaomi15Pro.png', sales: 500 },
				{ id: 5, name: 'HuaWei-Mate70', price: 4999, mainImage: '../../static/HuaWei-Mate70.jpg', sales: 400 },
				{ id: 6, name: 'HuaWei-P70', price: 5999, mainImage: '../../static/HuaWei-P70.jpg', sales: 300 }
			];
			const mockHotItems = [
				{ id: 1, name: 'Xiaomi15', price: 999, mainImage: '../../static/Xiaomi15.png', sales: 1000 },
				{ id: 2, name: 'HP-Shadow-Eif1010', price: 1999, mainImage: '../../static/HP-Shadow-Eif1010.jpg', sales: 800 },
				{ id: 3, name: 'Mechrevo-Dragon16', price: 2999, mainImage: '../../static/Mechrevo-Dragon16.jpg', sales: 600 }
			];

			// 如果没有登录或没有令牌，使用模拟数据
			if (!userInfo || !token) {
				console.log(`未登录，使用模拟${this.type === 'recommend' ? '推荐' : '热销'}商品数据`);
				this.items = this.type === 'recommend' ? mockRecommendItems : mockHotItems;
				this.loadingMore = false;
				this.hasMore = false;
				return;
			}

			// 构建请求URL
			let url = '';
			if (this.type === 'recommend') {
				url = `${API_BASE_URL}/recommend/user/${userInfo.id}?limit=${this.pageSize}&page=${this.page}`;
			} else {
				url = `${API_BASE_URL}/recommend/hot?limit=${this.pageSize}&page=${this.page}`;
			}

			// 调用接口获取数据
			uni.request({
				url: url,
				header: {
					'Authorization': `Bearer ${token}`
				},
				success: (res) => {
					this.loadingMore = false;
					if (res.data && res.data.data && res.data.data.length > 0) {
						if (this.page === 1) {
							this.items = res.data.data;
						} else {
							this.items = this.items.concat(res.data.data);
						}
						// 判断是否还有更多数据
						this.hasMore = res.data.data.length === this.pageSize;
					} else {
						this.hasMore = false;
						// 如果是推荐类型且第一页没有数据，尝试热销推荐
						if (this.type === 'recommend' && this.page === 1) {
							console.log('未获取到个性化推荐数据，尝试热销推荐');
							this.tryHotRecommend(token);
						}
					}
				},
				fail: (err) => {
					console.error(`获取${this.type === 'recommend' ? '推荐' : '热销'}商品失败`, err);
					this.loadingMore = false;
					// 使用模拟数据
					if (this.page === 1) {
						this.items = this.type === 'recommend' ? mockRecommendItems : mockHotItems;
					}
					this.hasMore = false;
					// 如果是推荐类型且第一页失败，尝试热销推荐
					if (this.type === 'recommend' && this.page === 1) {
						this.tryHotRecommend(token);
					}
				}
			});
		},
		// 尝试热销推荐（当个性化推荐失败时）
		tryHotRecommend(token) {
			const mockRecommendItems = [
				{ id: 4, name: 'Xiaomi15Pro', price: 3999, mainImage: '../../static/Xiaomi15Pro.png', sales: 500 },
				{ id: 5, name: 'HuaWei-Mate70', price: 4999, mainImage: '../../static/HuaWei-Mate70.jpg', sales: 400 },
				{ id: 6, name: 'HuaWei-P70', price: 5999, mainImage: '../../static/HuaWei-P70.jpg', sales: 300 }
			];
			uni.request({
				url: `${API_BASE_URL}/recommend/hot?limit=${this.pageSize}&page=${this.page}`,
				header: {
					'Authorization': `Bearer ${token}`
				},
				success: (res) => {
					if (res.data && res.data.data && res.data.data.length > 0) {
						this.items = res.data.data;
						this.hasMore = res.data.data.length === this.pageSize;
					} else {
						console.log('未获取到热销推荐数据，使用模拟数据');
						this.items = mockRecommendItems;
						this.hasMore = false;
					}
				},
				fail: (err) => {
					console.error('获取热销推荐失败', err);
					console.log('使用模拟推荐数据');
					this.items = mockRecommendItems;
					this.hasMore = false;
				}
			});
		},
		// 跳转到商品详情页
		goToDetail(id) {
			if (!id) return;
			uni.navigateTo({
				url: `/pages/item/detail?id=${id}`
			});
		}
	}
};
</script>

<style>
.content {
	display: flex;
	flex-direction: column;
	background-color: #f5f5f5;
	min-height: 100vh;
}

/* 顶部导航栏样式 */
.nav-bar {
	height: 88rpx;
	background-color: #ff6700;
	color: #fff;
	display: flex;
	align-items: center;
	justify-content: center;
	position: fixed;
	top: 0;
	width: 100%;
	z-index: 100;
	padding-top: var(--status-bar-height);
}

.back-icon {
	position: absolute;
	left: 20rpx;
	font-size: 32rpx;
}

.nav-title {
	font-size: 36rpx;
	font-weight: bold;
}

/* 商品列表样式 */
.item-list {
	display: flex;
	flex-wrap: wrap;
	padding: 0 10rpx;
	margin-top: 108rpx;
	margin-bottom: 20rpx;
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

/* 无数据提示 */
.no-data-placeholder {
	padding: 40rpx;
	text-align: center;
	color: #999;
	font-size: 28rpx;
	margin-top: 108rpx;
}

/* 加载更多样式 */
.load-more {
	text-align: center;
	padding: 30rpx 0;
	color: #999;
	font-size: 24rpx;
}
</style>
