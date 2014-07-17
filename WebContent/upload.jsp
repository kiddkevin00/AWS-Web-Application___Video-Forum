<%@ page import="com.marcus.function.CloudFrontManager"%>
<%@ page import="com.marcus.function.SNSManager"%>
<%@ page import="com.marcus.function.RDSManager"%>
<%@ page import="com.amazonaws.services.dynamodbv2.model.AttributeValue"%>
<%@ page import="com.marcus.function.DynamoDBManager"%>
<%@ page import="com.amazonaws.services.s3.model.GetObjectRequest"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.File"%>
<%@ page import="com.marcus.function.S3Controller"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.jspsmart.upload.*"%>
<%
	/*
	 * Some commented statements with double stars are some additional 
	 * useful tools to clean up S3 and DynamoDB contents
	 */
	long maxSize = 1024 * 1024 * 1024;
	int uploadCount = 0;
	String videoName = "";
	String bucketName = "fromhomepage";

	boolean debugMode = false;

	if (!(debugMode)) {
		SmartUpload smartUpload = new SmartUpload();
		smartUpload.initialize(pageContext);
		smartUpload.setMaxFileSize(maxSize);
		smartUpload.upload();

		videoName = smartUpload.getRequest()
				.getParameter("video_title");

		S3Controller s3Controller = new S3Controller();
		DynamoDBManager dynamoDBManager = new DynamoDBManager();

		if (!videoName.equals("")) {
			uploadCount = smartUpload.getFiles().getCount();

			System.out.println("Upload Count : " + uploadCount + "\n"
					+ "Video Name : " + videoName);

			String savedFilePath;

			savedFilePath = getClass().getResource("/").getPath()
					+ videoName;
			System.out.println("Saved file path : " + savedFilePath);

			com.jspsmart.upload.File uploadedFile = smartUpload
					.getFiles().getFile(0);
			// save physically in (secondary)File system, which can be accessed by java.io.File 
			uploadedFile.saveAs(savedFilePath,
					smartUpload.SAVE_PHYSICAL);
			// Access the file by the provided path 
			java.io.File savedFile = new File(savedFilePath);
			savedFile.deleteOnExit();

			// storing video to S3:
			s3Controller.uploadToS3(videoName, bucketName, savedFile);

			ArrayList<String> bucketList = new ArrayList<String>();
			bucketList = s3Controller.listBucketName();

			com.amazonaws.services.s3.model.S3Object object = s3Controller.s3Client
					.getObject(new GetObjectRequest(bucketName,
							videoName));
			S3Controller.displayOnConsole(object.getObjectContent());

			// Add a distribution and Get the CloudFront domain:
			CloudFrontManager cloudFrontManager = new CloudFrontManager();
			String domainName;
			domainName = cloudFrontManager
					.getCloudFrontDomain(bucketName);

			// Storing info to DynamoDB:
			String tableName = "videoInfo";

			dynamoDBManager.createTable(tableName);

			/* 
			 * Due to implementing CloudFront feature, need to change storing 
			 * item from bucketName to domainName..
			 */
			dynamoDBManager.saveAItemToDynamoDB(tableName,
					"bucketName", bucketName, "videoKey", videoName);

		}
		// **delete a bucket including all obejcts inside
		//s3Controller.deleteAllObejctInBucket(bucketName);

		// **delete all bucket in S3
		//s3Controller.deleteAllBucketInS3();
		// **delete all table in DynamoDB
		//dynamoDBManager.deleteAllTable();

	}

	// Implementing SNS:
	SNSManager snsManager = new SNSManager();
	String topic = "videoForum";
	String subscriberEmail = "kiddkevin01@gmail.com";
	String message = "Welcome to Awesome Video Forum!!";

	//snsManager.deleteATopic("MyNewTopic");
	// only needed for first time creating topic
	//snsManager.createATopic(topic);
	// only needed for first time suscription
	//snsManager.subscribeToATopic(topic, subscriberEmail);

	snsManager.publishToATopic(topic, message);

	response.sendRedirect("videoHome.jsp?status=complete");
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Insert title here</title>
</head>
<body>

</body>
</html>