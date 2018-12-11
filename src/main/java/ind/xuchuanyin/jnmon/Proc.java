package ind.xuchuanyin.jnmon;

import java.io.File;

public class Proc {
  public File fp;
  public String filename;
  public int size;
  public int lines;
  public String line;
  public String buf;
  /* track updates for each update to stop  double data collection */
  public int read_this_interval;
}
