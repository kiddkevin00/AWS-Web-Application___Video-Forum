package com.marcus.function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Random;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Controller {

	public AmazonS3Client s3Client;

	public S3Controller() throws AmazonClientException, AmazonServiceException {
		try {

			AWSCredentials credentials = new ProfileCredentialsProvider()
					.getCredentials();
			// amazonDynamoDBClient = new AmazonDynamoDBClient(
			// new ClasspathPropertiesFileCredentialsProvider());
			s3Client = new AmazonS3Client(credentials);

			// s3Client = new AmazonS3Client(
			// new ClasspathPropertiesFileCredentialsProvider());
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			s3Client.setRegion(usWest2);
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. "
							+ "Please make sure that your credentials file is at the correct "
							+ "location (~/.aws/credentials), and is in valid format.",
					e);
		}
	}

	public void uploadToS3(String videoName, String bucketName,
			File uploadedFile) throws AmazonClientException,
			AmazonServiceException {
		try {
			if (!(s3Client.doesBucketExist(bucketName))) {
				s3Client.createBucket(bucketName);
			}
			Random random = new Random();
			// can't use some random number t avoid key naming conflict, will
			// "cause The specified key does not exist"
			String s3key = videoName;
			System.out.println(s3key);

			AccessControlList acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.FullControl);
			s3Client.putObject(new PutObjectRequest(bucketName, s3key,
					uploadedFile).withAccessControlList(acl));

		} catch (AmazonServiceException ase) {
			System.out
					.println("Caught an AmazonServiceException, which means your request made it "
							+ "to Amazon S3, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out
					.println("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public ArrayList<String> listBucketName() {
		ArrayList<String> bucketNameList = new ArrayList<String>();

		for (Bucket bucket : s3Client.listBuckets()) {
			String bucketName = bucket.getName();
			System.out.println("Bucket Name : " + bucketName);
			bucketNameList.add(bucketName);
		}
		return bucketNameList;
	}

	public ArrayList<String> listObjectName(String bucketName) {
		ObjectListing objectListing = s3Client
				.listObjects(new ListObjectsRequest()
						.withBucketName(bucketName).withPrefix(""));
		ArrayList<String> objectList = new ArrayList<String>();
		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			System.out.println("Object in " + bucketName + " : "
					+ objectSummary.getKey() + "  " + objectSummary.getSize());
			objectList.add(objectSummary.getKey());
		}
		return objectList;

	}

	public void deleteAllObejctInBucket(String bucketName) {
		S3Controller s3Controller = new S3Controller();
		ArrayList<String> objectsList = new ArrayList<String>();
		objectsList = s3Controller.listObjectName(bucketName);
		for (String object : objectsList) {
			s3Controller.s3Client.deleteObject(bucketName, object);
			System.out.println("deleted object*1");
		}
		s3Controller.s3Client.deleteBucket(bucketName);
	}

	public void deleteAllBucketInS3() {
		S3Controller s3Controller = new S3Controller();
		ArrayList<String> bucketList = new ArrayList<String>();
		bucketList = s3Controller.listBucketName();
		for (String bucket : bucketList) {
			System.out.println("**" + bucket.substring(0, 7));
			if (!(bucket.substring(0, 7).equals("elastic"))) {
				System.out.println("here!!");
				s3Controller.deleteAllObejctInBucket(bucket);
				System.out.println("delete bucket*1");
			}
		}
	}

	public static File createSampleFile() throws IOException {
		File file = File.createTempFile("_temp", ".txt");
		file.deleteOnExit();

		Writer writer = new OutputStreamWriter(new FileOutputStream(file));
		writer.write("this is the first line!!\n");
		writer.write("this is the second line!!\n");
		writer.write("this is the third line!!\n");
		writer.write("this is the forth line!!\n");
		writer.close();

		return file;
	}

	public static void displayOnConsole(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		while (true) {
			String readLine = bufferedReader.readLine();
			if (readLine == null) {
				break;
			}
			System.out.println(readLine);
		}
		System.out.println();

	}
}
