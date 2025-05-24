<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
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
</style>
