package br.com.cea.batimentoVendas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VendaTotal {

	public Integer Loja;
	public String Data;
	public double Valor;

	@Override
	public boolean equals(Object object) {
		boolean equal = false;
		if (object != null && object instanceof VendaTotal) {
			if (this.Loja.equals(((VendaTotal) object).getLoja()) && this.Data.equals(((VendaTotal) object).getData())
					&& this.getValor() == ((VendaTotal) object).getValor()) {
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

	public double getValor() {
		BigDecimal bd = new BigDecimal(Valor);
		bd = bd.setScale(4, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public void setValor(double valor) {
		Valor = valor;
	}
	
}
