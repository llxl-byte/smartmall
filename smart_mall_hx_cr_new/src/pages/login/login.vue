<template>
	<view class="container">
		<view class="login-header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">智能商城</text>
		</view>

		<view class="login-card">
			<view class="welcome-text">
				<text class="welcome-title">欢迎登录</text>
				<text class="welcome-subtitle">请使用您的账号登录系统</text>
			</view>

			<view class="form-item">
				<view class="input-icon">👤</view>
				<input class="input" type="text" v-model="username" placeholder="请输入用户名" />
			</view>

			<view class="form-item">
				<view class="input-icon">🔒</view>
				<input class="input" type="password" v-model="password" placeholder="请输入密码" />
			</view>

			<view class="remember-password">
				<checkbox :checked="rememberPassword" @click="rememberPassword = !rememberPassword" color="#007AFF" />
				<text class="remember-text" @click="rememberPassword = !rememberPassword">记住密码</text>
			</view>

			<view class="error-message" v-if="errorMessage">{{ errorMessage }}</view>

			<button class="btn-login" @click="login" hover-class="btn-login-hover">登录</button>

			<view class="register-link">
				<text>还没有账号？</text>
				<text class="link" @click="goToRegister">立即注册</text>
			</view>
		</view>

		<view class="login-footer">
			<text>© 2023 智能商城系统</text>
		</view>
	</view>
</template>

<script>
	import { API_BASE_URL } from '@/config.js';

	export default {
		data() {
			return {
				username: '',
				password: '',
				rememberPassword: false,
				errorMessage: '',
				baseUrl: ''
			}
		},
		onLoad() {
			// 尝试从本地存储获取保存的用户名和密码
			const savedUsername = uni.getStorageSync('savedUsername');
			const savedPassword = uni.getStorageSync('savedPassword');

			if (savedUsername && savedPassword) {
				this.username = savedUsername;
				this.password = savedPassword;
				this.rememberPassword = true;
			}

			// 设置API基础URL
			this.baseUrl = API_BASE_URL;
			console.log('Using API Base URL:', this.baseUrl);
		},
		methods: {
			login() {
				// 重置错误信息
				this.errorMessage = "";

				// 验证输入
				if (!this.username || !this.password) {
					this.errorMessage = "用户名和密码不能为空";
					return;
				}

				// 构建完整的URL
				// 使用正确的登录接口路径，确保使用HTTP协议
				let serverUrl = this.baseUrl;
				if (serverUrl.startsWith('https:')) {
					serverUrl = serverUrl.replace('https:', 'http:');
					console.log('URL changed to HTTP:', serverUrl);
				}
				serverUrl = `${serverUrl}/mallUserLogin`;

				// 发送请求
				uni.request({
					url: serverUrl,
					method: 'GET',
					data: {
						// 使用正确的参数名称
						username: this.username,
						password: this.password
					},
					header: {
						'Accept': 'application/json',
						'Content-Type': 'application/x-www-form-urlencoded'
					},
					success: (res) => {
						console.log('Request URL:', serverUrl);
						console.log('Response:', res);

						if (typeof res.data === 'string' && res.data.includes('<!DOCTYPE html>')) {
							console.error('Received HTML instead of JSON');
							this.errorMessage = '服务器配置错误，请联系管理员';
							return;
						}

						if (res.data && res.data.success) {
							// 登录成功，处理响应数据
							uni.showToast({
								title: '登录成功',
								icon: 'success',
								duration: 2000
							});

							// 处理记住密码
							if (this.rememberPassword) {
								uni.setStorageSync('savedUsername', this.username);
								uni.setStorageSync('savedPassword', this.password);
							} else {
								uni.removeStorageSync('savedUsername');
								uni.removeStorageSync('savedPassword');
							}

							// 存储用户信息
							if (res.data.data) {
								// 处理后端返回的数据格式，从data中提取userInfo和token
								const responseData = res.data.data;
								let userInfo = {};
								let token = '';

								// 检查后端返回的数据格式
								console.log('Response data structure:', responseData);

								if (responseData.userInfo) {
									// 新格式，包含userInfo和token
									userInfo = responseData.userInfo || {};
									token = responseData.token || '';
								} else if (typeof responseData === 'object') {
									// 可能是直接返回的用户对象
									userInfo = responseData;
									// token可能在其他地方
								} else if (typeof responseData === 'string') {
									// 可能只返回了token字符串
									token = responseData;
									userInfo = {
										username: this.username,
										nickname: this.username
									};
								}

								// 构建完整的用户信息对象
								const completeUserInfo = {
									id: userInfo.id || '',
									username: this.username,
									nickname: userInfo.nickname || this.username,
									avatar: userInfo.avatar || '/static/default-avatar.png',
									token: token
								};

								// 存储用户信息
								uni.setStorageSync('userInfo', completeUserInfo);
								console.log('User info stored:', completeUserInfo);

								// 如果有令牌，存储令牌
								if (token) {
									localStorage.setItem('jwtToken', token);
									console.log('JWT Token stored:', token);
								}
							} else {
								// 如果没有用户数据，创建一个基本的用户信息对象
								const userInfo = {
									id: '',
									username: this.username,
									nickname: this.username,
									avatar: '/static/default-avatar.png',
									token: ''
								};
								uni.setStorageSync('userInfo', userInfo);
								console.log('Basic user info stored:', userInfo);
							}

							// 跳转到首页
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/index/index'
								});
							}, 1500);
						} else {
							// 登录失败，显示错误信息
							this.errorMessage = res.data?.message || '登录失败，请稍后再试';
							console.error('Login failed:', res.data);
						}
					},
					fail: (err) => {
						// 网络请求失败，显示错误信息
						console.error('Request failed:', err);
						this.errorMessage = '网络请求失败，请检查网络连接';
					}
				});
			},
			goToRegister() {
				// 跳转到注册页面
				uni.navigateTo({
					url: '/pages/register/register'
				});
			}
		}
	}
