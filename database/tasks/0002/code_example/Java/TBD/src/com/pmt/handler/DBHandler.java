package com.pmt.handler;

import com.pmt.model.KyotoDBModel;
import com.pmt.thrift.service.DataBaseService;
import com.pmt.thrift.tstruct.PingResult;
import com.pmt.thrift.tstruct.TDBResult;
import org.apache.thrift.TException;

import java.nio.ByteBuffer;

/**
 * Created by minhtam on 25/12/2019
 */
public class DBHandler implements DataBaseService.Iface {
    @Override
    public TDBResult put(String key, ByteBuffer val) throws TException {
        System.out.println("DBHandler.put");
        return KyotoDBModel.INS.put(key, val);
    }

    @Override
    public ByteBuffer get(String key) throws TException {
        System.out.println("DBHandler.get");
        return KyotoDBModel.INS.get(key);
    }

    @Override
    public PingResult ping() throws TException {
        System.out.println("ping DBHandler");
        return new PingResult(0);
    }
}
