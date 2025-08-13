// 响应式设计工具函数
export interface BreakpointConfig {
  xs: number;
  sm: number;
  md: number;
  lg: number;
  xl: number;
}

// 默认断点配置
export const defaultBreakpoints: BreakpointConfig = {
  xs: 0,
  sm: 576,
  md: 768,
  lg: 992,
  xl: 1200
};

// 获取当前屏幕尺寸类型
export const getCurrentBreakpoint = (breakpoints: BreakpointConfig = defaultBreakpoints): keyof BreakpointConfig => {
  if (typeof window === 'undefined') return 'lg';
  
  const width = window.innerWidth;
  
  if (width >= breakpoints.xl) return 'xl';
  if (width >= breakpoints.lg) return 'lg';
  if (width >= breakpoints.md) return 'md';
  if (width >= breakpoints.sm) return 'sm';
  return 'xs';
};

// 判断是否为移动设备
export const isMobile = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.innerWidth <= defaultBreakpoints.md;
};

// 判断是否为平板设备
export const isTablet = (): boolean => {
  if (typeof window === 'undefined') return false;
  const width = window.innerWidth;
  return width > defaultBreakpoints.md && width < defaultBreakpoints.lg;
};

// 判断是否为桌面设备
export const isDesktop = (): boolean => {
  if (typeof window === 'undefined') return true;
  return window.innerWidth >= defaultBreakpoints.lg;
};

// 获取设备类型
export const getDeviceType = (): 'mobile' | 'tablet' | 'desktop' => {
  if (isMobile()) return 'mobile';
  if (isTablet()) return 'tablet';
  return 'desktop';
};

// 响应式动画配置
export const getResponsiveAnimationSettings = () => {
  const deviceType = getDeviceType();
  
  switch (deviceType) {
    case 'mobile':
      return {
        duration: 0.3,
        delay: 0,
        particlesEnabled: false,
        complexAnimationsEnabled: false
      };
    case 'tablet':
      return {
        duration: 0.4,
        delay: 0.1,
        particlesEnabled: true,
        complexAnimationsEnabled: true
      };
    case 'desktop':
    default:
      return {
        duration: 0.6,
        delay: 0.2,
        particlesEnabled: true,
        complexAnimationsEnabled: true
      };
  }
};

// 性能检测
export const getDevicePerformance = (): 'low' | 'medium' | 'high' => {
  if (typeof navigator === 'undefined') return 'medium';
  
  // 检查硬件并发数
  const cores = navigator.hardwareConcurrency || 4;
  
  // 检查内存（如果支持）
  let memory = 4; // 默认4GB
  if ('deviceMemory' in navigator) {
    memory = (navigator as any).deviceMemory;
  }
  
  // 检查连接类型（如果支持）
  let connectionSpeed = 'fast';
  if ('connection' in navigator) {
    const connection = (navigator as any).connection;
    if (connection.effectiveType) {
      connectionSpeed = ['slow-2g', '2g', '3g'].includes(connection.effectiveType) ? 'slow' : 'fast';
    }
  }
  
  // 综合评估
  if (cores <= 2 || memory <= 2 || connectionSpeed === 'slow') {
    return 'low';
  } else if (cores <= 4 || memory <= 4) {
    return 'medium';
  } else {
    return 'high';
  }
};

// 根据设备性能调整动画设置
export const getPerformanceBasedSettings = () => {
  const performance = getDevicePerformance();
  
  switch (performance) {
    case 'low':
      return {
        enableBackgroundEffects: false,
        enableParticles: false,
        enableComplexAnimations: false,
        animationDuration: 0.2,
        maxConcurrentAnimations: 3
      };
    case 'medium':
      return {
        enableBackgroundEffects: true,
        enableParticles: false,
        enableComplexAnimations: true,
        animationDuration: 0.4,
        maxConcurrentAnimations: 6
      };
    case 'high':
    default:
      return {
        enableBackgroundEffects: true,
        enableParticles: true,
        enableComplexAnimations: true,
        animationDuration: 0.6,
        maxConcurrentAnimations: 10
      };
  }
};

// 创建响应式监听器
export class ResponsiveListener {
  private listeners: Array<(breakpoint: keyof BreakpointConfig) => void> = [];
  private currentBreakpoint: keyof BreakpointConfig;
  
  constructor(private breakpoints: BreakpointConfig = defaultBreakpoints) {
    this.currentBreakpoint = getCurrentBreakpoint(breakpoints);
    this.init();
  }
  
  private init() {
    if (typeof window === 'undefined') return;
    
    const handleResize = () => {
      const newBreakpoint = getCurrentBreakpoint(this.breakpoints);
      if (newBreakpoint !== this.currentBreakpoint) {
        this.currentBreakpoint = newBreakpoint;
        this.listeners.forEach(listener => listener(newBreakpoint));
      }
    };
    
    window.addEventListener('resize', handleResize);
  }
  
  public subscribe(listener: (breakpoint: keyof BreakpointConfig) => void) {
    this.listeners.push(listener);
    // 立即调用一次
    listener(this.currentBreakpoint);
    
    // 返回取消订阅函数
    return () => {
      const index = this.listeners.indexOf(listener);
      if (index > -1) {
        this.listeners.splice(index, 1);
      }
    };
  }
  
  public getCurrentBreakpoint(): keyof BreakpointConfig {
    return this.currentBreakpoint;
  }
}

// 全局响应式监听器实例
export const responsiveListener = new ResponsiveListener();
