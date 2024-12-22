package entity;

public class InvoiceItem {
	int id;
	int productId;
	int amount;
	double totalPrice;
	
	public InvoiceItem(int id, int productId, int amount, double totalPrice) {
		super();
		this.id = id;
		this.productId = productId;
		this.amount = amount;
		this.totalPrice = totalPrice;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}
