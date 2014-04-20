package bitzguild.mkt.event.compress;

import bitzguild.mkt.event.QuoteChain;
import bitzguild.mkt.event.QuoteChainUtil;
import bitzguild.ts.event.TimeSpec;

import java.util.ArrayList;
import java.util.List;

public class CompressUtil {

    /**
     * Answer a list of objects in chain
     *
     * @param head start of chain
     * @return list
     */
    public static ArrayList<QuoteCompression> list(QuoteChain head) {
        ArrayList<QuoteCompression> compressions = new ArrayList<QuoteCompression>();
        ArrayList<QuoteChain> chain = QuoteChainUtil.list(head);
        for(QuoteChain qc : chain) {
            if (qc instanceof QuoteCompression && ((QuoteCompression) qc).getCompression() != null) compressions.add((QuoteCompression)qc);
        }
        return compressions;
    }

    public static boolean consistent(List<QuoteCompression> chain) {
        if (chain.isEmpty()) return true;

        QuoteCompression head = chain.get(0);
        TimeSpec priorSpec = head.getCompression();
        for(QuoteCompression c : chain) {
            TimeSpec thisSepc = c.getCompression();
            if (!checkOnePair(priorSpec,thisSepc)) return false;
            priorSpec = thisSepc;
        }
        return true;
    }

    protected static boolean checkOnePair(TimeSpec a, TimeSpec b) {
        //System.out.println("Frame Validation: " + a + " frames " + b + " ==> " + a.frames(b));
        return a.frames(b);
    }

}
