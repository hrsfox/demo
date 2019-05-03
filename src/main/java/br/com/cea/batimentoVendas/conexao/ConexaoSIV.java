package br.com.cea.batimentoVendas.conexao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import br.com.cea.batimentoVendas.util.PropertiesLoaderImpl;

public class ConexaoSIV extends Conexao {

	private static final String SQL_RECUPERA_TOTAIS_LOJA_SIV = "select a.cd_loj as loja,"
			+ "       to_char(a.dt_mov,'DD/MM/YYYY') as data, sum(vl_prc_liq_item) as valor"
			+ " from  admsiv.iv028t_ven_item a, ADMSIV.iv075t_ipt_clcd_ven b                "
			+ "where  a.cd_loj = b.cd_loj"
			+ "  and  a.dt_mov = b.dt_mov and  a.nu_tkt = b.nu_tkt and  a.nu_nsu_item = b.nu_nsu_item"
			+ "  and  b.cd_ipt = 'ICMS' and  a.dt_mov = ?"
			+ "  and  a.cd_loj not in (606) group  by a.cd_loj, a.dt_mov order  by a.cd_loj, a.dt_mov";

	public Connection conectar() {
		return obterConexao(PropertiesLoaderImpl.getValor("driver"), PropertiesLoaderImpl.getValor("serverSIV"),
				PropertiesLoaderImpl.getValor("userSIV"), PropertiesLoaderImpl.getValor("pwdSIV"),
				PropertiesLoaderImpl.getValor("portaSIV"), PropertiesLoaderImpl.getValor("serviceSIV"));

	}

	public ResultSet recuperaTotais(String dtIni) {
		return executar(SQL_RECUPERA_TOTAIS_LOJA_SIV, dtIni);
	}

	public ResultSet recuperaDetalhes(String dtIni, List<String> lojas) throws Exception {
		return executarDetalhe(montaQueryDetalhes(lojas), dtIni, lojas);
	}

	public String montaQueryDetalhes(List<String> lojas) {

		String query = "select a.cd_loj as loja," + "       to_char(a.dt_mov,'DD/MM/YYYY') as data, b.nu_chv_elt as danfe, "
				+ "       lpad(a.nu_tkt,9,0) as ticket, sum(vl_prc_liq_item) as valor"
				+ " from  admsiv.iv028t_ven_item a, ADMSIV.iv075t_ipt_clcd_ven b "
				+ "where  a.cd_loj = b.cd_loj "
				+ "  and  a.dt_mov = b.dt_mov and  a.nu_tkt = b.nu_tkt and  a.nu_nsu_item = b.nu_nsu_item "
				+ "  and  b.cd_ipt = 'ICMS' and  a.dt_mov = ? " + "  and  a.cd_loj in ("
				+ lojas.toString().replaceAll("\\[|\\]", "") + ") group  by a.cd_loj, a.dt_mov, b.nu_chv_elt, a.nu_tkt "
				+ "order  by a.cd_loj, a.dt_mov, b.nu_chv_elt, a.nu_tkt ";
		return query;
	}

}
