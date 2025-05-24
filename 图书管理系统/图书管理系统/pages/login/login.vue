<template>
	<view class="login-container">
	    <view class="logo-container">
	      <image class="logo" src="/static/logo.png" mode="aspectFit"></image>
	      <text class="app-name">欢迎登录</text>
	    </view>
	    
	    <view class="form-container">
	      <view class="input-group">
	        <text class="input-label">账号</text>
	        <input 
	          class="input-field" 
	          type="text" 
	          v-model="username" 
	          placeholder="请输入账号" 
	          placeholder-class="placeholder"
	        />
	      </view>
	      
	      <view class="input-group">
	        <text class="input-label">密码</text>
	        <input 
	          class="input-field" 
	          type="password" 
	          v-model="password" 
	          placeholder="请输入密码" 
	          placeholder-class="placeholder"
	        />
	      </view>
	      
	      <view class="remember-me">
	        <checkbox-group @change="toggleRemember">
	          <label>
	            <checkbox :checked="rememberMe" color="#1890ff"/>记住我
	          </label>
	        </checkbox-group>
	      </view>
	      
	      <button class="login-btn" @click="handleLogin">登录</button>
	      
	      <view class="other-options">
	        <text class="option-text" @click="navigateToRegister">注册账号</text>
	        <text class="option-text" @click="navigateToForget">忘记密码</text>
	      </view>
	    </view>
	  </view>
</template>

<script>
	export default {
		data() {
			return {
				username: '',
				password: '',
				rememberMe: false
			}
		},
		methods: {
			handleLogin() {
			      if (!this.username) {
			        uni.showToast({
			          title: '请输入账号',
			          icon: 'none'
			        });
			        return;
			      }
			      
			      if (!this.password) {
			        uni.showToast({
			          title: '请输入密码',
			          icon: 'none'
			        });
			        return;
			      }
			      
			      // 模拟登录请求
			      uni.showLoading({
			        title: '登录中...'
			      });
			      
			      setTimeout(() => {
			        uni.hideLoading();
			        uni.showToast({
			          title: '登录成功',
			          icon: 'success'
			        });
			        
			        // 跳转到首页
			        uni.navigateTo({
			          url: '/pages/index/index'
			        });
			      }, 1500);
			    },
			    
			    toggleRemember(e) {
			      this.rememberMe = e.detail.value.length > 0;
			    },
			    
			    navigateToRegister() {
			      uni.navigateTo({
			        url: '/pages/register/register'
			      });
			    },
			    
			    navigateToForget() {
			      uni.navigateTo({
			        url: '/pages/forget/forget'
			      });
			    }
			  }
		}
	
</script>

<style>
    .login-container {
      padding: 40rpx;
      display: flex;
      flex-direction: column;
      align-items: center;
      height: 100vh;
      background-color: #f5f5f5;
    }
    
    .logo-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      margin-top: 100rpx;
      margin-bottom: 80rpx;
    }
    
    .logo {
      width: 150rpx;
      height: 150rpx;
    }
    
    .app-name {
      font-size: 36rpx;
      color: #333;
      margin-top: 20rpx;
      font-weight: bold;
    }
    
    .form-container {
      width: 100%;
    }
    
    .input-group {
      margin-bottom: 40rpx;
    }
    
    .input-label {
      display: block;
      font-size: 28rpx;
      color: #666;
      margin-bottom: 15rpx;
    }
    
    .input-field {
      width: 100%;
      height: 80rpx;
      padding: 0 20rpx;
      border-radius: 8rpx;
      background-color: #fff;
      border: 1rpx solid #ddd;
      font-size: 28rpx;
    }
    
    .placeholder {
      color: #ccc;
    }
    
    .remember-me {
      margin-bottom: 40rpx;
      font-size: 26rpx;
      color: #666;
    }
    
    .login-btn {
      width: 100%;
      height: 90rpx;
      line-height: 90rpx;
      background-color: #1890ff;
      color: #fff;
      border-radius: 45rpx;
      font-size: 32rpx;
      margin-bottom: 40rpx;
    }
    
    .other-options {
      display: flex;
      justify-content: space-between;
    }
    
    .option-text {
      color: #1890ff;
      font-size: 28rpx;
    }

</style>
