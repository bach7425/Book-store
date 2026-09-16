SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM thong_bao
WHERE ma_thong_bao BETWEEN 1 AND 4
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('admin', 'hoai.an', 'minh.binh')
   );

DELETE FROM danh_gia
WHERE ma_danh_gia BETWEEN 1 AND 3
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
   )
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   );

DELETE FROM thanh_toan
WHERE ma_thanh_toan BETWEEN 1 AND 2
   OR ma_don_hang IN (
       SELECT ma_don_hang FROM don_hang
       WHERE ma_nguoi_dung IN (
           SELECT ma_nguoi_dung FROM nguoi_dung
           WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
       )
   );

DELETE FROM chi_tiet_don_hang
WHERE ma_chi_tiet_don_hang BETWEEN 1 AND 5
   OR ma_don_hang IN (
       SELECT ma_don_hang FROM don_hang
       WHERE ma_nguoi_dung IN (
           SELECT ma_nguoi_dung FROM nguoi_dung
           WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
       )
   )
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   );

DELETE FROM don_hang
WHERE ma_don_hang BETWEEN 1 AND 2
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
   );

DELETE FROM chi_tiet_gio_hang
WHERE ma_chi_tiet_gio_hang BETWEEN 1 AND 3
   OR ma_gio_hang IN (
       SELECT ma_gio_hang FROM gio_hang
       WHERE ma_nguoi_dung IN (
           SELECT ma_nguoi_dung FROM nguoi_dung
           WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
       )
   )
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   );

DELETE FROM gio_hang
WHERE ma_gio_hang BETWEEN 1 AND 2
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
   );

DELETE FROM sach_yeu_thich
WHERE ma_nguoi_dung BETWEEN 1 AND 3
   OR ma_sach BETWEEN 1 AND 16
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
   )
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   );

DELETE FROM sach_the_loai
WHERE ma_sach BETWEEN 1 AND 16
   OR ma_the_loai BETWEEN 1 AND 5
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   )
   OR ma_the_loai IN (
       SELECT ma_the_loai FROM the_loai
       WHERE ten IN (
           'Văn học Việt Nam', 'Van hoc Viet Nam', 'Kỹ năng sống', 'Ky nang song',
           'Công nghệ thông tin', 'Cong nghe thong tin',
           'Tiểu thuyết', 'Tieu thuyet', 'Kinh doanh'
       )
   );

DELETE FROM ton_kho
WHERE ma_ton_kho BETWEEN 1 AND 16
   OR ma_sach IN (
       SELECT ma_sach FROM sach
       WHERE ten_sach IN (
           'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
           'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
           'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
           'Clean Architecture'
       )
   );

DELETE FROM sach
WHERE ma_sach BETWEEN 1 AND 16
   OR ten_sach IN (
       'Mắt Biếc', 'Mắt Biec', 'Đắc Nhân Tâm', 'Dac Nhan Tam',
       'Clean Code', 'Rừng Na Uy', 'Rung Na Uy',
       'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Toi Thay Hoa Vang Tren Co Xanh',
       'Clean Architecture'
   );

DELETE FROM dia_chi
WHERE ma_dia_chi BETWEEN 1 AND 2
   OR ma_nguoi_dung IN (
       SELECT ma_nguoi_dung FROM nguoi_dung
       WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
   );

DELETE FROM ma_giam_gia
WHERE ma_giam_gia BETWEEN 1 AND 2
   OR ma_code IN ('HELLO10', 'FREESHIP');

DELETE FROM nguoi_dung
WHERE ma_nguoi_dung BETWEEN 1 AND 3
   OR ten_dang_nhap IN ('admin', 'hoai.an', 'minh.binh')
   OR email IN ('admin@bookstore.vn', 'an.nguyen@example.com', 'binh.tran@example.com');

DELETE FROM tac_gia
WHERE ma_tac_gia BETWEEN 1 AND 10
   OR ten IN (
       'Nguyễn Nhật Ánh', 'Nguyen Nhat Anh',
       'Dale Carnegie',
       'Robert C. Martin',
       'Haruki Murakami',
       'Paulo Coelho',
       'George Orwell',
       'Yuval Noah Harari',
       'J.K. Rowling',
       'Napoleon Hill',
       'Daniel Kahneman'
   );

