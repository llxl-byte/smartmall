<template>
    <view class="cart-container">
        <!-- 空购物车提示 -->
        <view class="empty-cart" v-if="cartItems.length === 0">
            <image src="/static/empty_cart.png" mode="aspectFit" class="empty-image"></image>
            <text class="empty-text">购物车还是空的</text>
            <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
        </view>

        <!-- 购物车列表 -->
        <view class="cart-content" v-else>
            <!-- 购物车商品列表 -->
            <view class="cart-list">
                <view class="cart-item" v-for="(item, index) in cartItems" :key="index">
                    <!-- 选择框 -->
                    <view class="item-select" @click="toggleSelect(item)">
                        <view class="select-icon" :class="{ selected: item.selected }">
                            <text v-if="item.selected" class="select-icon-inner">✓</text>
                        </view>
                    </view>

                    <!-- 商品图片 -->
                    <image :src="item.image" class="item-image"></image>

                    <!-- 商品信息 -->
                    <view class="item-info">
                        <text class="item-name">{{ item.name }}</text>
                        <view class="item-price-qty">
                            <text class="item-price">¥{{ item.price }}</text>
                            <view class="qty-control">
                                <text class="qty-btn" @click="decreaseQuantity(item)">-</text>
                                <input type="number" v-model="item.quantity" class="qty-input" @blur="updateQuantity(item)" />
                                <text class="qty-btn" @click="increaseQuantity(item)">+</text>
                            </view>
                        </view>
                    </view>

                    <!-- 删除按钮 -->
                    <view class="delete-btn" @click="deleteItem(item)">×</view>
                </view>
            </view>

            <!-- 底部结算栏 -->
            <view class="cart-footer">
                <view class="select-all" @click="toggleSelectAll">
                    <view class="select-icon" :class="{ selected: isAllSelected }">
                        <text v-if="isAllSelected" class="select-icon-inner">✓</text>
                    </view>
                    <text class="select-text">全选</text>
                </view>

                <view class="total-info">
                    <text>合计：</text>
                    <text class="total-price">¥{{ totalPrice }}</text>
                </view>

                <button class="checkout-btn" :disabled="selectedCount === 0" @click="checkout">
                    {{ selectedCount === 0 ? '请选择商品' : `结算(${selectedCount})` }}
                    <text v-if="selectedCount > 0" class="total-price">¥{{ totalPrice }}</text>
                </button>
            </view>
        </view>
    </view>
</template>

<script>
import { API_BASE_URL, getApiUrl } from '@/config.js';

