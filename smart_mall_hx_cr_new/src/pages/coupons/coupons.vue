<template>
	<view class="coupon-select-container">
		<view class="page-title">选择优惠券</view>
		<!-- 优惠券列表将在这里渲染 -->
		<view class="coupon-list">
			<view class="coupon-item" v-for="coupon in coupons" :key="coupon.id" @click="selectCoupon(coupon)">
				<!-- 优惠券具体样式待设计 -->
				<text>{{ coupon.name }} - (抵扣 ¥{{ coupon.discountAmount }})</text>
				<text v-if="selectedCouponId === coupon.id"> (已选)</text>
			</view>
			<view v-if="coupons.length === 0" class="empty-message">
				暂无可用优惠券
			</view>
		</view>

		<!-- 不使用优惠券的选项 -->
		<view class="no-coupon-option" @click="selectNoCoupon">
			<text>不使用优惠券</text>
			<text v-if="!selectedCouponId">(已选)</text>
		</view>

		<!-- 确定按钮 -->
		<!-- <button class="confirm-btn" @click="confirmSelection">确定</button> -->
		<!-- 点击列表项直接选择并返回，通常更方便 -->
	</view>
</template>

<script>
import { API_BASE_URL } from '@/config.js';

export default {
	data() {
		return {
			coupons: [], // 从订单确认页或接口获取的可用优惠券列表
			selectedCouponId: null, // 当前选中的优惠券ID
			totalAmount: 0 // 从订单确认页传递过来的总金额 (可选)
		};
	},
	onLoad(options) {
		// 1. 尝试从上个页面获取数据 (从 storage)
		const passedData = uni.getStorageSync('couponSelectionData');
		if(passedData) {
			console.log("从Storage获取到数据:", passedData);
			this.coupons = passedData.availableCoupons || [];
			this.selectedCouponId = passedData.selectedId || null;
			this.totalAmount = passedData.totalAmount || 0;
			// 获取后建议清除，避免下次误用，除非有特殊需求
			// uni.removeStorageSync('couponSelectionData');
		} else {
			console.warn("未能从Storage获取优惠券数据，将加载用户优惠券");
			// 如果没有传递数据，则加载用户的所有优惠券
			this.loadUserCoupons();
		}

		// --- 移除模拟数据 ---
		// console.log("优惠券选择页加载，接收到的 options:", options);
		// this.coupons = [
		// 	 { id: 1, name: '满100减10元', discountAmount: 10.00, threshold: 100 },
		// 	 { id: 2, name: '满50减5元', discountAmount: 5.00, threshold: 50 },
		// 	 { id: 3, name: '无门槛减2元', discountAmount: 2.00, threshold: 0 },
		// ];
		// this.selectedCouponId = options.selectedId ? parseInt(options.selectedId) : null;
	},
	methods: {
		// 加载用户的优惠券
		loadUserCoupons() {
			// 获取用户信息
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo || !userInfo.id) {
				console.warn('用户未登录或用户ID不存在');
				return;
			}

			// 显示加载中
			uni.showLoading({
				title: '加载中...'
			});

			// 调用后端接口获取用户的优惠券
			uni.request({
				url: `${API_BASE_URL}/api/coupons/user/${userInfo.id}`,
				method: 'GET',
				success: (res) => {
					if (res.data && res.data.success) {
						this.coupons = res.data.data || [];
						console.log('获取到用户优惠券:', this.coupons);
					} else {
						console.warn('获取用户优惠券失败:', res.data);
						// 如果获取失败，使用模拟数据
						this.useMockData();
					}
				},
				fail: (err) => {
					console.error('请求用户优惠券失败:', err);
					// 如果请求失败，使用模拟数据
					this.useMockData();
				},
				complete: () => {
					uni.hideLoading();
				}
			});
		},

		// 使用模拟数据
		useMockData() {
			console.log('使用模拟优惠券数据');
			this.coupons = [
				{ id: 1, name: '满100减10元', discountAmount: 10.00, threshold: 100 },
				{ id: 2, name: '满50减5元', discountAmount: 5.00, threshold: 50 },
				{ id: 3, name: '无门槛减2元', discountAmount: 2.00, threshold: 0 },
			];
		},

		selectCoupon(coupon) {
			console.log('选择了优惠券:', coupon);
			// 将选择结果保存到 storage，供上个页面 onShow 时读取
			uni.setStorageSync('selectedCoupon', coupon);
			// 返回上个页面
			uni.navigateBack();
		},

		selectNoCoupon() {
			console.log('选择不使用优惠券');
			// 保存一个特殊标记或 null 到 storage
			uni.setStorageSync('selectedCoupon', null); // 使用 null 表示不选择
			// 返回上个页面
			uni.navigateBack();
		},

		// // 如果需要确定按钮
		// confirmSelection() {
		// 	 let selected = null;
		// 	 if (this.selectedCouponId) {
		// 	 	 selected = this.coupons.find(c => c.id === this.selectedCouponId);
		// 	 }
		// 	 uni.setStorageSync('selectedCoupon', selected);
		// 	 uni.navigateBack();
		// }
	}
};
</script>

<style scoped>
.coupon-select-container {
	padding: 20rpx;
	background-color: #f8f8f8;
	min-height: 100vh;
}

.page-title {
	font-size: 32rpx;
	font-weight: bold;
	text-align: center;
	margin-bottom: 30rpx;
	color: #333;
}

.coupon-list {
	background-color: #fff;
	border-radius: 10rpx;
	margin-bottom: 30rpx;
	overflow: hidden; /* 防止内部元素溢出圆角 */
}

.coupon-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 20rpx;
	border-bottom: 1rpx solid #eee; /* 分隔线 */
	font-size: 28rpx;
	color: #333;
}

.coupon-item:last-child {
	border-bottom: none;
}

.empty-message {
	padding: 40rpx;
	text-align: center;
	color: #999;
	font-size: 28rpx;
}

.no-coupon-option {
	display: flex;
	justify-content: space-between;
	align-items: center;
	background-color: #fff;
	padding: 30rpx 20rpx;
	border-radius: 10rpx;
	font-size: 28rpx;
	color: #333;
}

/* 可以为已选状态添加特殊样式 */
</style>