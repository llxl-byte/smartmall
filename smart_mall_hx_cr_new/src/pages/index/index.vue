<template>
	<view class="content">
		<!-- <image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view> -->
		<view>
			<text v-for="category in categoryList" v-on:click="categoryClick(parent.id)">
				{{ category.name }}
			</text>>
		</view>
		<view v-for="item in itemList">
			<image v-bind:src="item.image"></image>
			<text>{{item.itemName}}</text>
			<text>{{item.price}}</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// title: 'Hello',
				/* categoryList: [
					{id:1,name:"手机"},
					{id:2,name:"电脑"},
					{id:3,name:"图书"},
					{id:4,name:"衣服"}
					
				], */
				categoryList:[],
				itemList:[{
					Image:"/static/mobile.jpg",
					itemName:"手机",
					price:5000
				},
				{
					Image:"/static/computer.jpg",
					itemName:"电脑",
					price:10000
				}]
			}
		},
		onLoad() {

		},
		mounted() {
			//网页加载完获取手机商品，1是手机分类的编号
			this.getItem(1);//0表示顶级分类
			let serverUrl = "http://127.0.0.1:8083/selectAll";
			uni.request({
				url:serverUrl,
				success: (res) => {
					//更变了this.categoryList的值，界面会自动更新
					this.categoryList = res.data
				},
				fail: (err) => {
					console.log(err)
				}
				
			})
			
		},
		methods: {
			categoryClick(id){
				this.getItem(id)
			},
			getItem(parentId){
				let serverUrl="http://127.0.0.1:8083/selectByParentId?parentId="+parentId;
				uni.request({
					url:serverUrl,
					success: (res) => {
						this.itemList = res.data
					}
				})
			}
			
		}
	}
</script>

<style>
	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.logo {
		height: 200rpx;
		width: 200rpx;
		margin-top: 200rpx;
		margin-left: auto;
		margin-right: auto;
		margin-bottom: 50rpx;
	}

	.text-area {
		display: flex;
		justify-content: center;
	}

	.title {
		font-size: 36rpx;
		color: #8f8f94;
	}
</style>