// 动画配置和工具函数
export interface AnimationConfig {
  duration: number;
  delay: number;
  easing: string;
  reducedMotion: boolean;
}

// 默认动画配置
export const defaultAnimationConfig: AnimationConfig = {
  duration: 0.6,
  delay: 0,
  easing: 'ease-out',
  reducedMotion: false
};

// 检测用户是否偏好减少动画
export const prefersReducedMotion = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

// 获取适配后的动画配置
export const getAnimationConfig = (config: Partial<AnimationConfig> = {}): AnimationConfig => {
  const reducedMotion = prefersReducedMotion();
  
  return {
    ...defaultAnimationConfig,
    ...config,
    reducedMotion,
    duration: reducedMotion ? 0.2 : (config.duration || defaultAnimationConfig.duration),
    delay: reducedMotion ? 0 : (config.delay || defaultAnimationConfig.delay)
  };
};

// 动画类型枚举
export enum AnimationType {
  FADE_IN = 'fadeIn',
  FADE_IN_UP = 'fadeInUp',
  FADE_IN_DOWN = 'fadeInDown',
  FADE_IN_LEFT = 'fadeInLeft',
  FADE_IN_RIGHT = 'fadeInRight',
  SLIDE_IN_UP = 'slideInUp',
  SLIDE_IN_DOWN = 'slideInDown',
  SLIDE_IN_LEFT = 'slideInLeft',
  SLIDE_IN_RIGHT = 'slideInRight',
  ZOOM_IN = 'zoomIn',
  BOUNCE_IN = 'bounceIn',
  BOX_REVEAL = 'boxReveal'
}

// 响应式动画配置
export const getResponsiveAnimationConfig = (): Partial<AnimationConfig> => {
  if (typeof window === 'undefined') return {};
  
  const isMobile = window.innerWidth <= 768;
  const isLowEndDevice = navigator.hardwareConcurrency && navigator.hardwareConcurrency <= 2;
  
  if (isMobile || isLowEndDevice) {
    return {
      duration: 0.3,
      delay: 0
    };
  }
  
  return {};
};

// 性能监控工具
export class AnimationPerformanceMonitor {
  private static instance: AnimationPerformanceMonitor;
  private animationCount = 0;
  private maxConcurrentAnimations = 10;
  
  static getInstance(): AnimationPerformanceMonitor {
    if (!AnimationPerformanceMonitor.instance) {
      AnimationPerformanceMonitor.instance = new AnimationPerformanceMonitor();
    }
    return AnimationPerformanceMonitor.instance;
  }
  
  startAnimation(): boolean {
    if (this.animationCount >= this.maxConcurrentAnimations) {
      console.warn('Too many concurrent animations, skipping...');
      return false;
    }
    this.animationCount++;
    return true;
  }
  
  endAnimation(): void {
    this.animationCount = Math.max(0, this.animationCount - 1);
  }
  
  getCurrentCount(): number {
    return this.animationCount;
  }
}

// 动画工具函数
export const animationUtils = {
  // 延迟执行
  delay: (ms: number): Promise<void> => {
    return new Promise(resolve => setTimeout(resolve, ms));
  },
  
  // 缓动函数
  easing: {
    easeOutCubic: (t: number): number => 1 - Math.pow(1 - t, 3),
    easeInOutCubic: (t: number): number => t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2,
    easeOutBounce: (t: number): number => {
      const n1 = 7.5625;
      const d1 = 2.75;
      
      if (t < 1 / d1) {
        return n1 * t * t;
      } else if (t < 2 / d1) {
        return n1 * (t -= 1.5 / d1) * t + 0.75;
      } else if (t < 2.5 / d1) {
        return n1 * (t -= 2.25 / d1) * t + 0.9375;
      } else {
        return n1 * (t -= 2.625 / d1) * t + 0.984375;
      }
    }
  }
};
