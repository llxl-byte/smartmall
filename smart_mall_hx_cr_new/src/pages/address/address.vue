<template>
	<view class="container">
		<!-- 地址列表 -->
		<view class="address-list" v-if="addressList.length > 0">
			<view
				class="address-item"
				v-for="(item, index) in addressList"
				:key="item.id"
				@click="selectAddress(item)"
			>
				<view class="address-info">
					<view class="address-header">
						<text class="name">{{ item.name }}</text>
						<text class="phone">{{ item.phone }}</text>
						<text class="default-tag" v-if="item.isDefault === 1">默认</text>
					</view>
					<view class="address-detail">
						{{ item.province + item.city + item.district + item.address }}
					</view>
				</view>
				<view class="address-actions">
					<view class="action-btn edit" @click.stop="editAddress(item)">编辑</view>
					<view class="action-btn delete" @click.stop="confirmDelete(item)">删除</view>
				</view>
			</view>
		</view>

		<!-- 空地址提示 -->
		<view class="empty-address" v-else>
			<image class="empty-icon" src="/static/empty-address.png" mode="aspectFit"></image>
			<text class="empty-text">您还没有添加收货地址</text>
		</view>

		<!-- 添加地址按钮 -->
		<view class="add-btn" @click="goToAddAddress">
			<text class="add-icon">+</text>
			<text class="add-text">新增收货地址</text>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			addressList: [],
			userId: null,
			isFromOrder: false, // 是否从订单页面跳转而来
		}
	},
	onLoad(options) {
		// 获取用户信息
		const userInfo = uni.getStorageSync('userInfo');
		if (!userInfo) {
			uni.showToast({
				title: '请先登录',
				icon: 'none',
				complete: () => {
					setTimeout(() => {
						uni.navigateTo({ url: '/pages/login/login' });
					}, 1500);
				}
			});
			return;
		}

		this.userId = userInfo.id;

		// 判断是否从订单页面跳转而来
		if (options.from === 'order') {
			this.isFromOrder = true;
		}

		// 加载地址列表
		this.loadAddressList();
	},
	methods: {
		// 加载地址列表
		loadAddressList() {
			uni.showLoading({ title: '加载中...' });

			uni.request({
				url: `${API_BASE_URL}/address/list?userId=${this.userId}`,
				method: 'GET',
				success: (res) => {
					if (res.data && res.data.success) {
						this.addressList = res.data.data || [];
					} else {
						uni.showToast({
							title: res.data.message || '获取地址列表失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('获取地址列表失败', err);
					uni.showToast({
						title: '获取地址列表失败，请检查网络',
						icon: 'none'
					});
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},

		// 选择地址（从订单页面跳转而来时使用）
		selectAddress(address) {
			if (this.isFromOrder) {
				// 将选中的地址信息存储到本地
				uni.setStorageSync('selectedAddress', address);
				// 返回上一页
				uni.navigateBack();
			}
		},

		// 跳转到添加地址页面
		goToAddAddress() {
			uni.navigateTo({
				url: '/pages/address/add'
			});
		},

		// 跳转到编辑地址页面
		editAddress(address) {
			uni.navigateTo({
				url: `/pages/address/edit?id=${address.id}`
			});
		},

		// 确认删除地址
		confirmDelete(address) {
			uni.showModal({
				title: '提示',
				content: '确定要删除该收货地址吗？',
				success: (res) => {
					if (res.confirm) {
						this.deleteAddress(address.id);
					}
				}
			});
		},

		// 删除地址
		deleteAddress(id) {
			uni.showLoading({ title: '删除中...' });

			uni.request({
				url: `${API_BASE_URL}/address/delete`,
				method: 'POST',
				data: {
					id: id,
					userId: this.userId
				},
				header: {
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				success: (res) => {
					if (res.data && res.data.success) {
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						});
						// 重新加载地址列表
						this.loadAddressList();
					} else {
						uni.showToast({
							title: res.data.message || '删除失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('删除地址失败', err);
					uni.showToast({
						title: '删除失败，请检查网络',
						icon: 'none'
					});
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		}
	}
}
</script>

<style>
.container {
	padding-bottom: 120rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.address-list {
	background-color: #fff;
	margin-top: 20rpx;
}

.address-item {
	padding: 30rpx;
	border-bottom: 1rpx solid #eee;
}

.address-header {
	display: flex;
	align-items: center;
	margin-bottom: 10rpx;
}

.name {
	font-size: 32rpx;
	font-weight: bold;
	margin-right: 20rpx;
}

.phone {
	font-size: 28rpx;
	color: #666;
}

.default-tag {
	margin-left: 20rpx;
	background-color: #ff6700;
	color: #fff;
	font-size: 24rpx;
	padding: 4rpx 10rpx;
	border-radius: 4rpx;
}

.address-detail {
	font-size: 28rpx;
	color: #333;
	line-height: 1.5;
}

.address-actions {
	display: flex;
	justify-content: flex-end;
	margin-top: 20rpx;
}

.action-btn {
	padding: 10rpx 20rpx;
	font-size: 26rpx;
	border-radius: 4rpx;
	margin-left: 20rpx;
}

.edit {
	color: #007AFF;
	border: 1rpx solid #007AFF;
}

.delete {
	color: #FF3B30;
	border: 1rpx solid #FF3B30;
}

.empty-address {
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
	font-size: 28rpx;
	color: #999;
}

.add-btn {
	position: fixed;
	bottom: 30rpx;
	left: 30rpx;
	right: 30rpx;
	height: 90rpx;
	background-color: #007AFF;
	color: #fff;
	border-radius: 45rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-icon {
	font-size: 40rpx;
	margin-right: 10rpx;
}

.add-text {
	font-size: 32rpx;
}
</style>