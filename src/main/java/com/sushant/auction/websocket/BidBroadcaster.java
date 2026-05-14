package com.sushant.auction.websocket;

import com.sushant.auction.bid.dto.BidResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidBroadcaster {
    private final SimpMessagingTemplate messagingTemplate;
    public void broadcastNewBid(Long auctionId, BidResponse newBid) {
        String destination = "/topic/auctions/" + auctionId + "/bids";
        System.out.println("DJ IS BROADCASTING TO: " + destination + " AMOUNT: " + newBid.getAmount());
        messagingTemplate.convertAndSend(destination, newBid);
    }
}