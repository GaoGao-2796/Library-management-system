<template>
  <view class="agreement-container">
    <!-- 顶部导航 -->
    <uni-nav-bar 
      :title="navTitle"
      left-icon="left"
      @clickLeft="navigateBack"
    />

    <!-- 协议内容 -->
    <scroll-view scroll-y class="content-container">
      <view class="content">
        <rich-text :nodes="agreementContent"></rich-text>
      </view>
    </scroll-view>

    <!-- 同意按钮 -->
    <view class="footer" v-if="showAgreeBtn">
      <button class="agree-btn" @click="handleAgree">同意并继续</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      agreementType: 'user', // user/privacy
      agreementContent: '',
      showAgreeBtn: false
    }
  },
  computed: {
    navTitle() {
      return this.agreementType === 'user' ? '用户协议' : '隐私政策';
    }
  },
  onLoad(options) {
    this.agreementType = options.type || 'user';
    this.showAgreeBtn = options.from === 'register';
    this.loadAgreementContent();
  },
  methods: {
    // 加载协议内容
    async loadAgreementContent() {
      try {
        uni.showLoading({ title: '加载中...' });
        
        const res = await uni.request({
          url: `/api/system/agreement/${this.agreementType}`
        });
        
        if (res[1].statusCode === 200) {
          this.agreementContent = res[1].data.content;
        } else {
          throw new Error(res[1].data.message || '加载失败');
        }
      } catch (error) {
        console.error('加载协议失败:', error);
        uni.showToast({
          title: '加载协议内容失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 处理同意协议
    handleAgree() {
      uni.$emit('agreement-agreed', this.agreementType);
      uni.navigateBack();
    },
    
    // 返回上一页
    navigateBack() {
      uni.navigateBack();
    }
  }
}
</script>

<style scoped>
.agreement-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #fff;
}

.content-container {
  flex: 1;
  padding: 30rpx;
}

.content {
  font-size: 28rpx;
  line-height: 1.6;
  color: #333;
}

.footer {
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #eee;
  background-color: #fff;
}

.agree-btn {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background: linear-gradient(90deg, #1890ff, #36cfc9);
  color: #fff;
  border-radius: 40rpx;
  font-size: 30rpx;
  border: none;
}
</style>