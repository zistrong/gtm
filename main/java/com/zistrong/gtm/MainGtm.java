package com.zistrong.gtm;

import java.util.Date;
import java.util.concurrent.locks.LockSupport;

public class MainGtm {

	public static void main(String[] args) {

		Task task1 = new Task("task1", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute1 start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 4000);
				System.out.println("execute1 end time.............." + new Date());
			}
		});
		Task task2 = new Task("task2", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute2, start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 10000);
				System.out.println("execute2, end time.............." + new Date());
			}
		});

		Task task3 = new Task("task3", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute3 start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 5000);
				System.out.println("execute3 end time.............." + new Date());
			}
		});
		Task task4 = new Task("task4", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute4 start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 2000);
				System.out.println("execute4 end time.............." + new Date());
			}
		});
		Task task5 = new Task("task5", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute5 start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 2000);
				System.out.println("execute5 end time.............." + new Date());
			}
		});
		Task task6 = new Task("task6", new TaskInterface() {

			@Override
			public void execute() throws GtmException {
				// TODO Auto-generated method stub
				System.out.println("execute6 start time.............." + new Date());
				LockSupport.parkUntil(System.currentTimeMillis() + 2000);
				System.out.println("execute6 end time.............." + new Date());
			}
		});
		task2.addChildTask(task3);
		task3.addChildTask(task4);
		task3.addChildTask(task5);
		task1.addChildTask(task6);
		task1.addChildTask(task3);
		//task6.addChildTask(task4);

		Work work = new Work();
		work.setId("work1");
		work.getStartNode().addChildTask(task1);
		work.getStartNode().addChildTask(task2);
		work.process();

	}
}
