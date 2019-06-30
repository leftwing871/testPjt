package com.prd.utils;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Uploader {
	public static boolean upload(String regionName, String keyId, String accessKey, String bucket, String srcPath, String distPath, ObjectMetadata md) throws Exception
	{
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(keyId, accessKey);
		AmazonS3 s3Client = new AmazonS3Client(awsCreds);
		s3Client.setRegion(Region.getRegion(Regions.fromName(regionName)));
		
		try
		{
			File file = new File(srcPath);
			if(!file.exists())
				throw new Exception(srcPath + "file not exists");
	        
			s3Client.putObject(new PutObjectRequest(bucket, distPath, file)
						.withCannedAcl(CannedAccessControlList.PublicRead)
						.withMetadata(md));
			
			return true;
		}
		catch(AmazonClientException e)
		{
			throw e;
		}
	}
}
