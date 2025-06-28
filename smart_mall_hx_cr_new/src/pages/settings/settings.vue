<template>
	<view class="container">
		<view class="settings-section">
			<view class="avatar-section">
				<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" @click="chooseImage"></image>
				<text class="tip">点击更换头像</text>
			</view>
			
			<view class="info-section">
				<view class="info-item">
					<text class="label">用户名</text>
					<text class="value">{{userInfo.username}}</text>
				</view>
				<view class="info-item">
					<text class="label">昵称</text>
					<input class="value-input" v-model="userInfo.nickname" placeholder="设置昵称"/>
				</view>
				<view class="info-item">
					<text class="label">手机号</text>
					<input class="value-input" v-model="userInfo.phone" placeholder="设置手机号"/>
				</view>
			</view>
			
			<button class="save-btn" @click="saveSettings">保存修改</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: {},
				baseUrl: ''
			}
		},
		onShow() {
			this.loadUserInfo();
			// 设置baseUrl
			const currentUrl = window.location.href;
			if (currentUrl.includes('.app.github.dev')) {
				const domain = window.location.hostname;
				// 确保使用HTTP协议
				this.baseUrl = `http://${domain.replace('-8080', '-8083')}`;
			} else {
				this.baseUrl = 'http://127.0.0.1:8083';
			}
			
			console.log('API Base URL:', this.baseUrl);
		},
		methods: {
			loadUserInfo() {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					this.userInfo = {...userInfo};
				}
			},
			chooseImage() {
				// 检查是否有用户ID
				if (!this.userInfo.id || this.userInfo.id === '') {
					uni.showToast({
						title: '请先保存基本信息',
						icon: 'none'
					});
					return;
				}
				
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0];
						
						// 确保URL使用HTTP协议
						let apiUrl = this.baseUrl;
						if (apiUrl.startsWith('https:')) {
							apiUrl = apiUrl.replace('https:', 'http:');
						}
						
						// 上传图片到服务器
						uni.uploadFile({
							url: `${apiUrl}/uploadAvatar`,
							filePath: tempFilePath,
							name: 'file',
							formData: {
								userId: this.userInfo.id
							},
							success: (uploadRes) => {
								const result = JSON.parse(uploadRes.data);
								if (result.success) {
									// 更新本地头像URL
									this.userInfo.avatar = result.data;
									// 更新Storage中的用户信息
									uni.setStorageSync('userInfo', this.userInfo);
									
									uni.showToast({
										title: '头像更新成功',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: '头像上传失败',
										icon: 'none'
									});
								}
							},
							fail: () => {
								uni.showToast({
									title: '上传失败，请重试',
									icon: 'none'
								});
							}
						});
					}
				});
			},
			saveSettings() {
				// 检查是否有用户ID，如果没有，先尝试注册/获取用户ID
				if (!this.userInfo.id || this.userInfo.id === '') {
					// 提示用户
					uni.showModal({
						title: '完善信息',
						content: '您需要先完成注册才能保存个人信息，是否继续？',
						success: (res) => {
							if (res.confirm) {
								// 用户确认，进行注册流程
								this.registerAndSaveInfo();
							}
						}
					});
					return;
				}
				
				// 已有ID，正常更新信息
				this.updateUserInfo();
			},
			// 注册并保存信息的方法
			registerAndSaveInfo() {
				// 确保URL使用HTTP协议
				let apiUrl = this.baseUrl;
				if (apiUrl.startsWith('https:')) {
					apiUrl = apiUrl.replace('https:', 'http:');
				}

				// 先进行用户注册
				uni.request({
					url: `${apiUrl}/mallUserRegister`,
					method: 'POST',
					data: {
						username: this.userInfo.username,
						password: '123456', // 默认密码，实际应用中应当要求用户设置
						nickname: this.userInfo.nickname || this.userInfo.username,
						phone: this.userInfo.phone || ''
					},
					success: (registerRes) => {
						console.log('Register response:', registerRes);
						
						if (registerRes.data === true || (registerRes.data && registerRes.data.success)) {
							// 注册成功，现在尝试登录获取用户ID
							this.loginAndGetUserId();
						} else {
							// 用户可能已存在，尝试直接登录
							this.loginAndGetUserId();
						}
					},
					fail: (err) => {
						console.error('Register failed:', err);
						uni.showToast({
							title: '注册失败，请重试',
							icon: 'none'
						});
					}
				});
			},
			// 登录并获取用户ID
			loginAndGetUserId() {
				// 确保URL使用HTTP协议
				let apiUrl = this.baseUrl;
				if (apiUrl.startsWith('https:')) {
					apiUrl = apiUrl.replace('https:', 'http:');
				}
				
				uni.request({
					url: `${apiUrl}/mallUserLogin`,
					method: 'GET',
					data: {
						username: this.userInfo.username,
						password: '123456' // 默认密码，与注册时使用相同的密码
					},
					success: (loginRes) => {
						console.log('Login response:', loginRes);
						
						if (loginRes.data && loginRes.data.success) {
							// 登录成功，获取用户ID
							const responseData = loginRes.data.data;
							let userId = '';
							let token = '';
							
							if (responseData.userInfo) {
								// 新格式
								userId = responseData.userInfo.id;
								token = responseData.token || '';
							} else if (responseData.id) {
								// 直接是用户对象
								userId = responseData.id;
							}
							
							if (userId) {
								// 更新当前用户信息
								this.userInfo.id = userId;
								if (token) this.userInfo.token = token;
								
								// 保存到存储
								uni.setStorageSync('userInfo', this.userInfo);
								
								// 继续更新用户信息
								this.updateUserInfo();
							} else {
								uni.showToast({
									title: '获取用户ID失败',
									icon: 'none'
								});
							}
						} else {
							uni.showToast({
								title: '登录失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('Login failed:', err);
						uni.showToast({
							title: '登录失败，请重试',
							icon: 'none'
						});
					}
				});
			},
			// 更新用户信息
			updateUserInfo() {
				// 确保URL使用HTTP协议
				let apiUrl = this.baseUrl;
				if (apiUrl.startsWith('https:')) {
					apiUrl = apiUrl.replace('https:', 'http:');
				}
				
				uni.request({
					url: `${apiUrl}/updateUserInfo`,
					method: 'POST',
					data: this.userInfo,
					success: (res) => {
						if (res.data && res.data.success) {
							// 更新Storage中的用户信息
							uni.setStorageSync('userInfo', this.userInfo);
							
							uni.showToast({
								title: '保存成功',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: '保存失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('Update user info failed:', err);
						uni.showToast({
							title: '网络错误，请重试',
							icon: 'none'
						});
					}
				});
			}
		}
	}
</script>

<style>
.container {
	padding: 20px;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.settings-section {
	background-color: #fff;
	border-radius: 8px;
	padding: 20px;
}

.avatar-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 30px;
}

.avatar {
	width: 100px;
	height: 100px;
	border-radius: 50%;
	margin-bottom: 10px;
	border: 2px solid #007AFF;
}

.tip {
	font-size: 14px;
	color: #666;
}

.info-section {
	margin-bottom: 30px;
}

.info-item {
	display: flex;
	align-items: center;
	padding: 15px 0;
	border-bottom: 1px solid #eee;
}

.label {
	width: 80px;
	color: #333;
	font-size: 14px;
}

.value {
	flex: 1;
	color: #666;
	font-size: 14px;
}

.value-input {
	flex: 1;
	font-size: 14px;
	color: #333;
	padding: 0 10px;
}

.save-btn {
	background-color: #007AFF;
	color: white;
	border-radius: 4px;
	padding: 10px;
	width: 100%;
}
</style>