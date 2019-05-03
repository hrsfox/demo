package br.com.cea.batimentoVendas;

import java.io.FileWriter;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.cea.batimentoVendas.conexao.ConexaoSIV;
import br.com.cea.batimentoVendas.conexao.ConexaoSYN;
import net.ravendb.client.documents.DocumentStore;
import net.ravendb.client.documents.operations.DeleteByQueryOperation;
import net.ravendb.client.documents.queries.IndexQuery;
import net.ravendb.client.documents.session.IDocumentSession;

public class App {
	public static void main(String[] args) throws Exception {

		try {

			// Conecta Oracle SIV
			ConexaoSIV conSIV = new ConexaoSIV();
			conSIV.conectar();

			// Conecta Oracle SYNCHRO
			ConexaoSYN conSYN = new ConexaoSYN();
			conSYN.conectar();
			
			String dini = "";
			List<LocalDate> loopingDatas= new ArrayList<LocalDate>();
			DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dataIni = LocalDate.parse(args[0],formato);
			LocalDate dataFim = LocalDate.parse(args[1],formato);	

			for (LocalDate date = dataIni; !date.isAfter(dataFim); date = date.plusDays(1))
			{
				loopingDatas.add(date);
			}

			// GRAVA ARQUIVO EXCEL
			FileWriter excelFileSiv = new FileWriter("extracaoSIV.csv");	
			FileWriter excelFileSynchro = new FileWriter("extracaoSynchro.csv");
			
			//Looping Datas a Processar
			for (LocalDate data:loopingDatas) {
				dini = data.toString();

				// Conecta Base RavenDb
				DocumentStore dStore = new DocumentStore("http://localhost:8080", "BatimentoVendas");
				dStore.initialize();
				IDocumentSession documentSession = dStore.openSession();
				
				// Limpa Dados RavenDb
				dStore.operations().send(new DeleteByQueryOperation(new IndexQuery("from SivVendasTotals")));
				dStore.operations().send(new DeleteByQueryOperation(new IndexQuery("from SynchroVendasTotals")));

				// Limpa Dados RavenDb
				dStore.operations().send(new DeleteByQueryOperation(new IndexQuery("from SivVendas")));
				dStore.operations().send(new DeleteByQueryOperation(new IndexQuery("from SynchroVendas")));
				
				ResultSet rsSiv = conSIV.recuperaTotais(dini);

				List<SivVendasTotal> vendasSivOracle = new ArrayList<SivVendasTotal>();

				while (rsSiv.next()) {
					SivVendasTotal vendaTotal = new SivVendasTotal();
					vendaTotal.setLoja(rsSiv.getInt("loja"));
					vendaTotal.setData(rsSiv.getString("data"));
					vendaTotal.setValor(rsSiv.getDouble("valor"));
					vendasSivOracle.add(vendaTotal);
				}

				ResultSet rsSyn = conSYN.recuperaTotais(dini);

				List<SynchroVendasTotal> vendasSynOracle = new ArrayList<SynchroVendasTotal>();

				while (rsSyn.next()) {
					SynchroVendasTotal vendaTotal = new SynchroVendasTotal();
					vendaTotal.setLoja(rsSyn.getInt("loja"));
					vendaTotal.setData(rsSyn.getString("data"));
					vendaTotal.setValor(rsSyn.getDouble("valor"));
					vendasSynOracle.add(vendaTotal);
				}
				
				// Insere Totais Raven
				for (SivVendasTotal venda : vendasSivOracle) {
					documentSession.store(venda);
				}
				for (SynchroVendasTotal venda : vendasSynOracle) {
					documentSession.store(venda);
				}

				documentSession.saveChanges();

				// Recupera Totais Data/Loja
				List<VendaTotal> vendasSiv = documentSession.advanced().rawQuery(VendaTotal.class,
						"from SivVendasTotals  order by Loja, Data select Loja, Data, Valor").waitForNonStaleResults().toList();
				List<VendaTotal> vendasSynchro = documentSession.advanced().rawQuery(VendaTotal.class,
						"from SynchroVendasTotals order by Loja, Data select Loja, Data, Valor").waitForNonStaleResults().toList();

				// Batimento Synchro x Siv - Totais Data/Loja
				List<VendaTotal> divergenciasLoja = new ArrayList<VendaTotal>();
				System.out.print("Divergencias \n");
				for (VendaTotal vendaSiv : vendasSiv) {
					if (!vendasSynchro.contains(vendaSiv)) {
						divergenciasLoja.add(vendaSiv);
						System.out.print("Data: " + vendaSiv.getData() + " - Loja: " + vendaSiv.getLoja() + "\n");
					}
				}

				if (divergenciasLoja.size() == 0) {
					System.out.print("Não existem Divergências entre SIV e Synchro");
					dStore.close();
					conSIV.FechaConexao();
					conSYN.FechaConexao();
					return;
				}

				// Recupera Lista Lojas
				List<String> inLoja = new ArrayList<>();
				for (VendaTotal divergenciaLoja : divergenciasLoja) {
					inLoja.add(divergenciaLoja.getLoja().toString());
				}

				// Recupera Detalhes Vendas
				ResultSet rsDetSyn = conSYN.recuperaDetalhes(dini, inLoja);

				List<SynchroVenda> vendasSynDetalheOracle = new ArrayList<SynchroVenda>();

				while (rsDetSyn.next()) {
					SynchroVenda vendaSynDetalhe = new SynchroVenda();
					vendaSynDetalhe.setLoja(rsDetSyn.getInt("loja"));
					vendaSynDetalhe.setData(rsDetSyn.getString("data"));
					vendaSynDetalhe.setValor(rsDetSyn.getDouble("valor"));
					vendaSynDetalhe.setDanfe(rsDetSyn.getString("danfe"));
					vendaSynDetalhe.setTicket(rsDetSyn.getString("ticket"));
					vendasSynDetalheOracle.add(vendaSynDetalhe);
				}

				ResultSet rsDetSiv = conSIV.recuperaDetalhes(dini, inLoja);

				List<SivVenda> vendasSivDetalheOracle = new ArrayList<SivVenda>();

				while (rsDetSiv.next()) {
					SivVenda vendasSivDetalhe = new SivVenda();
					vendasSivDetalhe.setLoja(rsDetSiv.getInt("loja"));
					vendasSivDetalhe.setData(rsDetSiv.getString("data"));
					vendasSivDetalhe.setValor(rsDetSiv.getDouble("valor"));
					vendasSivDetalhe.setDanfe(rsDetSiv.getString("danfe"));
					vendasSivDetalhe.setTicket(rsDetSiv.getString("ticket"));
					vendasSivDetalheOracle.add(vendasSivDetalhe);
				}

				// Insere Detalhes RavenDB
				for (SivVenda venda : vendasSivDetalheOracle) {
					documentSession.store(venda);
				}
				for (SynchroVenda venda : vendasSynDetalheOracle) {
					documentSession.store(venda);
				}

				documentSession.saveChanges();

				// Recupera Tickets Data/Loja/Ticket-Danfe
				List<SivVenda> detalhesSiv = documentSession.query(SivVenda.class)
						.containsAny("Loja", Arrays.asList(inLoja)).waitForNonStaleResults().toList();
				List<SynchroVenda> detalhesSynchro = documentSession.query(SynchroVenda.class)
						.containsAny("Loja", Arrays.asList(inLoja)).waitForNonStaleResults().toList();

				// Batimento Synchro x Siv - Tickets Data/Loja
				List<Venda> ticketsDivergenteSynchro = new ArrayList<Venda>();
				for (SivVenda detalheSiv : detalhesSiv) {
					if (!detalhesSynchro.contains(detalheSiv)) {
						ticketsDivergenteSynchro.add(detalheSiv);
					}
				}
				
				System.out.print("Lista Divergências Faltando Synchro \n");
				for (Venda ticketDivergenteSynchro : ticketsDivergenteSynchro) {
					System.out.print("Data: " + ticketDivergenteSynchro.getData() + " - Loja: "
							+ ticketDivergenteSynchro.getLoja() + " - DANFE/Ticket: "
							+ ticketDivergenteSynchro.getDanfe() + " / " + ticketDivergenteSynchro.getTicket()
							+ " - Valor: " + ticketDivergenteSynchro.getValor() + "\n");
					excelFileSynchro.write(ticketDivergenteSynchro.getData() + ","
							+ ticketDivergenteSynchro.getLoja() + ","
							+ ticketDivergenteSynchro.getDanfe() + "," + ticketDivergenteSynchro.getTicket()
							+ "," + ticketDivergenteSynchro.getValor() + "\n");
				}

				// Batimento Siv x Synchro - Tickets Data/Loja
				List<Venda> ticketsDivergenteSiv = new ArrayList<Venda>();
				for (SynchroVenda detalheSynchro : detalhesSynchro) {
					if (!detalhesSiv.contains(detalheSynchro)) {
						ticketsDivergenteSiv.add(detalheSynchro);
					}
				}
				System.out.print("Lista Divergências Faltando SIV \n");
				for (Venda ticketDivergenteSiv : ticketsDivergenteSiv) {
					System.out.print("Data: " + ticketDivergenteSiv.getData() + " - Loja: "
							+ ticketDivergenteSiv.getLoja() + " - DANFE/Ticket: " + ticketDivergenteSiv.getDanfe()
							+ " / " + ticketDivergenteSiv.getTicket() + " - Valor: " + ticketDivergenteSiv.getValor()
							+ "\n");
					excelFileSiv.write(ticketDivergenteSiv.getData() + ","
							+ ticketDivergenteSiv.getLoja() + "," + ticketDivergenteSiv.getDanfe()
							+ "," + ticketDivergenteSiv.getTicket() + "," + ticketDivergenteSiv.getValor()
							+ "\n");
				}
				
				// Fecha Conexão
				dStore.close();
		
			}
			// Fecha Excel
			excelFileSiv.close();
			excelFileSynchro.close();

			// Fecha Conexões Oracle
			conSIV.FechaConexao();
			conSYN.FechaConexao();

		} finally {
			System.out.print("Fim do Programa");
		}

	}
}
