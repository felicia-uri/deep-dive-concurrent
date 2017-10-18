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

  private AtomicLong sum;
  private CountDownLatch signal;

  public static void main(String[] args) {
    AtomicReduction race = new AtomicReduction();
    race.start();
    race.finish();
  }

  private void start() {
    System.out.printf(START_MESSAGE, NUM_THREADS);
    signal = new CountDownLatch(NUM_THREADS);
    sum = new AtomicLong(0);
    for (int i = 0; i < NUM_THREADS; i++) {
      new Thread (new Runnable()) {
        
        public void run() {
          for (int j = 0; j < UPPER_LIMIT; j++) {
            localSum += ThreadLocalRandom.current().nextInt(2);
        }
        }
        signal.countDown();
        System.out.printf(THREAD_COMPLETE_MESSAGE, Thread.currentThread().getId())
      }).start();
    }
  }

  private void finish() {
    try {
      signal.await();
    } catch (InterruptedException ex) {
      // Do nothing
    } finally {
      System.out.printf(FINISH_MESSAGE, sum.longValue());
    }
  }

}
