<template>
  <ul class="yin-header-nav" :class="{ 'auth-nav': isAuthNav }">
    <li 
      :class="{ 
        active: item.name === activeName,
        'auth-btn': isAuthButton(item.name),
        'login-btn': item.name === '登录',
        'signup-btn': item.name === '注册'
      }" 
      v-for="item in styleList" 
      :key="item.path" 
      @click="handleChangeView(item)"
    >
      <span class="btn-content">
        <i v-if="item.name === '登录'" class="icon-login">👤</i>
        <i v-if="item.name === '注册'" class="icon-signup">✨</i>
        {{ item.name }}
      </span>
    </li>
  </ul>
</template>

<script lang="ts">
import { defineComponent, getCurrentInstance, computed, PropType } from "vue";

interface NavItem {
  name: string;
  path: string;
}

export default defineComponent({
  props: {
    styleList: {
      type: Array as PropType<NavItem[]>,
      default: () => []
    },
    activeName: String,
  },
  emits: ["click"],
  setup(props) {
    const { proxy } = getCurrentInstance();

    // 判断是否为认证相关的导航栏（包含登录/注册按钮）
    const isAuthNav = computed(() => {
      return props.styleList?.some(item => item.name === '登录' || item.name === '注册');
    });

    // 判断是否为认证按钮
    function isAuthButton(name: string) {
      return name === '登录' || name === '注册';
    }

    function handleChangeView(item: NavItem) {
      proxy.$emit("click", item.path, item.name);
    }
    
    return {
      handleChangeView,
      isAuthNav,
      isAuthButton,
    };
  },
});
</script>

<style lang="scss" scoped>
@import "@/assets/css/var.scss";

.yin-header-nav {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  
  &.auth-nav {
    gap: 1rem;
  }
}

li {
  margin: $header-nav-margin;
  padding: $header-nav-padding;
  line-height: 3.3rem;
  color: $color-grey;
  border-bottom: none;
  cursor: pointer;
  transition: all $transition-medium;
  border-radius: $radius-small;
  position: relative;
  overflow: hidden;
  
  &:hover {
    color: $color-black;
    transform: translateY(-1px);
  }
  
  .btn-content {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0;
    
    .icon-login,
    .icon-signup {
      font-size: 0.9rem;
      opacity: 0.8;
    }
  }
}

li.active {
  color: $color-black;
  font-weight: 600;
  border-bottom: 5px solid $color-black;
}

// 认证按钮样式
li.auth-btn {
  margin: 0 0.25rem;
  padding: 0.5rem 1.2rem;
  line-height: 1.5;
  border-radius: 25px;
  font-weight: 500;
  font-size: 0.9rem;
  border-bottom: none !important;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.6s;
  }
  
  &:hover::before {
    left: 100%;
  }
  
  .btn-content {
    position: relative;
    z-index: 1;
    
    .icon-login,
    .icon-signup {
      transition: transform $transition-medium;
    }
  }
  
  &:hover {
    transform: translateY(-2px) scale(1.02);
    
    .icon-login,
    .icon-signup {
      transform: scale(1.1);
    }
  }
  
  &:active {
    transform: translateY(0) scale(0.98);
  }
}

// 登录按钮 - 优雅的蓝色渐变
li.login-btn {
  background: linear-gradient(135deg,rgba(102, 126, 234, 0.47) 0%,rgb(104, 101, 106) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  
  &:hover {
    background: linear-gradient(135deg, #e081e9 0%, #e3455a 100%);
    box-shadow: 0 6px 20px rgba(240, 147, 251, 0.4);
    color: white;
  }
  
  &.active {
    background: linear-gradient(135deg,rgb(15, 164, 202) 0%,rgb(101, 128, 96) 100%);
    color: white;
  }
}

// 注册按钮 - 活力的粉色渐变
li.signup-btn {
  background: linear-gradient(135deg,rgb(106, 101, 209) 0%,rgba(154, 21, 184, 0.71) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(240, 147, 251, 0.3);
  
  &:hover {
    background: linear-gradient(135deg, #e081e9 0%, #e3455a 100%);
    box-shadow: 0 6px 20px rgba(240, 147, 251, 0.4);
    color: white;
  }
  
  &.active {
    background: linear-gradient(135deg, #e081e9 0%, #e3455a 100%);
    color: white;
  }
}

// 响应式设计
@media screen and (max-width: $sm) {
  .yin-header-nav {
    gap: 0.25rem;
    
    &.auth-nav {
      gap: 0.5rem;
    }
  }
  
  li.auth-btn {
    padding: 0.4rem 1rem;
    font-size: 0.85rem;
    
    .btn-content {
      gap: 0.3rem;
      
      .icon-login,
      .icon-signup {
        font-size: 0.8rem;
      }
    }
  }
}
</style>
