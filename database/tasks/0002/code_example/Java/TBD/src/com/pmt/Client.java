package com.pmt;

import com.pmt.thrift.service.DataBaseService;
import com.pmt.thrift.tstruct.TDBResearch;
import com.pmt.thrift.tstruct.TDBResult;
import com.pmt.thrift.tutorial.Calculator;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by minhtam on 26/12/2019
 */
public class Client {



    public static void main(String[] args) {
        TDBResearch val = new TDBResearch();
        val.setTask("002");
        val.setLeader("Loc.Vo");
        val.setMember(Arrays.asList("Loc", "Son", "Linh", "Thai","Tam"));

        String key = "Grokking_Research";

        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();
            TProtocol protocol = new TBinaryProtocol(transport);

            DataBaseService.Client client = new DataBaseService.Client(protocol);
            //put to Db
            TSerializer serializer = new TSerializer();
            byte[] serialize = serializer.serialize(val);
            TDBResult put = client.put(key, ByteBuffer.wrap(serialize));
            System.out.println("put error code: " + put.err);

            //get from db
            TDBResearch fromDb = new TDBResearch();
            ByteBuffer byteBuffer = client.get(key);
            TDeserializer deserializer = new TDeserializer();
            deserializer.deserialize(fromDb, byteBuffer.array());
            System.out.println("val get from db: " + fromDb);

            transport.close();
        } catch (TException x) {
            x.printStackTrace();
        }
    }
}
