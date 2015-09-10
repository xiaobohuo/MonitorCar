package com.example.monitorcar.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
	private ExecutorService service;
	private static ThreadPoolManager instance;

	public ThreadPoolManager() {
		int num = Runtime.getRuntime().availableProcessors();
		service = Executors.newFixedThreadPool(num * 2);
	}

	public static ThreadPoolManager getInstance() {
		if (instance == null) {
			instance = new ThreadPoolManager();
		}
		return instance;
	}

	public void addTask(Runnable r) {
		service.submit(r);
	}

}
