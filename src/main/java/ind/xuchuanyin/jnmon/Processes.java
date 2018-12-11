package ind.xuchuanyin.jnmon;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Processes {
  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
  private static Processes processesInstance;

  public static final String PROC_DISK_STATS = "/proc/diskstats";
  public static final String PROC_PARTITIONS = "/proc/partitions";



  private static final int P_CPUINFO = 0;
  private static final int P_STAT = 1;
  private static final int P_VERSION = 2;
  private static final int P_MEMINFO = 3;
  private static final int P_UPTIME = 4;
  private static final int P_LOADAVG = 5;
  private static final int P_NFS = 6;
  private static final int P_NFSD = 7;
  private static final int P_VMSTAT = 8;
  private static final int P_NUMBER = 9;

  private Proc[] proc = new Proc[P_NUMBER];



  private static final int DISK_MODE_IO = 1;
  private static final int DISK_MODE_DISKSTATS = 2;
  private static final int DISK_MODE_PARTITIONS = 3;
  private int disk_mode = 0;
  private int partitions_short;

  /* Flag if we have run function proc_cpu() already in this interval */
  private int proc_cpu_done = 0;
  int reread = 0;

  private Processes() {
  }

  public static Processes getInstance() {
    if (processesInstance == null) {
      processesInstance = new Processes();
    }
    return processesInstance;
  }

  private void proc_init() {
    proc[P_CPUINFO].filename = "/proc/cpuinfo";
    proc[P_STAT].filename = "/proc/stat";
    proc[P_VERSION].filename = "/proc/version";
    proc[P_MEMINFO].filename = "/proc/meminfo";
    proc[P_UPTIME].filename = "/proc/uptime";
    proc[P_LOADAVG].filename = "/proc/loadavg";
    proc[P_NFS].filename = "/proc/net/rpc/nfs";
    proc[P_NFSD].filename = "/proc/net/rpc/nfsd";
    proc[P_VMSTAT].filename = "/proc/vmstat";
  }


  /**
   * The /proc/diskstats file displays the I/O statistics
   * of block devices. Each line contains the following 14
   * fields:
   * 1 - major number
   * 2 - minor mumber
   * 3 - device name
   * 4 - reads completed successfully
   * 5 - reads merged
   * 6 - sectors read
   * 7 - time spent reading (ms)
   * 8 - writes completed
   * 9 - writes merged
   * 10 - sectors written
   * 11 - time spent writing (ms)
   * 12 - I/Os currently in progress
   * 13 - time spent doing I/Os (ms)
   * 14 - weighted time spent doing I/Os (ms)
   *
   * @param fileName
   * @param elapsed
   */
  public void procDiskStats(String fileName, double elapsed) {
    InputStream inputStream = null;
    InputStreamReader streamReader = null;
    BufferedReader bufferedReader = null;
    try {
      inputStream = new FileInputStream(fileName);
      streamReader = new InputStreamReader(inputStream);
      bufferedReader = new BufferedReader(streamReader);

      List<ProcEntity.DiskStat> diskStats = new ArrayList<>();
      int i = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        ProcEntity.DiskStat diskStat = new ProcEntity.DiskStat();
        String[] elements = line.trim().split("[ \t]+");
        if (elements.length == 7) {
          // this logic is from lmon16g.c
          diskStat.dk_major = Integer.parseInt(elements[0]);
          diskStat.dk_minor = Integer.parseInt(elements[1]);
          diskStat.dk_name = elements[2];
          diskStat.dk_reads = Long.parseLong(elements[3]);
          diskStat.dk_rmerge = Long.parseLong(elements[4]);
          diskStat.dk_rkb = Long.parseLong(elements[5]);
          diskStat.dk_rmsec = Long.parseLong(elements[6]);

          diskStat.dk_partition = 1;
          diskStat.dk_wkb = diskStat.dk_rmsec;
          diskStat.dk_writes = diskStat.dk_rkb;
          diskStat.dk_rkb = diskStat.dk_rmerge;
          diskStat.dk_rmsec = 0;
          diskStat.dk_rmerge = 0;
        } else if (elements.length == 14) {
          diskStat.dk_major = Integer.parseInt(elements[0]);
          diskStat.dk_minor = Integer.parseInt(elements[1]);
          diskStat.dk_name = elements[2];
          diskStat.dk_reads = Long.parseLong(elements[3]);
          diskStat.dk_rmerge = Long.parseLong(elements[4]);
          diskStat.dk_rkb = Long.parseLong(elements[5]);
          diskStat.dk_rmsec = Long.parseLong(elements[6]);
          diskStat.dk_writes = Long.parseLong(elements[7]);
          diskStat.dk_wmerge = Long.parseLong(elements[8]);
          diskStat.dk_wkb = Long.parseLong(elements[9]);
          diskStat.dk_wmsec = Long.parseLong(elements[10]);
          diskStat.dk_inflight = Long.parseLong(elements[11]);
          diskStat.dk_time = Long.parseLong(elements[12]);
          diskStat.dk_backlog = Long.parseLong(elements[13]);

          diskStat.dk_partition = 0;

        } else {
          LOGGER.log(Level.SEVERE,
              String.format("disk stats wanted 14 but returned=%d line=%s",
                  elements.length, line));
        }

        diskStat.dk_rkb /= 2;
        diskStat.dk_wkb /= 2;
        diskStat.dk_xfers = diskStat.dk_reads + diskStat.dk_writes;
        if (diskStat.dk_xfers == 0) {
          diskStat.dk_bsize = 0;
        } else {
          diskStat.dk_bsize = ((diskStat.dk_rkb + diskStat.dk_wkb) / diskStat.dk_xfers) * 1024;
        }
        diskStat.dk_time /= 10.0;
        if (diskStat.dk_xfers > 0) {
          i++;
        }

        diskStats.add(diskStat);
      }

      for (int j = 0; j < diskStats.size(); j++) {
        LOGGER.log(Level.SEVERE, PROC_DISK_STATS + ": No." + j + " -> " + diskStats.get(j).toString());
      }

    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Failed to read disk stat", e);
    } finally {
      closeCloseable(bufferedReader, streamReader, inputStream);
    }
  }

  /**
   * /proc/partitions
   * major minor  #blocks  name     rio rmerge rsect ruse wio wmerge wsect wuse running use aveq
   *
   *   33     0    1052352 hde 2855 15 2890 4760 0 0 0 0 -4 7902400 11345292
   *
   * @param fileName
   * @param elapsed
   */
  public void procPartitions(String fileName, double elapsed) {
    InputStream inputStream = null;
    InputStreamReader streamReader = null;
    BufferedReader bufferedReader = null;
    try {
      inputStream = new FileInputStream(fileName);
      streamReader = new InputStreamReader(inputStream);
      bufferedReader = new BufferedReader(streamReader);

      // skip the header line
      if (bufferedReader.readLine() == null) {
        return;
      }
      List<ProcEntity.DiskStat> diskStats = new ArrayList<>();
      int i = 0;
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (line.trim().isEmpty()) {
          continue;
        }
        ProcEntity.DiskStat diskStat = new ProcEntity.DiskStat();
        String[] elements = line.trim().split("[ \t]+");
        diskStat.dk_major = Integer.parseInt(elements[0]);
        diskStat.dk_minor = Integer.parseInt(elements[1]);
        diskStat.dk_blocks = Integer.parseInt(elements[2]);
        diskStat.dk_name = elements[3];
        diskStat.dk_reads = elements.length > 4 ? Long.parseLong(elements[4]) : 0;
        diskStat.dk_rmerge = elements.length > 5 ? Long.parseLong(elements[5]) : 0;
        diskStat.dk_rkb = elements.length > 6 ? Long.parseLong(elements[6]) : 0;
        diskStat.dk_rmsec = elements.length > 7 ? Long.parseLong(elements[7]) : 0;
        diskStat.dk_writes = elements.length > 8 ? Long.parseLong(elements[8]) : 0;
        diskStat.dk_wmerge = elements.length > 9 ? Long.parseLong(elements[9]) : 0;
        diskStat.dk_wkb = elements.length > 10 ? Long.parseLong(elements[10]) : 0;
        diskStat.dk_wmsec = elements.length > 11 ? Long.parseLong(elements[11]) : 0;
        diskStat.dk_inflight = elements.length > 12 ? Long.parseLong(elements[12]) : 0;
        diskStat.dk_use= elements.length > 13 ? Long.parseLong(elements[13]) : 0;
        diskStat.dk_aveq = elements.length > 14 ? Long.parseLong(elements[14]) : 0;

        diskStat.dk_rkb /= 2;
        diskStat.dk_wkb /= 2;
        diskStat.dk_xfers = diskStat.dk_reads + diskStat.dk_writes;
        if (diskStat.dk_xfers == 0) {
          diskStat.dk_bsize = 0;
        } else {
          diskStat.dk_bsize = ((diskStat.dk_rkb + diskStat.dk_wkb) / diskStat.dk_xfers) * 1024;
        }
        diskStat.dk_time /= 10.0;

        if (elements.length != 15) {
          this.partitions_short = 1;
        } else {
          this.partitions_short = 0;
        }
        diskStats.add(diskStat);
      }

      for (int j = 0; j < diskStats.size(); j++) {
        LOGGER.log(Level.SEVERE, PROC_PARTITIONS + ": No." + j + " -> " + diskStats.get(j).toString());
      }

    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Failed to read disk stat", e);
    } finally {
      closeCloseable(bufferedReader, streamReader, inputStream);
    }
  }

  public void procDiskIo(String fileName, double elapsed) {
    // todo:
  }

  private void closeCloseable(Closeable... closeables) {
    for (Closeable closeable : closeables) {
      if (closeable != null) {
        try {
          closeable.close();
        } catch (IOException e) {
          LOGGER.log(Level.INFO, "Swallowing error", e);
        }
      }
    }
  }

}
