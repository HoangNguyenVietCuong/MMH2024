package control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
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
import service.SignatureService;

/**
 * Servlet implementation class ForgotPasswordControl
 */
@WebServlet(name = "OrderControl", urlPatterns = {"/order"})
public class OrderControl extends HttpServlet {
	//private SignatureService sv = new SignatureService();
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
	        dao.insertInvoice(accountID, totalMoneyVAT, list, listMoney);
	        TongChiTieuBanHang t = dao.checkTongChiTieuBanHangExist(accountID);
	        if(t==null) {
	        	dao.insertTongChiTieuBanHang(accountID,totalMoneyVAT,0);
	        }
	        else {
	        	dao.editTongChiTieu(accountID, totalMoneyVAT);
	        }     

	        try {
	            String jsonFilePath = dao.exportInvoicesToJSON();
	            String encodedPath = URLEncoder.encode(jsonFilePath, StandardCharsets.UTF_8.toString());
	            session.setAttribute("jsonFilePath", encodedPath); 
	        } catch (Exception e) {
	            e.printStackTrace();
	            request.setAttribute("error", "Failed to export invoices to JSON.");
	        }
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
			String signature = request.getParameter("signature");
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
		        String key = dao.getKey(accountID);
		       PublicKey b = SignatureService.getPublicKeyFromBase64(key);
		        
		       
		        
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
				email.setAttachmentPath(null);
				EmailUtils.send(email);
				request.setAttribute("mess", "Dat hang thanh cong!");
				dao.deleteCartByAccountID(accountID);
				
				

			
		} catch (Exception e) {
			request.setAttribute("error", "Dat hang that bai!");
			e.printStackTrace();
		}
	
		request.getRequestDispatcher("DatHang.jsp").forward(request, response);
	}

}
