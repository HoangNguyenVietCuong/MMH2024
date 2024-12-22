package control;

import java.io.IOException;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import dao.DAO;

import entity.Account;
import entity.Email;
import entity.EmailUtils;
import entity.Invoice;
import entity.InvoiceItem;
import entity.Cart;
import entity.Product;
import entity.SoLuongDaBan;
import entity.TongChiTieuBanHang;

/**
 * Servlet implementation class ForgotPasswordControl
 */
@WebServlet(name = "OrderControl", urlPatterns = {"/order"})
public class OrderControl extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 HttpSession session = request.getSession();
		 	// Kiểm tra đăng nhập
	        Account a = (Account) session.getAttribute("acc");
	        if(a == null) {
	        	response.sendRedirect("login");
	        	return;
	        }
	        // Tính tổng tiền
	        int accountID = a.getId();
	        DAO dao = new DAO();
	        List<Cart> list = dao.getCartByAccountID(accountID);
	        List<Product> list2 = dao.getAllProduct();
	        List<Double> listMoney = new ArrayList<Double>();
	        double totalMoney=0;
	        for(Cart c : list) {
				for(Product p : list2) {
					if(c.getProductID()==p.getId()) {
						totalMoney=totalMoney+(p.getPrice()*c.getAmount());
						listMoney.add(p.getPrice()*c.getAmount());
					}
				}
			}
	        double totalMoneyVAT=totalMoney+totalMoney*0.1;
	        
	        double tongTienBanHangThem=0;
	        int sell_ID;
	        for(Cart c : list) {
				for(Product p : list2) {
					if(c.getProductID()==p.getId()) {
						tongTienBanHangThem=0;
						sell_ID=dao.getSellIDByProductID(p.getId());
						tongTienBanHangThem=tongTienBanHangThem+(p.getPrice()*c.getAmount());
						TongChiTieuBanHang t2 = dao.checkTongChiTieuBanHangExist(accountID);
						if(t2==null) {
							dao.insertTongChiTieuBanHang(accountID,0,tongTienBanHangThem);
						}
						else {
							dao.editTongBanHang(sell_ID, tongTienBanHangThem);
						}	
					}
				}
			}
	        
	        
	        for(Cart c : list) {
				for(Product p : list2) {
					if(c.getProductID()==p.getId()) {
						SoLuongDaBan s = dao.checkSoLuongDaBanExist(p.getId());
						if(s == null) {
							dao.insertSoLuongDaBan(p.getId(), c.getAmount());
						}
						else {
							dao.editSoLuongDaBan(p.getId(), c.getAmount());
						}	
					}
				}
			}
	        
	        int idInvoice = dao.insertInvoice(accountID, totalMoneyVAT, list, listMoney);
	        TongChiTieuBanHang t = dao.checkTongChiTieuBanHangExist(accountID);
	        if(t==null) {
	        	dao.insertTongChiTieuBanHang(accountID,totalMoneyVAT,0);
	        }
	        else {
	        	dao.editTongChiTieu(accountID, totalMoneyVAT);
	        }     
