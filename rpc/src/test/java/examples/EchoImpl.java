package examples;


import java.io.IOException;

public class EchoImpl implements Echo {

	public String who() throws IOException {
		return "EchoImpl, from RPC-Server";
	}

	public void from(String name) throws IOException {
		System.out.println("receipted, name: " + name);
	}
}
