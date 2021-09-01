# alpakka-s3-localstack

Demonstrating a bug/inconsistency in alpakka S3 whereby custom Attributes are honored for methods `makeBucket`
and `checkIfBucketExists` but not for `multipartUpload` or `download`.

## About

There are three projects.

* `common` Containing code to create a bucket, upload a file to it, and download a file from it.
* `good` Uses `common` with settings from `application.conf`.
* `bad` Uses `common` with custom Attributes.

## Using

1. Start the localstack container.

```bash
docker-compose up --build --detach
```

2. Run the good project.

```bash
sbt "good / run"
```

3. See that it echos this file.

4. Tear down the localstack container.

```bash
docker container rm -f alpakka-s3-localstack_localstack_1
```

5. Run the bad project.

```bash
sbt "bad / run"
```

6. See that it fails because it's trying to connect to AWS.

7. Verify that the bucket was created in localstack.

```bash
aws --endpoint-url="http://localhost:4566" s3 ls
```
