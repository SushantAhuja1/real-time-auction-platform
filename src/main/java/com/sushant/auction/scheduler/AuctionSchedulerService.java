package com.sushant.auction.scheduler;

import com.sushant.auction.auction.Auction;
import com.sushant.auction.auction.AuctionRepository;
import com.sushant.auction.auction.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionSchedulerService {
    private final AuctionRepository  auctionRepository;

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void manageAuctionLifeCycles() {
        Instant now = Instant.now();
        List<Auction> readyToOpen = auctionRepository.findByStatusAndStartAtBefore(AuctionStatus.SCHEDULED,now);
        for(Auction auction : readyToOpen) {
            auction.setStatus(AuctionStatus.LIVE);
            log.info("⏰ AUCTION OPENED: Auction #{} is now LIVE!", auction.getId());
        }
        auctionRepository.saveAll(readyToOpen);

        List<Auction> readyToClose = auctionRepository.findByStatusAndCloseAtBefore(AuctionStatus.LIVE,now);
        for(Auction auction : readyToClose) {
            auction.setStatus(AuctionStatus.CLOSED);
            log.info("🛑 AUCTION CLOSED: Auction #{} has ended!", auction.getId());
            // TODO: In the future, we will also broadcast the winner here!
        }
        auctionRepository.saveAll(readyToClose);
    }

}
