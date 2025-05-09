<template>
	<view class="container">
		<view class="form-item">
			<text class="label">用户名</text>
			<input class="input" type="text" v-model="username" placeholder="请输入用户名" />
		</view>
		<view class="form-item">
			<text class="label">密码</text>
			<input class="input" type="password" v-model="password" placeholder="请输入密码" />
		</view>
		<view class="remember-password">
			<checkbox :checked="rememberPassword" @click="rememberPassword = !rememberPassword" />
			<text class="remember-text" @click="rememberPassword = !rememberPassword">记住密码</text>
		</view>
		<view class="error-message" v-if="errorMessage">{{ errorMessage }}</view>
		<button class="btn-login" @click="login">登录</button>
		<view class="register-link">
			<text>还没有账号？</text>
			<text class="link" @click="goToRegister">立即注册</text>
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
    padding: 20px;
}

.form-item {
    margin-bottom: 15px;
}

.label {
    display: block;
    margin-bottom: 5px;
    font-size: 14px;
}

.input {
    width: 100%;
    height: 40px;
    border: 1px solid #ddd;
    border-radius: 4px;
    padding: 0 10px;
}

.btn-login {
    margin-top: 20px;
    background-color: #007AFF;
    color: white;
}

.error-message {
    color: red;
    margin-top: 10px;
    font-size: 14px;
}
/* 保持原有样式不变，添加注册链接的样式 */
.register-link {
    margin-top: 20px;
    text-align: center;
}

.link {
    color: #007AFF;
    margin-left: 5px;
}

/* 保持原有样式，添加记住密码选项的样式 */
.remember-password {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
}

.remember-text {
    margin-left: 5px;
    font-size: 14px;
}
</style>
