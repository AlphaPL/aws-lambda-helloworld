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
public class RequestReportLambda
{

    String queueName = "testRequestReport";
    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public String myHandler(Map<String,Object> input, Context context)
    {
        LambdaLogger logger = context.getLogger();
        logger.log("received : " + input);
        createQueue();
        sendRequestIds(getRequestIds());
        return "Hello Welt";
    }


    final String exampleResp =
        "<RequestReportResponse xmlns=\"http://xml.com/doc/20019-01-01/\">" +
        "    <RequestReportResult>" +
        "        <ReportRequestInfo>" +
        "            <ReportRequestId>1234567890</ReportRequestId>" +
        "            <ReportType>_GET_STATISTICS_DATA_</ReportType>" +
        "            <StartDate>20020-01-21T12:10:39+00:00</StartDate>" +
        "            <EndDate>2020-02-13T12:10:39+00:00</EndDate>" +
        "            <SubmittedDate>2009-02-20T12:10:39+00:00</SubmittedDate>" +
        "            <ReportProcessingStatus>_SUBMITTED_</ReportProcessingStatus>" +
        "        </ReportRequestInfo>" +
        "    </RequestReportResult>" +
        "</RequestReportResponse>";
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

    private List<String> getRequestIds() {
         List<String> allMatches = new ArrayList<String>();
         Matcher m = Pattern.compile("<ReportRequestId>(.*)</ReportRequestId>")
             .matcher(exampleResp);
         while (m.find()) {
           allMatches.add(m.group(1));
         }
        System.out.println(allMatches);
        return allMatches;
    }

    private void sendRequestIds(List<String> requestIds) {
        for (String requestId: requestIds) {
            SendMessageRequest send_msg_request = new SendMessageRequest()
                    .withQueueUrl("https://sqs.us-east-1.amazonaws.com/751836677119/" + queueName)
                    .withMessageBody(requestId)
                    .withDelaySeconds(5);
            sqs.sendMessage(send_msg_request);
        }
    }
}