</script>

<style>
.container {
    padding: 0;
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.login-header {
    padding: 40px 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.logo {
    width: 80px;
    height: 80px;
    margin-bottom: 10px;
}

.title {
    font-size: 24px;
    font-weight: bold;
    color: #333;
}

.login-card {
    margin: 0 20px;
    padding: 30px 20px;
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
    animation: fadeIn 0.8s ease-in-out;
}

.welcome-text {
    text-align: center;
    margin-bottom: 25px;
}

.welcome-title {
    display: block;
    font-size: 22px;
    font-weight: bold;
    color: #333;
    margin-bottom: 5px;
}

.welcome-subtitle {
    display: block;
    font-size: 14px;
    color: #666;
}

.form-item {
    margin-bottom: 20px;
    position: relative;
}

.input-icon {
    position: absolute;
    left: 10px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: #999;
}

.input {
    width: 100%;
    height: 48px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 0 15px 0 40px;
    font-size: 16px;
    transition: all 0.3s ease;
    background-color: #f9f9f9;
}

.input:focus {
    border-color: #007AFF;
    background-color: #fff;
    box-shadow: 0 0 0 2px rgba(0, 122, 255, 0.1);
}

.btn-login {
    margin-top: 25px;
    background: linear-gradient(to right, #007AFF, #00c6ff);
    color: white;
    height: 48px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    box-shadow: 0 4px 10px rgba(0, 122, 255, 0.2);
    transition: all 0.3s ease;
}

.btn-login-hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(0, 122, 255, 0.3);
    opacity: 0.9;
}

.error-message {
    color: #ff3b30;
    margin-top: 10px;
    font-size: 14px;
    text-align: center;
    animation: shake 0.5s ease-in-out;
}

.register-link {
    margin-top: 25px;
    text-align: center;
    font-size: 14px;
}

.link {
    color: #007AFF;
    margin-left: 5px;
    font-weight: bold;
}

.remember-password {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
}

.remember-text {
    margin-left: 5px;
    font-size: 14px;
    color: #666;
}

.login-footer {
    margin-top: auto;
    padding: 20px 0;
    text-align: center;
    font-size: 12px;
    color: #999;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes shake {
    0%, 100% { transform: translateX(0); }
    10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
    20%, 40%, 60%, 80% { transform: translateX(5px); }
}
</style>
