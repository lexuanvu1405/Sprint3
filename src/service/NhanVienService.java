package service;

import java.sql.Statement;
import java.util.List;
import model.NhanVien;
import repository.JdbcHelper;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NhanVienService extends SellingApplicationImpl<NhanVien, Integer> {

    String insert_sql = """
                        INSERT INTO [dbo].[NhanVien]
                                   ([Ma]
                                   ,[Passwords]
                                   ,[Ten]
                                   ,[SDT]
                                   ,[Email]
                                   ,[ChucVu]
                                   ,[TrangThai]
                                   ,[Luong]
                                   ,[NgaySinh]
                                   ,[DiaChi])
                             VALUES(?,?,?,?,?,?,?,?,?,?)
                        """;
    String update_sql = """
                       UPDATE NhanVien
                          SET Ma = ?
                             ,Passwords = ?
                             ,Ten = ?
                             ,SDT = ?
                             ,Email = ?
                             ,ChucVu = ?
                             ,TrangThai =?
                             ,Luong = ?
                             ,NgaySinh = ?
                             ,DiaChi = ?
                        WHERE ID = ?
                       """;
    String delete_sql = """
                        DELETE FROM NhanVien
                        WHERE ID = ?
                        """;

    String select_all = """
                        select * from NhanVien
                        """;
    String selectById = """
                        select * from nhanVien
                        WHERE ID = ?
                        """;
    String selectByMa = """
                        select * from nhanVien
                        WHERE Ma= ?

                        """;

    @Override
    public void insert(NhanVien entity) {
        try (Connection conn = JdbcHelper.getConnection();
         // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy giá trị ID được tạo tự động
         PreparedStatement ps = conn.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS)) {
        
        ps.setString(1, entity.getMa());
        ps.setString(2, entity.getPass());
        ps.setString(3, entity.getTen());
        ps.setString(4, entity.getSdt());
        ps.setString(5, entity.getEmail());
        ps.setBoolean(6, entity.isChucVu());
        ps.setBoolean(7, entity.isTrangThai());
        ps.setObject(8, entity.getLuong()); // Sửa kiểu dữ liệu phù hợp với Luong (có thể là BigDecimal, double, ...)
        // Giả sử entity.getNgaySinh() trả về java.util.Date, chuyển sang java.sql.Date
        ps.setDate(9, new java.sql.Date(entity.getNgaySinh().getTime()));
        ps.setString(10, entity.getDiaChi());

        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    // Gán id mới tạo cho entity nếu cần
                    entity.setId(generatedId);
                    System.out.println("ID được tạo: " + generatedId);
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    @Override
    public void update(NhanVien entity) {
        JdbcHelper.update(update_sql,
                entity.getMa(),
                entity.getPass(),
                entity.getTen(),
                entity.getSdt(),
                entity.getEmail(),
                entity.isChucVu(),
                entity.isTrangThai(),
                entity.getLuong(),
                entity.getNgaySinh(),
                entity.getDiaChi(),
                entity.getId());
    }

    @Override
    public void delete(Integer id) {
        JdbcHelper.update(delete_sql, id);
    }

    @Override
    public NhanVien selectById(Integer id) {
        List<NhanVien> list = this.selectBySql(selectById, id);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public NhanVien selectByMa(String ma) {
        List<NhanVien> list = this.selectBySql(selectByMa, ma);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Override
    public List<NhanVien> selectAll() {
        return this.selectBySql(select_all);
    }

    @Override
    protected List<NhanVien> selectBySql(String sql, Object... args) {
        List<NhanVien> list = new ArrayList<>();
        try {

            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {

                NhanVien nv = new NhanVien();
                nv.setId(rs.getInt("ID"));
                nv.setMa(rs.getString("Ma"));
                nv.setPass(rs.getString("Passwords"));
                nv.setTen(rs.getString("Ten"));
                nv.setSdt(rs.getString("SDT"));
                nv.setEmail(rs.getString("Email"));
                nv.setChucVu(rs.getBoolean("ChucVu"));
                nv.setTrangThai(rs.getBoolean("TrangThai"));
                nv.setLuong(rs.getDouble("Luong"));
                nv.setNgaySinh(rs.getDate("NgaySinh"));
                nv.setDiaChi(rs.getString("DiaChi"));
                list.add(nv);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<NhanVien> searchDataMa(String ma) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = JdbcHelper.getConnection();
        String sql = "SELECT * FROM NhanVien WHERE Ma LIKE ?";
        PreparedStatement preSt = conn.prepareStatement(sql); // ✅ Sửa lỗi prepareCall

        preSt.setString(1, "%" + ma + "%");
        ResultSet rs = preSt.executeQuery();

        while (rs.next()) {
            NhanVien nv = new NhanVien();
            nv.setId(rs.getInt("ID"));
            nv.setMa(rs.getString("Ma"));
            nv.setPass(rs.getString("Passwords"));
            nv.setTen(rs.getString("Ten"));
            nv.setSdt(rs.getString("SDT"));
            nv.setEmail(rs.getString("Email"));
            nv.setChucVu(rs.getInt("ChucVu") == 1);
            nv.setTrangThai(rs.getInt("TrangThai") == 1);
            nv.setLuong(rs.getDouble("Luong"));
            nv.setNgaySinh(rs.getDate("NgaySinh"));
            nv.setDiaChi(rs.getString("DiaChi"));

            list.add(nv);
        }

        rs.close();
        preSt.close();
        conn.close();
        return list;
    }

    public List<NhanVien> searchDataTen(String Ten) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = JdbcHelper.getConnection();
        String sql = "select * FROM NhanVien WHERE Ten LIKE ?";
        PreparedStatement preSt = conn.prepareCall(sql);
        preSt.setString(1, "%" + Ten + "%");
        ResultSet rs = preSt.executeQuery();
        while (rs.next()) {
            NhanVien nv = new NhanVien();
            nv.setId(rs.getInt("ID"));
            nv.setMa(rs.getString("Ma"));
            nv.setPass(rs.getString("Passwords"));
            nv.setTen(rs.getString("Ten"));
            nv.setSdt(rs.getString("SDT"));
            nv.setEmail(rs.getString("Email"));
            nv.setChucVu(rs.getBoolean("ChucVu"));
            nv.setTrangThai(rs.getBoolean("TrangThai"));
            nv.setLuong(rs.getDouble("Luong"));
            nv.setNgaySinh(rs.getDate("NamSinh"));
            nv.setDiaChi(rs.getString("DiaChi"));
            list.add(nv);
        }
        rs.close();
        preSt.close();
        conn.close();
        return list;
    }

    public List<NhanVien> searchDataTrangThai(String trangThai) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = JdbcHelper.getConnection();
        String sql = "SELECT * FROM NhanVien WHERE TrangThai = ?";
        PreparedStatement preSt = conn.prepareStatement(sql);

        preSt.setInt(1, Integer.parseInt(trangThai));

        ResultSet rs = preSt.executeQuery();
        while (rs.next()) {
            NhanVien nv = new NhanVien();
            nv.setId(rs.getInt("ID"));
            nv.setMa(rs.getString("Ma"));
            nv.setPass(rs.getString("Passwords"));
            nv.setTen(rs.getString("Ten"));
            nv.setSdt(rs.getString("SDT"));
            nv.setEmail(rs.getString("Email"));
            nv.setChucVu(rs.getInt("ChucVu") == 1);
            nv.setTrangThai(rs.getInt("TrangThai") == 1);
            nv.setLuong(rs.getDouble("Luong"));
            nv.setNgaySinh(rs.getDate("NgaySinh"));
            nv.setDiaChi(rs.getString("DiaChi"));

            list.add(nv);
        }
        rs.close();
        preSt.close();
        conn.close();
        return list;
    }

    public List<NhanVien> searchDataChucVu(String chucVu) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        Connection conn = JdbcHelper.getConnection();
        String sql = "SELECT * FROM NhanVien WHERE ChucVu = ?";
        PreparedStatement preSt = conn.prepareStatement(sql);
        preSt.setInt(1, Integer.parseInt(chucVu));
        ResultSet rs = preSt.executeQuery();
        while (rs.next()) {
            NhanVien nv = new NhanVien();
            nv.setId(rs.getInt("ID"));
            nv.setMa(rs.getString("Ma"));
            nv.setPass(rs.getString("Passwords"));
            nv.setTen(rs.getString("Ten"));
            nv.setSdt(rs.getString("SDT"));
            nv.setEmail(rs.getString("Email"));
            nv.setChucVu(rs.getInt("ChucVu") == 1);
            nv.setTrangThai(rs.getInt("TrangThai") == 1);
            nv.setLuong(rs.getDouble("Luong"));
            nv.setNgaySinh(rs.getDate("NgaySinh"));
            nv.setDiaChi(rs.getString("DiaChi"));
            list.add(nv);
        }
        rs.close();
        preSt.close();
        conn.close();
        return list;
    }

    public void updateMK(NhanVien nv) {
        String sql = "update NhanVien set Passwords = ? where ID = ?";
        JdbcHelper.update(sql,
                nv.getPass(),
                nv.getId()
        );
    }

    public void paging(int page, int limit) {

    }

}
