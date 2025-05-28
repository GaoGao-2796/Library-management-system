<template>
	<view class="index-container">
		<!-- 顶部导航栏 -->
		<uni-nav-bar :fixed="true" right-icon="person" title="图书管理系统"
			 @clickRight="navigateToUserCenter" />

		<!-- 搜索区域 -->
		<view class="search-section">
			<uni-search-bar v-model="searchKeyword" placeholder="搜索图书/作者" @confirm="handleSearch" @clear="clearSearch"
				cancel-button="none" />
		</view>

		<!-- 分类标签 -->
		<view class="category-tabs">
			<view v-for="(tab, index) in tabs" :key="index" class="tab-item" :class="{ active: activeTab === index }"
				@click="switchTab(index)">
				<text>{{ tab.name }}</text>
			</view>
		</view>

		<!-- 轮播图 -->
		<view class="swiper-container">
			<swiper circular :indicator-dots="true" :autoplay="true" :interval="5000">
				<swiper-item v-for="(item, index) in banners" :key="index">
					<image :src="item.image" mode="aspectFill" class="banner-image" @click="handleBannerClick(item)" />
				</swiper-item>
			</swiper>
		</view>

		<!-- 公告 -->
		<uni-notice-bar show-icon scrollable :text="announcement" />

		<!-- 热门图书 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">热门图书</text>
				<text class="more" @click="navigateToBookList('hot')">更多 ></text>
			</view>
			<scroll-view scroll-x class="book-scroll">
				<view v-for="book in hotBooks" :key="book.id" class="book-card" @click="navigateToBookDetail(book.id)">
					<image :src="book.cover" class="book-cover" mode="aspectFit" />
					<text class="book-title">{{ book.title }}</text>
					<text class="book-author">{{ book.author }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 新书推荐 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">新书推荐</text>
				<text class="more" @click="navigateToBookList('new')">更多 ></text>
			</view>
			<view class="book-grid">
				<view v-for="book in newBooks" :key="book.id" class="book-item" @click="navigateToBookDetail(book.id)">
					<image :src="book.cover" class="book-cover" mode="aspectFit" />
					<text class="book-title">{{ book.title }}</text>
					<text class="book-price">¥{{ book.price }}</text>
				</view>
			</view>
		</view>

		<!-- 底部导航 -->
		<view class="footer-nav">
			<view v-for="(nav, index) in navItems" :key="index" class="nav-item" @click="handleNavClick(nav, index)">
				<uni-icons :type="nav.icon" size="24" :color="isCurrentPage(nav.path) ? '#1890ff' : '#666'" />
				<text :class="{ active: isCurrentPage(nav.path) }">{{ nav.text }}</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				searchKeyword: '',
				activeTab: 0,
				tabs: [{
						name: '全部',
						type: 'all'
					},
					{
						name: '文学',
						type: 'literature'
					},
					{
						name: '科技',
						type: 'technology'
					},
					{
						name: '历史',
						type: 'history'
					},
					{
						name: '小说',
						type: 'fiction'
					}
				],
				banners: [{
						id: 1,
						image: '/static/banner1.jpg',
						link: '/pages/promotion/1'
					},
					{
						id: 2,
						image: '/static/banner2.jpg',
						link: '/pages/promotion/2'
					},
					{
						id: 3,
						image: '/static/banner3.jpg',
						link: '/pages/promotion/3'
					}
				],
				announcement: '系统维护通知：本周六凌晨2:00-4:00将进行系统升级，期间无法借阅图书。',
				hotBooks: [],
				newBooks: [],
				navItems: [{
						text: '首页',
						icon: 'home',
						path: '/pages/index/index'
					},
					{
						text: '分类',
						icon: 'list',
						path: '/pages/content/content'
					},
					{
						text: '借阅',
						icon: 'cart',
						path: '/pages/borrow/index'
					},
					{
						text: '我的',
						icon: 'person',
						path: '/pages/user/user'
					}
				]
			}
		},
		onLoad() {
			this.loadData();
		},
		methods: {
			async loadData() {
				try {
					uni.showLoading({
						title: '加载中...'
					});

					// 获取热门图书
					const [hotRes, newRes] = await Promise.all([
						uni.request({
							url: '/api/books/hot'
						}),
						uni.request({
							url: '/api/books/new'
						})
					]);

					if (hotRes[1].code === 200) {
						this.hotBooks = hotRes[1].data.slice(0, 5);
					}

					if (newRes[1].code === 200) {
						this.newBooks = newRes[1].data.slice(0, 4);
					}
				} catch (error) {
					console.error('数据加载失败:', error);
					uni.showToast({
						title: '数据加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			handleSearch() {
				if (!this.searchKeyword.trim()) {
					uni.showToast({
						title: '请输入搜索内容',
						icon: 'none'
					});
					return;
				}
				uni.navigateTo({
					url: `/pages/search/result?keyword=${this.searchKeyword}`
				});
			},

			clearSearch() {
				this.searchKeyword = '';
			},

			switchTab(index) {
				this.activeTab = index;
				// 实际项目中这里应该加载对应分类的图书
			},

			handleBannerClick(item) {
				uni.navigateTo({
					url: item.link
				});
			},

			navigateToBookList(type) {
				uni.navigateTo({
					url: `/pages/book/list?type=${type}`
				});
			},

			navigateToBookDetail(id) {
				uni.navigateTo({
					url: `/pages/book/detail?id=${id}`
				});
			},

			navigateToUserCenter() {
				const currentUser = uni.getStorageSync('currentUser');
				if (currentUser) {
					// 已登录，跳转到用户中心
					uni.switchTab({
						url: '/pages/user/user'
					});
				} else {
					// 未登录，跳转到登录页面
					uni.navigateTo({
						url: '/pages/login/login'
					});
				}
			},

			navigateToLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			},

			isCurrentPage(path) {
				const pages = getCurrentPages();
				if (!pages.length) return false;
				const currentPage = pages[pages.length - 1];
				return currentPage.route === path.replace('/pages/', '').replace('/index', '');
			},

			handleNavClick(nav, index) {
				if (this.isCurrentPage(nav.path)) return;

				// 特殊处理"我的"页面
				if (nav.path === '/pages/user/user') {
					const currentUser = uni.getStorageSync('currentUser');
					if (currentUser) {
						// 已登录，使用 switchTab 跳转到用户中心
						uni.switchTab({
							url: '/pages/user/user',
							success: () => {
								console.log('已跳转到用户中心');
							},
							fail: (err) => {
								console.error('跳转失败:', err);
							}
						});
					} else {
						// 未登录，使用 navigateTo 跳转到登录页
						uni.navigateTo({
							url: '/pages/login/login',
							success: () => {
								console.log('跳转到登录页');
							}
						});
					}
					return;
				}

				// 其他导航项正常跳转
				uni.switchTab({
					url: nav.path
				});
			},



			handleBack() {
				uni.navigateBack()
			}
		}
	}
</script>

<style scoped>
	.index-container {
		padding-bottom: 100rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.search-section {
		padding: 20rpx;
		background-color: #fff;
	}

	.category-tabs {
		display: flex;
		padding: 20rpx 0;
		background-color: #fff;
		border-bottom: 1rpx solid #eee;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 15rpx 0;
		font-size: 28rpx;
		color: #666;
	}

	.tab-item.active {
		color: #1890ff;
		position: relative;
	}

	.tab-item.active::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		width: 60rpx;
		height: 4rpx;
		background-color: #1890ff;
	}

	.swiper-container {
		padding: 20rpx;
		background-color: #fff;
	}

	.banner-image {
		width: 100%;
		height: 300rpx;
		border-radius: 12rpx;
	}

	.section {
		margin-top: 20rpx;
		padding: 20rpx;
		background-color: #fff;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.more {
		font-size: 26rpx;
		color: #999;
	}

	.book-scroll {
		white-space: nowrap;
		width: 100%;
	}

	.book-card {
		display: inline-block;
		width: 200rpx;
		margin-right: 20rpx;
	}

	.book-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
	}

	.book-item {
		display: flex;
		flex-direction: column;
	}

	.book-cover {
		width: 100%;
		height: 300rpx;
		border-radius: 8rpx;
		margin-bottom: 10rpx;
	}

	.book-title {
		font-size: 28rpx;
		color: #333;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.book-author {
		font-size: 24rpx;
		color: #999;
	}

	.book-price {
		font-size: 26rpx;
		color: #e74c3c;
		font-weight: bold;
	}

	.footer-nav {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		justify-content: space-around;
		padding: 15rpx 0;
		background-color: #fff;
		border-top: 1rpx solid #eee;
		z-index: 100;
	}

	.nav-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 10rpx 0;
		/* 添加点击效果 */
		transition: all 0.3s;
	}

	.nav-item:active {
		opacity: 0.7;
		transform: scale(0.95);
	}

	.nav-item text {
		font-size: 24rpx;
		color: #666;
		margin-top: 5rpx;
		transition: color 0.3s;
	}

	.nav-item text.active {
		color: #1890ff;
	}
</style>