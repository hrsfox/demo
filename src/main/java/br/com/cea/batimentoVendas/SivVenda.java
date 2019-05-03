package br.com.cea.batimentoVendas;

public class SivVenda extends Venda {
	@Override
	public boolean equals(Object object) {
		boolean equal = false;
		if (object != null && object instanceof Venda) {
			if (pdvEcf(((Venda) object))) {
				if (this.Loja.equals(((Venda) object).getLoja()) && this.Data.equals(((Venda) object).getData())
						&& this.getTicket().equals(((Venda) object).getTicket())
						&& this.getValor() == ((Venda) object).getValor()) {
					equal = true;
				}
			} else {
				if (this.getDanfe() == null) {// Tratamento para os casos do
												// XStore
					if (this.Loja.equals(((Venda) object).getLoja()) && this.Data.equals(((Venda) object).getData())
							&& this.getValor() == ((Venda) object).getValor()) {
						equal = true;
					}
				} else {
					if (this.Loja.equals(((Venda) object).getLoja()) && this.Data.equals(((Venda) object).getData())
							&& this.getDanfe().equals(((Venda) object).getDanfe())
							&& this.getValor() == ((Venda) object).getValor()) {
						equal = true;
					}
				}
			}
		}
		return equal;
	}

	public boolean pdvEcf(Venda venda) {
		boolean equal = false;

		if (venda.getDanfe() == null) {
			equal = true;
		}
		// if (venda.getDanfe().equals("(null)")) {
		// equal = true;
		// }
		return equal;
	}
}