DELETE FROM the_loai
WHERE ma_the_loai BETWEEN 1 AND 5
   OR ten IN (
       'Văn học Việt Nam', 'Van hoc Viet Nam',
       'Kỹ năng sống', 'Ky nang song',
       'Công nghệ thông tin', 'Cong nghe thong tin',
       'Tiểu thuyết', 'Tieu thuyet',
       'Kinh doanh'
   );

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO nguoi_dung (
    ma_nguoi_dung, ho_va_ten, email, ten_dang_nhap, mat_khau_bam,
    vai_tro, so_dien_thoai, anh_dai_dien
) VALUES
    (1, 'Quản trị viên', 'admin@bookstore.vn', 'admin',
     '$2a$10$Ph71uXgxrfg7lFCqAbtE9.LOQalJVE6J0jrPp.CCWYhp93jXmqUey',
     'QUAN_TRI_VIEN', '0900000000', '/uploads/nguoi-dung/admin.jpg'),
    (2, 'Nguyễn Hoài An', 'an.nguyen@example.com', 'hoai.an',
     '$2a$10$Ph71uXgxrfg7lFCqAbtE9.LOQalJVE6J0jrPp.CCWYhp93jXmqUey',
     'NGUOI_DUNG', '0912345678', '/uploads/avatars/an.png'),
    (3, 'Trần Minh Bình', 'binh.tran@example.com', 'minh.binh',
     '$2a$10$Ph71uXgxrfg7lFCqAbtE9.LOQalJVE6J0jrPp.CCWYhp93jXmqUey',
     'NGUOI_DUNG', '0987654321', '/uploads/avatars/binh.png')
ON DUPLICATE KEY UPDATE
    ho_va_ten = VALUES(ho_va_ten),
    email = VALUES(email),
    ten_dang_nhap = VALUES(ten_dang_nhap),
    mat_khau_bam = VALUES(mat_khau_bam),
    vai_tro = VALUES(vai_tro),
    so_dien_thoai = VALUES(so_dien_thoai),
    anh_dai_dien = VALUES(anh_dai_dien);

INSERT INTO dia_chi (
    ma_dia_chi, ma_nguoi_dung, nguoi_nhan, so_dien_thoai,
    dia_chi_chi_tiet, mac_dinh
) VALUES
    (1, 2, 'Nguyễn Hoài An', '0912345678',
     '12 Nguyễn Trãi, Phường Bến Thành, Quận 1, Thành phố Hồ Chí Minh', true),
    (2, 3, 'Trần Minh Bình', '0987654321',
     '45 Cầu Giấy, Phường Dịch Vọng, Quận Cầu Giấy, Hà Nội', true)
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung),
    nguoi_nhan = VALUES(nguoi_nhan),
    so_dien_thoai = VALUES(so_dien_thoai),
    dia_chi_chi_tiet = VALUES(dia_chi_chi_tiet),
    mac_dinh = VALUES(mac_dinh);

INSERT INTO gio_hang (ma_gio_hang, ma_nguoi_dung) VALUES
    (1, 2),
    (2, 3)
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung);

INSERT INTO tac_gia (ma_tac_gia, ten, tieu_su) VALUES
    (1, 'Nguyễn Nhật Ánh', 'Tác giả Việt Nam nổi tiếng với các tác phẩm trong trẻo về tuổi thơ, tình bạn và những rung động đầu đời.'),
    (2, 'Dale Carnegie', 'Tác giả và diễn giả người Mỹ, được biết đến qua các tác phẩm kinh điển về giao tiếp và phát triển bản thân.'),
    (3, 'Robert C. Martin', 'Kỹ sư phần mềm, tác giả nhiều cuốn sách có ảnh hưởng về mã sạch, thiết kế và kiến trúc phần mềm.'),
    (4, 'Haruki Murakami', 'Nhà văn Nhật Bản với phong cách hiện thực huyền ảo, giàu âm nhạc, cô đơn và suy tưởng.'),
    (5, 'Paulo Coelho', 'Nhà văn Brazil nổi tiếng với những tác phẩm giàu chất triết lý, khơi gợi niềm tin và hành trình theo đuổi ước mơ.'),
    (6, 'George Orwell', 'Nhà văn Anh được biết đến với các tác phẩm châm biếm chính trị sắc bén và những cảnh báo sâu sắc về quyền lực.'),
    (7, 'Yuval Noah Harari', 'Nhà sử học Israel, tác giả các cuốn sách phổ biến về lịch sử nhân loại, công nghệ và tương lai xã hội.'),
    (8, 'J.K. Rowling', 'Nhà văn Anh nổi tiếng với thế giới phép thuật Harry Potter, giàu trí tưởng tượng và sức hấp dẫn với nhiều thế hệ độc giả.'),
    (9, 'Napoleon Hill', 'Tác giả người Mỹ với nhiều tác phẩm kinh điển về tư duy thành công, mục tiêu và phát triển cá nhân.'),
    (10, 'Daniel Kahneman', 'Nhà tâm lý học đoạt giải Nobel Kinh tế, nổi tiếng với các nghiên cứu về phán đoán, ra quyết định và thiên kiến nhận thức.')
