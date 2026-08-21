package com.ntb.bookstore.exception;

public class KhongCoDuLieuException extends RuntimeException {
    public KhongCoDuLieuException(String thongbao) {
        super(thongbao);
    }

    public KhongCoDuLieuException(String thongbao, Object id) {
        super(thongbao + " với mã: " + id);
    }
}
