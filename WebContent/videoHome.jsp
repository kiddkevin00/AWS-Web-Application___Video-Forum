<%@ page import="com.marcus.function.DynamoDBManager"%>
<%@ page import="java.util.*"%>
<%@ page import="com.marcus.function.S3Controller"%>
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
	// Implement CloudFront which need to change domain name to the following:
	/* urlList.add("http://" + item.get("bucketName") + "/"
		+ item.get("videoKey")); */
	System.out.println("http://" + item.get("bucketName")
	+ ".s3.amazonaws.com/" + item.get("videoKey"));
		}

	}
%>

<script>
//print java code in console for debugging
console.log("here: " + "<%=urlList.size()%>");
</script>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title>Video Collection</title>

<link href="./css/bootstrap.css" rel="stylesheet" />
<style>
/* Move down content because we have a fixed navbar that is 50px tall */
body {
	padding-top: 50px;
	padding-bottom: 20px;
}

.row {
	margin-top: 5px;
	margin-bottom: 20px;
}

.jumbotron {
	padding: 10px;
	margin-bottom: 10px;
}
</style>
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Amazon Web Services DEMO</a>
			</div>
			<div>
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a style="color: white; font-size: 15px" class="navbar-brand"
					href="./index.html">upload another video</a>
			</div>
			<div class="navbar-collapse collapse">
				<form class="navbar-form navbar-right" role="form">
					<div class="form-group">
						<input type="text" placeholder="Email" class="form-control">
					</div>
					<div class="form-group">
						<input type="password" placeholder="Password" class="form-control">
					</div>
					<button type="submit" class="btn btn-success">Sign in</button>
				</form>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>

	<!-- Main jumbotron for a primary marketing message or call to action -->
	<div class="jumbotron">
		<div class="container">
			<h1>Hello, world!</h1>
			<p>This is a template for a simple marketing or informational
				website. It includes a large callout called a jumbotron and three
				supporting pieces of content. Use it as a starting point to create
				something more unique.</p>
			<p>
				<a class="btn btn-primary btn-lg" role="button">Learn more
					&raquo;</a>
			</p>
		</div>
	</div>

	<div class="container">
		<!-- Example row of columns -->

		<div class="row">

			<%
				if (urlList.size() <= 3) {
					for (int i = 0; i < urlList.size(); i++) {
			%>
			<div class="col-md-4">
				<video width="320" height="240" controls> <source
					src="<%=urlList.get(i)%>" type="video/mp4"> Your browser
				does not support the video tag. </video>
			</div>
			<%
				}
				} else if (urlList.size() <= 6) {
			%>

		</div>


		<div class="row">

			<%
				for (int i = 0; i < 3; i++) {
			%>
			<div class="col-md-4">
				<video width="320" height="240" controls> <source
					src="<%=urlList.get(i)%>" type="video/mp4"> Your browser
				does not support the video tag. </video>

			</div>

			<%
				}
			%>

		</div>


		<div class="row">

			<%
				for (int i = 3; i < urlList.size(); i++) {
			%>
			<div class="col-md-4">
				<video width="320" height="240" controls> <source
					src="<%=urlList.get(i)%>" type="video/mp4"> Your browser
				does not support the video tag. </video>
			</div>
			<%
				}
				}
			%>

		</div>
		<!-- <div class="col-md-4">
				<h2>Heading</h2>
				<p>Donec id elit non mi porta gravida at eget metus. Fusce
					dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh,
					ut fermentum massa justo sit amet risus. Etiam porta sem malesuada
					magna mollis euismod. Donec sed odio dui.</p>
				<p>
					<a class="btn btn-default" href="#" role="button">View details
						&raquo;</a>
				</p>
			</div>
			<div class="col-md-4">
				<h2>Heading</h2>
				<p>Donec sed odio dui. Cras justo odio, dapibus ac facilisis in,
					egestas eget quam. Vestibulum id ligula porta felis euismod semper.
					Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum
					nibh, ut fermentum massa justo sit amet risus.</p>
				<p>
					<a class="btn btn-default" href="#" role="button">View details
						&raquo;</a>
				</p>
			</div> -->
	</div>
	
	<form style="display:none;" method="post" action="">
		<%
			String javaParameter = "some important info generated from this page only";
			// pass Java parameters from this page to other HTML pages 
			out.println("<input type='hidden' id='index' name='index' value='"
					+ javaParameter + "'>");
		%>
	</form>

	<hr>

	<footer>
	<p>&copy; Company 2014</p>
	</footer>
	</div>
	<!-- /container -->


	<!-- Bootstrap core JavaScript
    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="./js/bootstrap.min.js"></script>
</body>
</html>