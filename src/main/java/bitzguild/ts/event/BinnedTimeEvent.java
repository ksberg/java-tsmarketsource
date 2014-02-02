package bitzguild.ts.event;

public interface BinnedTimeEvent extends TimeEvent {

    public long timespecRep();
    public TimeSpec timespec();

    public BinnedTimeEvent fold(BinnedTimeEvent e);

}
