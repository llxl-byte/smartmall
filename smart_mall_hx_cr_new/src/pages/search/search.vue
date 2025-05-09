<template>
	<view class="search-container">
		<!-- 搜索栏 -->
		<view class="search-header">
			<view class="search-input-box">
				<text class="search-icon">🔍</text>
				<input 
					class="search-input" 
					type="text" 
					v-model="searchKeyword" 
					placeholder="搜索商品" 
					confirm-type="search"
					@confirm="searchItems"
					focus
				/>
				<text class="clear-icon" v-if="searchKeyword" @click="clearSearch">✕</text>
			</view>
			<text class="cancel-btn" @click="goBack">取消</text>
		</view>
		
		<!-- 搜索历史 -->
		<view class="search-history" v-if="!searchKeyword && searchHistory.length > 0 && !loading">
			<view class="history-header">
				<text class="history-title">搜索历史</text>
				<text class="clear-history" @click="clearHistory">清除</text>
			</view>
			<view class="history-list">
				<view 
					class="history-item" 
					v-for="(item, index) in searchHistory" 
					:key="index"
					@click="useHistoryKeyword(item)"
				>
					<text class="history-icon">⏱</text>
					<text class="history-text">{{ item }}</text>
				</view>
			</view>
		</view>
		
		<!-- 热门搜索 -->
		<view class="hot-search" v-if="!searchKeyword && hotSearches.length > 0 && !loading">
			<view class="hot-header">
				<text class="hot-title">热门搜索</text>
			</view>
			<view class="hot-list">
				<view 
					class="hot-item" 
					v-for="(item, index) in hotSearches" 
					:key="index"
					@click="useHistoryKeyword(item)"
				>
					{{ item }}
				</view>
			</view>
		</view>
		
		<!-- 搜索结果 -->
		<view class="search-results" v-if="searchKeyword">
			<!-- 加载中 -->
			<view class="loading" v-if="loading">
				<text>搜索中...</text>
			</view>
			
			<!-- 无结果 -->
			<view class="no-results" v-else-if="searchResults.length === 0">
				<text>没有找到相关商品</text>
				<text class="no-results-tip">换个关键词试试吧</text>
			</view>
			
			<!-- 结果列表 -->
			<view class="result-list" v-else>
				<view
					class="result-item"
					v-for="(item, index) in searchResults"
					:key="index"
					@click="goToDetail(item.id)"
				>
					<image class="result-image" :src="item.mainImage || '/static/default_item.png'" mode="aspectFill"></image>
					<view class="result-info">
						<view class="result-name">{{ item.name }}</view>
						<view class="result-price">¥{{ item.price }}</view>
						<view class="result-sales">销量: {{ item.sales || 0 }}</view>
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
			searchKeyword: '',
			searchResults: [],
			searchHistory: [],
			hotSearches: ['手机', '电脑', '耳机', '平板', '智能手表'],
			loading: false
		}
	},
	onLoad(options) {
		// 如果有传入的关键词，直接搜索
		if (options.keyword) {
			this.searchKeyword = options.keyword;
			this.searchItems();
		}
		
		// 从本地存储加载搜索历史
		const history = uni.getStorageSync('searchHistory');
		if (history) {
			this.searchHistory = JSON.parse(history);
		}
	},
	methods: {
		// 搜索商品
		searchItems() {
			if (!this.searchKeyword.trim()) return;
			
			this.loading = true;
			
			// 保存到搜索历史
			this.saveToHistory(this.searchKeyword);
			
			// 调用搜索接口
			uni.request({
				url: `${API_BASE_URL}/searchItemByName?itemName=${encodeURIComponent(this.searchKeyword)}`,
				success: (res) => {
					this.loading = false;
					if (res.data) {
						this.searchResults = res.data;
					} else {
						this.searchResults = [];
					}
				},
				fail: (err) => {
					this.loading = false;
					console.error('搜索失败', err);
					uni.showToast({
						title: '搜索失败',
						icon: 'none'
					});
				}
			});
		},
		
		// 保存到搜索历史
		saveToHistory(keyword) {
			// 如果已存在，先移除
			const index = this.searchHistory.indexOf(keyword);
			if (index !== -1) {
				this.searchHistory.splice(index, 1);
			}
			
			// 添加到最前面
			this.searchHistory.unshift(keyword);
			
			// 最多保存10条
			if (this.searchHistory.length > 10) {
				this.searchHistory = this.searchHistory.slice(0, 10);
			}
			
			// 保存到本地存储
			uni.setStorageSync('searchHistory', JSON.stringify(this.searchHistory));
		},
		
		// 使用历史关键词
		useHistoryKeyword(keyword) {
			this.searchKeyword = keyword;
			this.searchItems();
		},
		
		// 清空搜索框
		clearSearch() {
			this.searchKeyword = '';
			this.searchResults = [];
		},
		
		// 清除搜索历史
		clearHistory() {
			uni.showModal({
				title: '提示',
				content: '确定要清除所有搜索历史吗？',
				success: (res) => {
					if (res.confirm) {
						this.searchHistory = [];
						uni.removeStorageSync('searchHistory');
					}
				}
			});
		},
		
		// 返回上一页
		goBack() {
			uni.navigateBack();
		},
		
		// 跳转到商品详情
		goToDetail(id) {
			if (!id) return;
			
			uni.navigateTo({
				url: `/pages/item/detail?id=${id}`
			});
		}
	}
}
</script>

