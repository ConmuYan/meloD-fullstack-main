const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: [],

  // 开发服务器设置
  devServer: {
    client: {
      overlay: false // 关闭浏览器的全屏红色报错遮罩
    },
    // 禁用主机检查，避免开发环境警告
    allowedHosts: 'all',
    // 启用 HTML5 History API 支持，解决刷新页面空白问题
    historyApiFallback: true,
    // 配置代理解决跨域问题
    proxy: {
      '/img': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        secure: false,
        logLevel: 'debug'
      },
      '/song': {
        target: 'http://localhost:8888',
        changeOrigin: true,
        secure: false,
        logLevel: 'debug'
      }
    }
  },

  // 生产环境配置
  productionSourceMap: false,
  
  // 配置webpack
  configureWebpack: {
    // 解决Vue开发环境警告
    resolve: {
      alias: {
        'vue': 'vue/dist/vue.esm-bundler.js'
      },
      fallback: {
        "vue": require.resolve("vue")
      }
    },
    // 优化构建性能
    optimization: {
      splitChunks: {
        chunks: 'all'
      }
    }
  },

  chainWebpack: config => {
    // 定义环境变量
    config.plugin('define').tap(definitions => {
      Object.assign(definitions[0]['process.env'], {
        NODE_HOST: '"http://localhost:8888"',
      });
      // 解决Vue 3开发环境特性标志警告
      Object.assign(definitions[0], {
        __VUE_OPTIONS_API__: true,
        __VUE_PROD_DEVTOOLS__: false,
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false
      });
      return definitions;
    });
    
    // 优化开发环境构建速度
    if (process.env.NODE_ENV === 'development') {
      config.devtool('eval-cheap-module-source-map');
    }
  }
})
