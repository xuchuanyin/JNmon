package ind.xuchuanyin.jnmon;

import java.io.File;

public class Main {

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

    private static final String[] MONTH = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

    private Proc[] proc = new Proc[P_NUMBER];

    private void initProcs() {
        proc[P_CPUINFO].fileName = "/proc/cpuinfo";
        proc[P_STAT].fileName = "/proc/stat";
        proc[P_VERSION].fileName = "/proc/version";
        proc[P_MEMINFO].fileName = "/proc/meminfo";
        proc[P_UPTIME].fileName = "/proc/uptime";
        proc[P_LOADAVG].fileName = "/proc/loadavg";
        proc[P_NFS].fileName = "/proc/net/rpc/nfs";
        proc[P_NFSD].fileName = "/proc/net/rpc/nfsd";
        proc[P_VMSTAT].fileName = "/proc/vmstat";
    }

    private void procDiskStats(double elapsed) {
        String fileName = "/proc/diskstats";
        File file = new File(fileName);



    }


    private void findRelease() {
        // /etc/os-release
    }



    public static void main(String[] args) {
        Main main = new Main();
        main.initProcs();

    }
}
