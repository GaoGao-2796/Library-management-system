<template>
  <view class="forgot-container">
    <!-- 顶部导航 -->
    <uni-nav-bar 
      title="找回密码"
      left-icon="left"
      @clickLeft="navigateBack"
    />

    <!-- 步骤条 -->
    <view class="steps">
      <view 
        class="step-item"
        :class="{ active: currentStep >= 1 }"
      >
        <text class="step-num">1</text>
        <text class="step-text">验证身份</text>
      </view>
      <view class="step-line" :class="{ active: currentStep >= 2 }"></view>
      <view 
        class="step-item"
        :class="{ active: currentStep >= 2 }"
      >
        <text class="step-num">2</text>
        <text class="step-text">重置密码</text>
      </view>
    </view>

    <!-- 步骤1：验证身份 -->
    <view class="step-content" v-if="currentStep === 1">
      <view class="input-group">
        <uni-icons type="phone" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="number" 
          v-model="form.mobile" 
          placeholder="请输入注册手机号"
          maxlength="11"
        />
      </view>

      <view class="input-group">
        <uni-icons type="email" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="number" 
          v-model="form.code" 
          placeholder="请输入验证码"
          maxlength="6"
        />
        <button 
          class="code-btn" 
          :disabled="isCountingDown"
          @click="sendSmsCode"
        >
          {{ countdownText }}
        </button>
      </view>

      <button class="next-btn" @click="verifyIdentity">下一步</button>
    </view>

    <!-- 步骤2：重置密码 -->
    <view class="step-content" v-else>
      <view class="input-group">
        <uni-icons type="locked" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="password" 
          v-model="form.newPassword" 
          placeholder="请设置新密码(6-20位)"
        />
      </view>

      <view class="input-group">
        <uni-icons type="locked" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="password" 
          v-model="form.confirmPassword" 
          placeholder="请再次输入新密码"
        />
      </view>

      <button 
        class="submit-btn" 
        :class="{ 'loading': isLoading }"
        :disabled="isLoading"
        @click="resetPassword"
      >
        <text v-if="!isLoading">提交</text>
        <uni-load-more v-else status="loading" color="#fff"></uni-load-more>
      </button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      currentStep: 1,
      form: {
        mobile: '',
        code: '',
        newPassword: '',
        confirmPassword: ''
      },
      isLoading: false,
      isCountingDown: false,
      countdown: 60,
      countdownTimer: null
    }
  },
  computed: {
    countdownText() {
      return this.isCountingDown ? `${this.countdown}s后重新获取` : '获取验证码';
    }
  },
  methods: {
    // 发送短信验证码
    async sendSmsCode() {
      if (!this.form.mobile) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        });
        return;
      }
      
      if (!/^1[3-9]\d{9}$/.test(this.form.mobile)) {
        uni.showToast({
          title: '手机号格式不正确',
          icon: 'none'
        });
        return;
      }
      
      this.isCountingDown = true;
      
      try {
        // 调用发送验证码接口
        const res = await uni.request({
          url: '/api/auth/send-code',
          method: 'POST',
          data: {
            mobile: this.form.mobile,
            type: 'reset-password'
          }
        });
        
        if (res[1].statusCode === 200) {
          uni.showToast({
            title: '验证码已发送',
            icon: 'success'
          });
          
          // 开始倒计时
          this.countdownTimer = setInterval(() => {
            if (this.countdown <= 1) {
              clearInterval(this.countdownTimer);
              this.isCountingDown = false;
              this.countdown = 60;
            } else {
              this.countdown--;
            }
          }, 1000);
        } else {
          throw new Error(res[1].data.message || '验证码发送失败');
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
        uni.showToast({
          title: error.message || '验证码发送失败',
          icon: 'none'
        });
        this.isCountingDown = false;
      }
    },
    
    // 验证身份
    async verifyIdentity() {
      if (!this.form.mobile || !this.form.code) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        });
        return;
      }
      
      this.isLoading = true;
      
      try {
        // 调用验证接口
        const res = await uni.request({
          url: '/api/auth/verify-reset',
          method: 'POST',
          data: {
            mobile: this.form.mobile,
            code: this.form.code
          }
        });
        
        if (res[1].statusCode === 200) {
          this.currentStep = 2;
        } else {
          throw new Error(res[1].data.message || '验证失败');
        }
      } catch (error) {
        console.error('验证失败:', error);
        uni.showToast({
          title: error.message || '验证失败',
          icon: 'none'
        });
      } finally {
        this.isLoading = false;
      }
    },
    
    // 重置密码
    async resetPassword() {
      if (!this.form.newPassword || !this.form.confirmPassword) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        });
        return;
      }
      
      if (this.form.newPassword !== this.form.confirmPassword) {
        uni.showToast({
          title: '两次输入密码不一致',
          icon: 'none'
        });
        return;
      }
      
      if (this.form.newPassword.length < 6 || this.form.newPassword.length > 20) {
        uni.showToast({
          title: '密码长度为6-20位',
          icon: 'none'
        });
        return;
      }
      
      this.isLoading = true;
      
      try {
        // 调用重置密码接口
        const res = await uni.request({
          url: '/api/auth/reset-password',
          method: 'POST',
          data: {
            mobile: this.form.mobile,
            code: this.form.code,
            new_password: this.form.newPassword
          }
        });
        
        if (res[1].statusCode === 200) {
          uni.showToast({
            title: '密码重置成功',
            icon: 'success'
          });
          
          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          throw new Error(res[1].data.message || '密码重置失败');
        }
      } catch (error) {
        console.error('密码重置失败:', error);
        uni.showToast({
          title: error.message || '密码重置失败',
          icon: 'none'
        });
      } finally {
        this.isLoading = false;
      }
    },
    
    // 返回上一页
    navigateBack() {
      if (this.currentStep === 1) {
        uni.navigateBack();
      } else {
        this.currentStep = 1;
      }
    }
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer);
    }
  }
}
</script>

<style scoped>
.forgot-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.steps {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx 0;
  background-color: #fff;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.step-num {
  width: 50rpx;
  height: 50rpx;
  line-height: 50rpx;
  text-align: center;
  border-radius: 50%;
  background-color: #ccc;
  color: #fff;
  font-size: 28rpx;
  margin-bottom: 10rpx;
}

.step-item.active .step-num {
  background-color: #1890ff;
}

.step-text {
  font-size: 26rpx;
  color: #666;
}

.step-item.active .step-text {
  color: #1890ff;
}

.step-line {
  width: 100rpx;
  height: 4rpx;
  background-color: #ccc;
  margin: 0 20rpx;
}

.step-line.active {
  background-color: #1890ff;
}

.step-content {
  padding: 40rpx;
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
  z-index: 1;
}

.input-field {
  width: 100%;
  height: 90rpx;
  padding: 0 80rpx 0 60rpx;
  border-radius: 45rpx;
  background-color: #fff;
  border: 1rpx solid #eee;
  font-size: 28rpx;
  box-sizing: border-box;
}

.code-btn {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  height: 60rpx;
  line-height: 60rpx;
  padding: 0 20rpx;
  background-color: #f5f7fa;
  color: #1890ff;
  font-size: 24rpx;
  border-radius: 30rpx;
  border: none;
}

.code-btn[disabled] {
  color: #999;
  background-color: #eee;
}

.next-btn,
.submit-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: linear-gradient(90deg, #1890ff, #36cfc9);
  color: #fff;
  border-radius: 45rpx;
  font-size: 32rpx;
  border: none;
}

.submit-btn.loading {
  opacity: 0.8;
}
</style>