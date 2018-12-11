package ind.xuchuanyin.jnmon;

import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessesTest {

  @Test
  public void procDiskStats() {
    String testFile = getClass().getClassLoader().getResource("proc_diskstats.sample").getPath();
    Processes.getInstance().procDiskStats(testFile, 2);
  }

  @Test
  public void procPartitions() {
    String testFile = getClass().getClassLoader().getResource("proc_partitions.sample").getPath();
    Processes.getInstance().procPartitions(testFile, 2);
  }
}