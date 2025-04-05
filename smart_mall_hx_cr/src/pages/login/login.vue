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
				rememberPassword: false
				
			}
		},
		onLoad() {
			// 页面加载时，尝试从本地存储获取用户名和密码
			try {
				const savedUsername = uni.getStorageSync('savedUsername');
				const savedPassword = uni.getStorageSync('savedPassword');
				if (savedUsername && savedPassword) {
					this.username = savedUsername;
					this.password = savedPassword;
					this.rememberPassword = true;
				}
			} catch (e) {
				console.log(e);
			}
		},
		methods: {
			login() {
				// 重置错误信息
				this.errorMessage = "";

				// 用户名和密码不能为空
				if (!this.username || !this.password) {
					this.errorMessage = "用户名和密码不能为空";
					return;
				}
				// 构建请求URL
				let serverUrl = "http://127.0.0.1:8083/mallUserLogin?password=" + encodeURIComponent(this.password) + "&username=" + encodeURIComponent(this.username);
				// 发送登录请求
				uni.request({
					url: serverUrl,
					method: 'GET',
					success: (res) => {
						console.log(res);
						//根据服务器返回的数据判断登录是否成功
						if(res.data && res.data.success){
							//登录成功
							uni.showToast({
								title: '登录成功',
								icon: 'success',
								duration: 2000
							});
							//保存用户信息到本地
							uni.setStorageSync('userInfo', res.data.data);
							// 如果选择了记住密码，保存用户名和密码到本地存储
							if (this.rememberPassword) {
								uni.setStorageSync('savedUsername', this.username);
								uni.setStorageSync('savedPassword', this.password);
							} else {
								// 如果取消了记住密码，清除本地存储中的用户名和密码
								uni.removeStorageSync('savedUsername');
								uni.removeStorageSync('savedPassword');
							}
							//跳转到首页
							setTimeout(() => {
								uni.switchTab({
									url: '/pages/index/index'
								})
							}, 1500)
						}else{
							//登录失败
							this.errorMessage = res.data.message || '登录失败，请稍后再试';
						}
					},
					fail: (err) => {
						console.error(err);
						this.errorMessage = '网络登录请求失败，请稍后再试';
					}
				})
			},
			goToRegister(){
				uni.navigateTo({
					url: '/pages/register/register'
				})
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
