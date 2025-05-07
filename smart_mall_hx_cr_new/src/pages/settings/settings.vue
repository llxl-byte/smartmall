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
				this.baseUrl = `https://${domain.replace('-8080', '-8083')}`;
			} else {
				this.baseUrl = 'http://127.0.0.1:8083';
			}
		},
		methods: {
			loadUserInfo() {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					this.userInfo = {...userInfo};
				}
			},
			chooseImage() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0];
						
						// 上传图片到服务器
						uni.uploadFile({
							url: `${this.baseUrl}/uploadAvatar`,
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
				uni.request({
					url: `${this.baseUrl}/updateUserInfo`,
					method: 'POST',
					data: this.userInfo,
					success: (res) => {
						if (res.data.success) {
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
					fail: () => {
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