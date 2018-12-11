package ind.xuchuanyin.jnmon;

import static org.junit.Assert.*;

public class MainTest {

  @org.junit.Test
  public void procDiskStats() {
    String testFile = getClass().getClassLoader().getResource("proc_diskstats.sample").getPath();
    Processes.getInstance().procDiskStats(testFile, 2);
  }
}