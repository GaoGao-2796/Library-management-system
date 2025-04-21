<template>
	
	<view class="box-bg">
					<uni-nav-bar :fixed="true" shadow left-icon="left" leftText="返回" right-icon="person" title="图书管理系统" style="margin-top: -12rpx;"/>
				</view>
	
	<view>
		<uni-search-bar @confirm="search" :focus="true" v-model="searchValue" @blur="blur" @focus="focus" @input="input"
						@cancel="cancel" @clear="clear">
		</uni-search-bar>
	</view>
	
	<!-- 本示例未包含完整css，获取外链css请参考上文，在hello uni-app项目中查看 -->
	
		<view>
			<view class="uni-margin-wrap">
				<swiper class="swiper" circular :indicator-dots="indicatorDots" :autoplay="autoplay" :interval="interval"
					:duration="duration">
					<swiper-item>
						<view class="swiper-item uni-bg-red" >A</view>
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
				            duration: 500
			}
		},
		onLoad() {

		},
		methods: {
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
            			}
            		,
					onBackPress() {
								// #ifdef APP-PLUS
								plus.key.hideSoftKeybord();
								// #endif
							}
		}
	}
	
</script>

<style>
	.uni-column{
		flex-direction: column;
	}
	.uni-row{
		flex-direction: row;
	}

	.uni-flex{
		display: flex;
	}
	.uni-bg-red{
		background-color: red;
	}
	.uni-bg-green{
		background-color: green;
	}
	.uni-bg-blue{
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
			width: 550rpx;
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
			color:#666;
		}
	
		.example-body {
			/* #ifndef APP-NVUE */
			display: block;
			/* #endif */
			padding: 0px;
		}
	
		.uni-mt-10 {
			margin-top: 10px;
		}
		.box-bg {
				background-color: #F5F5F5;
				padding: 5px 0;
			}
</style>
