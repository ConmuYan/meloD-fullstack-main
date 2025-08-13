const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: [],

  // 开发服务器设置
  devServer: {
    client: {
      overlay: false // 关闭浏览器的全屏红色报错遮罩
    }
  },

  chainWebpack: config => {
    config.plugin('define').tap(definitions => {
      Object.assign(definitions[0]['process.env'], {
        NODE_HOST: '"http://localhost:8888"',
      });
      return definitions;
    });
  }
})
