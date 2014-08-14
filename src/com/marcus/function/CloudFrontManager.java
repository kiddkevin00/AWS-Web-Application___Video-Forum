package com.marcus.function;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.Aliases;
import com.amazonaws.services.cloudfront.model.CacheBehaviors;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.PriceClass;
import com.amazonaws.services.cloudfront.model.S3OriginConfig;
import com.amazonaws.services.cloudfront.model.TrustedSigners;
import com.amazonaws.services.cloudfront.model.ViewerProtocolPolicy;

public class CloudFrontManager {

	public AmazonCloudFrontClient amazonCloudFrontClient;

	public CloudFrontManager() {
		AWSCredentials credentials = new ProfileCredentialsProvider()
				.getCredentials();
		amazonCloudFrontClient = new AmazonCloudFrontClient(credentials);

		Region region = Region.getRegion(Regions.US_WEST_2);
		amazonCloudFrontClient.setRegion(region);
	}

	public String getCloudFrontDomain(String bucketName) {
		DistributionConfig distributionConfig = new DistributionConfig();

		distributionConfig.setAliases(new Aliases().withQuantity(0));
		distributionConfig.setCallerReference(System.currentTimeMillis() + "");
		distributionConfig.setDefaultRootObject("");
		distributionConfig.withOrigins(new Origins().withItems(
				new Origin()
						.withId(bucketName)
						.withDomainName(bucketName + ".s3.amazonaws.com")
						.withS3OriginConfig(
								new S3OriginConfig()
										.withOriginAccessIdentity("")))
				.withQuantity(1));
		distributionConfig
				.withDefaultCacheBehavior(new DefaultCacheBehavior()
						.withTargetOriginId(bucketName)
						.withForwardedValues(
								new ForwardedValues().withQueryString(false)
										.withCookies(
												new CookiePreference()
														.withForward("none")))
						.withTrustedSigners(
								new TrustedSigners().withQuantity(0)
										.withEnabled(false))
						.withViewerProtocolPolicy(ViewerProtocolPolicy.AllowAll)
						.withMinTTL((long) 36000));
		distributionConfig.withCacheBehaviors(new CacheBehaviors()
				.withQuantity(0));
		distributionConfig.withLogging(new LoggingConfig().withEnabled(false)
				.withBucket("").withPrefix("").withIncludeCookies(false));
		distributionConfig.withComment("CloudFront Distribution for: "
				+ bucketName);
		distributionConfig.withEnabled(true);
		distributionConfig.withPriceClass(PriceClass.PriceClass_All);

		CreateDistributionRequest createDistributionRequest = new CreateDistributionRequest(
				distributionConfig);

		String cloudFrontDomainName = amazonCloudFrontClient
				.createDistribution(createDistributionRequest)
				.getDistribution().getDomainName();

		System.out.println(cloudFrontDomainName);
		return cloudFrontDomainName;
	}
}
