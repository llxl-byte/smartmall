<template>
	<view class="container">
		<!-- 用户信息区域 -->
		<view class="user-info-section">
			<!-- 未登录状态 -->
			 <view v-if="!isLogin" class="not-login">
				<image class="avatar" src="/static/default-avatar.png"></image>
			<view class="login-btn-area">
				<button class="login-btn" @click="goToLogin">登录/注册</button>
			</view>
		</view>
		<!-- 已登录状态 -->
		<view v-else class="logged-in">
			<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'"></image>
			<view class="user-details">
				<text class="nickname">{{ userInfo.nickname || userInfo.username }}</text>
				<text class="user-id">{{ userInfo.id }}</text>
			</view>
		</view>

		<!-- 用户数据统计 -->
		<view class="user-stats" v-if="isLogin">
			<view class="stat-item" @click="goToFavorites">
				<text class="stat-value">{{favoriteCount}}</text>
				<text class="stat-label">收藏</text>
			</view>
			<view class="stat-item" @click="goToHistory">
				<text class="stat-value">{{historyCount}}</text>
				<text class="stat-label">浏览历史</text>
			</view>
			<view class="stat-item" @click="goToOrders">
				<text class="stat-value">{{orderCount}}</text>
				<text class="stat-label">订单</text>
			</view>
			<view class="stat-item" @click="goToCoupons">
				<text class="stat-value">{{couponCount}}</text>
				<text class="stat-label">优惠券</text>
			</view>
		</view>

		<!-- 功能菜单区域 -->
		<view class="menu-section">
			<view class="menu-group">
				<view class="menu-item" @click="goToOrders">
					<text class="menu-icon">📦</text>
					<text class="menu-text">我的订单</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToCoupons">
					<text class="menu-icon">🎟️</text>
					<text class="menu-text">我的优惠券</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToFavorites">
					<text class="menu-icon">❤️</text>
					<text class="menu-text">我的收藏</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToHistory">
					<text class="menu-icon">🕒</text>
					<text class="menu-text">浏览历史</text>
					<text class="menu-arrow">></text>
				</view>
			</view>

			<view class="menu-group">
				<view class="menu-item" @click="goToAddress">
					<text class="menu-icon">🧭</text>
					<text class="menu-text">收货地址</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToCustomerService">
					<text class="menu-icon">💬</text>
					<text class="menu-text">智能客服</text>
					<text class="menu-arrow">></text>
				</view>
				<view class="menu-item" @click="goToSettings">
					<text class="menu-icon">⚙️</text>
					<text class="menu-text">设置</text>
					<text class="menu-arrow">></text>
				</view>
			</view>

			<view v-if="isLogin" class="logout-container" @click="logout">
				<text class="logout-text">退出登录</text>
			</view>
		</view>
	</view>


		<!-- <button v-on:click="toRegister()">去注册</button>
		<button @click="toLogin">去登录</button> -->
	</view>
</template>

