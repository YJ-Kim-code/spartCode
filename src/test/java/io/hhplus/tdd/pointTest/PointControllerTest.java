package io.hhplus.tdd.pointTest;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;



import static org.mockito.Mockito.*;

public class PointControllerTest {

    @Test
    /* 포인트 조회 mock 단위테스트 */
    public void getPointMockTest(){
        // given
        UserPointTable mockUserPointTable = mock(UserPointTable.class);
        PointService pointService = new PointService(mockUserPointTable);
        PointController pointController = new PointController(pointService);

        UserPoint userPoint = new UserPoint(1L, 0L, System.currentTimeMillis());

        when(mockUserPointTable.selectById(userPoint.id()))
                .thenReturn(new UserPoint(userPoint.id(), userPoint.point(), System.currentTimeMillis()));
        // when
        pointController.point(userPoint.id());

        // then
        verify(mockUserPointTable, times(1)).selectById(userPoint.id());
    }

    /* 포인트 조회 stub 단위테스트 */
    @Test
    public void getPointStubTest(){
        // given
        UserPointTable stubUserPointTable = mock(UserPointTable.class);
        PointService pointService = new PointService(stubUserPointTable);
        PointController pointController = new PointController(pointService);

        UserPoint userPoint = new UserPoint(1L, 0L, System.currentTimeMillis());

        when(stubUserPointTable.selectById(userPoint.id()))
                .thenReturn(new UserPoint(userPoint.id(), userPoint.point(), System.currentTimeMillis()));

        // when
        UserPoint result = pointController.point(userPoint.id());

        // then
        assertThat(result.id()).isEqualTo(1L);
    }

    /* 포인트 충전 mock 단위테스트 */
    @Test
    public void chargePointMockTest() {
        // given
        /** mock 객체 생성 및 설정 */
        UserPointTable mockUserPointTable = mock(UserPointTable.class);
        PointHistoryTable mockPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(mockUserPointTable, mockPointHistoryTable);
        PointController pointController = new PointController(pointService);

        /** 임의적으로 값을 코드에 대입.
         *  id : 1L
         *  point : 1000L
         *  충전
         * */
        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        /** mockUserPointTable.selectById() 호출하여 id : 1L 조회
         *  new UserPoint(1L, 0L, System.currentTimeMillis()) 반환
         *
         *  mockUserPointTable.insertOrUpdate(1L, 1000L))을 호출하면
         *  userPoint를 반환.
         */
        when(mockUserPointTable.selectById(userPoint.id()))
                        .thenReturn(new UserPoint(userPoint.id(), 0L, System.currentTimeMillis()));
        when(mockUserPointTable.insertOrUpdate(userPoint.id(), userPoint.point()))
                .thenReturn(userPoint);

        // when
        pointController.charge(userPoint.id(), userPoint.point());

        // then
        /**
         * 한번 호출 되는지 검증.
         */
        verify(mockUserPointTable, times(1)).selectById(userPoint.id());
        verify(mockUserPointTable, times(1)).insertOrUpdate(userPoint.id(), userPoint.point());
    }

    @Test
    /* 포인트 충전 stub 단위테스트 */
    void chargePointStubTest(){
        // given
        UserPointTable stubUserPointTable = mock(UserPointTable.class);
        PointHistoryTable stubPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(stubUserPointTable, stubPointHistoryTable);
        PointController pointController = new PointController(pointService);

        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        when(stubUserPointTable.selectById(userPoint.id()))
                .thenReturn(new UserPoint(userPoint.id(), 0L, System.currentTimeMillis()));
        when(stubUserPointTable.insertOrUpdate(userPoint.id(), userPoint.point()))
                .thenReturn(userPoint);
        // when
        UserPoint result = pointController.charge(userPoint.id(), userPoint.point());

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(userPoint.point());
    }


    /* 포인트 사용 단위테스트 */
    @Test
    public void usePointMockTest(){
        // given
        UserPointTable mockUserPointTable = mock(UserPointTable.class);
        PointHistoryTable mockPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(mockUserPointTable, mockPointHistoryTable);
        PointController pointController = new PointController(pointService);

        /**
         * 임의로 값을 대입
         */
        long id = 1L;
        long amount = 500L;

        when(mockUserPointTable.selectById(id))
                .thenReturn(new UserPoint(id, 1000L, System.currentTimeMillis()));
        when(mockUserPointTable.insertOrUpdate(id, amount))
                .thenReturn(new UserPoint(id, amount, System.currentTimeMillis()));
        // when
        pointController.use(id, amount);

        /**
         * 메서드가 한번 호출 되는지 검증.
         */
        // then
        verify(mockUserPointTable, times(1)).selectById(id);
        verify(mockUserPointTable, times(1)).insertOrUpdate(id, amount);
    }

    @Test
    public void userPointStubTest(){
        // given
        UserPointTable stubUserPointTable = mock(UserPointTable.class);
        PointHistoryTable stubPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(stubUserPointTable, stubPointHistoryTable);
        PointController pointController = new PointController(pointService);

        UserPoint userPoint = new UserPoint(1L, 1000L, System.currentTimeMillis());

        when(stubUserPointTable.selectById(userPoint.id()))
                .thenReturn(new UserPoint(userPoint.id(), 0L, System.currentTimeMillis()));
        when(stubUserPointTable.insertOrUpdate(userPoint.id(), userPoint.point()))
                .thenReturn(userPoint);

        // when
        UserPoint result = pointController.charge(userPoint.id(), userPoint.point());

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.point()).isEqualTo(userPoint.point());
    }

    /* 포인트 사용/이용 내역 history 단위테스트 */
    @Test
    public void historiesMockTest(){
        UserPointTable mockUserPointTable = mock(UserPointTable.class);
        PointHistoryTable mockPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(mockUserPointTable, mockPointHistoryTable);
        PointController pointController = new PointController(pointService);

        long userId = 1L;
        List<PointHistory> mockHistories = List.of(
                new PointHistory(1L, userId, 1000L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(1L, userId, 500L, TransactionType.USE, System.currentTimeMillis())
        );

        when(mockPointHistoryTable.selectAllByUserId(userId))
                .thenReturn(mockHistories);
        // when
        pointController.history(userId);

        // then
        verify(mockPointHistoryTable, times(1)).selectAllByUserId(userId);
    }

    /* 포인트 사용/이용내역 stub 단위테스트 */
    @Test
    public void historiesStubTest(){
        // given
        UserPointTable stubUserPointTable = mock(UserPointTable.class);
        PointHistoryTable stubPointHistoryTable = mock(PointHistoryTable.class);

        PointService pointService = new PointService(stubUserPointTable, stubPointHistoryTable);
        PointController pointController = new PointController(pointService);

        /**
        * userId = 1L 인 사용자의 포인트 사용/이용내역 기록을 임의로 만듬.
        * */
        long userId = 1L;
        List<PointHistory> mockHistories = List.of(
                new PointHistory(1L, userId, 1000L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(1L, userId, 500L, TransactionType.USE, System.currentTimeMillis())
        );

        when(stubPointHistoryTable.selectAllByUserId(userId))
                .thenReturn(mockHistories);
        // when
        List<PointHistory> histories = pointController.history(userId);

        // then
        assertThat(histories).hasSize(2).isEqualTo(mockHistories);

    }
}
