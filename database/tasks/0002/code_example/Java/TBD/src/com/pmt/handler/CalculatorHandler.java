package com.pmt.handler;

import com.pmt.common.TValue;
import com.pmt.thrift.shared.CalculatorResult;
import com.pmt.thrift.shared.Work;
import com.pmt.thrift.tutorial.Calculator;
import org.apache.thrift.*;

import java.util.HashMap;
import kyotocabinet.*;
import org.apache.thrift.protocol.TProtocol;

/**
 * Created by minhtam on 18/12/2019
 */
public class CalculatorHandler implements Calculator.Iface {

    @Override
    public CalculatorResult add(int num1, int num2) throws TException {
        // create the object
        DB db = new DB();
        CalculatorResult v = new CalculatorResult();


        TSerializer serializer = new TSerializer();
        byte[] buff = serializer.serialize(v);
        // open the database
        if (!db.open("casket.kch", DB.OWRITER | DB.OCREATE)){
            System.err.println("open error: " + db.error());
        }

        return null;
    }

    @Override
    public CalculatorResult calculate(int logid, Work w) throws TException {
        return null;
    }

    @Override
    public CalculatorResult zip() throws TException {
        return null;
    }

    @Override
    public CalculatorResult ping() throws TException {
        return null;
    }
}
