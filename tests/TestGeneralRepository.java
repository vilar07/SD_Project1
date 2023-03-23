package tests;

import src.sharedRegions.GeneralRepository;

public class TestGeneralRepository {
    public static void main(String[] args) {
        GeneralRepository generalRepository = new GeneralRepository();
        generalRepository.printHead();
        generalRepository.printState();
        generalRepository.setMasterThiefState(1000);
        generalRepository.setOrdinaryThiefState(0, 3000, 'P', 3);
        generalRepository.setOrdinaryThiefState(1, 3000, 'P', 4);
        generalRepository.setOrdinaryThiefState(2, 3000, 'P', 3);
        generalRepository.setOrdinaryThiefState(3, 1000, 'W', 2);
        generalRepository.setOrdinaryThiefState(4, 2000, 'W', 5);
        generalRepository.setOrdinaryThiefState(5, 1000, 'W', 6);
        generalRepository.setRoomState(0, 10, 17);
        generalRepository.setRoomState(1, 12, 19);
        generalRepository.setRoomState(2, 9, 22);
        generalRepository.setRoomState(3, 21, 15);
        generalRepository.setRoomState(4, 5, 30);
        generalRepository.setAssaultPartyRoom(0, 1);
        generalRepository.setAssaultPartyMember(0, 0, 16, 0);
        generalRepository.setAssaultPartyMember(0, 1, 17, 0);
        generalRepository.setAssaultPartyMember(0, 2, 19, 1);
        generalRepository.printState();
        generalRepository.disbandAssaultParty(0);
        generalRepository.printState();
        generalRepository.printTail(0);
    }
}
