package com.marcus.function;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.SubscribeRequest;

public class SNSManager {

	public AmazonSNSClient snsClient;

	public SNSManager() {
		// create a new SNS client and set endpoint
		AWSCredentials credentials = new ProfileCredentialsProvider()
				.getCredentials();
		snsClient = new AmazonSNSClient(credentials);

		Region region = Region.getRegion(Regions.US_WEST_2);
		snsClient.setRegion(Region.getRegion(Regions.US_WEST_2));
	}

	// create a new SNS topic
	public void createATopic(String topic) {
		CreateTopicRequest createTopicRequest = new CreateTopicRequest(topic);
		CreateTopicResult createTopicResult = snsClient
				.createTopic(createTopicRequest);
		// print TopicArn
		System.out.println(createTopicResult);
		// get request id for CreateTopicRequest from SNS metadata
		System.out.println("CreateTopicRequest - "
				+ snsClient.getCachedResponseMetadata(createTopicRequest));
	}

	// subscribe to an SNS topic
	public void subscribeToATopic(String topic, String email) {
		String topicArn = "arn:aws:sns:us-west-2:003723184718:" + topic;
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, "email",
				email);
		snsClient.subscribe(subRequest);
		// get request id for SubscribeRequest from SNS metadata
		System.out.println("SubscribeRequest - "
				+ snsClient.getCachedResponseMetadata(subRequest));
		System.out.println("Check your email and confirm subscription.");
	}

	// publish to an SNS topic
	public void publishToATopic(String topic, String msg) {
		String topicArn = "arn:aws:sns:us-west-2:003723184718:" + topic;
		PublishRequest publishRequest = new PublishRequest(topicArn, msg);
		PublishResult publishResult = snsClient.publish(publishRequest);
		// print MessageId of message published to SNS topic
		System.out.println("MessageId - " + publishResult.getMessageId());
	}

	// delete an SNS topic
	public void deleteATopic(String topic) {
		String topicArn = "arn:aws:sns:us-west-2:003723184718:" + topic;
		DeleteTopicRequest deleteTopicRequest = new DeleteTopicRequest(topicArn);
		snsClient.deleteTopic(deleteTopicRequest);
		// get request id for DeleteTopicRequest from SNS metadata
		System.out.println("DeleteTopicRequest - "
				+ snsClient.getCachedResponseMetadata(deleteTopicRequest));
	}
}
