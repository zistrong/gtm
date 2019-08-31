package com.zistrong.test.thread.gtm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * 
 * <ul>
 * <li><b>purpose:</b>
 * <p>
 * work
 * </p>
 * </li>
 * <li><b>hisotry：</b>
 * 
 * @since 2019-8-31 13:50:34
 * @author zhangziqiang</li>
 *         </ul>
 */
public class Task implements Callable<TaskState> {

	List<Task> plist = new ArrayList<>();// parent

	List<Task> clist = new ArrayList<>();// sub

	TaskInterface taskInterface;

	ExecutorService service;

	Lock lock = new ReentrantLock();

	// taskstate，0-waiting，1-running，2-error，3-done
	private volatile TaskState state = TaskState.WAITING;

	private String taskId = "";

	public Task(String taskId, TaskInterface taskInterface) {
		super();
		this.taskId = taskId;
		this.taskInterface = taskInterface;
	}

	@Override
	public TaskState call() throws GtmException, InterruptedException, ExecutionException {
		if (this.service == null && !this.getPlist().isEmpty()) {
			this.service = this.getPlist().get(0).getService();
		}
			
		lock.lock();
		try {
			if (state == TaskState.WAITING) {// go run

				boolean flag = false;
				for (Task task : plist) {
					if (!task.getTaskId().equals("0") && task.getState() != TaskState.DONE) {
						flag = true;
					}
				}
				if (flag) {// runing of parent
					return null;
				} else {
					state = TaskState.RUNNING;
					if (run(taskId) == 0)
						state = TaskState.DONE;
					else
						state = TaskState.ERROR;

				}

				List<Future<TaskState>> list = new ArrayList<>();
				for (Task task : clist) {
					if (task.taskId.equals("")) {
						task.setTaskId(System.nanoTime() + "");
					}
					list.add(service.submit(task));
				}
				for (Future<TaskState> future : list) {
					TaskState taskState = future.get();
					if (taskState != null && taskState == TaskState.ERROR) {
						throw new GtmException("task fail");
					}
				}

			} else if (this.state == TaskState.ERROR) {// cant run
				throw new GtmException("error state");
			}
			return state;
		} finally {
			lock.unlock();
		}

	}

	public int run(String taskId) throws GtmException {

		try {
			System.out.println(this.taskId);
			taskInterface.execute();
		} catch (Exception e) {
			throw new GtmException(e);
		}
		return 0;
	}

	public int getTaskCount() {
		int count = 1;
		if (clist.isEmpty()) {
			return count;
		}
		for (Task task : clist) {
			count += task.getTaskCount();
		}
		return count;
	}

	public List<Task> getPlist() {
		return plist;
	}

	public List<Task> getClist() {
		return clist;
	}

	public ExecutorService getService() {
		return service;
	}

	public void setService(ExecutorService service) {
		this.service = service;
	}

	public TaskState getState() {
		return state;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	private void addParentTask(Task task) {
		if (!this.getPlist().contains(task)) {
			this.getPlist().add(task);
			if (task.getService() == null)
				task.setService(this.service);
		}
	}

	public void addChildTask(Task task) {
		if (!this.getClist().contains(task)) {
			this.getClist().add(task);
			task.addParentTask(this);
			if (task.getService() == null)
				task.setService(this.service);
		}
	}

}
