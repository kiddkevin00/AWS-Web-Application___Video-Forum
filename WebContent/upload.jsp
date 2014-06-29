<%@page import="com.amazonaws.services.s3.model.GetObjectRequest"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.File"%>
<%@page import="com.marcus.function.S3Controller"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.jspsmart.upload.*"%>
<%
	long maxSize = 1024 * 1024 * 1024;
	int uploadCount = 0;
	String videoName = "";

	SmartUpload smartUpload = new SmartUpload();
	smartUpload.initialize(pageContext);
	smartUpload.setMaxFileSize(maxSize);
	smartUpload.upload();

	videoName = smartUpload.getRequest().getParameter("video_title");
	if (!videoName.equals("")) {
		uploadCount = smartUpload.getFiles().getCount();

		System.out.println("Upload Count : " + uploadCount + "\n"
				+ "Video Name : " + videoName);

		String bucketName = "fromhomepage";
		String savedFilePath;

		savedFilePath = getClass().getResource("/").getPath()
				+ videoName;
		System.out.println("Saved file path : " + savedFilePath);

		com.jspsmart.upload.File uploadedFile = smartUpload.getFiles()
				.getFile(0);
		// save physically in (secondary)File system, which can be accessed by java.io.File 
		uploadedFile.saveAs(savedFilePath, smartUpload.SAVE_PHYSICAL);
		// Access the file by the provided path 
		java.io.File savedFile = new File(savedFilePath);
		savedFile.deleteOnExit();

		// create a sample text file
		File sampleFile = S3Controller.createSampleFile();
		System.out
				.println("Sample File Name : " + sampleFile.getName());

		S3Controller s3Controller = new S3Controller();
		s3Controller.uploadToS3(videoName, bucketName, savedFile);

		ArrayList<String> bucketList = new ArrayList<String>();
		bucketList = s3Controller.listBucketName();

		com.amazonaws.services.s3.model.S3Object object = s3Controller.s3Client
				.getObject(new GetObjectRequest(bucketName, videoName
						+ "_key"));
		S3Controller.displayOnConsole(object.getObjectContent());

		ArrayList<String> objectList = new ArrayList<String>();
		objectList = s3Controller.listObjectName(bucketName);
	}

	// delete a bucket including all obejcts inside
	//s3Controller.deleteAllObejctInBucket(bucketName);

	// delete all bucket in S3
	//s3Controller.deleteAllBucketInS3();

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