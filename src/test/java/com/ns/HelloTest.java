package com.ns;

import static org.junit.Assert.*;

import org.junit.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.requestreport.aws.lambda.RequestReportLambda;

public class HelloTest {

	@Test
	public void test() {
		RequestReportLambda lambda = new RequestReportLambda();

		System.out.println("Return Val = " + String.valueOf(10));
		System.out.println("Return Val = " + ("V=" + 10));
		//lambda.myHandler(10, context);
		//fail("Not yet implemented");
	}

}
