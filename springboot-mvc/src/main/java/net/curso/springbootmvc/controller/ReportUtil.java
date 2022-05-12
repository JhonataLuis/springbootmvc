package net.curso.springbootmvc.controller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable{

	private static final long serialVersionUID = 1L;

	//RETORNA NOSSO PDF EM Byte PARA DOWNLOAD NO NAVEGADOR
	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext) throws Exception{
		
		//CRIA A LISTA DE DADOS PARA O RELATÓRIO COM NOSSA LISTA DE OBJETOS PARA 
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);
		
		//CARREGA O CAMINHO DO ARQUIVO JASPER COMPILADO
		String caminhoJasper = servletContext.getRealPath("relatorios") + File.separator + relatorio + ".jasper";
		
		//CARREGA O ARQUIVO JASPER PASSANDO OS DADOS
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap<>(), jrbcds);
		
		//EXPORTA PARA BYTE[] PARA FAZER O DOWNLOAD DO PDF
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}
