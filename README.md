# Paypal-ZhaoMeiqi

The project simply implements a purchasing scenario with Paypal's Express Checkout API.

The main files are:
index.jsp
complete.jsp
failed.jsp
Test.java

where,

index.jsp indicates the portal of the website. It allows users to choose the items they want to pay for. The main codes lie in the file Test.java, which serves as a Servlet in Java Web. It deals with the main payment logics and is responsible for forwarding to/from Paypal's EndPoint. complete.jsp and failed.jsp are the webpages to display the result. 

Here's the explanation of the code in Test.java:

First store the credentials and other configuration in file sdk_config.properties.

With the credentials we get the access token from Paypal.

According to the items the user selects in index.jsp, we generate a transaction including their prices. After the setting of the redirect URLs we create a payment and redirect to the payment link.

When the user returns from the EndPoint, the payment with PayerId is executed. The page is forwarded to complete.jsp and the payment process is done.