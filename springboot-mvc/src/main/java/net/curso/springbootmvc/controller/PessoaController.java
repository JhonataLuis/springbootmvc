package net.curso.springbootmvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import net.curso.springbootmvc.model.Pessoa;
import net.curso.springbootmvc.model.Profissao;
import net.curso.springbootmvc.model.Telefone;
import net.curso.springbootmvc.repository.CargoRepository;
import net.curso.springbootmvc.repository.PessoaRepository;
import net.curso.springbootmvc.repository.ProfissaoRepository;
import net.curso.springbootmvc.repository.TelefoneRepository;

@Controller
public class PessoaController {

	@Autowired
	private PessoaRepository pessoaReposoitory;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@Autowired
	private CargoRepository cargoRepository;
	
	@GetMapping("/")
	public String inicio() {
		
		return "index";
	}
	
	//DIRECIONA PARA A PAGINA DE CADASTRO
	@GetMapping("/cadastropessoas")
	public ModelAndView cadastrar(Pessoa pessoa) {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");
		//Iterable<Pessoa> pessoaIt = pessoaReposoitory.findAll();
		modelAndView.addObject("pessoas", pessoaReposoitory.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());//CARREGA O OBJETO VAZIO NA PAGINA PARA EDITAR NOS MESMOS CAMPOS DE CADASTRAR
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("cargos", cargoRepository.findAll());
		return modelAndView;
	}
	
