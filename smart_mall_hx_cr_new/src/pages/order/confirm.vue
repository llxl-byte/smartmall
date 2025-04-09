<template>
	<view class="order-confirm-container">
		<!-- 收货地址 -->
		<view class="address-section" @click="selectAddress">
			<view class="address-empty" v-if="!addressInfo">
				<text class="iconfont">&#xe6a9;</text>
				<text class="text">请选择收货地址</text>
				<text class="iconfont">&#xe6a3;</text>
			</view>
			<view class="address-info" v-else>
				<view class="info-top">
					<text class="name">{{ addressInfo.name }}</text>
					<text class="mobile">{{ addressInfo.phone }}</text>
				</view>
				<view class="info-bottom">
					<text class="address">{{ addressInfo.province + addressInfo.city + addressInfo.district + addressInfo.address }}</text>
					<text class="iconfont">&#xe6a3;</text>
				</view>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="goods-item" v-for="(item, index) in orderItems" :key="index">
				<image :src="item.image || '/static/default_item.png'" class="goods-img"></image>
				<view class="goods-info">
					<view class="goods-name">{{ item.name }}</view>
					<view class="goods-price-qty">
						<text class="goods-price">¥{{ item.price }}</text>
						<text class="goods-qty">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单金额 -->
		<view class="amount-section">
			<view class="amount-item">
				<text>商品金额</text>
				<text>¥{{ totalAmount }}</text>
			</view>
			<view class="amount-item">
				<text>运费</text>
				<text>¥0.00</text>
			</view>
			<view class="amount-item total">
				<text>实付金额</text>
				<text class="total-price">¥{{ totalAmount }}</text>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="action-bar">
			<view class="total-section">
				<text>实付金额：</text>
				<text class="total-price">¥{{ totalAmount }}</text>
			</view>
			<view class="submit-btn" @click="submitOrder">提交订单</view>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
		data() {
			return {
				itemId: null,
				quantity: 1,
				orderItems: [],
				addressInfo: null,
				totalAmount: '0.00',
				isLoading: true
			}
		},
		onLoad(options) {
			// 获取路由参数
			this.itemId = options.itemId;
			this.quantity = options.quantity || 1;

			// 获取本地存储的结算商品信息
			const checkoutItems = uni.getStorageSync('checkoutItems');
			
			if (checkoutItems && checkoutItems.length > 0) {
				// 如果是从购物车来的结算商品
				this.orderItems = checkoutItems;
				this.calculateTotal();
			} else if (this.itemId) {
				// 如果是直接购买单个商品
				this.loadItemInfo();
			}
			
			// 加载默认地址
			this.loadDefaultAddress();
		},
		methods: {
			// 加载单个商品信息
			loadItemInfo() {
				uni.request({
					url: `http://127.0.0.1:8083/selectByItemId?itemId=${this.itemId}`,
					success: (res) => {
						if (res.data) {
							const item = res.data;
							this.orderItems = [{
								id: item.id,
								name: item.name,
								price: item.price,
								image: item.mainImage,
								quantity: parseInt(this.quantity)
							}];
							this.calculateTotal();
						}
					},
					fail: (err) => {
						console.error('获取商品信息失败', err);
						uni.showToast({
							title: '获取商品信息失败',
							icon: 'none'
						});
					}
				});
			},

			// 加载购物车商品
			loadCartItems() {
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({
						url: '/pages/login/login'
					});
					return;
				}

				uni.request({
					url: `${API_BASE_URL}/selectByUserId?userId=${userInfo.id}`,
					success: (res) => {
						if (res.data && res.data.length > 0) {
							// 需要获取每个商品的详细信息
							const promises = res.data.map(cart => {
								return new Promise((resolve, reject) => {
									uni.request({
										url: `${API_BASE_URL}/selectByItemId?itemId=${cart.itemId}`,
										success: (itemRes) => {
											if (itemRes.data) {
												resolve({
													id: itemRes.data.id,
													name: itemRes.data.name,
													price: itemRes.data.price,
													image: itemRes.data.mainImage,
													quantity: cart.quantity
												});
											} else {
												resolve(null);
											}
										},
										fail: () => {
											resolve(null);
										}
									});
								});
							});

							Promise.all(promises).then(items => {
								this.orderItems = items.filter(item => item !== null);
								this.calculateTotal();
							});
						}
					},
					fail: (err) => {
						console.error('获取购物车信息失败', err);
					}
				});
			},

			// 加载默认地址
			loadDefaultAddress() {
				// 这里应该从后端获取用户的默认地址
				// 由于没有地址管理功能，这里使用模拟数据
				this.addressInfo = {
					name: '张三',
					phone: '13800138000',
					province: '广东省',
					city: '深圳市',
					district: '南山区',
					address: '科技园1号楼'
				};
			},

			// 选择地址
			selectAddress() {
				// 这里应该跳转到地址选择页面
				// 由于没有地址管理功能，这里使用模拟数据
				uni.showToast({
					title: '地址管理功能待开发',
					icon: 'none'
				});
			},

			// 计算总金额
			calculateTotal() {
				let total = 0;
				this.orderItems.forEach(item => {
					total += item.price * item.quantity;
				});
				this.totalAmount = total.toFixed(2);
			},

			// 提交订单
			submitOrder() {
				if (!this.addressInfo) {
					uni.showToast({
						title: '请选择收货地址',
						icon: 'none'
					});
					return;
				}

				if (this.orderItems.length === 0) {
					uni.showToast({
						title: '没有可购买的商品',
						icon: 'none'
					});
					return;
				}

				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({
						url: '/pages/login/login'
					});
					return;
				}

				// 提交订单
				uni.request({
					url: `${API_BASE_URL}/addMallOrder`,
					method: 'POST',
					data: {
						userId: userInfo.id,
						addressInfo: JSON.stringify(this.addressInfo)
					},
					success: (res) => {
						if (res.data) {
							uni.showToast({
								title: '订单提交成功',
								icon: 'success'
							});
							// 跳转到支付页面或订单详情页
							setTimeout(() => {
								uni.redirectTo({
									url: '/pages/order/success'
								});
							}, 1500);
						} else {
							uni.showToast({
								title: '订单提交失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('提交订单失败', err);
						uni.showToast({
							title: '提交订单失败',
							icon: 'none'
						});
					}
				});
			}
		}
	}
