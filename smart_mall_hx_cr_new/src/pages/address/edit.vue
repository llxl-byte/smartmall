<template>
	<view class="container">
		<view class="form-section">
			<!-- 收货人 -->
			<view class="form-item">
				<text class="label">收货人</text>
				<input class="input" v-model="addressForm.name" placeholder="请输入收货人姓名" />
			</view>

			<!-- 手机号 -->
			<view class="form-item">
				<text class="label">手机号</text>
				<input class="input" v-model="addressForm.phone" placeholder="请输入手机号" type="number" maxlength="11" />
			</view>

			<!-- 所在地区 -->
			<view class="form-item">
				<text class="label">所在地区</text>
				<view class="region-picker" @click="showRegionPicker">
					<text v-if="addressForm.province && addressForm.city && addressForm.district">
						{{ addressForm.province + addressForm.city + addressForm.district }}
					</text>
					<text v-else class="placeholder">请选择所在地区</text>
					<text class="arrow">></text>
				</view>
			</view>

			<!-- 详细地址 -->
			<view class="form-item">
				<text class="label">详细地址</text>
				<textarea class="textarea" v-model="addressForm.address" placeholder="请输入详细地址，如街道、门牌号、小区、楼栋号、单元室等" />
			</view>

			<!-- 设为默认地址 -->
			<view class="form-item switch-item">
				<text class="label">设为默认地址</text>
				<switch :checked="addressForm.isDefault === 1" @change="switchChange" color="#007AFF" />
			</view>
		</view>

		<!-- 保存按钮 -->
		<view class="btn-section">
			<button class="save-btn" @click="saveAddress">保存</button>
			<button class="delete-btn" @click="confirmDelete">删除该地址</button>
		</view>

		<!-- 地区选择器弹出层 -->
		<view class="region-popup-mask" v-if="isRegionPickerVisible" @click="cancelRegion"></view>
		<view class="region-popup" v-if="isRegionPickerVisible">
			<view class="region-header">
				<text class="cancel" @click="cancelRegion">取消</text>
				<text class="title">选择地区</text>
				<text class="confirm" @click="confirmRegion">确定</text>
			</view>
			<view class="region-tabs">
				<text
					class="tab"
					:class="{ active: regionTabIndex === 0 }"
					@click="switchRegionTab(0)"
				>{{ selectedProvince || '请选择' }}</text>
				<text
					class="tab"
					:class="{ active: regionTabIndex === 1 }"
					@click="switchRegionTab(1)"
					v-if="selectedProvince"
				>{{ selectedCity || '请选择' }}</text>
				<text
					class="tab"
					:class="{ active: regionTabIndex === 2 }"
					@click="switchRegionTab(2)"
					v-if="selectedCity"
				>{{ selectedDistrict || '请选择' }}</text>
			</view>
			<scroll-view class="region-list" scroll-y>
				<view
					class="region-item"
					v-for="(item, index) in currentRegionList"
					:key="index"
					@click="selectRegion(item)"
					:class="{ active: isRegionSelected(item) }"
				>
					{{ item }}
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			addressId: null,
			addressForm: {
				id: null,
				userId: null,
				name: '',
				phone: '',
				province: '',
				city: '',
				district: '',
				address: '',
				isDefault: 0
			},
			// 地区选择相关数据
			isRegionPickerVisible: false, // 是否显示地区选择器
			regionTabIndex: 0, // 当前选中的标签索引：0-省，1-市，2-区
			selectedProvince: '', // 已选择的省
			selectedCity: '', // 已选择的市
			selectedDistrict: '', // 已选择的区
			provinces: ['北京市', '天津市', '上海市', '重庆市', '河北省', '山西省', '辽宁省', '吉林省', '黑龙江省', '江苏省', '浙江省', '安徽省', '福建省', '江西省', '山东省', '河南省', '湖北省', '湖南省', '广东省', '海南省', '四川省', '贵州省', '云南省', '陕西省', '甘肃省', '青海省', '台湾省', '内蒙古自治区', '广西壮族自治区', '西藏自治区', '宁夏回族自治区', '新疆维吾尔自治区', '香港特别行政区', '澳门特别行政区'],
			cities: {
				'河南省': ['郑州市', '开封市', '洛阳市', '平顶山市', '安阳市', '鹤壁市', '新乡市', '焦作市', '濮阳市', '许昌市', '漯河市', '三门峡市', '南阳市', '商丘市', '信阳市', '周口市', '驻马店市', '济源市'],
				// 这里简化处理，只添加了河南省的城市，实际应用中应该包含所有省份的城市
			},
			districts: {
				'郑州市': ['中原区', '二七区', '管城回族区', '金水区', '上街区', '惠济区', '中牟县', '巩义市', '荥阳市', '新密市', '新郑市', '登封市'],
				// 这里简化处理，只添加了郑州市的区县，实际应用中应该包含所有城市的区县
			}
		}
	},
	computed: {
		// 当前显示的地区列表
		currentRegionList() {
			if (this.regionTabIndex === 0) {
				return this.provinces;
			} else if (this.regionTabIndex === 1) {
				return this.cities[this.selectedProvince] || [];
			} else {
				return this.districts[this.selectedCity] || [];
			}
		}
	},
	onLoad(options) {
		// 获取用户信息
		const userInfo = uni.getStorageSync('userInfo');
		if (!userInfo) {
			uni.showToast({
				title: '请先登录',
				icon: 'none',
				complete: () => {
					setTimeout(() => {
						uni.navigateTo({ url: '/pages/login/login' });
					}, 1500);
				}
			});
			return;
		}

		this.addressForm.userId = userInfo.id;

		// 获取地址ID
		if (options.id) {
			this.addressId = options.id;
			this.loadAddressDetail();
		} else {
			uni.showToast({
				title: '地址ID不存在',
				icon: 'none',
				complete: () => {
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				}
			});
		}
	},
	methods: {
		// 加载地址详情
		loadAddressDetail() {
			uni.showLoading({ title: '加载中...' });

			// 从地址列表中获取地址详情
			uni.request({
				url: `${API_BASE_URL}/address/list?userId=${this.addressForm.userId}`,
				method: 'GET',
				success: (res) => {
					if (res.data && res.data.success) {
						const addressList = res.data.data || [];
						const address = addressList.find(item => item.id == this.addressId);

						if (address) {
							this.addressForm = { ...address };

							// 设置已选择的地区
							this.selectedProvince = address.province;
							this.selectedCity = address.city;
							this.selectedDistrict = address.district;
						} else {
							uni.showToast({
								title: '地址不存在',
								icon: 'none',
								complete: () => {
									setTimeout(() => {
										uni.navigateBack();
									}, 1500);
								}
							});
						}
					} else {
						uni.showToast({
							title: res.data.message || '获取地址详情失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('获取地址详情失败', err);
					uni.showToast({
						title: '获取地址详情失败，请检查网络',
						icon: 'none'
					});
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},

		// 切换默认地址开关
		switchChange(e) {
			this.addressForm.isDefault = e.detail.value ? 1 : 0;
		},

		// 显示地区选择器
		showRegionPicker() {
			this.isRegionPickerVisible = true;
		},

		// 切换地区选择标签
		switchRegionTab(index) {
			this.regionTabIndex = index;
		},

		// 选择地区
		selectRegion(region) {
			if (this.regionTabIndex === 0) {
				this.selectedProvince = region;
				this.selectedCity = '';
				this.selectedDistrict = '';
				this.regionTabIndex = 1;
			} else if (this.regionTabIndex === 1) {
				this.selectedCity = region;
				this.selectedDistrict = '';
				this.regionTabIndex = 2;
			} else {
				this.selectedDistrict = region;
			}
		},

		// 判断地区是否被选中
		isRegionSelected(region) {
			if (this.regionTabIndex === 0) {
				return region === this.selectedProvince;
			} else if (this.regionTabIndex === 1) {
				return region === this.selectedCity;
			} else {
				return region === this.selectedDistrict;
			}
		},

		// 取消地区选择
		cancelRegion() {
			this.isRegionPickerVisible = false;
		},

		// 确认地区选择
		confirmRegion() {
			if (this.selectedProvince) {
				this.addressForm.province = this.selectedProvince;
			}

			if (this.selectedCity) {
				this.addressForm.city = this.selectedCity;
			}

			if (this.selectedDistrict) {
				this.addressForm.district = this.selectedDistrict;
			}

			this.isRegionPickerVisible = false;
		},

		// 保存地址
		saveAddress() {
			// 表单验证
			if (!this.addressForm.name) {
				uni.showToast({
					title: '请输入收货人姓名',
					icon: 'none'
				});
				return;
			}

			if (!this.addressForm.phone) {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}

			if (!/^1\d{10}$/.test(this.addressForm.phone)) {
				uni.showToast({
					title: '手机号格式不正确',
					icon: 'none'
				});
				return;
			}

			if (!this.addressForm.province || !this.addressForm.city || !this.addressForm.district) {
				uni.showToast({
					title: '请选择所在地区',
					icon: 'none'
				});
				return;
			}

			if (!this.addressForm.address) {
				uni.showToast({
					title: '请输入详细地址',
					icon: 'none'
				});
				return;
			}

			// 提交表单
			uni.showLoading({ title: '保存中...' });

			uni.request({
				url: `${API_BASE_URL}/address/update`,
				method: 'POST',
				data: this.addressForm,
				header: {
					'Content-Type': 'application/json'
				},
				success: (res) => {
					if (res.data && res.data.success) {
						uni.showToast({
							title: '更新成功',
							icon: 'success'
						});

						// 返回上一页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: res.data.message || '更新失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('更新地址失败', err);
					uni.showToast({
						title: '更新失败，请检查网络',
						icon: 'none'
					});
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},

		// 确认删除地址
		confirmDelete() {
			uni.showModal({
				title: '提示',
				content: '确定要删除该收货地址吗？',
				success: (res) => {
					if (res.confirm) {
						this.deleteAddress();
					}
				}
			});
		},

		// 删除地址
		deleteAddress() {
			uni.showLoading({ title: '删除中...' });

			uni.request({
				url: `${API_BASE_URL}/address/delete`,
				method: 'POST',
				data: {
					id: this.addressForm.id,
					userId: this.addressForm.userId
				},
				header: {
					'Content-Type': 'application/x-www-form-urlencoded'
				},
				success: (res) => {
					if (res.data && res.data.success) {
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						});

						// 返回上一页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: res.data.message || '删除失败',
							icon: 'none'
						});
					}
				},
				fail: (err) => {
					console.error('删除地址失败', err);
					uni.showToast({
						title: '删除失败，请检查网络',
						icon: 'none'
					});
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		}
	}
}
</script>

<style>
.container {
	padding-bottom: 30rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.form-section {
	background-color: #fff;
	margin-top: 20rpx;
	padding: 0 30rpx;
}

.form-item {
	padding: 30rpx 0;
	border-bottom: 1rpx solid #eee;
}

.form-item:last-child {
	border-bottom: none;
}

.label {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 20rpx;
	display: block;
}

.input, .textarea {
	width: 100%;
	font-size: 28rpx;
	color: #333;
}

.textarea {
	height: 150rpx;
	line-height: 1.5;
}

.region-picker {
	display: flex;
	justify-content: space-between;
	align-items: center;
	font-size: 28rpx;
	color: #333;
}

.placeholder {
	color: #999;
}

.arrow {
	color: #999;
	font-size: 24rpx;
}

.switch-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.switch-item .label {
	margin-bottom: 0;
}

.btn-section {
	padding: 30rpx;
}

.save-btn {
	background-color: #007AFF;
	color: #fff;
	border-radius: 45rpx;
	height: 90rpx;
	line-height: 90rpx;
	font-size: 32rpx;
	margin-bottom: 20rpx;
}

.delete-btn {
	background-color: #fff;
	color: #FF3B30;
	border: 1rpx solid #FF3B30;
	border-radius: 45rpx;
	height: 90rpx;
	line-height: 90rpx;
	font-size: 32rpx;
}

/* 地区选择器样式 */
.region-popup-mask {
	position: fixed;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	background-color: rgba(0, 0, 0, 0.5);
	z-index: 998;
}

.region-popup {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #fff;
	border-top-left-radius: 20rpx;
	border-top-right-radius: 20rpx;
	overflow: hidden;
	z-index: 999;
}

.region-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #eee;
}

.cancel {
	color: #999;
	font-size: 28rpx;
}

.title {
	font-size: 32rpx;
	font-weight: bold;
}

.confirm {
	color: #007AFF;
	font-size: 28rpx;
}

.region-tabs {
	display: flex;
	padding: 20rpx 30rpx;
	border-bottom: 1rpx solid #eee;
}

.tab {
	font-size: 28rpx;
	color: #666;
	margin-right: 30rpx;
	padding-bottom: 10rpx;
	position: relative;
}

.tab.active {
	color: #007AFF;
}

.tab.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	height: 4rpx;
	background-color: #007AFF;
}

.region-list {
	height: 600rpx;
	padding: 0 30rpx;
}

.region-item {
	padding: 30rpx 0;
	font-size: 28rpx;
	color: #333;
	border-bottom: 1rpx solid #eee;
}

.region-item.active {
	color: #007AFF;
}
</style>
