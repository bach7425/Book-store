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

DELETE FROM lich_su_don_hang
WHERE ma_lich_su BETWEEN 1 AND 5
   OR ma_don_hang IN (
       SELECT ma_don_hang FROM don_hang
       WHERE ma_nguoi_dung IN (
           SELECT ma_nguoi_dung FROM nguoi_dung
           WHERE ten_dang_nhap IN ('hoai.an', 'minh.binh')
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

INSERT INTO tac_gia (ma_tac_gia, ten, tieu_su, anh_dai_dien) VALUES
    (1, 'Nguyễn Nhật Ánh', 'Tác giả Việt Nam nổi tiếng với các tác phẩm trong trẻo về tuổi thơ, tình bạn và những rung động đầu đời.', '/uploads/authors/nguyen-nhat-anh.jpg'),
    (2, 'Dale Carnegie', 'Tác giả và diễn giả người Mỹ, được biết đến qua các tác phẩm kinh điển về giao tiếp và phát triển bản thân.', '/uploads/authors/dale-carnegie.jpg'),
    (3, 'Robert C. Martin', 'Kỹ sư phần mềm, tác giả nhiều cuốn sách có ảnh hưởng về mã sạch, thiết kế và kiến trúc phần mềm.', '/uploads/authors/robert-martin.jpg'),
    (4, 'Haruki Murakami', 'Nhà văn Nhật Bản với phong cách hiện thực huyền ảo, giàu âm nhạc, cô đơn và suy tưởng.', '/uploads/authors/haruki-murakami.jpg'),
    (5, 'Paulo Coelho', 'Nhà văn Brazil nổi tiếng với những tác phẩm giàu chất triết lý, khơi gợi niềm tin và hành trình theo đuổi ước mơ.', '/uploads/authors/paulo-coelho.jpg'),
    (6, 'George Orwell', 'Nhà văn Anh được biết đến với các tác phẩm châm biếm chính trị sắc bén và những cảnh báo sâu sắc về quyền lực.', '/uploads/authors/george-orwell.jpg'),
    (7, 'Yuval Noah Harari', 'Nhà sử học Israel, tác giả các cuốn sách phổ biến về lịch sử nhân loại, công nghệ và tương lai xã hội.', '/uploads/authors/yuval-noah-harari.jpg'),
    (8, 'J.K. Rowling', 'Nhà văn Anh nổi tiếng với thế giới phép thuật Harry Potter, giàu trí tưởng tượng và sức hấp dẫn với nhiều thế hệ độc giả.', '/uploads/authors/jk-rowling.jpg'),
    (9, 'Napoleon Hill', 'Tác giả người Mỹ với nhiều tác phẩm kinh điển về tư duy thành công, mục tiêu và phát triển cá nhân.', '/uploads/authors/napoleon-hill.jpg'),
    (10, 'Daniel Kahneman', 'Nhà tâm lý học đoạt giải Nobel Kinh tế, nổi tiếng với các nghiên cứu về phán đoán, ra quyết định và thiên kiến nhận thức.', '/uploads/authors/daniel-kahneman.jpg')
ON DUPLICATE KEY UPDATE
    ten = VALUES(ten),
    tieu_su = VALUES(tieu_su),
    anh_dai_dien = VALUES(anh_dai_dien);

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
    ma_tac_gia, ngay_xuat_ban
) VALUES
    (1, 'Mắt Biếc', 'Câu chuyện trong trẻo và man mác buồn về tình yêu tuổi học trò, ký ức làng quê và những điều không thể nói thành lời.',
     89000.00, '/uploads/sach/mat-biec.jpg', 'NXB Trẻ', 1, '2019-05-20'),
    (2, 'Đắc Nhân Tâm', 'Những nguyên tắc nền tảng giúp ứng xử khéo léo, giao tiếp hiệu quả và tạo thiện cảm trong cuộc sống.',
     96000.00, '/uploads/sach/dac-nhan-tam.jpg', 'NXB Tổng hợp TP.HCM', 2, '2020-03-10'),
    (3, 'Clean Code', 'Hướng dẫn viết mã rõ ràng, dễ đọc, dễ bảo trì và có chất lượng cao cho lập trình viên chuyên nghiệp.',
     420000.00, '/uploads/sach/clean-code.jpg', 'Prentice Hall', 3, '2008-08-01'),
    (4, 'Rừng Na Uy', 'Một tiểu thuyết sâu lắng về tuổi trẻ, tình yêu, mất mát và hành trình trưởng thành nhiều day dứt.',
     135000.00, '/uploads/sach/rung-na-uy.jpg', 'NXB Hội Nhà Văn', 4, '2021-11-12'),
    (5, 'Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Ký ức tuổi thơ miền quê trong veo, ấm áp và nhiều rung động qua lăng kính hồn nhiên của trẻ nhỏ.',
     110000.00, '/uploads/sach/toi-thay-hoa-vang.jpg', 'NXB Trẻ', 1, '2018-04-15'),
    (6, 'Clean Architecture', 'Các nguyên tắc thiết kế kiến trúc phần mềm bền vững, tách biệt trách nhiệm và dễ thích nghi với thay đổi.',
     510000.00, '/uploads/sach/clean-architecture.jpg', 'Prentice Hall', 3, '2017-09-20'),
    (7, 'Nhà Giả Kim', 'Câu chuyện giàu chất ngụ ngôn về hành trình đi tìm kho báu, lắng nghe trái tim và theo đuổi vận mệnh cá nhân.',
     79000.00, '/uploads/sach/nha-gia-kim.jpg', 'NXB Hội Nhà Văn', 5, '2020-06-18'),
    (8, '1984', 'Tiểu thuyết phản địa đàng kinh điển về giám sát, kiểm soát tư tưởng và sự mong manh của tự do cá nhân.',
     125000.00, '/uploads/sach/1984.jpg', 'NXB Văn Học', 6, '2021-02-22'),
    (9, 'Trại Súc Vật', 'Một ngụ ngôn chính trị ngắn gọn, sắc lạnh về quyền lực, lý tưởng bị bóp méo và vòng lặp áp bức.',
     85000.00, '/uploads/sach/trai-suc-vat.jpg', 'NXB Văn Học', 6, '2021-03-12'),
    (10, 'Sapiens: Lược Sử Loài Người', 'Bức tranh rộng lớn về lịch sử nhân loại, từ cách mạng nhận thức đến xã hội hiện đại và những câu hỏi về tương lai.',
     189000.00, '/uploads/sach/sapiens.jpg', 'NXB Thế Giới', 7, '2022-08-05'),
    (11, 'Homo Deus: Lược Sử Tương Lai', 'Một góc nhìn táo bạo về tương lai của con người trước dữ liệu lớn, trí tuệ nhân tạo và khát vọng vượt giới hạn sinh học.',
     209000.00, '/uploads/sach/homo-deus.jpg', 'NXB Thế Giới', 7, '2022-09-10'),
    (12, 'Harry Potter Và Hòn Đá Phù Thủy', 'Tập mở đầu đưa độc giả bước vào thế giới phép thuật Hogwarts, nơi tình bạn, lòng can đảm và bí mật cùng hiện diện.',
     150000.00, '/uploads/sach/harry-potter-hon-da-phu-thuy.jpg', 'NXB Trẻ', 8, '2023-01-15'),
    (13, 'Nghĩ Giàu Làm Giàu', 'Cuốn sách kinh điển về tư duy làm giàu, mục tiêu rõ ràng, sự kiên trì và sức mạnh của niềm tin.',
     115000.00, '/uploads/sach/nghi-giau-lam-giau.jpg', 'NXB Tổng hợp TP.HCM', 9, '2020-11-20'),
    (14, 'Tư Duy Nhanh Và Chậm', 'Khám phá hai hệ thống tư duy chi phối cách con người phán đoán, lựa chọn và mắc sai lầm trong đời sống.',
     269000.00, '/uploads/sach/tu-duy-nhanh-va-cham.jpg', 'NXB Thế Giới', 10, '2021-07-30'),
    (15, 'Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'Một chuyến tàu dịu dàng trở về tuổi thơ, nơi ký ức, nghịch ngợm và nỗi buồn rất nhẹ cùng song hành.',
     95000.00, '/uploads/sach/cho-toi-xin-mot-ve-di-tuoi-tho.jpg', 'NXB Trẻ', 1, '2018-09-05'),
    (16, 'Tôi Là Bêtô', 'Câu chuyện hồn nhiên, hóm hỉnh qua góc nhìn của một chú cún, mở ra nhiều suy ngẫm dễ thương về đời sống.',
     88000.00, '/uploads/sach/toi-la-beto.jpg', 'NXB Trẻ', 1, '2019-03-25')
ON DUPLICATE KEY UPDATE
    ten_sach = VALUES(ten_sach),
    mo_ta = VALUES(mo_ta),
    gia = VALUES(gia),
    anh_bia = VALUES(anh_bia),
    nha_xuat_ban = VALUES(nha_xuat_ban),
    ma_tac_gia = VALUES(ma_tac_gia),
    ngay_xuat_ban = VALUES(ngay_xuat_ban);

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

INSERT INTO lich_su_don_hang (
    ma_lich_su, ma_don_hang, trang_thai, ghi_chu, ma_nguoi_cap_nhat, thoi_gian
) VALUES
    (1, 1, 'CHO_XU_LY', 'Khách hàng đã tạo đơn hàng.', null, DATE_SUB(NOW(), INTERVAL 4 DAY)),
    (2, 1, 'DA_XAC_NHAN', 'Đơn hàng đã được xác nhận.', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
    (3, 1, 'DA_GIAO', 'Đơn hàng đã giao thành công.', 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
    (4, 2, 'CHO_XU_LY', 'Khách hàng đã tạo đơn hàng.', null, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    (5, 2, 'DANG_GIAO', 'Đơn hàng đang trên đường giao.', 1, NOW())
ON DUPLICATE KEY UPDATE
    ma_don_hang = VALUES(ma_don_hang),
    trang_thai = VALUES(trang_thai),
    ghi_chu = VALUES(ghi_chu),
    ma_nguoi_cap_nhat = VALUES(ma_nguoi_cap_nhat),
    thoi_gian = VALUES(thoi_gian);

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
    (4, 1, 'Đánh giá chờ duyệt', 'Có đánh giá mới cần duyệt.', 'DANH_GIA', false, NOW(), '/admin/danh-gia')
ON DUPLICATE KEY UPDATE
    ma_nguoi_dung = VALUES(ma_nguoi_dung),
    tieu_de = VALUES(tieu_de),
    noi_dung = VALUES(noi_dung),
    loai = VALUES(loai),
    da_doc = VALUES(da_doc),
    ngay_tao = VALUES(ngay_tao),
    duong_dan = VALUES(duong_dan);
