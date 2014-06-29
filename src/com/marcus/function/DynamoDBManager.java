package com.marcus.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

public class DynamoDBManager {

	public AmazonDynamoDBClient amazonDynamoDBClient;

	public DynamoDBManager() {
		amazonDynamoDBClient = new AmazonDynamoDBClient(
				new ClasspathPropertiesFileCredentialsProvider());
		Region region = Region.getRegion(Regions.US_WEST_2);
		amazonDynamoDBClient.setRegion(region);
	}

	public void saveToDynamoDB(String tableName,
			Map<String, AttributeValue> item) {
		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
		PutItemResult putItemResult = amazonDynamoDBClient
				.putItem(putItemRequest);
		System.out.println("Put Item Result: " + putItemResult);
	}

	public Map<String, AttributeValue> generateAMap(String item1, String item2,
			String key1, String key2) {
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
		map.put(key1, new AttributeValue(item1));
		map.put(key2, new AttributeValue(item2));
		return map;

	}

	// TODO..
	public void deleteATable() {

	}

	// TODO..
	public ArrayList<String> listingAllTables() {
		ArrayList<String> tableList = new ArrayList<String>();
		return tableList;

	}
}
