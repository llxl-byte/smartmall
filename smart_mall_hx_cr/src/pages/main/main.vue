<template>
	<view>
		<!--支持富文本-->
		<text>京淘商城</text>	
	</view>
</template>

<script>
	export default {
		data() {
			return {
				
			}
		},
		mounted(){
			console.log("网页加载完后运行")
			let time=3000//3秒钟
			setTimeout(function(){
				//3秒后跳转到index.vue
				uni.switchTab({
					url:"/pages/index/index"
				})
			},time)
		},
		methods: {
			
		}
	}
</script>

<style>

</style>
