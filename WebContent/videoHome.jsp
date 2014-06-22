<%@page import="java.util.ArrayList"%>
<%@page import="com.marcus.function.S3Controller"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	String bucketName = "fromhomepage";
	S3Controller s3Controller = new S3Controller();
	ArrayList<String> objectList = new ArrayList<String>();
	ArrayList<String> urlList = new ArrayList<String>();

	objectList = s3Controller.listObjectName(bucketName);
	for (String object : objectList) {
		urlList.add("http://" + bucketName + ".s3.amazonaws.com/"
				+ object);
		System.out.println("http://" + bucketName
				+ ".s3.amazonaws.com/" + object);
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>
</head>
<body>
	<%
		for (int i = 0; i < urlList.size(); i++) {
	%>
	<video width="320" height="240" controls> <source
		src="<%=urlList.get(1)%>" type="video/mp4"> Your browser does
	not support the video tag. </video>
	<%
		}
	%>
</body>
</html>