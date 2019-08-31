package com.zistrong.test.thread.gtm;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Work {

	private String id;

	private Task startNode;

	private ExecutorService service;

	public Work() {
		super();
		startNode = new Task("startNode", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
			}
		});

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void process() {

		try {

			System.out.println(this.id + " start time:" + new Date());
			service = Executors.newFixedThreadPool(this.getTaskCount());
			this.startNode.setService(service);
			Future<TaskState> mainTask = service.submit(this.startNode);
			TaskState taskState = mainTask.get();
			System.out.println("result: " + taskState);
			System.out.println(this.id + " end time:" + new Date());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (service != null && !service.isShutdown())
				service.shutdownNow();
		}
	}

	public Task getStartNode() {
		return startNode;
	}

	public void setStartNode(Task startNode) {
		this.startNode = startNode;
	}

	private int getTaskCount() {

		return this.startNode.getTaskCount();
	}

}
