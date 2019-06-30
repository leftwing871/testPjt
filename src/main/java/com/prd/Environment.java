package com.prd;

public class Environment {
	private String jdbcUrl;
	private String dbUserID;
	private String dbUserPW;
	private int dbLoginTimoutSecond = 3;
	private String elasticsearchUrl;

	private String s3UploadBucket;
	private String s3UploadPath;
	private String aws_region;
	private String aws_access_key_id;
	private String aws_secret_access_key;
	private String aws_rds_access_key_id;
	private String aws_rds_secret_access_key;
	private String redisEndpoint;
	private String aws_sqs_url;
	
	private String tabjoyKey;
	
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}
	public String getDbUserID() {
		return dbUserID;
	}
	public void setDbUserID(String dbUserID) {
		this.dbUserID = dbUserID;
	}
	public String getDbUserPW() {
		return dbUserPW;
	}
	public void setDbUserPW(String dbUserPW) {
		this.dbUserPW = dbUserPW;
	}
	public String getS3UploadBucket() {
		return s3UploadBucket;
	}
	public void setS3UploadBucket(String s3UploadBucket) {
		this.s3UploadBucket = s3UploadBucket;
	}
	public String getS3UploadPath() {
		return s3UploadPath;
	}
	public void setS3UploadPath(String s3UploadPath) {
		this.s3UploadPath = s3UploadPath;
	}
	public String getAws_region() {
		return aws_region;
	}
	public void setAws_region(String aws_region) {
		this.aws_region = aws_region;
	}
	public String getAws_access_key_id() {
		return aws_access_key_id;
	}
	public void setAws_access_key_id(String aws_access_key_id) {
		this.aws_access_key_id = aws_access_key_id;
	}
	public String getAws_secret_access_key() {
		return aws_secret_access_key;
	}
	public void setAws_secret_access_key(String aws_secret_access_key) {
		this.aws_secret_access_key = aws_secret_access_key;
	}
	public String getAws_rds_access_key_id() {
		return aws_rds_access_key_id;
	}
	public void setAws_rds_access_key_id(String aws_rds_access_key_id) {
		this.aws_rds_access_key_id = aws_rds_access_key_id;
	}
	public String getAws_rds_secret_access_key() {
		return aws_rds_secret_access_key;
	}
	public void setAws_rds_secret_access_key(String aws_rds_secret_access_key) {
		this.aws_rds_secret_access_key = aws_rds_secret_access_key;
	}
	public String getRedisEndpoint() {
		return redisEndpoint;
	}
	public void setRedisEndpoint(String redisEndpoint) {
		this.redisEndpoint = redisEndpoint;
	}
	
	public int getDbLoginTimoutSecond() {
		return dbLoginTimoutSecond;
	}

	public String getElasticsearchUrl() {
		return elasticsearchUrl;
	}
	public void setElasticsearchUrl(String elasticsearchUrl) {
		this.elasticsearchUrl = elasticsearchUrl;
	}
	
//	public void setDbLoginTimoutSecond(int dbLoginTimoutSecond) {
//		this.dbLoginTimoutSecond = dbLoginTimoutSecond;
//	}
	

	public String getTabjoyKey() {
		return tabjoyKey;
	}
	public void setTabjoyKey(String tabjoyKey) {
		this.tabjoyKey = tabjoyKey;
	}
	
	public String getAws_sqs_url() {
		return aws_sqs_url;
	}
	public void setAws_sqs_url(String aws_sqs_url) {
		this.aws_sqs_url = aws_sqs_url;
	}
	
	@Override
	public String toString() {
		return "Environment [jdbcUrl=" + jdbcUrl + ", dbUserID=" + dbUserID + ", dbUserPW=" + dbUserPW
				+ ", dbLoginTimoutSecond=" + dbLoginTimoutSecond + ", s3UploadBucket=" + s3UploadBucket
				+ ", s3UploadPath=" + s3UploadPath + ", aws_region=" + aws_region + ", aws_access_key_id="
				+ aws_access_key_id + ", aws_secret_access_key=" + aws_secret_access_key + ", aws_rds_access_key_id="
				+ aws_rds_access_key_id + ", aws_rds_secret_access_key=" + aws_rds_secret_access_key
				+ ", redisEndpoint=" + redisEndpoint + "]";
	}
}
