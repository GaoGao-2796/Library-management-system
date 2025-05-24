<template>
	<view class="container">
		<!-- 顶部导航栏 -->
		<view class="header">
			<view class="admin-info">
				<text>管理员 {{adminName}}</text>
				<text class="logout" @click="handleLogout">退出管理台</text>
			</view>
			<view class="breadcrumb">
				<text>首页 / 图书管理</text>
			</view>
		</view>

		<uni-section title="实心标签" type="line" padding>
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

		<!-- 搜索和筛选区域 -->
		<view class="search-filter">
			<view class="search-box">
				<input class="search-input" v-model="searchKeyword" placeholder="请输入图书名称" @confirm="handleSearch" />
				<button class="search-btn" @click="handleSearch">查询</button>
			</view>

			<view class="filter-box">
				<picker class="type-picker" mode="selector" :range="bookTypes" range-key="name" @change="onTypeChange">
					<view class="picker-text">
						{{selectedType ? selectedType.name : '请选择图书类型'}}
					</view>
				</picker>
			</view>

			<view class="action-btns">
				<button class="add-btn" @click="showAddDialog">+ 新增</button>
				<button class="delete-btn" @click="batchDelete">删除</button>
			</view>
		</view>

		<!-- 图书列表表格 -->
		<view class="book-table">
			<view class="table-header">
				<view class="header-cell" style="width: 5%;">索引</view>
				<view class="header-cell" style="width: 15%;">图书名称</view>
				<view class="header-cell" style="width: 10%;">图书图片</view>
				<view class="header-cell" style="width: 10%;">图书类型</view>
				<view class="header-cell" style="width: 8%;">图书库存</view>
				<view class="header-cell" style="width: 10%;">购买获得积分</view>
				<view class="header-cell" style="width: 8%;">图书原价</view>
				<view class="header-cell" style="width: 8%;">现价</view>
				<view class="header-cell" style="width: 8%;">点击次数</view>
				<view class="header-cell" style="width: 8%;">层层上限</view>
				<view class="header-cell" style="width: 10%;">操作</view>
			</view>

			<view class="table-body">
				<view class="table-row" v-for="(book, index) in bookList" :key="book.id">
					<view class="body-cell" style="width: 5%;">{{index + 1}}</view>
					<view class="body-cell" style="width: 15%;">{{book.name}}</view>
					<view class="body-cell" style="width: 10%;">
						<image class="book-image" :src="book.image || '/static/default-book.png'" mode="aspectFit">
						</image>
					</view>
					<view class="body-cell" style="width: 10%;">{{book.type_name}}</view>
					<view class="body-cell" style="width: 8%;">{{book.stock}}</view>
					<view class="body-cell" style="width: 10%;">{{book.points}}</view>
					<view class="body-cell" style="width: 8%;">{{book.original_price}}</view>
					<view class="body-cell" style="width: 8%;">{{book.current_price}}</view>
					<view class="body-cell" style="width: 8%;">{{book.click_count}}</view>
					<view class="body-cell" style="width: 8%;">{{book.layer_limit}}</view>
					<view class="body-cell action-cell" style="width: 10%;">
						<text class="action-btn" @click="toggleStatus(book)">{{book.status ? '上架' : '下架'}}</text>
						<text class="action-btn" @click="showDetail(book)">详情</text>
						<text class="action-btn" @click="editBook(book)">修改</text>
						<text class="action-btn" @click="deleteBook(book.id)">删除</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 分页控件 -->
		<view class="pagination">
			<text class="page-info">共 {{total}} 条记录</text>
			<view class="page-controls">
				<button :disabled="currentPage === 1" @click="prevPage">上一页</button>
				<text class="page-current">{{currentPage}}/{{totalPages}}</text>
				<button :disabled="currentPage === totalPages" @click="nextPage">下一页</button>
			</view>
		</view>

		<!-- 新增/编辑对话框 -->
		<uni-popup ref="bookDialog" type="dialog">
			<uni-popup-dialog mode="base" :title="dialogTitle" @confirm="confirmDialog">
				<view class="dialog-form">
					<view class="form-item">
						<text class="form-label">图书名称:</text>
						<input class="form-input" v-model="currentBook.name" placeholder="请输入图书名称" />
					</view>
					<view class="form-item">
						<text class="form-label">图书类型:</text>
						<picker mode="selector" :range="bookTypes" range-key="name" @change="onDialogTypeChange">
							<view class="form-input">{{currentBook.type_name || '请选择图书类型'}}</view>
						</picker>
					</view>
					<view class="form-item">
						<text class="form-label">图书库存:</text>
						<input class="form-input" type="number" v-model="currentBook.stock" placeholder="请输入库存数量" />
					</view>
					<view class="form-item">
						<text class="form-label">图书原价:</text>
						<input class="form-input" type="number" v-model="currentBook.original_price"
							placeholder="请输入原价" />
					</view>
					<view class="form-item">
						<text class="form-label">现价:</text>
						<input class="form-input" type="number" v-model="currentBook.current_price"
							placeholder="请输入现价" />
					</view>
					<view class="form-item">
						<text class="form-label">购买获得积分:</text>
						<input class="form-input" type="number" v-model="currentBook.points" placeholder="请输入购买获得积分" />
					</view>
					<view class="form-item" v-if="dialogTitle === '新增图书'">
						<text class="form-label">图书图片:</text>
						<button @click="chooseImage">选择图片</button>
						<image v-if="currentBook.image" :src="currentBook.image" class="preview-image"></image>
					</view>
				</view>
			</uni-popup-dialog>
		</uni-popup>

		<!-- 详情对话框 -->
		<uni-popup ref="detailDialog" type="dialog">
			<uni-popup-dialog mode="base" title="图书详情" @confirm="closeDetailDialog">
				<view class="detail-content" v-if="selectedBook">
					<image :src="selectedBook.image" class="detail-image"></image>
					<view class="detail-row">
						<text class="detail-label">图书名称:</text>
						<text class="detail-value">{{selectedBook.name}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">图书类型:</text>
						<text class="detail-value">{{selectedBook.type_name}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">库存数量:</text>
						<text class="detail-value">{{selectedBook.stock}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">原价:</text>
						<text class="detail-value">{{selectedBook.original_price}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">现价:</text>
						<text class="detail-value">{{selectedBook.current_price}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">购买积分:</text>
						<text class="detail-value">{{selectedBook.points}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">点击次数:</text>
						<text class="detail-value">{{selectedBook.click_count}}</text>
					</view>
					<view class="detail-row">
						<text class="detail-label">状态:</text>
						<text class="detail-value">{{selectedBook.status ? '上架' : '下架'}}</text>
					</view>
				</view>
			</uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				adminName: 'admin',
				searchKeyword: '',
				selectedType: null,
				bookTypes: [],
				bookList: [],
				currentPage: 1,
				pageSize: 10,
				total: 0,
				dialogTitle: '新增图书',
				currentBook: {
					id: null,
					name: '',
					type_id: null,
					type_name: '',
					stock: 0,
					points: 0,
					original_price: 0,
					current_price: 0,
					click_count: 0,
					layer_limit: 0,
					status: 1,
					image: ''
				},
				selectedBook: null,
				selectedIds: [],
				uploadUrl: 'https://your-api-domain.com/api/upload' // 文件上传接口
			}
		},
		computed: {
			totalPages() {
				return Math.ceil(this.total / this.pageSize);
			}
		},
		methods: {
			goToPageContent() {
				uni.navigateTo({
					url: '/pages/content/content'
				})
			},
			goToPageIndex() {
				uni.navigateTo({
					url: '/pages/index/index'
				})
			},
			goToPageLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				})
			},
			// 获取图书列表
			async fetchBooks() {
				try {
					uni.showLoading({
						title: '加载中...'
					});

					const params = {
						page: this.currentPage,
						page_size: this.pageSize,
						keyword: this.searchKeyword,
						type_id: this.selectedType ? this.selectedType.id : null
					};

					const res = await uni.request({
						url: 'https://your-api-domain.com/api/books',
						method: 'GET',
						data: params
					});

					if (res.data.code === 200) {
						this.bookList = res.data.data.list;
						this.total = res.data.data.total;
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('获取图书列表失败:', error);
					uni.showToast({
						title: '获取数据失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 获取图书类型
			async fetchBookTypes() {
				try {
					const res = await uni.request({
						url: 'https://your-api-domain.com/api/book-types',
						method: 'GET'
					});

					if (res.data.code === 200) {
						this.bookTypes = res.data.data;
					}
				} catch (error) {
					console.error('获取图书类型失败:', error);
				}
			},

			// 搜索图书
			handleSearch() {
				this.currentPage = 1;
				this.fetchBooks();
			},

			// 选择图书类型
			onTypeChange(e) {
				this.selectedType = this.bookTypes[e.detail.value];
				this.currentPage = 1;
				this.fetchBooks();
			},

			// 对话框中选择图书类型
			onDialogTypeChange(e) {
				const selected = this.bookTypes[e.detail.value];
				this.currentBook.type_id = selected.id;
				this.currentBook.type_name = selected.name;
			},

			// 分页控制
			prevPage() {
				if (this.currentPage > 1) {
					this.currentPage--;
					this.fetchBooks();
				}
			},

			nextPage() {
				if (this.currentPage < this.totalPages) {
					this.currentPage++;
					this.fetchBooks();
				}
			},

			// 显示新增对话框
			showAddDialog() {
				this.dialogTitle = '新增图书';
				this.currentBook = {
					id: null,
					name: '',
					type_id: null,
					type_name: '',
					stock: 0,
					points: 0,
					original_price: 0,
					current_price: 0,
					click_count: 0,
					layer_limit: 0,
					status: 1,
					image: ''
				};
				this.$refs.bookDialog.open();
			},

			// 编辑图书
			editBook(book) {
				this.dialogTitle = '编辑图书';
				this.currentBook = {
					...book
				};
				this.$refs.bookDialog.open();
			},

			// 确认对话框（新增/编辑）
			async confirmDialog() {
				if (!this.currentBook.name) {
					uni.showToast({
						title: '请输入图书名称',
						icon: 'none'
					});
					return;
				}

				try {
					uni.showLoading({
						title: '提交中...'
					});

					const url = this.currentBook.id ?
						`https://your-api-domain.com/api/books/${this.currentBook.id}` :
						'https://your-api-domain.com/api/books';

					const method = this.currentBook.id ? 'PUT' : 'POST';

					const res = await uni.request({
						url,
						method,
						data: this.currentBook
					});

					if (res.data.code === 200) {
						uni.showToast({
							title: '操作成功',
							icon: 'success'
						});
						this.$refs.bookDialog.close();
						this.fetchBooks();
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('操作失败:', error);
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 切换图书状态
			async toggleStatus(book) {
				try {
					uni.showLoading({
						title: '处理中...'
					});

					const res = await uni.request({
						url: `https://your-api-domain.com/api/books/${book.id}/status`,
						method: 'PUT',
						data: {
							status: book.status ? 0 : 1
						}
					});

					if (res.data.code === 200) {
						book.status = book.status ? 0 : 1;
						uni.showToast({
							title: book.status ? '已上架' : '已下架',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: res.data.message,
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('操作失败:', error);
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 显示详情
			showDetail(book) {
				this.selectedBook = {
					...book
				};
				this.$refs.detailDialog.open();
			},

			// 关闭详情对话框
			closeDetailDialog() {
				this.$refs.detailDialog.close();
			},

			// 删除图书
			async deleteBook(id) {
				uni.showModal({
					title: '提示',
					content: '确定要删除这本图书吗?',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({
									title: '删除中...'
								});

								const result = await uni.request({
									url: `https://your-api-domain.com/api/books/${id}`,
									method: 'DELETE'
								});

								if (result.data.code === 200) {
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
									this.fetchBooks();
								} else {
									uni.showToast({
										title: result.data.message,
										icon: 'none'
									});
								}
							} catch (error) {
								console.error('删除失败:', error);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							} finally {
								uni.hideLoading();
							}
						}
					}
				});
			},

			// 批量删除
			batchDelete() {
				if (this.selectedIds.length === 0) {
					uni.showToast({
						title: '请先选择要删除的图书',
						icon: 'none'
					});
					return;
				}

				uni.showModal({
					title: '提示',
					content: `确定要删除选中的${this.selectedIds.length}本图书吗?`,
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({
									title: '删除中...'
								});

								const result = await uni.request({
									url: 'https://your-api-domain.com/api/books/batch',
									method: 'DELETE',
									data: {
										ids: this.selectedIds
									}
								});

								if (result.data.code === 200) {
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
									this.selectedIds = [];
									this.fetchBooks();
								} else {
									uni.showToast({
										title: result.data.message,
										icon: 'none'
									});
								}
							} catch (error) {
								console.error('批量删除失败:', error);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							} finally {
								uni.hideLoading();
							}
						}
					}
				});
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
						uni.showLoading({
							title: '上传中...'
						});

						const uploadRes = await uni.uploadFile({
							url: this.uploadUrl,
							filePath: res.tempFilePaths[0],
							name: 'file',
							formData: {
								type: 'book'
							}
						});

						const data = JSON.parse(uploadRes[1].data);
						if (data.code === 200) {
							this.currentBook.image = data.data.url;
						} else {
							uni.showToast({
								title: '上传失败',
								icon: 'none'
							});
						}
					}
				} catch (error) {
					console.error('选择图片失败:', error);
					uni.showToast({
						title: '选择图片失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 退出登录
			handleLogout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出管理系统吗?',
					success: (res) => {
						if (res.confirm) {
							uni.removeStorageSync('token');
							uni.redirectTo({
								url: '/pages/login/login'
							});
						}
					}
				});
			}
		},
		onLoad() {
			this.fetchBookTypes();
			this.fetchBooks();
		}
	}
</script>

<style>
	/* 样式部分与之前相同，保持不变 */
	.container {
		display: flex;
		flex-direction: column;
		padding: 20rpx;
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.admin-info {
		font-size: 28rpx;
	}

	.logout {
		color: #f56c6c;
		margin-left: 20rpx;
	}

	.breadcrumb {
		font-size: 24rpx;
		color: #909399;
	}

	.search-filter {
		display: flex;
		margin-bottom: 20rpx;
	}

	.search-box {
		flex: 1;
		display: flex;
		margin-right: 20rpx;
	}

	.search-input {
		flex: 1;
		border: 1rpx solid #dcdfe6;
		padding: 10rpx 20rpx;
		border-radius: 8rpx;
	}

	.search-btn {
		margin-left: 10rpx;
		background-color: #409eff;
		color: white;
		border-radius: 8rpx;
	}

	.filter-box {
		margin-right: 20rpx;
	}

	.type-picker {
		border: 1rpx solid #dcdfe6;
		padding: 10rpx 20rpx;
		border-radius: 8rpx;
	}

	.action-btns button {
		margin-left: 10rpx;
	}

	.add-btn {
		background-color: #67c23a;
		color: white;
	}

	.delete-btn {
		background-color: #f56c6c;
		color: white;
	}

	.book-table {
		border: 1rpx solid #ebeef5;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.table-header,
	.table-row {
		display: flex;
	}

	.table-header {
		background-color: #f5f7fa;
		font-weight: bold;
	}

	.header-cell,
	.body-cell {
		padding: 20rpx;
		border-right: 1rpx solid #ebeef5;
		border-bottom: 1rpx solid #ebeef5;
		text-align: center;
	}

	.header-cell:last-child,
	.body-cell:last-child {
		border-right: none;
	}

	.table-row:last-child .body-cell {
		border-bottom: none;
	}

	.book-image {
		width: 80rpx;
		height: 80rpx;
	}

	.action-cell {
		display: flex;
		flex-direction: column;
	}

	.action-btn {
		margin: 5rpx 0;
		color: #409eff;
	}

	.pagination {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.page-controls button {
		margin: 0 10rpx;
		padding: 10rpx 20rpx;
	}

	.dialog-form {
		padding: 20rpx;
	}

	.form-item {
		margin-bottom: 20rpx;
		display: flex;
		align-items: center;
	}

	.form-label {
		width: 150rpx;
	}

	.form-input {
		flex: 1;
		border: 1rpx solid #dcdfe6;
		padding: 10rpx 20rpx;
		border-radius: 8rpx;
	}

	.preview-image {
		width: 100rpx;
		height: 100rpx;
		margin-left: 20rpx;
	}

	.detail-content {
		padding: 20rpx;
	}

	.detail-image {
		width: 200rpx;
		height: 200rpx;
		display: block;
		margin: 0 auto 20rpx;
	}

	.detail-row {
		display: flex;
		margin-bottom: 15rpx;
	}

	.detail-label {
		width: 150rpx;
		font-weight: bold;
	}

	.detail-value {
		flex: 1;
	}

	.example-body {
		/* #ifndef APP-NVUE */
		display: flex;
		/* #endif */
		padding: 20rpx;

	}
</style>