	//M??TODO PARA ADICIONAR PAGINA????O NA PAGINA
	@GetMapping("/pessoaspaginacao")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView model,
			@RequestParam("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaReposoitory.findPessoaByNamePage(nomepesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/pessoacadastro");
		return model;
	}
	
	
	//M??TODO PARA SALVAR UMA PESSOA E EDITAR TAMB??M
	//@PostMapping("/salvarpessoa", consumes = {"multipart/form-data"})
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(Pessoa pessoa, final MultipartFile file) throws IOException {
		
		System.out.println(file.getContentType());
		System.out.println(file.getOriginalFilename());
		
		
		//CARREGA OS TELEFONES NA HORA DE SALVAR E EDITAR 
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		//VERIFICA SE O CAMPO ARQUIVO EST?? VAZIO OU N??O
		if(file.getSize() > 0) {//CADASTRANDO UM CURR??CULO
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		}
		else { 
			if(pessoa.getId() != null && pessoa.getId() > 0){//VERIFICA O SE A PESSOA EXISTE COM O ARQUIVO PARA EDITAR
				Pessoa pessoalTemp = pessoaReposoitory.findById(pessoa.getId()).get();
				pessoa.setCurriculo(pessoalTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoalTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoalTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaReposoitory.save(pessoa);//SALVA NO BANCO DE DADOS
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");//CARREGA A LISTA DE PESSOAS
		//Iterable<Pessoa> pessoasIt = pessoaReposoitory.findAll(PageRequest.of(0, 5, Sort.by("nome")));
		modelAndView.addObject("pessoas", pessoaReposoitory.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("cargos", cargoRepository.findAll());
		return modelAndView;
	}
	
	//M??TODO PARA LISTAR TODAS AS PESSOAS CADASTRDAS NO BANCO DE DADOS
	@GetMapping("/listarpessoas")
	public ModelAndView listarPessoas() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");
		//Iterable<Pessoa> pessoaIt = pessoaReposoitory.findAll();
		modelAndView.addObject("pessoas", pessoaReposoitory.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		return modelAndView;
	}
	
	//M??TODO PARA EDITAR UMA PESSOA
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaReposoitory.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("cargos", cargoRepository.findAll());
		return modelAndView;
	}
	
	//M??TODO PARA EXCLUIR UMA PESSOA DO BANCO DE DADOS
	@GetMapping("/deletarpessoa/{idpessoa}")
	public ModelAndView deletar(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaReposoitory.deleteById(idpessoa);
		//Iterable<Pessoa> pessoaIt = pessoaReposoitory.findAll();//ITERA PELA LISTA DE PESSOAS
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaReposoitory.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.addObject("cargos", cargoRepository.findAll());
		return modelAndView;
	}
	
	//M??TODO PARA PESQUISAR PESSOAS
	@PostMapping("/pesquisarpessoas")
	//@PreAuthorize("hasPermission(")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa, 
			@PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		
		pessoas = pessoaReposoitory.findPessoaByNamePage(nomepesquisa, pageable);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/pessoacadastro");
		modelAndView.addObject("pessoas", pessoaReposoitory.findPessoaByName(nomepesquisa));
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		return modelAndView;
	}
	
	@GetMapping("/pesquisarpessoas")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//System.out.println("RELAT??RIO EM PDF");
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(nomepesquisa != null && nomepesquisa.isEmpty()) {//BUSCA SOMENTE POR NOME
			pessoas = pessoaReposoitory.findPessoaByName(nomepesquisa);
		}
		else {//BUSCA TODOS
			Iterable<Pessoa> iterator = pessoaReposoitory.findAll();
			
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		//CHAMA O SERVI??O QUE FAZ A GERA????O DO RELAT??RIO
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		//TAMANHO DA RESPOSTA
		response.setContentLength(pdf.length);
		
		//DEFINIR NA RESPOSTA O TIPO DE ARQUIVO
		response.setContentType("application/octet-stream");
		
		//DEFINIR O CABE??ALHO DA RESPOSTA
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		//FINALIZA A RESPOSTA PARA O NAVEGADOR
		response.getOutputStream().write(pdf);
	}
	
	//M??TODO PARA CONSULTAR UMA PESSOA E LEVAR PARA A TELA DE TELEFONES
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaReposoitory.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		return modelAndView;
	}
	
	//M??TODO PARA SALVAR OS TELEFONES DA PESSOA
	@PostMapping("/addfonePessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaReposoitory.findById(pessoaid).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			
			if(telefone.getNumero().isEmpty()) {
			msg.add("N??mero deve ser informado!");
		
		}
		if(telefone.getTipo().isEmpty()) {
			msg.add("Tipo deve ser Informado!");
		
		}
		modelAndView.addObject("msg", msg);
		return modelAndView;
		
		}
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		return modelAndView;
	}
	
	//M??TODO PARA DELETAR UM TELEFONE DA PESSOA
	@GetMapping("/removerFone/{idtelefone}")
	public ModelAndView removerFone(@PathVariable("idtelefone") Long idtelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		telefoneRepository.deleteById(idtelefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return modelAndView;
		
	}
	
	//M??TODO PARA ACESSAR A PAGINA DE CADASTRO DE PROFISS??ES
	@GetMapping("/profissao")
	public ModelAndView cadastrarProfissao() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/profissao");
		Iterable<Profissao> profissaoIt = profissaoRepository.findAll();
		modelAndView.addObject("profissoes", profissaoIt);
		return modelAndView;
	}
	
	//M??TODO PARA SALVAR UMA PROFISS??O
	@PostMapping("/salvarprofissao")
	public ModelAndView salvarProfissao(Profissao profissao) {
		
		profissaoRepository.save(profissao);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/profissao");
		Iterable<Profissao> profissaoIt = profissaoRepository.findAll();
		modelAndView.addObject("profissoes", profissaoIt);
		return modelAndView;
		
	}
	
	//M??TODO PARA FAZER DOWNLOAD DO DO ARQUIVO
	@GetMapping("/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		
		//CONSULTAR O OBJETO PESSOA NO BANCO DE DADOS
		Pessoa pessoa = pessoaReposoitory.findById(idpessoa).get();
		if(pessoa.getCurriculo() != null) {
			
			//SETAR TAMANHO DA RESPOSTA
			response.setContentLength(pessoa.getCurriculo().length);
			
			//TIPO DE ARQUIVO PARA DOWNLOAD OU PODE SER GENERICA application/octet-stream
			response.setContentType(pessoa.getTipoFileCurriculo());
			//response.setContentType("application/octet-stream");
			
			//DEFINE O CABE??ALHO DA RESPOSTA
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			
			//FINALIZA A RESPOSTA PASSANDO O ARQUIVO
		
			response.getOutputStream().write(pessoa.getCurriculo());
			
			
		}
	}

	
}
