# 📚 Nhà Sách Trực Tuyến - Bookstore Application

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-8.0-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4.0-38BDF8?style=for-the-badge&logo=tailwindcss&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

Hệ thống **Nhà sách Trực tuyến (Bookstore)** là ứng dụng thương mại điện tử full-stack hiện đại dành cho việc quản lý và mua bán sách online. Ứng dụng tích hợp công nghệ **Spring Boot (Backend)**, **React 19 & Vite (Frontend)**, bảo mật với **JWT (JSON Web Token)**, cùng trợ lý **AI Chatbot** thông minh hỗ trợ bởi **Spring AI**, **Google GenAI** và **Tavily Web Search**.

---

## 🌟 Tính Năng Nổi Bật

### 🛒 Dành Cho Khách Hàng (Client)

- **Khám phá & Tìm kiếm Sách**: Tìm kiếm theo từ khóa, lọc theo thể loại, tác giả, mức giá, và sắp xếp linh hoạt.
- **Chi tiết Sách & Đánh giá**: Xem thông tin chi tiết, hình ảnh, tác giả, số lượng tồn kho, điểm đánh giá trung bình và các nhận xét từ người dùng.
- **Giỏ hàng & Thanh toán**: Thêm/sửa/xóa sản phẩm trong giỏ hàng, áp dụng **Mã giảm giá (Coupon)**, nhập địa chỉ giao hàng và xác nhận đơn hàng.
- **Danh sách Yêu thích (Wishlist)**: Lưu lại các cuốn sách yêu thích để mua sau.
- **Quản lý Tài khoản**: Đăng ký, đăng nhập, cập nhật thông tin cá nhân, danh sách địa chỉ nhận hàng, và tải lên ảnh đại diện (avatar).
- **Quản lý Đơn hàng**: Theo dõi trạng thái đơn hàng (Chờ xác nhận, Đang xử lý, Đang giao, Hoàn thành, Đã hủy).
- **Hệ thống Thông báo**: Nhận thông báo thời gian thực về cập nhật đơn hàng, khuyến mãi, v.v.
- **🤖 Trợ lý AI Thông Minh**: Chatbot tư vấn chọn sách, tìm kiếm thông tin tác giả/sách trên Internet (Tavily Search) và giải đáp thắc mắc người dùng.

### 🛡️ Dành Cho Quản Trị Viên (Admin Dashboard)

- **Quản lý Sách & Tồn kho**: Thêm, sửa, xóa sách, tải ảnh bìa, cập nhật số lượng tồn kho (`TonKho`).
- **Quản lý Thể loại & Tác giả**: Quản lý danh mục thể loại và thông tin các tác giả.
- **Quản lý Đơn hàng**: Duyệt đơn hàng, cập nhật trạng thái vận chuyển và thanh toán.
- **Quản lý Người dùng & Khách hàng**: Xem danh sách khách hàng, khóa/mở khóa tài khoản hoặc phân quyền.
- **Quản lý Khuyến mãi & Mã giảm giá**: Tạo và quản lý mã giảm giá (phần trăm hoặc số tiền cố định, thời hạn sử dụng, lượt dùng).
- **Quản lý Đánh giá & Bình luận**: Duyệt hoặc ẩn các nhận xét không phù hợp.
- **Báo cáo & Thống kê**: Trực quan hóa doanh thu, số lượng đơn hàng, sách bán chạy qua biểu đồ Recharts.

---

## 🛠️ Công Nghệ Sử Dụng

### Backend (`/bookstore`)

- **Ngôn ngữ & Framework**: Java 21, Spring Boot 4.1
- **Bảo mật**: Spring Security, JWT (JSON Web Token Authentication & Authorization)
- **Database & ORM**: MySQL, Spring Data JPA, H2 Database (cho môi trường testing)
- **Tích hợp AI**: Spring AI, OpenAI / Groq model integration, Google GenAI Embedding, Tavily Web Search
- **Khác**: Lombok, Spring Validation, Maven