export default {
        data() {
            return {
                cartItems: [],
                isAllSelected: false,
                isLoading: false
            }
        },
        computed: {
            // 选中的商品数量
            selectedCount() {
                return this.cartItems.filter(item => item.selected).length;
            },
            // 选中商品的总价
            totalPrice() {
                let total = 0;
                this.cartItems.forEach(item => {
                    if (item.selected) {
                        total += item.price * item.quantity;
                    }
                });
                return total.toFixed(2);
            }
        },
        onShow() {
            // 每次显示页面时加载购物车数据
            this.loadCartItems();
        },
        onPullDownRefresh() {
            // 下拉刷新
            this.loadCartItems();
            uni.stopPullDownRefresh();
        },
        methods: {
            // 加载购物车数据
            loadCartItems() {
                const userInfo = uni.getStorageSync('userInfo');
                if (!userInfo) {
                    return;
                }

                this.isLoading = true;
                uni.showLoading({
                    title: '加载中...',
                    mask: true
                });
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
                                                    id: cart.id,
                                                    itemId: itemRes.data.id,
                                                    name: itemRes.data.name,
                                                    price: itemRes.data.price,
                                                    image: itemRes.data.mainImage,
                                                    quantity: cart.quantity,
                                                    selected: cart.selected === 1
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
                                this.cartItems = items.filter(item => item !== null);
                                this.checkSelectAll();
                            });
                        } else {
                            this.cartItems = [];
                        }
                    },
                    fail: (err) => {
                        console.error('获取购物车数据失败', err);
                        uni.showToast({
                            title: '获取购物车数据失败',
                            icon: 'none'
                        });
                    },
                    complete: () => {
                        this.isLoading = false;
                        uni.hideLoading();
                    }
                });
            },

            // 跳转到商品详情
            goToDetail(id) {
                uni.navigateTo({
                    url: `/pages/item/detail?id=${id}`
                });
            },

            // 去购物
            goShopping() {
                uni.switchTab({
                    url: '/pages/index/index'
                });
            },

            // 切换选中状态
            toggleSelect(item) {
                item.selected = !item.selected;
                // 已改为批量更新
                this.checkSelectAll();
            },

            // 切换全选状态
            toggleSelectAll() {
                this.isAllSelected = !this.isAllSelected;
                const selectedStatus = this.isAllSelected ? 1 : 0;
                
                // 批量更新选中状态
                const updatePromises = this.cartItems.map(item => {
                    item.selected = this.isAllSelected;
                    return new Promise((resolve) => {
                        uni.request({
                            url: `${API_BASE_URL}/batchUpdateCartSelected`,
                            method: 'POST',
                            data: {
                                userId: uni.getStorageSync('userInfo')?.id,
                                itemIds: this.cartItems.map(i => i.id),
                                selected: selectedStatus
                            },
                            success: () => resolve(true),
                            fail: () => resolve(false)
                        });
                    });
                });

                Promise.all(updatePromises).then(results => {
                    if (results.some(r => !r)) {
                        uni.showToast({
                            title: '部分商品状态更新失败',
                            icon: 'none'
                        });
                    }
                });
            },

            // 检查是否全选
            checkSelectAll() {
                this.isAllSelected = this.cartItems.length > 0 && this.cartItems.every(item => item.selected);
            },

            // 增加数量
            increaseQuantity(item) {
                if (item.quantity < 99) {
                    item.quantity++;
                    this.updateCartItemQuantity(item);
                }
            },

            // 减少数量
            decreaseQuantity(item) {
                if (item.quantity > 1) {
                    item.quantity--;
                    this.updateCartItemQuantity(item);
                }
            },

            // 更新数量
            updateQuantity(item) {
                // 确保数量是有效的数字
                let quantity = parseInt(item.quantity);
                if (isNaN(quantity) || quantity < 1) {
                    quantity = 1;
                } else if (quantity > 99) {
                    quantity = 99;
                }
                item.quantity = quantity;
                this.updateCartItemQuantity(item);
            },

            // 更新购物车商品数量
            updateCartItemQuantity(item) {
                uni.request({
                    url: `${API_BASE_URL}/updateCart`,
                    method: 'POST',
                    data: {
                        id: item.id,
                        quantity: item.quantity
                    },
                    fail: (err) => {
                        console.error('更新购物车数量失败', err);
                        uni.showToast({
                            title: '更新购物车数量失败',
                            icon: 'none'
                        });
                    }
                });
            },

            // 更新购物车商品选中状态
            // 保留原有单个更新方法用于非全选操作

            // 删除商品
            deleteItem(item) {
                uni.showModal({
                    title: '提示',
                    content: '确定要删除这个商品吗？',
                    success: (res) => {
                        if (res.confirm) {
                            uni.request({
                                url: `${API_BASE_URL}/deleteCart?id=${item.id}`,
                                success: (res) => {
                                    if (res.data) {
                                        // 从列表中移除
                                        const index = this.cartItems.findIndex(i => i.id === item.id);
                                        if (index !== -1) {
                                            this.cartItems.splice(index, 1);
                                        }
                                        this.checkSelectAll();
                                        uni.showToast({
                                            title: '删除成功',
                                            icon: 'success'
                                        });
                                    } else {
                                        uni.showToast({
                                            title: '删除失败',
                                            icon: 'none'
                                        });
                                    }
                                },
                                fail: (err) => {
                                    console.error('删除购物车商品失败', err);
                                    uni.showToast({
                                        title: '删除购物车商品失败',
                                        icon: 'none'
                                    });
                                }
                            });
                        }
                    }
                });
            },

            // 结算功能
            checkout() {
                if (this.selectedCount === 0) {
                    uni.showToast({
                        title: '请选择要结算的商品',
                        icon: 'none'
                    });
                    return;
                }

                // 获取选中的商品
                const selectedItems = this.cartItems.filter(item => item.selected);
                
                // 将选中的商品信息存储到本地，用于订单确认页面使用
                uni.setStorageSync('checkoutItems', selectedItems);

                // 跳转到订单确认页面
                uni.navigateTo({
                    url: '/pages/order/confirm'
                });
            }
        }
    }
</script>

<style>
.cart-container {
    min-height: 100vh;
    background-color: #f8f8f8;
    padding-bottom: 100rpx;
}

.cart-item {
    background-color: #fff;
    margin: 20rpx;
    padding: 20rpx;
    border-radius: 10rpx;
    display: flex;
    align-items: center;
}

.item-select {
    margin-right: 20rpx;
}

.select-icon {
    width: 40rpx;
    height: 40rpx;
    border: 2rpx solid #ccc;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
}

.select-icon.selected {
    background-color: #ff6700;
    border-color: #ff6700;
}

.select-icon-inner {
    color: #fff;
    font-size: 24rpx;
}

.item-image {
    width: 160rpx;
    height: 160rpx;
    margin-right: 20rpx;
}

.item-info {
    flex: 1;
}

.item-name {
    font-size: 28rpx;
    margin-bottom: 10rpx;
}

.item-price {
    color: #ff6700;
    font-size: 32rpx;
}

.qty-control {
    display: flex;
    align-items: center;
    margin-top: 10rpx;
}

.qty-btn {
    width: 60rpx;
    height: 60rpx;
    line-height: 60rpx;
    text-align: center;
    background-color: #f5f5f5;
}

.qty-input {
    width: 80rpx;
    text-align: center;
    margin: 0 10rpx;
}

.delete-btn {
    padding: 20rpx;
    color: #999;
    font-size: 40rpx;
}

.cart-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    padding: 0 20rpx;
    border-top: 1rpx solid #eee;
}

.select-all {
    display: flex;
    align-items: center;
}

.select-text {
    margin-left: 10rpx;
    font-size: 28rpx;
}

.total-info {
    flex: 1;
    text-align: right;
    margin-right: 20rpx;
}

.total-price {
    color: #ff6700;
    font-size: 32rpx;
    font-weight: bold;
}

.checkout-btn {
    width: 200rpx;
    height: 80rpx;
    line-height: 80rpx;
    background-color: #ff6700;
    color: #fff;
    font-size: 28rpx;
    border-radius: 40rpx;
    text-align: center;
}

.checkout-btn[disabled] {
    background-color: #ccc;
    opacity: 0.6;
}
.total-price {
    margin-left: 10rpx;
    font-weight: normal;
}
</style>
