mvn install
aws lambda update-function-code --function-name GetReport --zip-file fileb://target/getRequestLambda-1.0-SNAPSHOT.jar