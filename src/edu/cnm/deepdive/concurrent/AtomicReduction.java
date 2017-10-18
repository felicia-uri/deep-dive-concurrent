package edu.cnm.deepdive.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicReduction {


  private static final int UPPER_LIMIT = 20_000_000;
  private static final int NUM_THREADS = 10;
  private static final String START_MESSAGE = "Starting %d threads.%n";
  private static final String THREAD_COMPLETE_MESSAGE = "Thread %d complete.%n";
  private static final String FINISH_MESSAGE = "Sum = %, d.%n";
  private static final String TIMING_MESSAGE = "Total time = %.3f seconds.%n";

  private AtomicLong sum;
  private AtomicLong updates;
  private CountDownLatch signal;

  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    AtomicReduction race = new AtomicReduction();
    race.start();
    race.finish();
    long finishTime = System.currentTimeMillis();
    System.out.printf(TIMING_MESSAGE, (finishTime - startTime) / 1000.0);
  }
  
  private void start() {
    System.out.printf(START_MESSAGE, NUM_THREADS);
    sum = new AtomicLong(0);
    updates = new AtomicLong(0);
    signal = new CountDownLatch(NUM_THREADS);
    for (int i = 0; i < NUM_THREADS; i++) {
      new Thread(() -> {
        long localSum = 0;
        long localUpdates = 0;
        for (int j = 0; j < UPPER_LIMIT; j++) {
          localSum += ThreadLocalRandom.current().nextInt(2);
          localUpdates++;
        }
        sum.addAndGet(localSum);
        updates.addAndGet(localUpdates);
        System.out.printf(THREAD_COMPLETE_MESSAGE, Thread.currentThread().getId());
        signal.countDown();
      }).start();
    }
  }

  private void finish() {
    try {
      signal.await();
    } catch (InterruptedException ex) {
      // Do nothing
    } finally {
      System.out.printf(FINISH_MESSAGE, updates.longValue(), sum.longValue());
    }
  }
  
}