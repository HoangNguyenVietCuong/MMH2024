/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import context.DBContext;
import entity.Account;
import entity.Cart;
import entity.Review;
import entity.SoLuongDaBan;
import entity.TongChiTieuBanHang;
import entity.Supplier;
//import entity.Account;
import entity.Category;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Product;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;



public class DAO {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    public List<Product> getAllProduct() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<SoLuongDaBan> getTop10SanPhamBanChay() {
        List<SoLuongDaBan> list = new ArrayList<>();
        String query = "select top(10) *\r\n"
        		+ "from SoLuongDaBan\r\n"
        		+ "order by soLuongDaBan desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new SoLuongDaBan(rs.getInt(1),
                        rs.getInt(2)
                  ));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Invoice> getAllInvoice() {
        List<Invoice> list = new ArrayList<>();
        String query = "select * from Invoice";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
            	int invoiceId = rs.getInt(1);
                list.add(new Invoice(invoiceId,
                        rs.getInt(2),
                        rs.getDouble(3),
                        rs.getDate(4),
                        getAllItemOfInvoice(invoiceId)
                       ));
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return list;
    }
    
    // Lấy danh sách item trong đơn hàng
    public List<InvoiceItem> getAllItemOfInvoice(int invoiceId){
    	List<InvoiceItem> list = new ArrayList<>();
    	String query = "SELECT cthd.id, cthd.productId, cthd.amount, cthd.totalPrice "
    			+ "FROM QuanLyChiTiet qlct " 
    			+ "JOIN ChiTietHoaDon cthd ON qlct.idChiTietHoaDon = cthd.id "
    			+ "WHERE qlct.id = ?";
    	try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, invoiceId);
            rs = ps.executeQuery();
            while (rs.next()) {
            	
            	list.add(new InvoiceItem(rs.getInt("id"), rs.getInt("productId"), rs.getInt("amount"), rs.getDouble("totalPrice")));
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    	return list;
    }
    
    public int countAllProductBySellID(int sell_ID) {
        String query = "select count(*) from Product where sell_ID=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, sell_ID);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public int getSellIDByProductID(int productID) {
        String query = "select sell_ID\r\n"
        		+ "from Product\r\n"
        		+ "where [id]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, productID);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public double totalMoneyDay(int day) {
        String query = "select \r\n"
        		+ "	SUM(tongGia) \r\n"
        		+ "from Invoice\r\n"
        		+ "where DATEPART(dw,[ngayXuat]) = ?\r\n"
        		+ "Group by ngayXuat ";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, day);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getDouble(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public double totalMoneyMonth(int month) {
        String query = "select SUM(tongGia) from Invoice\r\n"
        		+ "where MONTH(ngayXuat)=?\r\n"
        		+ "Group by MONTH(ngayXuat)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, month);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getDouble(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public int countAllProduct() {
        String query = "select count(*) from Product";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public double sumAllInvoice() {
        String query = "select SUM(tongGia) from Invoice";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getDouble(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public int countAllReview() {
        String query = "select count(*) from Review";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public int getCateIDByProductID(String id) {
        String query = "select [cateID] from Product\r\n"
        		+ "where [id] =?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
               return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }
    
    public List<Account> getAllAccount() {
        List<Account> list = new ArrayList<>();
        String query = "select * from Account";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Account(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                		rs.getString(6),
                		rs.getInt(7)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Supplier> getAllSupplier() {
        List<Supplier> list = new ArrayList<>();
        String query = "select * from Supplier";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Supplier(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                		rs.getInt(6)));
            }
        } catch (Exception e) {
        }
        return list;
    }
   
    public List<TongChiTieuBanHang> getTop5KhachHang() {
        List<TongChiTieuBanHang> list = new ArrayList<>();
        String query = "select top(5) *\r\n"
        		+ "from TongChiTieuBanHang\r\n"
        		+ "order by TongChiTieu desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new TongChiTieuBanHang(rs.getInt(1),
                        rs.getDouble(2),
                        rs.getDouble(3)
                       ));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<TongChiTieuBanHang> getTop5NhanVien() {
        List<TongChiTieuBanHang> list = new ArrayList<>();
        String query = "select top(5) *\r\n"
        		+ "from TongChiTieuBanHang\r\n"
        		+ "order by TongBanHang desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new TongChiTieuBanHang(rs.getInt(1),
                        rs.getDouble(2),
                        rs.getDouble(3)
                       ));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<Product> getTop3() {
        List<Product> list = new ArrayList<>();
        String query = "select top 3 * from Product";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<Product> getNext3Product(int amount) {
        List<Product> list = new ArrayList<>();
        String query = "SELECT *\n"
                + "  FROM Product\n"
                + " ORDER BY id\n"
                + "OFFSET ? ROWS\n"
                + " FETCH NEXT 3 ROWS ONLY";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> getNext4NikeProduct(int amount) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where cateID=2\r\n"
        		+ "order by id desc\r\n"
        		+ "offset ? rows\r\n"
        		+ "fetch next 4 rows only";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> getNext4AdidasProduct(int amount) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where cateID=1\r\n"
        		+ "order by id desc\r\n"
        		+ "offset ? rows\r\n"
        		+ "fetch next 4 rows only";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> getProductByCID(String cid) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\n"
                + "where cateID = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, cid);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<Product> getProductBySellIDAndIndex(int id, int indexPage) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product \r\n"
        		+ "where sell_ID = ?\r\n"
        		+ "order by [id]\r\n"
        		+ "offset ? rows\r\n"
        		+ "fetch next 5 rows only";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setInt(2, (indexPage-1)*5);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> getProductByIndex(int indexPage) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product \r\n"
        		+ "order by [id]\r\n"
        		+ "offset ? rows\r\n"
        		+ "fetch next 9 rows only";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, (indexPage-1)*9);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<Product> searchByName(String txtSearch) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\n"
                + "where [name] like ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, "%" + txtSearch + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Invoice> searchByNgayXuat(String ngayXuat) {
        List<Invoice> list = new ArrayList<>();
        String query = "select * from Invoice\r\n"
        		+ "where [ngayXuat] ='"+ngayXuat+"'";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
//            ps.setString(1,ngayXuat);
            rs = ps.executeQuery();
            while (rs.next()) {
            	int invoiceId = rs.getInt(1);
                list.add(new Invoice(invoiceId,
                        rs.getInt(2),
                        rs.getDouble(3),
                        rs.getDate(4),
                        getAllItemOfInvoice(invoiceId)
                       ));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchPriceUnder100() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where [price] < 100";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchPrice100To200() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where [price] >= 100 and [price]<=200";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchColorWhite() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where color = 'White'";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchColorGray() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where color = 'Gray'";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchColorBlack() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where color = 'Black'";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchColorYellow() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where color = 'Yellow'";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    public List<Product> searchByPriceMinToMax(String priceMin,String priceMax) {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where [price] >= ? and [price]<=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, priceMin);
            ps.setString(2, priceMax);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> searchPriceAbove200() {
        List<Product> list = new ArrayList<>();
        String query = "select * from Product\r\n"
        		+ "where [price] > 200";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> getRelatedProduct(int cateIDProductDetail) {
        List<Product> list = new ArrayList<>();
        String query = "select top 4 * from product\r\n"
        		+ "where [cateID] =?\r\n"
        		+ "order by id desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, cateIDProductDetail);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    
    public List<Review> getAllReviewByProductID(String productId) {
        List<Review> list = new ArrayList<>();
        String query = "select * from Review\r\n"
        		+ "where [productID] =?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, productId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Review(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getDate(4)
                       ));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public Product getProductByID(String id) {
        String query = "select * from Product\n"
                + "where id = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14));
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public List<Cart> getCartByAccountID(int accountID) {
    	 List<Cart> list = new ArrayList<>();
        String query = "select * from Cart where accountID = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Cart(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getString(5)));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public Cart checkCartExist(int accountID,int productID) {

       String query = "select * from Cart\r\n"
       		+ "where [accountID] = ? and [productID] = ?";
       try {
           conn = new DBContext().getConnection();//mo ket noi voi sql
           ps = conn.prepareStatement(query);
           ps.setInt(1, accountID);
           ps.setInt(2, productID);
           rs = ps.executeQuery();
           while (rs.next()) {
               return new Cart(rs.getInt(1),
                       rs.getInt(2),
                       rs.getInt(3),
                       rs.getInt(4),
                       rs.getString(5));
           }
       } catch (Exception e) {
       }
      return null;
   }
    
    public int checkAccountAdmin(int userID) {

        String query = "select isAdmin from Account where [uID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, userID);

            rs = ps.executeQuery();  
            while (rs.next()) {
            	 return rs.getInt(1);
             }
        } catch (Exception e) {
        	
        }
        return 0;
    }
    
    public TongChiTieuBanHang checkTongChiTieuBanHangExist(int userID) {

        String query = "select * from TongChiTieuBanHang where [userID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, userID);
           
            rs = ps.executeQuery();
            while (rs.next()) {
                return new TongChiTieuBanHang(rs.getInt(1),
                        rs.getDouble(2),
                        rs.getDouble(3)
                        );
            }
        } catch (Exception e) {
        }
       return null;
    }
    
    public SoLuongDaBan checkSoLuongDaBanExist(int productID) {

        String query = "select * from SoLuongDaBan where productID = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, productID);
           
            rs = ps.executeQuery();
            while (rs.next()) {
                return new SoLuongDaBan(rs.getInt(1),
                        rs.getInt(2)
                       );
            }
        } catch (Exception e) {
        }
       return null;
    }
    
    
    
    public List<Category> getAllCategory() {
        List<Category> list = new ArrayList<>();
        String query = "select * from Category";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
          
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(rs.getInt(1),
                        rs.getString(2)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    
//
    public Product getLast() {
        String query = "select top 1 * from Product\n"
                + "order by id desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12));
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public List<Product> get8Last() {
    	List<Product> list = new ArrayList<>();
        String query = "select top 8 * from Product order by id desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
            	list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> get4NikeLast() {
    	List<Product> list = new ArrayList<>();
        String query = "select top 4 * from Product\r\n"
        		+ "where cateID = 2\r\n"
        		+ "order by id desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
            	list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }
    
    public List<Product> get4AdidasLast() {
    	List<Product> list = new ArrayList<>();
        String query = "select top 4 * from Product\r\n"
        		+ "where cateID = 1\r\n"
        		+ "order by id desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
            	list.add(new Product(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getDouble(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getString(14)));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public Account login(String user, String pass) {
        String query = "select * from Account\n"
                + "where [user] = ?\n"
                + "and pass = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Account(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                		rs.getString(6),
                		rs.getInt(7));
            }
        } catch (Exception e) {
        }
        return null;
    }

    public Account checkAccountExist(String user) {
        String query = "select * from Account\n"
                + "where [user] = ?\n";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Account(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                		rs.getString(6),
                		rs.getInt(7));
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public Account checkAccountExistByUsernameAndEmail(String username, String email) {
        String query = "select * from Account where [user]=? and [email]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, email);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Account(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                		rs.getString(6),
                		rs.getInt(7));
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public Review getNewReview(int accountID, int productID) {
        String query = "select top 1 * from Review\r\n"
        		+ "where accountID = ? and productID = ?\r\n"
        		+ "order by maReview desc";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            rs = ps.executeQuery();
            while (rs.next()) {
                return new Review(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getDate(4));
            }
        } catch (Exception e) {
        }
        return null;
    }

    public void singup(String user, String pass, String email,int keyId) {
        String query = "insert into Account\n"
                + "values(?,?,0,0,?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, email);
            ps.setInt(4, keyId);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void deleteInvoiceByAccountId(String id) {
        String query = "delete from Invoice\n"
                + "where [accountID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteTongChiTieuBanHangByUserID(String id) {
        String query = "delete from TongChiTieuBanHang\n"
                + "where [userID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    
    public void deleteProduct(String pid) {
        String query = "delete from Product\n"
                + "where [id] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, pid);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteProductBySellID(String id) {
        String query = "delete from Product\n"
                + "where [sell_ID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteCartByAccountID(int accountID) {
        String query = "delete from Cart \r\n"
        		+ "where [accountID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteCartByProductID(String productID) {
        String query = "delete from Cart where [productID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, productID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteReviewByProductID(String productID) {
        String query = "delete from Review where [productID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, productID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteReviewByAccountID(String id) {
        String query = "delete from Review where [accountID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteAccount(String id) {
        String query = "delete from Account where uID= ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteSupplier(String idSupplier) {
        String query = "delete from Supplier\r\n"
        		+ "where idSupplier=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, idSupplier);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void deleteCart(int productID) {
        String query = "delete from Cart where productID = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, productID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void insertProduct(String name, String image, String price,
            String title, String description, String category, int sid, String model, String color, String delivery, String image2, String image3, String image4) {
        String query = "insert Product([name],[image],[price],[title],\r\n"
        		+ "[description],[cateID],[sell_ID],[model],\r\n"
        		+ "[color],[delivery],[image2],[image3],[image4])\r\n"
        		+ "values(N'"+name+"','"+image+"','"+price+"',N'"+title+"',N'"+description+"','"+category+"','"+sid+"',N'"+model+"',N'"+color+"',N'"+delivery+"','"+image2+"','"+image3+"','"+image4+"')";
        try {
        	
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
			/*
			 * ps.setString(1, name); ps.setString(2, image); ps.setString(3, price);
			 * ps.setString(4, title); ps.setString(5, description); ps.setString(6,
			 * category); ps.setInt(7, sid); ps.setString(8, model); ps.setString(9, color);
			 * ps.setString(10, delivery); ps.setString(11, image2); ps.setString(12,
			 * image3); ps.setString(13, image4);
			 */
            ps.executeUpdate();
           
        } catch (Exception e) {
        	
        	
        }
    }
    
    public void insertAccount(String user, String pass, String isSell,
    		String isAdmin, String email) {
        String query = "insert Account([user], pass, isSell, isAdmin, email)\r\n"
        		+ "values(?,?,?,?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, isSell);
            ps.setString(4, isAdmin);
            ps.setString(5, email);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void insertTongChiTieuBanHang(int userID, double tongChiTieu, double tongBanHang) {
        String query = "insert TongChiTieuBanHang(userID,TongChiTieu,TongBanHang)\r\n"
        		+ "values(?,?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, userID);
            ps.setDouble(2, tongChiTieu);
            ps.setDouble(3, tongBanHang);
        
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void insertSoLuongDaBan(int productID, int soLuongDaBan) {
        String query = "insert SoLuongDaBan(productID,soLuongDaBan)\r\n"
        		+ "values(?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, productID);
            ps.setInt(2, soLuongDaBan);
           
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void insertSupplier(String nameSupplier, String phoneSupplier, String emailSupplier, String addressSupplier, String cateID) {
        String query = "insert Supplier(nameSupplier, phoneSupplier, emailSupplier, addressSupplier, cateID) \r\n"
        		+ "values(?,?,?,?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, nameSupplier);
            ps.setString(2, phoneSupplier);
            ps.setString(3, emailSupplier);
            ps.setString(4, addressSupplier);
            ps.setString(5, cateID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    private static java.sql.Date getCurrentDate() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Date(today.getTime());
    }
  
    public void insertReview(int accountID, int productID, String contentReview) {
        String query = "insert Review(accountID, productID, contentReview, dateReview)\r\n"
        		+ "values(?,?,?,?)";

        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            ps.setString(3, contentReview);
            ps.setDate(4,getCurrentDate());
            ps.executeUpdate();
           
        } catch (Exception e) {	
        }
    }
    
    // Thêm hóa đơn vào dtb
    public int insertInvoice(int accountID, double tongGia, List<Cart> list, List<Double> listGia) {
        String query = "INSERT Invoice(accountID, tongGia, ngayXuat)\r\n "+"VALUES (?, ?, ?)";
        int generatedId = -1; // Khởi tạo giá trị mặc định

        try {
            conn = new DBContext().getConnection(); // Mở kết nối SQL
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS); // Cho phép trả về khóa vừa sinh
            ps.setInt(1, accountID);
            ps.setDouble(2, tongGia);
            ps.setDate(3, getCurrentDate()); // Lấy thời gian hiện tại
            System.out.println(ps.executeUpdate());

            // Lấy `maHD` mới được tạo
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1); // Lấy giá trị khóa chính
            }
            System.out.println(generatedId);
            insertQuanLyChiTiet(generatedId, list, listGia);
        } catch (Exception e) {
        	System.out.println("Failure");
            e.printStackTrace();
        }
        return generatedId; // Trả về `maHD` mới
    }
    
    public void insertQuanLyChiTiet(int maHD, List<Cart> list, List<Double> listGia) {
        String query = "INSERT QuanLyChiTiet(id, idChitietHoaDon)\r\n "+"VALUES (?, ?)";

        try {
            conn = new DBContext().getConnection(); // Mở kết nối SQL
            
            int count = 0;
            // Lặp qua danh sách sản phẩm trong giỏ hàng (list)
            for (Cart cart : list) {
                int chiTietHoaDonId = insertChiTietHoaDon(cart, listGia.get(count)); // Thêm vào ChiTietHoaDon và lấy ID mới
                // Cập nhật vào QuanLyChiTiet
                // Note: Chạy hàm khác bên trong hàm có prepare statement cũng sẽ ảnh hưởng prepare statement của hàm hiện tại (Hoặc do tên biến giống nhau)
                ps = conn.prepareStatement(query);
                ps.setInt(1, maHD);
                ps.setInt(2, chiTietHoaDonId);
                System.out.println(ps.executeUpdate());
                System.out.println("Yes, please");
                count++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public int insertChiTietHoaDon(Cart cart, double gia) {
        String query = "INSERT ChiTietHoaDon(productId, amount, totalPrice)\r\n"+"VALUES (?, ?, ?)";
        int generatedId = -1;

        try {
            conn = new DBContext().getConnection(); // Mở kết nối SQL
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, cart.getProductID());
            ps.setInt(2, cart.getAmount());
            ps.setDouble(3, gia);
            ps.executeUpdate();

            // Lấy `id` vừa được tạo
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
            System.out.println(generatedId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    
    public void insertCart(int accountID, int productID, int amount, String size) {
        String query = "insert Cart(accountID, productID, amount,size)\r\n"
        		+ "values(?,?,?,?)";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            ps.setInt(3, amount);
            ps.setString(4, size);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public void editProduct(String pname, String pimage, String pprice, String ptitle, String pdescription, String pcategory, 
    		String pmodel, String pcolor, 
    		String pdelivery, String pimage2, String pimage3, String pimage4, String pid) {
        String query = "update Product\r\n"
        		+ "set [name] = ?,\r\n"
        		+ "[image] = ?,\r\n"
        		+ "price = ?,\r\n"
        		+ "title = ?,\r\n"
        		+ "[description] = ?,\r\n"
        		+ "cateID = ?,\r\n"
        		+ "model= ?,\r\n"
        		+ "color= ?,\r\n"
        		+ "delivery=?,\r\n"
        		+ "image2=?,\r\n"
        		+ "image3=?,\r\n"
        		+ "image4=?\r\n"
        		+ "where [id] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, pname);
            ps.setString(2, pimage);
            ps.setString(3, pprice);
            ps.setString(4, ptitle);
            ps.setString(5, pdescription);
            ps.setString(6, pcategory);
            ps.setString(7, pmodel);
            ps.setString(8, pcolor);
            ps.setString(9, pdelivery);
            ps.setString(10, pimage2);
            ps.setString(11, pimage3);
            ps.setString(12, pimage4);
            ps.setString(13, pid);
            ps.executeUpdate();
           
        } catch (Exception e) {
        	 
        }
    }
    
    
    public void editProfile(String username, String password, String email, int uID) {
        String query = "update Account set [user]=?,\r\n"
        		+ "[pass]=?,\r\n"
        		+ "[email]=?\r\n"
        		+ "where [uID] = ?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setInt(4, uID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void editTongChiTieu(int accountID, double totalMoneyVAT) {
        String query = "exec dbo.proc_CapNhatTongChiTieu ?,?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, accountID);
            ps.setDouble(2, totalMoneyVAT);
          
            ps.executeUpdate();
            
        } catch (Exception e) {
        	
        }
    }
    
    public void editSoLuongDaBan(int productID, int soLuongBanThem) {
        String query = "exec dbo.proc_CapNhatSoLuongDaBan ?,?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, productID);
            ps.setInt(2, soLuongBanThem);
          
            ps.executeUpdate();
            
        } catch (Exception e) {
        	
        }
    }
    
    public void editTongBanHang(int sell_ID, double tongTienBanHangThem) {
        String query = "exec dbo.proc_CapNhatTongBanHang ?,?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, sell_ID);
            ps.setDouble(2, tongTienBanHangThem);
          
            ps.executeUpdate();
            
        } catch (Exception e) {
        	
        }
    }
    
    public void editAmountAndSizeCart(int accountID, int productID, int amount, String size) {
        String query = "update Cart set [amount]=?,\r\n"
        		+ "[size]=?\r\n"
        		+ "where [accountID]=? and [productID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            ps.setString(2, size);
            ps.setInt(3, accountID);
            ps.setInt(4, productID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    public void editAmountCart(int accountID, int productID, int amount) {
        String query = "update Cart set [amount]=?\r\n"
        		+ "where [accountID]=? and [productID]=?";
        try {
            conn = new DBContext().getConnection();//mo ket noi voi sql
            ps = conn.prepareStatement(query);
            ps.setInt(1, amount);
            ps.setInt(2, accountID);
            ps.setInt(3, productID);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
    public int savePublicKey(String publicKey) {
        String sql = "INSERT INTO Keys (pkey, date) VALUES (?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, publicKey);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // Trả về key_id
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; 
    }

    public String getPublicKeyByKeyId(int keyId) {
        String query = "SELECT pkey FROM Keys WHERE keyid = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, keyId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                	System.out.println(keyId);
                	System.out.println(rs.getString("pkey"));
                    return rs.getString("pkey");
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("get key ko thanh cong");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("get key ko thanh cong");
        return null;
    }
    public String getKeyCreateTime(int keyId) {
        String query = "SELECT FORMAT(date, 'yyyy-MM-dd HH:mm:ss') AS formattedDate FROM Keys WHERE keyid = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, keyId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(keyId);
                    System.out.println(rs.getString("formattedDate"));
                    return rs.getString("formattedDate"); // Return the formatted date
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Get key time failed.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Get key time failed.");
        return null;
    }
    public String getKeyExpiredTime(int keyId) {
        String query = "SELECT FORMAT(expired_date, 'yyyy-MM-dd HH:mm:ss') AS formattedDate FROM Keys WHERE keyid = ?";
        
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, keyId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println(keyId);
                    System.out.println(rs.getString("formattedDate"));
                    return rs.getString("formattedDate"); // Return the formatted date
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Get key time failed.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Get key time failed.");
        return null;
    }
    public static String savePrivateKey(PrivateKey privateKey) throws Exception {
        // Create a temporary directory
        Path tempDir = Files.createTempDirectory("user-private-key-");

        // Define the file name (e.g., private-key.key)
        String filename = "private-key.txt";

        // Full file path
        File file = new File(tempDir.toFile(), filename);
        
        // Save the private key to the file
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(privateKey);
        }

        // Return the absolute file path to be used for email attachment
        return file.getAbsolutePath();
    }
    public void updateKeyId(String email, int keyId) {
        String query = "UPDATE Account SET keyid = ? WHERE email = ?";
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, keyId); // Set password
            ps.setString(2, email); // Specify user for the WHERE clause
            ps.executeUpdate(); // Execute the update
        } catch (Exception e) {
            e.printStackTrace(); // Print the stack trace in case of error
        }
    }
    public void setKeyExpired(int keyId) {
        String query = "INSERT INTO Keys expired_date VALUES ?";
        
        try (PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String exportInvoicesToJSON() throws Exception {
        String query = "SELECT maHD, accountID, tongGia, ngayXuat FROM Invoice WHERE maHD=( SELECT max(maHD) FROM Invoice)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        String jsonFilePath = null;

        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            List<Map<String, Object>> invoices = new ArrayList<>();

            // Parse result set into a list of maps
            while (rs.next()) {
                Map<String, Object> invoice = new HashMap<>();
                invoice.put("maHD", rs.getInt("maHD"));
                invoice.put("accountID", rs.getInt("accountID"));
                invoice.put("tongGia", rs.getFloat("tongGia"));
                invoice.put("ngayXuat", rs.getTimestamp("ngayXuat")); // Ensure datetime is stored properly
                invoices.add(invoice);
            }

            // Convert the list to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            File tempFile = File.createTempFile("invoices", ".json");
            objectMapper.writeValue(tempFile, invoices);

            jsonFilePath = tempFile.getAbsolutePath(); // Get the file path

        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Propagate exception
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }

        return jsonFilePath; // Return the path of the generated JSON file
    }
    
	/*
	 * String query = "select top 1 * from Review\r\n" +
	 * "where accountID = ? and productID = ?\r\n" + "order by maReview desc"; try {
	 * conn = new DBContext().getConnection();//mo ket noi voi sql ps =
	 * conn.prepareStatement(query); ps.setInt(1, accountID); ps.setInt(2,
	 * productID); rs = ps.executeQuery(); while (rs.next()) { return new
	 * Review(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getDate(4)); } } catch
	 * (Exception e) {
	 */
    
public String getKey(int id) {
	String query ="select k.pubKey from Key\r\n"+
"join Account a on a.uID = k.uID\r\n" +
			"where a.uID=?";
	String publicKey=null;
	try {
		conn = new DBContext().getConnection();
		ps = conn.prepareStatement(query);
		ps.setInt(1, id);
		rs = ps.executeQuery(); 
			 while (rs.next()) {
				 publicKey=rs.getString(2);
			 }
		
	}catch (Exception e) {
    }
	return publicKey;
}
   public static void main(String[] args) {
        DAO dao = new DAO();
   }

}