//	        // Chỗ tạo file JSON trong server
//	        List<InvoiceItem> listTest = dao.getAllItemOfInvoice(idInvoice);
//	        Date day = null;
//	        for(Invoice ico : dao.getAllInvoice()) {
//	        	if(ico.getMaHD() == idInvoice) {
//	        		day = ico.getNgayXuat();
//	        	}
//	        }
//	        
//	     // Tạo đối tượng JSON để lưu thông tin hóa đơn
//	        JSONObject invoiceJson = new JSONObject();
//	        JSONArray itemsJson = new JSONArray();
//
//	        for (InvoiceItem item : listTest) {
//	            JSONObject itemJson = new JSONObject();
//	            itemJson.put("id", item.getId());
//	            itemJson.put("productId", item.getProductId());
//	            itemJson.put("amount", item.getAmount());
//	            itemJson.put("totalPrice", item.getTotalPrice());
//
//	            itemsJson.put(itemJson);
//	        }
//
//	        invoiceJson.put("invoiceID", idInvoice);
//	        invoiceJson.put("totalMoney", totalMoneyVAT);
//	        invoiceJson.put("date", day);
//	        invoiceJson.put("items", itemsJson);
//
//	        // Lấy đường dẫn thư mục để lưu file JSON
//	        String path = getServletContext().getRealPath("/WEB-INF/test");
//	        System.out.println("Path to save file: " + path);
//
//	        // Tạo thư mục nếu chưa tồn tại
//	        Path dirPath = Paths.get(path);
//	        if (Files.notExists(dirPath)) {
//	            try {
//	                Files.createDirectories(dirPath); // Tạo thư mục nếu chưa tồn tại
//	            } catch (IOException e) {
//	                e.printStackTrace();
//	                return; // Thoát nếu không thể tạo thư mục
//	            }
//	        }
//
//	        // Đảm bảo file được lưu trong thư mục test
//	        Path filePath = dirPath.resolve("invoice_" + idInvoice + ".json");
//	        System.out.println("File path: " + filePath);
//
//	        // Kiểm tra xem file đã tồn tại hay chưa
//	        if (Files.exists(filePath)) {
//	            System.out.println("File already exists: " + filePath);
//	        } else {
//	            System.out.println("Creating new file: " + filePath);
//	        }
//
//	        // Ghi dữ liệu JSON vào file
//	        try {
//	            Files.write(filePath, invoiceJson.toString().getBytes()); // Ghi nội dung JSON vào file
//	            System.out.println("File created and data written successfully!");
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	        
//	        HttpSession sessionGet = request.getSession() ;
//	        sessionGet.setAttribute("pathToJSON", path);
		request.getRequestDispatcher("DatHang.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			String emailAddress = request.getParameter("email");
			String name = request.getParameter("name");
			String phoneNumber = request.getParameter("phoneNumber");
			String deliveryAddress = request.getParameter("deliveryAddress");
			
			 HttpSession session = request.getSession();
		        Account a = (Account) session.getAttribute("acc");
		        if(a == null) {
		        	response.sendRedirect("login");
		        	return;
		        }
		        int accountID = a.getId();
		        DAO dao = new DAO();
		        List<Cart> list = dao.getCartByAccountID(accountID);
		        List<Product> list2 = dao.getAllProduct();
					
		        double totalMoney=0;
		        for(Cart c : list) {
					for(Product p : list2) {
						if(c.getProductID()==p.getId()) {
							totalMoney=totalMoney+(p.getPrice()*c.getAmount());
						}
					}
				}
		        double totalMoneyVAT=totalMoney+totalMoney*0.1;
		        
		        
		        // Need code email here
		        
		        //old code
				Email email =new Email();
				email.setFrom("tienanhnono@gmail.com"); //chinh lai email quan tri tai day [chu y dung email con hoat dong]
				email.setFromPassword("gvkg pzjb suod bqib"); //mat khau email tren
				email.setTo(emailAddress);
				email.setSubject("Dat hang thanh cong tu Shoes Family");
				StringBuilder sb = new StringBuilder();
				sb.append("Dear ").append(name).append("<br>");
				sb.append("Ban vua dat dang tu Shoes Family. <br> ");
				sb.append("Dia chi nhan hang cua ban la: <b>").append(deliveryAddress).append(" </b> <br>");
				sb.append("So dien thoai khi nhan hang cua ban la: <b>").append(phoneNumber).append(" </b> <br>");
				sb.append("Cac san pham ban dat la: <br>");
				for(Cart c : list) {
					for(Product p : list2) {
						if(c.getProductID()==p.getId()) {
							sb.append(p.getName()).append(" | ").append("Price:").append(p.getPrice()).append("$").append(" | ").append("Amount:").append(c.getAmount()).append(" | ").append("Size:").append(c.getSize()).append("<br>");
						}
					}
				}
				sb.append("Tong Tien: ").append(String.format("%.02f",totalMoneyVAT)).append("$").append("<br>");
				sb.append("Cam on ban da dat hang tai Shoes Family<br>");
				sb.append("Chu cua hang");
				
				email.setContent(sb.toString());
				EmailUtils.send(email);
				request.setAttribute("mess", "Dat hang thanh cong!");
				
				dao.deleteCartByAccountID(accountID);
				
				
				//new code
//				request.setAttribute("email", emailAddress);
//				request.getRequestDispatcher("ThongTinDatHang.jsp").forward(request, response);
				
			
		} catch (Exception e) {
			request.setAttribute("error", "Dat hang that bai!");
			e.printStackTrace();
		}
	
		request.getRequestDispatcher("DatHang.jsp").forward(request, response);
	}

}
