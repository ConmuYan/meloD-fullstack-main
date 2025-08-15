# 🎵 meloD 音乐流媒体平台

> 基于 Spring Boot + Vue 3 + TypeScript 构建的现代化全栈音乐流媒体平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5.18-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-4.5.5-blue.svg)](https://www.typescriptlang.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.10.7-409EFF.svg)](https://element-plus.org/)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5.1-red.svg)](https://baomidou.com/)

## 📖 项目简介

meloD 是一个功能完整的音乐流媒体平台，提供音乐播放、收藏、评论、歌单管理等核心功能。系统采用前后端分离架构，支持用户端和管理端双重界面，具备完善的用户管理、内容管理和文件存储能力。

### 🎯 核心特性

- 🎵 **音乐播放**：支持在线音乐播放、歌词显示、播放控制
- 📱 **双端支持**：用户端(music-client) + 管理端(music-manage)
- 👥 **用户系统**：注册登录、个人资料、收藏管理、评论互动
- 🎤 **内容管理**：歌手管理、歌曲管理、歌单管理、轮播图管理
- 🔍 **搜索功能**：支持歌曲、歌手、歌单的全文搜索
- 💾 **文件存储**：集成 MinIO 对象存储，支持音频、图片文件管理
- ⚡ **性能优化**：Redis 缓存、数据库连接池、前端懒加载
- 🐳 **容器化部署**：完整的 Docker 部署方案

### 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                    前端展示层                                  │
├─────────────────────┬───────────────────────────────────────┤
│   music-client      │          music-manage                 │
│   (用户端:8080)      │        (管理端:8081)                   │
│   Vue3+TypeScript   │        Vue3+TypeScript               │
└─────────────────────┴───────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │   HTTP/REST API   │
                    │   (Axios + CORS)  │
                    └─────────┬─────────┘
┌─────────────────────────────┴─────────────────────────────────┐
│                    music-server                               │
│                 Spring Boot 后端服务 (:8888)                  │
│  ┌─────────────┬─────────────┬─────────────┬─────────────┐   │
│  │ Controller  │   Service   │   Mapper    │   Domain    │   │
│  │   (接口层)   │  (业务层)    │  (数据层)    │  (实体层)    │   │
│  └─────────────┴─────────────┴─────────────┴─────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
   ┌─────────┐        ┌─────────────┐        ┌─────────┐
   │  MySQL  │        │    Redis    │        │  MinIO  │
   │ (主数据)  │        │   (缓存)     │        │(文件存储)│
   │ :3306   │        │   :6379     │        │ :9000   │
   └─────────┘        └─────────────┘        └─────────┘
```

## 🛠️ 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.6.2 | 核心框架 |
| MyBatis-Plus | 3.5.1 | ORM 框架 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存中间件 |
| MinIO | 8.3.0 | 对象存储 |
| Spring Security | - | 安全框架 |
| Spring Mail | - | 邮件服务 |
| HikariCP | - | 数据库连接池 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.18 | 前端框架 |
| TypeScript | 4.5.5 | 类型系统 |
| Element Plus | 2.10.7 | UI 组件库 |
| Vue Router | 4.0.3 | 路由管理 |
| Vuex | 4.0.0 | 状态管理 |
| Axios | 1.11.0 | HTTP 客户端 |
| Sass | 1.32.7 | CSS 预处理器 |
| Tailwind CSS | 4.1.11 | 原子化 CSS |

### 开发工具

- **构建工具**：Maven 3.6+, Vue CLI 5.0+
- **代码规范**：ESLint, TypeScript
- **容器化**：Docker, Docker Compose
- **版本控制**：Git

### 模块划分架构
```
┌─────────────────────────────────────────────────────────────┐
│                    前端展示层                                  │
├─────────────────────┬───────────────────────────────────────┤
│   music-client      │          music-manage                 │
│   (用户端:8080)      │        (管理端:8081)                   │
│   Vue3+TypeScript   │        Vue3+TypeScript               │
└─────────────────────┴───────────────────────────────────────┘
                              │
                    ┌─────────┴─────────┐
                    │   API Gateway     │
                    │   (Axios调用)      │
                    └─────────┬─────────┘
┌─────────────────────────────┴─────────────────────────────────┐
│                    music-server                               │
│                 Spring Boot后端服务                           │
│  ┌─────────────┬─────────────┬─────────────┬─────────────┐   │
│  │ Controller  │   Service   │   Mapper    │   Domain    │   │
│  │   (接口层)   │  (业务层)    │  (数据层)    │  (实体层)    │   │
│  └─────────────┴─────────────┴─────────────┴─────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
   ┌─────────┐        ┌─────────────┐        ┌─────────┐
   │  MySQL  │        │    Redis    │        │  MinIO  │
   │ (主数据)  │        │   (缓存)     │        │(文件存储)│
   │ :3306   │        │   :6379     │        │         │
   └─────────┘        └─────────────┘        └─────────┘
```

### 前后端交互协议与数据流路径
- **通信协议**：HTTP/HTTPS + REST API
- **数据格式**：JSON
- **API 基础 URL**：http://localhost:8888
- **跨域配置**：后端已配置支持 localhost:8080/8081 的 CORS 请求

## 📁 项目结构

```
meloD-base/
├── music-server/           # Spring Boot 后端服务
│   ├── src/main/java/com/example/yin/
│   │   ├── controller/     # REST API 控制器
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # MyBatis 数据访问层
│   │   ├── model/domain/   # 实体类
│   │   ├── config/         # 配置类
│   │   └── common/         # 通用工具类
│   ├── src/main/resources/
│   │   ├── mapper/         # MyBatis XML 映射文件
│   │   ├── application.yml # 主配置文件
│   │   └── sql/           # 数据库脚本
│   ├── docker-server/      # 后端 Docker 配置
│   ├── img/               # 图片资源目录
│   ├── song/              # 音频文件目录
│   └── pom.xml            # Maven 依赖配置
├── music-client/           # Vue3 用户端前端
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 通用组件
│   │   ├── router/        # 路由配置
│   │   ├── store/         # Vuex 状态管理
│   │   ├── api/           # API 接口封装
│   │   ├── utils/         # 工具函数
│   │   ├── enums/         # 枚举常量
│   │   └── assets/        # 静态资源
│   └── package.json       # 前端依赖配置
├── music-manage/           # Vue3 管理端前端
│   ├── src/               # 同 music-client 结构
│   └── package.json
├── docker-compose.yml      # Docker 编排配置
└── README.md              # 项目文档
```

## 🎯 核心功能模块

### 用户端功能 (music-client)

#### 🏠 首页模块
- 轮播图展示
- 推荐歌单
- 热门歌曲
- 最新音乐

#### 🎵 音乐播放
- 在线音乐播放
- 歌词同步显示
- 播放控制（播放/暂停/上一首/下一首）
- 播放模式（顺序/随机/单曲循环）
- 音量控制

#### 👤 用户系统
- 用户注册/登录
- 邮箱登录支持
- 个人资料管理
- 头像上传
- 密码修改
- 忘记密码功能

#### 🎤 歌手模块
- 歌手列表浏览
- 歌手详情页
- 歌手作品展示
- 按地区/性别筛选

#### 📋 歌单模块
- 歌单分类浏览
- 歌单详情页
- 个人歌单创建
- 歌单收藏
- 歌单评论

#### ❤️ 收藏系统
- 歌曲收藏
- 歌单收藏
- 收藏列表管理

#### 💬 评论互动
- 歌曲评论
- 歌单评论
- 评论点赞

#### 🔍 搜索功能
- 歌曲搜索
- 歌手搜索
- 歌单搜索

### 管理端功能 (music-manage)

#### 🔐 管理员系统
- 管理员登录
- 权限验证
- 会话管理

#### 👥 用户管理
- 用户列表查看
- 用户信息编辑
- 用户状态管理
- 在线状态监控
- 用户强制删除

#### 🎵 歌曲管理
- 歌曲信息 CRUD
- 音频文件上传
- 歌曲封面管理
- 歌词编辑
- 歌曲分类管理

#### 🎤 歌手管理
- 歌手信息 CRUD
- 歌手头像上传
- 歌手作品关联

#### 📋 歌单管理
- 歌单信息 CRUD
- 歌单封面管理
- 歌单歌曲关联
- 歌单分类管理

#### 💬 评论管理
- 评论列表查看
- 评论删除

#### ❤️ 收藏管理
- 用户收藏查看
- 收藏数据统计

#### 🖼️ 轮播图管理
- 轮播图 CRUD
- 图片上传
- 显示状态控制

## 🗄️ 数据库设计

### 核心数据表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `admin` | 管理员表 | id, name, password |
| `consumer` | 用户表 | id, username, password, email, phone_num, sex, birth, introduction, location, avator |
| `singer` | 歌手表 | id, name, sex, pic, birth, location, introduction |
| `song` | 歌曲表 | id, singer_id, name, introduction, pic, lyric, url, create_time, update_time |
| `song_list` | 歌单表 | id, title, pic, introduction, style |
| `list_song` | 歌单歌曲关联表 | id, song_id, song_list_id |
| `collect` | 收藏表 | id, user_id, type, song_id, song_list_id, create_time |
| `comment` | 评论表 | id, user_id, song_id, song_list_id, content, create_time, type, up |
| `rank_list` | 排行榜表 | id, song_list_id, consumer_id, score |
| `user_support` | 用户点赞表 | id, comment_id, user_id |
| `banner` | 轮播图表 | id, pic, url, status, category |

### 数据库配置

```yaml
# application.yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/tp_music?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  
  # Redis 配置
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000ms
    
  # 邮件配置
  mail:
    host: smtp.163.com
    port: 587
    username: mqkim0525@163.com
    password: ******your-auth-code******
    
# MyBatis-Plus 配置
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath:mapper/*.xml
  
# MinIO 配置
minio:
  endpoint: http://localhost:9000
  accessKey: root
  secretKey: 123456789
  bucketName: user01
```

## 🔧 API 接口设计

### 后端 API 结构

#### 用户相关接口
```
POST /user/add                    # 用户注册
POST /user/login/status           # 用户登录
POST /user/email/status           # 邮箱登录
GET  /user/detail?id={id}         # 获取用户详情
POST /user/update                 # 更新用户信息
POST /user/updatePassword         # 修改密码
POST /user/avatar/update?id={id}  # 更新头像
GET  /user/delete?id={id}         # 删除用户
POST /user/heartbeat              # 用户心跳
```

#### 歌曲相关接口
```
GET  /song                        # 获取所有歌曲
GET  /song/detail?id={id}         # 获取歌曲详情
GET  /song/singer/detail?singerId={id} # 获取歌手歌曲
POST /song/add                    # 添加歌曲
POST /song/update                 # 更新歌曲信息
POST /song/img/update?id={id}     # 更新歌曲封面
POST /song/url/update?id={id}     # 更新歌曲文件
POST /song/lrc/update?id={id}     # 更新歌词
DELETE /song/delete?id={id}       # 删除歌曲
```

#### 歌手相关接口
```
GET  /singer                      # 获取所有歌手
GET  /singer/detail?id={id}       # 获取歌手详情
POST /singer/add                  # 添加歌手
POST /singer/update               # 更新歌手信息
POST /singer/avatar/update?id={id} # 更新歌手头像
GET  /singer/delete?id={id}       # 删除歌手
```

#### 歌单相关接口
```
GET  /songList                    # 获取所有歌单
GET  /songList/detail?id={id}     # 获取歌单详情
POST /songList/add                # 添加歌单
POST /songList/update             # 更新歌单信息
POST /songList/img/update?id={id} # 更新歌单封面
GET  /songList/delete?id={id}     # 删除歌单
```

#### 收藏相关接口
```
GET  /collection/detail?userId={id} # 获取用户收藏
POST /collection/add              # 添加收藏
DELETE /collection/delete         # 删除收藏
```

#### 评论相关接口
```
GET  /comment/song/detail?songId={id}     # 获取歌曲评论
GET  /comment/songList/detail?songListId={id} # 获取歌单评论
POST /comment/add                 # 添加评论
POST /comment/update              # 更新评论
GET  /comment/delete?id={id}      # 删除评论
```

### 前端状态管理

#### Vuex Store 模块

```typescript
// store/index.ts - 主 store
interface RootState {
  currentRecommendationPlaylist: any;
}

// store/user.ts - 用户模块
interface UserState {
  userId: string;
  username: string;
  userPic: string;
  // ... 其他用户信息
}

// store/song.ts - 音乐播放模块
interface SongState {
  id: string;
  url: string;
  pic: string;
  index: number;
  lyric: string;
  currentTime: number;
  duration: number;
  isPlay: boolean;
  playList: any[];
  // ... 其他播放状态
}

// store/configure.ts - 配置模块
interface ConfigureState {
  token: boolean;
  showAside: boolean;
  searchWord: string;
  activeNavName: string;
}
```

#### 路由配置

```typescript
// 用户端路由 (music-client)
const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/sign-in', name: 'sign-in', component: SignIn },
  { path: '/sign-up', name: 'sign-up', component: SignUp },
  { path: '/personal', name: 'personal', component: Personal, meta: { requireAuth: true } },
  { path: '/user/:id', name: 'UserProfile', component: UserProfile },
  { path: '/song-sheet', name: 'song-sheet', component: SongSheet },
  { path: '/song-sheet-detail/:id', name: 'song-sheet-detail', component: SongSheetDetail },
  { path: '/singer', name: 'singer', component: Singer },
  { path: '/singer-detail/:id', name: 'singer-detail', component: SingerDetail },
  { path: '/search', name: 'search', component: Search },
  { path: '/lyric/:id', name: 'lyric', component: Lyric },
  { path: '/setting', name: 'setting', component: Setting, meta: { requireAuth: true } },
  { path: '/recommendation-playlist', name: 'RecommendationPlaylist', component: RecommendationPlaylist }
];

// 管理端路由 (music-manage)
const routes = [
  { path: '/', component: Login },
  { path: '/Home', component: Home, children: [
    { path: '/Info', component: InfoPage },
    { path: '/song', component: SongPage },
    { path: '/singer', component: SingerPage },
    { path: '/SongList', component: SongListPage },
    { path: '/ListSong', component: ListSongPage },
    { path: '/Comment', component: CommentPage },
    { path: '/Consumer', component: ConsumerPage },
    { path: '/Collect', component: CollectPage }
  ]}
];
```

## 🚀 快速开始

### 环境要求

- **Java**: JDK 8+
- **Node.js**: 14.0+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **MinIO**: 最新版本
- **Maven**: 3.6+

### 安装步骤

#### 1. 克隆项目
```bash
git clone <repository-url>
cd meloD-base
```

#### 2. 数据库初始化
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE tp_music CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据表结构
mysql -u root -p tp_music < music-server/src/main/resources/sql/tp_music.sql
```

#### 3. 配置文件修改
```bash
# 修改后端配置
cp music-server/src/main/resources/application-dev.properties.example \
   music-server/src/main/resources/application-dev.properties

# 根据实际环境修改数据库连接信息
vim music-server/src/main/resources/application-dev.properties
```

#### 4. 启动 Redis 和 MinIO
```bash
# 启动 Redis
redis-server

# 启动 MinIO
minio server /data --console-address ":9001"
```

#### 5. 启动后端服务
```bash
cd music-server
./mvnw spring-boot:run
# 或者
mvn spring-boot:run
```

#### 6. 启动前端服务

```bash
# 启动用户端
cd music-client
npm install
npm run serve

# 启动管理端
cd music-manage
npm install
npm run serve
```

### 访问地址

- **用户端**: http://localhost:8080
- **管理端**: http://localhost:8081
- **后端 API**: http://localhost:8888
- **MinIO 控制台**: http://localhost:9001

### 默认账户

- **管理员账户**: admin / 123
- **测试用户**: 需要通过注册页面创建

## 🐳 Docker 部署

### 使用 Docker Compose 一键部署

```bash
# 构建并启动所有服务
docker-compose up --build

# 后台运行
docker-compose up -d

# 停止服务
docker-compose down
```

### Docker Compose 配置

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: tp_music
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./music-server/src/main/resources/sql:/docker-entrypoint-initdb.d

  redis:
    image: redis:6.2-alpine
    ports:
      - "6379:6379"

  minio:
    image: minio/minio:latest
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: root
      MINIO_ROOT_PASSWORD: 123456789
    command: server /data --console-address ":9001"
    volumes:
      - minio_data:/data

  music-server:
    build: ./music-server
    ports:
      - "8888:8888"
    depends_on:
      - mysql
      - redis
      - minio
    environment:
      SPRING_PROFILES_ACTIVE: prod

  music-client:
    build: ./music-client
    ports:
      - "8080:8080"
    depends_on:
      - music-server

  music-manage:
    build: ./music-manage
    ports:
      - "8081:8081"
    depends_on:
      - music-server

volumes:
  mysql_data:
  minio_data:
```

### 单独构建镜像

```bash
# 构建后端镜像
cd music-server
docker build -t melod-server .

# 构建前端镜像
cd music-client
npm run build
docker build -t melod-client .

cd music-manage
npm run build
docker build -t melod-manage .
```

## 🔧 开发指南

### 代码规范

#### 后端代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Spring Boot 标准注解
- Controller 层只处理请求响应
- Service 层处理业务逻辑
- Mapper 层只处理数据访问

#### 前端代码规范
- 使用 TypeScript 严格模式
- 遵循 Vue 3 Composition API 规范
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case
- 使用 ESLint 进行代码检查

### 项目配置

#### 环境配置文件
```
music-server/src/main/resources/
├── application.yml              # 主配置文件
├── application-dev.properties   # 开发环境配置
├── application-prod.properties  # 生产环境配置
└── application-test.properties  # 测试环境配置
```

#### 前端环境变量
```javascript
// .env.development
NODE_ENV=development
VUE_APP_BASE_URL=http://localhost:8888

// .env.production
NODE_ENV=production
VUE_APP_BASE_URL=https://your-api-domain.com
```

### 文件上传配置

#### MinIO 存储桶配置
```
存储桶结构:
music/
├── avatars/     # 用户头像
├── singers/     # 歌手图片
├── songs/       # 歌曲封面
├── playlists/   # 歌单封面
├── banners/     # 轮播图
└── audio/       # 音频文件
```

#### 文件上传限制
- 图片文件：最大 5MB，支持 jpg/jpeg/png/gif
- 音频文件：最大 50MB，支持 mp3/wav/flac
- 头像图片：最大 2MB，建议尺寸 200x200

### 安全配置

#### CORS 配置
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);
        // ...
    }
}
```

#### 用户认证
- 基于 Session 的用户认证
- 支持邮箱登录和用户名登录
- 密码使用 MD5 加盐加密
- 用户会话心跳机制

### 缓存策略

#### Redis 缓存使用
```java
@Cacheable(value = "banner", key = "'list'")
public List<Banner> getAllBanner() {
    return bannerMapper.selectList(null);
}

@Cacheable(value = "banner", key = "'active_list'")
public List<Banner> getAllActiveBanner() {
    return bannerMapper.selectAllActive();
}
```

### 性能优化

#### 前端优化
- 路由懒加载
- 图片懒加载
- 组件按需引入
- 代码分割
- Gzip 压缩

#### 后端优化
- 数据库连接池 (HikariCP)
- Redis 缓存
- SQL 查询优化
- 分页查询
- 异步处理

## 🐛 常见问题

### 开发环境问题

**Q: 前端启动报错 "Module not found"**
```bash
# 清除 node_modules 重新安装
rm -rf node_modules package-lock.json
npm install
```

**Q: 后端启动报错 "Could not connect to MySQL"**
```bash
# 检查 MySQL 服务状态
sudo systemctl status mysql
# 检查配置文件中的数据库连接信息
```

**Q: 文件上传失败**
```bash
# 检查 MinIO 服务状态
docker ps | grep minio
# 检查存储桶是否存在
mc ls minio/music
```

### 部署问题

**Q: Docker 容器启动失败**
```bash
# 查看容器日志
docker logs <container-name>
# 检查端口占用
netstat -tulpn | grep :8888
```

**Q: 跨域请求失败**
- 检查后端 CORS 配置
- 确认前端请求地址正确
- 检查浏览器控制台错误信息

### 性能问题

**Q: 音频播放卡顿**
- 检查音频文件大小和格式
- 确认网络连接稳定
- 检查浏览器音频解码支持

**Q: 页面加载缓慢**
- 启用 Gzip 压缩
- 优化图片大小
- 使用 CDN 加速
- 检查数据库查询性能

## 🙏 致谢与许可

### 致谢

本项目基于 [Yin-Hongwei/music-website](https://github.com/Yin-Hongwei/music-website) 开源项目进行大规模改进和优化。感谢原作者 **Yin-Hongwei** 提供的优秀基础架构和设计思路，为音乐流媒体平台的开发提供了宝贵的参考。

### 许可证声明

本项目遵循 **Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0)** 许可证。

#### 许可证要求
- ✅ **署名**：使用本项目时必须给出适当的署名
- ✅ **非商业性使用**：不得将本项目用于商业目的
- ✅ **相同方式共享**：基于本项目的衍生作品必须采用相同许可证
- ❌ **禁止商业使用**：不得将本项目或其衍生作品用于商业用途

#### 版权信息
```
Original work Copyright (c) 2018 Yin-Hongwei
Modified work Copyright (c) 2025 meloD Contributors

This work is licensed under the Creative Commons Attribution-NonCommercial 4.0 
International License. To view a copy of this license, visit 
http://creativecommons.org/licenses/by-nc/4.0/ or send a letter to 
Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
```

#### 使用声明

如果您使用本项目，请在您的项目中包含以下声明：

```
本项目基于 meloD 音乐流媒体平台开发，该平台基于 Yin-Hongwei 的开源项目进行改进。
原始项目：https://github.com/Yin-Hongwei/music-website
改进项目：[您的项目地址]

遵循 CC BY-NC 4.0 许可证，仅供学习和非商业用途使用。
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- **邮箱**: mqkim0525@163.com

感谢所有为这个项目做出贡献的开发者和开源社区。

---

## 📋 项目总结

meloD 是一个功能完整、技术先进的音乐流媒体平台，采用现代化的全栈技术架构。本项目基于 [Yin-Hongwei/music-website](https://github.com/Yin-Hongwei/music-website) 进行大规模改进和优化：

### 🎯 核心优势

- **🏗️ 现代化架构**: Spring Boot 2.6.2 + Vue 3 + TypeScript 技术栈
- **🔄 前后端分离**: REST API 设计，清晰的接口规范
- **📱 双端支持**: 用户端和管理端独立应用
- **🗄️ 完整数据模型**: 11 个核心数据表，支持复杂业务场景
- **⚡ 性能优化**: Redis 缓存 + HikariCP 连接池 + 前端懒加载
- **📁 文件存储**: MinIO 对象存储，支持音频、图片文件管理
- **🐳 容器化部署**: 完整的 Docker 部署方案
- **🔒 安全设计**: 用户认证、CORS 配置、文件上传限制

### 🚀 技术特色

- **后端**: 标准四层架构 (Controller-Service-Mapper-Domain)
- **前端**: Vue 3 Composition API + TypeScript + Element Plus
- **状态管理**: Vuex 模块化设计 (user/song/configure)
- **路由管理**: Vue Router 4 + 路由守卫 + 懒加载
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.1
- **缓存**: Redis 6.0+ 缓存策略
- **存储**: MinIO 分布式对象存储

### 🎵 业务功能

- **音乐播放**: 在线播放、歌词显示、播放控制、播放模式
- **用户系统**: 注册登录、个人资料、收藏管理、评论互动
- **内容管理**: 歌手管理、歌曲管理、歌单管理、轮播图管理
- **搜索功能**: 全文搜索、搜索历史、分类筛选
- **管理后台**: 用户管理、内容审核、数据统计、在线监控

这个项目展示了现代 Web 应用开发的最佳实践，具备良好的可扩展性、可维护性和用户体验。
