<template>
  <view class="book-manager-container">
    <!-- 顶部管理导航 -->
    <uni-nav-bar 
      :fixed="true"
      left-icon="left"
      title="图书管理"
      @clickLeft="handleBack"
    >
      <template #right>
        <uni-icons 
          type="plus" 
          size="24" 
          color="#1890ff"
          @click="showAddDialog"
        />
      </template>
    </uni-nav-bar>

    <!-- 搜索和筛选区域 -->
    <view class="search-filter-bar">
      <uni-search-bar 
        v-model="searchKeyword"
        placeholder="搜索图书名称/ISBN"
        @confirm="loadBookList"
        @clear="clearSearch"
        radius="100"
        clear-button="auto"
      />
      
      <view class="filter-row">
        <picker 
          class="filter-picker"
          mode="selector" 
          :range="filterOptions.status" 
          range-key="label"
          @change="onStatusChange"
        >
          <view class="picker-content">
            <text>{{ selectedStatus.label || '全部状态' }}</text>
            <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
          </view>
        </picker>
        
        <picker 
          class="filter-picker"
          mode="selector" 
          :range="filterOptions.categories" 
          range-key="label"
          @change="onCategoryChange"
        >
          <view class="picker-content">
            <text>{{ selectedCategory.label || '全部分类' }}</text>
            <uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
          </view>
        </picker>
      </view>
    </view>

    <!-- 图书列表表格 -->
    <scroll-view 
      scroll-y 
      class="book-list-container"
      :style="{ height: scrollViewHeight }"
    >
      <view class="book-list-header">
        <text class="header-cell" style="width: 10%;">封面</text>
        <text class="header-cell" style="width: 20%;">书名</text>
        <text class="header-cell" style="width: 15%;">作者</text>
        <text class="header-cell" style="width: 10%;">库存</text>
        <text class="header-cell" style="width: 10%;">价格</text>
        <text class="header-cell" style="width: 15%;">状态</text>
        <text class="header-cell" style="width: 20%;">操作</text>
      </view>
      
      <view 
        class="book-list-row"
        v-for="book in bookList"
        :key="book.id"
        @click="showBookDetail(book)"
      >
        <view class="row-cell" style="width: 10%;">
          <image 
            :src="book.cover || '/static/default-book.png'" 
            class="book-cover"
            mode="aspectFit"
          />
        </view>
        <view class="row-cell" style="width: 20%;">{{ book.title }}</view>
        <view class="row-cell" style="width: 15%;">{{ book.author }}</view>
        <view class="row-cell" style="width: 10%;">{{ book.stock }}</view>
        <view class="row-cell" style="width: 10%;">¥{{ book.price }}</view>
        <view class="row-cell" style="width: 15%;">
          <text :class="['status-badge', book.status]">
            {{ book.status | statusText }}
          </text>
        </view>
        <view class="row-cell actions" style="width: 20%;">
          <uni-icons 
            type="compose" 
            size="20" 
            color="#1890ff"
            @click.stop="editBook(book)"
          />
          <uni-icons 
            type="trash" 
            size="20" 
            color="#f56c6c"
            @click.stop="confirmDelete(book.id)"
          />
        </view>
      </view>
      
      <!-- 空状态 -->
      <view class="empty-placeholder" v-if="bookList.length === 0">
        <uni-icons type="info" size="50" color="#999"></uni-icons>
        <text>暂无图书数据</text>
      </view>
      
      <!-- 分页控件 -->
      <view class="pagination" v-if="bookList.length > 0">
        <button 
          :disabled="currentPage === 1"
          @click="prevPage"
        >
          上一页
        </button>
        <text class="page-info">{{ currentPage }}/{{ totalPages }}</text>
        <button 
          :disabled="currentPage === totalPages"
          @click="nextPage"
        >
          下一页
        </button>
      </view>
    </scroll-view>

    <!-- 图书详情抽屉 -->
    <uni-drawer 
      ref="detailDrawer"
      mode="right"
      :width="700"
      @change="onDrawerChange"
    >
      <view class="drawer-content" v-if="currentBook">
        <!-- 详情头部 -->
        <view class="detail-header">
          <text class="detail-title">图书详情</text>
          <uni-icons 
            type="close" 
            size="24" 
            color="#999"
            @click="closeDetail"
          />
        </view>
        
        <!-- 详情内容 -->
        <scroll-view scroll-y class="detail-body">
          <view class="detail-section">
            <image 
              :src="currentBook.cover || '/static/default-book.png'"
              class="detail-cover"
              mode="aspectFit"
            />
            
            <view class="basic-info">
              <text class="book-title">{{ currentBook.title }}</text>
              <text class="book-meta">ISBN: {{ currentBook.isbn }}</text>
              <text class="book-meta">作者: {{ currentBook.author }}</text>
              <text class="book-meta">出版社: {{ currentBook.publisher }}</text>
              <text class="book-meta">出版日期: {{ currentBook.publish_date }}</text>
              
              <view class="price-stock">
                <text class="price">¥{{ currentBook.price }}</text>
                <text class="stock">库存: {{ currentBook.stock }}本</text>
              </view>
              
              <text class="status" :class="currentBook.status">
                {{ currentBook.status | statusText }}
              </text>
            </view>
          </view>
          
          <view class="detail-section">
            <view class="section-title">图书简介</view>
            <text class="book-description">
              {{ currentBook.description || '暂无简介' }}
            </text>
          </view>
          
          <view class="detail-section">
            <view class="section-title">详细信息</view>
            <view class="info-grid">
              <view class="info-item">
                <text class="info-label">分类:</text>
                <text class="info-value">{{ currentBook.category_name }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">页数:</text>
                <text class="info-value">{{ currentBook.pages }}页</text>
              </view>
              <view class="info-item">
                <text class="info-label">语言:</text>
                <text class="info-value">{{ currentBook.language }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">点击量:</text>
                <text class="info-value">{{ currentBook.click_count }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">上架时间:</text>
                <text class="info-value">{{ currentBook.create_time }}</text>
              </view>
              <view class="info-item">
                <text class="info-label">最后更新:</text>
                <text class="info-value">{{ currentBook.update_time }}</text>
              </view>
            </view>
          </view>
        </scroll-view>
        
        <!-- 操作按钮 -->
        <view class="detail-footer">
          <button 
            class="action-btn status-btn"
            :class="{ 'offline': currentBook.status === 'online' }"
            @click="toggleBookStatus"
          >
            {{ currentBook.status === 'online' ? '下架图书' : '上架图书' }}
          </button>
          
          <button 
            class="action-btn edit-btn"
            @click="editCurrentBook"
          >
            编辑图书
          </button>
          
          <button 
            class="action-btn delete-btn"
            @click="confirmDelete(currentBook.id)"
          >
            删除图书
          </button>
        </view>
      </view>
    </uni-drawer>

    <!-- 新增/编辑弹窗 -->
    <uni-popup ref="bookDialog" type="dialog">
      <uni-popup-dialog 
        :title="dialogTitle"
        mode="base"
        @confirm="submitBookForm"
        @close="closeDialog"
      >
        <view class="dialog-form">
          <view class="form-item">
            <text class="form-label">图书名称:</text>
            <input 
              class="form-input"
              v-model="bookForm.title"
              placeholder="请输入图书名称"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">ISBN编号:</text>
            <input 
              class="form-input"
              v-model="bookForm.isbn"
              placeholder="请输入ISBN编号"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">图书分类:</text>
            <picker 
              mode="selector"
              :range="filterOptions.categories"
              range-key="label"
              @change="onFormCategoryChange"
            >
              <view class="form-input">
                {{ bookForm.category_name || '请选择分类' }}
              </view>
            </picker>
          </view>
          
          <view class="form-item">
            <text class="form-label">作者:</text>
            <input 
              class="form-input"
              v-model="bookForm.author"
              placeholder="请输入作者"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">出版社:</text>
            <input 
              class="form-input"
              v-model="bookForm.publisher"
              placeholder="请输入出版社"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">价格:</text>
            <input 
              class="form-input"
              type="number"
              v-model="bookForm.price"
              placeholder="请输入价格"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">库存:</text>
            <input 
              class="form-input"
              type="number"
              v-model="bookForm.stock"
              placeholder="请输入库存数量"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">图书封面:</text>
            <button 
              class="upload-btn"
              @click="chooseImage"
            >
              选择图片
            </button>
            <image 
              v-if="bookForm.cover"
              :src="bookForm.cover"
              class="cover-preview"
              mode="aspectFit"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">图书简介:</text>
            <textarea 
              class="form-textarea"
              v-model="bookForm.description"
              placeholder="请输入图书简介"
            />
          </view>
        </view>
      </uni-popup-dialog>
    </uni-popup>
    
    <!-- 删除确认弹窗 -->
    <uni-popup ref="deleteDialog" type="dialog">
      <uni-popup-dialog 
        type="warn"
        title="确认删除"
        :content="`确认删除这本图书吗？此操作不可恢复！`"
        @confirm="deleteBook"
        @close="closeDeleteDialog"
      />
    </uni-popup>
  </view>
</template>

<script>
export default {
  filters: {
    statusText(status) {
      const map = {
        'online': '在售',
        'offline': '下架',
        'soldout': '缺货'
      };
      return map[status] || '未知';
    }
  },
  data() {
    return {
      scrollViewHeight: 'calc(100vh - 320rpx)',
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      bookList: [],
      currentBook: null,
      isDrawerOpen: false,
      filterOptions: {
        status: [
          { label: '全部状态', value: '' },
          { label: '在售', value: 'online' },
          { label: '下架', value: 'offline' },
          { label: '缺货', value: 'soldout' }
        ],
        categories: [
          { label: '文学', value: 'literature' },
          { label: '科技', value: 'technology' },
          { label: '历史', value: 'history' },
          { label: '小说', value: 'fiction' }
        ]
      },
      selectedStatus: { label: '全部状态', value: '' },
      selectedCategory: { label: '全部分类', value: '' },
      dialogTitle: '新增图书',
      bookForm: {
        id: null,
        title: '',
        isbn: '',
        category_id: null,
        category_name: '',
        author: '',
        publisher: '',
        price: '',
        stock: '',
        cover: '',
        description: '',
        status: 'online'
      },
      deleteId: null
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize);
    }
  },
  onLoad() {
    this.calculateScrollHeight();
    this.loadBookList();
  },
  onReady() {
    // 监听窗口变化调整滚动区域高度
    uni.onWindowResize(() => {
      this.calculateScrollHeight();
    });
  },
  onUnload() {
    uni.offWindowResize();
  },
  methods: {
    // 计算滚动区域高度
    calculateScrollHeight() {
      const query = uni.createSelectorQuery().in(this);
      query.select('.search-filter-bar').boundingClientRect(data => {
        const systemInfo = uni.getSystemInfoSync();
        const navBarHeight = 44; // 导航栏高度
        const statusBarHeight = systemInfo.statusBarHeight || 20;
        const barHeight = data.height + navBarHeight + statusBarHeight + 10;
        this.scrollViewHeight = `calc(100vh - ${barHeight}px)`;
      }).exec();
    },
    
    // 加载图书列表
    async loadBookList() {
      try {
        uni.showLoading({ title: '加载中...' });
        
        const params = {
          page: this.currentPage,
          page_size: this.pageSize,
          keyword: this.searchKeyword,
          status: this.selectedStatus.value,
          category: this.selectedCategory.value
        };
        
        const res = await uni.request({
          url: '/api/books',
          method: 'GET',
          data: params
        });
        
        if (res[1].statusCode === 200) {
          this.bookList = res[1].data.list;
          this.total = res[1].data.total;
        } else {
          throw new Error(res[1].data.message || '加载失败');
        }
      } catch (error) {
        console.error('加载图书列表失败:', error);
        uni.showToast({
          title: '加载数据失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 显示图书详情
    showBookDetail(book) {
      this.currentBook = { ...book };
      this.$refs.detailDrawer.open();
      
      // 更新点击量
      this.updateClickCount(book.id);
    },
    
    // 更新点击量
    async updateClickCount(bookId) {
      try {
        await uni.request({
          url: `/api/books/${bookId}/click`,
          method: 'PUT'
        });
      } catch (error) {
        console.error('更新点击量失败:', error);
      }
    },
    
    // 关闭详情抽屉
    closeDetail() {
      this.$refs.detailDrawer.close();
    },
    
    // 抽屉状态变化
    onDrawerChange(open) {
      this.isDrawerOpen = open;
    },
    
    // 状态筛选
    onStatusChange(e) {
      this.selectedStatus = this.filterOptions.status[e.detail.value];
      this.currentPage = 1;
      this.loadBookList();
    },
    
    // 分类筛选
    onCategoryChange(e) {
      this.selectedCategory = this.filterOptions.categories[e.detail.value];
      this.currentPage = 1;
      this.loadBookList();
    },
    
    // 表单分类选择
    onFormCategoryChange(e) {
      const selected = this.filterOptions.categories[e.detail.value];
      this.bookForm.category_id = selected.value;
      this.bookForm.category_name = selected.label;
    },
    
    // 清空搜索
    clearSearch() {
      this.searchKeyword = '';
      this.loadBookList();
    },
    
    // 分页控制
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.loadBookList();
      }
    },
    
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.loadBookList();
      }
    },
    
    // 显示新增弹窗
    showAddDialog() {
      this.dialogTitle = '新增图书';
      this.resetBookForm();
      this.$refs.bookDialog.open();
    },
    
    // 编辑图书
    editBook(book) {
      this.dialogTitle = '编辑图书';
      this.bookForm = { ...book };
      this.$refs.bookDialog.open();
    },
    
    // 编辑当前图书
    editCurrentBook() {
      this.editBook(this.currentBook);
      this.closeDetail();
    },
    
    // 重置表单
    resetBookForm() {
      this.bookForm = {
        id: null,
        title: '',
        isbn: '',
        category_id: null,
        category_name: '',
        author: '',
        publisher: '',
        price: '',
        stock: '',
        cover: '',
        description: '',
        status: 'online'
      };
    },
    
    // 关闭弹窗
    closeDialog() {
      this.$refs.bookDialog.close();
    },
    
    // 提交表单
    async submitBookForm() {
      if (!this.validateBookForm()) return;
      
      try {
        uni.showLoading({ title: '提交中...' });
        
        const url = this.bookForm.id ? 
          `/api/books/${this.bookForm.id}` : 
          '/api/books';
        
        const method = this.bookForm.id ? 'PUT' : 'POST';
        
        const res = await uni.request({
          url,
          method,
          data: this.bookForm
        });
        
        if (res[1].statusCode === 200) {
          uni.showToast({
            title: '操作成功',
            icon: 'success'
          });
          this.closeDialog();
          this.loadBookList();
          
          // 如果当前正在查看该书的详情，更新详情数据
          if (this.currentBook && this.currentBook.id === this.bookForm.id) {
            this.currentBook = { ...this.currentBook, ...this.bookForm };
          }
        } else {
          throw new Error(res[1].data.message || '操作失败');
        }
      } catch (error) {
        console.error('操作失败:', error);
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 表单验证
    validateBookForm() {
      if (!this.bookForm.title) {
        uni.showToast({ title: '请输入图书名称', icon: 'none' });
        return false;
      }
      
      if (!this.bookForm.isbn) {
        uni.showToast({ title: '请输入ISBN编号', icon: 'none' });
        return false;
      }
      
      if (!this.bookForm.category_id) {
        uni.showToast({ title: '请选择图书分类', icon: 'none' });
        return false;
      }
      
      if (!this.bookForm.author) {
        uni.showToast({ title: '请输入作者', icon: 'none' });
        return false;
      }
      
      if (!this.bookForm.price || isNaN(this.bookForm.price)) {
        uni.showToast({ title: '请输入有效的价格', icon: 'none' });
        return false;
      }
      
      if (!this.bookForm.stock || isNaN(this.bookForm.stock)) {
        uni.showToast({ title: '请输入有效的库存', icon: 'none' });
        return false;
      }
      
      return true;
    },
    
    // 选择图片
    async chooseImage() {
      try {
        const res = await uni.chooseImage({
          count: 1,
          sizeType: ['compressed'],
          sourceType: ['album', 'camera']
        });
        
        if (res.tempFilePaths.length > 0) {
          uni.showLoading({ title: '上传中...' });
          
          const uploadRes = await uni.uploadFile({
            url: '/api/upload',
            filePath: res.tempFilePaths[0],
            name: 'file',
            formData: {
              type: 'book_cover'
            }
          });
          
          const data = JSON.parse(uploadRes[1].data);
          if (data.code === 200) {
            this.bookForm.cover = data.data.url;
          } else {
            throw new Error(data.message || '上传失败');
          }
        }
      } catch (error) {
        console.error('选择图片失败:', error);
        uni.showToast({
          title: '上传图片失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 切换图书状态
    async toggleBookStatus() {
      if (!this.currentBook) return;
      
      try {
        uni.showLoading({ title: '处理中...' });
        
        const newStatus = this.currentBook.status === 'online' ? 'offline' : 'online';
        
        const res = await uni.request({
          url: `/api/books/${this.currentBook.id}/status`,
          method: 'PUT',
          data: { status: newStatus }
        });
        
        if (res[1].statusCode === 200) {
          this.currentBook.status = newStatus;
          
          // 更新列表中的状态
          const index = this.bookList.findIndex(b => b.id === this.currentBook.id);
          if (index !== -1) {
            this.bookList[index].status = newStatus;
          }
          
          uni.showToast({
            title: newStatus === 'online' ? '已上架' : '已下架',
            icon: 'success'
          });
        } else {
          throw new Error(res[1].data.message || '操作失败');
        }
      } catch (error) {
        console.error('切换状态失败:', error);
        uni.showToast({
          title: error.message || '操作失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 确认删除
    confirmDelete(id) {
      this.deleteId = id;
      this.$refs.deleteDialog.open();
    },
    
    // 关闭删除确认
    closeDeleteDialog() {
      this.deleteId = null;
    },
    
    // 删除图书
    async deleteBook() {
      if (!this.deleteId) return;
      
      try {
        uni.showLoading({ title: '删除中...' });
        
        const res = await uni.request({
          url: `/api/books/${this.deleteId}`,
          method: 'DELETE'
        });
        
        if (res[1].statusCode === 200) {
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          });
          
          // 从列表中移除
          this.bookList = this.bookList.filter(b => b.id !== this.deleteId);
          this.total--;
          
          // 如果正在查看被删除的图书，关闭详情
          if (this.currentBook && this.currentBook.id === this.deleteId) {
            this.closeDetail();
            this.currentBook = null;
          }
          
          this.closeDeleteDialog();
        } else {
          throw new Error(res[1].data.message || '删除失败');
        }
      } catch (error) {
        console.error('删除失败:', error);
        uni.showToast({
          title: error.message || '删除失败',
          icon: 'none'
        });
      } finally {
        uni.hideLoading();
      }
    },
    
    // 返回上一页
    handleBack() {
      uni.switchTab({
      	url:'/pages/index/index'
      })
    }
  }
}
</script>

<style scoped>
.book-manager-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

.search-filter-bar {
  padding: 20rpx;
  background-color: #fff;
  border-bottom: 1rpx solid #eee;
}

.filter-row {
  display: flex;
  margin-top: 20rpx;
}

.filter-picker {
  flex: 1;
  margin-right: 20rpx;
}

.filter-picker:last-child {
  margin-right: 0;
}

.picker-content {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 70rpx;
  background-color: #f5f7fa;
  border-radius: 35rpx;
  font-size: 26rpx;
  color: #333;
}

.picker-content text {
  margin-right: 10rpx;
}

.book-list-container {
  background-color: #fff;
}

.book-list-header {
  display: flex;
  padding: 20rpx;
  background-color: #fafafa;
  border-bottom: 1rpx solid #eee;
}

.header-cell {
  font-size: 26rpx;
  color: #666;
  text-align: center;
}

.book-list-row {
  display: flex;
  padding: 25rpx 20rpx;
  border-bottom: 1rpx solid #eee;
}

.book-list-row:active {
  background-color: #f5f5f5;
}

.row-cell {
  font-size: 28rpx;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  word-break: break-all;
}

.actions {
  display: flex;
  justify-content: space-around;
}

.actions uni-icons {
  padding: 10rpx;
}

.book-cover {
  width: 80rpx;
  height: 100rpx;
}

.status-badge {
  padding: 5rpx 15rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-badge.online {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1rpx solid #91d5ff;
}

.status-badge.offline {
  background-color: #fff2f0;
  color: #f5222d;
  border: 1rpx solid #ffa39e;
}

.status-badge.soldout {
  background-color: #fffbe6;
  color: #faad14;
  border: 1rpx solid #ffe58f;
}

.empty-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100rpx 0;
  color: #999;
}

.empty-placeholder text {
  margin-top: 20rpx;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30rpx;
}

.pagination button {
  margin: 0 20rpx;
  padding: 0 30rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  border-radius: 30rpx;
  background-color: #f5f5f5;
  color: #666;
  border: none;
}

.pagination button[disabled] {
  opacity: 0.5;
}

.page-info {
  font-size: 26rpx;
  color: #666;
}

/* 详情抽屉样式 */
.drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #eee;
}

.detail-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.detail-body {
  flex: 1;
  padding: 30rpx;
}

.detail-section {
  margin-bottom: 40rpx;
}

.detail-cover {
  width: 200rpx;
  height: 280rpx;
  margin-right: 30rpx;
  float: left;
}

.basic-info {
  overflow: hidden;
}

.book-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 15rpx;
  display: block;
}

.book-meta {
  font-size: 26rpx;
  color: #666;
  margin-bottom: 10rpx;
  display: block;
}

.price-stock {
  display: flex;
  align-items: center;
  margin: 20rpx 0;
}

.price {
  font-size: 36rpx;
  color: #f5222d;
  margin-right: 30rpx;
}

.stock {
  font-size: 26rpx;
  color: #666;
}

.status {
  display: inline-block;
  padding: 5rpx 15rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status.online {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1rpx solid #91d5ff;
}

.status.offline {
  background-color: #fff2f0;
  color: #f5222d;
  border: 1rpx solid #ffa39e;
}

.section-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
  border-left: 6rpx solid #1890ff;
  padding-left: 15rpx;
}

.book-description {
  font-size: 26rpx;
  color: #666;
  line-height: 1.6;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}

.info-item {
  display: flex;
}

.info-label {
  width: 120rpx;
  font-size: 26rpx;
  color: #999;
}

.info-value {
  flex: 1;
  font-size: 26rpx;
  color: #333;
}

.detail-footer {
  display: flex;
  padding: 20rpx;
  border-top: 1rpx solid #eee;
}

.action-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 28rpx;
  border-radius: 40rpx;
  margin: 0 10rpx;
  border: none;
}

.status-btn {
  background-color: #f5f5f5;
  color: #666;
}

.status-btn.offline {
  background-color: #1890ff;
  color: #fff;
}

.edit-btn {
  background-color: #1890ff;
  color: #fff;
}

.delete-btn {
  background-color: #f5222d;
  color: #fff;
}

/* 表单弹窗样式 */
.dialog-form {
  padding: 20rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 15rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border-radius: 8rpx;
  background-color: #f5f7fa;
  border: 1rpx solid #eee;
  font-size: 28rpx;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  height: 200rpx;
  padding: 20rpx;
  border-radius: 8rpx;
  background-color: #f5f7fa;
  border: 1rpx solid #eee;
  font-size: 28rpx;
  box-sizing: border-box;
}

.upload-btn {
  width: 200rpx;
  height: 60rpx;
  line-height: 60rpx;
  font-size: 26rpx;
  background-color: #f5f7fa;
  color: #1890ff;
  border: 1rpx solid #1890ff;
  border-radius: 30rpx;
  margin-bottom: 15rpx;
}

.cover-preview {
  width: 150rpx;
  height: 200rpx;
  border-radius: 8rpx;
}
</style>