package ind.xuchuanyin.jnmon;

public class ProcEntity {
  public static class DiskStat {
    String dk_name;
    int dk_major;
    int dk_minor;
    long dk_noinfo;
    long dk_reads;
    long dk_rmerge;
    long dk_rmsec;
    long dk_rkb;
    long dk_writes;
    long dk_wmerge;
    long dk_wmsec;
    long dk_wkb;
    long dk_xfers;
    long dk_bsize;
    long dk_time;
    long dk_inflight;
    long dk_backlog;
    long dk_partition;
    long dk_blocks;
    long dk_use;
    long dk_aveq;

    public DiskStat() {
    }

    @Override public String toString() {
      final StringBuffer sb = new StringBuffer("DiskStat{");
      sb.append("dk_name='").append(dk_name).append('\'');
      sb.append(", dk_major=").append(dk_major);
      sb.append(", dk_minor=").append(dk_minor);
      sb.append(", dk_noinfo=").append(dk_noinfo);
      sb.append(", dk_reads=").append(dk_reads);
      sb.append(", dk_rmerge=").append(dk_rmerge);
      sb.append(", dk_rmsec=").append(dk_rmsec);
      sb.append(", dk_rkb=").append(dk_rkb);
      sb.append(", dk_writes=").append(dk_writes);
      sb.append(", dk_wmerge=").append(dk_wmerge);
      sb.append(", dk_wmsec=").append(dk_wmsec);
      sb.append(", dk_wkb=").append(dk_wkb);
      sb.append(", dk_xfers=").append(dk_xfers);
      sb.append(", dk_bsize=").append(dk_bsize);
      sb.append(", dk_time=").append(dk_time);
      sb.append(", dk_inflight=").append(dk_inflight);
      sb.append(", dk_backlog=").append(dk_backlog);
      sb.append(", dk_partition=").append(dk_partition);
      sb.append(", dk_blocks=").append(dk_blocks);
      sb.append(", dk_use=").append(dk_use);
      sb.append(", dk_aveq=").append(dk_aveq);
      sb.append('}');
      return sb.toString();
    }
  }
}
