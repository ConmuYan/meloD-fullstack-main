# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A full-stack music streaming platform with three main components:
- **music-client**: Vue 3 + TypeScript frontend for music discovery and playback
- **music-manage**: Vue 3 + TypeScript admin dashboard for content management
- **music-server**: Spring Boot + MyBatis backend API service

## Architecture & Structure

### Backend (Spring Boot)
- **Port**: 8888
- **Framework**: Spring Boot 2.6.2 + MyBatis-Plus 3.5.1
- **Database**: MySQL 5.7+ with 10 main tables (song, singer, song_list, consumer, etc.)
- **Cache**: Redis (localhost:6379)
- **Storage**: MinIO for file storage (images, audio files)
- **Key Packages**:
  - `com.example.yin.controller` - REST API endpoints
  - `com.example.yin.service` - Business logic
  - `com.example.yin.mapper` - MyBatis database operations
  - `com.example.yin.model.domain` - Entity classes

### Frontend (Vue 3)
- **Client**: Port 8080 (Vue CLI dev server)
- **Manage**: Port 8081 (Vue CLI dev server)
- **State**: Vuex with modular stores (user, song, configure)
- **UI**: Element Plus component library
- **Routing**: Vue Router 4 with lazy-loaded views
- **API**: Axios with base URL http://localhost:8888

### Key Domain Models
- **Song**: Music tracks with metadata, lyrics, URLs
- **Singer**: Artist information
- **SongList**: Playlists/collections
- **Consumer**: User accounts
- **Collect**: User favorites/bookmarks
- **Comment**: User comments on songs/playlists

## Development Commands

### Backend (music-server)
```bash
# Start server
cd music-server
./mvnw spring-boot:run      # Unix/Mac
mvnw.cmd spring-boot:run    # Windows
# OR
mvn spring-boot:run         # With Maven installed

# Database setup
mysql -u root -p < music-server/sql/tp_music.sql
# Update credentials in application.properties
```

### Frontend (music-client)
```bash
cd music-client
npm install
npm run serve    # dev server at localhost:8080
npm run build    # production build
npm run lint     # ESLint
```

### Admin Panel (music-manage)
```bash
cd music-manage
npm install
npm run serve    # dev server at localhost:8081
npm run build    # production build
```

### External Dependencies
```bash
# Start Redis
redis-server

# Start MinIO (file storage)
minio server /path/to/data
```

## Database Configuration

**Default MySQL Config** (update in `music-server/src/main/resources/application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tp_music?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
```

**Redis Config** (localhost:6379, DB 0)

## File Structure Highlights

### Backend Resources
- `music-server/img/` - User avatars, singer pics, song covers
- `music-server/song/` - Audio files (MP3)
- `music-server/swiper/` - Banner images

### Frontend Components
- **Shared**: YinAudio, YinPlayBar, YinHeader (reused across client/manage)
- **Client Views**: Home, SongSheet, Singer, Search, Personal, Lyric
- **Manage Views**: Consumer, Song, Singer, SongList pages (CRUD operations)

## API Endpoints Pattern

All endpoints follow REST conventions:
- `/song/*` - Song operations
- `/singer/*` - Artist operations
- `/songList/*` - Playlist operations
- `/consumer/*` - User operations
- `/collect/*` - User favorites
- `/comment/*` - Comments system
- `/admin/*` - Admin operations

## Development Notes

### Environment Setup
1. Install MySQL 5.7+ and create `tp_music` database
2. Import SQL schema: `music-server/sql/tp_music.sql`
3. Install Redis (default port 6379)
4. Install MinIO for file storage
5. Configure database credentials in application.properties

### Resource Dependencies
- Download media files from: https://pan.baidu.com/s/1Qv0ohAIPeTthPK_CDwpfWg (提取码: gwa4)
- Place files in `music-server/` directory structure as shown in README

### Docker Deployment
```bash
docker compose up --build  # From project root
```

### Common Issues
- **Resource loading**: Ensure img/ and song/ directories are in correct locations
- **Audio playback**: Verify media files are not corrupted
- **CORS**: Backend configured for cross-origin requests from localhost:8080/8081