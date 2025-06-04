<template>
  <view class="register-container">
   
    
    <view class="form-container">
	  <!--  借阅者ID --> 
	  <view class="input-group">
	    <uni-icons type="person" size="20" color="#1890ff"></uni-icons>
	    <input 
	      class="input-field" 
	      type="text" 
	      v-model="form.borrowerId" 
	      placeholder="请输入借阅证ID"
	      maxlength="20"
	      @focus="resetError('borrowerId')"
	    />
	    <text class="error-msg">{{ errors.borrowerId }}</text>
	  </view>
      <!-- 借阅证编号 -->
      <view class="input-group">
        <uni-icons type="person" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="text" 
          v-model="form.borrowerId" 
          placeholder="请输入借阅证编号"
          maxlength="20"
          @focus="resetError('borrowerId')"
        />
        <text class="error-msg">{{ errors.borrowerId }}</text>
      </view>

      <!-- 姓名 -->
      <view class="input-group">
        <uni-icons type="person" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="text" 
          v-model="form.name" 
          placeholder="请输入姓名"
          maxlength="50"
          @focus="resetError('name')"
        />
        <text class="error-msg">{{ errors.name }}</text>
      </view>

      <!-- 性别 -->
      <view class="input-group">
        <uni-icons type="person" size="20" color="#1890ff"></uni-icons>
        <picker 
          class="input-field" 
          mode="selector" 
          :range="genderOptions" 
          @change="onGenderChange"
        >
          <view class="picker-content">
            {{ form.gender || '请选择性别' }}
          </view>
        </picker>
        <text class="error-msg">{{ errors.gender }}</text>
      </view>

      <!-- 年龄 -->
      <view class="input-group">
        <uni-icons type="calendar" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="number" 
          v-model="form.age" 
          placeholder="请输入年龄"
          maxlength="3"
          @focus="resetError('age')"
        />
        <text class="error-msg">{{ errors.age }}</text>
      </view>

      <!-- 密码 -->
      <view class="input-group">
        <uni-icons type="locked" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="password" 
          v-model="form.password" 
          placeholder="请设置登录密码(6-20位)"
          @focus="resetError('password')"
        />
        <text class="error-msg">{{ errors.password }}</text>
      </view>

      <!-- 确认密码 -->
      <view class="input-group">
        <uni-icons type="locked" size="20" color="#1890ff"></uni-icons>
        <input 
          class="input-field" 
          type="password" 
          v-model="form.confirmPassword" 
          placeholder="请再次输入密码"
          @focus="resetError('confirmPassword')"
        />
        <text class="error-msg">{{ errors.confirmPassword }}</text>
      </view>

      <!-- 注册按钮 -->
      <button class="register-btn" @click="handleRegister">立即注册</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      form: {
        borrowerId: '',
        name: '',
        gender: '',
        age: '',
        password: '',
        confirmPassword: ''
      },
      errors: {
        borrowerId: '',
        name: '',
        gender: '',
        age: '',
        password: '',
        confirmPassword: ''
      },
      genderOptions: ['男', '女']
    }
  },
  methods: {
    // 表单验证
    validateForm() {
      let isValid = true;
      
      // 借阅证编号验证
      if (!this.form.borrowerId) {
        this.errors.borrowerId = '请输入借阅证编号';
        isValid = false;
      } else if (this.form.borrowerId.length < 6) {
        this.errors.borrowerId = '借阅证编号至少6位';
        isValid = false;
      }
      
      // 姓名验证
      if (!this.form.name) {
        this.errors.name = '请输入姓名';
        isValid = false;
      }
      
      // 性别验证
      if (!this.form.gender) {
        this.errors.gender = '请选择性别';
        isValid = false;
      }
      
      // 年龄验证
      if (!this.form.age) {
        this.errors.age = '请输入年龄';
        isValid = false;
      } else if (this.form.age < 1 || this.form.age > 120) {
        this.errors.age = '请输入有效年龄(1-120)';
        isValid = false;
      }
      
      // 密码验证
      if (!this.form.password) {
        this.errors.password = '请输入密码';
        isValid = false;
      } else if (this.form.password.length < 6) {
        this.errors.password = '密码至少6位';
        isValid = false;
      }
      
      // 确认密码验证
      if (this.form.password !== this.form.confirmPassword) {
        this.errors.confirmPassword = '两次输入密码不一致';
        isValid = false;
      }
      
      return isValid;
    },
    
    // 处理注册
    handleRegister() {
      if (!this.validateForm()) return;
      
      // 从本地存储获取现有用户数据
      const borrowers = JSON.parse(uni.getStorageSync('borrowers') || '[]');
      
      // 唯一性校验
      const exists = borrowers.some(user => user.borrowerId === this.form.borrowerId);
      if (exists) {
        uni.showToast({ title: '该借阅证编号已注册', icon: 'none' });
        return;
      }
      
      // 创建新用户对象
      const newUser = {
        borrowerId: this.form.borrowerId,
        name: this.form.name,
        gender: this.form.gender,
        age: parseInt(this.form.age),
        password: this.form.password, // 注意：实际应用中密码应该加密存储
        registrationDate: new Date().toISOString().split('T')[0]
      };
      
      // 添加到用户数组
      borrowers.push(newUser);
      
      // 保存回本地存储
      uni.setStorageSync('borrowers', JSON.stringify(borrowers));
      
      // 注册成功提示
      uni.showToast({ title: '注册成功', icon: 'success' });
      
      // 跳转到登录页
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/login' });
      }, 1500);
    },
    
    // 其他辅助方法
    resetError(field) {
      this.errors[field] = '';
    },
    
    onGenderChange(e) {
      this.form.gender = this.genderOptions[e.detail.value];
      this.errors.gender = '';
    },
    
    navigateBack() {
      uni.navigateBack();
    }
  }
}
</script>

<style scoped>
/* 保持原有的样式不变 */
.register-container {
  padding: 60rpx;
  
}

.input-group {
  margin-bottom: 30rpx;
  position: relative;
}

.input-field {
  width: 100%;
  height: 80rpx;
  padding-left: 80rpx;
  border: 1rpx solid #eee;
  border-radius: 40rpx;
}

.error-msg {
  color: #f56c6c;
  font-size: 24rpx;
  margin-top: 10rpx;
  display: block;
  padding-left: 20rpx;
}

.register-btn {
  background-color: #1890ff;
  color: white;
  border-radius: 40rpx;
  height: 80rpx;
  line-height: 80rpx;
  margin-top: 40rpx;
}

.picker-content {
  height: 80rpx;
  line-height: 80rpx;
  padding-left: 80rpx;
  color: #333;
}
</style>