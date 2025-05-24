<template>
	<view class="book-detail-container">
		<!-- 顶部导航 -->
		<view class="nav-bar">
			<view class="nav-back" @click="navigateBack">
				<uni-icons type="arrowleft" size="24" color="#333"></uni-icons>
			</view>
			<text class="nav-title">图书详情</text>
		</view>

		<!-- 图书封面和基本信息 -->
		<view class="book-header">
			<image class="book-cover" :src="bookInfo.image || '/static/default-book.png'" mode="aspectFit"></image>
			<view class="book-basic-info">
				<text class="book-title">{{bookInfo.name}}</text>
				<text class="book-price">价格: {{bookInfo.current_price}}RMB</text>
				<text class="book-category">图书分类: {{bookInfo.type_name}}</text>
			</view>
		</view>

		<!-- 图书简介 -->
		<view class="section">
			<text class="section-title">图书简介</text>
			<text class="book-description">{{bookInfo.description || '暂无简介'}}</text>
		</view>

		<!-- 详细信息 -->
		<view class="section">
			<text class="section-title">详细信息</text>
			<view class="detail-list">
				<view class="detail-item">
					<text class="detail-label">作者:</text>
					<text class="detail-value">{{bookInfo.author || '未知'}}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">数量:</text>
					<text class="detail-value">{{bookInfo.stock}}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">出版社:</text>
					<text class="detail-value">{{bookInfo.publisher || '未知'}}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">点击次数:</text>
					<text class="detail-value">{{bookInfo.click_count}}</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">原价:</text>
					<text class="detail-value">{{bookInfo.original_price}}RMB</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">购买积分:</text>
					<text class="detail-value">{{bookInfo.points}}</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="add-cart-btn" @click="addToCart">加入购物车</button>
			<button class="buy-now-btn" @click="buyNow">立即购买</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				bookId: null,
				bookInfo: {
					id: null,
					name: '',
					image: '',
					type_name: '',
					current_price: 0,
					original_price: 0,
					stock: 0,
					author: '',
					publisher: '',
					click_count: 0,
					points: 0,
					description: ''
				}
			}
		},
		onLoad(options) {
			this.bookId = options.id || options.bookId;
			this.fetchBookDetail();
		},
		methods: {
			// 获取图书详情
			async fetchBookDetail() {
				try {
					uni.showLoading({
						title: '加载中...'
					});

					const res = await uni.request({
						url: `https://your-api-domain.com/api/books/${this.bookId}`,
						method: 'GET'
					});

					if (res.data.code === 200) {
						this.bookInfo = res.data.data;
						// 更新点击次数
						this.updateClickCount();
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('获取图书详情失败:', error);
					uni.showToast({
						title: '获取数据失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 更新点击次数
			async updateClickCount() {
				try {
					await uni.request({
						url: `https://your-api-domain.com/api/books/${this.bookId}/click`,
						method: 'PUT'
					});
				} catch (error) {
					console.error('更新点击次数失败:', error);
				}
			},

			// 加入购物车
			async addToCart() {
				try {
					uni.showLoading({
						title: '处理中...'
					});

					const res = await uni.request({
						url: 'https://your-api-domain.com/api/cart',
						method: 'POST',
						data: {
							book_id: this.bookId,
							quantity: 1
						},
						header: {
							'Authorization': `Bearer ${uni.getStorageSync('token')}`
						}
					});

					if (res.data.code === 200) {
						uni.showToast({
							title: '已加入购物车',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加入购物车失败:', error);
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 立即购买
			buyNow() {
				uni.navigateTo({
					url: `/pages/order/confirm?book_id=${this.bookId}&quantity=1`
				});
			},

			// 返回上一页
			navigateBack() {
				uni.navigateBack();
			}
		}
	}
</script>

<style scoped>
	.book-detail-container {
		padding: 20rpx;
		padding-bottom: 120rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.nav-bar {
		display: flex;
		align-items: center;
		padding: 15rpx 0;
		margin-bottom: 20rpx;
	}

	.nav-back {
		padding: 10rpx;
	}

	.nav-title {
		font-size: 36rpx;
		font-weight: bold;
		margin-left: 20rpx;
	}

	.book-header {
		display: flex;
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.book-cover {
		width: 240rpx;
		height: 320rpx;
		border-radius: 8rpx;
		margin-right: 30rpx;
	}

	.book-basic-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.book-title {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		color: #333;
	}

	.book-price {
		font-size: 32rpx;
		color: #e74c3c;
		margin-bottom: 15rpx;
	}

	.book-category {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 15rpx;
	}

	.section {
		background-color: #fff;
		padding: 30rpx;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: block;
		border-left: 6rpx solid #3498db;
		padding-left: 15rpx;
	}

	.book-description {
		font-size: 28rpx;
		color: #666;
		line-height: 1.6;
	}

	.detail-list {
		display: flex;
		flex-direction: column;
	}

	.detail-item {
		display: flex;
		margin-bottom: 15rpx;
	}

	.detail-label {
		width: 160rpx;
		font-size: 28rpx;
		color: #999;
	}

	.detail-value {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	.action-buttons {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		background-color: #fff;
		padding: 20rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		z-index: 10;
	}

	.add-cart-btn,
	.buy-now-btn {
		flex: 1;
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 40rpx;
		font-size: 30rpx;
	}

	.add-cart-btn {
		background-color: #f8f8f8;
		color: #333;
		margin-right: 20rpx;
	}

	.buy-now-btn {
		background-color: #e74c3c;
		color: #fff;
	}
</style>