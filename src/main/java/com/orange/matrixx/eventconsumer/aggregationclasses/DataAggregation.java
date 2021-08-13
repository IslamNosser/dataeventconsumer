package com.orange.matrixx.eventconsumer.aggregationclasses;

import org.apache.flink.api.common.functions.ReduceFunction;

import com.orange.matrixx.eventconsumer.objects.DataEvent;

public class DataAggregation implements ReduceFunction<DataEvent> {

	@Override
	public DataEvent reduce(DataEvent value1, DataEvent value2) throws Exception {
		System.out.println("this is Test");
		return value2;
	}

}