ON DUPLICATE KEY UPDATE
    ten = VALUES(ten),
    tieu_su = VALUES(tieu_su);

INSERT INTO the_loai (ma_the_loai, ten, mo_ta) VALUES
    (1, 'Văn học Việt Nam', 'Tác phẩm văn học trong nước, gần gũi với đời sống và cảm xúc của độc giả Việt.'),
    (2, 'Kỹ năng sống', 'Sách phát triển bản thân, giao tiếp, tư duy và xây dựng thói quen tích cực.'),
    (3, 'Công nghệ thông tin', 'Sách lập trình, kỹ thuật phần mềm, kiến trúc hệ thống và tư duy công nghệ.'),
    (4, 'Tiểu thuyết', 'Tác phẩm hư cấu, truyện dài và văn học hiện đại trong nước lẫn quốc tế.'),
    (5, 'Kinh doanh', 'Sách quản trị, tài chính, bán hàng, khởi nghiệp và vận hành doanh nghiệp.')
ON DUPLICATE KEY UPDATE
    ten = VALUES(ten),
    mo_ta = VALUES(mo_ta);

INSERT INTO sach (
    ma_sach, ten_sach, mo_ta, gia, anh_bia, nha_xuat_ban,
    ma_tac_gia, ngay_xuat_ban, do_tuoi, ten_nha_cung_cap, nguoi_dich,
    ngon_ngu, trong_luong_gram, kich_thuoc_bao_bi, so_trang, hinh_thuc
) VALUES
    (1, 'Mắt Biếc', 'Mắt Biếc là câu chuyện trong trẻo và man mác buồn về Ngạn, Hà Lan, ký ức làng Đo Đo và một tình yêu tuổi học trò kéo dài qua năm tháng. Tác phẩm nổi bật bởi giọng văn dịu dàng, giàu hình ảnh, đưa người đọc trở lại những ngày thơ ấu với sân trường, hàng cây, những rung động đầu đời và cả nỗi tiếc nuối khi tình cảm không đi đến cùng. Cuốn sách phù hợp với độc giả yêu văn học Việt Nam, thích những câu chuyện nhẹ nhàng nhưng đọng lại lâu trong lòng.',
     89000.00, '/uploads/sach/mat-biec.jpg', 'NXB Trẻ', 1, '2019-05-20', '13+', 'NXB Trẻ', null, 'Tiếng Việt', 250, '20 x 13 x 1.2 cm', 300, 'Bìa mềm'),
    (2, 'Đắc Nhân Tâm', 'Đắc Nhân Tâm trình bày những nguyên tắc nền tảng trong giao tiếp, ứng xử và xây dựng mối quan hệ tích cực. Thông qua các câu chuyện ngắn gọn, dễ hiểu, cuốn sách gợi ý cách lắng nghe, thấu hiểu, khích lệ người khác và tạo thiện cảm trong công việc lẫn đời sống cá nhân. Đây là lựa chọn phù hợp cho người muốn cải thiện kỹ năng giao tiếp, bán hàng, quản lý đội nhóm hoặc đơn giản là cư xử tinh tế hơn mỗi ngày.',
     96000.00, '/uploads/sach/dac-nhan-tam.jpg', 'NXB Tổng hợp TP.HCM', 2, '2020-03-10', '15+', 'NXB Tổng hợp TP.HCM', 'Nguyễn Văn Phước', 'Tiếng Việt', 320, '20.5 x 14.5 x 1.6 cm', 320, 'Bìa mềm'),
    (3, 'Clean Code', 'Clean Code là tài liệu kinh điển dành cho lập trình viên muốn nâng chất lượng mã nguồn từ mức chạy được lên mức dễ đọc, dễ hiểu và dễ bảo trì. Sách bàn về cách đặt tên, tổ chức hàm, xử lý lỗi, viết kiểm thử, quản lý phụ thuộc và nhận diện những dấu hiệu mã xấu trong dự án thực tế. Nội dung phù hợp với lập trình viên đã có nền tảng cơ bản, đặc biệt là người làm việc trong đội nhóm hoặc duy trì hệ thống lâu dài.',
     420000.00, '/uploads/sach/clean-code.jpg', 'Prentice Hall', 3, '2008-08-01', '16+', 'Pearson Education', null, 'Tiếng Anh', 820, '23.5 x 18 x 3 cm', 464, 'Bìa mềm'),
    (4, 'Rừng Na Uy', 'Rừng Na Uy là tiểu thuyết sâu lắng về tuổi trẻ, tình yêu, cô đơn, mất mát và hành trình trưởng thành nhiều day dứt. Qua ký ức của Toru Watanabe, Haruki Murakami dẫn người đọc bước vào một thế giới vừa thực vừa mơ, nơi âm nhạc, nỗi buồn và những lựa chọn khó khăn luôn hiện diện. Tác phẩm phù hợp với độc giả trưởng thành, yêu văn chương Nhật Bản và những câu chuyện nội tâm giàu cảm xúc.',
     135000.00, '/uploads/sach/rung-na-uy.jpg', 'NXB Hội Nhà Văn', 4, '2021-11-12', '18+', 'Nhã Nam', 'Trịnh Lữ', 'Tiếng Việt', 420, '20.5 x 14 x 2 cm', 552, 'Bìa mềm'),
    (5, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh mở ra thế giới tuổi thơ miền quê qua ánh nhìn hồn nhiên, trong trẻo nhưng không thiếu những va vấp đầu đời. Câu chuyện xoay quanh tình anh em, tình bạn, những bí mật nhỏ bé, sự ghen tị, lòng bao dung và vẻ đẹp bình dị của làng quê Việt Nam. Với văn phong nhẹ nhàng, giàu cảm xúc, cuốn sách phù hợp cho thiếu nhi, thanh thiếu niên và cả người lớn muốn tìm lại ký ức tuổi thơ.',
     110000.00, '/uploads/sach/toi-thay-hoa-vang.jpg', 'NXB Trẻ', 1, '2018-04-15', '10+', 'NXB Trẻ', null, 'Tiếng Việt', 300, '20 x 13 x 1.5 cm', 380, 'Bìa mềm'),
    (6, 'Clean Architecture', 'Clean Architecture tập trung vào cách thiết kế hệ thống phần mềm bền vững, dễ kiểm thử và ít phụ thuộc vào framework hay cơ sở dữ liệu cụ thể. Robert C. Martin giải thích các nguyên tắc phân tách trách nhiệm, ranh giới kiến trúc, dependency rule và cách tổ chức mã để hệ thống có thể thích nghi với thay đổi. Sách phù hợp với lập trình viên backend, kiến trúc sư phần mềm, tech lead và những ai đang xây dựng ứng dụng quy mô vừa đến lớn.',
     510000.00, '/uploads/sach/clean-architecture.jpg', 'Prentice Hall', 3, '2017-09-20', '16+', 'Pearson Education', null, 'Tiếng Anh', 780, '23.5 x 18 x 2.7 cm', 432, 'Bìa mềm'),
    (7, 'Nhà Giả Kim', 'Nhà Giả Kim là câu chuyện giàu chất ngụ ngôn về Santiago, chàng chăn cừu lên đường tìm kho báu và dần học cách lắng nghe trái tim mình. Hành trình ấy không chỉ là chuyến đi qua sa mạc, mà còn là quá trình nhận ra ước mơ, niềm tin, tình yêu và ý nghĩa của những dấu hiệu trong cuộc sống. Cuốn sách có văn phong giản dị, truyền cảm hứng, phù hợp với độc giả yêu thích phát triển bản thân và những câu chuyện mang màu sắc triết lý nhẹ nhàng.',
     79000.00, '/uploads/sach/nha-gia-kim.jpg', 'NXB Hội Nhà Văn', 5, '2020-06-18', '12+', 'Nhã Nam', 'Lê Chu Cầu', 'Tiếng Việt', 180, '20.5 x 13 x 1 cm', 228, 'Bìa mềm'),
    (8, '1984', '1984 là tiểu thuyết phản địa đàng kinh điển khắc họa một xã hội bị giám sát toàn diện, nơi ngôn ngữ, ký ức và tư tưởng cá nhân đều có thể bị kiểm soát. Qua số phận Winston Smith, George Orwell đặt ra những câu hỏi sắc lạnh về quyền lực, sự thật, tự do và nỗi sợ hãi trong đời sống chính trị. Tác phẩm phù hợp với độc giả yêu văn học kinh điển, quan tâm đến xã hội, truyền thông và những cảnh báo về chủ nghĩa toàn trị.',
     125000.00, '/uploads/sach/1984.jpg', 'NXB Văn Học', 6, '2021-02-22', '16+', 'Đông A', 'Đặng Phương-Nghi', 'Tiếng Việt', 360, '20.5 x 14.5 x 1.8 cm', 400, 'Bìa mềm'),
    (9, 'Trại Súc Vật', 'Trại Súc Vật là một ngụ ngôn chính trị ngắn gọn nhưng sắc lạnh về quyền lực, lý tưởng và sự tha hóa. Bằng câu chuyện các con vật nổi dậy giành quyền làm chủ trang trại, George Orwell phơi bày cách một cuộc cách mạng có thể bị bóp méo khi quyền lực rơi vào tay những kẻ thao túng. Cuốn sách dễ đọc, nhiều tầng nghĩa, phù hợp với độc giả muốn tiếp cận văn học chính trị qua một hình thức cô đọng và giàu tính biểu tượng.',
     85000.00, '/uploads/sach/trai-suc-vat.jpg', 'NXB Văn Học', 6, '2021-03-12', '15+', 'Đông A', 'An Lý', 'Tiếng Việt', 220, '20.5 x 14.5 x 1 cm', 184, 'Bìa mềm'),
    (10, 'Sapiens: Lược Sử Loài Người', 'Sapiens: Lược Sử Loài Người đưa người đọc đi qua hành trình phát triển của nhân loại từ thời săn bắt hái lượm, cách mạng nhận thức, cách mạng nông nghiệp cho đến xã hội hiện đại. Yuval Noah Harari kết hợp lịch sử, sinh học, kinh tế và triết học để lý giải vì sao Homo sapiens có thể thống trị thế giới và xây dựng những hệ thống niềm tin phức tạp. Sách phù hợp với độc giả yêu lịch sử, khoa học xã hội và những câu hỏi lớn về con người.',
     189000.00, '/uploads/sach/sapiens.jpg', 'NXB Thế Giới', 7, '2022-08-05', '16+', 'Omega Plus', 'Nguyễn Thủy Chung', 'Tiếng Việt', 620, '24 x 16 x 2.5 cm', 554, 'Bìa mềm'),
    (11, 'Homo Deus: Lược Sử Tương Lai', 'Homo Deus: Lược Sử Tương Lai tiếp nối những suy tư của Sapiens bằng cách nhìn về các tham vọng mới của con người trong thế kỷ dữ liệu, trí tuệ nhân tạo và công nghệ sinh học. Harari đặt câu hỏi liệu nhân loại sẽ theo đuổi bất tử, hạnh phúc và quyền năng đến đâu, đồng thời cảnh báo những hệ quả xã hội khi thuật toán ngày càng hiểu con người hơn chính họ. Cuốn sách phù hợp với độc giả quan tâm đến tương lai, công nghệ và triết học hiện đại.',
     209000.00, '/uploads/sach/homo-deus.jpg', 'NXB Thế Giới', 7, '2022-09-10', '16+', 'Omega Plus', 'Dương Ngọc Trà', 'Tiếng Việt', 650, '24 x 16 x 2.6 cm', 568, 'Bìa mềm'),
    (12, 'Harry Potter Và Hòn Đá Phù Thủy', 'Harry Potter Và Hòn Đá Phù Thủy là tập mở đầu đưa độc giả bước vào thế giới phép thuật Hogwarts cùng Harry, Ron và Hermione. Câu chuyện kết hợp phiêu lưu, bí mật, tình bạn, lòng can đảm và cảm giác kỳ diệu của tuổi thơ khi một cậu bé bình thường phát hiện mình thuộc về một thế giới hoàn toàn khác. Tác phẩm phù hợp với thiếu nhi, thanh thiếu niên và những độc giả yêu fantasy nhẹ nhàng, giàu trí tưởng tượng.',
     150000.00, '/uploads/sach/harry-potter-hon-da-phu-thuy.jpg', 'NXB Trẻ', 8, '2023-01-15', '9+', 'NXB Trẻ', 'Lý Lan', 'Tiếng Việt', 380, '20 x 14 x 1.8 cm', 366, 'Bìa mềm'),
    (13, 'Nghĩ Giàu Làm Giàu', 'Nghĩ Giàu Làm Giàu là cuốn sách kinh điển về tư duy thành công, mục tiêu rõ ràng, sự kiên trì và sức mạnh của niềm tin. Napoleon Hill tổng hợp những nguyên tắc được rút ra từ quá trình quan sát nhiều doanh nhân thành đạt, nhấn mạnh vai trò của khát vọng, kế hoạch, hành động và môi trường hỗ trợ. Sách phù hợp với người quan tâm đến kinh doanh, phát triển cá nhân và xây dựng tư duy chủ động trong công việc.',
     115000.00, '/uploads/sach/nghi-giau-lam-giau.jpg', 'NXB Tổng hợp TP.HCM', 9, '2020-11-20', '15+', 'First News', 'Việt Khương', 'Tiếng Việt', 290, '20.5 x 14.5 x 1.5 cm', 312, 'Bìa mềm'),
    (14, 'Tư Duy Nhanh Và Chậm', 'Tư Duy Nhanh Và Chậm khám phá hai hệ thống tư duy chi phối cách con người đánh giá, lựa chọn và mắc sai lầm trong đời sống. Daniel Kahneman trình bày nhiều thí nghiệm tâm lý nổi tiếng để giải thích thiên kiến nhận thức, sự tự tin quá mức, hiệu ứng khung và những giới hạn trong ra quyết định. Cuốn sách phù hợp với độc giả quan tâm đến tâm lý học, kinh tế hành vi, quản trị, đầu tư và cách suy nghĩ tỉnh táo hơn.',
     269000.00, '/uploads/sach/tu-duy-nhanh-va-cham.jpg', 'NXB Thế Giới', 10, '2021-07-30', '16+', 'Alpha Books', 'Hương Lan', 'Tiếng Việt', 700, '24 x 16 x 3 cm', 612, 'Bìa mềm'),
    (15, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Cho Tôi Xin Một Vé Đi Tuổi Thơ là chuyến tàu dịu dàng đưa người đọc trở lại thế giới trẻ nhỏ, nơi mọi thứ đều có thể được nhìn bằng trí tưởng tượng và sự hồn nhiên. Nguyễn Nhật Ánh kể về những trò nghịch ngợm, tình bạn, cảm giác lớn lên và nỗi buồn rất nhẹ của tuổi thơ bằng giọng văn ấm áp, dí dỏm. Cuốn sách phù hợp với độc giả mọi lứa tuổi, đặc biệt là những ai muốn tìm một khoảng lặng trong trẻo giữa đời sống bận rộn.',
     95000.00, '/uploads/sach/cho-toi-xin-mot-ve-di-tuoi-tho.jpg', 'NXB Trẻ', 1, '2018-09-05', '10+', 'NXB Trẻ', null, 'Tiếng Việt', 240, '20 x 13 x 1.2 cm', 208, 'Bìa mềm'),
    (16, 'Tôi Là Bêtô', 'Tôi Là Bêtô kể chuyện đời sống qua góc nhìn hồn nhiên, hóm hỉnh của một chú cún tên Bêtô. Từ những quan sát tưởng như nhỏ bé về con người, đồ vật, thói quen và tình cảm, câu chuyện mở ra nhiều suy ngẫm nhẹ nhàng về sự gắn bó, niềm vui, nỗi sợ và cách ta yêu thương nhau. Văn phong gần gũi, dễ đọc, phù hợp với thiếu nhi, gia đình và độc giả yêu những câu chuyện trong sáng nhưng vẫn có chiều sâu cảm xúc.',
     88000.00, '/uploads/sach/toi-la-beto.jpg', 'NXB Trẻ', 1, '2019-03-25', '8+', 'NXB Trẻ', null, 'Tiếng Việt', 210, '20 x 13 x 1 cm', 200, 'Bìa mềm')
