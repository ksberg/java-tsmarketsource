package bitzguild.mkt.event;

import java.util.ArrayList;

public class QuoteChainUtil {

    /**
     * Answer a list of objects in chain
     *
     * @param head start of chain
     * @return list
     */
    public static ArrayList<QuoteChain> list(QuoteChain head) {
        ArrayList<QuoteChain> chain = new ArrayList<QuoteChain>();
        QuoteChain current = head;
        while(current != QuoteChain.TERMINAL && current != null) {
            chain.add(current);
            current = current.chain();
        }
        return chain;
    }

}
