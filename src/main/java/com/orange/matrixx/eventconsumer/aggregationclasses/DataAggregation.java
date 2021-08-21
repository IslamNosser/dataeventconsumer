package com.orange.matrixx.eventconsumer.aggregationclasses;

import org.apache.flink.api.common.functions.ReduceFunction;

import com.orange.matrixx.eventconsumer.objects.DataEvent;
import com.orange.matrixx.eventconsumer.objects.OEGEvent;

public class DataAggregation implements ReduceFunction<OEGEvent> {

	@Override
	public OEGEvent reduce(OEGEvent value1, OEGEvent value2) throws Exception {
		System.out.println("this is Test");
		return value2;
	}

}
