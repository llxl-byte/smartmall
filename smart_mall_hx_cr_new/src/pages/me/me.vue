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

		<!-- 功能菜单区域 -->
		<view class="menu-section">
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
			<view class="menu-item" @click="goToAddress">
				<text class="menu-icon">🧭</text>
				<text class="menu-text">收货地址</text>
				<text class="menu-arrow">></text>
			</view>
			<view class="menu-item" @click="goToSettings">
				<text class="menu-icon">⚙️</text>
				<text class="menu-text">设置</text>
				<text class="menu-arrow">></text>
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
	export default {
		data() {
			return {
				isLogin: false,
				userInfo: {}

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
					if (userInfo) {
						this.isLogin = true;
						this.userInfo = userInfo;
						console.log('已登录用户信息:', this.userInfo);
					} else {
						this.isLogin = false;
						this.userInfo = {};
					}
				} catch (e) {
					console.error('获取登录状态失败', e);
					this.isLogin = false;
					this.userInfo = {};
				}
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
				// TODO: 确认优惠券页面路径
				uni.navigateTo({
					url: '/pages/coupons/coupons' // 假设路径，后续需要确认或创建
				});
			},

			// 跳转到地址管理页
			goToAddress() {
				if (!this.isLogin) {
					this.goToLogin();
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
				uni.navigateTo({
					url: '/pages/settings/settings'
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

.logout-container {
    margin-top: 20px;
    padding: 15px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    border-top: 1px solid #f0f0f0;
}

.logout-text {
    color: #FF3B30;
    font-size: 16px;
    text-align: center;
}
</style>