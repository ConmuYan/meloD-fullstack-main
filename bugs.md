# Bugs 与业务问题清单

以下问题基于代码实际扫描与文件定位，逐条给出问题现象、影响与建议修复。所有路径均以项目根目录为基准。

---

## 一、music-server（后端）

### 1. MinIO 上传实现存在多处严重问题
- 文件：`music-server/src/main/java/com/example/yin/controller/MinioUploadController.java`
- 代码：`uploadFile/uploadImgFile/uploadSonglistImgFile/uploadSongImgFile/uploadAtorImgFile`
- 主要问题：
  - 使用 `InputStream.available()` 作为对象大小，并将 `partSize` 置为 `-1`：
    - 参考：`MinioUploadController.java` 第 57-66、76-86、97-106、117-126、137-146 行
    - 影响：`available()` 不是文件总大小，可能导致 MinIO `PutObjectArgs.stream()` 参数非法，出现上传失败或数据损坏。MinIO 要求未知大小时 `objectSize=-1` 且合理 `partSize`。
  - 读取固定的 `application-dev.properties` 获取 MinIO 配置，忽略 `spring.profiles.active`：
    - 参考：`MinioUploadController.java` 第 35 行；`application.properties` 第 24 行 `spring.profiles.active=dev`
    - 影响：生产/测试环境切换无效；与 `application.properties`/`application.yml` 中 MinIO 配置不一致时造成连接失败或错桶上传。
  - 静态 `MinioClient` 和静态工具方法，未使用 Spring Bean 管理，线程安全与生命周期不可控。
  - 对象 key 以 `/` 开头（如 `"/singer/img/"`）：
    - 参考：第 80、100、120、141 行
    - 影响：S3/MinIO 允许任意 key，但前导斜杠会导致路径混乱，不利于统一管理与 URL 拼接。
  - 文件名直接使用 `file.getOriginalFilename()`：可能覆盖同名文件，且未做非法字符/路径校验。
- 建议修复：
  - 使用 Spring `@ConfigurationProperties` 注入 MinIO 参数，通过 Bean 管理 `MinioClient`；
  - 上传时 `PutObjectArgs.stream(inputStream, -1, 10 * 1024 * 1024)`（示例）并自定义唯一 key（时间戳/UUID/分目录）；
  - 移除对 `application-dev.properties` 的硬编码读取，统一使用 `application-*.properties|yml` 配置；
  - 去掉 key 前导斜杠，统一目录规范。

### 2. CORS 实现存在冲突与不规范
- 文件：
  - `music-server/src/main/java/com/example/yin/config/WebMvcConfig.java`
  - `music-server/src/main/java/com/example/yin/config/CorsInterceptor.java`
  - `music-server/src/main/java/com/example/yin/config/WebCharacterEncodingFilter.java`
- 主要问题：
  - 使用拦截器动态设置 CORS，且 `Access-Control-Allow-Origin` 直接回显 `Origin`：
    - 参考：`CorsInterceptor.java` 第 13-18 行
    - 风险：未校验 Origin 白名单，易被任意来源携带 Cookie 调用；与 `allowedOrigins("*")` 冲突。
  - `WebCharacterEncodingFilter.addCorsMappings` 使用 `allowedOrigins("*")` 且 `CorsInterceptor` 开启 `Allow-Credentials=true`：
    - 参考：`WebCharacterEncodingFilter.java` 第 35-37 行；`CorsInterceptor.java` 第 17 行
    - 风险：浏览器规范禁止 `Access-Control-Allow-Origin: *` 与 `Credentials: true` 同时存在 → 请求将失败。
  - 使用已废弃的 `HandlerInterceptorAdapter`。
  - `@EnableWebMvc` 会禁用 Spring Boot MVC 自动配置，可能影响静态资源与消息转换配置：
    - 参考：`WebCharacterEncodingFilter.java` 第 21 行
- 建议修复：
  - 移除 `CorsInterceptor` 与 `@EnableWebMvc`，统一在 `WebMvcConfigurer#addCorsMappings` 配置 CORS；
  - 使用明确的 `allowedOrigins` 白名单（如 `http://localhost:8080`,`http://localhost:8081`），并在需要时开启 `allowCredentials(true)`；
  - 升级为 `HandlerInterceptor`（如仍需拦截器用途）。

### 3. MinIO 配置来源不一致（YAML/Properties/DEV混用）
- 文件：
  - `music-server/src/main/resources/application.properties`（第 33-37 行）
  - `music-server/src/main/resources/application-dev.properties`（第 6-9 行）
  - `music-server/src/main/resources/application.yml`（第 22-25 行）
- 现象：不同文件中 MinIO 的 access-key/secret/bucket 不一致（`minioadmin` vs `root/123456789`，`music` vs `user01`）。
- 影响：环境切换与配置覆盖不可预期；可能连接错误桶或鉴权失败。
- 建议：统一配置来源与命名，仅保留一种格式（properties 或 yml），并通过 `spring.profiles.active` 切换。

### 4. Service 层误用 Web 注解
- 文件：`music-server/src/main/java/com/example/yin/service/impl/SongListServiceImpl.java`
- 代码：第 90 行 `updateSongListImg(MultipartFile avatorFile, @RequestParam("id") int id)`
- 问题：`@RequestParam` 应仅出现在 Controller 层，出现在 Service 实现无效且破坏分层边界。
- 建议：移除 Service 层的 Web 注解，由 Controller 负责参数绑定。

