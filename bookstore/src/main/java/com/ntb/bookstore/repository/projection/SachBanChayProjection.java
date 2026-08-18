package com.ntb.bookstore.repository.projection;

import java.math.BigDecimal;

public interface SachBanChayProjection {
    Long getMaSach();

    String getTenSach();

    String getAnhBia();

    String getTacGia();

    Long getSoLuongBan();

    BigDecimal getDoanhThu();
}
