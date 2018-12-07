package ind.xuchuanyin.jnmon;

import java.io.File;

class Proc {
    File filehandle;
    String fileName;
    // track updates for each update to stop double data collection
    int readThisInterval;

    public Proc() {
    }
}