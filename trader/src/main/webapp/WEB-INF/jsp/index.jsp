<!DOCTYPE html>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>
<html xmlns="http://www.w3.org/1999/xhtml" >

<head>
	<meta name="viewport" content="width=device-width,initial-scale=1"/>
	<title>My Trader</title>

    	<jsp:include page="fragments/header.jsp" />
    
</head>
<body>
     
     <jsp:include page="fragments/navbar.jsp"></jsp:include>
     <div><h3>Top Three Stock Quotes Information  </h3></div>
     <div class="col-md-6" >
			<jsp:include page="fragments/quotesummary.jsp"></jsp:include>
	</div>
    
</body>
</html>