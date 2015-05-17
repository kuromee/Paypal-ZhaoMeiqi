package com.zhaomeiqi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;


/**
 * Servlet implementation class Test
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			InputStream is = Test.class
					.getResourceAsStream("/sdk_config.properties");
			OAuthTokenCredential tokenCredential = Payment.initConfig(is);
			String accessToken = tokenCredential.getAccessToken();
			APIContext apiContext = new APIContext(accessToken);
			if (request.getParameter("PayerID") == null) {
				boolean b1 = request.getParameter("item1") != null;
				boolean b2 = request.getParameter("item2") != null;
				boolean b3 = request.getParameter("item3") != null;
				int p1 = (b1 ? 1 : 0) * 1;
				int p2 = (b2 ? 1 : 0) * 2;
				int p3 = (b3 ? 1 : 0) * 3;
				int p = p1 + p2 + p3;

				Details details = new Details();
				details.setShipping("1");
				details.setSubtotal(String.valueOf(p));
				details.setTax("1");

				Amount amount = new Amount();
				amount.setCurrency("USD");
				amount.setTotal(String.valueOf(2 + p));
				amount.setDetails(details);

				Transaction transaction = new Transaction();
				transaction.setAmount(amount);
				transaction
						.setDescription("This is the payment transaction description.");

				ItemList itemList = new ItemList();
				List<Item> items = new ArrayList<Item>();
				if (b1) {
					Item item1 = new Item();
					item1.setName("item1").setQuantity("1").setCurrency("USD")
							.setPrice("1");
					items.add(item1);
				}
				if (b2) {
					Item item2 = new Item();
					item2.setName("item2").setQuantity("1").setCurrency("USD")
							.setPrice("2");
					items.add(item2);
				}
				if (b3) {
					Item item3 = new Item();
					item3.setName("item3").setQuantity("1").setCurrency("USD")
							.setPrice("3");
					items.add(item3);
				}
				itemList.setItems(items);
				transaction.setItemList(itemList);

				List<Transaction> transactions = new ArrayList<Transaction>();
				transactions.add(transaction);

				Payer payer = new Payer();
				payer.setPaymentMethod("paypal");

				Payment payment = new Payment();
				payment.setIntent("sale");
				payment.setPayer(payer);
				payment.setTransactions(transactions);

				RedirectUrls redirectUrls = new RedirectUrls();
				String guid = UUID.randomUUID().toString().replaceAll("-", "");
				redirectUrls.setCancelUrl(request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath()
						+ "/Test?guid=" + guid);
				redirectUrls.setReturnUrl(request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath()
						+ "/Test?guid=" + guid);
				payment.setRedirectUrls(redirectUrls);

				Payment createdPayment = payment.create(apiContext);
				System.out.println("Created payment with id = "
						+ createdPayment.getId() + " and status = "
						+ createdPayment.getState());

				Iterator<Links> links = createdPayment.getLinks().iterator();
				while (links.hasNext()) {
					Links link = links.next();
					if (link.getRel().equalsIgnoreCase("approval_url")) {
						request.setAttribute("redirectURL", link.getHref());
					}
				}

				response.sendRedirect(request.getAttribute("redirectURL")
						.toString());
			} else {
				Payment payment = new Payment();
				payment.setId(request.getParameter("paymentId"));
				System.out.println(request.getParameter("paymentId"));
				PaymentExecution paymentExecution = new PaymentExecution();
				paymentExecution.setPayerId(request.getParameter("PayerID"));
				System.out.println(request.getParameter("PayerID"));
				Payment createdPayment = payment.execute(apiContext, paymentExecution);
				response.sendRedirect("complete.jsp");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