### 5. Excel 导出接口命名拼写问题（可能影响可读性/一致性）
- 文件：`music-server/src/main/java/com/example/yin/controller/ListSongController.java`
- 代码：第 56 行 `@GetMapping("/excle")`
- 问题：`excle` 疑为 `excel` 拼写错误。虽前后端一致，但不利于维护与搜索。
- 建议：改为 `/excel`，并同步前端调用。

### 6. 安全与健壮性问题（输入校验/覆盖/鉴权）
- 文件：多处文件上传接口（参考 Song/Singer/SongList/Consumer 相关 Service 与 Controller）
- 问题：
  - 未见对上传类型、大小、恶意文件名的校验；
  - 直接使用原始文件名作为对象 key，存在覆盖风险；
  - 未见鉴权/权限校验（例如仅管理员可更新资源）。
- 建议：加入参数校验、服务端类型白名单与大小限制、唯一文件名策略、鉴权拦截（如 Spring Security/JWT）。

---

## 二、music-client（前台）

### 1. Axios 全局 Content-Type 与请求体不匹配风险
- 文件：`music-client/src/api/request.ts`
- 代码：第 10 行 `application/x-www-form-urlencoded`，而 `post(url, data)` 直接传对象（第 84-90 行）。
- 影响：后端如果按表单读取将失败；或后端期望 JSON 时应发送 `application/json`。
- 建议：统一改为 JSON；或在发起请求前使用 `qs.stringify` 与表单编码保持一致。

### 2. 个别接口硬编码后端地址，绕过 BASE_URL
- 文件：`music-client/src/views/FPassword.vue`
- 代码：第 57、85 行直接调用 `http://localhost:8888`。
- 影响：环境切换困难，代理/反向代理无法接管；
- 建议：改为使用 `getBaseURL()` 或封装 API 方法。

### 3. 构建配置将 `NODE_HOST` 强制为本地服务
- 文件：`music-client/vue.config.js`
- 代码：第 7 行 `NODE_HOST: "http://localhost:8888"`
- 影响：生产构建也会指向本地后端，发布后前端将无法访问正确后端。
- 建议：基于 `process.env` 或 `.env.*` 区分环境，例如开发指向本地、生产指向网关域名。

---

## 三、music-manage（管理端）

### 1. Axios 全局 Content-Type 与请求体不匹配风险
- 文件：`music-manage/src/api/request.ts`
- 同 `music-client`，第 10 行设为表单编码，但 `post(url, data)` 直接传对象（第 84-90 行）。
- 建议：同前台调整。

### 2. 硬编码上传/导出接口地址
- 文件：
  - `music-manage/src/views/ConsumerPage.vue` 第 45 行：`action="http://localhost:8888/user/avatar/update?id=..."`
  - `music-manage/src/views/SongListPage.vue` 第 156-170 行（函数在 156 起）：导出 `http://localhost:8888/excle`
- 影响：环境不可配置，绕过统一拦截与错误处理。
- 建议：统一使用 `getBaseURL()`/API 封装，或通过环境变量配置。

### 3. 构建配置将 `NODE_HOST` 强制为本地服务
- 文件：`music-manage/vue.config.js` 第 8 行
- 影响与建议：同 `music-client`。

### 4. `/excle` 命名拼写问题
- 与后端一致，但建议修正为 `/excel` 并同步前后端。

---

## 四、通用与配置层面

### 1. 数据源与敏感信息暴露风险
- 文件：
  - `music-server/src/main/resources/application-prod.properties`（第 1 行包含外网 MySQL 地址）
  - 多处 MinIO/Redis 明文凭据
- 风险：凭据暴露在仓库；
- 建议：使用环境变量/配置中心/密钥管理，仓库仅保留示例。

### 2. CORS 与凭证并用导致浏览器直接拦截
- 现象：前端 `axios.defaults.withCredentials = true`，后端 `allowedOrigins("*")`；
- 影响：浏览器拒绝携带 Cookie 的跨域响应；
- 建议：将 `allowedOrigins` 改为具体域名，或关闭 `withCredentials`。

### 3. 日志与错误处理
- 现象：上传失败直接 `e.printStackTrace()`（参考 `MinioUploadController`），响应体返回字符串；
- 建议：统一错误码/响应模型，记录结构化日志，避免将底层异常直接暴露给前端。

---

# 修复优先级建议（从高到低）
1. 规范 CORS 配置，移除冲突设置，确保可携带凭证的跨域请求生效。
2. 重构 MinIO 上传逻辑（Bean 化、正确 `PutObjectArgs.stream` 参数、统一配置源、唯一文件名）。
3. 移除 Service 层的 `@RequestParam`，保持分层清晰。
4. 清理前端硬编码后端地址与构建时 `NODE_HOST` 的固定值，改为按环境注入。
5. 统一 Axios `Content-Type` 与请求体编码，避免后端解析异常。
6. 修正 `/excle` 命名并同步前后端，优化语义与维护性。
7. 查漏补缺安全/鉴权与输入校验（文件类型/大小/覆盖）。
