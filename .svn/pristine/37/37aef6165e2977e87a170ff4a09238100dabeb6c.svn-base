package br.com.cea.batimentoVendas.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Conexao {

	public Connection conexao;
	public PreparedStatement ps;
	public Statement st;
	public ResultSet rs;
	public String url;

	public Conexao() {

	}

	public Connection obterConexao(String driver, String serverName, String usuario, String senha, String portNumber,
			String servico) {
		String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + "/" + servico;

		try {
			Class.forName(driver).newInstance();
			this.conexao = DriverManager.getConnection(url, usuario, senha);
		} catch (SQLException e) {
			System.out.println(e.getMessage());

		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());

		} catch (InstantiationException e) {
			System.out.println(e.getMessage());

		} catch (IllegalAccessException e) {
			System.out.println(e.getMessage());

		}
		return conexao;
	}

	public ResultSet executar(String query, String dtIni) {

		try {
			this.ps = this.conexao.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(dtIni));
			this.rs = ps.executeQuery();
			return this.rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet executarDetalhe(String query, String dtIni, List<String> lojas) {

		try {
			this.ps = this.conexao.prepareStatement(query);
			ps.setDate(1, java.sql.Date.valueOf(dtIni));

//			final int len = lojas.size();
//			for(int i=0;i<len;i++){
//				ps.setObject(3+i,lojas.get(i).toString());
//			}
			System.out.println(ps.getFetchSize());
			this.rs = ps.executeQuery();
			//rs.beforeFirst();
			//rs.last();
			System.out.println(rs.getRow());
			//rs.beforeFirst();
			return this.rs;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void FechaConexao()

	{
		if (this.rs != null) {
			try {
				this.rs.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (this.st != null) {
			try {
				this.st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (this.conexao != null) {
			try {
				this.conexao.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

}