</script>

<style>
	.order-confirm-container {
		padding-bottom: 100rpx; /* 为底部操作栏留出空间 */
		background-color: #f5f5f5;
	}

	.address-section {
		background-color: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.address-empty {
		display: flex;
		align-items: center;
		color: #666;
	}

	.address-empty .text {
		flex: 1;
		margin: 0 20rpx;
	}

	.address-info {
		color: #333;
	}

	.info-top {
		display: flex;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.name {
		margin-right: 20rpx;
		font-weight: bold;
	}

	.info-bottom {
		display: flex;
		align-items: center;
	}

	.address {
		flex: 1;
		margin-right: 20rpx;
	}

	.goods-section {
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
	}

	.goods-item {
		display: flex;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #eee;
	}

	.goods-item:last-child {
		border-bottom: none;
	}

	.goods-img {
		width: 160rpx;
		height: 160rpx;
		margin-right: 20rpx;
	}

	.goods-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.goods-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 10rpx;
	}

	.goods-price-qty {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.goods-price {
		color: #ff6700;
		font-size: 32rpx;
	}

	.goods-qty {
		color: #999;
		font-size: 28rpx;
	}

	.amount-section {
		background-color: #fff;
		padding: 20rpx;
	}

	.amount-item {
		display: flex;
		justify-content: space-between;
		padding: 20rpx 0;
		font-size: 28rpx;
		color: #666;
	}

	.total {
		border-top: 1rpx solid #eee;
		padding-top: 20rpx;
		font-weight: bold;
		color: #333;
	}

	.total-price {
		color: #ff6700;
		font-size: 32rpx;
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
	}

	.total-section {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		padding-right: 20rpx;
	}

	.submit-btn {
		width: 240rpx;
		height: 100rpx;
		line-height: 100rpx;
		text-align: center;
		background-color: #ff6700;
		color: #fff;
		font-size: 32rpx;
	}
</style>
