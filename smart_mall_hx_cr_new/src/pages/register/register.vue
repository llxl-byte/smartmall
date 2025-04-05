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
		errorMessage: ""
	}
},
methods: {
	register(){
		//重置错误信息
		this.errorMessage = "";
		//用户名和密码不能为空
		if(!this.username || !this.password){
			this.errorMessage = "用户名和密码不能为空";
			return;
		}
		//
		if(this.password.length < 6){
			this.errorMessage = "密码长度不能小于6位";
			return;
		}
		//确认密码验证
		if(this.password !== this.confirmPassword){
			this.errorMessage = "两次输入的密码不一致";
			return;
		}
		//1，得到用户输入的用户名和密码
		console.log(this.username)
		//2,把数据发给服务器
		let serverUrl="http://127.0.0.1:8083/mallUserRegister?password="+encodeURIComponent(this.password)+"&username="+encodeURIComponent(this.username)
		uni.request({
			url:serverUrl,
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