<script>
	import { API_BASE_URL } from '@/config.js';

	export default {
		data() {
			return {
				isLogin: false,
				userInfo: {},
				favoriteCount: 0,
				historyCount: 0,
				orderCount: 0,
				couponCount: 0
			}
		},
		onShow() {
			// 每次页面显示时检查登录状态
			this.checkLoginStatus();
		},
		methods: {
				// 检查登录状态
				checkLoginStatus() {
				try {
					const userInfo = uni.getStorageSync('userInfo');
					console.log('获取到的用户信息:', userInfo);
					if (userInfo) {
						this.isLogin = true;
						this.userInfo = userInfo;
						console.log('用户头像URL:', this.userInfo.avatar);
						console.log('用户名:', this.userInfo.nickname || this.userInfo.username);

						// 加载用户数据
						this.loadUserData();
					} else {
						this.isLogin = false;
						this.userInfo = {};
						console.log('未获取到用户信息，显示默认头像');
					}
				} catch (e) {
					console.error('获取登录状态失败', e);
					this.isLogin = false;
					this.userInfo = {};
				}
			},

			// 加载用户数据
			loadUserData() {
				// 用户已登录但ID为空的情况处理
				if (!this.isLogin) return;

				// 检查用户ID是否存在
				const userId = this.userInfo.id;
				if (!userId || userId === '') {
					console.log('用户ID不存在，无法加载用户数据，但会显示基本信息');
					// 设置默认数据
					this.favoriteCount = 0;
					this.historyCount = 0;
					this.orderCount = 0;
					this.couponCount = 0;
					return;
				}

				console.log('加载用户ID:', userId, '的数据');

				// 加载收藏数量
				uni.request({
					url: `${API_BASE_URL}/behavior/count?userId=${userId}&behaviorType=4`,
					success: (res) => {
						if (res.data && res.data.success) {
							this.favoriteCount = res.data.data || 0;
						}
					}
				});

				// 加载浏览历史数量
				uni.request({
					url: `${API_BASE_URL}/behavior/count?userId=${userId}&behaviorType=1`,
					success: (res) => {
						if (res.data && res.data.success) {
							this.historyCount = res.data.data || 0;
						}
					}
				});

				// 加载订单数量
				uni.request({
					url: `${API_BASE_URL}/order/count?userId=${userId}`,
					success: (res) => {
						if (res.data && res.data.success) {
							this.orderCount = res.data.data || 0;
						}
					}
				});

				// 加载优惠券数量
				uni.request({
					url: `${API_BASE_URL}/api/coupons/count?userId=${userId}`,
					success: (res) => {
						if (res.data && res.data.success) {
							this.couponCount = res.data.data || 0;
						}
					},
					fail: (err) => {
						console.error('获取优惠券数量失败:', err);
					}
				});
			},
			// 跳转到登录页
			goToLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			},

			// 跳转到订单页
			goToOrders() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}

				// 检查用户ID是否存在
				if (!this.userInfo.id || this.userInfo.id === '') {
					uni.showToast({
						title: '请先完善个人信息',
						icon: 'none'
					});
					// 可以选择跳转到设置页面
					setTimeout(() => {
						this.goToSettings();
					}, 1500);
					return;
				}

				uni.navigateTo({
					url: '/pages/orders/orders'
				});
			},

			// 跳转到优惠券页
			goToCoupons() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}

				// 检查用户ID是否存在
				if (!this.userInfo.id || this.userInfo.id === '') {
					uni.showToast({
						title: '请先完善个人信息',
						icon: 'none'
					});
					// 可以选择跳转到设置页面
					setTimeout(() => {
						this.goToSettings();
					}, 1500);
					return;
				}

				uni.navigateTo({
					url: '/pages/coupons/coupons'
				});
			},

			// 跳转到地址管理页
			goToAddress() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}

				// 检查用户ID是否存在
				if (!this.userInfo.id || this.userInfo.id === '') {
					uni.showToast({
						title: '请先完善个人信息',
						icon: 'none'
					});
					// 可以选择跳转到设置页面
					setTimeout(() => {
						this.goToSettings();
					}, 1500);
					return;
				}

				uni.navigateTo({
					url: '/pages/address/address'
				});
			},

			// 跳转到设置页
			goToSettings() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}
				// 设置页不需要检查ID，因为可以在设置页完善信息
				uni.navigateTo({
					url: '/pages/settings/settings'
				});
			},

			// 跳转到智能客服页
			goToCustomerService() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}
				uni.navigateTo({
					url: '/pages/chat/chat'
				});
			},

			// 跳转到收藏页面
			goToFavorites() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}
				uni.navigateTo({
					url: '/pages/favorites/favorites'
				});
			},

			// 跳转到浏览历史页面
			goToHistory() {
				if (!this.isLogin) {
					this.goToLogin();
					return;
				}
				uni.navigateTo({
					url: '/pages/history/history'
				});
			},

			// 退出登录
			logout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							// 清除用户信息
							uni.removeStorageSync('userInfo');
							// 清除记住的用户名密码
							uni.removeStorageSync('savedUsername');
							uni.removeStorageSync('savedPassword');

							// 更新状态
							this.isLogin = false;
							this.userInfo = {};

							uni.showToast({
								title: '已退出登录',
								icon: 'success'
							});
						}
					}
				});
			}
		}
	}
		// methods: {
		// 	toRegister(){
		// 		//跳转到tabbar用uni.switchto
		// 		//跳转到普通.vue（register.vue）用uni.navagateTo
		// 		//uni.navigateTo() 用于导航到非tabBar页面。与 switchTab 不同， navigateTo 会保留当前页面，创建新页面并跳转。
		// 		uni.navigateTo({
		// 			url:"/pages/register/register"
		// 		})
		// 	},
		// 	toLogin(){
		// 		uni.navigateTo({
		// 			url:"/pages/login/login"
		// 		})
		// 	}

		// }

</script>

<style>
.container {
	padding: 0;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.user-info-section {
	background-color: #007AFF;
	padding: 30px 20px;
	color: white;
	border-bottom-left-radius: 20px;
	border-bottom-right-radius: 20px;
}

.not-login, .logged-in {
	display: flex;
	align-items: center;
}

.avatar {
	width: 80px;
	height: 80px;
	border-radius: 50%;
	background-color: #fff;
	object-fit: cover;
	border: 2px solid #fff;
	box-shadow: 0 2px 6px rgba(0,0,0,0.1);
	cursor: pointer;
}

.login-btn-area {
	margin-left: 20px;
}

.login-btn {
	background-color: transparent;
	border: 1px solid white;
	color: white;
	font-size: 16px;
	padding: 5px 15px;
}

.user-details {
	margin-left: 20px;
}

.nickname {
	font-size: 18px;
	font-weight: bold;
	margin-bottom: 5px;
	display: block;
}

.user-id {
	font-size: 14px;
	opacity: 0.8;
}

/* 用户数据统计区域 */
.user-stats {
	display: flex;
	justify-content: space-around;
	background-color: white;
	padding: 20px 0;
	margin: -20px 15px 15px;
	border-radius: 10px;
	box-shadow: 0 2px 10px rgba(0,0,0,0.05);
	position: relative;
	z-index: 10;
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.stat-value {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin-bottom: 5px;
}

.stat-label {
	font-size: 14px;
	color: #666;
}

.menu-section {
	margin-top: 15px;
	background-color: white;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 15px 20px;
	border-bottom: 1px solid #f0f0f0;
}

.menu-icon {
	margin-right: 10px;
	font-size: 20px;
}

.menu-text {
	flex: 1;
	font-size: 16px;
	color: black;
}

.menu-arrow {
	color: #999;
}

.logout {
	margin-top: 15px;
	justify-content: center;
	color: #FF3B30;
}

.menu-group {
	margin-bottom: 15px;
	border-radius: 10px;
	overflow: hidden;
	box-shadow: 0 1px 5px rgba(0,0,0,0.05);
}

.logout-container {
    margin-top: 20px;
    padding: 15px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top: 1px solid #f0f0f0;
	border-radius: 10px;
	background-color: white;
	box-shadow: 0 1px 5px rgba(0,0,0,0.05);
}

.logout-text {
    color: #FF3B30;
    font-size: 16px;
    text-align: center;
}
</style>