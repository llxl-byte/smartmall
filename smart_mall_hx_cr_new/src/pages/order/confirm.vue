<template>
	<view class="order-confirm-container">
		<!-- 收货地址 -->
		<view class="address-section" @click="selectAddress">
			<view class="address-empty" v-if="!addressInfo">
				<text class="iconfont">&#xe6a9;</text>
				<text class="text">请选择收货地址</text>
				<text class="iconfont">&#xe6a3;</text>
			</view>
			<view class="address-info" v-else>
				<view class="info-top">
					<text class="name">{{ addressInfo.name }}</text>
					<text class="mobile">{{ addressInfo.phone }}</text>
				</view>
				<view class="info-bottom">
					<text class="address">{{ addressInfo.province + addressInfo.city + addressInfo.district + addressInfo.address }}</text>
					<text class="iconfont">&#xe6a3;</text>
				</view>
			</view>
		</view>

		<!-- 商品列表 -->
		<view class="goods-section">
			<view class="goods-item" v-for="(item, index) in orderItems" :key="index">
				<image :src="item.image || '/static/default_item.png'" class="goods-img"></image>
				<view class="goods-info">
					<view class="goods-name">{{ item.name }}</view>
					<view class="goods-price-qty">
						<text class="goods-price">¥{{ item.price }}</text>
						<text class="goods-qty">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 优惠券 -->
		<!-- 点击跳转到优惠券选择页面 -->
		<view class="list-item coupon-section" @click="selectCoupon">
			<text class="label">优惠券</text>
			<view class="content">
				<!-- 根据是否有可用/已选优惠券显示不同文本 -->
				<text v-if="!selectedCoupon && (!availableCoupons || availableCoupons.length === 0)" class="placeholder gray">暂无可用优惠券</text>
				<text v-else-if="!selectedCoupon && availableCoupons.length > 0" class="placeholder">{{ availableCoupons.length }}张可用</text>
				<text v-else class="coupon-info price">-¥{{ couponDiscount.toFixed(2) }}</text>
			</view>
			<text class="iconfont arrow">&#xe6a3;</text> <!-- 右箭头图标 -->
		</view>

		<!-- 订单金额 -->
		<view class="amount-section">
			<view class="amount-item">
				<text>商品金额</text>
				<text>¥{{ totalAmount }}</text>
			</view>
			<view class="amount-item">
				<text>运费</text>
				<text>¥0.00</text>
			</view>
			<view class="amount-item" v-if="couponDiscount > 0">
				<text>优惠券抵扣</text>
				<text class="discount-amount price">-¥{{ couponDiscount.toFixed(2) }}</text>
			</view>
			<view class="amount-item total">
				<text>实付金额</text>
				<text class="total-price price">¥{{ finalAmount }}</text>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="action-bar">
			<view class="total-section">
				<text>实付金额：</text>
				<text class="total-price price">¥{{ finalAmount }}</text>
			</view>
			<view class="submit-btn" @click="submitOrder">提交订单</view>
			<!-- 添加一个调试按钮，在主按钮失败时可以尝试备用方法 -->
			<view v-if="orderSubmitFailed" class="alt-submit-btn" @click="submitOrderAlternative">重试</view>
		</view>
	</view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
		data() {
			return {
				itemId: null,
				quantity: 1,
				orderItems: [],
				addressInfo: null,
				totalAmount: '0.00',
				isLoading: true,
				couponDiscount: 0,
				selectedCoupon: null,
				availableCoupons: [],
				submitOrderAttempts: 0,
				orderSubmitFailed: false
			}
		},
		computed: {
			// 计算最终支付金额
			finalAmount() {
				const total = parseFloat(this.totalAmount) || 0;
				const discount = parseFloat(this.couponDiscount) || 0;
				// 确保结果至少为 0，并保留两位小数
				return Math.max(0, total - discount).toFixed(2);
			}
		},
		onLoad(options) {
			// 获取路由参数
			this.itemId = options.itemId;
			this.quantity = options.quantity || 1;

			// 获取本地存储的结算商品信息
			const checkoutItems = uni.getStorageSync('checkoutItems');
			
			if (checkoutItems && checkoutItems.length > 0) {
				// 如果是从购物车来的结算商品
				this.orderItems = checkoutItems;
				this.calculateTotal();
			} else if (this.itemId) {
				// 如果是直接购买单个商品
				this.loadItemInfo();
			}
			
			// 加载默认地址
			this.loadDefaultAddress();

			// 注意：此时可能还未完全计算好 totalAmount，需要等异步加载完成后调用
			// this.loadAvailableCoupons(); // 移到 calculateTotal 或 loadItemInfo/loadCartItems 成功回调后
		},
		// 添加 onShow 生命周期钩子，用于处理页面显示时的逻辑，特别是从其他页面返回时
		onShow() {
			// 检查是否从优惠券选择页面返回，并带来了选择结果
			// 例如，可以通过 uni.$once 监听全局事件，或检查特定的 storage
			const selectedCouponInfo = uni.getStorageSync('selectedCoupon'); // 假设选择结果存储在这里
			if (selectedCouponInfo) {
				this.selectedCoupon = selectedCouponInfo;
				// 根据优惠券信息计算抵扣金额，这里假设优惠券对象有 discountAmount 属性
				this.couponDiscount = selectedCouponInfo.discountAmount || 0;
				// 清除storage，避免下次进入页面时误用
				uni.removeStorageSync('selectedCoupon');
			}
			// else if (selectedCouponInfo === null) { // 可能表示用户清除了选择
			// 	this.selectedCoupon = null;
			// 	this.couponDiscount = 0;
			// 	uni.removeStorageSync('selectedCoupon');
			// }
		},
		methods: {
			// 加载单个商品信息
			loadItemInfo() {
				uni.showLoading({
					title: '加载商品信息...'
				});
				console.log('正在加载单个商品信息，itemId:', this.itemId);
				
				uni.request({
					url: `${API_BASE_URL}/selectByItemId?itemId=${this.itemId}`,
					success: (res) => {
						if (res.data) {
							const item = res.data;
							console.log('获取到商品信息:', item);
							
							this.orderItems = [{
								id: item.id,
								itemId: item.id, // 明确设置itemId字段
								name: item.name,
								price: item.price,
								image: item.mainImage || '/static/default_item.png',
								quantity: parseInt(this.quantity) || 1
							}];
							
							console.log('设置订单商品:', this.orderItems);
							this.calculateTotal();
						} else {
							console.error('商品信息返回为空');
							uni.showToast({
								title: '获取商品信息失败',
								icon: 'none'
							});
						}
					},
					fail: (err) => {
						console.error('获取商品信息失败', err);
						uni.showToast({
							title: '获取商品信息失败',
							icon: 'none'
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 加载购物车商品
			loadCartItems() {
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo) {
					uni.navigateTo({
						url: '/pages/login/login'
					});
					return;
				}

				uni.request({
					url: `${API_BASE_URL}/selectByUserId?userId=${userInfo.id}`,
					success: (res) => {
						if (res.data && res.data.length > 0) {
							// 需要获取每个商品的详细信息
							const promises = res.data.map(cart => {
								return new Promise((resolve, reject) => {
									uni.request({
										url: `${API_BASE_URL}/selectByItemId?itemId=${cart.itemId}`,
										success: (itemRes) => {
											if (itemRes.data) {
												resolve({
													id: itemRes.data.id,
													name: itemRes.data.name,
													price: itemRes.data.price,
													image: itemRes.data.mainImage,
													quantity: cart.quantity
												});
											} else {
												resolve(null);
											}
										},
										fail: () => {
											resolve(null);
										}
									});
								});
							});

							Promise.all(promises).then(items => {
								this.orderItems = items.filter(item => item !== null);
								this.calculateTotal();
							});
						}
					},
					fail: (err) => {
						console.error('获取购物车信息失败', err);
					}
				});
			},

			// 加载默认地址
			loadDefaultAddress() {
				// 这里应该从后端获取用户的默认地址
				// 由于没有地址管理功能，这里使用模拟数据
				this.addressInfo = {
					name: '张三',
					phone: '13800138000',
					province: '广东省',
					city: '深圳市',
					district: '南山区',
					address: '科技园1号楼'
				};
			},

			// 选择地址
			selectAddress() {
				// 这里应该跳转到地址选择页面
				// 由于没有地址管理功能，这里使用模拟数据
				uni.showToast({
					title: '地址管理功能待开发',
					icon: 'none'
				});
			},

			// 计算总金额，并触发加载可用优惠券
			calculateTotal() {
				let total = 0;
				this.orderItems.forEach(item => {
					total += (parseFloat(item.price) || 0) * (parseInt(item.quantity) || 0);
				});
				this.totalAmount = total.toFixed(2);
				// 当商品总额计算完成后，再去加载可用的优惠券
				this.loadAvailableCoupons();
			},

			// 加载当前订单可用的优惠券 - 改为调用后端接口
			loadAvailableCoupons() {
				this.isLoading = true;
				this.availableCoupons = []; // 清空旧数据
				this.selectedCoupon = null; // 重置选择
				this.couponDiscount = 0;

				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.id) {
					console.warn('用户未登录，无法加载优惠券');
					// 可以选择跳转登录页或静默失败
					this.isLoading = false;
					return;
				}

				if (this.orderItems.length === 0) {
					console.warn('订单中没有商品，不加载优惠券');
					this.isLoading = false;
					return;
				}

				const requestData = {
					userId: userInfo.id,
					itemIds: this.orderItems.map(item => item.id), // 提取商品 ID 列表
					totalAmount: parseFloat(this.totalAmount) // 确保传递数字类型
				};

				// 确保 API_BASE_URL 正确配置并可用
				// 例如，在 config.js 中定义 export const API_BASE_URL = 'http://127.0.0.1:8083';
				const apiUrl = getApiUrl('/api/coupons/available'); // 假设 getApiUrl 帮你拼接基础URL
				if (!apiUrl) {
					console.error('API 地址未配置');
					uni.showToast({ title: '网络配置错误', icon: 'none' });
					this.isLoading = false;
					return;
				}

				uni.request({
					url: apiUrl,
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: requestData,
					success: (res) => {
						console.log('获取可用优惠券响应:', res.data);
						if (res.data && res.data.success && Array.isArray(res.data.data)) {
							this.availableCoupons = res.data.data;
						} else {
							console.error('获取可用优惠券失败:', res.data.message || '返回数据格式错误');
							// 不提示用户，静默失败，认为没有可用优惠券
							this.availableCoupons = [];
						}
					},
					fail: (err) => {
						console.error('请求可用优惠券接口失败:', err);
						uni.showToast({
							title: '加载优惠券失败',
							icon: 'none'
						});
					},
					complete: () => {
						this.isLoading = false;
					}
				});
			},

			// 跳转到优惠券选择页面
			selectCoupon() {
				if (this.availableCoupons.length === 0 && !this.selectedCoupon) {
					uni.showToast({ title: '暂无可用优惠券', icon: 'none' });
					return;
				}
				// 跳转前将需要的数据存入 storage
				const couponSelectionData = {
					availableCoupons: this.availableCoupons,
					selectedId: this.selectedCoupon ? this.selectedCoupon.id : null,
					totalAmount: parseFloat(this.totalAmount) // 传递总金额供优惠券页参考
				};
				uni.setStorageSync('couponSelectionData', couponSelectionData);

				uni.navigateTo({
					url: '/pages/coupons/coupons'
				});
			},

			// 提交订单 - 使用备用方法
			submitOrderAlternative(userId, addressInfo, totalAmount, actualAmount) {
				console.log('尝试使用备用方法提交订单...');
				
				// 准备收货人信息（格式化为后端可能需要的格式）
				const receiver = {
					name: addressInfo.name,
					phone: addressInfo.phone,
					address: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`
				};
				
				// 特殊处理：检查orderItems是否有itemId
				if (this.orderItems.length > 0) {
					this.orderItems.forEach(item => {
						if (!item.itemId && item.id) {
							console.log(`商品 ${item.name} 没有itemId字段，使用id(${item.id})替代`);
							item.itemId = item.id;
						}
					});
				}
				
				// 准备商品列表
				const items = this.orderItems.map(item => ({
					itemId: item.itemId || item.id, // 优先使用itemId，如果不存在才使用id
					quantity: parseInt(item.quantity) || 1, // 确保数量是整数
					price: item.price, 
					name: item.name,
					image: item.image || '/static/default_item.png'
				}));
				
				console.log('处理后的商品列表:', items);
				
				// 准备订单数据
				const orderData = {
					userId: userId,
					addressInfo: JSON.stringify(receiver),
					totalAmount: totalAmount,
					actualAmount: actualAmount,
					items: items,
					status: 0, // 待付款
					// 增加字段区分商品来源
					fromCart: !!uni.getStorageSync('checkoutItems'), // 是否来自购物车
					directBuy: !!this.itemId // 是否是直接购买
				};
				
				console.log('备用方法 - 提交订单数据:', JSON.stringify(orderData));
				
				uni.showLoading({ title: '正在提交订单...' });
				
				// 使用 createFullOrder 接口
				uni.request({
					url: `${API_BASE_URL}/createFullOrder`,
					method: 'POST',
					header: {
						'content-type': 'application/json'
					},
					data: orderData,
					success: (res) => {
						console.log('完整订单创建响应:', res);
						
						// 更全面的成功判断
						const isSuccess = 
							(res.statusCode === 200) && (
								(res.data === true) || 
								(res.data && res.data.success) ||
								(res.data && res.data.data && (
									res.data.data.orderCreated || 
									res.data.data.directBuySuccess || 
									res.data.data.cartCheckoutSuccess
								))
							);
							
						if (isSuccess) {
							console.log('最终方法订单创建成功，处理成功逻辑');
							this.handleOrderSuccess();
						} else {
							// 显示详细的错误信息
							let errorMsg = '创建订单失败';
							if (res.data && res.data.message) {
								errorMsg = res.data.message;
							}
							
					uni.showToast({
								title: errorMsg,
								icon: 'none',
								duration: 2000
							});
							
							// 设置失败状态，允许用户重试
							this.orderSubmitFailed = true;
						}
					},
					fail: (err) => {
						console.error('完整订单创建失败:', err);
						uni.showToast({
							title: '无法创建订单，请联系客服',
						icon: 'none'
					});
						
						// 设置失败状态，允许用户重试
						this.orderSubmitFailed = true;
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 处理订单提交成功的通用方法
			handleOrderSuccess() {
				// 获取订单商品详情
				const orderDetails = this.orderItems.map(item => ({
					itemId: item.itemId || item.id, // 优先使用itemId，如果不存在才使用id
					quantity: parseInt(item.quantity) || 1, // 确保数量是整数
					price: item.price,
					name: item.name,
					image: item.image
				}));
				
				// 尝试添加订单详情
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo && userInfo.id) {
					this.tryAddOrderDetails(orderDetails, userInfo.id);
				}
				
				uni.showToast({
					title: '订单提交成功',
					icon: 'success'
				});

				// 根据购买方式处理购物车
				const isFromCart = !!uni.getStorageSync('checkoutItems');
				const isDirectBuy = !!this.itemId;
				
				console.log('订单提交成功，购买方式:', 
					isFromCart ? '购物车结算' : isDirectBuy ? '直接购买' : '未知方式');
				
				// 只有从购物车下单才需要清理购物车
				if (isFromCart) {
					this.clearCartItems();
				}
				
				// 清除相关本地存储
				uni.removeStorageSync('checkoutItems');
				
				// 跳转到订单成功页面
				setTimeout(() => {
					// 使用reLaunch而不是redirectTo，确保回到tab页面时会刷新
					uni.reLaunch({
						url: '/pages/index/index' // 跳转到首页
					});
				}, 1500);
			},

			// 尝试添加订单详情 (查询最近订单并添加详情)
			tryAddOrderDetails(orderDetails, userId) {
				if (!orderDetails || orderDetails.length === 0 || !userId) {
					console.warn('没有订单详情数据或用户ID无效，跳过添加订单详情步骤');
					return;
				}

				// 查询用户最近的订单
				uni.request({
					url: `${API_BASE_URL}/orders?userId=${userId}`,
					method: 'GET',
					success: (res) => {
						// 假设后端返回的订单列表是按时间倒序排列的
						if (res.statusCode === 200 && res.data && res.data.data && res.data.data.length > 0) {
							// 获取最新的订单ID
							const latestOrder = res.data.data[0];
							const orderId = latestOrder.id;
							
							console.log('获取到最新订单ID:', orderId);
							
							// 添加订单详情
							this.addOrderDetails(orderId, orderDetails);
						} else {
							console.error('获取最新订单失败:', res);
						}
					},
					fail: (err) => {
						console.error('查询订单失败:', err);
					}
				});
			},

			// 添加订单详情记录
			addOrderDetails(orderId, orderDetails) {
				if (!orderId || !orderDetails || orderDetails.length === 0) {
					console.warn('订单ID或详情数据无效，无法添加订单详情');
					return;
				}
				
				// 对每个商品详情发起请求
				orderDetails.forEach((detail, index) => {
					// 构造订单详情数据
					const detailData = {
						orderId: orderId,
						itemId: detail.itemId,
						quantity: detail.quantity,
						price: detail.price,
						totalPrice: (detail.price * detail.quantity).toFixed(2),
						itemName: detail.name,
						itemImage: detail.image
					};
					
					// 发送添加订单详情的请求
					uni.request({
						url: `${API_BASE_URL}/addOrderDetail`,
						method: 'POST',
						header: {
							'content-type': 'application/x-www-form-urlencoded'
						},
						data: detailData,
						success: (res) => {
							console.log(`添加订单详情 ${index+1}/${orderDetails.length} 结果:`, res.data);
						},
						fail: (err) => {
							console.error(`添加订单详情 ${index+1}/${orderDetails.length} 失败:`, err);
						}
					});
				});
			},

			// 处理订单提交错误的通用方法
			handleOrderError(res) {
					uni.showToast({
					title: res.data && res.data.message ? res.data.message : '订单提交失败',
					icon: 'none',
					duration: 2000
				});
			},

			// 添加清除购物车方法
			clearCartItems() {
				// 获取当前结算的商品ID列表
				const itemIds = this.orderItems.map(item => item.id);
				if (!itemIds || itemIds.length === 0) {
					console.log('没有需要从购物车中清除的商品');
					return;
				}

				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.id) {
					console.log('用户未登录，无法清除购物车');
					return;
				}
				
				console.log('清除购物车中的商品:', itemIds);
				
				// 从本地存储中获取购物车项的原始ID
				const checkoutItems = uni.getStorageSync('checkoutItems');
				if (!checkoutItems || checkoutItems.length === 0) {
					console.log('本地没有购物车数据，无法清除');
					return;
				}
				
				// 提取购物车项ID
				const cartItemIds = checkoutItems.map(item => item.id);
				
				// 调用后端接口批量删除购物车项
				uni.request({
					url: `${API_BASE_URL}/batchDeleteCart`,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: {
						ids: cartItemIds.join(',') // 将ID数组转换为逗号分隔的字符串
					},
					success: (res) => {
						console.log('清除购物车响应:', res.data);
						if (res.data === true) {
							console.log('购物车清除成功');
						} else {
							console.error('购物车清除失败');
							// 如果批量删除接口不存在或失败，尝试逐个删除
							this.deleteCartItemsOneByOne(cartItemIds);
						}
					},
					fail: (err) => {
						console.error('清除购物车请求失败:', err);
						// 尝试逐个删除
						this.deleteCartItemsOneByOne(cartItemIds);
					}
				});
			},

			// 逐个删除购物车项（备用方法）
			deleteCartItemsOneByOne(cartItemIds) {
				if (!cartItemIds || cartItemIds.length === 0) {
					return;
				}

				cartItemIds.forEach(id => {
					uni.request({
						url: `${API_BASE_URL}/deleteCart?id=${id}`,
						success: (res) => {
							console.log(`删除购物车项 ${id} 结果:`, res.data);
						},
						fail: (err) => {
							console.error(`删除购物车项 ${id} 失败:`, err);
						}
					});
				});
			},

			// 提交订单 - 主方法，尝试多种提交方式
			submitOrder() {
				if (!this.addressInfo) {
					uni.showToast({ title: '请选择收货地址', icon: 'none' });
					return;
				}

				if (this.orderItems.length === 0) {
					uni.showToast({ title: '没有可购买的商品', icon: 'none' });
					return;
				}

				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.id) {
					uni.navigateTo({ url: '/pages/login/login' });
					return;
				}

				// 计算订单总金额用于日志记录
				const totalAmount = parseFloat(this.totalAmount) || 0;
				const actualAmount = parseFloat(this.finalAmount) || 0;

				// 首先尝试使用createFullOrder接口
				this.submitOrderAlternative(userInfo.id, this.addressInfo, totalAmount, actualAmount);
			},

			// 方法1：提交订单 - 使用基本的参数
			submitOrderMethod1(userId, addressInfo, totalAmount, actualAmount) {
				// 准备商品列表数据
				const itemsList = this.orderItems.map(item => ({
					itemId: item.itemId || item.id, // 优先使用itemId，如果不存在才使用id
					quantity: parseInt(item.quantity) || 1, // 确保数量是整数
					price: item.price,
					name: item.name
				}));
				
				// 准备完整请求数据
				const requestData = {
					userId: userId,
					addressInfo: JSON.stringify({
						name: addressInfo.name,
						phone: addressInfo.phone,
						address: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`
					}),
					itemsList: JSON.stringify(itemsList) // 添加商品列表
				};

				console.log('方法1 - 提交订单数据:', JSON.stringify(requestData));
				const apiUrl = `${API_BASE_URL}/addMallOrder`;

				uni.showLoading({ title: '正在提交...' });

				uni.request({
					url: apiUrl,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: requestData,
					success: (res) => {
						// 输出完整响应，包括状态和头信息
						console.log('提交订单完整响应:', {
							statusCode: res.statusCode,
							header: res.header,
							data: typeof res.data === 'object' ? JSON.stringify(res.data) : res.data
						});
						
						if (res.statusCode === 200 && res.data === true) {
							this.handleOrderSuccess();
						} else {
							console.error('订单提交失败，HTTP状态:', res.statusCode, '响应:', res.data);
							this.handleOrderError(res);
							// 如果方法1失败，尝试方法2
							this.submitOrderMethod2(userId, addressInfo, totalAmount, actualAmount);
						}
					},
					fail: (err) => {
						console.error('提交订单请求失败', err);
						uni.showToast({ title: '网络错误，请稍后重试', icon: 'none' });
						this.orderSubmitFailed = true;
						
						// 如果方法1失败，尝试方法2
						this.submitOrderMethod2(userId, addressInfo, totalAmount, actualAmount);
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 方法2：提交订单 - 显式提供receiverName等字段
			submitOrderMethod2(userId, addressInfo, totalAmount, actualAmount) {
				// 准备数据 - 包含receiverName等字段
				const requestData = {
					userId: userId,
					// 提供单独的接收者字段
					receiverName: addressInfo.name,
					receiverPhone: addressInfo.phone,
					receiverAddress: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`,
					// 同时也提供addressInfo字段
					addressInfo: JSON.stringify({
						name: addressInfo.name,
						phone: addressInfo.phone,
						address: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`
					}),
					// 提供金额信息
					totalAmount: totalAmount,
					actualAmount: actualAmount,
					// 状态信息
					status: 0 // 待付款状态
				};
				
				console.log('方法2 - 提交订单数据:', JSON.stringify(requestData));
				const apiUrl = `${API_BASE_URL}/addMallOrder`;
				
				uni.showLoading({ title: '继续尝试中...' });
				
				uni.request({
					url: apiUrl,
					method: 'POST',
					header: {
						'content-type': 'application/x-www-form-urlencoded'
					},
					data: requestData,
					success: (res) => {
						console.log('方法2提交订单响应:', res);
						if (res.statusCode === 200 && res.data === true) {
							this.handleOrderSuccess();
						} else {
							this.handleOrderError(res);
							// 尝试备用方法
							this.submitOrderAlternative(userId, addressInfo, totalAmount, actualAmount);
						}
					},
					fail: (err) => {
						console.error('方法2提交订单失败', err);
						// 尝试备用方法
						this.submitOrderAlternative(userId, addressInfo, totalAmount, actualAmount);
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			},

			// 最终备用方法：尝试直接发送完整的订单信息到后端
			submitOrderFinal(userId, addressInfo, totalAmount, actualAmount) {
				// 生成订单号 - 日期格式+随机数
				const generateOrderNo = () => {
					const date = new Date();
					const timestamp = date.getFullYear().toString().substr(2) + 
									 ('0' + (date.getMonth() + 1)).slice(-2) + 
									 ('0' + date.getDate()).slice(-2) + 
									 ('0' + date.getHours()).slice(-2) + 
									 ('0' + date.getMinutes()).slice(-2) + 
									 ('0' + date.getSeconds()).slice(-2);
					const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
					return timestamp + random;
				};
				
				// 构建完整订单对象
				const completeOrder = {
					// 订单主信息
					orderNo: generateOrderNo(),
					userId: userId,
					totalAmount: totalAmount.toFixed(2),
					actualAmount: actualAmount.toFixed(2),
					status: 0, // 待付款
					addressInfo: JSON.stringify({
						name: addressInfo.name,
						phone: addressInfo.phone,
						address: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`
					}),
					createTime: new Date().toISOString(),
					
					// 收货信息
					receiverName: addressInfo.name,
					receiverPhone: addressInfo.phone,
					receiverAddress: `${addressInfo.province}${addressInfo.city}${addressInfo.district}${addressInfo.address}`,
					
					// 优惠券
					couponId: this.selectedCoupon ? this.selectedCoupon.id : null,
					
					// 增加标识
					directBuy: !!this.itemId,
					fromCart: !!uni.getStorageSync('checkoutItems'),
					
					// 订单项
					orderItems: this.orderItems.map(item => ({
						itemId: item.itemId || item.id,
						quantity: parseInt(item.quantity) || 1,
						price: parseFloat(item.price),
						name: item.name,
						image: item.image
					})),
				};
				
				console.log('最终备用方法 - 完整订单数据:', JSON.stringify(completeOrder));
				
				// 尝试发送到一个可能存在的完整订单创建接口
				const apiUrl = `${API_BASE_URL}/createFullOrder`;
				
				uni.showLoading({ title: '最后尝试...' });
				
				uni.request({
					url: apiUrl,
					method: 'POST',
					header: {
						'content-type': 'application/json'
					},
					data: completeOrder,
					success: (res) => {
						console.log('完整订单创建响应:', res);
						
						// 更全面的成功判断
						const isSuccess = 
							(res.statusCode === 200) && (
								(res.data === true) || 
								(res.data && res.data.success) ||
								(res.data && res.data.data && (
									res.data.data.orderCreated || 
									res.data.data.directBuySuccess || 
									res.data.data.cartCheckoutSuccess
								))
							);
							
						if (isSuccess) {
							console.log('最终方法订单创建成功，处理成功逻辑');
							this.handleOrderSuccess();
						} else {
							// 显示详细的错误信息
							let errorMsg = '创建订单失败';
							if (res.data && res.data.message) {
								errorMsg = res.data.message;
							}
							
							uni.showToast({
								title: errorMsg,
								icon: 'none',
								duration: 2000
							});
							
							// 设置失败状态，允许用户重试
							this.orderSubmitFailed = true;
						}
					},
					fail: (err) => {
						console.error('完整订单创建失败:', err);
						uni.showToast({
							title: '无法创建订单，请联系客服',
							icon: 'none'
						});
						
						// 设置失败状态，允许用户重试
						this.orderSubmitFailed = true;
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
	.order-confirm-container {
		padding-bottom: 100rpx; /* 为底部操作栏留出空间 */
		background-color: #f5f5f5;
	}

	.address-section {
		background-color: #fff;
		padding: 30rpx;
		margin-bottom: 20rpx;
	}

	.address-empty {
		display: flex;
		align-items: center;
		color: #666;
	}

	.address-empty .text {
		flex: 1;
		margin: 0 20rpx;
	}

	.address-info {
		color: #333;
	}

	.info-top {
		display: flex;
		align-items: center;
		margin-bottom: 10rpx;
	}

	.name {
		margin-right: 20rpx;
		font-weight: bold;
	}

	.info-bottom {
		display: flex;
		align-items: center;
	}

	.address {
		flex: 1;
		margin-right: 20rpx;
	}

	.goods-section {
		background-color: #fff;
		padding: 20rpx;
		margin-bottom: 20rpx;
	}

	.goods-item {
		display: flex;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #eee;
	}

	.goods-item:last-child {
		border-bottom: none;
	}

	.goods-img {
		width: 160rpx;
		height: 160rpx;
		margin-right: 20rpx;
	}

	.goods-info {
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.goods-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 10rpx;
	}

	.goods-price-qty {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.goods-price {
		color: #ff6700;
		font-size: 32rpx;
	}

	.goods-qty {
		color: #999;
		font-size: 28rpx;
	}

	.amount-section {
		background-color: #fff;
		padding: 20rpx;
	}

	.amount-item {
		display: flex;
		justify-content: space-between;
		padding: 20rpx 0;
		font-size: 28rpx;
		color: #666;
	}

	.total {
		border-top: 1rpx solid #eee;
		padding-top: 20rpx;
		font-weight: bold;
		color: #333;
	}

	.total-price {
		color: #ff6700;
		font-size: 32rpx;
	}

	.action-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		border-top: 1rpx solid #eee;
	}

	.total-section {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: flex-end;
		padding-right: 20rpx;
	}

	.submit-btn {
		width: 240rpx;
		height: 100rpx;
		line-height: 100rpx;
		text-align: center;
		background-color: #ff6700;
		color: #fff;
		font-size: 32rpx;
	}

	/* 优惠券区域通用列表项样式 */
	.list-item {
		display: flex;
		align-items: center;
		background-color: #fff;
		padding: 25rpx 30rpx;
		margin-bottom: 20rpx;
		font-size: 28rpx;
		color: #333;
	}
	.list-item .label {
		width: 150rpx; /* 固定标签宽度 */
		color: #666;
	}
	.list-item .content {
		flex: 1;
		text-align: right;
	}
	.list-item .arrow {
		font-size: 24rpx;
		color: #ccc;
		margin-left: 10rpx;
	}

	.coupon-section .placeholder {
		color: #999;
	}
	.coupon-section .placeholder.gray {
		color: #bbb;
	}
	.coupon-section .coupon-info {
		/* 可以在这里给已选优惠券名称添加样式 */
	}
	.price {
		color: #ff6700; /* 价格统一用醒目颜色 */
		font-weight: bold;
	}
	.discount-amount {
		/* 抵扣金额样式 */
	}

	.alt-submit-btn {
		width: 100rpx;
		height: 60rpx;
		line-height: 60rpx;
		text-align: center;
		background-color: #ff9900;
		color: #fff;
		font-size: 26rpx;
		border-radius: 30rpx;
		margin-left: 20rpx;
	}
</style>
