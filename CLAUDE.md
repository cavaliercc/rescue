# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Full-stack rescue management application with:
- **Backend**: Spring Boot 3.2 + MyBatis-Plus + PostgreSQL + JWT auth (`backend/`)
- **Frontend**: Vue 3 + TypeScript + Vite + Element Plus + Pinia + Tailwind (`frontend/`)

## Commands

### Backend
```bash
cd backend
./mvnw spring-boot:run          # Start dev server on :8080
./mvnw test                     # Run all tests
./mvnw test -Dtest=UserServiceTest  # Run single test class
./mvnw package -DskipTests      # Build JAR
```

### Frontend
```bash
cd frontend
npm run dev      # Start Vite dev server on :5173
npm run build    # Type-check + build to dist/
npm run preview  # Preview production build
```

### Environment
- Backend reads `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` from env (defaults to `postgres`/`postgres` and a dev secret)
- Frontend reads `VITE_API_BASE_URL` from `.env` (defaults to `http://localhost:8080`)
- PostgreSQL database: `rescue` on port 5432

## Architecture

### Backend Layer Structure
```
entity/      → JPA/MyBatis-Plus entities (Lombok @Data)
mapper/      → MyBatis-Plus BaseMapper extensions (no XML needed for CRUD)
service/     → Business logic
dto/         → Request/response DTOs; ApiResponse<T> wraps all responses
security/    → JwtUtil (token create/validate) + JwtAuthFilter (OncePerRequestFilter)
```

All API responses use `ApiResponse<T>` with `code` (200 = success), `msg`, and `data` fields.

### Frontend Layer Structure
```
src/api/     → http.ts (axios instance) + per-feature API modules (e.g. auth.ts)
src/stores/  → Pinia stores (auth.ts manages token + user state)
src/router/  → Vue Router with auth guard (requiresAuth meta)
src/layouts/ → MainLayout.vue wraps authenticated pages
src/views/   → Feature views organized by domain (auth/, dashboard/, ...)
```

The axios interceptor in `http.ts` attaches `Bearer` token from `localStorage`, unwraps `ApiResponse`, shows Element Plus error messages on failure, and redirects to `/login` on 401.

Router guard redirects unauthenticated users to `/login` and authenticated users away from `/login`.

### Auth Flow
1. `POST /api/auth/login` → returns JWT token
2. Token stored in `localStorage` and Pinia `authStore`
3. All subsequent requests attach token via axios request interceptor
4. 401 response → `authStore.logout()` + redirect to `/login`

## Skill & Workflow Reference

### 开发工作流（Vue + Spring Boot）
| 场景 | 优先使用的 Skill |
|------|----------------|
| Vue 3 组件、Pinia、路由开发 | `frontend-patterns` |
| Spring Boot API、MyBatis-Plus、JWT | `springboot-patterns` |
| 测试驱动开发（写测试先行） | `tdd-workflow` |
| 提交、PR、分支管理 | `git-workflow` |

### 调研 & 搜索
| 场景 | 优先使用的 Skill |
|------|----------------|
| 快速搜索技术方案、库文档 | `tavily-search` |
| 深度研究、竞品分析、行业调研 | `deep-research` |

### 飞书（Lark）集成
| 场景 | 优先使用的 Skill |
|------|----------------|
| 发送消息、机器人通知 | `lark-im` |
| 文档创建与编辑 | `lark-doc` |
| 日历事件管理 | `lark-calendar` |
| 其他飞书能力（审批、任务、表格等） | `lark-approval` / `lark-task` / `lark-sheets` |

### 合规 & 安全
| 场景 | 优先使用的 Skill |
|------|----------------|
| 医疗数据隐私（PHI/HIPAA） | `hipaa-compliance` |
| DeFi / 链上金融合约安全 | `defi-amm-security` |
