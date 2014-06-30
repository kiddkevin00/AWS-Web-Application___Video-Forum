<%@page import="com.marcus.function.DynamoDBManager"%>
<%@page import="java.util.*"%>
<%@page import="com.marcus.function.S3Controller"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%
	String bucketName = "fromhomepage";
	String tableName = "videoInfo";

	ArrayList<String> objectList = new ArrayList<String>();
	ArrayList<String> urlList = new ArrayList<String>();

	DynamoDBManager dynamoDBManager = new DynamoDBManager();
	List<Map<String, String>> items = new ArrayList<Map<String, String>>();
	items = dynamoDBManager.listAllItemInATable(tableName);

	/* Alternative way to list all videos by using S3 only
	S3Controller s3Controller = new S3Controller();
	objectList = s3Controller.listObjectName(bucketName);
	if (objectList.size() != 0) {
		for (String object : objectList) {
			urlList.add("http://" + bucketName + ".s3.amazonaws.com/"
					+ object);
			System.out.println("http://" + bucketName
					+ ".s3.amazonaws.com/" + object);
		}
	} */

	// usign DynamoDB to list all videos
	if (items.size() != 0) {
		for (Map<String, String> item : items) {
			urlList.add("http://" + item.get("bucketName")
					+ ".s3.amazonaws.com/" + item.get("videoKey"));
			System.out.println("http://" + item.get("bucketName")
					+ ".s3.amazonaws.com/" + item.get("videoKey"));
		}

	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Video Collection</title>
</head>
<body>
	<%
		for (int i = 0; i < urlList.size(); i++) {
	%>
	<video width="320" height="240" controls> <source
		src="<%=urlList.get(i)%>" type="video/mp4"> Your browser does
	not support the video tag. </video>
	<%
		}
	%>
</body>
</html>