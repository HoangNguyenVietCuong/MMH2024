/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import dao.DAO;
import entity.Account;
import entity.Email;
import entity.EmailUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import entity.RSAModel;


@WebServlet(name = "SignUpControl", urlPatterns = {"/signup"})
public class SignUpControl extends HttpServlet {
	private RSAModel rsa = new RSAModel();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String user = request.getParameter("user");
        String pass = request.getParameter("pass");
        String re_pass = request.getParameter("repass");
        String email = request.getParameter("email");
        if(!pass.equals(re_pass)){
            response.sendRedirect("Login.jsp");
        }else{
            DAO dao = new DAO();
            Account a = dao.checkAccountExist(user);
            if(a == null){
            	//tao key
            	try {
					rsa.genkey();
					
	            	String publicKeyBase64 = Base64.getEncoder().encodeToString(rsa.getPubkey().getEncoded());
	            	int keyId = dao.savePublicKey(publicKeyBase64);
	            	
	            	if(keyId != -1) {
		                //dc signup
		                dao.singup(user, pass, email,keyId);
		                System.out.println("user signup success");
		                String pvkeyPath =  dao.savePrivateKey(rsa.getPvkey());
		                // email private key
		                // su dung email rieng hoac rac o day, tao app password cho vung setFromPassword
		                Email mail = new Email();
		                mail.setFrom("");
		                mail.setFromPassword("");
		                mail.setTo(email); // Recipient's email
		                mail.setSubject("Your Private Key");
		                mail.setAttachmentPath(pvkeyPath);
		                mail.setContent(" ");
		                // Send the email 
		                EmailUtils.send(mail);

		                System.out.println("Email sent successfully!");
		                
		                // Clean up 
		                new File(pvkeyPath).delete();
		                response.sendRedirect("login");
	            	}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("loi save key");
					response.sendRedirect("Login.jsp");
				}
            }else{
                //day ve trang login.jsp
                response.sendRedirect("Login.jsp");
            }
        }
        //sign up
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
