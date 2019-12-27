package com.pmt;

import com.pmt.handler.CalculatorHandler;
import com.pmt.handler.DBHandler;
import com.pmt.thrift.service.DataBaseService;
import com.pmt.thrift.tutorial.Calculator;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class Main {
    public static DBHandler handler;

    public static DataBaseService.Processor processor;

    public static void simple(DataBaseService.Processor processor) {
        try {
            TServerTransport serverTransport =  new TServerSocket(9090);
            ////TServer server = new TNonblockingServer();
            //TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            // Use this for a multithreaded server
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args) {
        ClassLoader.getSystemClassLoader();
        try {
            handler = new DBHandler();
            processor = new DataBaseService.Processor(handler);

            Runnable simple = new Runnable() {
                public void run() {
                    simple(processor);
                }
            };
            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

}
