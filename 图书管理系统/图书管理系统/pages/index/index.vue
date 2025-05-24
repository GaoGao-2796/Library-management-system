<template>
<view style="background-color:azure;">
	<view class="box-bg">
		<uni-nav-bar :fixed="true" left-icon="left" leftText="返回" right-icon="person" title="图书管理系统"
			style="margin-top: -12rpx;" />
	</view>

	<view class="uni-flex uni-row" style="justify-content: space-between; background-color: burlywood;">
		<view>
			<uni-search-bar @confirm="search" :focus="true" v-model="searchValue" @blur="blur" @focus="focus"
				@input="input" @cancel="cancel" @clear="clear">
			</uni-search-bar>
		</view>
		<view>
			
			<uni-section title="实心标签" type="line" padding >
				<view class="example-body">
					<view class="tag-view" style="padding-left: 25rpx;" @click="goToPageIndex('/pages/index/index')">
						<uni-tag text="首页" type="primary" />
					</view>
					<view class="tag-view" style="padding-left: 25rpx;" @click="goToPageContent('/pages/content/content')">
						<uni-tag text="大全" type="success" />
					</view>
					<view class="tag-view" style="padding-left: 25rpx;">
						<uni-tag text="咨询" type="warning" />
					</view>
					<view class="tag-view" style="padding-left: 25rpx;" @click="goToPageLogin('/pages/login/login')">
						<uni-tag text="个人" type="error" />
					</view>
				</view>
			</uni-section>
		</view>
	</view>


	<view>
		<view class="uni-margin-wrap" style="margin-bottom: 20rpx;">
			<swiper class="swiper" circular :indicator-dots="indicatorDots" :autoplay="autoplay" :interval="interval"
				:duration="duration">
				<swiper-item>
					<view class="swiper-item uni-bg-red">A</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item uni-bg-green">B</view>
				</swiper-item>
				<swiper-item>
					<view class="swiper-item uni-bg-blue">C</view>
				</swiper-item>
			</swiper>
		</view>
		
		<view>
		<uni-notice-bar show-icon scrollable
						text="uni-app 版正式发布，开发一次，同时发布iOS、Android、H5、微信小程序、支付宝小程序、百度小程序、头条小程序等7大平台。" />
        </view>

		<view>
			<view class="uni-padding-wrap uni-common-mt">
				<view class="uni-flex uni-column">
					<view class="flex-item flex-item-V uni-bg-red">
						<view class="uni-flex uni-row">
							<view class="flex-item uni-bg-red">A</view>
							<view class="flex-item uni-bg-green">B</view>
							<view class="flex-item uni-bg-blue">C</view>
						</view>
					</view>
					<view class="flex-item flex-item-V uni-bg-green">
						<view class="uni-flex uni-row">
							<view class="flex-item uni-bg-red">A</view>
							<view class="flex-item uni-bg-green">B</view>
							<view class="flex-item uni-bg-blue">C</view>
						</view>
					</view>
					<view class="flex-item flex-item-V uni-bg-blue">
						<view class="uni-flex uni-row">
							<view class="flex-item uni-bg-red">A</view>
							<view class="flex-item uni-bg-green">B</view>
							<view class="flex-item uni-bg-blue">C</view>
						</view>
					</view>
				</view>
			</view>
		</view>


	</view>




	<view class="content">
		<image class="logo" src="/static/logo.png"></image>
		<view class="text-area">
			<text class="title">{{title}}</text>
		</view>
	</view>
	
	<view class="uni-padding-wrap uni-common-mt" style="background-color: burlywood;">
		<view class="uni-flex uni-row" style=" justify-content:space-between;">
			<view class="uni-flex uni-row">
				<view class="flex-item uni-bg-red">
					<view class="flex-item uni-column" style="padding: 12rpx; flex-wrap: nowrap;">开放时间</view>
					<view class="flex-item uni-column" style="padding: 12rpx;">交通信息</view>
					<view class="flex-item uni-column" style="padding: 12rpx;">分管流通点</view>
				</view>
				<view class="flex-item uni-bg-blue">
					<view class="flex-item uni-column bigFontSize" style="text-align: center; padding:15rpx;">关于我们</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">图书馆简介</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">管史略览</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">领导集体</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">部门设置</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">业务统计</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">计划年报</view>
				</view>
				<view class="flex-item uni-bg-green">
					<view class="flex-item uni-column bigFontSize" style="text-align: center; padding:15rpx;">相关链接</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">古籍保护网</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">广东省图书馆</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">新世纪图书馆</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">广东少儿图书馆</view>
					<view class="flex-item uni-column commonFontSize" style="padding: 12rpx;">国家语言文字推广基地</view>
				</view>
			</view>
			<view>
				<view style="padding: 50rpx;">
					<view class="flex-item uni-column" style="padding: 20rpx; text-align: center;">图书管理系统</view>
					<view class="flex-item uni-column " style="text-align: center;">
						<image src="/static/logo.png" style="width: 150rpx; height: 150rpx;"></image>
					</view>
					<view class="flex-item uni-column commonFontSize">扫码咨询图书管理员</view>
					<view class="flex-item uni-column commonFontSize">电话咨询:1008611</view>
				</view>
			</view>
		</view>
	</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				title: 'Hello',
				background: ['color1', 'color2', 'color3'],
				indicatorDots: true,
				autoplay: true,
				interval: 5000,
				duration: 500,
				type: "default",
				inverted: false,
			}
		},
		onLoad() {

		},
		methods: {
			goToPageContent(){
				uni.navigateTo({
					url:'/pages/content/content'
				})
			},
			goToPageIndex(){
				uni.navigateTo({
					url:'/pages/index/index'					
				})
			},
			goToPageLogin(){
				uni.navigateTo({
					url:'/pages/login/login'
				})
			},
			setType() {
				let types = ["default", "primary", "success", "warning", "error"];
				let index = types.indexOf(this.type);
				types.splice(index, 1);
				let randomIndex = Math.floor(Math.random() * 4);
				this.type = types[randomIndex];
			},
			setInverted() {
				this.inverted = !this.inverted;
			},
			back() {
				uni.navigateBack({
					delta: 1
				})
			},
			changeIndicatorDots(e) {
				this.indicatorDots = !this.indicatorDots
			},
			changeAutoplay(e) {
				this.autoplay = !this.autoplay
			},
			intervalChange(e) {
				this.interval = e.target.value
			},
			durationChange(e) {
				this.duration = e.target.value
			},
			search(res) {
				uni.showToast({
					title: '搜索：' + res.value,
					icon: 'none'
				})
			},
			input(res) {
				console.log('----input:', res)
			},
			clear(res) {
				uni.showToast({
					title: 'clear事件，清除值为：' + res.value,
					icon: 'none'
				})
			},
			blur(res) {
				uni.showToast({
					title: 'blur事件，输入值为：' + res.value,
					icon: 'none'
				})
			},
			focus(e) {
				uni.showToast({
					title: 'focus事件，输出值为：' + e.value,
					icon: 'none'
				})
			},
			cancel(res) {
				uni.showToast({
					title: '点击取消，输入值为：' + res.value,
					icon: 'none'
				})
			},
			onBackPress() {
				// #ifdef APP-PLUS
				plus.key.hideSoftKeybord();
				// #endif
			}
		}
	}
