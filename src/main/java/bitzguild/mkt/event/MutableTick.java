package bitzguild.mkt.event;

public class MutableTick extends ImmutableTick {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Default Constructor
     */
    public MutableTick() {
        super();
    }

    /**
     * Copy Constructor
     *
     * @param that ImmutableQuote
     */
    public MutableTick(Tick that) {
        super(that);
    }
    
    
    /**
     * Constructor with Market Symbol
     * @param symbol
     */
    public MutableTick(String symbol) {
        super(symbol);
    }

    
    /**
     * Fully parameterized Constructor
     *
     * @param specrep long
     * @param time long
     * @param open double
     * @param high double
     * @param low double
     * @param close double
     * @param volume long
     */
    public MutableTick(long time, String symbol, char xchg, double price, long volume) {
        super(time, symbol, xchg, price, volume);
    }

    protected ImmutableTick thisOrCopy(ImmutableTick tick) {
    	return this;
    }
    
    
	
    
}
