<template>
	<view class="contanier">
		<view class="form-item">
			<text class="label">用户名：</text>
			<input class="input" v-model="username" placeholder="请输入用户名"/>
		</view>
		<view class="form-item">
			<text class="label">密码：</text>
			<input class="input" v-model="password" type="password" placeholder="请输入密码"/>
		</view>
		<view class="form-item">
			<text class="label">确认密码：</text>
			<input class="input" v-model="confirmPassword" type="password" placeholder="请再次输入密码"/>
		</view>
		<view>
			<button class="btn-register" v-on:click="register()">注册</button>
		</view>
		<view v-if="errorMessage" class="error-message">    <!-- v-if 是Vue.js中的一个指令，v-if="errorMessage" 是 Vue 的条件渲染指令，只有当 errorMessage 有值时才会显示这个元素。 -->
			<text style="color:red;margin-top: 10px;">{{errorMessage}}</text>
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
				console.log(err)
				//debugger
				this.errorMessage = "注册失败，请稍后再试";
			},
			success: (res) => {
				//res是返回值
				//debugger
				console.log(res)	//可以通过 console.log(res) 查看完整的响应对象，帮助调试
				if(res.data === true){
					uni.showToast({
						title:"注册成功"
					})
				}else{
					this.errorMessage = res.data.message || "注册失败，请稍后再试";
				}
			}
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

.btn-register {
    margin-top: 20px;
    background-color: #007AFF;
    color: white;
}

.error-message {
    color: red;
    margin-top: 10px;
    font-size: 14px;
}
</style>