ON DUPLICATE KEY UPDATE
    ten_sach = VALUES(ten_sach),
    mo_ta = VALUES(mo_ta),
    gia = VALUES(gia),
    anh_bia = VALUES(anh_bia),
    nha_xuat_ban = VALUES(nha_xuat_ban),
    ma_tac_gia = VALUES(ma_tac_gia),
    ngay_xuat_ban = VALUES(ngay_xuat_ban),
    do_tuoi = VALUES(do_tuoi),
    ten_nha_cung_cap = VALUES(ten_nha_cung_cap),
    nguoi_dich = VALUES(nguoi_dich),
    ngon_ngu = VALUES(ngon_ngu),
    trong_luong_gram = VALUES(trong_luong_gram),
    kich_thuoc_bao_bi = VALUES(kich_thuoc_bao_bi),
    so_trang = VALUES(so_trang),
    hinh_thuc = VALUES(hinh_thuc);

INSERT INTO sach_the_loai (ma_sach, ma_the_loai) VALUES
    (1, 1),
    (1, 4),
    (2, 2),
    (2, 5),
    (3, 3),
    (4, 4),
    (5, 1),
    (5, 4),
    (6, 3),
    (7, 4),
    (7, 2),
    (8, 4),
    (9, 4),
    (10, 4),
    (10, 5),
    (11, 4),
    (11, 3),
    (12, 4),
    (13, 2),
    (13, 5),
    (14, 2),
    (14, 5),
    (15, 1),
    (15, 4),
    (16, 1),
    (16, 4);

