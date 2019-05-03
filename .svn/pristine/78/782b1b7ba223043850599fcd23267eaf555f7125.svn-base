package br.com.cea.batimentoVendas.conexao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import br.com.cea.batimentoVendas.util.PropertiesLoaderImpl;

public class ConexaoSYN extends Conexao {

	private static final String SQL_RECUPERA_TOTAIS_LOJA_SYNCHRO = "select substr(e.est_codigo,5,3) as loja, to_char(e.dt_emissao,'DD/MM/YYYY') as data,"
			+ "sum(e.VL_UNITARIO*e.qtd - nvl(e.vl_desc,0)) as valor "
			+ "from cor_ecf_item e, cor_equipamento eq where e.eqp_id=eq.id and e.dt_emissao = ? "
			+ "and e.ctrl_situacao='N' and e.sit_tribut!='CANC' "
			+ "and e.est_codigo not in ('C&A0606FX') group by substr(e.est_codigo,5,3), e.dt_emissao Order by substr(e.est_codigo,5,3), e.dt_emissao";

	public Connection conectar() {
		return obterConexao(PropertiesLoaderImpl.getValor("driver"), PropertiesLoaderImpl.getValor("serverSYN"),
				PropertiesLoaderImpl.getValor("userSYN"), PropertiesLoaderImpl.getValor("pwdSYN"),
				PropertiesLoaderImpl.getValor("portaSYN"), PropertiesLoaderImpl.getValor("serviceSYN"));
	}

	public ResultSet recuperaTotais(String dtIni) {
		return executar(SQL_RECUPERA_TOTAIS_LOJA_SYNCHRO, dtIni);
	}

	public ResultSet recuperaDetalhes(String dtIni, List<String> lojas) throws Exception {
		return executarDetalhe(montaQueryDetalhes(lojas), dtIni, formataLojaSyn(lojas));
	}

	public String montaQueryDetalhes(List<String> lojas) throws Exception {
		List<String> lojasSyn = formataLojaSyn(lojas);

		String query = "select substr(e.est_codigo,5,3) as loja , to_char(e.dt_emissao,'DD/MM/YYYY') as data,"
				+ "e.chv_cfe as danfe, lpad(eq.codigo,3,0) || lpad(e.num_ordem_doc,6,0) as ticket,"
				+ "sum(e.VL_UNITARIO*e.qtd - nvl(e.vl_desc,0)) as valor "
				+ "from cor_ecf_item e, cor_equipamento eq where e.eqp_id=eq.id and e.dt_emissao = ? "
				+ "and e.ctrl_situacao='N' and e.sit_tribut!='CANC' and e.est_codigo in ("
				+ lojasSyn.toString().replaceAll("\\[|\\]", "")
				+ ") group by substr(e.est_codigo,5,3), e.dt_emissao,e.chv_cfe, eq.codigo, e.num_ordem_doc Order by substr(e.est_codigo,5,3), e.dt_emissao, e.chv_cfe, eq.codigo, e.num_ordem_doc";

		return query;
	}

	public List<String> formataLojaSyn(List<String> lojas) throws Exception {

		List<String> lojasSyn = new ArrayList<>();
		for (String loja : lojas) {
			lojasSyn.add("'C&'||'A0" + loja + "FX'");
		}

		return lojasSyn;
	}
}
