<template>
	<uni-nav-bar :fixed="true" left-icon="left" title="图书管理系统" @clickLeft="handleBack" />
	<view class="user-center-container">
		<!-- 顶部用户信息 -->
		<view class="user-header">
			<image class="avatar" :src="userInfo.avatar || '/static/default-avatar.png'" mode="aspectFill"></image>
			<view class="user-info">
				<text class="username">{{userInfo.name}}</text>
				<text class="account">账号: {{userInfo.account}}</text>
				<text class="member-level">会员等级: {{userInfo.member_level}}</text>
			</view>
		</view>

		<!-- 借阅情况统计 -->
		<view class="borrow-stats">
			<view class="stat-item" @click="navigateToBorrowList('immediate')">
				<text class="stat-number">{{borrowStats.immediate}}</text>
				<text class="stat-label">立即借阅</text>
			</view>
			<view class="stat-item" @click="navigateToBorrowList('borrowing')">
				<text class="stat-number">{{borrowStats.borrowing}}</text>
				<text class="stat-label">在借中</text>
			</view>
			<view class="stat-item overdue" @click="navigateToBorrowList('overdue')">
				<text class="stat-number">{{borrowStats.overdue}}</text>
				<text class="stat-label">逾期({{overdueFee}}元)</text>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-list">
			<view class="menu-item" @click="navigateTo('borrow-history')">
				<uni-icons type="calendar" size="20" color="#3498db"></uni-icons>
				<text class="menu-text">借阅历史</text>
				<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
			</view>
			<view class="menu-item" @click="navigateTo('favorites')">
				<uni-icons type="star" size="20" color="#f1c40f"></uni-icons>
				<text class="menu-text">我的收藏</text>
				<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
			</view>
			<view class="menu-item" @click="navigateTo('payment')">
				<uni-icons type="wallet" size="20" color="#2ecc71"></uni-icons>
				<text class="menu-text">缴费记录</text>
				<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
			</view>
			<view class="menu-item" @click="showOverduePayment" v-if="borrowStats.overdue > 0">
				<uni-icons type="alert" size="20" color="#e74c3c"></uni-icons>
				<text class="menu-text">逾期缴费</text>
				<text class="overdue-fee">需缴: {{overdueFee}}元</text>
				<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
			</view>
			<view class="menu-item" @click="showLogoutDialog">
				<uni-icons type="logout" size="20" color="#9b59b6"></uni-icons>
				<text class="menu-text">退出登录</text>
				<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
			</view>
		</view>

		<!-- 逾期缴费对话框 -->
		<uni-popup ref="overduePaymentDialog" type="dialog">
			<uni-popup-dialog mode="base" title="逾期缴费" :content="`您有${borrowStats.overdue}本图书逾期，需缴纳${overdueFee}元`"
				@confirm="handlePayment" @close="closePaymentDialog">
				<view class="payment-methods" v-if="showPaymentMethods">
					<radio-group @change="paymentMethodChange">
						<label class="payment-method">
							<radio value="wechat" checked /> 微信支付
						</label>
						<label class="payment-method">
							<radio value="alipay" /> 支付宝
						</label>
						<label class="payment-method">
							<radio value="balance" /> 余额支付
						</label>
					</radio-group>
				</view>
			</uni-popup-dialog>
		</uni-popup>

		<!-- 退出登录对话框 -->
		<uni-popup ref="logoutDialog" type="dialog">
			<uni-popup-dialog mode="base" title="提示" content="确定要退出登录吗?" @confirm="handleLogout"></uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: {
					id: null,
					account: '',
					name: '',
					avatar: '',
					member_level: '普通会员',
					balance: 0
				},
				borrowStats: {
					immediate: 0,
					borrowing: 0,
					overdue: 0
				},
				overdueFee: 0,
				paymentMethod: 'wechat',
				showPaymentMethods: false
			}
		},
		onShow() {
			this.fetchUserInfo();
			this.fetchBorrowStats();
		},
		methods: {
			handleBack() {
				uni.switchTab({
					url:'/pages/index/index'
				});
			},
			// 获取用户信息
			async fetchUserInfo() {
				try {
					uni.showLoading({
						title: '加载中...'
					});

					const res = await uni.request({
						url: 'https://your-api-domain.com/api/user/info',
						method: 'GET',
						header: {
							'Authorization': `Bearer ${uni.getStorageSync('token')}`
						}
					});

					if (res.data.code === 200) {
						this.userInfo = res.data.data;
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('获取用户信息失败:', error);
					uni.showToast({
						title: '获取数据失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 获取借阅统计
			async fetchBorrowStats() {
				try {
					const res = await uni.request({
						url: 'https://your-api-domain.com/api/user/borrow-stats',
						method: 'GET',
						header: {
							'Authorization': `Bearer ${uni.getStorageSync('token')}`
						}
					});

					if (res.data.code === 200) {
						this.borrowStats = res.data.data.stats;
						this.overdueFee = res.data.data.overdue_fee;
					}
				} catch (error) {
					console.error('获取借阅统计失败:', error);
				}
			},

			// 导航到不同页面
			navigateTo(page) {
				const routes = {
					'borrow-history': '/pages/user/borrow-history',
					'favorites': '/pages/user/favorites',
					'payment': '/pages/user/payment'
				};

				if (routes[page]) {
					uni.navigateTo({
						url: routes[page]
					});
				}
			},

			// 导航到借阅列表
			navigateToBorrowList(type) {
				uni.navigateTo({
					url: `/pages/user/borrow-list?type=${type}`
				});
			},

			// 显示逾期缴费对话框
			showOverduePayment() {
				this.showPaymentMethods = false;
				this.$refs.overduePaymentDialog.open();

				// 2秒后显示支付方式选择
				setTimeout(() => {
					this.showPaymentMethods = true;
				}, 2000);
			},

			// 关闭缴费对话框
			closePaymentDialog() {
				this.$refs.overduePaymentDialog.close();
			},

			// 处理支付方式选择
			paymentMethodChange(e) {
				this.paymentMethod = e.detail.value;
			},

			// 处理缴费
			async handlePayment() {
				try {
					uni.showLoading({
						title: '支付处理中...'
					});

					const res = await uni.request({
						url: 'https://your-api-domain.com/api/user/pay-overdue',
						method: 'POST',
						data: {
							amount: this.overdueFee,
							method: this.paymentMethod
						},
						header: {
							'Authorization': `Bearer ${uni.getStorageSync('token')}`
						}
					});

					if (res.data.code === 200) {
						uni.showToast({
							title: '缴费成功',
							icon: 'success'
						});
						this.fetchBorrowStats(); // 刷新借阅统计
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('缴费失败:', error);
					uni.showToast({
						title: '缴费失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
					this.$refs.overduePaymentDialog.close();
				}
			},

			// 显示退出登录对话框
			showLogoutDialog() {
				this.$refs.logoutDialog.open();
			},

			// 处理退出登录
			handleLogout() {
				// 清除登录状态
				uni.removeStorageSync('token');
				uni.removeStorageSync('userInfo');

				// 跳转到登录页
				uni.reLaunch({
					url: '/pages/login/login'
				});
			}
		}
	}
</script>

<style scoped>
	.user-center-container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.user-header {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		margin-right: 30rpx;
	}

	.user-info {
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	.username {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.account,
	.member-level {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 8rpx;
	}

	.borrow-stats {
		display: flex;
		background-color: #fff;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		padding: 30rpx 0;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.stat-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.stat-item.overdue {
		color: #e74c3c;
	}

	.stat-number {
		font-size: 40rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.stat-label {
		font-size: 28rpx;
		color: #666;
	}

	.menu-list {
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}

	.menu-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #eee;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.menu-text {
		flex: 1;
		font-size: 30rpx;
		margin-left: 20rpx;
		color: #333;
	}

	.overdue-fee {
		font-size: 26rpx;
		color: #e74c3c;
		margin-right: 15rpx;
	}

	.payment-methods {
		padding: 20rpx 0;
	}

	.payment-method {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		font-size: 28rpx;
	}
</style>