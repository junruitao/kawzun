/*
 * Copyright 2010-2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.kwz.aws;

import com.kwz.enums.EntityType;

/**
 * This is a general purpose object used for storing data on S3.
 * The mimeType and data variables need only be set if you are
 * planning to initiate a storage call.  If you do not specify a
 * mimeType "text/html" is the default.
 */
public class KwzStorageObject {

	private String bucketName="kwzdata1";
	// ziped serialised data
	private byte [] data;
	private EntityType type;
	private String mimeType="application/zip";

    public KwzStorageObject(EntityType type) {
        this.type = type;
    }

    public KwzStorageObject() {
    }

    public void setBucketName(String bucketName) {
		/**
		 * S3 prefers that the bucket name be lower case.  While you can
		 * create buckets with different cases, it will error out when
		 * being passed through the AWS SDK due to stricter checking.
		 */
		this.bucketName = bucketName.toLowerCase();
	}

	public String getBucketName() {
		return bucketName;
	}

	public byte[] getData() {
		return data;
	}

    public void setData(byte[] data) {
        this.data=data;
    }


	public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
