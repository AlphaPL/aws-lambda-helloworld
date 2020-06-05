package com.requestreport.aws.lambda;

import java.util.Map;

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
public class ParamsTrigger
{
    public String requestId;
}
