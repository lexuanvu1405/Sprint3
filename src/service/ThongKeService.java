/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.ThongKe;
import repository.JdbcHelper;

/**
 *
 * @author lapto
 */
public class ThongKeService {

    public List<ThongKe> searchDataMa(String start, String end) throws SQLException {
    List<ThongKe> list = new ArrayList<>();
    Connection conn = JdbcHelper.getConnection();
    String sql = "SELECT SUM(TongTien) AS TongTien, COUNT(ID) AS SoLuongHoaDon "
               + "FROM HoaDon "
               + "WHERE NgayTao BETWEEN ? AND ?;";
    PreparedStatement preSt = conn.prepareCall(sql);
    preSt.setTimestamp(1, java.sql.Timestamp.valueOf(start + " 00:00:00"));
    preSt.setTimestamp(2, java.sql.Timestamp.valueOf(end + " 23:59:59"));

    ResultSet rs = preSt.executeQuery();
    while (rs.next()) {
        ThongKe tk = new ThongKe();
        tk.setTongtien(rs.getInt("TongTien"));
        tk.setTonghoadon(rs.getInt("SoLuongHoaDon"));
        list.add(tk);
    }

    rs.close();
    preSt.close();
    conn.close();
    return list;
}
}
