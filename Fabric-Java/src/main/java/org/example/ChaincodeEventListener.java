package org.example;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.hyperledger.fabric.client.ChaincodeEvent;
import org.hyperledger.fabric.client.CloseableIterator;
import org.hyperledger.fabric.client.Network;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Administrator
 * @date2023/6/9 0009 11:16
 */
@Slf4j
@Log
public class ChaincodeEventListener {
    final Network network;

    public ChaincodeEventListener(Network network) {
        this.network = network;

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName(this.getClass() + "chaincode_event_listener");
                return thread;
            }
        });

        executor.execute((Runnable) this);
    }


    public void run() {
        CloseableIterator<ChaincodeEvent> events = network.getChaincodeEvents("Fabric-Contract-Java");
        log.info("chaincodeEvents  " + events);

        // events.hasNext() 会阻塞等待
        while (events.hasNext()) {
            ChaincodeEvent event = events.next();
            log.info("receive chaincode event {} "+event.getEventName() +" , transaction id {}"+event.getTransactionId()+" ,  block number {}"+event.getBlockNumber() +" , payload {} "+
                      StringUtils.newStringUtf8(event.getPayload()));

        }
    }

}
