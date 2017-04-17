package middleware.basic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Request implements Serializable {
	private String op;
	private List<Object> args;

	public Request() {
	}

	public Request(String op, List<Object> args) {
		this.op = op;
		this.args = args;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public List<Object> getArgs() {
		return args;
	}

	public void setArgs(ArrayList<Object> args) {
		this.args = args;
	}
}
