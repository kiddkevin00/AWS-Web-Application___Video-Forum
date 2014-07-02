package com.marcus.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class DynamoDBManager {

	public AmazonDynamoDBClient amazonDynamoDBClient;

	public DynamoDBManager() {
		AWSCredentials credentials = new ProfileCredentialsProvider()
				.getCredentials();
		// amazonDynamoDBClient = new AmazonDynamoDBClient(
		// new ClasspathPropertiesFileCredentialsProvider());
		amazonDynamoDBClient = new AmazonDynamoDBClient(credentials);

		Region region = Region.getRegion(Regions.US_WEST_2);
		amazonDynamoDBClient.setRegion(region);
	}

	public void createTable(String tableName) {
		if (Tables.doesTableExist(amazonDynamoDBClient, tableName)) {
			System.out.println("***Table already exist!!!");
		} else {
			CreateTableRequest request = new CreateTableRequest()
					.withTableName(tableName)
					.withKeySchema(
							new KeySchemaElement()
									.withAttributeName("videoKey").withKeyType(
											KeyType.HASH))
					.withAttributeDefinitions(
							new AttributeDefinition().withAttributeName(
									"videoKey").withAttributeType(
									ScalarAttributeType.S))
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(
									1L).withWriteCapacityUnits(1L));

			CreateTableResult result = amazonDynamoDBClient
					.createTable(request);
			System.out.println("Created Table : "
					+ result.getTableDescription());
			// (necessary) Wait for it to become active
			System.out.println("Waiting for " + tableName
					+ " to become ACTIVE...");
			Tables.waitForTableToBecomeActive(amazonDynamoDBClient, tableName);

		}

	}

	public void saveAItemToDynamoDB(String tableName, String key1,
			String item1, String key2, String item2) {
		if (!(item1.equals("") || item2.equals(""))) {
			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			item = this.generateAMap(key1, item1, key2, item2);
			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			PutItemResult putItemResult = amazonDynamoDBClient
					.putItem(putItemRequest);
			System.out.println("Put Item Result: " + putItemResult);
		}
	}

	public Map<String, AttributeValue> generateAMap(String key1, String item1,
			String key2, String item2) {
		Map<String, AttributeValue> map = new HashMap<String, AttributeValue>();
		map.put(key1, new AttributeValue(item1));
		map.put(key2, new AttributeValue(item2));
		return map;

	}

	public void deleteAllTable() {
		List<String> tables = new ArrayList<String>();
		tables = this.listingAllTables();
		for (String table : tables) {
			DeleteTableRequest request = new DeleteTableRequest()
					.withTableName(table);
			amazonDynamoDBClient.deleteTable(request);
		}

	}

	public List<String> listingAllTables() {
		List<String> tableList = new ArrayList<String>();

		String lastEvaluatedtableName = null;
		do {
			ListTablesRequest request = new ListTablesRequest().withLimit(10)
					.withExclusiveStartTableName(lastEvaluatedtableName);
			ListTablesResult result = amazonDynamoDBClient.listTables(request);
			lastEvaluatedtableName = result.getLastEvaluatedTableName();
			tableList = result.getTableNames();
		} while (lastEvaluatedtableName != null);

		return tableList;

	}

	public List<Map<String, String>> listAllItemInATable(String tableName) {
		ScanRequest request = new ScanRequest().withTableName(tableName);
		ScanResult result = amazonDynamoDBClient.scan(request);
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		for (Map<String, AttributeValue> item : result.getItems()) {
			items.add(printItem(item));
		}
		return items;

	}

	private static Map<String, String> printItem(
			Map<String, AttributeValue> attributeList) {
		Map<String, String> itemMap = new HashMap<String, String>();
		for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
			String attributeName = item.getKey();
			AttributeValue value = item.getValue();
			System.out.println(attributeName
					+ " "
					+ (value.getS() == null ? "" : "S=[" + value.getS() + "]")
					+ (value.getN() == null ? "" : "N=[" + value.getN() + "]")
					+ (value.getB() == null ? "" : "B=[" + value.getB() + "]")
					+ (value.getSS() == null ? "" : "SS=[" + value.getSS()
							+ "]")
					+ (value.getNS() == null ? "" : "NS=[" + value.getNS()
							+ "]")
					+ (value.getBS() == null ? "" : "BS=[" + value.getBS()
							+ "] \n"));
			String valueString = (value.getS() == null ? "" : value.getS())
					+ (value.getN() == null ? "" : value.getN())
					+ (value.getB() == null ? "" : value.getB())
					+ (value.getSS() == null ? "" : value.getSS())
					+ (value.getNS() == null ? "" : value.getNS())
					+ (value.getBS() == null ? "" : value.getBS() + "\n");
			itemMap.put(attributeName, valueString);
		}
		return itemMap;

	}
}
