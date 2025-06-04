<template>
  <view class="borrow-container">
    <!-- 顶部导航 -->
    <uni-nav-bar 
      :fixed="true" 
      title="图书借阅/归还"
      left-icon="left"
      @clickLeft="goBack"
    />
    
    <!-- 借阅表单 -->
    <view class="form-section">
      <view class="form-title">图书借阅</view>
      <view class="form-item">
        <text class="label">借阅者编号:</text>
        <input class="input" v-model="borrowForm.borrowerNumber" placeholder="请输入借阅者编号"/>
      </view>
      <view class="form-item">
        <text class="label">图书编码:</text>
        <input class="input" v-model="borrowForm.bookCode" placeholder="请输入图书编码"/>
      </view>
      <button class="submit-btn" @click="handleBorrow">借阅</button>
    </view>
    
    <!-- 归还表单 -->
    <view class="form-section">
      <view class="form-title">图书归还</view>
      <view class="form-item">
        <text class="label">事务编号:</text>
        <input class="input" v-model="returnForm.transactionNumber" placeholder="请输入事务编号"/>
      </view>
      <button class="submit-btn return-btn" @click="handleReturn">归还</button>
    </view>
    
    <!-- 借阅记录 -->
    <view class="record-section">
      <view class="section-header">
        <text class="section-title">我的借阅记录</text>
        <uni-icons 
          type="refresh" 
          size="20" 
          color="#1890ff"
          @click="loadRecords"
        />
      </view>
      
      <scroll-view scroll-y class="record-list">
        <view 
          v-for="record in records" 
          :key="record.transactionNumber"
          class="record-item"
          :class="{'overdue': record.status === 'OVERDUE'}"
        >
          <view class="record-header">
            <text class="book-title">{{record.bookTitle}}</text>
            <text class="status-badge" :class="record.status.toLowerCase()">
              {{formatStatus(record.status)}}
            </text>
          </view>
          <view class="record-meta">
            <text>ISBN: {{record.isbn}}</text>
            <text>图书编码: {{record.bookCode}}</text>
          </view>
          <view class="record-dates">
            <text>借阅日期: {{formatDate(record.borrowDate)}}</text>
            <text>应还日期: {{formatDate(record.dueDate)}}</text>
          </view>
          <text class="transaction-number">事务编号: {{record.transactionNumber}}</text>
        </view>
        
        <view class="empty-tip" v-if="records.length === 0">
          <uni-icons type="info" size="24" color="#999"/>
          <text>暂无借阅记录</text>
        </view>
      </scroll-view>
    </view>
  </view>
 
  
  
</template>

<script>


export default {
  data() {
    return {
      socket: null,
      borrowForm: {
        borrowerNumber: '',
        bookCode: ''
      },
      returnForm: {
        transactionNumber: ''
      },
      records: []
    }
  },
  onLoad() {
    this.initSocket();
    this.loadRecords();
  },
  onUnload() {
    if(this.socket) {
      this.socket.disconnect();
    }
  },
  methods: {

    initSocket() {
      this.socket = io('http://your-server-ip:8080', {
        path: '/library-socket',
        reconnection: true
      });
      
      this.socket.on('connect', () => {
        console.log('Socket connected');
      });
      
      this.socket.on('borrow-result', (data) => {
        uni.showToast({
          title: data.success ? '借阅成功' : `借阅失败: ${data.message}`,
          icon: data.success ? 'success' : 'none'
        });
        if(data.success) {
          this.loadRecords();
        }
      });
      
      this.socket.on('return-result', (data) => {
        uni.showToast({
          title: data.success ? '归还成功' : `归还失败: ${data.message}`,
          icon: data.success ? 'success' : 'none'
        });
        if(data.success) {
          this.loadRecords();
        }
      });
    },
    
    handleBorrow() {
      if(!this.borrowForm.borrowerNumber || !this.borrowForm.bookCode) {
        uni.showToast({
          title: '请填写完整信息',
          icon: 'none'
        });
        return;
      }
      
      this.socket.emit('borrow-book', {
        borrowerNumber: this.borrowForm.borrowerNumber,
        bookCode: this.borrowForm.bookCode
      });
    },
    
    handleReturn() {
      if(!this.returnForm.transactionNumber) {
        uni.showToast({
          title: '请输入事务编号',
          icon: 'none'
        });
        return;
      }
      
      this.socket.emit('return-book', {
        transactionNumber: this.returnForm.transactionNumber
      });
    },
    
    loadRecords() {
      const borrowerNumber = uni.getStorageSync('userInfo')?.borrowerNumber;
      if(!borrowerNumber) return;
      
      this.socket.emit('get-records', { borrowerNumber }, (data) => {
        if(data.success) {
          this.records = data.records;
        } else {
          uni.showToast({
            title: '获取记录失败',
            icon: 'none'
          });
        }
      });
    },
    
    formatStatus(status) {
      const map = {
        'BORROWED': '借阅中',
        'RETURNED': '已归还',
        'OVERDUE': '逾期'
      };
      return map[status] || status;
    },
    
    formatDate(dateStr) {
      return formatDate(dateStr, 'YYYY-MM-DD');
    },
    
    goBack() {
      uni.navigateBack();
    }
  }
}
</script>

<style scoped>
.borrow-container {
  padding-bottom: 20rpx;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.form-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.form-title {
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 30rpx;
  color: #333;
  border-left: 6rpx solid #1890ff;
  padding-left: 15rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 15rpx;
}

.input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border: 1rpx solid #e5e5e5;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  font-size: 32rpx;
  color: #fff;
  background-color: #1890ff;
  border-radius: 45rpx;
  margin-top: 20rpx;
}

.return-btn {
  background-color: #52c41a;
}

.record-section {
  background-color: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.record-list {
  max-height: 600rpx;
}

.record-item {
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
  margin-bottom: 20rpx;
}

.record-item.overdue {
  background-color: #fff2f0;
  border-radius: 8rpx;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.book-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
}

.status-badge {
  padding: 5rpx 15rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-badge.borrowed {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1rpx solid #91d5ff;
}

.status-badge.returned {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1rpx solid #b7eb8f;
}

.status-badge.overdue {
  background-color: #fff2f0;
  color: #f5222d;
  border: 1rpx solid #ffa39e;
}

.record-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15rpx;
  font-size: 26rpx;
  color: #666;
}

.record-dates {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15rpx;
  font-size: 26rpx;
  color: #666;
}

.transaction-number {
  font-size: 24rpx;
  color: #999;
  word-break: break-all;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0;
  color: #999;
}

.empty-tip text {
  margin-top: 20rpx;
}
	
</style>