### Frontend (`/frontend`)

- **Framework & Tooling**: React 19, Vite 8, TypeScript
- **State Management & Data Fetching**: React Query (`@tanstack/react-query`), Zustand
- **Routing & Form Handling**: React Router v7, React Hook Form, Zod
- **UI & Styling**: Tailwind CSS v4, Lucide React (Icons), Sonner (Toasts)
- **Data Visualization**: Recharts (Biểu đồ báo cáo Admin)

---

## 📁 Cấu Trúc Thư Mục

```text
Book-Git/
├── bookstore/                  # Backend Spring Boot API
│   ├── src/main/java/com/ntb/bookstore/
│   │   ├── CauHinh/            # Cấu hình CORS, Security, Swagger/OpenAPI, AI
│   │   ├── controller/         # REST Controllers (Client & Admin APIs)
│   │   ├── dto/                # Data Transfer Objects (Requests & Responses)
│   │   ├── entity/             # JPA Entities (Sach, DonHang, NguoiDung,...)
│   │   ├── exception/          # Global Exception Handler
│   │   ├── repository/         # Spring Data JPA Repositories
│   │   ├── security/           # JWT Filters, UserDetailsService
│   │   └── service/            # Business Logic Services
│   ├── src/main/resources/
│   │   ├── application.yaml    # Cấu hình ứng dụng
│   │   └── data.sql            # Dữ liệu mẫu (Seed Data)
│   └── pom.xml                 # Maven dependencies
├── frontend/                   # Frontend React + Vite
│   ├── src/
│   │   ├── api/                # Axios instance & API client calls
│   │   ├── app/                # Layouts & Routing setup
│   │   ├── components/         # Reusable UI components (Button, Input, Modal,...)
│   │   ├── features/           # Feature modules (sach, don-hang, quan-tri, xac-thuc,...)
│   │   ├── types/              # TypeScript interfaces & types
│   │   ├── utils/              # Helper functions & formatters
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   └── vite.config.ts
└── README.md                   # Tài liệu hướng dẫn dự án
```

---

## ⚙️ Yêu Cầu Hệ Thống

Trước khi bắt đầu, hãy đảm bảo bạn đã cài đặt các công cụ sau:

- **Java Development Kit (JDK)**: Version 21 trở lên
- **Apache Maven**: Version 3.8+ (Khuyên dùng `mvn` cài trên hệ thống)
- **Node.js**: Version 18.x trở lên
- **npm** hoặc **yarn** / **pnpm**
- **MySQL Server**: Version 8.0 trở lên

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy Dự Án

### 1. Cấu Hình & Chạy Backend (`bookstore`)

#### Bước 1: Tạo cơ sở dữ liệu MySQL

Mở MySQL Workbench hoặc terminal MySQL và tạo database:

```sql
CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### Bước 2: Cấu hình biến môi trường

Tạo file `.env` tại thư mục `bookstore/.env` (nếu muốn ghi đè cấu hình mặc định trong `application.yaml`):

```env
# Database Credentials
DB_URL=jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=root

# Security & JWT Settings
JWT_BI_MAT=12345678901234567890123456789012345678901234567890
JWT_THOI_GIAN_HIEU_LUC=3600000
JWT_THOI_GIAN_LAM_MOI=86400000

# AI Configuration (tùy chọn)
# Có thể để trống 3 key này; hệ thống vẫn chạy, chỉ tắt chat AI/RAG/tìm kiếm web.
GROQ_API_KEY=
GEMINI_API_KEY=
TAVILY_API_KEY=

# Chỉ bật khi đã cấu hình key tương ứng.
AI_CHAT_MODEL=none
AI_EMBEDDING_MODEL=none

# Admin Seed Account
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin@123
```

#### Bước 3: Khởi chạy Backend Server

Di chuyển vào thư mục `bookstore` và chạy lệnh:

```bash
cd bookstore
mvn spring-boot:run
```

Server Backend sẽ khởi chạy tại: `http://localhost:8080`

