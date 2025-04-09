<template>
	<view class="success-container">
		<view class="success-icon">
			<image src="/static/success.png" mode="aspectFit"></image>
		</view>
		<view class="success-title">订单提交成功</view>
		<view class="success-tips">我们将尽快为您发货</view>
		
		<view class="action-buttons">
			<button class="btn view-order" @click="viewOrder">查看订单</button>
			<button class="btn continue-shopping" @click="continueShopping">继续购物</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		methods: {
			// 查看订单
			viewOrder() {
				// 跳转到订单列表页面
				uni.showToast({
					title: '订单列表功能待开发',
					icon: 'none'
				});
			},
			
			// 继续购物
			continueShopping() {
				// 跳转到首页
				uni.switchTab({
					url: '/pages/index/index'
				});
			}
		}
	}
</script>

<style>
	.success-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 100rpx 30rpx;
	}
	
	.success-icon {
		margin-bottom: 40rpx;
	}
	
	.success-icon image {
		width: 160rpx;
		height: 160rpx;
	}
	
	.success-title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.success-tips {
		font-size: 28rpx;
		color: #999;
		margin-bottom: 60rpx;
	}
	
	.action-buttons {
		width: 100%;
		display: flex;
		justify-content: space-between;
	}
	
	.btn {
		width: 45%;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		border-radius: 40rpx;
		font-size: 28rpx;
	}
	
	.view-order {
		background-color: #fff;
		color: #ff6700;
		border: 1rpx solid #ff6700;
	}
	
	.continue-shopping {
		background-color: #ff6700;
		color: #fff;
	}
</style>
