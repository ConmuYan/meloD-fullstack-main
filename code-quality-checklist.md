# 代码质量检查清单

## 常见问题类型及解决方案

### 1. 资源文件路径错误
**问题描述**: 引用不存在的图片、CSS或其他静态资源文件
**检查要点**:
- 确认assets目录下文件的实际存在性
- 检查文件扩展名是否正确(.jpg vs .png)
- 验证相对路径的正确性

**解决方案**: 
- 使用正确的文件路径和扩展名
- 建议在开发时定期检查assets目录结构

### 2. TypeScript类型错误
**问题描述**: Vue 3 Composition API中getCurrentInstance()和$router/$route的类型问题
**检查要点**:
- getCurrentInstance()返回值可能为null，需要非空断言
- proxy对象的$router和$route属性需要类型断言
- 确保所有Vue实例属性访问都有正确的类型处理

**解决方案**:
```typescript
// 正确的写法
const { proxy } = getCurrentInstance()!; // 添加非空断言
(proxy as any).$router.push(...); // 添加类型断言
(proxy as any).$route.query.id; // 添加类型断言
```

### 3. 编译前检查清单
在每次提交代码前，请检查以下项目:
- [ ] 运行 `npm run build` 确保编译无错误
- [ ] 检查控制台是否有TypeScript类型错误
- [ ] 验证所有静态资源文件路径的正确性
- [ ] 确保所有Vue组件中的getCurrentInstance()使用了非空断言
- [ ] 确保所有$router和$route访问使用了类型断言

### 4. 项目特定注意事项
- music-manage和music-client项目都使用Vue 3 + TypeScript
- 静态资源统一放在src/assets目录下
- 路由相关操作统一通过mixin.ts中的routerManager函数处理

### 5. 修复记录
**2024年修复记录**:
- 修复Login.vue背景图片路径错误(background.jpg → background.png)
- 修复getCurrentInstance()类型错误(添加非空断言)
- 修复所有$router/$route类型错误(添加类型断言)
- 涉及文件: mixin.ts, CollectPage.vue, CommentPage.vue, ListSongPage.vue, SongPage.vue

---
*此清单将持续更新，记录项目开发过程中遇到的常见问题和解决方案*