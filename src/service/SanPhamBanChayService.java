
package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SanPhamBanChay;
import repository.JdbcHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SanPhamBanChayService {

    public List<SanPhamBanChay> showDataSanPhamBanChay() {
    List<SanPhamBanChay> list = new ArrayList<>();
    try {
        Connection conn = JdbcHelper.getConnection();
        String sql = "SELECT sp.ID AS IDSanPham, sp.Ma AS MASANPHAM, sp.Ten AS TENSANPHAM, "
                   + "SUM(hdct.SoLuong) AS SLBAN, SUM(hdct.SoLuong * hdct.DonGia) AS TongDoanhThu "
                   + "FROM HoaDonChiTiet hdct "
                   + "JOIN SanPham sp ON hdct.IDSanPham = sp.ID "
                   + "GROUP BY sp.ID, sp.Ma, sp.Ten "
                   + "ORDER BY SLBAN DESC"; // Sắp xếp theo số lượng bán giảm dần

        PreparedStatement preSt = conn.prepareStatement(sql);
        ResultSet rs = preSt.executeQuery();

        while (rs.next()) {
            SanPhamBanChay sanPhamBanChay = new SanPhamBanChay();
            sanPhamBanChay.setIdsp(rs.getInt("IDSanPham"));
            sanPhamBanChay.setMasp(rs.getString("MASANPHAM"));
            sanPhamBanChay.setTensp(rs.getString("TENSANPHAM"));
            sanPhamBanChay.setSlban(rs.getInt("SLBAN"));
            sanPhamBanChay.setTongtien(rs.getInt("TongDoanhThu"));
            list.add(sanPhamBanChay);
        }

        rs.close();
        preSt.close();
        conn.close();
    } catch (SQLException ex) {
        Logger.getLogger(SanPhamBanChayService.class.getName()).log(Level.SEVERE, null, ex);
    }
    return list;
}

    
    public static void main(String[] args) {
        System.out.println(new SanPhamBanChayService().showDataSanPhamBanChay());
    }
    
}