<style>
.search-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
}

/* 搜索栏样式 */
.search-header {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #eee;
}

.search-input-box {
	flex: 1;
	height: 70rpx;
	background-color: #f5f5f5;
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

.search-input {
	flex: 1;
	height: 70rpx;
	font-size: 28rpx;
}

.clear-icon {
	font-size: 28rpx;
	color: #999;
	padding: 0 10rpx;
}

.cancel-btn {
	padding: 0 20rpx;
	font-size: 28rpx;
	color: #333;
}

/* 搜索历史样式 */
.search-history {
	background-color: #fff;
	margin: 20rpx;
	border-radius: 10rpx;
	padding: 20rpx;
}

.history-header {
	display: flex;
	justify-content: space-between;
	margin-bottom: 20rpx;
}

.history-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.clear-history {
	font-size: 24rpx;
	color: #999;
}

.history-list {
	display: flex;
	flex-direction: column;
}

.history-item {
	display: flex;
	align-items: center;
	padding: 15rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.history-icon {
	font-size: 28rpx;
	color: #999;
	margin-right: 10rpx;
}

.history-text {
	font-size: 28rpx;
	color: #333;
}

/* 热门搜索样式 */
.hot-search {
	background-color: #fff;
	margin: 20rpx;
	border-radius: 10rpx;
	padding: 20rpx;
}

.hot-header {
	margin-bottom: 20rpx;
}

.hot-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}

.hot-list {
	display: flex;
	flex-wrap: wrap;
}

.hot-item {
	padding: 10rpx 20rpx;
	background-color: #f5f5f5;
	border-radius: 30rpx;
	margin: 10rpx;
	font-size: 24rpx;
	color: #666;
}

/* 搜索结果样式 */
.search-results {
	flex: 1;
	padding: 20rpx;
}

.loading {
	display: flex;
	justify-content: center;
	padding: 40rpx 0;
	color: #999;
	font-size: 28rpx;
}

.no-results {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	color: #999;
	font-size: 28rpx;
}

.no-results-tip {
	margin-top: 20rpx;
	font-size: 24rpx;
}

.result-list {
	display: flex;
	flex-direction: column;
}

.result-item {
	display: flex;
	padding: 20rpx;
	background-color: #fff;
	border-radius: 10rpx;
	margin-bottom: 20rpx;
}

.result-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 10rpx;
}

.result-info {
	flex: 1;
	margin-left: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.result-name {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 10rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.result-price {
	font-size: 32rpx;
	color: #ff6700;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.result-sales {
	font-size: 24rpx;
	color: #999;
}
</style>
