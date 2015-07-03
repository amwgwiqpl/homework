package com.shine.nettyzk;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.junit.Test;

import com.shine.nettyzk.zkopt.CuratorZKClient;

public class SharedCounterTest {

	@Test
	public void testCounter() {
		try {
			CuratorFramework zkCurator = CuratorZKClient.getInstance().getZkCurator();

			SharedCount sharedCount = new SharedCount(zkCurator, "/counter", 1);
			
			sharedCount.addListener(new SharedCountListener() {

				@Override
				public void stateChanged(CuratorFramework client, ConnectionState newState) {
					// TODO Auto-generated method stub
					System.out.println("State changed: " + newState.toString());
				}

				@Override
				public void countHasChanged(SharedCountReader sharedCount, int newCount) throws Exception {
					// TODO Auto-generated method stub
					System.out.println("Counter's value is changed to " + newCount);
				}
			});
			
			sharedCount.start();
			sharedCount.trySetCount(sharedCount.getVersionedValue(), 2);
			System.out.println(sharedCount.getCount());
			
			
			ExecutorService service = Executors.newFixedThreadPool(5);
            for (int i = 0; i < 5; ++i) {
                final SharedCount count = new SharedCount(zkCurator, "/counter", 0);
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
						count.start();
						System.out.println("Increment:"
								+ count.trySetCount(count.getVersionedValue(), count.getCount() + 1));
						return null;
                    }
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
			sharedCount.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
