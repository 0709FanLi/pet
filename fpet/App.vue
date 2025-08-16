<script>
  export default {
    onLaunch: function () {
      console.log('App Launch')
    },
    onShow: function () {
      console.log('App Show')
    },
    onHide: function () {
      console.log('App Hide')
    },
  }
</script>

<style lang="scss">
  /* 引入公共样式库 */
  @import './common/styles.scss';

  /* 每个页面公共css */
  page {
    font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
    line-height: 1.5;
    color: var(--text-primary);
    background-color: var(--bg-secondary);
  }

  /* 全局滚动条样式 */
  ::-webkit-scrollbar {
    width: 0;
    background: transparent;
  }
</style>
