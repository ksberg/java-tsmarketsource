package bitzguild.io;



public interface LineWriter<T> {
	public void write(StringBuffer strb, T value);
	public void writeHeader(StringBuffer strb);
	public boolean includeHeader();
}