INSERT INTO ton_kho (ma_ton_kho, ma_sach, so_luong) VALUES
    (1, 1, 80),
    (2, 2, 120),
    (3, 3, 35),
    (4, 4, 60),
    (5, 5, 75),
    (6, 6, 28),
    (7, 7, 95),
    (8, 8, 58),
    (9, 9, 70),
    (10, 10, 42),
    (11, 11, 36),
    (12, 12, 90),
    (13, 13, 110),
    (14, 14, 40),
    (15, 15, 85),
    (16, 16, 78)
ON DUPLICATE KEY UPDATE
    ma_sach = VALUES(ma_sach),
    so_luong = VALUES(so_luong);

INSERT INTO sach_yeu_thich (ma_nguoi_dung, ma_sach) VALUES
    (2, 1),
    (2, 3),
    (2, 5),
    (3, 2),
    (3, 4);

INSERT INTO chi_tiet_gio_hang (
    ma_chi_tiet_gio_hang, ma_gio_hang, ma_sach, so_luong
) VALUES
    (1, 1, 2, 1),
    (2, 1, 6, 1),
    (3, 2, 1, 2)
ON DUPLICATE KEY UPDATE
    ma_gio_hang = VALUES(ma_gio_hang),
    ma_sach = VALUES(ma_sach),
    so_luong = VALUES(so_luong);

