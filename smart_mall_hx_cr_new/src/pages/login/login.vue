<template>
	<view class="container">
		<view class="form-item">
			<text class="label">用户名：</text>
			<input class="input" v-model="username" placeholder="请输入用户名"/>
		</view>
		<view class="form-item">
			<text class="label">密码：</text>
			<input class="input" v-model="password" type="password" placeholder="请输入密码"/>
		</view>
		<!--添加记住密码选项-->
		<view class="remember-password">
			<checkbox :checked="rememberPassword"@click="rememberPassword=!rememberPassword"/>
			<text class="remember-text">记住密码</text>
		</view>
		<view>
			<button class="btn-login" @click="login">登录</button>
		</view>
		<view v-if="errorMessage" class="error-message">
			<text>{{errorMessage}}</text>
		</view>
		<!-- 添加注册链接-->
		<view class="register-link">
			<text>还没有账号？</text>
			<text class="link" @click="goToRegister">去注册</text>
		</view>
	</view>



</template>

<script>
	export default {
		data() {
			return {
				username: '',
				password: '',
				errorMessage: '',
				rememberPassword: false,
				baseUrl: ''
			}
		},
		created() {
			// 获取当前域名并设置baseUrl
			const currentUrl = window.location.href;
			if (currentUrl.includes('.app.github.dev')) {
				// Codespace环境 - 使用完整的后端 URL
				// 将前端端口号替换为后端端口号 8083
				const domain = window.location.hostname;
				this.baseUrl = `https://${domain.replace('-8080', '-8083')}`;
			} else {
				// 本地开发环境
				this.baseUrl = 'http://127.0.0.1:8083';
			}
			console.log('Using API Base URL:', this.baseUrl); // 添加日志便于调试
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
				const serverUrl = `${this.baseUrl}/mallUserLogin`;

				// 发送请求
				uni.request({
					url: serverUrl,
					method: 'GET',
					data: {
						username: this.username,
						password: this.password
					},
					header: {
						'Accept': 'application/json',
						'Content-Type': 'application/x-www-form-urlencoded'
					},
					success: (res) => {
						console.log('Request URL:', serverUrl); // 打印请求URL
						console.log('Response:', res); // 打印完整响应

						if (typeof res.data === 'string' && res.data.includes('<!DOCTYPE html>')) {
							console.error('Received HTML instead of JSON');
							this.errorMessage = '服务器配置错误，请联系管理员';
							return;
						}

						if (res.data && res.data.success) {
							uni.showToast({
								title: '登录成功',
								icon: 'success',
								duration: 2000
							});

							if (this.rememberPassword) {
								uni.setStorageSync('savedUsername', this.username);
								uni.setStorageSync('savedPassword', this.password);
							} else {
								uni.removeStorageSync('savedUsername');
								uni.removeStorageSync('savedPassword');
							}

							uni.setStorageSync('userInfo', res.data.data);

							setTimeout(() => {
								uni.switchTab({
									url: '/pages/index/index'
								});
							}, 1500);
						} else {
							this.errorMessage = res.data?.message || '登录失败，请稍后再试';
						}
					},
					fail: (err) => {
						console.error('Request failed:', err);
						this.errorMessage = '网络请求失败，请检查网络连接';
					}
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
