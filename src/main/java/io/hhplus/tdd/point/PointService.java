package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable){
        this(userPointTable, null);
    }

    @Autowired
    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getHistories(long id)
    {
        return pointHistoryTable.selectAllByUserId(id);
    }

    /* 특정 유저의 포인트를 충전하는 기능 */
    public UserPoint chargePoint(long id, long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        UserPoint current = userPointTable.selectById(id);

        long newAmount = current.point() + amount;

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, newAmount);
    }

    /* 특정 유저의 포인트를 사용하는 기능 */
    public UserPoint usePoint(long id, long amount) {
        if(amount <= 0){
            throw new IllegalArgumentException("사용 금액은 0 보다 커야 합니다..");
        }

        UserPoint current = userPointTable.selectById(id);

        if(current.point() < amount){
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        long newAmount = current.point() - amount;
        log.info("newAmount = {}", newAmount);

        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, newAmount);
    }


}