INSERT INTO ma_giam_gia (
    ma_giam_gia, ma_code, loai_giam, gia_tri, giam_toi_da,
    don_toi_thieu, so_luong, so_luong_da_dung,
    ngay_bat_dau, ngay_ket_thuc, trang_thai, ngay_tao
) VALUES
    (1, 'HELLO10', 'PHAN_TRAM', 10.00, 50000.00, 150000.00, 100, 1,
     DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 3 MONTH), 'HOAT_DONG', NOW()),
    (2, 'FREESHIP', 'SO_TIEN', 30000.00, 30000.00, 200000.00, 60, 0,
     DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 2 MONTH), 'HOAT_DONG', NOW())
ON DUPLICATE KEY UPDATE
    ma_code = VALUES(ma_code),
    loai_giam = VALUES(loai_giam),
    gia_tri = VALUES(gia_tri),
    giam_toi_da = VALUES(giam_toi_da),
    don_toi_thieu = VALUES(don_toi_thieu),
    so_luong = VALUES(so_luong),
    so_luong_da_dung = VALUES(so_luong_da_dung),
    ngay_bat_dau = VALUES(ngay_bat_dau),
    ngay_ket_thuc = VALUES(ngay_ket_thuc),
    trang_thai = VALUES(trang_thai);

INSERT INTO don_hang (
    ma_don_hang, ma_nguoi_dung, ma_dia_chi, ma_giam_gia,
    tong_tien, phi_van_chuyen, so_tien_giam,
    tong_tien_thanh_toan, trang_thai
) VALUES
    (1, 2, 1, 1, 295000.00, 30000.00, 29500.00, 295500.00, 'DA_GIAO'),
    (2, 3, 2, null, 555000.00, 30000.00, 0.00, 585000.00, 'DANG_GIAO')
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung),
    ma_dia_chi = VALUES(ma_dia_chi),
    ma_giam_gia = VALUES(ma_giam_gia),
    tong_tien = VALUES(tong_tien),
    phi_van_chuyen = VALUES(phi_van_chuyen),
    so_tien_giam = VALUES(so_tien_giam),
    tong_tien_thanh_toan = VALUES(tong_tien_thanh_toan),
    trang_thai = VALUES(trang_thai);

