package br.com.cea.batimentoVendas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Venda {

	public Integer Loja;
	public String Data;
	public String Danfe;
	public String Ticket;
	public double Valor;

	@Override
	public boolean equals(Object object) {
		boolean equal = false;
		if (object != null && object instanceof Venda) {
			if (this.Loja.equals(((Venda) object).getLoja()) && this.Data.equals(((Venda) object).getData())
					&& this.getValor() == ((Venda) object).getValor()) {
				equal = true;
			}
		}
		return equal;
	}

	public Integer getLoja() {
		return Loja;
	}

	public void setLoja(Integer loja) {
		Loja = loja;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}

	public String getDanfe() {
		return Danfe;
	}

	public void setDanfe(String danfe) {
		Danfe = danfe;
	}

	public String getTicket() {
		return Ticket;
	}

	public void setTicket(String ticket) {
		Ticket = ticket;
	}

	public double getValor() {
		BigDecimal bd = new BigDecimal(Valor);
		bd = bd.setScale(4, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void setValor(double valor) {
		Valor = valor;
	}
	
}
