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
import com.google.gson.Gson;

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
public class GetReportListLambda
{

    String queueName = "testUploadFile";
    AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    public String myHandler(Map<String,Object> input, Context context)
    {
        try {
            LambdaLogger logger = context.getLogger();
            logger.log("received : " + input);
            createQueue();
            sendRequestIds(getRequestIds());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return "GetReportListLambda succeeded";
    }


    final String exampleResp =
        "<?xml version=\"1.0\"?>" + "\n" +
        "<GetReportListResponse xmlns=\"http://xml.com/doc/20019-01-01/\">" + "\n" +
        "  <GetReportRequestListResult>" + "\n" +
        "    <HasNext>false</HasNext>" + "\n" +
        "    <ReportRequestInfo>" + "\n" +
        "      <ReportType>_GET_STATISTICS_DATA_</ReportType>" + "\n" +
        "      <ReportProcessingStatus>_DONE_</ReportProcessingStatus>" + "\n" +
        "      <EndDate>2020-05-14T18:28:45+00:00</EndDate>" + "\n" +
        "      <ReportRequestId>52135018390</ReportRequestId>" + "\n" +
        "      <StartedProcessingDate>2020-05-08T00:31:56+00:00</StartedProcessingDate>" + "\n" +
        "      <SubmittedDate>2020-05-08T00:31:36+00:00</SubmittedDate>" + "\n" +
        "      <StartDate>2020-05-07T18:28:45+00:00</StartDate>" + "\n" +
        "      <CompletedDate>2020-05-08T00:32:09+00:00</CompletedDate>" + "\n" +
        "      <GeneratedReportId>20555922748018390</GeneratedReportId>" + "\n" +
        "    </ReportRequestInfo>" + "\n" +
        "    <ReportRequestInfo>" + "\n" +
        "      <ReportType>_GET_STATISTICS_DATA_</ReportType>" + "\n" +
        "      <ReportProcessingStatus>_DONE_</ReportProcessingStatus>" + "\n" +
        "      <EndDate>2020-05-14T16:33:58+00:00</EndDate>" + "\n" +
        "      <ReportRequestId>52131018389</ReportRequestId>" + "\n" +
        "      <StartedProcessingDate>2020-05-07T22:30:52+00:00</StartedProcessingDate>" + "\n" +
        "      <SubmittedDate>2020-05-07T22:30:46+00:00</SubmittedDate>" + "\n" +
        "      <StartDate>2020-05-07T16:33:58+00:00</StartDate>" + "\n" +
        "      <CompletedDate>2020-05-07T22:30:59+00:00</CompletedDate>" + "\n" +
        "      <GeneratedReportId>20555478631018389</GeneratedReportId>" + "\n" +
        "    </ReportRequestInfo>" + "\n" +
        "    <ReportRequestInfo>" + "\n" +
        "      <ReportType>_GET_SUMMARY_STATISTICS_DATA_</ReportType>" + "\n" +
        "      <ReportProcessingStatus>_DONE_</ReportProcessingStatus>" + "\n" +
        "      <EndDate>2020-05-13T16:30:44+00:00</EndDate>" + "\n" +
        "      <ReportRequestId>52130018389</ReportRequestId>" + "\n" +
        "      <StartedProcessingDate>2020-05-07T22:29:50+00:00</StartedProcessingDate>" + "\n" +
        "      <SubmittedDate>2020-05-07T22:29:46+00:00</SubmittedDate>" + "\n" +
        "      <StartDate>2020-05-06T16:30:44+00:00</StartDate>" + "\n" +
        "      <CompletedDate>2020-05-07T22:29:56+00:00</CompletedDate>" + "\n" +
        "    </ReportRequestInfo>" + "\n" +
        "    <ReportRequestInfo>" + "\n" +
        "      <ReportType>_GET_FORECAST_STATISTICS_DATA_</ReportType>" + "\n" +
        "      <ReportProcessingStatus>_DONE_NO_DATA_</ReportProcessingStatus>" + "\n" +
        "      <EndDate>2020-05-13T14:32:59+00:00</EndDate>" + "\n" +
        "      <ReportRequestId>52127018389</ReportRequestId>" + "\n" +
        "      <StartedProcessingDate>2020-05-07T20:35:06+00:00</StartedProcessingDate>" + "\n" +
        "      <SubmittedDate>2020-05-07T20:35:01+00:00</SubmittedDate>" + "\n" +
        "      <StartDate>2020-05-06T14:32:59+00:00</StartDate>" + "\n" +
        "      <CompletedDate>2020-05-07T20:35:15+00:00</CompletedDate>" + "\n" +
        "    </ReportRequestInfo>" + "\n" +
        "</GetReportListResponse";

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
         Matcher m = Pattern.compile("<ReportRequestInfo>(.*?)</ReportRequestInfo>", Pattern.DOTALL)
             .matcher(exampleResp);
         while (m.find()) {
            String item = m.group(1);
            System.out.println(item);
            Matcher status = Pattern.compile("<ReportProcessingStatus>(.*?)</ReportProcessingStatus>", Pattern.DOTALL)
                .matcher(item);
            if(status.find()) {
                System.out.println(status);
                if(status.group(1).equals("_DONE_")) {
                    Matcher generatedReportId =
                        Pattern.compile("<GeneratedReportId>(.*?)</GeneratedReportId>", Pattern.DOTALL)
                            .matcher(item);
                    if(generatedReportId.find()) {
                        Gson gson = new Gson();
                        DownloadRequest req = new DownloadRequest();
                        req.generatedId = generatedReportId.group(1);
                        allMatches.add(gson.toJson(req));
                    }
                }
            }
         }
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