INSERT INTO chi_tiet_don_hang (
    ma_chi_tiet_don_hang, ma_don_hang, ma_sach, so_luong, don_gia, thanh_tien
) VALUES
    (1, 1, 1, 1, 89000.00, 89000.00),
    (2, 1, 2, 1, 96000.00, 96000.00),
    (3, 1, 5, 1, 110000.00, 110000.00),
    (4, 2, 4, 1, 135000.00, 135000.00),
    (5, 2, 3, 1, 420000.00, 420000.00)
ON DUPLICATE KEY UPDATE
    ma_don_hang = VALUES(ma_don_hang),
    ma_sach = VALUES(ma_sach),
    so_luong = VALUES(so_luong),
    don_gia = VALUES(don_gia),
    thanh_tien = VALUES(thanh_tien);

INSERT INTO thanh_toan (
    ma_thanh_toan, ma_don_hang, phuong_thuc, trang_thai,
    so_tien, thoi_gian_thanh_toan, ngay_tao
) VALUES
    (1, 1, 'CHUYEN_KHOAN', 'DA_THANH_TOAN', 295500.00, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
    (2, 2, 'TIEN_MAT', 'CHO_THANH_TOAN', 585000.00, null, NOW())
ON DUPLICATE KEY UPDATE
    ma_don_hang = VALUES(ma_don_hang),
    phuong_thuc = VALUES(phuong_thuc),
    trang_thai = VALUES(trang_thai),
    so_tien = VALUES(so_tien),
    thoi_gian_thanh_toan = VALUES(thoi_gian_thanh_toan),
    ngay_tao = VALUES(ngay_tao);

INSERT INTO danh_gia (
    ma_danh_gia, ma_nguoi_dung, ma_sach, so_sao,
    noi_dung, trang_thai, phan_hoi, ngay_tao, ngay_cap_nhat
) VALUES
    (1, 2, 1, 5, 'Sách rất hay, gợi lại nhiều ký ức tuổi thơ.', 'DA_DUYET', 'Cảm ơn bạn đã đánh giá.', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
    (2, 2, 2, 4, 'Nội dung hữu ích, dễ áp dụng vào công việc và giao tiếp hằng ngày.', 'DA_DUYET', 'Cảm ơn bạn đã đánh giá.', DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
    (3, 3, 4, 5, 'Bản dịch mượt, cảm xúc rất tốt.', 'CHO_DUYET', null, DATE_SUB(NOW(), INTERVAL 1 DAY), null)
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung),
    ma_sach = VALUES(ma_sach),
    so_sao = VALUES(so_sao),
    noi_dung = VALUES(noi_dung),
    trang_thai = VALUES(trang_thai),
    phan_hoi = VALUES(phan_hoi),
    ngay_cap_nhat = VALUES(ngay_cap_nhat);

INSERT INTO thong_bao (
    ma_thong_bao, ma_nguoi_dung, tieu_de, noi_dung,
    loai, da_doc, ngay_tao, duong_dan
) VALUES
    (1, 2, 'Đơn hàng đã giao', 'Đơn hàng của bạn đã được giao thành công.', 'DON_HANG', true, DATE_SUB(NOW(), INTERVAL 2 DAY), '/don-hang'),
    (2, 2, 'Mã giảm giá mới', 'Dùng mã FREESHIP cho đơn hàng từ 200.000đ.', 'MA_GIAM_GIA', false, NOW(), '/'),
    (3, 3, 'Đơn hàng đang giao', 'Đơn hàng của bạn đang trên đường giao.', 'DON_HANG', false, NOW(), '/don-hang'),
    (4, 1, 'Đánh giá chờ duyệt', 'Có đánh giá mới cần duyệt.', 'DANH_GIA', false, NOW(), '/quan-tri/danh-gia')
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung),
    tieu_de = VALUES(tieu_de),
    noi_dung = VALUES(noi_dung),
    loai = VALUES(loai),
    da_doc = VALUES(da_doc),
    ngay_tao = VALUES(ngay_tao),
    duong_dan = VALUES(duong_dan);
