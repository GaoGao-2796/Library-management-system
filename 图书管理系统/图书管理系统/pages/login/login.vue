<template>
	<uni-nav-bar :fixed="true" left-icon="left" left-text="返回" title="图书管理系统" @clickLeft="handleBack" />
    <view class="login-container">
    <!-- 背景装饰元素 -->
    <view class="decorator-circle circle-1"></view>
    <view class="decorator-circle circle-2"></view>
    
    <!-- 登录表单区域 -->
    <view class="login-card">
      <!-- Logo和标题 -->
      <view class="logo-container">
        <image class="logo" src="/static/logo-book.png" mode="aspectFit"></image>
        <text class="app-name">图书管理系统</text>
      </view>

      <!-- 登录表单 -->
      <view class="form-container">
        <!-- 账号输入 -->
        <view class="input-group">
          <uni-icons type="person" size="20" color="#1890ff"></uni-icons>
          <input 
            class="input-field" 
            type="text" 
            v-model="form.username" 
            placeholder="请输入borrower 编号"
            placeholder-class="placeholder"
            @focus="resetField('username')"
          />
          <text class="error-msg" v-if="errors.username">{{ errors.username }}</text>
        </view>

        <!-- 密码输入 -->
        <view class="input-group">
          <uni-icons type="locked" size="20" color="#1890ff"></uni-icons>
          <input 
            class="input-field" 
            type="password" 
            v-model="form.password" 
            placeholder="请输入密码"
            placeholder-class="placeholder"
            @focus="resetField('password')"
          />
          <text class="error-msg" v-if="errors.password">{{ errors.password }}</text>
        </view>

        <!-- 记住我 & 忘记密码 -->
        <view class="form-options">
          <label class="remember-me">
            <checkbox-group @change="toggleRemember">
              <checkbox :checked="form.remember" color="#1890ff" />
              <text>记住我</text>
            </checkbox-group>
          </label>
          <text class="forget-pwd" @click="navigateToForget">忘记密码?</text>
        </view>

        <!-- 登录按钮 -->
        <button 
          class="login-btn" 
          :class="{ 'loading': isLoading }"
          :disabled="isLoading"
          @click="handleLogin"
        >
          <text v-if="!isLoading">登 录</text>
          <uni-load-more v-else status="loading" color="#fff"></uni-load-more>
        </button>

        <!-- 第三方登录 -->
        <view class="third-party-login" v-if="showThirdPartyLogin">
          <text class="divider">或</text>
          <view class="third-party-icons">
            <uni-icons 
              type="weixin" 
              size="50" 
              color="#09BB07" 
              @click="handleWechatLogin"
            ></uni-icons>
            <uni-icons 
              type="weibo" 
              size="50" 
              color="#E6162D" 
              @click="handleWeiboLogin"
            ></uni-icons>
          </view>
        </view>

        <!-- 注册引导 -->
        <view class="register-guide">
          <text>还没有账号?</text>
          <text class="register-link" @click="navigateToRegister">立即注册</text>
        </view>
      </view>
    </view>

    <!-- 协议声明 -->
    <view class="agreement">
      <text>登录即表示您同意</text>
      <text class="agreement-link" @click="showAgreement">《用户协议》</text>
      <text>和</text>
      <text class="agreement-link" @click="showPrivacy">《隐私政策》</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      form: {
        username: '',
        password: '',
        remember: false
      },
      errors: {
        username: '',
        password: ''
      },
      isLoading: false,
      showThirdPartyLogin: false,
      loginRules: {
        username: [
          { required: true, message: '请输入借阅证编号' },
          { min: 6, max: 20, message: '借阅证编号长度为6-20位' }
        ],
        password: [
          { required: true, message: '请输入密码' },
          { min: 6, max: 20, message: '密码长度为6-20位' }
        ]
      }
    }
  },
  onLoad() {
    this.loadRememberedAccount();
  },
  methods: {
    handleBack(){
		uni.switchTab({
			url:'/pages/index/index'
		})
	},
	navigateToRegister(){
		uni.navigateTo({
			url:'/pages/login/register/register'
		})
	},
    // 加载记住的账号
    loadRememberedAccount() {
      const remembered = uni.getStorageSync('rememberedAccount');
      if (remembered) {
        this.form = {
          ...remembered,
          password: '' // 安全考虑，不显示密码
        };
      }
    },
    
    // 表单验证
    validateForm() {
      let isValid = true;
      
      // 验证用户名
      if (!this.form.username.trim()) {
        this.errors.username = this.loginRules.username[0].message;
        isValid = false;
      } else if (this.form.username.length < 6 || this.form.username.length > 20) {
        this.errors.username = this.loginRules.username[1].message;
        isValid = false;
      }
      
      // 验证密码
      if (!this.form.password) {
        this.errors.password = this.loginRules.password[0].message;
        isValid = false;
      } else if (this.form.password.length < 6 || this.form.password.length > 20) {
        this.errors.password = this.loginRules.password[1].message;
        isValid = false;
      }
      
      return isValid;
    },
    
    // 重置字段错误
    resetField(field) {
      this.errors[field] = '';
    },
    
    // 记住我切换
    toggleRemember(e) {
      this.form.remember = e.detail.value.length > 0;
    },
    
    // 处理登录
    async handleLogin() {
      if (!this.validateForm()) return;
      
      this.isLoading = true;
      
      try {
        // 从本地存储获取用户数据
        const borrowers = JSON.parse(uni.getStorageSync('borrowers') || '[]');
        
        // 查找用户
        const user = borrowers.find(u => 
          u.borrowerId === this.form.username && 
          u.password === this.form.password
        );
        
        if (user) {
          // 保存登录状态
          uni.setStorageSync('currentUser', JSON.stringify(user));
          
          // 记住账号
          if (this.form.remember) {
            uni.setStorageSync('rememberedAccount', {
              username: this.form.username,
              remember: true
            });
          } else {
            uni.removeStorageSync('rememberedAccount');
          }
          
          // 跳转到首页
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          });
          
          setTimeout(() => {
            uni.switchTab({
              url: '/pages/index/index'
            });
          }, 1500);
        } else {
          throw new Error('借阅证编号或密码错误');
        }
      } catch (error) {
        console.error('登录失败:', error);
        uni.showToast({
          title: error.message || '登录失败，请重试',
          icon: 'none'
        });
      } finally {
        this.isLoading = false;
      }
    },
    
    // 其他方法保持不变...
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  overflow: hidden;
}