</script>

<style>
	.uni-column {
		flex-direction: column;
	}

	.uni-row {
		flex-direction: row;
	}

	.uni-flex {
		display: flex;
	}

	.uni-bg-red {
		background-color: red;
	}

	.uni-bg-green {
		background-color: green;
	}

	.uni-bg-blue {
		background-color: blue;
	}

	.uni-margin-wrap {
		width: 690rpx;
		width: 100%;
	}

	.swiper {
		height: 300rpx;
	}

	.swiper-item {
		display: block;
		height: 300rpx;
		line-height: 300rpx;
		text-align: center;
	}

	.swiper-list {
		margin-top: 40rpx;
		margin-bottom: 0;
	}

	.uni-common-mt {
		margin-top: 60rpx;
		position: relative;
	}

	.info {
		position: absolute;
		right: 20rpx;
	}

	.uni-padding-wrap {
		
		padding: 0 100rpx;
	}

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

	.search-result {
		padding-top: 10px;
		padding-bottom: 20px;
		text-align: center;
	}

	.search-result-text {
		text-align: center;
		font-size: 14px;
		color: #666;
	}

	.example-body {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		padding: 20rpx;
      
	}

	.uni-mt-10 {
		margin-top: 10px;
	}

	.box-bg {
		background-color: #F5F5F5;
		padding: 5px 0;
	}
	.bigFontSize{
		font-size: 15px;
	}
	.commonFontSize{
		font-size: 10px;
	}
</style>