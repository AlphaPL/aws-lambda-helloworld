package com.requestreport.aws.lambda;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.InputStream;
import java.net.URL;
import com.amazonaws.services.s3.*;
import java.util.UUID;
import java.io.*;
import java.io.InputStreamReader;
/**
 * Note:
 * Example: http://docs.aws.amazon.com/lambda/latest/dg/get-started-step4-optional.html doesn't work (gives - deserialization error with Integer
 *  com.fasterxml.jackson.databind.JsonMappingException: Can not deserialize instance of java.lang.Integer out of START_OBJECT token
 *
 * Solution: http://stackoverflow.com/questions/35545642/error-executing-hello-world-for-aws-lambda-in-java
 * http://docs.aws.amazon.com/AWSToolkitEclipse/latest/ug/lambda-tutorial.html
 *
 * @author namit
 *
 */
public class UploadFileLambda
{

    String queueName = "testDownloadFile";
    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public String myHandler(Map<String,Object> input, Context context)
    {
        try {
            InputStream inputFile = new URL("https://github.com/AlphaPL/aws-lambda-helloworld/blob/master/1?raw=true").openStream();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFile))) {
                String line = null;
                while((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return "File Uploaded";
    }

    private void createQueue() {
        CreateQueueRequest create_request = new CreateQueueRequest(queueName)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
    }


}