.decorator-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  opacity: 0.1;
}

.circle-1 {
  width: 500rpx;
  height: 500rpx;
  top: -150rpx;
  left: -150rpx;
}

.circle-2 {
  width: 300rpx;
  height: 300rpx;
  bottom: -50rpx;
  right: -50rpx;
}

.login-card {
  width: 85%;
  padding: 50rpx 40rpx;
  background-color: #fff;
  border-radius: 20rpx;
  box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.05);
  z-index: 1;
}

.logo-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
}

.logo {
  width: 120rpx;
  height: 120rpx;
}

.app-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-top: 20rpx;
}

.input-group {
  margin-bottom: 40rpx;
  position: relative;
}

.input-group uni-icons {
  position: absolute;
  left: 20rpx;
  top: 50%;
  transform: translateY(-50%);
}

.input-field {
  width: 100%;
  height: 90rpx;
  padding: 0 80rpx;
  border-radius: 45rpx;
  background-color: #f5f7fa;
  border: 1rpx solid #eee;
  font-size: 28rpx;
  box-sizing: border-box;
}

.error-msg {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  color: #f56c6c;
  padding-left: 80rpx;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  font-size: 26rpx;
  color: #666;
}

.remember-me {
  display: flex;
  align-items: center;
}

.remember-me text {
  margin-left: 10rpx;
}

.forget-pwd {
  color: #1890ff;
}

.login-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(90deg, #1890ff, #36cfc9);
  color: #fff;
  border-radius: 45rpx;
  font-size: 32rpx;
  margin-bottom: 40rpx;
  border: none;
}

.login-btn.loading {
  opacity: 0.8;
}

.third-party-login {
  margin-bottom: 40rpx;
}

.divider {
  display: block;
  text-align: center;
  color: #999;
  font-size: 26rpx;
  margin-bottom: 30rpx;
  position: relative;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 30%;
  height: 1rpx;
  background-color: #eee;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.third-party-icons {
  display: flex;
  justify-content: center;
  gap: 60rpx;
}

.register-guide {
  text-align: center;
  font-size: 26rpx;
  color: #666;
}

.register-link {
  color: #1890ff;
  margin-left: 10rpx;
}

.agreement {
  position: absolute;
  bottom: 40rpx;
  text-align: center;
  font-size: 24rpx;
  color: #999;
}

.agreement-link {
  color: #1890ff;
}
</style>