Để bật lại các tính năng AI:

```env
# Chat AI qua Groq-compatible OpenAI API
GROQ_API_KEY=your_groq_api_key
AI_CHAT_MODEL=openai

# RAG/embedding qua Gemini
GEMINI_API_KEY=your_gemini_api_key
AI_EMBEDDING_MODEL=google-genai

# Tìm kiếm web trong chat box
TAVILY_API_KEY=your_tavily_api_key
```

> 💡 **Lưu ý**: Lần chạy đầu tiên, Hibernate sẽ tự động tạo bảng dữ liệu và nạp dữ liệu mẫu từ `src/main/resources/data.sql`.

---

### 2. Cấu Hình & Chạy Frontend (`frontend`)

#### Bước 1: Cài đặt dependencies

Di chuyển vào thư mục `frontend` và cài đặt các gói phụ thuộc:

```bash
cd frontend
npm install
```

#### Bước 2: Cấu hình biến môi trường

Tạo file `.env` tại thư mục `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
```

#### Bước 3: Khởi chạy Frontend ở chế độ Dev

```bash
npm run dev
```

Ứng dụng Frontend sẽ có sẵn tại: `http://localhost:5173`

---

## 📡 Danh Sách API Chính (RESTful APIs)

| Nhóm API               | Endpoint                                  | Mô Tả                                                      |
| :--------------------- | :---------------------------------------- | :--------------------------------------------------------- |
| **Xác thực**           | `POST /api/xac-thuc/dang-ky`              | Đăng ký tài khoản mới                                      |
|                        | `POST /api/xac-thuc/dang-nhap`            | Đăng nhập & lấy Token JWT                                  |
|                        | `POST /api/xac-thuc/lam-moi-token`        | Làm mới Access Token                                       |
| **Sách**               | `GET /api/sach`                           | Lấy danh sách sách (tìm kiếm/lọc/phân trang)               |
|                        | `GET /api/sach/{id}`                      | Lấy chi tiết thông tin sách                                |
| **Thể loại & Tác giả** | `GET /api/the-loai`, `GET /api/tac-gia`   | Xem danh sách thể loại và tác giả                          |
| **Giỏ hàng**           | `GET /api/gio-hang`, `POST /api/gio-hang` | Quản lý giỏ hàng của người dùng                            |
| **Đơn hàng**           | `POST /api/don-hang`, `GET /api/don-hang` | Đặt hàng và lịch sử đơn hàng                               |
| **Yêu thích**          | `GET /api/sach-yeu-thich`                 | Danh sách sách yêu thích                                   |
| **Thông báo**          | `GET /api/thong-bao`                      | Danh sách thông báo người dùng                             |
| **Chat AI**            | `POST /api/ai/hoi`                        | Gửi câu hỏi cho Trợ lý AI                                  |
| **Quản trị (Admin)**   | `/api/quan-tri/**`                        | Các API CRUD Sách, Đơn hàng, Mã giảm giá, Thống kê báo cáo |

---

## 🧪 Kiểm Thu & Build (Testing & Production Build)

### Kiểm thu Backend:

```bash
cd bookstore
mvn test
```

### Kiểm tra Lint & Build Frontend:

```bash
cd frontend
npm run lint
npm run build
```

Thư mục build đầu ra sẽ nằm tại `frontend/dist`.

---

## 👥 Dữ Liệu Mẫu & Tài Khoản Thử Nghiệm

Dữ liệu khởi tạo mặc định từ `data.sql` bao gồm các thể loại sách, tác giả, danh mục sách phong phú cùng các tài khoản mẫu:

- **Tài khoản Admin**: `admin`
- **Tài khoản Khách hàng**: `hoai.an`, `minh.binh`
- **Mật khẩu**: `abc@123`

---

## 📝 Giấy Phép (License)

Dự án được phát triển phục vụ mục đích học tập và nghiên cứu. Bản quyền thuộc về tác giả.
