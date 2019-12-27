package com.pmt.model;

import com.pmt.thrift.tstruct.TDBResult;
import kyotocabinet.Cursor;
import kyotocabinet.DB;

import java.nio.ByteBuffer;

/**
 * Created by minhtam on 25/12/2019
 */
public class KyotoDBModel {

    public static KyotoDBModel INS = new KyotoDBModel();

    private TDBResult PutException = new TDBResult(-1);
    private DB db;

    private KyotoDBModel(){
        _init();
    }

    private void _init() {
        db = new DB();
        if (!db.open("simple.kch", DB.OWRITER | DB.OCREATE)){
            System.err.println("open error: " + db.error());
        }
    }

    public TDBResult put(String key, ByteBuffer val) {
        try {
            db.set(key.getBytes(), val.array());
            TDBResult ret = new TDBResult(0);
            ret.setVal(val);
            return ret;
        }catch (Exception ex) {
            System.err.println("put exception error: " + ex.getMessage());
        }
        return PutException;
    }

    public ByteBuffer get(String key) {
        try {
            byte[] bytes = db.get(key.getBytes());
            return ByteBuffer.wrap(bytes);
        }catch (Exception ex) {
            System.err.println("get exception error: " + ex.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {

    }



}
