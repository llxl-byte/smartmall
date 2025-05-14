<template>
	<view class="container">
		<view class="register-header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">智能商城</text>
		</view>

		<view class="register-card">
			<view class="welcome-text">
				<text class="welcome-title">用户注册</text>
				<text class="welcome-subtitle">创建您的账号开始购物之旅</text>
			</view>

			<view class="form-item">
				<view class="input-icon">👤</view>
				<input class="input" v-model="username" placeholder="请输入用户名"/>
			</view>

			<view class="form-item">
				<view class="input-icon">🔒</view>
				<input class="input" v-model="password" type="password" placeholder="请输入密码"/>
			</view>

			<view class="form-item">
				<view class="input-icon">✅</view>
				<input class="input" v-model="confirmPassword" type="password" placeholder="请再次输入密码"/>
			</view>

			<view class="error-message" v-if="errorMessage">{{errorMessage}}</view>

			<button class="btn-register" @click="register()" hover-class="btn-register-hover">注册</button>

			<view class="login-link">
				<text>已有账号？</text>
				<text class="link" @click="goToLogin">立即登录</text>
			</view>
		</view>

		<view class="register-footer">
			<text>© 2023 智能商城系统</text>
		</view>
	</view>
</template>

<script>
	export default {
data() {
	return {
		username:"",
		password:"",
		confirmPassword:"",
		errorMessage: "",
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
	register(){
		this.errorMessage = '';
		// 表单校验
		if (!this.username) {
			this.errorMessage = "用户名不能为空";
			return;
		}
		if (!this.password) {
			this.errorMessage = "密码不能为空";
			return;
		}
		if (this.password !== this.confirmPassword) {
			this.errorMessage = "两次输入的密码不一致";
			return;
		}

		// 确保baseUrl使用HTTP协议
		let apiUrl = this.baseUrl;
		if (apiUrl.startsWith('https:')) {
			apiUrl = apiUrl.replace('https:', 'http:');
			console.log('URL changed to HTTP:', apiUrl);
		}

		// 显示加载中提示
		uni.showLoading({
			title: '注册中...'
		});

		// 发送注册请求
		uni.request({
			url: `${apiUrl}/mallUserRegister`,
			method: 'POST',
			data: {
				username: this.username,
				password: this.password,
				nickname: this.username, // 默认使用用户名作为昵称
				phone: '' // 可以在表单中添加手机号字段
			},
			fail: (err) => {
				console.log(err);
				uni.hideLoading();
				this.errorMessage = "注册失败，请稍后再试";
			},
			success: (res) => {
				console.log(res);
				uni.hideLoading();

				if(res.data === true || (res.data && res.data.success)){
					uni.showToast({
						title: "注册成功",
						icon: 'success',
						duration: 2000
					});

					// 注册成功后跳转到登录页面
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 1500);
				} else {
					this.errorMessage = res.data && res.data.message ? res.data.message : "注册失败，请稍后再试";
				}
			}
		})
	},

	goToLogin() {
		// 跳转到登录页面
		uni.navigateTo({
			url: '/pages/login/login'
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

.register-header {
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

.register-card {
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

.btn-register {
    margin-top: 25px;
    background: linear-gradient(to right, #4CAF50, #8BC34A);
    color: white;
    height: 48px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: bold;
    box-shadow: 0 4px 10px rgba(76, 175, 80, 0.2);
    transition: all 0.3s ease;
}

.btn-register-hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(76, 175, 80, 0.3);
    opacity: 0.9;
}

.error-message {
    color: #ff3b30;
    margin-top: 10px;
    font-size: 14px;
    text-align: center;
    animation: shake 0.5s ease-in-out;
}

.login-link {
    margin-top: 25px;
    text-align: center;
    font-size: 14px;
}

.link {
    color: #007AFF;
    margin-left: 5px;
    font-weight: bold;
}

.register-footer {
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